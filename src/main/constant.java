package main;

import Trajectory.StraightLine;

public class constant {
	
	public static double solenoid_scale=0;
	public static boolean isLoaded=false;
	public static boolean isMC=false;
	public static boolean isCosmic=false;
	private static boolean WithSVT=false;
	public static StraightLine IdealBeam;
	
	public static double getSolenoidscale() {
		return solenoid_scale;
	}
	
	public static void setSolenoidscale(double scale) {
		solenoid_scale=scale;
		IdealBeam=new StraightLine();
		IdealBeam.setPoint_XYZ(0, 0, 0);
		IdealBeam.setSlope_XYZ(0, 0, 1);
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
	
	public static void IncludeSVT(boolean is) {
		WithSVT=is;
	}
	
	public static boolean IsWithSVT() {
		return WithSVT;
	}
	
}
