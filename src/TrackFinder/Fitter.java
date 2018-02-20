package TrackFinder;

import Jama.Matrix;
import BMT_struct.*;
import java.util.*;
import TrackFinder.TrackCandidate;
import TrackFinder.FCNChi2;
import org.freehep.math.minuit.*;

public class Fitter {
		
	public Fitter() {
		
	}
	
	public void StraightTrack(HashMap<Integer, TrackCandidate> Candidates) {
		//Use minimizer
		for (int num_cand=0;num_cand<Candidates.size();num_cand++) {
			if (Candidates.get(num_cand+1).IsFittable()) {
				MnUserParameters upar = new MnUserParameters();
			    upar.add("phi", Math.PI/2., Math.PI/2., 0, Math.PI);
			    upar.add("theta", Math.PI/2., Math.PI/2., Math.toRadians(25), Math.toRadians(150));
			    upar.add("point_x", 0, 50.,-50.,50);
			    upar.add("point_yz", 0, 50.,-50.,50.);
			    
			    FCNChi2 Straight=new FCNChi2();
			    Straight.SetTrackCandidate(Candidates.get(num_cand+1));
			    MnMigrad migrad = new MnMigrad(Straight, upar);
			    FunctionMinimum min = migrad.minimize();
			    System.out.println(min.isValid()+" "+min.nfcn());
			    double[] res=migrad.params();
		        System.out.println(Math.toDegrees(res[0])+" "+Math.toDegrees(res[1])+" "+res[2]+" "+res[3]);
			}
		}
		
	}

}
