package Analyzer;

import org.jlab.groot.data.*;
import TrackFinder.*;
import org.jlab.groot.ui.TCanvas;

public class TrackAna {
	H1F Theta_track;
	H1F Phi_track;
	
	public TrackAna() {
		Theta_track=new H1F("Theta angle of track","Theta angle for track",45,0,180);
		Phi_track=new H1F("Phi angle of track","Phi angle for track",45,0,180);
	}
	
	public void analyze(TrackCandidate cand) {
		if (cand.IsFittable()) {
			System.out.println(cand.get_VectorTrack().z());
			Theta_track.fill(Math.toDegrees(Math.acos(cand.get_VectorTrack().z())));
			Phi_track.fill(Math.toDegrees(Math.atan2(cand.get_VectorTrack().y(),cand.get_VectorTrack().x())));
		}
	}
	
	public void draw() {
		 TCanvas theta = new TCanvas("theta", 1100, 700);
		 theta.draw(Theta_track);
		 TCanvas phi = new TCanvas("phi", 1100, 700);
		 phi.draw(Phi_track);
	}
}
