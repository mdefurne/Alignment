package Trajectory;

import org.freehep.math.minuit.FCNBase;
import BMT_struct.Cluster;

public class StraightLine {
	
	double tx,ty,tz;
	double px,py,pz;
	
	public StraightLine() {
		
	}
	
	public void setTxTyTz(double dx, double dy, double dz) {
		this.setTx(dx);
		this.setTy(dy);
		this.setTz(dz);
	}
	
	public void setTx(double dx) {
		tx=dx;
	}
	
	public void setTy(double dy) {
		ty=dy;
	}
	
	public void setTz(double dz) {
		tz=dz;
	}
	
	public void setPxPyPz(double dx, double dy, double dz) {
		this.setPx(dx);
		this.setPy(dy);
		this.setPz(dz);
	}
	
	public void setPx(double dx) {
		px=dx;
	}
	
	public void setPy(double dy) {
		py=dy;
	}
	
	public void setPz(double dz) {
		pz=dz;
	}
	
	public double getDistance(Cluster clus) {
		double distance=0;
		
		return distance;
	}

}
