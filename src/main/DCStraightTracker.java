package main;

import java.io.IOException;
import java.util.ArrayList;

import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import Analyzer.Analyzer;
import DC_struct.DriftChambers;
import TrackFinder.*;
import Particles.*;
import PostProcessor.*;
import HipoWriter.*;
import org.jlab.detector.geant4.v2.DCGeant4Factory;


public class DCStraightTracker {
	static DriftChambers DC;
	static ParticleEvent MCParticles;
	static CentralWriter Asimov;
	static Analyzer Sherlock;
	static Tracker Tracky;
	static ArrayList<Integer> DisabledLayer;
	static ArrayList<Integer> DisabledSector;
	
	public DCStraightTracker() {
		DC=new DriftChambers();
		MCParticles=new ParticleEvent();
		Tracky=new Tracker();
		Sherlock=new Analyzer();
		Asimov=new CentralWriter();
		
		DisabledLayer=new ArrayList<Integer>();
		DisabledSector=new ArrayList<Integer>();
		
	}
	
	
	public static void main(String[] args) {
		
		//String fileName="/home/mdefurne/Bureau/CLAS12/DCAlignment/out_00001.hipo";
		String fileName="/home/mdefurne/Bureau/CLAS12/DCAlignment/r2_cy_0p2deg/out_clas_002467.evio.156.hipo";
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		int count=6;
		DataEvent event_zero = reader.gotoEvent(1);
		
		DCStraightTracker Straight=new DCStraightTracker();
		
		
		
		System.out.println("Starting Reconstruction.....");
		
		while(reader.hasEvent()&&count<7) {
		  DataEvent event = reader.gotoEvent(count);
		
			count++;
			//System.out.println(count);
			
		    //Load all the constant needed but only for the first event
		   
		    
		    if(event.hasBank("DC::tdc")) {
		    	DC.fillDCs(event.getBank("DC::tdc"));
		    	DC.FindTrackCandidate();
		    	System.out.println("/////////////////////// "+count);
		    	for (int sec=1;sec<7;sec++) {
		    		System.out.println(sec+" "+DC.getSector(sec).getSectorSegments().size());
		    		if (DC.getSector(sec).getSectorSegments().size()==1) DC.getSector(sec).getSectorSegments().get(0).PrintSegment();
		    	}
		    }
		   		   		         
		}
		
		
		System.out.println("Done! "+count);
 }
	
}