package TrackFinder;

import org.freehep.math.minuit.FCNBase;
import TrackFinder.TrackCandidate;
import Trajectory.StraightLine;

public class FCNChi2 implements FCNBase {

	TrackCandidate ToFit;
	
	public double valueOf(double[] par)
	   {
		  StraightLine line=new StraightLine();
		  line.setPhi(par[0]);
		  line.setTheta(par[1]);
		  		  		  
		  line.setPointLocation(ToFit.getPhiMean());
		  if (!line.IsPoint_in_Y()) line.setPoint_XYZ(0, par[2], par[3]);
		  if (line.IsPoint_in_Y()) line.setPoint_XYZ(par[2], 0, par[3]);
	     	      
	      double val=0;
	      
	      if (ToFit.size()==0) return val;
	      
	      for (int clus=0;clus<ToFit.size();clus++) {
	    	  val+=Math.pow(line.getDistance(ToFit.GetCluster(clus))/ToFit.GetCluster(clus).getErr(),2);
	      }
	      
	      return val;
	   }
	
	public void SetTrackCandidate(TrackCandidate Track) {
		ToFit=Track;
	}

}
