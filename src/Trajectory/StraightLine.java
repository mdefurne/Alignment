package Trajectory;

import org.freehep.math.minuit.FCNBase;
import BMT_struct.Cluster;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;


public class StraightLine {
	Vector3D slope;
	Vector3D point;
	
	public StraightLine() {
		slope.setXYZ(0, 0, 0);
		point.setXYZ(0, 0, 0);
	}
	
	public void setSlope_XYZ(double dx, double dy, double dz) {
		slope.setXYZ(dx, dy, dz);
	}
	
	public void setSlope_X(double dx) {
		slope.setX(dx);
	}
	
	public void setSlope_Y(double dy) {
		slope.setY(dy);
	}
	
	public void setSlope_Z(double dz) {
		slope.setZ(dz);
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
	
	
	
	public double getDistance(Cluster clus) {
		double distance=0;
		
		return distance;
	}

}
