package TrackFinder;

import Jama.Matrix;
import BMT_struct.*;
import BST_struct.*;
import java.util.*;
import TrackFinder.*;
import org.freehep.math.minuit.*;
import org.jlab.geom.prim.Vector3D;
import Trajectory.*;

public class Fitter {
		
	public Fitter() {
		
	}
			
	public void StraightTrack(Barrel BMT, Barrel_SVT BST, HashMap<Integer, TrackCandidate> Candidates) {
		//Use minimizer
		for (int num_cand=0;num_cand<Candidates.size();num_cand++) {
			if (Candidates.get(num_cand+1).size()>6) System.err.println("Error: TrackCandidate with more than 6 clusters");
			if (Candidates.get(num_cand+1).IsFittable()) {
								
				//Create parameters
				MnUserParameters upar = new MnUserParameters();
				 if (!main.constant.isCosmic) {
					 upar.add("phi", Candidates.get(num_cand+1).getPhiSeed(), Math.toRadians(20), Candidates.get(num_cand+1).getPhiSeed()-Math.toRadians(10), Candidates.get(num_cand+1).getPhiSeed()+Math.toRadians(10));
					 upar.add("theta", Candidates.get(num_cand+1).getThetaSeed(), Math.toRadians(20), Candidates.get(num_cand+1).getThetaSeed()-Math.toRadians(10), Candidates.get(num_cand+1).getThetaSeed()+Math.toRadians(10));
					 upar.add("point_phi", Math.atan2(Candidates.get(num_cand+1).getYMean(),Candidates.get(num_cand+1).getXMean()), Math.PI/4.,Math.atan2(Candidates.get(num_cand+1).getYMean(),Candidates.get(num_cand+1).getXMean())-Math.PI/8.,Math.atan2(Candidates.get(num_cand+1).getYMean(),Candidates.get(num_cand+1).getXMean())+Math.PI/8.);
					 upar.add("point_z", Candidates.get(num_cand+1).getZMean(), 600.,Candidates.get(num_cand+1).getZMean()-300.,Candidates.get(num_cand+1).getZMean()+300.);
				 }
				 
				 if (main.constant.isCosmic) {
					 upar.add("phi", Candidates.get(num_cand+1).getPhiSeed(), Math.toRadians(90), Candidates.get(num_cand+1).getPhiSeed()-Math.toRadians(45), Candidates.get(num_cand+1).getPhiSeed()+Math.toRadians(45));
					 upar.add("theta", Candidates.get(num_cand+1).getThetaSeed(), Math.toRadians(90), Candidates.get(num_cand+1).getThetaSeed()-Math.toRadians(45), Candidates.get(num_cand+1).getThetaSeed()+Math.toRadians(45));
					 upar.add("point_phi", Math.atan2(Candidates.get(num_cand+1).getLastY(),Candidates.get(num_cand+1).getLastX()), Math.PI/4.,Math.atan2(Candidates.get(num_cand+1).getLastY(),Candidates.get(num_cand+1).getLastX())-Math.PI/8.,Math.atan2(Candidates.get(num_cand+1).getLastY(),Candidates.get(num_cand+1).getLastX())+Math.PI/8.);
					 upar.add("point_z", Candidates.get(num_cand+1).getLastZ(), 600.,Candidates.get(num_cand+1).getZMean()-150.,Candidates.get(num_cand+1).getZMean()+150.);
				 }
					   
			  	    
			    //Create function to minimize
			    FCNChi2 Straight=new FCNChi2();
			    
			    //Give clusters to Chi2 to compute distance
			    Straight.SetTrackCandidate(BMT,BST,Candidates.get(num_cand+1));
			    
			    //Create Minuit (parameters and function to minimize)
			    MnMigrad migrad = new MnMigrad(Straight, upar);
			    
			    //Haven t checked if it is necessarry... might duplicate Straight to parameters for minimum
			    FunctionMinimum min = migrad.minimize();
			   
			    if (min.isValid()) {
			    	Candidates.get(num_cand+1).set_FitStatus(min.isValid());
			    	double[] res=migrad.params(); //res[0] and res[1] are phi and theta for vec, res[2] is phi for intersection point on cylinder and  res[3] is z_inter
			    	double[] err_res=new double[4];
			    	for (int i=0;i<migrad.covariance().nrow();i++) {
			    		err_res[i]=Math.sqrt(migrad.covariance().get(i, i));
			    	}
			    	Vector3D temp=new Vector3D();
			    	temp.setXYZ(Math.cos(res[0])*Math.sin(res[1]), Math.sin(res[0])*Math.sin(res[1]), Math.cos(res[1]));
			    	Candidates.get(num_cand+1).set_VectorTrack(temp);
			    	
			    	Vector3D temp_bis=new Vector3D();
			    	temp_bis.setXYZ(Constant.getPointRadius()*Math.cos(res[2]),Constant.getPointRadius()*Math.sin(res[2]),res[3]);
			    	Candidates.get(num_cand+1).set_PointTrack(temp_bis);
			    	
			    //Radius of the middle layer which should be crossed by the track in anycase	
					StraightLine line=new StraightLine();
					line.setSlope_XYZ(Math.cos(res[0])*Math.sin(res[1]),Math.sin(res[0])*Math.sin(res[1]),Math.cos(res[1]));
					line.setPoint_XYZ(Constant.getPointRadius()*Math.cos(res[2]), Constant.getPointRadius()*Math.sin(res[2]), res[3]);
					for (int clus=0;clus<Candidates.get(num_cand+1).size();clus++) {
						Candidates.get(num_cand+1).AddResidual(BMT.getGeometry().getResidual_line(Candidates.get(num_cand+1).GetBMTCluster(clus),line.getSlope(),line.getPoint()));
				    }
					
					Candidates.get(num_cand+1).set_chi2(min.fval());
					Candidates.get(num_cand+1).ComputeLocalDerivative(res,err_res);
			   	}

			}
		}
		
	}

}
