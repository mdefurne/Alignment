package HipoWriter;

import org.jlab.jnp.hipo.data.*;
import org.jlab.jnp.hipo.io.HipoWriter;
import org.jlab.io.base.*;
import org.jlab.jnp.hipo.schema.*;
import BST_struct.*;
import Particles.*;
import BMT_struct.*;
import TrackFinder.*;
import java.util.*;

public class CentralWriter {
	HipoWriter writer;
	SchemaFactory factory;
	
	public CentralWriter() {
		writer=new HipoWriter();
		factory = new SchemaFactory();
		factory.addSchema(new Schema("{1,BMTRec::Crosses}[1,ID,SHORT][2,sector,BYTE][3,region,BYTE][4,x,FLOAT][5,y,FLOAT][6,z,FLOAT]"
				+ "[7,err_x,FLOAT][8,err_y,FLOAT][9,err_z,FLOAT][10,ux,FLOAT][11,uy,FLOAT][12,uz,FLOAT][13,Cluster1_ID,SHORT][14,Cluster2_ID,SHORT][15,trkID,SHORT]"));
		factory.addSchema(new Schema("{2,BSTRec::Crosses}[1,ID,SHORT][2,sector,BYTE][3,region,BYTE][4,x,FLOAT][5,y,FLOAT][6,z,FLOAT]"
				+ "[7,err_x,FLOAT][8,err_y,FLOAT][9,err_z,FLOAT][10,ux,FLOAT][11,uy,FLOAT][12,uz,FLOAT][13,Cluster1_ID,SHORT][14,Cluster2_ID,SHORT][15,trkID,SHORT]"));
		factory.addSchema(new Schema("{3,MC::Particle}[1,pid,SHORT][2,px,FLOAT][3,py,FLOAT][4,pz,FLOAT][5,vx,FLOAT][6,vy,FLOAT][7,vz,FLOAT][8,vt,FLOAT]"));
		factory.addSchema(new Schema("{4,CVTRec::Cosmics}[1,ID,SHORT][2,trkline_yx_slope,FLOAT][3,trkline_yx_interc,FLOAT][4,trkline_yz_slope,FLOAT][5,trkline_yz_interc,FLOAT][6,theta,FLOAT][7,phi,FLOAT]"
				+ "[8,chi2,FLOAT][9,ndf,SHORT][10,Cross1_ID,SHORT][11,Cross2_ID,SHORT][12,Cross3_ID,SHORT][13,Cross4_ID,SHORT][14,Cross5_ID,SHORT][14,Cross5_ID,SHORT][15,Cross6_ID,SHORT]"
				+ "[16,Cross7_ID,SHORT][17,Cross8_ID,SHORT][18,Cross9_ID,SHORT][19,Cross10_ID,SHORT][20,Cross11_ID,SHORT][21,Cross12_ID,SHORT][22,Cross13_ID,SHORT][23,Cross14_ID,SHORT]"
				+ "[24,Cross15_ID,SHORT][25,Cross16_ID,SHORT][26,Cross17_ID,SHORT][27,Cross18_ID,SHORT]"));
		 writer.appendSchemaFactory(factory);
		 writer.open("/home/mdefurne/Bureau/CLAS12/customBank.hipo");
	}
	
	public void WriteEvent(Barrel BMT ,Barrel_SVT BST ,ArrayList<TrackCandidate> candidates, ParticleEvent MCParticles) {
		 HipoEvent event = writer.createEvent();
		 	 
		 event.writeGroup(this.fillBMTCrossesBank(BMT));
		 event.writeGroup(this.fillBSTCrossesBank(BST));
		 if (main.constant.isMC) event.writeGroup(this.fillMCBank(MCParticles));
		
		 writer.writeEvent( event );
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
					bank.getNode("x").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getX());
					bank.getNode("y").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getY());
					bank.getNode("z").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getZ());
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
					bank.getNode("sector").setByte(index, (byte) (sec+1));
					bank.getNode("region").setByte(index, (byte) ((lay+1)/2));
					bank.getNode("x").setFloat(index, (float) BST.getModule(lay,sec).getClusters().get(j+1).getX());
					bank.getNode("y").setFloat(index, (float) BST.getModule(lay,sec).getClusters().get(j+1).getY());
					bank.getNode("z").setFloat(index, (float) BST.getModule(lay,sec).getClusters().get(j+1).getZ());
					bank.getNode("err_x").setFloat(index, (float) Math.abs(BST.getModule(lay,sec).getClusters().get(j+1).getErrPhi()*Math.sin(BST.getModule(lay,sec).getClusters().get(j+1).getPhi())));
					bank.getNode("err_y").setFloat(index, (float) Math.abs(BST.getModule(lay,sec).getClusters().get(j+1).getErrPhi()*Math.cos(BST.getModule(lay,sec).getClusters().get(j+1).getPhi())));
					bank.getNode("err_z").setFloat(index, (float) BST.getModule(lay,sec).getClusters().get(j+1).getErrZ());
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
			
			bank.getNode("ID").setShort(index, (short) index);
			bank.getNode("trkline_yx_slope").setFloat(index, (float) (candidates.get(i).get_VectorTrack().x()/candidates.get(i).get_VectorTrack().y()));
			bank.getNode("trkline_yx_interc").setFloat(index, (float) (candidates.get(i).get_VectorTrack().x()/candidates.get(i).get_VectorTrack().y()));
			bank.getNode("trkline_yz_slope").setFloat(index, (float) (candidates.get(i).get_VectorTrack().z()/candidates.get(i).get_VectorTrack().y()));
			bank.getNode("trkline_yz_interc").setFloat(index, (float) (candidates.get(i).get_VectorTrack().x()/candidates.get(i).get_VectorTrack().y()));
		}
		
		return bank;
	}
	
	public void close() {
		writer.close();
	}
}
