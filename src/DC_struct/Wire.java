package DC_struct;

public class Wire {
	int tdc;
	double res;
	double doca;
	double spacing;
	
	public Wire(int tdc_m) {
		tdc=tdc_m;
		res=Double.NaN;
		doca=tdc;
		spacing=1; //cm
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
	
}
