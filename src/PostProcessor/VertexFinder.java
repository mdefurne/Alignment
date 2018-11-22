package PostProcessor;

import TrackFinder.*;
import Trajectory.*;
import java.util.*;
import Analyzer.*;
import DC_struct.Segment;
import PostProcessor.*;

public class VertexFinder {
	
	public VertexFinder() {
		
	}
	
	public void FindCVTVertices(StraightLine Beam, HashMap<Integer, ArrayList<TrackCandidate> > Events) {
		
		StraightLine track=new StraightLine();
		
		for (int i=0; i<Events.size();i++) {
			 for (int j=0; j<Events.get(i+1).size();j++) {
				if (Events.get(i+1).get(j).IsFittable()) {
					track.setPoint_XYZ(Events.get(i+1).get(j).get_PointTrack().x(), Events.get(i+1).get(j).get_PointTrack().y(), Events.get(i+1).get(j).get_PointTrack().z());
					track.setSlope_XYZ(Events.get(i+1).get(j).get_VectorTrack().x(),Events.get(i+1).get(j).get_VectorTrack().y(),Events.get(i+1).get(j).get_VectorTrack().z());
					
					if (track.getDistanceToLine(Beam)<5) Events.get(i+1).get(j).setVertex(track.getClosestPointToLine(Beam));
				}
			 }
		}
	}
	
	public void FindFDVertices(ArrayList<StraightLine> Beam,  HashMap<Integer, ArrayList<Segment>> Tracks) {
		
		for (int sec=1;sec<7;sec++) {
			for (int i=0; i<Tracks.get(sec).size();i++) {
				if (Tracks.get(sec).get(i).getHBtrack().getDistanceToLine(Beam.get(sec))<50) Tracks.get(sec).get(i).setVertex(Tracks.get(sec).get(i).getHBtrack().getClosestPointToLine(Beam.get(sec)));
			}
		}
	}

}
