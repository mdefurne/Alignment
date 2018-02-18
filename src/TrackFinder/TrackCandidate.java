package TrackFinder;

import BMT_struct.*;
import java.util.*;

public class TrackCandidate{
	ArrayList<Cluster> TrackTest;
	float mean_time;
	double mean_Theta;
	double mean_Phi;
	double chi2;
	double Err;
	boolean is_secondary_track;
	boolean has_secondary_track;
	ArrayList<Float> time_hit;
	ArrayList<Integer> layer_hit;
	ArrayList<Integer> sector_hit;
	int cand_prim;
	int nz;
	int nc;
	
	public TrackCandidate(){
		TrackTest=new ArrayList();
		mean_time=0;
		cand_prim=-1;
		is_secondary_track=false;
		has_secondary_track=false;
		chi2=0;
		time_hit=new ArrayList();
		mean_Theta=0;
		mean_Phi=0;
		nz=0;
		nc=0;
		Err=0.1;
	}
	
	public void add(int layer, int sector, Cluster clus) {
		layer_hit.add(layer);
		sector_hit.add(sector);
		TrackTest.add(clus);
		mean_time+=(mean_time*(TrackTest.size()-1)+clus.getT_min())/((double)TrackTest.size());
		mean_Phi+=(mean_Phi*(TrackTest.size()-1)+clus.getPhi())/((double)TrackTest.size());
		time_hit.add(clus.getT_min());
		if (layer==2||layer==3||layer==5) nz++;
		if (layer==1||layer==4||layer==6) nc++;
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
	
	public float GetTimeLastHit() {
		float time=0;
		if (time_hit.size()!=0) time=time_hit.get(time_hit.size()-1);
		return time;
	}
	
	public int GetLayerLastHit() {
		int layer=0;
		if (layer_hit.size()!=0) layer=layer_hit.get(layer_hit.size()-1);
		return layer;
	}
	
	public boolean IsFittable() {
		boolean fit=false;
		if (nz>=2&&nc>=2) fit=true;
		return fit;
	}
	
	public double GetErr() {
		return Err;
	}

}
