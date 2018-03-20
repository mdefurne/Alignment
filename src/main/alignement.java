package main;

import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.geom.prim.Vector3D;
import org.jlab.io.hipo.HipoDataSource;
import BMT_struct.Barrel;
import BST_struct.Barrel_SVT;
import TrackFinder.*;
import Analyzer.*;
import Particles.*;

public class alignement {
	static Barrel BMT;
	static Barrel_SVT BST;
	static ParticleEvent MCParticles;
	static Analyzer Sherlock;
	
	public alignement() {
		BST=new Barrel_SVT();
		BMT=new Barrel();
		Sherlock=new Analyzer();
		MCParticles=new ParticleEvent();
	}
	
	public static void main(String[] args) {
		
		alignement MVTAli=new alignement();
		
		String fileName;
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/out_clas_002467.evio.208.hipo";
		fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_all.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_off.hipo";
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		int count=0;
			
		while(reader.hasEvent()&&count<20000) {
		 DataEvent event = reader.getNextEvent();
		//DataEvent event = reader.gotoEvent(3418);	
		    count++;
		    System.out.println(count);
		    //Load all the constant needed but only for the first event
		    if (!main.constant.isLoaded) {
		    	if (event.hasBank("MC::Particle")) {
		    		main.constant.setMC(true);
		    	}
		    
		    	if (event.hasBank("RUN::config")) {
		    		main.constant.setSolenoidscale(event.getBank("RUN::config").getFloat("solenoid", 0));
		    		if (main.constant.solenoid_scale==0.0) main.constant.setCosmic(true);
		    	}
		    	main.constant.setLoaded(true);
		    }
		    
		    //Analyze the event
		    if(event.hasBank("BMT::adc")&&event.hasBank("BST::adc")) {
		    	BMT.fillBarrel(event.getBank("BMT::adc"),main.constant.isMC);
		    	BST.fillBarrel(event.getBank("BST::adc"),main.constant.isMC);
		    	TrackFinder tracky=new TrackFinder(BMT,BST);
		    	tracky.BuildCandidates();
		    	tracky.FetchTrack();
		    	if (event.hasBank("MC::Particle")) MCParticles.readMCBanks(event);
		    	Sherlock.analyze(BMT, BST, tracky.get_Candidates(), MCParticles);
		    }
		   		   		         
		}
		
		Sherlock.draw();
		
		System.out.println("Done!");
 }
}