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
		Vector3D point_inter;
		point_inter.setXYZ(0,0,0);
		
		if (!Double.isNaN(clus.getX())&&!Double.isNaN(clus.getY())&&Double.isNaN(clus.getZ())) {
			//In that case, we just compute the distance between the line and the point in xy-plane
			//First case: is the slope in x and y are both nul, the line becomes then a point.
			if (slope.x()==0&&slope.y()==0) distance=Math.sqrt(Math.pow(clus.getX()-point.x(), 2)+Math.pow(clus.getY()-point.y(), 2));
			else {
				
			}
		}
		
		//For C-detector, it a bit more complicated... You need to find the intersection between the cylinder and the line, which involves x and y component
		if (Double.isNaN(clus.getX())&&Double.isNaN(clus.getY())&&!Double.isNaN(clus.getZ())) {
			  
		double sx=slope.x(); double sy=slope.y(); 
		double ix=point.x(); double iy=point.y();
			  
		//Find the intersection
		double a=sx*sx+sy*sy;
		double b=2*(sx*ix+sy*iy);
		double c=ix*ix+iy*iy-clus.getRadius()*clus.getRadius();
			 
		double delta=b*b-4*a*c;
		if (delta<0) point_inter.setXYZ(0,0,0);
		if (delta==0) {
		    double lambda=-b/2./a;
		    if (this.IsInTile(line->GetPoint(lambda),layer,sector)) point_inter=line->GetPoint(lambda);
		  }
		  if (delta>0) {
		    double lambda_a=(-b+Math.sqrt(delta))/2./a;
		    double lambda_b=(-b-Math.sqrt(delta))/2./a;
		    if (this.IsInTile(line->GetPoint(lambda_a),layer,sector)) point_inter=line->GetPoint(lambda_a);
		    if (this.IsInTile(line->GetPoint(lambda_b),layer,sector)) point_inter=line->GetPoint(lambda_b);
		  }
		  
		}
		return distance;
	}

}
