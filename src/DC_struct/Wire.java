package DC_struct;

import Trajectory.StraightLine;
import eu.mihosoft.vrl.v3d.Vector3d;

public class Wire {
	int tdc;
	double res;
	double doca;
	double spacing;
	Vector3d Midpoint;
	Vector3d Direction;
	StraightLine wire;
	
	public Wire(Vector3d dir, Vector3d mid, int tdc_m) {
		tdc=tdc_m;
		res=Double.NaN;
		Direction=dir;
		Midpoint=mid;
		doca=tdc;
		spacing=1; //cm
		wire=new StraightLine();
		wire.setSlope_XYZ(Direction.x, Direction.y, Direction.z);
		wire.setPoint_XYZ(Midpoint.x, Midpoint.y, Midpoint.z);
	}
	
	public int getTDC() {
		return tdc;
	}
	
	public void setResidual(double residu) {
		res=residu;
	}
	
	public double getResidual() {
		return res;
	}
	
	public double getDoca() {
		return doca;
	}
	
	public Vector3d getWireDirection() {
		return Direction;
	}
	
	public Vector3d getWirePoint() {
		return Midpoint;
	}
	
	public StraightLine getWire() {
		return wire;
	}
	
}
