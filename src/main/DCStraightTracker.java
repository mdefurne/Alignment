package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import Analyzer.Analyzer;
import DC_struct.DriftChambers;
import DC_struct.Segment;
import TrackFinder.*;
import Particles.*;
import PostProcessor.*;
import HipoWriter.*;

import org.jlab.detector.base.DetectorType;
import org.jlab.detector.base.GeometryFactory;
import org.jlab.detector.geant4.v2.DCGeant4Factory;
import org.jlab.geom.base.ConstantProvider;


public class DCStraightTracker {
	static ParticleEvent MCParticles;
	static ForwardWriter Asimov;
	static Analyzer Sherlock;
	static Fitter Lycos;
	static Tracker Tracky;
	static ArrayList<Integer> DisabledLayer;
	static ArrayList<Integer> DisabledSector;
	static org.jlab.detector.geant4.v2.DCGeant4Factory DCgeo;
		
	public DCStraightTracker() {
		//How to get the cell size from geometry class... a complete mystery
		
		MCParticles=new ParticleEvent();
		Tracky=new Tracker("DC");
		Lycos=new Fitter();
		Sherlock=new Analyzer();
		Asimov=new ForwardWriter();
		DisabledLayer=new ArrayList<Integer>();
		DisabledSector=new ArrayList<Integer>();
		
	}
	
	
	public static void main(String[] args) {
		
		//String fileName="/home/mdefurne/Bureau/CLAS12/DCAlignment/out_00001.hipo";
		String fileName=args[0];//"/home/mdefurne/Bureau/CLAS12/DCAlignment/r2_cy_0p2deg/out_clas_002467.evio.156.hipo";
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		DataEvent event_zero = reader.gotoEvent(1);
		int count=1;
		
		int run=-1;
		if(event_zero.hasBank("RUN::config")) {
			run=event_zero.getBank("RUN::config").getInt("run",0); 
		 }
		//Loading the good calibration constant
		ConstantProvider provider = GeometryFactory.getConstants(DetectorType.DC, run, Optional.ofNullable("default").orElse("default"));
		DCgeo = new DCGeant4Factory(provider, true);//DCGeant4Factory.MINISTAGGERON);
		DriftChambers DC=new DriftChambers(run,"calib",DCgeo);
		DCStraightTracker Straight=new DCStraightTracker();
		
		Asimov.setOuputFileName(args[1]);//"/home/mdefurne/Bureau/CLAS12/DCAlignment/r2_cy_0p2deg/new_clas_002467.evio.156.hipo");
		//Asimov.setOuputFileName("/home/mdefurne/Bureau/CLAS12/DCAlignment/new_out_00001.hipo");
		//System.setProperty("java.awt.headless", "true");
		System.out.println("Starting Reconstruction.....");
		
		while(reader.hasEvent()&&count<1000) {
		  DataEvent event = reader.gotoEvent(count);
		
			count++;
			//System.out.println(count);
			
		    //Load all the constant needed but only for the first event
		   
		    
		    if(event.hasBank("DC::tdc")) {
		    	System.out.println("///////////////// "+count);
		    	DC.fillDCs(event.getBank("DC::tdc"), event.getBank("RUN::config").getLong("timestamp", 0));
		    	DC.FindTrackCandidate();
		    	if (event.hasBank("MC::Particle")) MCParticles.readMCBanks(event);
		    	for (int sec=1;sec<7;sec++) {
		    		
		    		if (DC.getSector(sec).getSectorSegments().size()<10) {
		    			for (int tr=0; tr<DC.getSector(sec).getSectorSegments().size();tr++) {
		    				if (DC.getSector(sec).getSectorSegments().get(tr).getSize()>30) {
		    					Lycos.DCStraightTrack(DC.getSector(sec).getSectorSegments().get(tr));
		    				 }
		    			}
		    		}
		    	}
		    	
		    	Tracky.addForwardEvent(count,DC);
		    	
				Asimov.WriteEvent(count, DC, MCParticles);
		    }
		   		   		         
		}
		
		Asimov.close();
		Tracky.draw();
		System.out.println("Done! "+count);
 }
	
}