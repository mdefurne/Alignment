package BST_struct;

public class Hit {
	
	private int adc_strip;
	private float time_strip;
	private double phi_strip;
	private double x_strip;
	private double y_strip;
	private double z_strip;
	private double err_phi_strip;
	private double err_z_strip;
		
	public Hit() {
		this.set(0,0,0,0,0,0,0,0);
	}
			
	public Hit(double x, double y, double z, double phi, double err_phi, double err_z, int adc, float time) {
		this.set(x, y , z , phi, err_phi, err_z, adc, time);
	}
		
	public final void set(double x, double y, double z, double phi,  double err_phi, double err_z, int adc, float time) {
		adc_strip=adc;
		time_strip=time;
		phi_strip=phi;	
		x_strip=x;
		y_strip=y;
		z_strip=z;	
		err_phi_strip=err_phi;	
		err_z_strip=err_z;	
	}
			
	public int getADC() {
		return adc_strip;
	}
		
	public float getTime() {
		return time_strip;
	}
	
	public double getPhi() {
		return phi_strip;
	}
	
	public double getErrPhi() {
		return err_phi_strip;
	}
	
	public double getZ() {
		return z_strip;
	}
	
	public double getX() {
		return x_strip;
	}
	
	public double getY() {
		return y_strip;
	}
	
	public double getErrZ() {
		return err_z_strip;
	}
		
			
	public void print() {
		System.out.println("ADC= "+adc_strip+" and time= "+time_strip+" ns");
	}

	

}
