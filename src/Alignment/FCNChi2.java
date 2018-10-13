package Alignment;

import org.freehep.math.minuit.FCNBase;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;
import Trajectory.StraightLine;
import BMT_struct.*;
import BST_struct.*;

public class FCNChi2 implements FCNBase {

	HipoDataSource reader;
	Barrel BMT;
	Barrel_SVT BST;
	int layer;
	int sector;
	
	public double valueOf(double[] par)
	   {
		
		  StraightLine line=new StraightLine();
		  double val=0;	  
		  int count=0;
		  //First three parameters are Rotations
		  BMT_geo.Constants.setRx(layer, sector, par[0]);
		  BMT_geo.Constants.setRy(layer, sector, par[1]);
		  BMT_geo.Constants.setRz(layer, sector, par[2]);
		  //Last three parameters are rotations
		  BMT_geo.Constants.setCx(layer, sector, par[3]);
		  BMT_geo.Constants.setCy(layer, sector, par[4]);
		  BMT_geo.Constants.setCz(layer, sector, par[5]);
		  
		  while(reader.hasEvent()) {
			  DataEvent event = reader.gotoEvent(count);
			
				count++;
			    	   
			    
			    if(event.hasBank("BMT::adc")&&event.hasBank("BST::adc")) {
			    	
			    }
		  }
	      return val;
	   }
	
	public void SetDetectorToAlign(Barrel BMT_det, Barrel_SVT BST_det, HipoDataSource Filereader, int lay, int sec) {
		reader=Filereader;
		BMT=BMT_det;
		BST=BST_det;
		layer=lay;
		sector=sec;
	}

}
