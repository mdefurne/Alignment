package Alignment;

import org.freehep.math.minuit.FCNBase;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo3.Hipo3DataSource;
import Trajectory.StraightLine;
import BMT_struct.*;
import BST_struct.*;
import org.jlab.io.base.DataBank;
import org.jlab.geom.prim.Vector3D;
import java.util.*;

public class MVTvsSVT implements FCNBase {

	Hipo3DataSource[] reader;
	Barrel BMT;
	Barrel_SVT BST;
		
	/*public double valueOf(double[] par)
	   {
		
		  double val=0;	  
		  int count=0;
		  
		  
		  //Check is cluster is compatible with expected intercept with track
		  float DeltaCentroid=10;
		  
		  //To store Layer, sector and cluster centroid
		  ArrayList<Integer> BMTLayer=new ArrayList<Integer>();
		  ArrayList<Integer> BMTSector=new ArrayList<Integer>();
		  ArrayList<Float> BMTCentroid=new ArrayList<Float>();
		  
		  
		  //First three parameters are Rotations
		  BMT_geo.Constants.setRxCVT(par[0]);
		  BMT_geo.Constants.setRyCVT(par[1]);
		  BMT_geo.Constants.setRzCVT(par[2]);
		  //Last three parameters are rotations
		  BMT_geo.Constants.setCxCVT(par[3]);
		  BMT_geo.Constants.setCyCVT(par[4]);
		  BMT_geo.Constants.setCzCVT(par[5]);
		
		  System.out.println(par[0]+" "+par[1]+" "+par[2]+" "+par[3]+" "+par[4]+" "+par[5]);
		 for (int infile=0; infile<reader.length; infile++) {
			 for (int i=0; i<reader[infile].getSize();i++) {
				 DataEvent event = reader[infile].gotoEvent(i);
				
				 if(event.hasBank("CVTRec::Cosmics")&&event.hasBank("CVTRec::Trajectory")) {
					 StraightLine ray=new StraightLine();
					 DataBank raybank=event.getBank("CVTRec::Cosmics");
					 DataBank Trajbank=event.getBank("CVTRec::Trajectory");
					 DataBank BMTClusbank=event.getBank("BMTRec::Clusters");
					 for (int nray=0;nray<raybank.rows();nray++) {
						 BMTLayer.clear();
						 BMTSector.clear();
						 BMTCentroid.clear();
			    		
						 if (raybank.getShort("ndf",nray)>=1) {//if (raybank.getShort("ndf",nray)>=3) {
							 boolean good_track=true;
							 ray.setSlope_XYZ(raybank.getFloat("trkline_yx_slope", nray), 1, raybank.getFloat("trkline_yz_slope", nray));
							 ray.setPoint_XYZ(raybank.getFloat("trkline_yx_interc", nray)*10, 0, raybank.getFloat("trkline_yz_interc", nray)*10);
			    		
							 //Check the calcCentroid of the track and make sure the track went through the tile we want to align
							 for (int npt=0; npt<Trajbank.rows(); npt++) {
								 if (raybank.getShort("ID",nray)==Trajbank.getShort("ID",npt)&&
										 Trajbank.getByte("LayerTrackIntersPlane",npt)>6) {
			    							BMTLayer.add((int) (Trajbank.getByte("LayerTrackIntersPlane",npt)-6));
			    							BMTSector.add((int)Trajbank.getByte("SectorTrackIntersPlane",npt));
			    							BMTCentroid.add(Trajbank.getFloat("CalcCentroidStrip",npt));
			    							if (!BMT.getGeometry().isinfiducial(new Vector3D(Trajbank.getFloat("XtrackIntersPlane",npt),Trajbank.getFloat("YtrackIntersPlane",npt),0),(int)Trajbank.getByte("SectorTrackIntersPlane",npt) , 15)) good_track=false;
								 }	
								 if (raybank.getShort("ID",nray)==Trajbank.getShort("ID",npt)&&Trajbank.getByte("LayerTrackIntersPlane",npt)<=6&&Trajbank.getFloat("PhitrackIntersPlane",npt)>40) good_track=false;
							 }
			    		
			    			//Since the track is supposed to have crossed the tile, let's find the corresponding cluster
							 if (good_track) {
								 for (int cl=0;cl<BMTLayer.size();cl++) {
									 for (int clus=0; clus<BMTClusbank.rows(); clus++) {
										 if (raybank.getShort("ID",nray)==BMTClusbank.getShort("trkID",clus)&&BMTLayer.get(cl)==BMTClusbank.getByte("layer",clus)&&BMTSector.get(cl)==BMTClusbank.getByte("sector",clus)){
											 if (Math.abs(BMTCentroid.get(cl)-BMTClusbank.getFloat("centroid",clus))<DeltaCentroid) {
			    							
												 BMT_struct.Cluster Clus=BMT.RecreateCluster(BMTLayer.get(cl),BMTSector.get(cl),BMTClusbank.getFloat("centroid",clus));
			    							
												 val+=Math.pow(BMT.getGeometry().getResidual_line(Clus,ray.getSlope(),ray.getPoint())/Clus.getErr(),2);
											 }
										 }
									 }
								 }
							 }
						 }
					 }
				 }
			 }
		 }	 
		  System.out.println(val);
	      return val;
	   }*/
	
	public double valueOf(double[] par)
	   {
		
		  double val=0;	  
		  int count=0;
		  
		  
		  //Check is cluster is compatible with expected intercept with track
		  float DeltaCentroid=10;
		  
		  //To store Layer, sector and cluster centroid
		  ArrayList<Integer> BSTLayer=new ArrayList<Integer>();
		  ArrayList<Integer> BSTSector=new ArrayList<Integer>();
		  ArrayList<Float> BSTCentroid=new ArrayList<Float>();
		  
		  
		  //First three parameters are Rotations
		  BMT_geo.Constants.setRxCVT(par[0]);
		  BMT_geo.Constants.setRyCVT(par[1]);
		  BMT_geo.Constants.setRzCVT(par[2]);
		  //Last three parameters are rotations
		  BMT_geo.Constants.setCxCVT(par[3]);
		  BMT_geo.Constants.setCyCVT(par[4]);
		  BMT_geo.Constants.setCzCVT(par[5]);
		
		  System.out.println(par[0]+" "+par[1]+" "+par[2]+" "+par[3]+" "+par[4]+" "+par[5]);
		 for (int infile=0; infile<reader.length; infile++) {
			 for (int i=0; i<reader[infile].getSize();i++) {
				 DataEvent event = reader[infile].gotoEvent(i);
				
				 if(event.hasBank("CVTRec::Cosmics")&&event.hasBank("CVTRec::Trajectory")) {
					 StraightLine ray=new StraightLine();
					 DataBank raybank=event.getBank("CVTRec::Cosmics");
					 DataBank Trajbank=event.getBank("CVTRec::Trajectory");
					 DataBank BSTClusbank=event.getBank("BSTRec::Clusters");
					 for (int nray=0;nray<raybank.rows();nray++) {
						 BSTLayer.clear();
						 BSTSector.clear();
						 BSTCentroid.clear();
			    		
						 if (raybank.getShort("ndf",nray)>=1) {//if (raybank.getShort("ndf",nray)>=3) {
							 boolean good_track=true;
							 ray.setSlope_XYZ(raybank.getFloat("trkline_yx_slope", nray), 1, raybank.getFloat("trkline_yz_slope", nray));
							 ray.setPoint_XYZ(raybank.getFloat("trkline_yx_interc", nray)*10, 0, raybank.getFloat("trkline_yz_interc", nray)*10);
							 Vector3D slopelab=BMT.getGeometry().Slope_CVTToLabFrame(ray.getSlope());
							 Vector3D pointlab=BMT.getGeometry().Point_CVTToLabFrame(ray.getPoint());
							 ray.setSlope_XYZ(slopelab.x(), slopelab.y(), slopelab.z());
							 ray.setPoint_XYZ(pointlab.x(), pointlab.y(), pointlab.z());
							 //Check the calcCentroid of the track and make sure the track went through the tile we want to align
							 for (int npt=0; npt<Trajbank.rows(); npt++) {
								 if (raybank.getShort("ID",nray)==Trajbank.getShort("ID",npt)&&
										 Trajbank.getByte("LayerTrackIntersPlane",npt)<7) {
			    							BSTLayer.add((int) (Trajbank.getByte("LayerTrackIntersPlane",npt)));
			    							BSTSector.add((int)Trajbank.getByte("SectorTrackIntersPlane",npt));
			    							BSTCentroid.add(Trajbank.getFloat("CalcCentroidStrip",npt));
			    							//if (!BST.getGeometry().isinfiducial(new Vector3D(Trajbank.getFloat("XtrackIntersPlane",npt),Trajbank.getFloat("YtrackIntersPlane",npt),0),(int)Trajbank.getByte("SectorTrackIntersPlane",npt) , 15)) good_track=false;
								 }	
								 //if (raybank.getShort("ID",nray)==Trajbank.getShort("ID",npt)&&Trajbank.getByte("LayerTrackIntersPlane",npt)<=6&&Trajbank.getFloat("PhitrackIntersPlane",npt)>40) good_track=false;
							 }
			    		
			    			//Since the track is supposed to have crossed the tile, let's find the corresponding cluster
							 if (good_track) {
								 for (int cl=0;cl<BSTLayer.size();cl++) {
									 for (int clus=0; clus<BSTClusbank.rows(); clus++) {
										 if (raybank.getShort("ID",nray)==BSTClusbank.getShort("trkID",clus)&&BSTLayer.get(cl)==BSTClusbank.getByte("layer",clus)&&BSTSector.get(cl)==BSTClusbank.getByte("sector",clus)){
											 if (Math.abs(BSTCentroid.get(cl)-BSTClusbank.getFloat("centroid",clus))<DeltaCentroid) {
			    							
												 BST_struct.Cluster Clus=BST.RecreateCluster(BSTLayer.get(cl),BSTSector.get(cl),BSTClusbank.getFloat("centroid",clus));
												 Vector3D inter=BST.getGeometry().getIntersectWithRay(Clus.getLayer(), Clus.getSector(), ray.getSlope(), ray.getPoint());
	    											if (!Double.isNaN(inter.x())) val+=Math.pow(BST.getGeometry().getResidual_line(Clus.getLayer(),Clus.getSector(),Clus.getCentroid(),inter)
	    													/BST.getGeometry().getSingleStripResolution(Clus.getLayer(), (int)Clus.getCentroid(), inter.z()),2);		    							
												 
											 }
										 }
									 }
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
	
	public void SetDetectorToAlign(Barrel BMT_det, Barrel_SVT BST_det, Hipo3DataSource[] ReadFile) {
		reader= ReadFile;
		BMT=BMT_det;
		BST=BST_det;
	}

}
