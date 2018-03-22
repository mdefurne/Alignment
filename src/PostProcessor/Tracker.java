package PostProcessor;

import TrackFinder.*;
import Trajectory.*;
import java.util.*;
import Analyzer.*;
import PostProcessor.*;
import Analyzer.*;

public class Tracker {
	
	private int ntarget;
	private int nBeamFinder;
	private VertexFinder Vexter;
	private BeamAna BPMer;
		
	private HashMap<Integer, ArrayList<TrackCandidate> > Events;
	
	public Tracker() {
		ntarget=0;
		nBeamFinder=30;
		Events=new HashMap<Integer, ArrayList<TrackCandidate> >();
		Vexter=new VertexFinder();
		BPMer=new BeamAna();	
	}
	
	public void addEvent(int event, HashMap<Integer,TrackCandidate> candidates) {
		ArrayList<TrackCandidate> TrackList=new ArrayList<TrackCandidate>();
		
		//Load the events and store them until enough statistics to find beam
		for (int i=0;i<candidates.size();i++) {
			if (candidates.get(i+1).IsGoodCandidate()) {
				TrackList.add(candidates.get(i+1));
				if (candidates.get(i+1).IsFromTarget()) ntarget++;
			}
		}
		
		Events.put(Events.size()+1,TrackList);
		
		if (ntarget>nBeamFinder) {
			BeamFinder Beamer=new BeamFinder();
			StraightLine Beam=Beamer.FindBeam(Events);
			Vexter.FindVertices(Beam,Events);
			BPMer.Analyze(Beam, Events);
			Events.clear();
			ntarget=0;
		}
		
	}
	
	public void draw() {
		BPMer.draw();
	}

}
