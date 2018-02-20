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
			    upar.add("p0", 0, 2*Math.PI);
			    upar.add("p1", 0, Math.PI);
			    upar.add("p2", -50, 50.);
			    upar.add("p3", -50., 50.);
			    
			    FCNChi2 Straight=new FCNChi2();
			    Straight.SetTrackCandidate(Candidates.get(num_cand+1));
			    MnMigrad migrad = new MnMigrad(Straight, upar);
			    FunctionMinimum min = migrad.minimize();
			    
			    double[] params = {1,1,1,1,1,1};
		        double[] error = {1,1,1,1,1,1};
		        MnScan scan = new MnScan(Straight, params, error);
		        System.out.println("scan parameters: "+scan.parameters());
			}
		}
		
	}

}
