package main;

public class constant {
	
	public static double solenoid_scale=0;
	public static boolean isLoaded=false;
	public static boolean isMC=false;
	public static boolean isCosmic=false;
	
	public static double getSolenoidscale() {
		return solenoid_scale;
	}
	
	public static void setSolenoidscale(double scale) {
		solenoid_scale=scale;
	}
	
	public static boolean IsMC() {
		return isMC;
	}
	
	public static void setMC(boolean is) {
		isMC=is;
	}
	
	public static void setLoaded(boolean is) {
		isLoaded=is;
	}
	
	public static boolean IsCosmic() {
		return isCosmic;
	}
	
	public static void setCosmic(boolean is) {
		isCosmic=is;
	}
	
}
