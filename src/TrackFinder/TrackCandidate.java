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
	private int nz; //Number of "usable" Z hit for tracking
	private int nc; //Number of "usable" C hit for tracking
	private int nsvt; //Number of usable hit in SVT for tracking
	
	//Seeder Searcher
	//Indexes are going from outer CVT to inner CVT
	private double phi_seed;
	private double theta_seed;
	private double phi_tolerance;
	private double theta_tolerance;
	private double theta_min;
	private double theta_max;
	private double dT;
	private double dR;
	private final double radius_SVT_2=93;
	private final double start_SVT_2=-181.;
	private final double end_SVT_2=153.;
	private double[] par;
	private double[] errpar;
	
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
		TrackResidual=new ArrayList<Double>();
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
		nsvt=0;
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
		theta_tolerance=Math.toRadians(5);
		if (main.constant.isCosmic) theta_tolerance=Math.toRadians(25);
		
		theta_min=Math.toRadians(0);
		theta_max=Math.toRadians(180);
		
		dT=0.001;
		dR=0.00002;
		
		par=new double[4];
		errpar=new double[4];
		
		BMT=BMT_det;
		BST=BST_det;
		
		vertex=new Vector3D();
		vertex.setXYZ(Double.NaN, Double.NaN, Double.NaN);
		}
	
	public void set_errpar(double[] err) {
		for (int i=0;i<4;i++) {
			errpar[i]=err[i];
		}
	}
	
	public void set_par(double[] para) {
		for (int i=0;i<4;i++) {
			par[i]=para[i];
		}
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
	
	public void set_PhiSeed(double phi) {
		phi_seed=phi;
	}
	
	public void set_ThetaSeed(double theta) {
		theta_seed=theta;
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
				if (phi_seed<-Math.PI) phi_seed=phi_seed+2*Math.PI;
				if (phi_seed>Math.PI) phi_seed=phi_seed-2*Math.PI;
				Phi_track.add(phi_seed);
				this.setPhiTolerance(Math.toRadians(60));
			}
			if (nz>0) {
				phi_seed=Math.atan2(Y_hit.get(Y_hit.size()-1)-clus.getY(),X_hit.get(X_hit.size()-1)-clus.getX());
				if (phi_seed<-Math.PI) phi_seed=phi_seed+2*Math.PI;
				if (phi_seed>Math.PI) phi_seed=phi_seed-2*Math.PI;
				Phi_track.add(phi_seed);
				this.setPhiTolerance(Math.toRadians(5));
			}
			mean_X=(mean_X*nz+clus.getX())/((double)(nz+1));
			mean_Y=(mean_Y*nz+clus.getY())/((double)(nz+1));
			X_hit.add(clus.getX());
			Y_hit.add(clus.getY());
			if (clus.IsInFit()) nz++;
		}
		
		//If it is a C-layer
		if (clus.getLayer()==1||clus.getLayer()==4||clus.getLayer()==6) {
			if (nc==0) {
				theta_seed=Math.acos(clus.getZ()/Math.sqrt(clus.getZ()*clus.getZ()+clus.getRadius()*clus.getRadius()));
				Theta_track.add(theta_seed);
				if (!main.constant.isCosmic) {
					this.setThetaMin(Math.acos((clus.getZ()-start_SVT_2)/Math.sqrt((clus.getZ()-start_SVT_2)*(clus.getZ()-start_SVT_2)+(clus.getRadius()-radius_SVT_2)*(clus.getRadius()-radius_SVT_2))));
					this.setThetaMax(Math.acos((clus.getZ()-end_SVT_2)/Math.sqrt((clus.getZ()-end_SVT_2)*(clus.getZ()-end_SVT_2)+(clus.getRadius()-radius_SVT_2)*(clus.getRadius()-radius_SVT_2))));
				}
			}
			if (nc>0) {
				theta_seed=Math.acos((Z_hit.get(Z_hit.size()-1)-clus.getZ())/Math.sqrt((Z_hit.get(Z_hit.size()-1)-clus.getZ())*(Z_hit.get(Z_hit.size()-1)-clus.getZ())
						+(clus.getRadius()-R_hit.get(R_hit.size()-1))*(clus.getRadius()-R_hit.get(R_hit.size()-1))));
				Theta_track.add(theta_seed);
				this.setThetaMin(theta_seed-theta_tolerance);
				this.setThetaMax(theta_seed+theta_tolerance);
			}
			mean_Z=(mean_Z*nc+clus.getZ())/((double)(nc+1));
			mean_R=(mean_R*nc+clus.getRadius())/((double)(nc+1));
			R_hit.add(clus.getRadius());
			Z_hit.add(clus.getZ());
		
			if (clus.IsInFit()) nc++;
		}
		double Rphi=Math.sqrt(mean_X*mean_X+mean_Y*mean_Y);
		vec_track.setXYZ(Math.cos(phi_seed)*Math.sin(theta_seed), Math.sin(phi_seed)*Math.sin(theta_seed), Math.cos(theta_seed));
		if (theta_seed==Math.PI/2.) point_track.setXYZ(mean_X, mean_Y, mean_Z);
		if (theta_seed!=Math.PI/2.) point_track.setXYZ(mean_X, mean_Y, (Rphi-mean_R)/Math.tan(theta_seed)+mean_Z);
	}
	
	public void addBST(BST_struct.Cluster clus) {
		BSTClus.add(clus);
		if (clus.IsInFit()) nsvt++;
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
		if (!main.constant.isCosmic&&(nz<2||nc<2)&&main.constant.TrackerType.equals("MVT")) fit=false;
		if (main.constant.isCosmic&&main.constant.TrackerType.equals("MVT")&&(nz<3||nc<4)) fit=false;
		if ((nz==0||nc==0||(nsvt+nc+nz)<6||nsvt<2)&&main.constant.TrackerType.equals("CVT")) fit=false;
		if (main.constant.isCosmic&&main.constant.TrackerType.equals("CVT")&&nsvt<8&&(nz<2||nc<2)) fit=false;
		if (main.constant.TrackerType.equals("CVT")||main.constant.TrackerType.equals("MVT")) {
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
		}
		if (main.constant.TrackerType.equals("SVT")&&(nsvt<4||nz==0||nc==0)&&!main.constant.isCosmic) fit=false; 
		if (main.constant.TrackerType.equals("SVT")&&(nsvt<7||nz==0||nc==0)&&main.constant.isCosmic) fit=false; 
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
	
	public TrackCandidate DuplicateBST() {
		TrackCandidate temp=new TrackCandidate(BMT, BST);
		for (int dup=0;dup<this.size();dup++) {//Do not want the last cluster since on the same layer
			temp.addBMT(this.GetBMTCluster(dup));
		}
		for (int dup=0;dup<this.BSTsize()-1;dup++) {//Do not want the last cluster since on the same layer
			temp.addBST(this.GetBSTCluster(dup));
		}
		return temp;
	}
	
	public TrackCandidate DuplicateBMT() {
		TrackCandidate temp=new TrackCandidate(BMT, BST);
		for (int dup=0;dup<this.size()-1;dup++) {//Do not want the last cluster since on the same layer
			temp.addBMT(this.GetBMTCluster(dup));
			
		}
		for (int dup=0;dup<this.BSTsize();dup++) {//Do not want the last cluster since on the same layer
			temp.addBST(this.GetBSTCluster(dup));
		}
		return temp;
	}
	
	public TrackCandidate Merge(TrackCandidate ToMerge) {
		TrackCandidate temp=new TrackCandidate(BMT, BST);
		for (int dup=0;dup<this.size();dup++) {//Do not want the last cluster since on the same layer
			temp.addBMT(this.GetBMTCluster(dup));
			
		}
		for (int dup=0;dup<this.BSTsize();dup++) {//Do not want the last cluster since on the same layer
			temp.addBST(this.GetBSTCluster(dup));
		}
		
		for (int dup=0;dup<ToMerge.size();dup++) {//Do not want the last cluster since on the same layer
			temp.addBMT(ToMerge.GetBMTCluster(dup));
			
		}
		for (int dup=0;dup<ToMerge.BSTsize();dup++) {//Do not want the last cluster since on the same layer
			boolean checked=true;
			for (int tt=0;tt<this.BSTsize();tt++) {
				if (ToMerge.GetBSTCluster(dup).equals(this.GetBSTCluster(tt))) checked=false;
			}
			if (checked) temp.addBST(ToMerge.GetBSTCluster(dup));
		}
		// For cosmic, we might have duplicated BST	
		
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
	
	public double ComputeChi2(StraightLine line) {
		double chi=0;
		if (this.size()==0) return chi;
		 if (main.constant.TrackerType.equals("CVT")||main.constant.TrackerType.equals("MVT")) {
			 for (int clus=0;clus<this.size();clus++) {
			  if (this.GetBMTCluster(clus).IsInFit()) chi+=Math.pow(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line.getSlope(),line.getPoint())/this.GetBMTCluster(clus).getErr(),2);
	      	}
		 }
	      if (main.constant.TrackerType.equals("CVT")||main.constant.TrackerType.equals("SVT")) {
	    	  for (int clus=0;clus<this.BSTsize();clus++) {
	    		  Vector3D inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line.getSlope(), line.getPoint());
	    		  if (!Double.isNaN(inter.x())&&this.GetBSTCluster(clus).IsInFit()) chi+=Math.pow(BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)
	    				  /BST.getGeometry().getSingleStripResolution(this.GetBSTCluster(clus).getLayer(), (int)this.GetBSTCluster(clus).getCentroid(), inter.z()),2);
	    	  }
	      }
	     
	   return chi;
	}
	
	public boolean IsGoodCandidate() {
		boolean good=true;
		if (main.constant.TrackerType.equals("MVT")) {
			if ((chi2>100&&!main.constant.isCosmic)||((chi2>1000||nz<3||nc<4)&&main.constant.isCosmic)) good=false;
			if (nz<3) good=false;
			if (nc<3) good=false;
		}
		if (main.constant.TrackerType.equals("CVT")) {
			if ((chi2>500&&!main.constant.isCosmic)||(chi2>2000&&main.constant.isCosmic)) good=false;
			if (nz==0||nc==0||(BSTClus.size()+BMTClus.size())<6||BSTClus.size()<4) good=false;
		}
		if (main.constant.TrackerType.equals("SVT")) {
			if ((chi2>300&&!main.constant.isCosmic)||(chi2>1000&&main.constant.isCosmic)) good=false;
			if (BSTClus.size()<5) good=false;
		}
		if (!fit_status) good=false;
		return good;
	}
	
	public boolean IsVeryGoodCandidate() {
		boolean good=true;
		if (main.constant.TrackerType.equals("MVT")) {
			if (chi2>30) good=false;
			if (nz<3) good=false;
			if (nc<3) good=false;
		}
		if (main.constant.TrackerType.equals("CVT")) {
			if (chi2>100) good=false;
			if (nz<2||nc<2||(BSTClus.size()+BMTClus.size())<9||BSTClus.size()<4) good=false;
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
		
		if (InCommon>1) IsSame=true;
		
		return IsSame;
	}
	
	public void ComputeMillepedeDerivative() {
		StraightLine line_plus=new StraightLine();
				
		double[] h_deriv=new double[4];
		for (int i=0;i<4;i++) {
			h_deriv[i]=0.02*errpar[i]; //0.1 has been tuned so that derivative values are stable within 1% when decreasing h_deriv by a factor 10.
		}
		
		//First derivative wrt to phi for track direction
		line_plus.setSlope_XYZ(Math.cos(par[0]+h_deriv[0]/2.)*Math.sin(par[1]),Math.sin(par[0]+h_deriv[0]/2.)*Math.sin(par[1]),Math.cos(par[1]));
		line_plus.setPoint_XYZ(Constant.getPointRadius()*Math.cos(par[2]), Constant.getPointRadius()*Math.sin(par[2]), par[3]);
		for (int clus=0; clus<this.size();clus++) {
			this.GetBMTCluster(clus).setLocDerivative(0,(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())
					-this.GetBMTCluster(clus).getCentroidResidual())/h_deriv[0]);
		}
		for (int clus=0; clus<this.BSTsize();clus++) {
		Vector3D inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line_plus.getSlope(), line_plus.getPoint());
		if (!Double.isNaN(inter.x())&&this.GetBSTCluster(clus).IsInFit()) this.GetBSTCluster(clus).setLocDerivative(0,
  				  (BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)-this.GetBSTCluster(clus).getCentroidResidual())/h_deriv[0]);
		else  this.GetBSTCluster(clus).setLocDerivative(0,Double.NaN);
		}
		
		
		//Second derivative wrt to theta for track direction
		line_plus.setSlope_XYZ(Math.cos(par[0])*Math.sin(par[1]+h_deriv[1]/2.),Math.sin(par[0])*Math.sin(par[1]+h_deriv[1]/2.),Math.cos(par[1]+h_deriv[1]/2.));
		line_plus.setPoint_XYZ(Constant.getPointRadius()*Math.cos(par[2]), Constant.getPointRadius()*Math.sin(par[2]), par[3]);
		for (int clus=0; clus<this.size();clus++) {
			this.GetBMTCluster(clus).setLocDerivative(1,(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())
					-this.GetBMTCluster(clus).getCentroidResidual())/h_deriv[1]);
		}
		for (int clus=0; clus<this.BSTsize();clus++) {
		Vector3D inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line_plus.getSlope(), line_plus.getPoint());
		if (!Double.isNaN(inter.x())&&this.GetBSTCluster(clus).IsInFit()) this.GetBSTCluster(clus).setLocDerivative(1,
  				  (BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)-this.GetBSTCluster(clus).getCentroidResidual())
  				  /h_deriv[1]);
		else  this.GetBSTCluster(clus).setLocDerivative(1,Double.NaN);
		}
		
		//Third derivative wrt to phi point
		line_plus.setSlope_XYZ(Math.cos(par[0])*Math.sin(par[1]),Math.sin(par[0])*Math.sin(par[1]),Math.cos(par[1]));
		line_plus.setPoint_XYZ(Constant.getPointRadius()*Math.cos(par[2]+h_deriv[2]/2.), Constant.getPointRadius()*Math.sin(par[2]+h_deriv[2]/2.), par[3]);
		for (int clus=0; clus<this.size();clus++) {
			this.GetBMTCluster(clus).setLocDerivative(2,(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())
					-this.GetBMTCluster(clus).getCentroidResidual())/h_deriv[2]);
		}
		for (int clus=0; clus<this.BSTsize();clus++) {
		Vector3D inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line_plus.getSlope(), line_plus.getPoint());
		if (!Double.isNaN(inter.x())&&this.GetBSTCluster(clus).IsInFit()) this.GetBSTCluster(clus).setLocDerivative(2,
  				  (BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)-this.GetBSTCluster(clus).getCentroidResidual())
  				  /h_deriv[2]);
		else  this.GetBSTCluster(clus).setLocDerivative(2,Double.NaN); 
		}
		
		
		//Fourth derivative wrt to z point
		line_plus.setSlope_XYZ(Math.cos(par[0])*Math.sin(par[1]),Math.sin(par[0])*Math.sin(par[1]),Math.cos(par[1]));
		line_plus.setPoint_XYZ(Constant.getPointRadius()*Math.cos(par[2]), Constant.getPointRadius()*Math.sin(par[2]), par[3]+h_deriv[3]/2.);
		for (int clus=0; clus<this.size();clus++) {
			this.GetBMTCluster(clus).setLocDerivative(3,(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())
					-this.GetBMTCluster(clus).getCentroidResidual())/h_deriv[3]);
		}
		for (int clus=0; clus<this.BSTsize();clus++) {
		Vector3D inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line_plus.getSlope(), line_plus.getPoint());
		if (!Double.isNaN(inter.x())&&this.GetBSTCluster(clus).IsInFit()) this.GetBSTCluster(clus).setLocDerivative(3,
  				  (BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)-this.GetBSTCluster(clus).getCentroidResidual())
  				  /h_deriv[3]);
		else  this.GetBSTCluster(clus).setLocDerivative(3,Double.NaN);
		}
		
		//Computation of global derivatives
		line_plus.setSlope_XYZ(Math.cos(par[0])*Math.sin(par[1]),Math.sin(par[0])*Math.sin(par[1]),Math.cos(par[1]));
		line_plus.setPoint_XYZ(Constant.getPointRadius()*Math.cos(par[2]), Constant.getPointRadius()*Math.sin(par[2]), par[3]);
		for (int clus=0; clus<this.size();clus++) {
			//Rx
			BMT_geo.Constants.setRx(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),dR+BMT_geo.Constants.getRx(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector()));
			this.GetBMTCluster(clus).setGlobDerivative(0,(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())
					-this.GetBMTCluster(clus).getCentroidResidual())/dR);
			BMT_geo.Constants.setRx(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),BMT_geo.Constants.getRx(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector())-dR);
			
			//Ry
			BMT_geo.Constants.setRy(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),dR+BMT_geo.Constants.getRy(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector()));
			this.GetBMTCluster(clus).setGlobDerivative(1,(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())
					-this.GetBMTCluster(clus).getCentroidResidual())/dR);
			BMT_geo.Constants.setRy(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),BMT_geo.Constants.getRy(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector())-dR);
		
			//Rz
			BMT_geo.Constants.setRz(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),dR+BMT_geo.Constants.getRz(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector()));
			this.GetBMTCluster(clus).setGlobDerivative(2,(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())
					-this.GetBMTCluster(clus).getCentroidResidual())/dR);
			if (BMT.getGeometry().getZorC(this.GetBMTCluster(clus).getLayer())==0) this.GetBMTCluster(clus).setGlobDerivative(2,0); //System.out.println(this.GetBMTCluster(clus).getGlobDerivative()[1] +" "+this.GetBMTCluster(clus).getGlobDerivative()[2] +" "+BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())+" "+this.GetBMTCluster(clus).getCentroidResidual());
			BMT_geo.Constants.setRz(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),BMT_geo.Constants.getRz(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector())-dR);
			//Tx
			BMT_geo.Constants.setCx(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),dT+BMT_geo.Constants.getCx(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector()));
			this.GetBMTCluster(clus).setGlobDerivative(3,(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())
					-this.GetBMTCluster(clus).getCentroidResidual())/dT);
			BMT_geo.Constants.setCx(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),BMT_geo.Constants.getCx(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector())-dT);
			
			//Ty
			BMT_geo.Constants.setCy(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),dT+BMT_geo.Constants.getCy(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector()));
			this.GetBMTCluster(clus).setGlobDerivative(4,(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())
					-this.GetBMTCluster(clus).getCentroidResidual())/dT);
			BMT_geo.Constants.setCy(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),BMT_geo.Constants.getCy(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector())-dT);
		
			//Tz
			BMT_geo.Constants.setCz(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),dT+BMT_geo.Constants.getCz(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector()));
			this.GetBMTCluster(clus).setGlobDerivative(5,(BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint())
					-this.GetBMTCluster(clus).getCentroidResidual())/dT);
			//System.out.println(this.GetBMTCluster(clus).getCentroidResidual()+" "+BMT.getGeometry().getResidual_line(this.GetBMTCluster(clus),line_plus.getSlope(),line_plus.getPoint()));
			BMT_geo.Constants.setCz(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector(),BMT_geo.Constants.getCz(this.GetBMTCluster(clus).getLayer(),this.GetBMTCluster(clus).getSector())-dT);
		}
		
		for (int clus=0; clus<this.BSTsize();clus++) {
			//Rx
			BST.getGeometry().setRx(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),dR+BST.getGeometry().getRx(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector()));
			Vector3D inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line_plus.getSlope(), line_plus.getPoint());
			this.GetBSTCluster(clus).setGlobDerivative(0,(BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)
					-this.GetBSTCluster(clus).getCentroidResidual())/dR);
			//System.out.println("Rx : "+inter.x()+" "+inter.y()+" "+inter.z()+BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter));
			BST.getGeometry().setRx(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),BST.getGeometry().getRx(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector())-dR);
			
			
			//Ry
			BST.getGeometry().setRy(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),dR+BST.getGeometry().getRy(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector()));
			inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line_plus.getSlope(), line_plus.getPoint());
			this.GetBSTCluster(clus).setGlobDerivative(1,(BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)
					-this.GetBSTCluster(clus).getCentroidResidual())/dR);
			//System.out.println("Ry : "+inter.x()+" "+inter.y()+" "+inter.z()+BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter));
			BST.getGeometry().setRy(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),BST.getGeometry().getRy(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector())-dR);
			

			//Rz
			BST.getGeometry().setRz(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),dR+BST.getGeometry().getRz(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector()));
			inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line_plus.getSlope(), line_plus.getPoint());
			this.GetBSTCluster(clus).setGlobDerivative(2,(BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)
					-this.GetBSTCluster(clus).getCentroidResidual())/dR);
			BST.getGeometry().setRz(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),BST.getGeometry().getRz(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector())-dR);
						
			//Tx
			BST.getGeometry().setCx(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),dT+BST.getGeometry().getCx(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector()));
			inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line_plus.getSlope(), line_plus.getPoint());
			this.GetBSTCluster(clus).setGlobDerivative(3,(BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)
					-this.GetBSTCluster(clus).getCentroidResidual())/dT);
			BST.getGeometry().setCx(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),BST.getGeometry().getCx(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector())-dT);
			
			//Ty
			BST.getGeometry().setCy(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),dT+BST.getGeometry().getCy(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector()));
			inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line_plus.getSlope(), line_plus.getPoint());
			this.GetBSTCluster(clus).setGlobDerivative(4,(BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)
					-this.GetBSTCluster(clus).getCentroidResidual())/dT);
			BST.getGeometry().setCy(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),BST.getGeometry().getCy(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector())-dT);
		
			//Tz
			BST.getGeometry().setCz(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),dT+BST.getGeometry().getCz(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector()));
			inter=BST.getGeometry().getIntersectWithRay(this.GetBSTCluster(clus).getLayer(), this.GetBSTCluster(clus).getSector(), line_plus.getSlope(), line_plus.getPoint());
			this.GetBSTCluster(clus).setGlobDerivative(5,(BST.getGeometry().getResidual_line(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),this.GetBSTCluster(clus).getCentroid(),inter)
					-this.GetBSTCluster(clus).getCentroidResidual())/dT);
			BST.getGeometry().setCz(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector(),BST.getGeometry().getCz(this.GetBSTCluster(clus).getLayer(),this.GetBSTCluster(clus).getSector())-dT);
		}
	}
	
	public StraightLine getLine() {
		StraightLine line=new StraightLine();
		line.setSlope_XYZ(vec_track.x(), vec_track.y(), vec_track.z());
		line.setPoint_XYZ(point_track.x(), point_track.y(), point_track.z());
		return line;
	}
	
	public void Print() {
		System.out.println("//////////////////");
		for (int i=0;i<this.BSTsize();i++) {
			System.out.println(this.GetBSTCluster(i).getPhi());
		}
		for (int i=0;i<this.size();i++) {
			System.out.println(this.GetBMTCluster(i).getPhi()+" "+this.GetBMTCluster(i).getZ()+" "+this.GetBMTCluster(i).getT_min());
		}
	}

	public int get_Nsvt() {
		return nsvt;
	}

	public boolean IsGoodForMillepede() {
		boolean GoodToGo=true;
		if (main.constant.TrackerType.equals("CVT")) {
			if (main.constant.isCosmic&&(this.BSTsize()+this.size())<8) GoodToGo=false;
			if (!main.constant.isCosmic&&(this.BSTsize()+this.size())<8) GoodToGo=false;
		}
		
		if (main.constant.TrackerType.equals("SVT")||main.constant.TrackerType.equals("CVT")) {
			for (int svt=0; svt<this.BSTsize();svt++) {
				if (Math.abs(this.GetBSTCluster(svt).getTrackPhiAngle())>60) GoodToGo=false;
			}
		}
		
		if (main.constant.TrackerType.equals("MVT")&&!main.constant.isCosmic) {
			if (this.get_Nc()!=3||this.get_Nz()!=3) GoodToGo=false;
		}
		
		if (main.constant.isCosmic) {
			for (int mvt=0; mvt<this.size();mvt++) {
				if (Math.abs(this.GetBMTCluster(mvt).getTrackPhiAngle())>35&&BMT.getGeometry().getZorC(this.GetBMTCluster(mvt).getLayer())==1) GoodToGo=false;
				if (Math.abs(this.GetBMTCluster(mvt).getTrackThetaAngle())>35&&BMT.getGeometry().getZorC(this.GetBMTCluster(mvt).getLayer())==0) GoodToGo=false;
			}			
		}
		
		return GoodToGo;
	}
}
