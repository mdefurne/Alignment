package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import Analyzer.Analyzer;
import DC_struct.DriftChambers;
import TrackFinder.*;
import Particles.*;
import PostProcessor.*;
import HipoWriter.*;

import org.jlab.detector.base.DetectorType;
import org.jlab.detector.base.GeometryFactory;
import org.jlab.detector.geant4.v2.DCGeant4Factory;
import org.jlab.geom.base.ConstantProvider;


public class DCStraightTracker {
	static DriftChambers DC;
	static ParticleEvent MCParticles;
	static CentralWriter Asimov;
	static Analyzer Sherlock;
	static Fitter Tracky;
	static ArrayList<Integer> DisabledLayer;
	static ArrayList<Integer> DisabledSector;
	static org.jlab.detector.geant4.v2.DCGeant4Factory DCgeo;
	
	public DCStraightTracker() {
		
		MCParticles=new ParticleEvent();
		Tracky=new Fitter();
		Sherlock=new Analyzer();
		Asimov=new CentralWriter();
		ConstantProvider provider = GeometryFactory.getConstants(DetectorType.DC, 2467, Optional.ofNullable("default").orElse("default"));
		DCgeo = new DCGeant4Factory(provider, true);//DCGeant4Factory.MINISTAGGERON);
		DC=new DriftChambers(DCgeo);
		DisabledLayer=new ArrayList<Integer>();
		DisabledSector=new ArrayList<Integer>();
		
	}
	
	
	public static void main(String[] args) {
		
		//String fileName="/home/mdefurne/Bureau/CLAS12/DCAlignment/out_00001.hipo";
		String fileName="/home/mdefurne/Bureau/CLAS12/DCAlignment/r2_cy_0p2deg/out_clas_002467.evio.156.hipo";
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		int count=150;
		DataEvent event_zero = reader.gotoEvent(1);
		
		DCStraightTracker Straight=new DCStraightTracker();
		
		
		
		System.out.println("Starting Reconstruction.....");
		
		while(reader.hasEvent()&&count<151) {
		  DataEvent event = reader.gotoEvent(count);
		
			count++;
			//System.out.println(count);
			
		    //Load all the constant needed but only for the first event
		   
		    
		    if(event.hasBank("DC::tdc")) {
		    	DC.fillDCs(event.getBank("DC::tdc"));
		    	DC.FindTrackCandidate();
		    	
		    	for (int sec=1;sec<7;sec++) {
		    		if (DC.getSector(sec).getSectorSegments().size()<10) {
		    			System.out.println(sec+" "+DC.getSector(sec).getSectorSegments().size());
		    			for (int tr=0; tr<DC.getSector(sec).getSectorSegments().size();tr++) {
		    				Tracky.DCStraightTrack(DC.getSector(sec).getSectorSegments().get(tr));
		    				System.out.println(sec+" "+DC.getSector(sec).getSectorSegments().get(tr).getHBtrack().getSlope());
		    				//DC.getSector(sec).getSectorSegments().get(0).PrintSegment();
		    			}
		    		}
		    	}
		    }
		   		   		         
		}
		
		
		System.out.println("Done! "+count);
 }
	
}