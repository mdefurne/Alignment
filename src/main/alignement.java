package main;

import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import Analyzer.Analyzer;
import BMT_struct.Barrel;
import BST_struct.Barrel_SVT;
import TrackFinder.*;
import Particles.*;
import PostProcessor.*;
import HipoWriter.*;


public class alignement {
	static Barrel BMT;
	static Barrel_SVT BST;
	static ParticleEvent MCParticles;
	static CentralWriter Asimov;
	static Analyzer Sherlock;
	static Tracker Tracky;
	
	
	public alignement() {
		BST=new Barrel_SVT();
		BMT=new Barrel();
		MCParticles=new ParticleEvent();
		Tracky=new Tracker();
		Sherlock=new Analyzer();
		Asimov=new CentralWriter();
	}
	
	public static void main(String[] args) {
		
		alignement MVTAli=new alignement();
		
		main.constant.IncludeSVT(true);
		
		String fileName;
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/cos148.hipo";
		fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/cosmic_mc.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/cos_march.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/out_clas_002467.evio.208.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_all.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_off.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/bug.hipo";
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		int count=0;
			
		while(reader.hasEvent()) {
		//while(count<100) {
			DataEvent event = reader.getNextEvent();
			 
			count++;
		    		  
		    //Load all the constant needed but only for the first event
		    if (!main.constant.isLoaded) {
		    	if (event.hasBank("MC::Particle")) {
		    		main.constant.setMC(true);
		    		if (main.constant.isMC&&BMT.getTile(1, 1).getClusteringMode()==1) System.out.println("Mode 1 is not available for MC data");
		    	}
		    
		    	if (event.hasBank("RUN::config")) {
		    		main.constant.setSolenoidscale(event.getBank("RUN::config").getFloat("solenoid", 0));
		    	}
		    	
		    	if (!event.hasBank("RUN::rf")) {
		    		//main.constant.setCosmic(true);
		    		//if (main.constant.IsWithSVT()) main.constant.IncludeSVT(false);
		    	}
		    	
		    	main.constant.setLoaded(true);
		    	
		    	
		    }
		    
		    if(event.hasBank("BMT::adc")&&event.hasBank("BST::adc")) {
		    	BMT.fillBarrel(event.getBank("BMT::adc"),main.constant.isMC);
		    	if (main.constant.IsWithSVT()) BST.fillBarrel(event.getBank("BST::adc"),main.constant.isMC);
		    	TrackFinder Lycos=new TrackFinder(BMT,BST);
		    	Lycos.BuildCandidates();
		    	Lycos.FetchTrack();
		    	if (event.hasBank("MC::Particle")) MCParticles.readMCBanks(event);
		    	Tracky.addEvent(count, Lycos.get_Candidates());
		    	Sherlock.analyze(BST, Lycos.get_Candidates(), MCParticles);
		    	Asimov.WriteEvent(BMT, BST, Tracky.DuplicateRemoval(Lycos.get_Candidates()), MCParticles);
		    }
		   		   		         
		}
		Asimov.close();
		Tracky.draw();
		Sherlock.draw();		
		System.out.println("Done! "+count);
 }
}