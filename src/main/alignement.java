package main;

import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import Analyzer.Analyzer;
import BMT_struct.Barrel;
import BST_struct.Barrel_SVT;
import TrackFinder.*;
import Particles.*;
import PostProcessor.*;


public class alignement {
	static Barrel BMT;
	static Barrel_SVT BST;
	static ParticleEvent MCParticles;
	static Analyzer Sherlock;
	static Tracker Tracky;
	
	
	public alignement() {
		BST=new Barrel_SVT();
		BMT=new Barrel();
		MCParticles=new ParticleEvent();
		Tracky=new Tracker();
		Sherlock=new Analyzer();
	}
	
	public static void main(String[] args) {
		
		alignement MVTAli=new alignement();
		
		main.constant.IncludeSVT(true);
		
		String fileName;
		fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/out_clas_002467.evio.208.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_all.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_off.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/bug.hipo";
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		int count=0;
			
		//while(reader.hasEvent()) {
		while(count<100) {
			DataEvent event = reader.getNextEvent();
			 
			//DataEvent event = reader.gotoEvent(19476);	
		    count++;
		    //System.out.println(count);
		  
		    //Load all the constant needed but only for the first event
		    if (!main.constant.isLoaded) {
		    	if (event.hasBank("MC::Particle")) {
		    		main.constant.setMC(true);
		    		if (main.constant.isMC&&BMT.getTile(1, 1).getClusteringMode()==1) System.out.println("Mode 1 is not available for MC data");
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
		    	TrackFinder Lycos=new TrackFinder(BMT,BST);
		    	Lycos.BuildCandidates();
		    	Lycos.FetchTrack();
		    	if (event.hasBank("MC::Particle")) MCParticles.readMCBanks(event);
		    	Tracky.addEvent(count, Lycos.get_Candidates());
		    	Sherlock.analyze(BST, Lycos.get_Candidates(), MCParticles);
		    }
		   		   		         
		}
		Tracky.draw();
		Sherlock.draw();		
		System.out.println("Done! "+count);
 }
}