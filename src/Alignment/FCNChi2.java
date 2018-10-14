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
		  
		  //First three parameters are Rotations
		  BMT_geo.Constants.setRx(layer, sector, par[0]);
		  BMT_geo.Constants.setRy(layer, sector, par[1]);
		  BMT_geo.Constants.setRz(layer, sector, par[2]);
		  //Last three parameters are rotations
		  BMT_geo.Constants.setCx(layer, sector, par[3]);
		  BMT_geo.Constants.setCy(layer, sector, par[4]);
		  BMT_geo.Constants.setCz(layer, sector, par[5]);
		  
		  while(reader.hasEvent()) {
			  DataEvent event = reader.gotoEvent(count);
				count++;
			    if(event.hasBank("CVTRec::Cosmics")&&event.hasBank("CVTRec::Trajectory")) {
			    	StraightLine ray=new StraightLine();
			    	DataBank raybank=event.getBank("CVTRec::Cosmics");
			    	DataBank Trajbank=event.getBank("CVTRec::Trajectory");
			    	DataBank BMTClusbank=event.getBank("BMTRec::Clusters");
			    	for (int nray=0;nray<raybank.rows();nray++) {
			    		ThroughTile=false;
			    		ClusterExpect=-20;
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
			    				if (raybank.getShort("ID",nray)==BMTClusbank.getShort("trkID",clus)&&
				    					layer==BMTClusbank.getByte("layer",clus)&&
				    					sector==BMTClusbank.getByte("sector",clus)&&
				    					Math.abs(ClusterExpect-BMTClusbank.getFloat("centroid",clus))<DeltaCentroid) {
			    						
			    					val+=BMT.getGeometry().getResidual_line(BMT.RecreateCluster(layer,sector,BMTClusbank.getFloat("centroid",clus)),ray.getSlope(),ray.getPoint());
			    				}
			    			}
			    		}
			    	}
			    }
		  }
	      return val;
	   }
	
	public void SetDetectorToAlign(Barrel BMT_det, Barrel_SVT BST_det, HipoDataSource Filereader, int lay, int sec) {
		reader=Filereader;
		BMT=BMT_det;
		BST=BST_det;
		layer=lay;
		sector=sec;
	}

}
