package HipoWriter;

import org.jlab.jnp.hipo.data.*;
import org.jlab.jnp.hipo.io.HipoWriter;
import org.jlab.jnp.hipo.schema.*;

import BST_struct.*;
import Particles.*;
import BMT_struct.*;
import TrackFinder.*;

import java.io.IOException;
import java.util.*;
import org.jlab.geom.prim.Vector3D;

public class CentralWriter {
	HipoWriter writer;
	SchemaFactory factory;
	Mille Millepede;
	
	
	public CentralWriter() {
		writer=new HipoWriter();
		factory = new SchemaFactory();
		factory.addSchema(new Schema("{20125,BMTRec::Crosses}[1,ID,SHORT][2,sector,BYTE][3,region,BYTE][4,x,FLOAT][5,y,FLOAT][6,z,FLOAT]"
				+ "[7,err_x,FLOAT][8,err_y,FLOAT][9,err_z,FLOAT][10,ux,FLOAT][11,uy,FLOAT][12,uz,FLOAT][13,Cluster1_ID,SHORT][14,Cluster2_ID,SHORT][15,trkID,SHORT]"));
		
		factory.addSchema(new Schema("{20225,BSTRec::Crosses}[1,ID,SHORT][2,sector,BYTE][3,region,BYTE][4,x,FLOAT][5,y,FLOAT][6,z,FLOAT]"
				+ "[7,err_x,FLOAT][8,err_y,FLOAT][9,err_z,FLOAT][10,ux,FLOAT][11,uy,FLOAT][12,uz,FLOAT][13,Cluster1_ID,SHORT][14,Cluster2_ID,SHORT][15,trkID,SHORT]"));
		
		factory.addSchema(new Schema("{20221,BSTRec::Hits}[1,ID,SHORT][2,sector,BYTE][3,layer,BYTE][4,strip,INT][5,fitResidual,FLOAT][6,trkingStat,INT]"
				+ "[7,clusterID,SHORT][8,trkID,SHORT]"));
		
		factory.addSchema(new Schema("{20222,BSTRec::Clusters}[1,ID,SHORT][2,sector,BYTE][3,layer,BYTE][4,size,SHORT][5,Etot,FLOAT][6,seedE,FLOAT][7,seedStrip,INT][8,centroid,FLOAT]"
				+ "[9,centroidResidual,FLOAT][10,seedResidual,FLOAT][11,Hit1_ID,SHORT][12,Hit2_ID,SHORT][13,Hit3_ID,SHORT][14,Hit4_ID,SHORT][15,Hit5_ID,SHORT][16,trkID,SHORT]"));
		
		factory.addSchema(new Schema("{20122,BMTRec::Clusters}[1,ID,SHORT][2,sector,BYTE][3,layer,BYTE][4,size,SHORT][5,Etot,FLOAT][6,seedE,FLOAT][7,seedStrip,INT][8,centroid,FLOAT]"
				+ "[9,centroidResidual,FLOAT][10,seedResidual,FLOAT][11,Hit1_ID,SHORT][12,Hit2_ID,SHORT][13,Hit3_ID,SHORT][14,Hit4_ID,SHORT][15,Hit5_ID,SHORT][16,trkID,SHORT][17,Tmin,FLOAT][18,Tmax,FLOAT]"));
		
		factory.addSchema(new Schema("{666,CVTRec::Millepede}[1,trkID,SHORT][2,sector,BYTE][3,layer,BYTE][4,local1,FLOAT][5,local2,FLOAT][6,local3,FLOAT][7,local4,FLOAT][8,dRx,FLOAT][9,dRy,FLOAT][10,dRz,FLOAT]"+
		"[11,dTx,FLOAT][12,dTy,FLOAT][13,dTz,FLOAT][14,dLocTx,FLOAT][15,residual,FLOAT][16,sigma,FLOAT]"));
		
		factory.addSchema(new Schema("{3,MC::Particle}[1,pid,SHORT][2,px,FLOAT][3,py,FLOAT][4,pz,FLOAT][5,vx,FLOAT][6,vy,FLOAT][7,vz,FLOAT][8,vt,FLOAT]"));
		
		factory.addSchema(new Schema("{20528,CVTRec::Cosmics}[1,ID,SHORT][2,trkline_yx_slope,FLOAT][3,trkline_yx_interc,FLOAT][4,trkline_yz_slope,FLOAT][5,trkline_yz_interc,FLOAT][6,theta,FLOAT][7,phi,FLOAT]"
				+ "[8,chi2,FLOAT][9,ndf,SHORT][10,Cross1_ID,SHORT][11,Cross2_ID,SHORT][12,Cross3_ID,SHORT][13,Cross4_ID,SHORT][14,Cross5_ID,SHORT][14,Cross5_ID,SHORT][15,Cross6_ID,SHORT]"
				+ "[16,Cross7_ID,SHORT][17,Cross8_ID,SHORT][18,Cross9_ID,SHORT][19,Cross10_ID,SHORT][20,Cross11_ID,SHORT][21,Cross12_ID,SHORT][22,Cross13_ID,SHORT][23,Cross14_ID,SHORT]"
				+ "[24,Cross15_ID,SHORT][25,Cross16_ID,SHORT][26,Cross17_ID,SHORT][27,Cross18_ID,SHORT][28,NbBSTHits,SHORT][29,NbBMTHits,SHORT]"));
		
		factory.addSchema(new Schema("{20529,CVTRec::Trajectory}[1,ID,SHORT][2,LayerTrackIntersPlane,BYTE][3,SectorTrackIntersPlane,BYTE][4,XtrackIntersPlane,FLOAT][5,YtrackIntersPlane,FLOAT][6,ZtrackIntersPlane,FLOAT]"
				+ "[7,PhitrackIntersPlane,FLOAT][8,ThetatrackIntersPlane,FLOAT][9,trkToMPlnAngl,FLOAT],[10,CalcCentroidStrip,FLOAT]"));
		
		factory.addSchema(new Schema("{11,RUN::config}[1,run,INT][2,event,INT][3,unixtime,INT][4,trigger,LONG][5,timestamp,LONG][6,type,BYTE][7,mode,BYTE][8,torus,FLOAT][9,solenoid,FLOAT]"));
		
		factory.addSchema(new Schema("{20211,BST::adc}[1,sector,BYTE][2,layer,BYTE][3,component,SHORT][4,order,BYTE][5,ADC,INT][6,time,FLOAT][7,ped,SHORT][8,timestamp,LONG]"));
		factory.addSchema(new Schema("{20111,BMT::adc}[1,sector,BYTE][2,layer,BYTE][3,component,SHORT][4,order,BYTE][5,ADC,INT][6,time,FLOAT][7,ped,SHORT][8,integral,INT][9,timestamp,LONG]"));
		 writer.appendSchemaFactory(factory);
		 		 
	}
	
	public void WriteEvent(int eventnb, Barrel BMT ,Barrel_SVT BST ,ArrayList<TrackCandidate> candidates, ParticleEvent MCParticles) throws IOException {
		HipoEvent event=writer.createEvent();
		 
		 event.writeGroup(this.fillCosmicRecBank(candidates));
		 event.writeGroup(this.fillCosmicTrajBank(BMT,BST,candidates));
		 event.writeGroup(this.fillBMTCrossesBank(BMT));
		 event.writeGroup(this.fillBSTCrossesBank(BST));
		 event.writeGroup(this.fillBSTHitsBank(BST));
		 event.writeGroup(this.fillBSTClusterBank(BST));
		 event.writeGroup(this.fillBMTClusterBank(BMT));
		 if (main.constant.isMC) event.writeGroup(this.fillMCBank(MCParticles));
		 if (main.constant.millepede) this.fillDerivativesBank(BMT,BST,candidates);
		 event.writeGroup(this.fillRunConfig(eventnb));
		 event.writeGroup(this.fillBSTADCbank(BST));
		 event.writeGroup(this.fillBMTADCbank(BMT));
		 writer.writeEvent( event );
	}
	
	public void setOuputFileName(String output) throws IOException{
		writer.open(output);
		if (main.constant.millepede) Millepede=new Mille("Mille.dat");
	}
	
	public void fillDerivativesBank(Barrel BMT ,Barrel_SVT BST ,ArrayList<TrackCandidate> candidates) throws IOException {
		for (int tr=0;tr<candidates.size();tr++) {
			candidates.get(tr).ComputeMillepedeDerivative();
			for (int clus=0;clus<candidates.get(tr).size();clus++) {
				Millepede.newSet();
				double[] loc=candidates.get(tr).GetBMTCluster(clus).getLocDerivative();
				double[] glob=candidates.get(tr).GetBMTCluster(clus).getGlobDerivative();
				int[] label=new int[glob.length];
				for (int ll=0;ll<glob.length;ll++) label[ll]=candidates.get(tr).GetBMTCluster(clus).getMillepedeLabel()+ll;
				Millepede.mille(loc, glob, label, candidates.get(tr).GetBMTCluster(clus).getCentroidResidual(), candidates.get(tr).GetBMTCluster(clus).getErr());
				Millepede.end();
			}
			for (int clus=0;clus<candidates.get(tr).BSTsize();clus++) {
				Millepede.newSet();
				double[] loc=candidates.get(tr).GetBSTCluster(clus).getLocDerivative();
				double[] glob=candidates.get(tr).GetBSTCluster(clus).getGlobDerivative();
				int[] label=new int[glob.length];
				for (int ll=0;ll<glob.length;ll++)  label[ll]=candidates.get(tr).GetBSTCluster(clus).getMillepedeLabel()+ll;
				
				Vector3D inter=BST.getGeometry().getIntersectWithRay(candidates.get(tr).GetBSTCluster(clus).getLayer(), candidates.get(tr).GetBSTCluster(clus).getSector(), candidates.get(tr).getLine().getSlope(), candidates.get(tr).getLine().getPoint());
				Millepede.mille(loc, glob, label, candidates.get(tr).GetBSTCluster(clus).getCentroidResidual(), BST.getGeometry().getSingleStripResolution(candidates.get(tr).GetBSTCluster(clus).getLayer(), (int) candidates.get(tr).GetBSTCluster(clus).getCentroid(), inter.z()));
				Millepede.end();
				
			}
		}
		
	}
	
	public HipoGroup fillBSTADCbank(Barrel_SVT BST) {
		int groupsize=BST.getNbHits();
		HipoGroup bank = writer.getSchemaFactory().getSchema("BST::adc").createGroup(groupsize);
		int index=0;
		for (int lay=1; lay<7; lay++) {
			for (int sec=1; sec<19; sec++) {
				for (int j = 0; j < BST.getModule(lay,sec).getClusters().size(); j++) {
					for (int str=0;str<BST.getModule(lay,sec).getClusters().get(j+1).getListOfHits().size();str++) {
						int strip=BST.getModule(lay,sec).getClusters().get(j+1).getListOfHits().get(str);
						bank.getNode("sector").setByte(index, (byte) sec);
						bank.getNode("layer").setByte(index, (byte) lay);
						bank.getNode("component").setShort(index, (short) strip);
						bank.getNode("ADC").setInt(index, BST.getModule(lay,sec).getHits().get(strip).getADC());
						bank.getNode("time").setFloat(index, BST.getModule(lay,sec).getHits().get(strip).getTime());
						index++;
					}
				}					
			}
		}
		
		return bank;
	}
	
	public HipoGroup fillBMTADCbank(Barrel BMT) {
		int groupsize=BMT.getNbHits();
		HipoGroup bank = writer.getSchemaFactory().getSchema("BMT::adc").createGroup(groupsize);
		
		int index=0;
		for (int lay=0; lay<6; lay++) {
			for (int sec=0; sec<3; sec++) {
				for (int j = 0; j < BMT.getTile(lay,sec).getClusters().size(); j++) {
					for (int str=0;str<BMT.getTile(lay,sec).getClusters().get(j+1).getListOfHits().size();str++) {
						int strip=BMT.getTile(lay,sec).getClusters().get(j+1).getListOfHits().get(str);
						bank.getNode("sector").setByte(index, (byte) (sec+1));
						bank.getNode("layer").setByte(index, (byte) (lay+1));
						bank.getNode("component").setShort(index, (short) strip);
						bank.getNode("ADC").setInt(index, BMT.getTile(lay,sec).getHits().get(strip).getADC());
						bank.getNode("time").setFloat(index, BMT.getTile(lay,sec).getHits().get(strip).getTime());
						index++;
					}
				}					
			}
		}
		
		return bank;
	}

	public HipoGroup fillBMTCrossesBank(Barrel BMT) {
		int groupsize=0;
		for (int lay=0; lay<6; lay++) {
			for (int sec=0; sec<3; sec++) {
			groupsize+=BMT.getTile(lay,sec).getClusters().size();
			}
		}
		
		HipoGroup bank = writer.getSchemaFactory().getSchema("BMTRec::Crosses").createGroup(groupsize);
		int index=0;
		
		for (int lay=0; lay<6; lay++) {
			for (int sec=0; sec<3; sec++) {
				for (int j = 0; j < BMT.getTile(lay,sec).getClusters().size(); j++) {
					bank.getNode("ID").setShort(index, (short) index);
					bank.getNode("sector").setByte(index, (byte) (sec+1));
					bank.getNode("region").setByte(index, (byte) ((lay+1)/2));
					bank.getNode("x").setFloat(index, (float) (BMT.getTile(lay,sec).getClusters().get(j+1).getX()/10.));
					bank.getNode("y").setFloat(index, (float) (BMT.getTile(lay,sec).getClusters().get(j+1).getY()/10.));
					bank.getNode("z").setFloat(index, (float) (BMT.getTile(lay,sec).getClusters().get(j+1).getZ()/10.));
					if (!Double.isNaN(BMT.getTile(lay,sec).getClusters().get(j+1).getX())) {
						bank.getNode("err_x").setFloat(index, (float) Math.abs(BMT.getTile(lay,sec).getClusters().get(j+1).getErr()*Math.sin(BMT.getTile(lay,sec).getClusters().get(j+1).getPhi())));
						bank.getNode("err_y").setFloat(index, (float) Math.abs(BMT.getTile(lay,sec).getClusters().get(j+1).getErr()*Math.cos(BMT.getTile(lay,sec).getClusters().get(j+1).getPhi())));
					}
					else {
						bank.getNode("err_x").setFloat(index, Float.NaN);
						bank.getNode("err_y").setFloat(index, Float.NaN);
					}
					if (!Double.isNaN(BMT.getTile(lay,sec).getClusters().get(j+1).getZ())) bank.getNode("err_z").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getErr());
					else bank.getNode("err_z").setFloat(index, Float.NaN);
					index++;
				}
			}
		}
        return bank;
	}
	
	public HipoGroup fillBSTCrossesBank(Barrel_SVT BST) {
		int groupsize=0;
		for (int lay=1; lay<7; lay++) {
			for (int sec=1; sec<19; sec++) {
			groupsize+=BST.getModule(lay,sec).getClusters().size();
			}
		}
		
		HipoGroup bank = writer.getSchemaFactory().getSchema("BSTRec::Crosses").createGroup(groupsize);
		int index=0;
		
		for (int lay=1; lay<7; lay++) {
			for (int sec=1; sec<19; sec++) {
				for (int j = 0; j < BST.getModule(lay,sec).getClusters().size(); j++) {
					bank.getNode("ID").setShort(index, (short) index);
					bank.getNode("sector").setByte(index, (byte) sec);
					bank.getNode("region").setByte(index, (byte) ((lay+1)/2));
					bank.getNode("x").setFloat(index, (float) (BST.getModule(lay,sec).getClusters().get(j+1).getX()/10.));
					bank.getNode("y").setFloat(index, (float) (BST.getModule(lay,sec).getClusters().get(j+1).getY()/10.));
					bank.getNode("z").setFloat(index, (float) (BST.getModule(lay,sec).getClusters().get(j+1).getZ()/10.));
					bank.getNode("err_x").setFloat(index, (float) Math.abs(BST.getModule(lay,sec).getClusters().get(j+1).getErrPhi()*Math.sin(BST.getModule(lay,sec).getClusters().get(j+1).getPhi())));
					bank.getNode("err_y").setFloat(index, (float) Math.abs(BST.getModule(lay,sec).getClusters().get(j+1).getErrPhi()*Math.cos(BST.getModule(lay,sec).getClusters().get(j+1).getPhi())));
					bank.getNode("err_z").setFloat(index, (float) (BST.getModule(lay,sec).getClusters().get(j+1).getErrZ()/10.));
					index++;
				}
			}
		}
        return bank;
	}
	
	public HipoGroup fillBSTHitsBank(Barrel_SVT BST) {
		int groupsize=BST.getNbHits();
				
		HipoGroup bank = writer.getSchemaFactory().getSchema("BSTRec::Hits").createGroup(groupsize);
		
		int index=0;
		int index_clus=0;
				
		for (int lay=1; lay<7; lay++) {
			for (int sec=1; sec<19; sec++) {
				for (int j = 0; j < BST.getModule(lay,sec).getClusters().size(); j++) {
					for (int str=0;str<BST.getModule(lay,sec).getClusters().get(j+1).getListOfHits().size();str++) {
						int strip=BST.getModule(lay,sec).getClusters().get(j+1).getListOfHits().get(str);
						bank.getNode("clusterID").setShort(index, (short) index_clus);
						bank.getNode("trkID").setShort(index, (short) BST.getModule(lay,sec).getClusters().get(j+1).gettrkID());
						bank.getNode("sector").setByte(index, (byte) sec);
						bank.getNode("layer").setByte(index, (byte) lay);
						bank.getNode("strip").setInt(index, strip);
						bank.getNode("ID").setShort(index, (short) BST.getModule(lay,sec).getHits().get(strip).getHit_ID());
						bank.getNode("fitResidual").setFloat(index, (float) BST.getModule(lay,sec).getHits().get(strip).getResidual());
						index++;
					}
					index_clus++;
				}
			}
		}
        return bank;
	}
	
	public HipoGroup fillBSTClusterBank(Barrel_SVT BST) {
		int groupsize=0;
		for (int lay=1; lay<7; lay++) {
			for (int sec=1; sec<19; sec++) {
			groupsize+=BST.getModule(lay,sec).getClusters().size();
			}
		}
		
		HipoGroup bank = writer.getSchemaFactory().getSchema("BSTRec::Clusters").createGroup(groupsize);
		int index=0;
						
		for (int lay=1; lay<7; lay++) {
			for (int sec=1; sec<19; sec++) {
				for (int j = 0; j < BST.getModule(lay,sec).getClusters().size(); j++) {
					bank.getNode("ID").setShort(index, (short) index);
					bank.getNode("trkID").setShort(index, (short) BST.getModule(lay,sec).getClusters().get(j+1).gettrkID());
					bank.getNode("sector").setByte(index, (byte) sec);
					bank.getNode("layer").setByte(index, (byte) lay);
					bank.getNode("centroid").setFloat(index, (float) BST.getModule(lay,sec).getClusters().get(j+1).getCentroid());
					bank.getNode("size").setShort(index, (short) BST.getModule(lay,sec).getClusters().get(j+1).getListOfHits().size());
					bank.getNode("Etot").setFloat(index, (float) BST.getModule(lay,sec).getClusters().get(j+1).getEtot());
					bank.getNode("seedStrip").setInt(index, (int) BST.getModule(lay,sec).getClusters().get(j+1).getSeed());
					bank.getNode("seedE").setFloat(index, (float) BST.getModule(lay,sec).getClusters().get(j+1).getSeedE());
					bank.getNode("centroidResidual").setFloat(index, (float) BST.getModule(lay,sec).getClusters().get(j+1).getCentroidResidual());
					index++;
				}
			}
		}
        return bank;
	}
	
	public HipoGroup fillBMTClusterBank(Barrel BMT) {
		int groupsize=0;
		for (int lay=0; lay<6; lay++) {
			for (int sec=0; sec<3; sec++) {
			groupsize+=BMT.getTile(lay,sec).getClusters().size();
			}
		}
		
		HipoGroup bank = writer.getSchemaFactory().getSchema("BMTRec::Clusters").createGroup(groupsize);
		int index=0;
						
		for (int lay=0; lay<6; lay++) {
			for (int sec=0; sec<3; sec++) {
				for (int j = 0; j < BMT.getTile(lay,sec).getClusters().size(); j++) {
					bank.getNode("ID").setShort(index, (short) index);
					bank.getNode("trkID").setShort(index, (short) BMT.getTile(lay,sec).getClusters().get(j+1).gettrkID());
					bank.getNode("sector").setByte(index, (byte) (sec+1));
					bank.getNode("layer").setByte(index, (byte) (lay+1));
					bank.getNode("centroid").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getCentroid());
					bank.getNode("size").setShort(index, (short) BMT.getTile(lay,sec).getClusters().get(j+1).getSize());
					bank.getNode("Etot").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getEdep());
					bank.getNode("seedStrip").setInt(index, (int) BMT.getTile(lay,sec).getClusters().get(j+1).getSeed());
					bank.getNode("seedE").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getSeedE());
					bank.getNode("Tmin").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getT_min());
					bank.getNode("Tmax").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getT_max());
					bank.getNode("centroidResidual").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getCentroidResidual());
					index++;
				}
			}
		}
        return bank;
	}
	
	public HipoGroup fillMCBank(ParticleEvent MCParticles) {
		
		int groupsize=MCParticles.hasNumberOfParticles();
		
		
		HipoGroup bank = writer.getSchemaFactory().getSchema("MC::Particle").createGroup(groupsize);
		int index=0;
		for (int i=0; i<groupsize; i++) {
			bank.getNode("pid").setShort(index, (short) MCParticles.getParticles().get(i).getPid());
			bank.getNode("px").setFloat(index, (float) MCParticles.getParticles().get(i).getPx());
			bank.getNode("py").setFloat(index, (float) MCParticles.getParticles().get(i).getPy());
			bank.getNode("pz").setFloat(index, (float) MCParticles.getParticles().get(i).getPz());
			bank.getNode("vx").setFloat(index, (float) MCParticles.getParticles().get(i).getVx());
			bank.getNode("vy").setFloat(index, (float) MCParticles.getParticles().get(i).getVy());
			bank.getNode("vz").setFloat(index, (float) MCParticles.getParticles().get(i).getVz());
			index++;
		}
		
		return bank;
	}
	
	public HipoGroup fillCosmicRecBank(ArrayList<TrackCandidate> candidates) {
		int groupsize=candidates.size();
				
		HipoGroup bank = writer.getSchemaFactory().getSchema("CVTRec::Cosmics").createGroup(groupsize);
		
		int index=0;
		for (int i=0; i<groupsize; i++) {
			Vector3D inter=candidates.get(i).getLine().IntersectWithPlaneY();
			bank.getNode("ID").setShort(index, (short) (index+1));
			bank.getNode("trkline_yx_slope").setFloat(index, (float) (candidates.get(i).get_VectorTrack().x()/candidates.get(i).get_VectorTrack().y()));
			bank.getNode("trkline_yx_interc").setFloat(index, (float) (inter.x()/10.));
			bank.getNode("trkline_yz_slope").setFloat(index, (float) (candidates.get(i).get_VectorTrack().z()/candidates.get(i).get_VectorTrack().y()));
			bank.getNode("trkline_yz_interc").setFloat(index, (float) (inter.z()/10.));
			bank.getNode("theta").setFloat(index, (float) Math.toDegrees((candidates.get(i).getTheta())));
			bank.getNode("phi").setFloat(index, (float) Math.toDegrees((candidates.get(i).getPhi())));
			bank.getNode("chi2").setFloat(index, (float) (candidates.get(i).get_chi2()));
			bank.getNode("NbBSTHits").setShort(index, (short) (candidates.get(i).get_Nsvt()));
			bank.getNode("NbBMTHits").setShort(index, (short) (candidates.get(i).get_Nc()+candidates.get(i).get_Nz()));
			int ndf=0;
			if (main.constant.TrackerType.equals("SVT")) ndf=candidates.get(i).get_Nsvt()-4;
			if (main.constant.TrackerType.equals("MVT")) ndf=candidates.get(i).get_Nc()+candidates.get(i).get_Nz()-4;
			if (main.constant.TrackerType.equals("CVT")) ndf=candidates.get(i).get_Nc()+candidates.get(i).get_Nz()+candidates.get(i).get_Nsvt()-4;
			bank.getNode("ndf").setShort(index, (short) ndf);
			index++;
		}
		
		return bank;
	}
	
	public HipoGroup fillRunConfig(int eventnb) {
		int groupsize=1;
				
		HipoGroup bank = writer.getSchemaFactory().getSchema("RUN::config").createGroup(groupsize);
		bank.getNode("event").setInt(0, (int) eventnb);
		if (main.constant.isCosmic) bank.getNode("type").setByte(0, (byte) 1);
		if (!main.constant.isCosmic) bank.getNode("type").setByte(0, (byte) 0);
		
		return bank;
	}
	
	
	//This method is filling cosmic traj bank... And Update crosses if they are linked to a trajectory!!!!
	public HipoGroup fillCosmicTrajBank(Barrel BMT, Barrel_SVT BST, ArrayList<TrackCandidate> candidates) {
		int groupsize=12*candidates.size();
		if (main.constant.isCosmic) groupsize=groupsize*2+10;
		
		HipoGroup bank = writer.getSchemaFactory().getSchema("CVTRec::Trajectory").createGroup(groupsize);
		
		int index=0;
		for (int track=0; track<candidates.size();track++) {
			//Intercept with SVT modules
			for (int lay=0; lay<6;lay++) {
				for (int sector=1; sector<BST.getGeometry().getNbModule(lay+1)+1;sector++) {
					Vector3D inter=new Vector3D(BST.getGeometry().getIntersectWithRay(lay+1, sector, candidates.get(track).get_VectorTrack(), candidates.get(track).get_PointTrack()));
					if (!Double.isNaN(inter.x())&&sector==BST.getGeometry().findSectorFromAngle(lay+1, inter)
							&&(main.constant.isCosmic||(BST.getGeometry().findSectorFromAngle(lay+1, candidates.get(track).get_PointTrack())==sector))) {
						//int sector=BST.getGeometry().findSectorFromAngle(lay+1, inter);
						
						Vector3D normSVT=BST.getGeometry().findBSTPlaneNormal(sector, lay+1);
						Vector3D PhiSVT=new Vector3D();
						PhiSVT.setX(candidates.get(track).get_VectorTrack().x());PhiSVT.setY(candidates.get(track).get_VectorTrack().y());PhiSVT.setZ(0);
						Vector3D eTheta=new Vector3D();
						eTheta.setX(-Math.sin(BST.getGeometry().findBSTPlaneAngle(sector, lay+1)));	eTheta.setY(Math.cos(BST.getGeometry().findBSTPlaneAngle(sector, lay+1))); eTheta.setZ(0);		
												
						Vector3D ThetaSVT=new Vector3D();
						ThetaSVT.setX(candidates.get(track).get_VectorTrack().x());ThetaSVT.setY(candidates.get(track).get_VectorTrack().y());ThetaSVT.setZ(candidates.get(track).get_VectorTrack().z());
						Vector3D ProjThetaSVT=new Vector3D();
						ProjThetaSVT.setX(candidates.get(track).get_VectorTrack().x());ProjThetaSVT.setY(candidates.get(track).get_VectorTrack().y());ProjThetaSVT.setZ(candidates.get(track).get_VectorTrack().z());
						ThetaSVT.sub(ProjThetaSVT.projection(eTheta));
						
						bank.getNode("ID").setShort(index, (short) (track+1));
						bank.getNode("LayerTrackIntersPlane").setByte(index, (byte) (lay+1));
						bank.getNode("SectorTrackIntersPlane").setByte(index, (byte) (sector));
						bank.getNode("XtrackIntersPlane").setFloat(index, (float) (inter.x()/10.));
						bank.getNode("YtrackIntersPlane").setFloat(index, (float) (inter.y()/10.));
						bank.getNode("ZtrackIntersPlane").setFloat(index, (float) (inter.z()/10.));
						bank.getNode("PhitrackIntersPlane").setFloat(index, (float) Math.toDegrees(PhiSVT.angle(normSVT)));
						bank.getNode("ThetatrackIntersPlane").setFloat(index, (float) Math.toDegrees(ThetaSVT.angle(normSVT)));
						bank.getNode("trkToMPlnAngl").setFloat(index, (float) Math.toDegrees(candidates.get(track).get_VectorTrack().angle(normSVT)));
						bank.getNode("CalcCentroidStrip").setFloat(index, (float) BST.getGeometry().calcNearestStrip(inter.x(), inter.y(), inter.z(), lay+1, sector));
						index++;
					
						int clus_id=-1;
						for (int clus_track=0;clus_track<candidates.get(track).BSTsize();clus_track++) {
							if (candidates.get(track).GetBSTCluster(clus_track).getLayer()==(lay+1)&&candidates.get(track).GetBSTCluster(clus_track).getSector()==sector) 
								clus_id=candidates.get(track).GetBSTCluster(clus_track).getLastEntry();
						}
					
						if (clus_id!=-1) { //&&(main.constant.TrackerType.equals("SVT")||main.constant.TrackerType.equals("CVT"))) {
							//Update the cluster X,Y,Z info with track info
							for (int clus=0;clus<BST.getModule(lay+1, sector).getClusters().size();clus++) {
								if (BST.getModule(lay+1, sector).getClusters().get(clus+1).getLastEntry()==clus_id) {
									BST.getModule(lay+1, sector).getClusters().get(clus+1).setX(inter.x());
									BST.getModule(lay+1, sector).getClusters().get(clus+1).setY(inter.y());
									BST.getModule(lay+1, sector).getClusters().get(clus+1).setZ(inter.z());
									BST.getModule(lay+1, sector).getClusters().get(clus+1).settrkID(track+1);
									BST.getModule(lay+1, sector).getClusters().get(clus+1).setCentroidResidual(BST.getGeometry().getResidual_line(lay+1,sector,BST.getModule(lay+1, sector).getClusters().get(clus+1).getCentroid(),inter));
								}
							}
						}
					}
				}
			}
			
			//Intercept with BMT tiles and add info on missing coordinates of the clusters.
			int sector=BMT.getGeometry().isinsector(candidates.get(track).get_PointTrack());
			for (int lay=0; lay<6;lay++) {
				for (int sec=1; sec<4;sec++) {
					
					Vector3D inter=new Vector3D(BMT.getGeometry().getIntercept(lay+1, sec, candidates.get(track).get_VectorTrack(), candidates.get(track).get_PointTrack()));
					
					if (!Double.isNaN(inter.x())&&(main.constant.isCosmic||sec==sector)) {
						bank.getNode("ID").setShort(index, (short) (track+1));
						bank.getNode("LayerTrackIntersPlane").setByte(index, (byte) (lay+7));
						bank.getNode("SectorTrackIntersPlane").setByte(index, (byte) sec);
						bank.getNode("XtrackIntersPlane").setFloat(index, (float) (inter.x()/10.));
						bank.getNode("YtrackIntersPlane").setFloat(index, (float) (inter.y()/10.));
						bank.getNode("ZtrackIntersPlane").setFloat(index, (float) (inter.z()/10.));
						
						if (BMT.getGeometry().getZorC(lay+1)==0) bank.getNode("CalcCentroidStrip").setFloat(index, (float) BMT.getGeometry().getCStrip(lay+1, inter.z()));
						if (BMT.getGeometry().getZorC(lay+1)==1) bank.getNode("CalcCentroidStrip").setFloat(index, (float) BMT.getGeometry().getZStrip(lay+1, Math.atan2(inter.y(), inter.x())));
						
						int clus_id=-1;
						for (int clus_track=0;clus_track<candidates.get(track).size();clus_track++) {
							if (candidates.get(track).GetBMTCluster(clus_track).getLayer()==(lay+1)&&candidates.get(track).GetBMTCluster(clus_track).getSector()==sec) {
								clus_id=candidates.get(track).GetBMTCluster(clus_track).getLastEntry();
								if (clus_id!=-1) { //&&(main.constant.TrackerType.equals("MVT")||main.constant.TrackerType.equals("CVT"))) {
									//Update the cluster X,Y,Z info with track info
									for (int clus=0;clus<BMT.getTile(lay, sec-1).getClusters().size();clus++) {
										if (BMT.getTile(lay, sec-1).getClusters().get(clus+1).getLastEntry()==clus_id) {
											if (BMT.getGeometry().getZorC(lay+1)==0) {
												BMT.getTile(lay, sec-1).getClusters().get(clus+1).setX(inter.x());
												BMT.getTile(lay, sec-1).getClusters().get(clus+1).setY(inter.y());
											}
											if (BMT.getGeometry().getZorC(lay+1)==1) BMT.getTile(lay, sec-1).getClusters().get(clus+1).setZ(inter.z());
											BMT.getTile(lay, sec-1).getClusters().get(clus+1).settrkID(track+1);
											//BMT.getTile(lay, sec-1).getClusters().get(clus+1).setCentroidResidual(BMT.getGeometry().getResidual_line(BMT.getTile(lay, sec-1).getClusters().get(clus+1),candidates.get(track).get_VectorTrack(),candidates.get(track).get_PointTrack()));
											BMT.getTile(lay, sec-1).getClusters().get(clus+1).setCentroidResidual(candidates.get(track).getResidual(clus_track));
										}
									}
								}
							}
						}
					
						
					
						inter.setZ(0);// er is the vector normal to the tile... use inter to compute the angle between track and tile normal.
						bank.getNode("trkToMPlnAngl").setFloat(index, (float) Math.toDegrees(candidates.get(track).get_VectorTrack().angle(inter)));
						Vector3D PhiBMT=new Vector3D();
						PhiBMT.setXYZ(candidates.get(track).get_VectorTrack().x(),candidates.get(track).get_VectorTrack().y() , 0);
						Vector3D eTheta=new Vector3D();
						eTheta.setXYZ(-inter.y(),inter.x(),0);
						Vector3D ThetaBMT=new Vector3D();
						ThetaBMT.setXYZ(candidates.get(track).get_VectorTrack().x(),candidates.get(track).get_VectorTrack().y() , candidates.get(track).get_VectorTrack().z());
						Vector3D ProjThetaBMT=new Vector3D();
						ProjThetaBMT=ThetaBMT.projection(eTheta);
						ThetaBMT.sub(ProjThetaBMT);
						bank.getNode("PhitrackIntersPlane").setFloat(index, (float) Math.toDegrees(PhiBMT.angle(inter)));
						bank.getNode("ThetatrackIntersPlane").setFloat(index, (float) Math.toDegrees(ThetaBMT.angle(inter)));
						index++;
					}
				}
			}
		}
				
		return bank;
	}
	
	public void close() throws IOException {
		writer.close();
		Mille.close();
	}
}
