package PostProcessor;

import org.freehep.math.minuit.FCNBase;
import TrackFinder.TrackCandidate;
import Trajectory.StraightLine;
import PostProcessor.BeamFinder;
import java.util.*;

public class LineToLine implements FCNBase{
	
	HashMap<Integer, ArrayList<TrackCandidate> > Events;
	
	public LineToLine(HashMap<Integer, ArrayList<TrackCandidate> > Blocks) {
		Events=Blocks;
	}
	
	public double valueOf(double[] par)
	   {
		 double val=0;
		 StraightLine line=new StraightLine();
		 line.setPoint_XYZ(par[2], par[3], 0);
		 line.setPhi(par[0]);line.setTheta(par[1]);
		 
		 StraightLine track=new StraightLine();
		 
		 for (int i=0; i<Events.size();i++) {
			 for (int j=0; j<Events.get(i+1).size();j++) {
				if (Events.get(i+1).get(j).IsFromTarget()) {
					track.setPoint_XYZ(Events.get(i+1).get(j).get_PointTrack().x(), Events.get(i+1).get(j).get_PointTrack().y(), Events.get(i+1).get(j).get_PointTrack().z());
					track.setSlope_XYZ(Events.get(i+1).get(j).get_VectorTrack());
					double dist=line.getDistanceToLine(track);
					if (dist>10) {
						System.out.println("Arrrgh");
						System.out.println(dist);
						line.Print();
						track.Print();
					}
					val+=Math.pow(dist,2); 
				}
			 }
		 }
		 
		 return val;
	   }
	
}
