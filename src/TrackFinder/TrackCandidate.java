package TrackFinder;

import BMT_struct.*;
import BST_struct.*;
import java.util.*;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;
import Trajectory.StraightLine;

public class TrackCandidate{
	private ArrayList<BMT_struct.Cluster> BMTClus;
	private ArrayList<BST_struct.Cluster> BSTClus;
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
	private double mean_R;	
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
	
	private Vector3D vertex;
	
	//Fit results
	private boolean fit_status;
	private Vector3D vec_track;
	private Vector3D point_track;
	
	//Need to access the geometry of both detectors for the track candidate
	Barrel BMT;
	Barrel_SVT BST;
		
	public TrackCandidate(Barrel BMT_det, Barrel_SVT BST_det){
		BMTClus=new ArrayList<BMT_struct.Cluster>();
		BSTClus=new ArrayList<BST_struct.Cluster>();
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
		mean_R=0;
				
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
		
		BMT=BMT_det;
		BST=BST_det;
		
		vertex=new Vector3D();
		vertex.setXYZ(Double.NaN, Double.NaN, Double.NaN);
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
	
	public void addBMT(BMT_struct.Cluster clus) {
		layer_hit.add(clus.getLayer());
		sector_hit.add(clus.getSector());
		BMTClus.add(clus);
		mean_time=(mean_time*(BMTClus.size()-1)+clus.getT_min())/((float)BMTClus.size());
		time_hit.add(clus.getT_min());
		
		//If it is a Z-layer
		if (clus.getLayer()==2||clus.getLayer()==3||clus.getLayer()==5) {
			if (nz==0) {
				phi_seed=clus.getPhi();
				if (phi_seed<0) phi_seed=phi_seed+2*Math.PI;
				Phi_track.add(phi_seed);
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
				this.setThetaMax(Math.acos((clus.getZ()-end_SVT_2)/Math.sqrt((clus.getZ()-end_SVT_2)*(clus.getZ()-end_SVT_2)+(clus.getRadius()-radius_SVT_2)*(clus.getRadius()-radius_SVT_2))));
				
			}
			if (nc>0) {
				theta_seed=Math.acos((Z_hit.get(Z_hit.size()-1)-clus.getZ())/Math.sqrt((Z_hit.get(Z_hit.size()-1)-clus.getZ())*(Z_hit.get(Z_hit.size()-1)-clus.getZ())
						+(clus.getRadius()-R_hit.get(R_hit.size()-1))*(clus.getRadius()-R_hit.get(R_hit.size()-1))));
				Theta_track.add(theta_seed);
				this.setThetaMin(theta_seed-Math.toRadians(5));
				this.setThetaMax(theta_seed+Math.toRadians(5));
				vec_track.setXYZ(Math.cos(phi_seed)*Math.sin(theta_seed), Math.sin(phi_seed)*Math.sin(theta_seed), Math.cos(theta_seed));
			}
			mean_Z=(mean_Z*nc+clus.getZ())/((double)(nc+1));
			mean_R=(mean_R*nc+clus.getRadius())/((double)(nc+1));
			R_hit.add(clus.getRadius());
			Z_hit.add(clus.getZ());
		
			nc++;
		}
		double Rphi=Math.sqrt(mean_X*mean_X+mean_Y*mean_Y);
		
		if (theta_seed==Math.PI/2.) point_track.setXYZ(mean_X, mean_Y, mean_Z);
		if (theta_seed!=Math.PI/2.) point_track.setXYZ(mean_X, mean_Y, (Rphi-mean_R)/Math.tan(theta_seed)+mean_Z);
	}
	
	public void addBST(BST_struct.Cluster clus) {
		BSTClus.add(clus);
	}
	
	public void clear() {
		BMTClus.clear();
	}
	
	public int size() {
		return BMTClus.size();
	}
	
	public int BSTsize() {
		return BSTClus.size();
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
	
	//Is fittable... one of the most important method... Avoid to give crap to JMinuit
	public boolean IsFittable() {
		boolean fit=true;
		if ((nz<2||nc<2)&&!main.constant.IsWithSVT()) fit=false;
		if ((nz==0||nc==0||BSTClus.size()<4)&&main.constant.IsWithSVT()) fit=false;
		for (int i=0;i<BMTClus.size();i++) {
			double sx=Math.cos(phi_seed); double sy=Math.sin(phi_seed); 
			double ix=mean_X; double iy=mean_Y;
				  
			//Find the intersection
			double a=sx*sx+sy*sy;
			double b=2*(sx*ix+sy*iy);
			double c=ix*ix+iy*iy-BMTClus.get(i).getRadius()*BMTClus.get(i).getRadius();
				 
			double delta=b*b-4*a*c;
			if (delta<0) fit=false;
			if (delta==0) {
			    double lambda=-b/2./a;
			   Vector3D inter=new Vector3D(sx*lambda+ix,sy*lambda+iy,0);
			    if (BMT.getGeometry().isinsector(inter)!=BMTClus.get(i).getSector()) fit=false;
			}
			if (delta>0) {
				double lambda_a=(-b+Math.sqrt(delta))/2./a;
			    double lambda_b=(-b-Math.sqrt(delta))/2./a;
			    Vector3D inter_a=new Vector3D(sx*lambda_a+ix,sy*lambda_a+iy,0);
			    Vector3D inter_b=new Vector3D(sx*lambda_b+ix,sy*lambda_b+iy,0);
				if (BMT.getGeometry().isinsector(inter_a)!=BMTClus.get(i).getSector()&&BMT.getGeometry().isinsector(inter_b)!=BMTClus.get(i).getSector()) fit=false;
			}
		}
		return fit;
	}
	
	public BMT_struct.Cluster GetBMTCluster(int i) {
		return BMTClus.get(i);
	}
	
	public BST_struct.Cluster GetBSTCluster(int i) {
		return BSTClus.get(i);
	}
	
	public double getResidual(int i) {
		return TrackResidual.get(i);
	}
	
	public void AddResidual(double res) {
		TrackResidual.add(res);
	}
	
	public TrackCandidate Duplicate() {
		TrackCandidate temp=new TrackCandidate(BMT, BST);
		for (int dup=0;dup<this.size();dup++) {//Do not want the last cluster since on the same layer
			temp.addBMT(this.GetBMTCluster(dup));
		}
		for (int dup=0;dup<this.BSTsize()-1;dup++) {//Do not want the last cluster since on the same layer
			temp.addBST(this.GetBSTCluster(dup));
		}
		return temp;
	}
	
	public double getPhiSeed() {
		return phi_seed;
	}
	
	public double getThetaSeed() {
		return theta_seed;
	}
	
	public double getPhi() {
		return Math.atan2(vec_track.y(), vec_track.x());
	}
	
	public double getTheta() {
		return Math.acos(vec_track.z());
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
	
	public double getRMean() {
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
		if (!main.constant.IsWithSVT()) {
			if (chi2>50) good=false;
			if (nz<2) good=false;
			if (nc<2) good=false;
		}
		if (main.constant.IsWithSVT()) {
			if (chi2>200) good=false;
			if (nz==0||nc==0||BSTClus.size()<4) good=false;
		}
		if (!fit_status) good=false;
		return good;
	}
	
	public boolean IsVeryGoodCandidate() {
		boolean good=true;
		if (!main.constant.IsWithSVT()) {
			if (chi2>10) good=false;
			if (nz<3) good=false;
			if (nc<3) good=false;
		}
		if (main.constant.IsWithSVT()) {
			if (chi2>100) good=false;
			if (nz>=2&&nc>=2&&BSTClus.size()>=4) good=false;
		}
		if (!fit_status) good=false;
		return good;
	}
	
	public boolean IsFromTarget() {
		boolean FromTarget=false;
		StraightLine track=new StraightLine();
		track.setPoint_XYZ(point_track.x(),point_track.y() , point_track.z());
		track.setSlope_XYZ(vec_track.x(),vec_track.y() , vec_track.z());
		if (main.constant.IdealBeam.getDistanceToLine(track)<12&&Math.abs(main.constant.IdealBeam.getClosestPointToLine(track).z())<80) FromTarget=true;

		return FromTarget;
	}
	
	public void setVertex(Vector3D ver) {
		vertex=ver;
	}
	
	public Vector3D getVertex() {
		return vertex;
	}
	
	public int getLastBSTLayer() {
		return BSTClus.get(BSTClus.size()-1).getLayer();
	}
	
	public boolean IsSimilar(TrackCandidate ToTest) {
		boolean IsSame=false;
		int InCommon=0;
		for (int clus=0; clus<ToTest.size(); clus++) {
			for (int clusb=0; clusb<this.size(); clusb++) {
			if (ToTest.GetBMTCluster(clus).getCentroid()==this.GetBMTCluster(clusb).getCentroid()&&ToTest.GetBMTCluster(clus).getLayer()==this.GetBMTCluster(clusb).getLayer()&&ToTest.GetBMTCluster(clus).getSector()==this.GetBMTCluster(clusb).getSector()) InCommon++;
			}
		}
		
		for (int clus=0; clus<ToTest.BSTsize(); clus++) {
			for (int clusb=0; clusb<this.BSTsize(); clusb++) {
			if (ToTest.GetBSTCluster(clus).getCentroid()==this.GetBSTCluster(clusb).getCentroid()&&ToTest.GetBSTCluster(clus).getLayer()==this.GetBSTCluster(clusb).getLayer()&&ToTest.GetBSTCluster(clus).getSector()==this.GetBSTCluster(clusb).getSector()) InCommon++;
			}
		}
		
		if (InCommon>2) IsSame=true;
		
		return IsSame;
	}
}
