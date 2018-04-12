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
		
		TrackList=DuplicateRemoval(candidates);
		
		//Load the events and store them until enough statistics to find beam
		for (int i=0;i<TrackList.size();i++) {
				if (TrackList.get(i+1).IsFromTarget()) ntarget++;
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
	
	private ArrayList<TrackCandidate> DuplicateRemoval(HashMap<Integer,TrackCandidate> cand){
		ArrayList<TrackCandidate> Temp_good=new ArrayList<TrackCandidate>();
		ArrayList<TrackCandidate> good=new ArrayList<TrackCandidate>();
		//Keep first only good tracks
		for (int i=0;i<cand.size();i++) {
			if (cand.get(i+1).IsGoodCandidate()) {
				Temp_good.add(cand.get(i+1));
			}
		}
		
		for (int i=0;i<Temp_good.size();i++) {
			good.add(Temp_good.get(i));
			for (int j=i+1;j<Temp_good.size();j++) {
				if (good.get(good.size()-1).IsSimilar(Temp_good.get(j))&&good.get(good.size()-1).get_chi2()>Temp_good.get(j).get_chi2()) {
					good.remove(good.size()-1);
					good.add(Temp_good.get(j));
				}
			}
		}
		
		return good;
	}
	
	public void draw() {
		BPMer.draw();
	}

}
