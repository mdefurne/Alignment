package TrackFinder;

import BMT_struct.*;
import BST_struct.*;
import java.util.*;
import TrackFinder.TrackCandidate;
import TrackFinder.Fitter;
import org.jlab.geom.prim.Vector3D;

public class TrackFinder {
	
	HashMap<Integer, TrackCandidate> Candidates;
	ArrayList<TrackCandidate> BufferLayer; //Temporary store the duplicated track to avoid infinite loop
	float time_match;
	int cand_newsec;
	Barrel BMT_det;
	Barrel_SVT BST_det;
	
	
	public TrackFinder(Barrel BMT, Barrel_SVT BST) {
		Candidates=new HashMap<Integer, TrackCandidate>();
		BufferLayer=new ArrayList<TrackCandidate>();
		time_match=40;
		cand_newsec=0;
		BMT_det=BMT;
		BST_det=BST;
	}
	
	public HashMap<Integer, TrackCandidate> get_Candidates(){
		return Candidates;
	}
	
	public void setTimeMatch(float timing) {
		time_match=timing;
	}
	
	public void clear() {
		Candidates.clear();
	}
	
	public void BuildCandidates() {
		boolean IsAttributed=true;
		boolean noHit_yet_sector=true;
		
			//We are looking for Straight Track
			//We analyze each sector separately in beam configuration
			for (int sec=0;sec<3;sec++) {
				cand_newsec=Candidates.size(); //Avoid to mix the sectors between them
				noHit_yet_sector=true;
			
				for (int lay=5;lay>-1;lay--) {
					//if (sec==1) {
						//for (int cc=0;cc<Candidates.size();cc++) Candidates.get(cc+1).Print();
					//}
					if (BMT_det.getTile(lay,sec).getClusters().size()<10) {
					//If we have already some hit in the sector, there are track candidate to check
					
						if (!noHit_yet_sector) {
							for (int clus=0;clus<BMT_det.getTile(lay,sec).getClusters().size();clus++) {
							
								//Here we always test if we have a match by time
								IsAttributed=false;
								for (int num_cand=cand_newsec;num_cand<Candidates.size();num_cand++) {
									//If we have a match in time and will add a new layer
									if (!this.IsLayerCompatible(BMT_det.getTile(lay,sec).getClusters().get(clus+1),Candidates.get(num_cand+1))) {
										TrackCandidate cand=Candidates.get(num_cand+1).DuplicateBMT();
										if (this.IsCompatible(BMT_det.getTile(lay,sec).getClusters().get(clus+1),cand)) {
											cand.addBMT(BMT_det.getTile(lay,sec).getClusters().get(clus+1));
											BufferLayer.add(cand);
											IsAttributed=true;
										}
									}
								
									if (this.IsCompatible(BMT_det.getTile(lay,sec).getClusters().get(clus+1),Candidates.get(num_cand+1))) {
										Candidates.get(num_cand+1).addBMT(BMT_det.getTile(lay,sec).getClusters().get(clus+1));
										IsAttributed=true;
									}
								
								}
							
								if (!IsAttributed) {
									TrackCandidate cand=new TrackCandidate(BMT_det,BST_det);
									cand.addBMT(BMT_det.getTile(lay,sec).getClusters().get(clus+1));
									Candidates.put(Candidates.size()+1, cand);
								}
							
							}
							for (int buf=0;buf<BufferLayer.size();buf++) {
								Candidates.put(Candidates.size()+1, BufferLayer.get(buf));
							}
							BufferLayer.clear();
						}	
				
						//We just enter the sector
						if (noHit_yet_sector) {
							//Create a new Track Candidate for each cluster of first layer
							for (int clus=0;clus<BMT_det.getTile(lay,sec).getClusters().size();clus++) {
								TrackCandidate cand=new TrackCandidate(BMT_det,BST_det);
								cand.addBMT(BMT_det.getTile(lay,sec).getClusters().get(clus+1));
								Candidates.put(Candidates.size()+1, cand);
								noHit_yet_sector=false;
							}
						
						}
					}
				}
			}
		
			//If we want to include SVT, we will try to find the strips compatible with the track candidates built with BMT
			if (main.constant.TrackerType.equals("SVT")||main.constant.TrackerType.equals("CVT")) {
				for (int lay=6; lay>0;lay--) {
					for (int ray=0; ray<Candidates.size();ray++) {
							int sector=BST_det.getGeometry().getSectIntersect(lay, Candidates.get(ray+1).get_VectorTrack(), Candidates.get(ray+1).get_PointTrack());
							int delta_sec=1; //Point_track may be a bit shifted compared to the module we are supposed to look at!!
							if (main.constant.isCosmic) delta_sec=2;
							if (sector!=-1) {
								for (int sector_stud=sector-delta_sec; sector_stud<sector+delta_sec; sector_stud++) {
									int sec=sector_stud%BST_det.getGeometry().getNbModule(lay)+1;
									Vector3D inter=BST_det.getGeometry().getIntersectWithRay(lay, sec, Candidates.get(ray+1).get_VectorTrack(), Candidates.get(ray+1).get_PointTrack());
									if (!Double.isNaN(inter.x())) {
										for (int str=0;str<BST_det.getModule(lay, sec).getClusters().size();str++) {
											double delta=BST_det.getGeometry().getResidual_line(lay, sec, BST_det.getModule(lay, sec).getClusters().get(str+1).getCentroid() , inter);
									
												if ((Math.abs(delta)<5&&!main.constant.isCosmic)||(Math.abs(delta)<25&&main.constant.isCosmic)) {
													if (Candidates.get(ray+1).BSTsize()!=0) {
														if (Candidates.get(ray+1).getLastBSTLayer()==lay) {
															TrackCandidate cand=Candidates.get(ray+1).DuplicateBST();
															cand.addBST(BST_det.getModule(lay, sec).getClusters().get(str+1));
															BufferLayer.add(cand);
														}
														if (Candidates.get(ray+1).getLastBSTLayer()!=lay) Candidates.get(ray+1).addBST(BST_det.getModule(lay, sec).getClusters().get(str+1));
													}
													if (Candidates.get(ray+1).BSTsize()==0) Candidates.get(ray+1).addBST(BST_det.getModule(lay, sec).getClusters().get(str+1));
												}
										}
									}	
								}
							}
					}
					for (int buf=0;buf<BufferLayer.size();buf++) {
						Candidates.put(Candidates.size()+1, BufferLayer.get(buf));
					}
					BufferLayer.clear();
				}
				
				//for (int i=0;i<Candidates.size();i++) {
					//if (Candidates.get(i+1).IsFittable()) Candidates.get(i+1).Print();
				//}
			}
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//For cosmic data, no need to overthink the pattern recognition
		
		if (main.constant.isCosmic) {
			
			//We look at track going between sec1 and sec3 of BMT
			int svt_opposite=0;
			ArrayList<Integer> index_fittable_sec2=new ArrayList<Integer>();
			ArrayList<Integer> index_fittable_sec1=new ArrayList<Integer>();
			ArrayList<Integer> index_fittable_sec3=new ArrayList<Integer>();
			for (int track=1;track<Candidates.size()+1;track++) {
				if (Candidates.get(track).IsFittable()&&Candidates.get(track).GetBMTCluster(0).getSector()==2)	index_fittable_sec2.add(track);
				if (Candidates.get(track).IsFittable()&&Candidates.get(track).GetBMTCluster(0).getSector()==1)	index_fittable_sec1.add(track);
				if (Candidates.get(track).IsFittable()&&Candidates.get(track).GetBMTCluster(0).getSector()==3)	index_fittable_sec3.add(track);
			}
			int sector_hit=0;
			if (index_fittable_sec1.size()>0) sector_hit++;
			if (index_fittable_sec2.size()>0) sector_hit++;
			if (index_fittable_sec3.size()>0) sector_hit++;
			
			//We loop over the track candidates
			
			if (sector_hit==1&&index_fittable_sec2.size()>0) {
				for (int lay=1; lay<7;lay++) {
					if (BST_det.getModule(lay, 1).getClusters().size()==1) svt_opposite++;
				}
				if (svt_opposite>3) {
					for (int cand=0;cand<index_fittable_sec2.size();cand++) {
					 for (int lay=1; lay<7;lay++) {
						 if (BST_det.getModule(lay, 1).getClusters().size()==1) Candidates.get(index_fittable_sec2.get(cand)).addBST(BST_det.getModule(lay, 1).getClusters().get(1));
					 }
					}
				}
			}
			
			if (sector_hit==1&&index_fittable_sec1.size()>0) {
				for (int lay=1; lay<7;lay++) {
					if (lay==1||lay==2) svt_opposite+=BST_det.getModule(lay,7).getClusters().size()+BST_det.getModule(lay,8).getClusters().size();
					if (lay==3||lay==4) svt_opposite+=BST_det.getModule(lay,10).getClusters().size()+BST_det.getModule(lay,11).getClusters().size();
					if (lay==5||lay==6) svt_opposite+=BST_det.getModule(lay,13).getClusters().size();
				}
				
				if (svt_opposite>3) {
				  for (int cand=0;cand<index_fittable_sec1.size();cand++) {
				  if (BST_det.getModule(1, 7).getClusters().size()==1&&BST_det.getModule(1, 8).getClusters().size()==0) Candidates.get(index_fittable_sec1.get(cand)).addBST(BST_det.getModule(1, 7).getClusters().get(1));
				  if (BST_det.getModule(1, 7).getClusters().size()==0&&BST_det.getModule(1, 8).getClusters().size()==1) Candidates.get(index_fittable_sec1.get(cand)).addBST(BST_det.getModule(1, 8).getClusters().get(1));
				  
				  if (BST_det.getModule(2, 7).getClusters().size()==1&&BST_det.getModule(2, 8).getClusters().size()==0) Candidates.get(index_fittable_sec1.get(cand)).addBST(BST_det.getModule(2, 7).getClusters().get(1));
				  if (BST_det.getModule(2, 7).getClusters().size()==0&&BST_det.getModule(2, 8).getClusters().size()==1) Candidates.get(index_fittable_sec1.get(cand)).addBST(BST_det.getModule(2, 8).getClusters().get(1));
				  
				  if (BST_det.getModule(3, 10).getClusters().size()==1&&BST_det.getModule(3, 11).getClusters().size()==0) Candidates.get(index_fittable_sec1.get(cand)).addBST(BST_det.getModule(3, 10).getClusters().get(1));
				  if (BST_det.getModule(3, 10).getClusters().size()==0&&BST_det.getModule(3, 11).getClusters().size()==1) Candidates.get(index_fittable_sec1.get(cand)).addBST(BST_det.getModule(3, 11).getClusters().get(1));
				  
				  if (BST_det.getModule(4, 10).getClusters().size()==1&&BST_det.getModule(4, 11).getClusters().size()==0) Candidates.get(index_fittable_sec1.get(cand)).addBST(BST_det.getModule(4, 10).getClusters().get(1));
				  if (BST_det.getModule(4, 10).getClusters().size()==0&&BST_det.getModule(4, 11).getClusters().size()==1) Candidates.get(index_fittable_sec1.get(cand)).addBST(BST_det.getModule(4, 11).getClusters().get(1));
				  
				  if (BST_det.getModule(5, 13).getClusters().size()==1) Candidates.get(index_fittable_sec1.get(cand)).addBST(BST_det.getModule(5, 13).getClusters().get(1));
				  if (BST_det.getModule(6, 13).getClusters().size()==1) Candidates.get(index_fittable_sec1.get(cand)).addBST(BST_det.getModule(6, 13).getClusters().get(1));
				  }
				}
			}
			
			if (sector_hit==1&&index_fittable_sec3.size()>0) {
				for (int lay=1; lay<7;lay++) {
					if (lay==1||lay==2) svt_opposite+=BST_det.getModule(lay,4).getClusters().size()+BST_det.getModule(lay,5).getClusters().size();
					if (lay==3||lay==4) svt_opposite+=BST_det.getModule(lay,5).getClusters().size()+BST_det.getModule(lay,6).getClusters().size();
					if (lay==5||lay==6) svt_opposite+=BST_det.getModule(lay,7).getClusters().size();
				}
				
				if (svt_opposite>3) {
				  for (int cand=0;cand<index_fittable_sec3.size();cand++) {
				  if (BST_det.getModule(1, 4).getClusters().size()==1&&BST_det.getModule(1, 5).getClusters().size()==0) Candidates.get(index_fittable_sec3.get(cand)).addBST(BST_det.getModule(1, 4).getClusters().get(1));
				  if (BST_det.getModule(1, 4).getClusters().size()==0&&BST_det.getModule(1, 5).getClusters().size()==1) Candidates.get(index_fittable_sec3.get(cand)).addBST(BST_det.getModule(1, 5).getClusters().get(1));
				  
				  if (BST_det.getModule(2, 4).getClusters().size()==1&&BST_det.getModule(2, 5).getClusters().size()==0) Candidates.get(index_fittable_sec3.get(cand)).addBST(BST_det.getModule(2, 4).getClusters().get(1));
				  if (BST_det.getModule(2, 4).getClusters().size()==0&&BST_det.getModule(2, 5).getClusters().size()==1) Candidates.get(index_fittable_sec3.get(cand)).addBST(BST_det.getModule(2, 5).getClusters().get(1));
				  
				  if (BST_det.getModule(3, 5).getClusters().size()==1&&BST_det.getModule(3, 6).getClusters().size()==0) Candidates.get(index_fittable_sec3.get(cand)).addBST(BST_det.getModule(3, 5).getClusters().get(1));
				  if (BST_det.getModule(3, 5).getClusters().size()==0&&BST_det.getModule(3, 6).getClusters().size()==1) Candidates.get(index_fittable_sec3.get(cand)).addBST(BST_det.getModule(3, 6).getClusters().get(1));
				  
				  if (BST_det.getModule(4, 5).getClusters().size()==1&&BST_det.getModule(4, 6).getClusters().size()==0) Candidates.get(index_fittable_sec3.get(cand)).addBST(BST_det.getModule(4, 5).getClusters().get(1));
				  if (BST_det.getModule(4, 5).getClusters().size()==0&&BST_det.getModule(4, 6).getClusters().size()==1) Candidates.get(index_fittable_sec3.get(cand)).addBST(BST_det.getModule(4, 6).getClusters().get(1));
				  
				  if (BST_det.getModule(5, 7).getClusters().size()==1) Candidates.get(index_fittable_sec3.get(cand)).addBST(BST_det.getModule(5, 7).getClusters().get(1));
				  if (BST_det.getModule(6, 7).getClusters().size()==1) Candidates.get(index_fittable_sec3.get(cand)).addBST(BST_det.getModule(6, 7).getClusters().get(1));
				  }
				}
			}
			
			//We loop over the track candidates
			if (sector_hit>1) {
				//Merge sec2 track to sec1 and then sec3
				for (int track_sec2=0;track_sec2<index_fittable_sec2.size();track_sec2++) {
					for (int track_sec1=0;track_sec1<index_fittable_sec1.size();track_sec1++) {
						Candidates.put(Candidates.size()+1,Candidates.get(index_fittable_sec2.get(track_sec2)).Merge(Candidates.get(index_fittable_sec1.get(track_sec1))));
						Candidates.get(Candidates.size()).set_VectorTrack(Candidates.get(index_fittable_sec2.get(track_sec2)).get_VectorTrack());
						Candidates.get(Candidates.size()).set_PointTrack(Candidates.get(index_fittable_sec2.get(track_sec2)).get_PointTrack());
						Candidates.get(Candidates.size()).set_PhiSeed(Candidates.get(index_fittable_sec2.get(track_sec2)).getPhiSeed());
						Candidates.get(Candidates.size()).set_ThetaSeed(Candidates.get(index_fittable_sec2.get(track_sec2)).getThetaSeed());
						
					}
					for (int track_sec3=0;track_sec3<index_fittable_sec3.size();track_sec3++) {
						Candidates.put(Candidates.size()+1,Candidates.get(index_fittable_sec2.get(track_sec2)).Merge(Candidates.get(index_fittable_sec3.get(track_sec3))));
						Candidates.get(Candidates.size()).set_VectorTrack(Candidates.get(index_fittable_sec2.get(track_sec2)).get_VectorTrack());
						Candidates.get(Candidates.size()).set_PointTrack(Candidates.get(index_fittable_sec2.get(track_sec2)).get_PointTrack());
						Candidates.get(Candidates.size()).set_PhiSeed(Candidates.get(index_fittable_sec2.get(track_sec2)).getPhiSeed());
						Candidates.get(Candidates.size()).set_ThetaSeed(Candidates.get(index_fittable_sec2.get(track_sec2)).getThetaSeed());
						
					}
				}
				//Merge sec1 and sec3 Track
				for (int track_sec3=0;track_sec3<index_fittable_sec3.size();track_sec3++) {
					for (int track_sec1=0;track_sec1<index_fittable_sec1.size();track_sec1++) {
						Candidates.put(Candidates.size()+1,Candidates.get(index_fittable_sec3.get(track_sec3)).Merge(Candidates.get(index_fittable_sec1.get(track_sec1))));
						Candidates.get(Candidates.size()).set_VectorTrack(Candidates.get(index_fittable_sec3.get(track_sec3)).get_VectorTrack());
						Candidates.get(Candidates.size()).set_PointTrack(Candidates.get(index_fittable_sec3.get(track_sec3)).get_PointTrack());
						Candidates.get(Candidates.size()).set_PhiSeed(Candidates.get(index_fittable_sec3.get(track_sec3)).getPhiSeed());
						Candidates.get(Candidates.size()).set_ThetaSeed(Candidates.get(index_fittable_sec3.get(track_sec3)).getThetaSeed());
					}
				}
				
			}
		}
	}
	
	public boolean IsCompatible(BMT_struct.Cluster clus, TrackCandidate ToBuild) {
		boolean test_val=false;
		if (this.IsTimeCompatible(clus, ToBuild)) {
			if (this.IsLayerCompatible(clus, ToBuild)) {
				if (this.IsSpatialCompatible(clus, ToBuild)) {
					test_val=true;
				}
			}
		}
		return test_val;
	}
	
	public boolean AllExceptLayerCompatible(BMT_struct.Cluster clus, TrackCandidate ToBuild) {
		boolean test_val=false;
		if (this.IsTimeCompatible(clus, ToBuild)) {
		 if (this.IsSpatialCompatible(clus, ToBuild)) {
			if (!this.IsLayerCompatible(clus, ToBuild)) {
					test_val=true;
				}
			}
		}
		return test_val;
	}
	
	public boolean IsTimeCompatible(BMT_struct.Cluster clus, TrackCandidate ToBuild) {
		//Test if not on the same layer... otherwise need to duplicate track candidate
		boolean test_val=false;
		//if (clus.getSector()==2) System.out.println(clus.getT_min()+" "+ToBuild.GetTimeLastHit());
		if (Math.abs(clus.getT_min()-ToBuild.GetTimeLastHit())<time_match) test_val=true;
		return test_val;
	}
	
	public boolean IsLayerCompatible(BMT_struct.Cluster clus, TrackCandidate ToBuild) {
		//Test if not on the same layer... otherwise need to duplicate track candidate
		boolean test_val=false;
		if (clus.getLayer()!=ToBuild.GetLayerLastHit()) test_val=true;
		return test_val;
	}
	
	public boolean IsSpatialCompatible(BMT_struct.Cluster clus, TrackCandidate ToBuild) {
		//Test if not on the same layer... otherwise need to duplicate track candidate
		boolean test_val=false;
		
		if (Double.isNaN(clus.getZ())) {
			if (ToBuild.get_Nz()==0) {
				test_val=true;
			}
			if (ToBuild.get_Nz()>0) {
				Vector3D meas=new Vector3D(ToBuild.getLastX()-clus.getX(),ToBuild.getLastY()-clus.getY(),0);
				Vector3D tr_phi=new Vector3D(Math.cos(ToBuild.getPhiSeed()),Math.sin(ToBuild.getPhiSeed()),0);
				double angle_meas=meas.angle(tr_phi);
				if (angle_meas<ToBuild.getPhiTolerance()
						||Math.abs(angle_meas-Math.PI)<ToBuild.getPhiTolerance()
							||Math.abs(angle_meas-2*Math.PI)<ToBuild.getPhiTolerance()) test_val=true;
			}
		}
		if (!Double.isNaN(clus.getZ())) {
			if (ToBuild.get_Nc()==0) {	
				test_val=true;
				
			}
			if (ToBuild.get_Nc()>0) {
				double Theta_meas=Math.acos((ToBuild.getLastZ()-clus.getZ())/Math.sqrt((ToBuild.getLastZ()-clus.getZ())*(ToBuild.getLastZ()-clus.getZ())
						+(clus.getRadius()-ToBuild.getLastR())*(clus.getRadius()-ToBuild.getLastR())));
				if (Theta_meas>ToBuild.getThetaMin()&&Theta_meas<ToBuild.getThetaMax()) test_val=true;
			}
		}
		return test_val;
	}
	
	public void FetchTrack() {
		Fitter myfit=new Fitter();
		myfit.StraightTrack(BMT_det, BST_det, Candidates);
		}
	
}	
	