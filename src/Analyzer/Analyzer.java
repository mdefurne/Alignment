package Analyzer;

import java.util.HashMap;
import BST_struct.*;
import BMT_struct.Barrel;
import TrackFinder.*;

public class Analyzer {
	TrackAna Trackmeter;
	BSTAna Simeter;
	
	public Analyzer() {
		Trackmeter=new TrackAna();
		Simeter=new BSTAna();
	}
	
	public void analyze(Barrel BMT, Barrel_SVT BST , HashMap<Integer,TrackCandidate> candidates) {
		for (int i=0;i<candidates.size();i++) {
			if (candidates.get(i+1).get_FitStatus()) {
				Trackmeter.analyze(candidates.get(i+1));
				Simeter.analyze(BST, candidates.get(i+1));
			}
		}
	}
	
	public void draw() {
		Trackmeter.draw();
	}
	
}
