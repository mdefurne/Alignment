package TrackFinder;

import BMT_struct.*;
import java.util.*;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;

public class TrackCandidate{
	private ArrayList<Cluster> TrackTest;
	private ArrayList<Double> TrackResidual;
	private float mean_time;
	private double mean_Phi;
	private double chi2;
	private boolean is_secondary_track;
	private boolean has_secondary_track;
	private ArrayList<Float> time_hit;
	private ArrayList<Integer> layer_hit;
	private ArrayList<Integer> sector_hit;
	private double mean_X;
	private double mean_Y;
	private double mean_Z;	
	private ArrayList<Double> X_hit;
	private ArrayList<Double> Y_hit;
	private ArrayList<Double> Z_hit;
	private ArrayList<Double> R_hit;
	private ArrayList<Double> Phi_track;
	private ArrayList<Double> Theta_track;
	private int cand_prim;
	private int nz;
	private int nc;
	
	//Seeder Searcher
	//Indexes are going from outer CVT to inner CVT
	private double phi_seed;
	private double theta_seed;
	private double phi_tolerance;
	private double theta_min;
	private double theta_max;
	private final double radius_SVT_2=93;
	private final double start_SVT_2=-181.;
	private final double end_SVT_2=153.;
	
	
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
		//X-Y only filled for Z layer
		X_hit=new ArrayList();
		Y_hit=new ArrayList();
		//R-Z only filled for C-layer
		Z_hit=new ArrayList();
		R_hit=new ArrayList();
		
		Theta_track=new ArrayList();
		Phi_track=new ArrayList();
		mean_Phi=0;
		nz=0;
		nc=0;
		mean_X=0;
		mean_Y=0;
		mean_Z=0;
				
		//Just to seed	
		fit_status=false;
		phi_seed=0;
		theta_seed=Math.PI/2.;
		vec_track=new Vector3D();
		vec_track.setXYZ(Math.cos(phi_seed)*Math.sin(theta_seed), Math.sin(phi_seed)*Math.sin(theta_seed), Math.cos(theta_seed));
		point_track=new Vector3D();
		point_track.setXYZ(Double.NaN, Double.NaN, Double.NaN);
		phi_tolerance=Math.toRadians(60);
		theta_min=Math.toRadians(0);
		theta_max=Math.toRadians(180);
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
		
		//If it is a Z-layer
		if (clus.getLayer()==2||clus.getLayer()==3||clus.getLayer()==5) {
			if (nz==0) {
				Phi_track.add(clus.getPhi());
				phi_seed=clus.getPhi();
				this.setPhiTolerance(Math.toRadians(60));
			}
			if (nz>0) {
				phi_seed=Math.atan2(Y_hit.get(Y_hit.size()-1)-clus.getY(),X_hit.get(X_hit.size()-1)-clus.getX());
				if (phi_seed<0) phi_seed=phi_seed+2*Math.PI;
				Phi_track.add(phi_seed);
				this.setPhiTolerance(Math.toRadians(5));
			}
			mean_X=(mean_X*nz+clus.getX())/((double)(nz+1));
			mean_Y=(mean_Y*nz+clus.getY())/((double)(nz+1));
			X_hit.add(clus.getX());
			Y_hit.add(clus.getY());
			nz++;
		}
		
		//If it is a C-layer
		if (clus.getLayer()==1||clus.getLayer()==4||clus.getLayer()==6) {
			if (nc==0) {
				theta_seed=Math.acos(clus.getZ()/Math.sqrt(clus.getZ()*clus.getZ()+clus.getRadius()*clus.getRadius()));
				Theta_track.add(theta_seed);
				vec_track.setXYZ(Math.cos(phi_seed)*Math.sin(theta_seed), Math.sin(phi_seed)*Math.sin(theta_seed), Math.cos(theta_seed));
				this.setThetaMin(Math.acos((clus.getZ()-start_SVT_2)/Math.sqrt((clus.getZ()-start_SVT_2)*(clus.getZ()-start_SVT_2)+(clus.getRadius()-radius_SVT_2)*(clus.getRadius()-radius_SVT_2))));
				this.setThetaMax(Math.acos((clus.getZ()-start_SVT_2)/Math.sqrt((clus.getZ()-end_SVT_2)*(clus.getZ()-end_SVT_2)+(clus.getRadius()-radius_SVT_2)*(clus.getRadius()-radius_SVT_2))));
			}
			if (nc>0) {
				theta_seed=Math.acos((Z_hit.get(Z_hit.size()-1)-clus.getZ())/Math.sqrt((Z_hit.get(Z_hit.size()-1)-clus.getZ())*(Z_hit.get(Z_hit.size()-1)-clus.getZ())
						+(clus.getRadius()-R_hit.get(R_hit.size()-1))*(clus.getRadius()-R_hit.get(R_hit.size()-1))));
				Theta_track.add(theta_seed);
				this.setThetaMin(theta_seed-Math.toRadians(10));
				this.setThetaMax(theta_seed+Math.toRadians(10));
				vec_track.setXYZ(Math.cos(phi_seed)*Math.sin(theta_seed), Math.sin(phi_seed)*Math.sin(theta_seed), Math.cos(theta_seed));
			}
			mean_Z=(mean_Z*nc+clus.getZ())/((double)(nc+1));
			R_hit.add(clus.getRadius());
			Z_hit.add(clus.getZ());
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
	
	public boolean IsFittable() {
		boolean fit=true;
		if (nz<2||nc<2) fit=false;
		for (int i=0;i<TrackTest.size();i++) {
			double sx=Math.cos(phi_seed); double sy=Math.sin(phi_seed); 
			double ix=mean_X; double iy=mean_Y;
				  
			//Find the intersection
			double a=sx*sx+sy*sy;
			double b=2*(sx*ix+sy*iy);
			double c=ix*ix+iy*iy-TrackTest.get(i).getRadius()*TrackTest.get(i).getRadius();
				 
			double delta=b*b-4*a*c;
			if (delta<0) fit=false;
		}
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
	
	public double getPhiSeed() {
		return phi_seed;
	}
	
	public double getThetaSeed() {
		return theta_seed;
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
	
	public double getXMean() {
		return mean_X;
	}
	
	public double getYMean() {
		return mean_Y;
	}
	
	public double getZMean() {
		return mean_Z;
	}
	
	public double getLastX() {
		return X_hit.get(X_hit.size()-1);
	}
	
	public double getLastY() {
		return Y_hit.get(Y_hit.size()-1);
	}
	
	public double getLastZ() {
		return Z_hit.get(Z_hit.size()-1);
	}
	
	public double getLastR() {
		return R_hit.get(R_hit.size()-1);
	}
	
	public void setPhiTolerance(double ang) {
		phi_tolerance=ang;
	}
	
	public double getPhiTolerance() {
		return phi_tolerance;
	}
	
	public void setThetaMin(double ang) {
		theta_min=ang;
	}
	
	public double getThetaMin() {
		return theta_min;
	}
	
	public void setThetaMax(double ang) {
		theta_max=ang;
	}
	
	public double getThetaMax() {
		return theta_max;
	}
	
	public boolean IsGoodCandidate() {
		boolean good=true;
		if (chi2>50) good=false;
		if (nz<2) good=false;
		if (nc<2) good=false;
		if (!fit_status) good=false;
		return good;
	}
	
	public boolean IsVeryGoodCandidate() {
		boolean good=true;
		if (chi2>10) good=false;
		if (nz<3) good=false;
		if (nc<3) good=false;
		if (!fit_status) good=false;
		return good;
	}

}
