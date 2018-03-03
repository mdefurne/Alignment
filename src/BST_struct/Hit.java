package BST_struct;

public class Hit {
	
	int adc_strip;
	float time_strip;
		
	public Hit() {
		this.set(0,0);
	}
			
	public Hit(int adc, float time) {
		this.set(adc, time);
	}
		
	public final void set(int adc, float time) {
		adc_strip=adc;
		time_strip=time;
			
	}
			
	public int getADC() {
		return adc_strip;
	}
		
	public float getTime() {
		return time_strip;
	}
		
			
	public void print() {
		System.out.println("ADC= "+adc_strip+" and time= "+time_strip+" ns");
	}

	

}
