package TrackFinder;

import Jama.Matrix;
import BMT_struct.*;
import java.util.*;
import TrackFinder.TrackCandidate;
import TrackFinder.FCNChi2;
import org.freehep.math.minuit.*;
import org.jlab.geom.prim.Vector3D;
import Trajectory.*;

public class Fitter {
	private double radius;
	
	public Fitter() {
		radius=177.646;
	}
	
	public void StraightTrack(HashMap<Integer, TrackCandidate> Candidates) {
		//Use minimizer
		for (int num_cand=0;num_cand<Candidates.size();num_cand++) {
			if (Candidates.get(num_cand+1).size()>6) System.out.println("Error: TrackCandidate with more than 6 clusters");
			if (Candidates.get(num_cand+1).IsFittable()) {
				//Create parameters
				MnUserParameters upar = new MnUserParameters();
			    //upar.add("phi", Math.PI/2., Math.PI/2., 0, Math.PI);
				upar.add("phi", Candidates.get(num_cand+1).getPhiMean(), Math.toRadians(40), Candidates.get(num_cand+1).getPhiMean()-Math.toRadians(45), Candidates.get(num_cand+1).getPhiMean()+Math.toRadians(45));
			    upar.add("theta", Math.PI/2., Math.PI/2. , Math.toRadians(25), Math.toRadians(150));
			    upar.add("point_phi", Candidates.get(num_cand+1).getPhiMean(), Math.PI/2.,Candidates.get(num_cand+1).getPhiMean()-Math.PI/4.,Candidates.get(num_cand+1).getPhiMean()+Math.PI/4.);
			    upar.add("point_z", 0, 300.,-300.,300.);
			    
			    //Create function to minimize
			    FCNChi2 Straight=new FCNChi2();
			    
			    //Give clusters to Chi2 to compute distance
			    Straight.SetTrackCandidate(Candidates.get(num_cand+1));
			    
			    //Create Minuit (parameters and function to minimize)
			    MnMigrad migrad = new MnMigrad(Straight, upar);
			    
			    //Haven t checked if it is necessarry... might duplicate Straight to parameters for minimum
			    FunctionMinimum min = migrad.minimize();
			    
			    if (min.isValid()) {
			    	Candidates.get(num_cand+1).set_FitStatus(min.isValid());
			    	double[] res=migrad.params(); //res[0] and res[1] are phi and theta for vec, res[2] is phi for intersection point on cylinder and  res[3] is z_inter
			    	Vector3D temp=new Vector3D();
			    	temp.setXYZ(Math.cos(res[0])*Math.sin(res[1]), Math.sin(res[0])*Math.sin(res[1]), Math.cos(res[1]));
			    	Candidates.get(num_cand+1).set_VectorTrack(temp);
			    	
			    	Vector3D temp_bis=new Vector3D();
			    	temp_bis.setXYZ(radius*Math.cos(res[2]),radius*Math.sin(res[2]),res[3]);
			    	Candidates.get(num_cand+1).set_PointTrack(temp_bis);
			    	
			    	//double radius=177.646;	//Radius of the middle layer which should be crossed by the track in anycase	
					StraightLine line=new StraightLine();
					line.setPhi(res[0]);
					line.setTheta(res[1]);
					line.setPoint_XYZ(radius*Math.cos(res[2]), radius*Math.sin(res[2]), res[3]);
					for (int clus=0;clus<Candidates.get(num_cand+1).size();clus++) {
						Candidates.get(num_cand+1).GetCluster(clus).set_residual(line.getDistance(Candidates.get(num_cand+1).GetCluster(clus)));
				    }
					Candidates.get(num_cand+1).set_chi2(min.fval());
			   	}
			    //Get parameters
			    //System.out.println(Math.toDegrees(res[0])+" "+Math.toDegrees(res[1])+" "+Math.toDegrees(res[2])+" "+res[3]);
		        //System.out.println(Math.toDegrees( Candidates.get(num_cand+1).getPhiMean()));
			}
		}
		
	}

}
