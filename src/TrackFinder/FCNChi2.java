package TrackFinder;

import org.freehep.math.minuit.FCNBase;
import TrackFinder.TrackCandidate;
import Trajectory.StraightLine;

public class FCNChi2 implements FCNBase {

	TrackCandidate ToFit;
	
	public double valueOf(double[] par)
	   {
		  StraightLine line=new StraightLine();
		  line.setSlope_XYZ(par[0], par[1], par[2]);
		  line.setPoint_XYZ(par[3], par[4], par[5]);
	     	      
	      double val=0;
	      
	      if (ToFit.size()==0) return val;
	      
	      for (int clus=0;clus<ToFit.size();clus++) {
	    	  val+=Math.pow(line.getDistance(ToFit.GetCluster(clus)),2)/ToFit.GetCluster(clus).getErr();
	      }
	     
	      return val;
	   }
	
	public void SetTrackCandidate(TrackCandidate GetIt) {
		ToFit=GetIt;
	}

}
