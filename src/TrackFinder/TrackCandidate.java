package TrackFinder;

import BMT_struct.*;
import java.util.*;

public class TrackCandidate{
	ArrayList<Cluster> TrackTest;
	float mean_time;
	double mean_X;
	double mean_Y;
	double mean_Z;
	double mean_Phi;
	double chi2;
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
		layer_hit=new ArrayList();
		sector_hit=new ArrayList();
		mean_X=0;
		mean_Phi=0;
		mean_Y=0;
		mean_Z=0;
		nz=0;
		nc=0;
	}
	
	public void add(int layer, int sector, Cluster clus) {
		layer_hit.add(layer);
		sector_hit.add(sector);
		TrackTest.add(clus);
		mean_time=(mean_time*(TrackTest.size()-1)+clus.getT_min())/((float)TrackTest.size());
		time_hit.add(clus.getT_min());
		if (layer==2||layer==3||layer==5) {
			mean_X=(mean_X*nz+clus.getX())/((double)(nz+1));
			mean_Y=(mean_Y*nz+clus.getY())/((double)(nz+1));
			mean_Phi=(mean_Phi*nz+clus.getPhi())/((double)(nz+1));
			nz++;
		}
		if (layer==1||layer==4||layer==6) {
			mean_Z=(mean_Z*nc+clus.getZ())/((double)(nc+1));
			nc++;
		}
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
	
	public int GetSectorLastHit() {
		int sector=0;
		if (sector_hit.size()!=0) sector=sector_hit.get(sector_hit.size()-1);
		return sector;
	}
	public boolean IsFittable() {
		boolean fit=false;
		if (nz>=2&&nc>=2) fit=true;
		return fit;
	}
	
	public Cluster GetCluster(int i) {
		return TrackTest.get(i);
	}
	
	public TrackCandidate Duplicate() {
		TrackCandidate temp=new TrackCandidate();
		for (int dup=0;dup<this.size()-1;dup++) {//Do not want the last cluster since on the same layer
			temp.add(this.GetCluster(dup).getLayer(),this.GetCluster(dup).getSector(),this.GetCluster(dup));
		}
		return temp;
	}
	
	public double getPhiMean() {
		return mean_Phi;
	}
	
	public double getXMean() {
		return mean_X;
	}
	
	public double getYMean() {
		return mean_Y;
	}
	
	public double getZMean() {
		return mean_Z;
	}

}
