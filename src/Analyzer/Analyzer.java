package Analyzer;

import java.util.HashMap;

import BMT_struct.Barrel;
import TrackFinder.*;

public class Analyzer {
	TrackAna Trackmeter;
	
	public Analyzer() {
		Trackmeter=new TrackAna();
	}
	
	public void analyze(Barrel BMT, HashMap<Integer, TrackCandidate> candidates) {
		for (int i=0;i<candidates.size();i++) {
			if (candidates.get(i+1).get_FitStatus()) Trackmeter.analyze(candidates.get(i+1));
		}
	}
	
	public void draw() {
		Trackmeter.draw();
	}
	
}
