package PostProcessor;

import Trajectory.*;
import TrackFinder.*;
import Trajectory.*;
import java.util.*;
import PostProcessor.*;
import org.freehep.math.minuit.*;

public class BeamFinder {
	
	public BeamFinder() {
		
	}
	
	public StraightLine FindBeam(HashMap<Integer, ArrayList<TrackCandidate> > Events) {
		StraightLine beam=new StraightLine();
		
		//Create parameters
		MnUserParameters upar = new MnUserParameters();
	  	upar.add("phi", 0, Math.toRadians(20), -Math.toRadians(10), Math.toRadians(10));
	    upar.add("theta", 0, Math.toRadians(20), -Math.toRadians(10), Math.toRadians(10));
	    upar.add("x", 0, 50,-25,25);
	    upar.add("y", 0, 50,-25,25);
	    
	    //Create function to minimize
	    LineToLine BeamCarac=new LineToLine(Events);
	    
	    //Create Minuit (parameters and function to minimize)
	    MnMigrad migrad = new MnMigrad(BeamCarac, upar);
	    
	    //Haven t checked if it is necessarry... might duplicate Straight to parameters for minimum
	    FunctionMinimum min = migrad.minimize();
	    
	    //If fit is valid, then compute the residuals
	    if (min.isValid()) {
	    	double[] res=migrad.params();
	    	beam.setPoint_XYZ(res[2], res[3], 0);
	    	beam.setPhi(res[0]);
	    	beam.setTheta(res[1]);
	    	System.out.println(res[0]+" "+res[1]+" "+res[2]+" "+res[3]);
	    }
		
		return beam;
	}

}
