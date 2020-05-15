package TrackFinder;

import BMT_struct.*;
import BST_struct.*;
import DC_struct.DriftChambers;
import DC_struct.Segment;

import java.util.*;
import TrackFinder.*;
import org.freehep.math.minuit.*;
import org.jlab.geom.prim.Vector3D;
import Trajectory.*;

public class Fitter {
	Thread thread;	
	
	public Fitter() {
		
	}
	
	public class CustomRunnable implements Runnable {

	    TrackCandidate track;
	    Barrel BMT;
	    Barrel_SVT BST;

	    public CustomRunnable (TrackCandidate argument, Barrel BMT_det, Barrel_SVT BST_det){
	        this.track = argument;
	        this.BMT=BMT_det;
	        this.BST=BST_det;
	}

	    @Override
	    public void run() {
	    	MnUserParameters upar = new MnUserParameters();
			 if (!main.constant.isCosmic) {
				 upar.add("phi", track.getPhiSeed(), Math.toRadians(20), track.getPhiSeed()-Math.toRadians(10), track.getPhiSeed()+Math.toRadians(10));
				 upar.add("theta", track.getThetaSeed(), Math.toRadians(20), track.getThetaSeed()-Math.toRadians(10), track.getThetaSeed()+Math.toRadians(10));
				 upar.add("point_phi", Math.atan2(track.getYMean(),track.getXMean()), Math.PI/4.,Math.atan2(track.getYMean(),track.getXMean())-Math.PI/8.,Math.atan2(track.getYMean(),track.getXMean())+Math.PI/8.);
				 upar.add("point_z", track.getZMean(), 200.,track.getZMean()-100.,track.getZMean()+100.);
			 }
			 
			 if (main.constant.isCosmic) {
				 upar.add("phi", track.getPhiSeed(), Math.toRadians(40), track.getPhiSeed()-Math.toRadians(20), track.getPhiSeed()+Math.toRadians(20));
				 upar.add("theta", track.getThetaSeed(), Math.toRadians(60), track.getThetaSeed()-Math.toRadians(30), track.getThetaSeed()+Math.toRadians(30));
				 Constant.setPointRadius(Math.sqrt(track.getLastY()*track.getLastY()+track.getLastX()*track.getLastX()));
				 upar.add("point_phi", Math.atan2(track.getLastY(),track.getLastX()), Math.PI/8.,Math.atan2(track.getLastY(),track.getLastX())-Math.PI/16.,Math.atan2(track.getLastY(),track.getLastX())+Math.PI/16.);
				 upar.add("point_z", track.getLastZ(), 200.,track.getZMean()-100.,track.getZMean()+100.);
			 }
						  	    
		    //Create function to minimize
		    FCNChi2 Straight=new FCNChi2();
		    
		    //Give clusters to Chi2 to compute distance
		    Straight.SetTrackCandidate(BMT,BST,track);
		    
		 
		    //Create Minuit (parameters and function to minimize)
		    MnMigrad migrad = new MnMigrad(Straight, upar);
		    
		    boolean FitStatus=false;
		    double chi2=Double.POSITIVE_INFINITY;
		 			    
		    //Haven t checked if it is necessarry... might duplicate Straight to parameters for minimum
		    try {
		    	FunctionMinimum min = migrad.minimize();
		    	FitStatus=min.isValid();
		    	chi2=min.fval();
		    	} catch(Exception e) {
		    		FitStatus=false;
		    	}
		    	//Candidates.get(num_cand+1).Print();    
		    if (FitStatus) {
		    	track.set_FitStatus(FitStatus);
		    	double[] res=migrad.params(); //res[0] and res[1] are phi and theta for vec, res[2] is phi for intersection point on cylinder and  res[3] is z_inter
		    	double[] err_res=new double[4];
		    	for (int i=0;i<migrad.covariance().nrow();i++) {
		    		err_res[i]=Math.sqrt(migrad.covariance().get(i, i));
		    	}
		    	Vector3D temp=new Vector3D();
		    	temp.setXYZ(Math.cos(res[0])*Math.sin(res[1]), Math.sin(res[0])*Math.sin(res[1]), Math.cos(res[1]));
		    	track.set_VectorTrack(temp);
		    	
		    	Vector3D temp_bis=new Vector3D();
		    	temp_bis.setXYZ(Constant.getPointRadius()*Math.cos(res[2]),Constant.getPointRadius()*Math.sin(res[2]),res[3]);
		    	track.set_PointTrack(temp_bis);
		    	
		    //Radius of the middle layer which should be crossed by the track in anycase	
				StraightLine line=new StraightLine();
				line.setSlope_XYZ(Math.cos(res[0])*Math.sin(res[1]),Math.sin(res[0])*Math.sin(res[1]),Math.cos(res[1]));
				line.setPoint_XYZ(Constant.getPointRadius()*Math.cos(res[2]), Constant.getPointRadius()*Math.sin(res[2]), res[3]);
				for (int clus=0;clus<track.size();clus++) {
					track.AddResidual(BMT.getGeometry().getResidual_line(track.GetBMTCluster(clus),line.getSlope(),line.getPoint()));
			    }
				
				track.set_chi2(chi2);
				track.set_par(res);
				track.set_errpar(err_res);
		
		   	}
	    }
	}
			
	public void CVTStraightTrack(Barrel BMT, Barrel_SVT BST, HashMap<Integer, TrackCandidate> Candidates) {
		//Use minimizer
		for (int num_cand=0;num_cand<Candidates.size();num_cand++) {
			//if (Candidates.get(num_cand+1).size()>6&&!main.constant.isCosmic) System.err.println("Error: TrackCandidate with more than 6 clusters");
			if (Candidates.get(num_cand+1).IsFittable()) {
				thread = new Thread(new CustomRunnable(Candidates.get(num_cand+1),BMT,BST));
			    thread.start();
			    long endTimeMillis = System.currentTimeMillis() + 10000;
			    while (thread.isAlive()) {
			        if (System.currentTimeMillis() > endTimeMillis) {
			        thread.interrupt();
			        break;
			        }
			       	        
			    }
				
			}
		}
	}
		

	public void DCStraightTrack(Segment seg) {
		//Use minimizer
		MnUserParameters upar = new MnUserParameters();
		double phi_init=seg.getClusters().get(0).getWires().get(0).getWirePoint().phi();
		double theta_init=seg.getClusters().get(0).getWires().get(0).getWirePoint().theta();
		upar.add("phi",phi_init, 2*Math.toRadians(30) , phi_init-Math.toRadians(30) , phi_init+Math.toRadians(30));
		upar.add("theta",theta_init, Math.toRadians(25), 0,  Math.toRadians(50));
		upar.add("lx",0, 200,-100, 100);
		upar.add("ly",0 , 200,-100, 100);
		upar.add("lz",100 , 200, 0, 300);
		upar.fix("lz");
	
		DCFCNChi2 DCStraight=new DCFCNChi2();
		
		DCStraight.SetTrackCandidate(seg);
		
		//Create Minuit (parameters and function to minimize)
	    MnMigrad migrad = new MnMigrad(DCStraight, upar);
	 			    
	    //Haven t checked if it is necessarry... might duplicate Straight to parameters for minimum
	    FunctionMinimum min = migrad.minimize();
	    if (min.isValid()) {
	    	double[] res=migrad.params();
	    	StraightLine HBtrack=new StraightLine();
	    	HBtrack.setSlope_XYZ(Math.cos(res[0])*Math.sin(res[1]),Math.sin(res[0])*Math.sin(res[1]),Math.cos(res[1]));
	    	HBtrack.setPoint_XYZ(res[2], res[3] ,res[4]);
	    	seg.setHBtrack(HBtrack);
	    	seg.setChi2(min.fval());
	    	seg.setFitStatus(true);
	    }
	    return; 
	}
	
	public void DCStraightTrack_init(Segment seg) {
		//Use minimizer
		MnUserParameters upar = new MnUserParameters();
		double phi_init=seg.getClusters().get(0).getWires().get(0).getWirePoint().phi();
		double theta_init=seg.getClusters().get(0).getWires().get(0).getWirePoint().theta();
		upar.add("phi",phi_init, 2*Math.toRadians(30) , phi_init-Math.toRadians(30) , phi_init+Math.toRadians(30));
		upar.add("theta",theta_init, Math.toRadians(25), 0,  Math.toRadians(50));
		upar.add("lx",0, 200,-100, 100);
		upar.add("ly",0 , 200,-100, 100);
		upar.add("lz",0 , 200, 0, 300);
		upar.fix("lz");upar.fix("lx");upar.fix("ly");
	
		DCFCNChi2 DCStraight=new DCFCNChi2();
		
		DCStraight.SetTrackCandidate(seg);
		
		//Create Minuit (parameters and function to minimize)
	    MnMigrad migrad = new MnMigrad(DCStraight, upar);
	 			    
	    //Haven t checked if it is necessarry... might duplicate Straight to parameters for minimum
	    FunctionMinimum min = migrad.minimize();
	    if (min.isValid()) {
	    	double[] res=migrad.params();
	    	StraightLine HBtrack=new StraightLine();
	    	HBtrack.setSlope_XYZ(Math.cos(res[0])*Math.sin(res[1]),Math.sin(res[0])*Math.sin(res[1]),Math.cos(res[1]));
	    	HBtrack.setPoint_XYZ(res[2], res[3] , res[4]);
	    	seg.setHBtrack(HBtrack);
	    	
	    }
	}

}
