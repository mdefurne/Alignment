package TrackFinder;

import Jama.Matrix;
import BMT_struct.*;
import java.util.*;
import TrackFinder.TrackCandidate;

public class Fitter {
		
	public Fitter() {
		
	}
	
	public void StraightTrack(HashMap<Integer, TrackCandidate> Candidates) {
		//Either x=ay+b or {x=x+b and y=ax}:: For z, we search for z=c*r+vz
		for (int num_cand=0;num_cand<Candidates.size();num_cand++) {
			if (Candidates.get(num_cand+1).IsFittable()) {
				double[][] Axy= {{0,0},{0,0}};
				double[] Bxy= {0,0};
			}
		}
		
	}

}
