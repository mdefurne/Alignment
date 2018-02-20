package Trajectory;

import org.freehep.math.minuit.FCNBase;
import BMT_struct.Cluster;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;


public class StraightLine {
	Vector3D slope;
	Vector3D point;
	boolean isCosmic;
	double Phi; //atan2(sy,sx);
	double Theta; //ACos(s_perp/s_tot)
	
	public StraightLine() {
		slope=new Vector3D();
		point=new Vector3D();
		isCosmic=false;
		Phi=0;
		Theta=0;
		slope.setXYZ(0, 0, 0);
		point.setXYZ(0, 0, 0);
	}
	
	public boolean IsCosmic() {
		return isCosmic;
	}
	
	public void setSlope_XYZ(double dx, double dy, double dz) {
		slope.setXYZ(dx, dy, dz);
	}
	
	public void setSlope_X(double dx) {
		slope.setX(dx);
		this.setPhi(Math.atan2(slope.y(), slope.x()));
		this.setTheta(Math.acos(slope.z()/slope.mag()));
	}
	
	public void setSlope_Y(double dy) {
		slope.setY(dy);
		this.setPhi(Math.atan2(slope.y(), slope.x()));
		this.setTheta(Math.acos(slope.z()/slope.mag()));
	}
	
	public void setSlope_Z(double dz) {
		slope.setZ(dz);
		this.setTheta(Math.acos(slope.z()/slope.mag()));
	}
	
	public void setPhi(double phi) {
		double s_perp=Math.sqrt(slope.x()*slope.x()+slope.y()*slope.y());
		slope.setX(s_perp*Math.cos(phi));
		slope.setY(s_perp*Math.sin(phi));
		Phi=phi;
	}
	
	public void setTheta(double theta) {
		if (slope.x()==0&&slope.y()==0) {
			this.setSlope_X(Math.sin(theta));
			slope.setZ(Math.cos(theta));
			Theta=theta;
			if (theta!=0&&theta!=Math.PI) System.out.println("Set by default slope x to match theta since s_perp=0");
		}
		else{
			double s_perp=Math.sqrt(slope.x()*slope.x()+slope.y()*slope.y());
			this.setSlope_X(slope.x()*Math.sin(theta));
			this.setSlope_Y(slope.y()*Math.sin(theta));
			slope.setZ(s_perp*Math.cos(theta));
			Theta=theta;
			}
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
		
	public Vector3D getPoint(double lambda) {
		Vector3D NewPoint=new Vector3D();
		NewPoint.setXYZ(slope.x()*lambda+point.x(),slope.y()*lambda+point.y() , slope.z()*lambda+point.z());
		return NewPoint;
	}
	
	public double getDistance(Cluster clus) {
		double distance=0;
		Vector3D point_inter=new Vector3D();
		point_inter.setXYZ(0,0,0);
		
		
		if (!Double.isNaN(clus.getX())&&!Double.isNaN(clus.getY())&&Double.isNaN(clus.getZ())) {
			//In that case, we just compute the distance between the line and the point in xy-plane
			//First case: is the slope in x and y are both null, the line becomes then a point.
			if (slope.x()==0&&slope.y()==0) distance=Math.sqrt(Math.pow(clus.getX()-point.x(), 2)+Math.pow(clus.getY()-point.y(), 2));
			
			//You have a line in xy-plane, so you can compute the distance between the point and the line
			else {
				if (slope.x()==0) distance=Math.abs(clus.getX()-point.x());
				if (slope.y()==0) distance=Math.abs(clus.getY()-point.y());
				if (slope.x()!=0&&slope.y()!=0) {
					//Need to find the intersect between the line and its orthogonal through the cluster
					double lambda=(clus.getY()*slope.y()+slope.x()*clus.getX()-slope.x()*point.x()-slope.y()*point.y())/(Math.pow(slope.x(), 2)+Math.pow(slope.y(), 2));
					Vector3D Proj=this.getPoint(lambda);
					distance=Math.sqrt(Math.pow(clus.getX()-Proj.x(), 2)+Math.pow(clus.getY()-Proj.y(), 2));
				}
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
		if (delta==0) {
		    double lambda=-b/2./a;
		    point_inter=this.getPoint(lambda);
		    distance=Math.abs(clus.getZ()-point_inter.z());
		}
		if (delta>0) {
			double lambda_a=(-b+Math.sqrt(delta))/2./a;
		    double lambda_b=(-b-Math.sqrt(delta))/2./a;
		    point_inter=this.getPoint(lambda_a);
		    double distance_a=Math.abs(clus.getZ()-point_inter.z());
		    point_inter=this.getPoint(lambda_b);
		    double distance_b=Math.abs(clus.getZ()-point_inter.z());
		    distance=Math.min(distance_a, distance_b);
		  }
		  
		}
		return distance;
	}

}
