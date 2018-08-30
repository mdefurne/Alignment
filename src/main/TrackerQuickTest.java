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


public class TrackerQuickTest {
	static Barrel BMT;
	static Barrel_SVT BST;
	static ParticleEvent MCParticles;
	static CentralWriter Asimov;
	static Analyzer Sherlock;
	static Tracker Tracky;
	
	
	public TrackerQuickTest(String Output) {
		BST=new Barrel_SVT();
		BMT=new Barrel();
		MCParticles=new ParticleEvent();
		Tracky=new Tracker();
		Sherlock=new Analyzer();
		Asimov=new CentralWriter();
	}
	
	public static void main(String[] args) {
		
		String Output="/home/mdefurne/Bureau/CLAS12/test.hipo";
		String TrackerType="CVT";
		String RunType="cosmic";
		
		if (RunType.equals("cosmic")) main.constant.setCosmic(true);
		
		main.constant.setTrackerType(TrackerType);
		
		for (int i=4; i<args.length; i++) {
			if (args[i].equals("-d")&&args[i+1].equals("DRAW")) main.constant.drawing=true;
			if (args[i].equals("-n")) main.constant.max_event=Integer.parseInt(args[i+1]);
		}
		
		Asimov.setOuputFileName(Output);
		
		TrackerQuickTest MVTAli=new TrackerQuickTest(Output);
		//String fileName="/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/tracker_cvtCosmics1-0145.hipo";
		String fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/cosmic_mc.hipo";
		//String fileName="/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/cvt211_str490dr500.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/cos_march.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/out_clas_002467.evio.208.hipo";
		//String fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/3859.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_all.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_off.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/bug.hipo";
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		int count=0;
			
		//for (int i=0;i<1;i++) {
			//DataEvent event = reader.gotoEvent(165+i);
		main.constant.max_event=1500;
		while(reader.hasEvent()&&count<main.constant.max_event) {
		 DataEvent event = reader.getNextEvent();
		
			count++;
		    	 System.out.println(count);
		    //Load all the constant needed but only for the first event
		    if (!main.constant.isLoaded) {
		    	if (event.hasBank("MC::Particle")) {
		    		main.constant.setMC(true);
		    		if (main.constant.isMC&&BMT.getTile(1, 1).getClusteringMode()==1) System.out.println("Mode 1 is not available for MC data");
		    	}
		    
		    	if (event.hasBank("RUN::config")) {
		    		main.constant.setSolenoidscale(event.getBank("RUN::config").getFloat("solenoid", 0));
		    	}
		    	
		    	main.constant.setLoaded(true);
		    }
		    
		    if(event.hasBank("BMT::adc")&&event.hasBank("BST::adc")) {
		    	BMT.fillBarrel(event.getBank("BMT::adc"),main.constant.isMC);
		    	BST.fillBarrel(event.getBank("BST::adc"),main.constant.isMC);
		    	if (BMT.getNbHits()<50) { //Cut on 50 hits if cosmic (most likely shower)
		    		TrackFinder Lycos=new TrackFinder(BMT,BST);
		    		Lycos.BuildCandidates();
		    		Lycos.FetchTrack();
		    		if (event.hasBank("MC::Particle")) MCParticles.readMCBanks(event);
		    		Tracky.addEvent(count, Lycos.get_Candidates());
		    		Sherlock.analyze(BST, Lycos.get_Candidates(), MCParticles);
		    	
		    		///////////////////////////////////////
		    		//Asimov.LoadADC(event.getBank("BST::adc"), event.getBank("BMT::adc"));
		    		Asimov.WriteEvent(count,BMT, BST, Tracky.DuplicateRemoval(Lycos.get_Candidates()), MCParticles);
		    	}
		    }
		   		   		         
		}
		Asimov.close();
		main.constant.drawing=true;
		if (main.constant.drawing) {
			Tracky.draw();
			Sherlock.draw();		
		}
		System.out.println("Done! "+count);
 }
}