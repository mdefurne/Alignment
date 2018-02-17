package TrackFinder;

import BMT_struct.*;
import java.util.*;
import TrackFinder.TrackCandidate;

public class TrackFinder {
	
	HashMap<Integer, TrackCandidate> Candidates;
	float time_match;
	
	public TrackFinder(Barrel BMT) {
		Candidates=new HashMap();
		time_match=40;
	}
	
	public void setTimeMatch(float timing) {
		time_match=timing;
	}
	
	public void BuildCandidates(Barrel BMT_det) {
		Tile tiles=new Tile();
		
		//We are looking for Straight Track
		//We analyze each sector separately 
		for (int sec=0;sec<3;sec++) {
			for (int lay=0;lay<6;lay++) {
				
				if (lay==0) {
					//Create a new Track Candidate for each cluster of first layer
					for (int clus=0;clus<BMT_det.getTile(lay,sec).getClusters().size();clus++) {
						TrackCandidate cand=new TrackCandidate();
						cand.add(lay+1,sec+1,BMT_det.getTile(lay,sec).getClusters().get(clus+1));
						Candidates.put(Candidates.size()+1, cand);
					}
				}
				
				if (lay>0) {
					//Here we always test if we have a match by time
				}	
			}	
		}	
	}	
}	
	