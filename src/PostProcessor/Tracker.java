package PostProcessor;

import TrackFinder.*;
import Trajectory.*;
import java.util.*;
import Analyzer.*;
import PostProcessor.*;

public class Tracker {
	
	private int ntarget;
	private int nBeamFinder;
	private VertexFinder Vexter;
		
	private HashMap<Integer, ArrayList<TrackCandidate> > Events;
	
	public Tracker() {
		ntarget=0;
		nBeamFinder=50;
		Events=new HashMap<Integer, ArrayList<TrackCandidate> >();
		Vexter=new VertexFinder();
			
	}
	
	public void addEvent(int event, HashMap<Integer,TrackCandidate> candidates) {
		ArrayList<TrackCandidate> TrackList=new ArrayList<TrackCandidate>();
		
		//Load the events and store them until enough statistics to find beam
		for (int i=0;i<candidates.size();i++) {
			if (candidates.get(i+1).IsVeryGoodCandidate()) {
				TrackList.add(candidates.get(i+1));
				if (candidates.get(i+1).IsFromTarget()) ntarget++;
			}
		}
		
		Events.put(Events.size()+1,TrackList);
		
		if (ntarget>nBeamFinder) {
			BeamFinder Beamer=new BeamFinder();
			StraightLine Beam=Beamer.FindBeam(Events);
			Vexter.FindVertices(Beam,Events);
			Events.clear();
			ntarget=0;
		}
		
	}

}
