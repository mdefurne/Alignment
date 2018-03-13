package Trajectory;

import org.freehep.math.minuit.FCNBase;
import BMT_struct.Cluster;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;


public class StraightLine {
	Vector3D slope;
	Vector3D point;
	boolean Point_in_Y; //Look at phi mean of the track candidate to determine which plan we should choose for the plan (x=0 or y=0)
	double Phi; //atan2(sy,sx);
	double Theta; //ACos(s_perp/s_tot)
		
	public StraightLine() {
		slope=new Vector3D();
		point=new Vector3D();
		Point_in_Y=false;
		Phi=0;
		Theta=0;
		slope.setXYZ(0, 0, 0);
		point.setXYZ(0, 0, 0);
	}
	
	public boolean IsPoint_in_Y() {
		return Point_in_Y;
	}
	
	
	public void setPhi(double phi) {
		slope.setX(Math.cos(phi)*Math.sin(Theta));
		slope.setY(Math.sin(phi)*Math.sin(Theta));
		slope.setZ(Math.cos(Theta));
		Phi=phi;
	}
	
	public void setTheta(double theta) {
		slope.setX(Math.cos(Phi)*Math.sin(theta));
		slope.setY(Math.sin(Phi)*Math.sin(theta));
		slope.setZ(Math.cos(theta));
		Theta=theta;
	}
	
	public void setPointLocation(double phi_mean) {
		if (phi_mean<0) phi_mean=phi_mean+2*Math.PI;
		if ((phi_mean>0&&phi_mean<Math.PI/4.)||(phi_mean>3*Math.PI/4.&&phi_mean<5*Math.PI/4.)||(phi_mean>7*Math.PI/4.&&phi_mean<2*Math.PI)) Point_in_Y=true;
	}
	
	public void setPoint_XYZ(double dx, double dy, double dz) {
		point.setXYZ(dx, dy, dz);
	}
	
	public void setPoint_X(double dx) {
		point.setX(dx);
	}
	
	public void setPoint_Y(double dy) {
		point.setY(dy);
	}
	
	public void setPoint_Z(double dz) {
		point.setZ(dz);
	}
		
	public Vector3D getPointOnTrack(double lambda) {
		Vector3D NewPoint=new Vector3D();
		NewPoint.setXYZ(slope.x()*lambda+point.x(),slope.y()*lambda+point.y() , slope.z()*lambda+point.z());
		return NewPoint;
	}
	
	public Vector3D getPoint() {
		return point;
	}
	
	public Vector3D getSlope() {
		return slope;
	}
	
	
}
