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
		  if (!line.IsCosmic()) line.setPoint_XYZ(par[3], par[4], 0);
		  if (!line.IsCosmic()) line.setPoint_XYZ(par[3], 0, par[4]);
	     	      
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
