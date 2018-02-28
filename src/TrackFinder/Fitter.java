package TrackFinder;

import Jama.Matrix;
import BMT_struct.*;
import java.util.*;
import TrackFinder.TrackCandidate;
import TrackFinder.FCNChi2;
import org.freehep.math.minuit.*;
import org.jlab.geom.prim.Vector3D;

public class Fitter {
		
	public Fitter() {
		
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
			    
			    //System.out.println(min.isValid()+" "+min.nfcn());
			    if (min.isValid()) {
			    	Candidates.get(num_cand+1).set_FitStatus(min.isValid());
			    	double[] res=migrad.params(); //res[0] and res[1] are phi and theta for vec, res[2] is phi for intersection point on cylinder and  res[3] is z_inter
			    	Vector3D temp=new Vector3D();
			    	temp.setXYZ(Math.cos(res[0])*Math.sin(res[1]), Math.sin(res[0])*Math.sin(res[1]), Math.cos(res[1]));
			    	Candidates.get(num_cand+1).set_VectorTrack(temp);
			    	
			    	Vector3D temp_bis=new Vector3D();
			    	temp_bis.setXYZ(Math.cos(res[2])*177.646,Math.sin(res[2])*177.646,res[3]);
			    	Candidates.get(num_cand+1).set_PointTrack(temp_bis);
			    	
			    }
			    //Get parameters
			    //System.out.println(Math.toDegrees(res[0])+" "+Math.toDegrees(res[1])+" "+Math.toDegrees(res[2])+" "+res[3]);
		        //System.out.println(Math.toDegrees( Candidates.get(num_cand+1).getPhiMean()));
			}
		}
		
	}

}
