package TrackFinder;

import BMT_struct.*;
import java.util.*;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;

public class TrackCandidate{
	private ArrayList<Cluster> TrackTest;
	private ArrayList<Double> TrackResidual;
	private float mean_time;
	private double mean_X;
	private double mean_Y;
	private double mean_Z;
	private double mean_Phi;
	private double last_Phi;
	private double last_Z;
	private double chi2;
	private boolean is_secondary_track;
	private boolean has_secondary_track;
	private ArrayList<Float> time_hit;
	private ArrayList<Integer> layer_hit;
	private ArrayList<Integer> sector_hit;
	private int cand_prim;
	private int nz;
	private int nc;
	
	//Fit results
	private boolean fit_status;
	private Vector3D vec_track;
	private Vector3D point_track;
	
	public TrackCandidate(){
		TrackTest=new ArrayList();
		TrackResidual=new ArrayList();
		mean_time=0;
		cand_prim=-1;
		is_secondary_track=false;
		has_secondary_track=false;
		chi2=Double.NaN;
		time_hit=new ArrayList();
		layer_hit=new ArrayList();
		sector_hit=new ArrayList();
		mean_X=0;
		mean_Phi=0;
		mean_Y=0;
		mean_Z=0;
		nz=0;
		nc=0;
		last_Phi=Double.NaN;
		last_Z=Double.NaN;
		
		fit_status=false;
		vec_track=new Vector3D();
		vec_track.setXYZ(Double.NaN, Double.NaN, Double.NaN);
		point_track=new Vector3D();
		point_track.setXYZ(Double.NaN, Double.NaN, Double.NaN);
	}
	
	public void set_FitStatus(boolean status) {
		fit_status=status;
	}
	
	public boolean get_FitStatus() {
		return fit_status;
	}
	
	public void set_VectorTrack(Vector3D vec) {
		vec_track=vec;
	}
	
	public void set_PointTrack(Vector3D point) {
		point_track=point;
	}
	
	public Vector3D get_VectorTrack() {
		return vec_track;
	}
	
	public Vector3D get_PointTrack() {
		return point_track;
	}
	
	public void add(Cluster clus) {
		layer_hit.add(clus.getLayer());
		sector_hit.add(clus.getSector());
		TrackTest.add(clus);
		mean_time=(mean_time*(TrackTest.size()-1)+clus.getT_min())/((float)TrackTest.size());
		time_hit.add(clus.getT_min());
		if (clus.getLayer()==2||clus.getLayer()==3||clus.getLayer()==5) {
			mean_X=(mean_X*nz+clus.getX())/((double)(nz+1));
			mean_Y=(mean_Y*nz+clus.getY())/((double)(nz+1));
			mean_Phi=(mean_Phi*nz+clus.getPhi())/((double)(nz+1));
			last_Phi=clus.getPhi();
			nz++;
		}
		if (clus.getLayer()==1||clus.getLayer()==4||clus.getLayer()==6) {
			mean_Z=(mean_Z*nc+clus.getZ())/((double)(nc+1));
			last_Z=clus.getZ();
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
	
	public int GetLayerLast_Z_Hit() {
		int layer=0;
		for (int lay=0;lay<layer_hit.size();lay++) {
		  if ((layer_hit.get(lay)==2||layer_hit.get(lay)==3||layer_hit.get(lay)==5)&&layer<layer_hit.get(lay)) layer=layer_hit.get(lay);				
		}
		return layer;
	}
	
	public int GetLayerLast_C_Hit() {
		int layer=0;
		for (int lay=0;lay<layer_hit.size();lay++) {
			  if ((layer_hit.get(lay)==1||layer_hit.get(lay)==4||layer_hit.get(lay)==6)&&layer<layer_hit.get(lay)) layer=layer_hit.get(lay);				
			}	
		return layer;
	}
	
	
	public int GetSectorLastHit() {
		int sector=0;
		if (sector_hit.size()!=0) sector=sector_hit.get(sector_hit.size()-1);
		return sector;
	}
	
	public double GetLastPhi() {
		return last_Phi;
	}
	
	public double GetLastZ() {
		return last_Z;
	}
	
	public boolean IsFittable() {
		boolean fit=false;
		if (nz>=2&&nc>=2) fit=true;
		return fit;
	}
	
	public Cluster GetCluster(int i) {
		return TrackTest.get(i);
	}
	
	public double getResidual(int i) {
		return TrackResidual.get(i);
	}
	
	public void AddResidual(double res) {
		TrackResidual.add(res);
	}
	
	public TrackCandidate Duplicate() {
		TrackCandidate temp=new TrackCandidate();
		for (int dup=0;dup<this.size()-1;dup++) {//Do not want the last cluster since on the same layer
			temp.add(this.GetCluster(dup));
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
	
	public int get_Nz() {
		return nz;
	}
	
	public int get_Nc() {
		return nc;
	}
	
	public void set_chi2(double chi) {
		chi2=chi;
	}
	
	public double get_chi2() {
		return chi2;
	}

}
