package TrackFinder;

import org.freehep.math.minuit.FCNBase;
import TrackFinder.TrackCandidate;
import Trajectory.StraightLine;
import TrackFinder.Fitter;
import BMT_struct.*;
import BST_struct.*;

public class FCNChi2 implements FCNBase {

	TrackCandidate ToFit;
	Barrel BMT;
	Barrel_SVT BST;
	
	public double valueOf(double[] par)
	   {
		double radius=Constant.getPointRadius();	//Radius of the middle layer which should be crossed by the track in anycase	
		  StraightLine line=new StraightLine();
		  line.setPhi(par[0]);
		  line.setTheta(par[1]);
		  		  		  
		  line.setPoint_XYZ(radius*Math.cos(par[2]), radius*Math.sin(par[2]), par[3]);
		  
	      double val=0;
	      
	      if (ToFit.size()==0) return val;
	      
	      for (int clus=0;clus<ToFit.size();clus++) {
	    	  if (ToFit.GetBMTCluster(clus).IsInFit()) val+=Math.pow(BMT.getGeometry().getResidual_line(ToFit.GetBMTCluster(clus),line.getSlope(),line.getPoint())/ToFit.GetBMTCluster(clus).getErr(),2);
	      }
	      
	      return val;
	   }
	
	public void SetTrackCandidate(Barrel BMT_det, Barrel_SVT BST_det, TrackCandidate Track) {
		ToFit=Track;
		BMT=BMT_det;
		BST=BST_det;
	}

}
