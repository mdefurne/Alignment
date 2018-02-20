package TrackFinder;

import BMT_struct.*;
import java.util.*;
import TrackFinder.TrackCandidate;
import TrackFinder.Fitter;

public class TrackFinder {
	
	HashMap<Integer, TrackCandidate> Candidates;
	float time_match;
	int cand_newsec;
	
	public TrackFinder() {
		Candidates=new HashMap();
		time_match=40;
		cand_newsec=0;
	}
	
	public void setTimeMatch(float timing) {
		time_match=timing;
	}
	
	public void clear() {
		Candidates.clear();
	}
	
	public void BuildCandidates(Barrel BMT_det) {
		Tile tiles=new Tile();
		boolean IsAttributed=true;
		//We are looking for Straight Track
		//We analyze each sector separately 
		for (int sec=0;sec<3;sec++) {
			cand_newsec=Candidates.size(); //Avoid to mix the sectors between them
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
					for (int clus=0;clus<BMT_det.getTile(lay,sec).getClusters().size();clus++) {
						//Here we always test if we have a match by time
						IsAttributed=false;
						for (int num_cand=cand_newsec;num_cand<Candidates.size();num_cand++) {
							//If we have a match in time
							if (Math.abs(BMT_det.getTile(lay,sec).getClusters().get(clus+1).getT_min()-Candidates.get(num_cand+1).GetTimeLastHit())<time_match) {
								Candidates.get(num_cand+1).add(lay+1,sec+1,BMT_det.getTile(lay,sec).getClusters().get(clus+1));
								IsAttributed=true;
							}
						}
						if (!IsAttributed) {
							TrackCandidate cand=new TrackCandidate();
							cand.add(lay+1,sec+1,BMT_det.getTile(lay,sec).getClusters().get(clus+1));
							Candidates.put(Candidates.size()+1, cand);
						}
					}
				}	
			}	
		}
		System.out.println("Size of candidate vector "+Candidates.size());
	}
	
	public void FetchTrack() {
		Fitter myfit=new Fitter();
		myfit.StraightTrack(Candidates);
	}
	
}	
	