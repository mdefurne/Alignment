package Alignment;

import org.freehep.math.minuit.FCNBase;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;
import Trajectory.StraightLine;
import BMT_struct.*;
import BST_struct.*;
import org.jlab.io.base.DataBank;

public class FCNChi2 implements FCNBase {

	HipoDataSource reader;
	Barrel BMT;
	Barrel_SVT BST;
	int layer;
	int sector;
	
	public double valueOf(double[] par)
	   {
		
		  double val=0;	  
		  int count=0;
		  //Look if track is supposed to 
		  boolean ThroughTile=false;
		  float ClusterExpect=-20;
		  
		  //Check is cluster is compatible with expected intercept with track
		  float DeltaCentroid=10;
		  
		  if (layer>6) {
			  //First three parameters are Rotations
			  BMT_geo.Constants.setRx(layer-6, sector, par[0]);
			  BMT_geo.Constants.setRy(layer-6, sector, par[1]);
			  BMT_geo.Constants.setRz(layer-6, sector, par[2]);
			  //Last three parameters are rotations
			  BMT_geo.Constants.setCx(layer-6, sector, par[3]);
			  BMT_geo.Constants.setCy(layer-6, sector, par[4]);
			  BMT_geo.Constants.setCz(layer-6, sector, par[5]);
		  }
		 
		 for (int i=0; i<reader.getSize();i++) {
			  DataEvent event = reader.gotoEvent(i);
				
				if(event.hasBank("CVTRec::Cosmics")&&event.hasBank("CVTRec::Trajectory")) {
			    	StraightLine ray=new StraightLine();
			    	DataBank raybank=event.getBank("CVTRec::Cosmics");
			    	DataBank Trajbank=event.getBank("CVTRec::Trajectory");
			    	DataBank BMTClusbank=event.getBank("BMTRec::Clusters");
			    	for (int nray=0;nray<raybank.rows();nray++) {
			    		ThroughTile=false;
			    		ClusterExpect=-20;
			    		if (raybank.getShort("ndf",nray)>=3) {
			    			
			    			ray.setSlope_XYZ(raybank.getFloat("trkline_yx_slope", nray), 1, raybank.getFloat("trkline_yz_slope", nray));
			    			ray.setPoint_XYZ(raybank.getFloat("trkline_yx_interc", nray)*10, 0, raybank.getFloat("trkline_yz_interc", nray)*10);
			    		
			    			//Check the calcCentroid of the track and make sure the track went through the tile we want to align
			    			for (int npt=0; npt<Trajbank.rows(); npt++) {
			    				if (raybank.getShort("ID",nray)==Trajbank.getShort("ID",npt)&&
			    						layer==Trajbank.getByte("LayerTrackIntersPlane",npt)&&
			    						sector==Trajbank.getByte("SectorTrackIntersPlane",npt)) {
			    						ThroughTile=true;
			    						ClusterExpect=Trajbank.getFloat("CalcCentroidStrip",npt);
			    						continue;
			    				}
			    			}
			    		
			    			//Since the track is supposed to have crossed the tile, let's find the corresponding cluster
			    			if (ThroughTile) {
			    				
			    				for (int clus=0; clus<BMTClusbank.rows(); clus++) {
			    					/*System.out.println("///////////////////");
			    					System.out.println(raybank.getShort("ID",nray)+" "+BMTClusbank.getShort("trkID",clus));
			    					System.out.println(layer+" "+BMTClusbank.getByte("layer",clus));
			    					System.out.println(sector+" "+BMTClusbank.getByte("sector",clus));
			    					System.out.println(Math.abs(ClusterExpect-BMTClusbank.getFloat("centroid",clus))+" "+DeltaCentroid);*/
			    					if (raybank.getShort("ID",nray)==BMTClusbank.getShort("trkID",clus)&&
			    							(layer-6)==BMTClusbank.getByte("layer",clus)&&
			    							sector==BMTClusbank.getByte("sector",clus)&&
			    							Math.abs(ClusterExpect-BMTClusbank.getFloat("centroid",clus))<DeltaCentroid) {
			    						
			    							BMT_struct.Cluster Clus=BMT.RecreateCluster(layer-6,sector,BMTClusbank.getFloat("centroid",clus));
			    							if (Clus.getCentroid()>1152||BMTClusbank.getFloat("centroid",clus)>1152) System.out.println(i+1);
			    							val+=Math.pow(BMT.getGeometry().getResidual_line(Clus,ray.getSlope(),ray.getPoint())/Clus.getErr(),2);
			    					}
			    				}
			    			}
			    		}
			    	}
			    }
		  }
		  System.out.println(val);
	      return val;
	   }
	
	public void SetDetectorToAlign(Barrel BMT_det, Barrel_SVT BST_det, HipoDataSource ReadFile, int lay, int sec) {
		reader= ReadFile;
		BMT=BMT_det;
		BST=BST_det;
		layer=lay;
		sector=sec;
	}

}
