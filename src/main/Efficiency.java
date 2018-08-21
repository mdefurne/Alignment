package main;

import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;


public class Efficiency {

	public static void main(String[] args) {
		
		int count=0;
		double ref_track=0;
		double[][] GotHit=new double[6][3];
		for (int i=0;i<6;i++) {
			for (int j=0;j<3;j++) {
				GotHit[i][j]=0;
			}
		}
		
		String fileName="/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/cvt211_str490dr500.hipo";
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		
		while(reader.hasEvent()) {
		  DataEvent event = reader.getNextEvent();
		  //Look if a track has been found
		  if(event.hasBank("CVTRec::Cosmics")&&event.hasBank("CVTRec::Trajectory")) {
			  
			  for (int track=0;track<10;track++) {
				  //For each good track found (i.e. crossing the 12 micromegas layer with 10 SVT hits found), we check if we have a cluster
				  
			  }
			  
		  }
		}
	}
}
