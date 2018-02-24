package TrackFinder;

import org.freehep.math.minuit.FCNBase;
import TrackFinder.TrackCandidate;
import Trajectory.StraightLine;

public class FCNChi2 implements FCNBase {

	TrackCandidate ToFit;
	
	public double valueOf(double[] par)
	   {
		double radius=177.646;	//Radius of the middle layer which should be crossed by the track in anycase	
		  StraightLine line=new StraightLine();
		  line.setPhi(par[0]);
		  line.setTheta(par[1]);
		  		  		  
		  line.setPointLocation(ToFit.getPhiMean());
		  line.setPoint_XYZ(radius*Math.cos(par[2]), radius*Math.sin(par[2]), par[3]);
		  
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
