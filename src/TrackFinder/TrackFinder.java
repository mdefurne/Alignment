package TrackFinder;

import BMT_struct.*;
import java.util.*;
import TrackFinder.TrackCandidate;
import TrackFinder.Fitter;

public class TrackFinder {
	
	HashMap<Integer, TrackCandidate> Candidates;
	ArrayList<TrackCandidate> BufferLayer; //Temporary store the duplicated track to avoid infinite loop
	float time_match;
	double phi_match;
	int cand_newsec;
	
	public TrackFinder() {
		Candidates=new HashMap();
		BufferLayer=new ArrayList();
		time_match=40;
		phi_match=Math.toRadians(15);
		cand_newsec=0;
	}
	
	public void setTimeMatch(float timing) {
		time_match=timing;
	}
	
	public void clear() {
		Candidates.clear();
	}
	
	public void BuildCandidates(Barrel BMT_det) {
		Tile tiles=new Tile();
		boolean IsAttributed=true;
		boolean noHit_yet_sector=true;
		//We are looking for Straight Track
		//We analyze each sector separately 
		for (int sec=0;sec<3;sec++) {
			cand_newsec=Candidates.size(); //Avoid to mix the sectors between them
			noHit_yet_sector=true;
			
			for (int lay=0;lay<6;lay++) {
				
				//If we have already some hit in the sector, there are track candidate to check
				if (!noHit_yet_sector) {
					for (int clus=0;clus<BMT_det.getTile(lay,sec).getClusters().size();clus++) {
						//Here we always test if we have a match by time
						IsAttributed=false;
						for (int num_cand=cand_newsec;num_cand<Candidates.size();num_cand++) {
							//If we have a match in time but have already added a cluster of the same layer to the track candidate
							if (this.IsTimeCompatible(BMT_det.getTile(lay,sec).getClusters().get(clus+1),Candidates.get(num_cand+1))&&!this.IsSpatialCompatible(BMT_det.getTile(lay,sec).getClusters().get(clus+1),Candidates.get(num_cand+1))) {
								TrackCandidate cand=new TrackCandidate();
								cand=Candidates.get(num_cand+1).Duplicate();//Duplicate without the last cluster
								cand.add(lay+1,sec+1,BMT_det.getTile(lay,sec).getClusters().get(clus+1));
								BufferLayer.add(cand);
								IsAttributed=true;
							}
							//If we have a match in time and will add a new layer
							if (this.IsCompatible(BMT_det.getTile(lay,sec).getClusters().get(clus+1),Candidates.get(num_cand+1))) {
								Candidates.get(num_cand+1).add(lay+1,sec+1,BMT_det.getTile(lay,sec).getClusters().get(clus+1));
								IsAttributed=true;
							}
						}
						if (!IsAttributed) {
							TrackCandidate cand=new TrackCandidate();
							cand.add(lay+1,sec+1,BMT_det.getTile(lay,sec).getClusters().get(clus+1));
							Candidates.put(Candidates.size()+1, cand);
						}
					}
					
					//Need to transfer duplicated track candidate from the buffer to the Candidates map and then empty buffer list
					for (int buf=0;buf<BufferLayer.size();buf++) {
						Candidates.put(Candidates.size()+1, BufferLayer.get(buf));
					}
					BufferLayer.clear();
				}	
				
				//We just enter the sector
				if (noHit_yet_sector) {
					//Create a new Track Candidate for each cluster of first layer
					for (int clus=0;clus<BMT_det.getTile(lay,sec).getClusters().size();clus++) {
						TrackCandidate cand=new TrackCandidate();
						cand.add(lay+1,sec+1,BMT_det.getTile(lay,sec).getClusters().get(clus+1));
						Candidates.put(Candidates.size()+1, cand);
						noHit_yet_sector=false;
					}
				}
			}	
		}
		
	}
	
	public boolean IsCompatible(Cluster clus, TrackCandidate ToBuild) {
		boolean test_val=false;
		if (this.IsTimeCompatible(clus, ToBuild)) {
			if (this.IsSpatialCompatible(clus, ToBuild)) {
				test_val=true;
			}
		}
		if (Double.isNaN(clus.getZ())) {
			if (Math.abs(clus.getPhi()-ToBuild.GetLastPhi())<(ToBuild.GetLayerLast_Z_Hit()-clus.getLayer())*phi_match) test_val=false;//Test that the cluster for the z-layer is not too far from the previous one
		}
		return test_val;
	}
	
	public boolean IsTimeCompatible(Cluster clus, TrackCandidate ToBuild) {
		//Test if not on the same layer... otherwise need to duplicate track candidate
		boolean test_val=false;
		if (Math.abs(clus.getT_min()-ToBuild.GetTimeLastHit())<time_match) test_val=true;
		return test_val;
	}
	
	public boolean IsSpatialCompatible(Cluster clus, TrackCandidate ToBuild) {
		//Test if not on the same layer... otherwise need to duplicate track candidate
		boolean test_val=false;
		if (clus.getLayer()!=ToBuild.GetLayerLastHit()) test_val=true;
		return test_val;
	}
	
	public void FetchTrack() {
		Fitter myfit=new Fitter();
		myfit.StraightTrack(Candidates);
		System.out.println("We have in total "+Candidates.size()+" track candidates");
		}
	
}	
	