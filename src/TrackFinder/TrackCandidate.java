package TrackFinder;

import BMT_struct.*;
import java.util.*;

public class TrackCandidate{
	ArrayList<Cluster> TrackTest;
	float mean_time=0;
	double mean_Theta=0;
	double mean_phi;
	double chi2;
	double ndf;
	boolean is_secondary_track;
	boolean has_secondary_track;
	int cand_prim;
	
	public TrackCandidate(){
		TrackTest=new ArrayList();
		mean_time=0;
		cand_prim=-1;
		is_secondary_track=false;
		has_secondary_track=false;
		ndf=0;
		chi2=0;
		mean_phi=0;
	}
	
	public void add(Cluster clus) {
		TrackTest.add(clus);
	}
	
	public void clear() {
		TrackTest.clear();
	}
	
	public int size() {
		return TrackTest.size();
	}
	
	public void Analyze() {
		
	}
	
	public void IsSecondary() {
		is_secondary_track=true;
	}
	
	public void HasSecondary() {
		has_secondary_track=true;
	}

}
