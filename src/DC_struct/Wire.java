package DC_struct;

public class Wire {
	int tdc;
	double res;
	
	public Wire(int tdc_m) {
		tdc=tdc_m;
		res=Double.NaN;
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
	
}
