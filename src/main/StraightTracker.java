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


public class StraightTracker {
	static Barrel BMT;
	static Barrel_SVT BST;
	static ParticleEvent MCParticles;
	static CentralWriter Asimov;
	static Analyzer Sherlock;
	static Tracker Tracky;
	
	
	public StraightTracker() {
		BST=new Barrel_SVT();
		BMT=new Barrel();
		MCParticles=new ParticleEvent();
		Tracky=new Tracker();
		Sherlock=new Analyzer();
		Asimov=new CentralWriter();
	}
	
	public static void main(String[] args) {
		
		if (args.length<4) {
			System.out.println("Execution line is as follows:\n");
			System.out.println("java -jar Tracker.jar INPUT_FILE OUTPUT_FILE TRACKER_TYPE RUN_TYPE (-d DRAW -n NUM_EVENTS -m MODE -l X/Y)");
			System.out.println("TRACKER_TYPE: MVT, SVT or CVT");
			System.out.println("RUN_TYPE: cosmic or target\n");
			System.out.println("with a few options which might be useful");
			System.out.println("NUM_EVENTS: to set a maximum number of events");
			System.out.println("DRAW: Display residuals and beam info if DRAW is entered");
			System.out.println("X/Y: will disable layer X sector Y. If Y=*, disable an entire layer");
			System.out.println("MODE can be chosen among:");
			System.out.println("       -EFFICENCY: Prevent from merging tracks from different sectors in cosmic mode\n");
			System.out.println("For more info, please contact Maxime DEFURNE");
			System.out.println("maxime.defurne@cea.fr");
			System.exit(0);
		}
		
		String fileName=args[0];
		String Output=args[1];
		String TrackerType=args[2];
		String RunType=args[3];
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		int count=0;
		DataEvent event_zero = reader.gotoEvent(1);
		if (!main.constant.isLoaded) {
		   	if (event_zero.hasBank("MC::Particle")) main.constant.setMC(true);
		   		
		   	if (event_zero.hasBank("RUN::config")) {
		   		main.constant.setSolenoidscale(event_zero.getBank("RUN::config").getFloat("solenoid", 0));
		   	}
		   	
		   	main.constant.setLoaded(true);
	   }
		
		if (RunType.equals("cosmic")) main.constant.setCosmic(true);
		
		main.constant.setTrackerType(TrackerType);
		
		StraightTracker Straight=new StraightTracker();
		Asimov.setOuputFileName(Output);
		
		for (int i=4; i<args.length; i++) {
			if (args[i].equals("-d")&&args[i+1].equals("DRAW")) main.constant.drawing=true;
			if (args[i].equals("-n")) main.constant.max_event=Integer.parseInt(args[i+1]);
			if (args[i].equals("-m")&&args[i+1].equals("EFFICIENCY")) main.constant.efficiency=true;
			if (args[i].equals("-l")) {
				int LayToDisable=Integer.parseInt(args[i+1].substring(0, args[i+1].indexOf('/')));
				if (args[i+1].charAt(args[i+1].length()-1)=='*') {
					if (LayToDisable>=7) BMT.DisableLayer(LayToDisable-6);
					else BST.DisableLayer(LayToDisable);
				}
				else {
					int sectorToDisable=Integer.parseInt(args[i+1].substring(args[i+1].indexOf('/'),args[i+1].length()));
					if (LayToDisable>=7) BMT.DisableTile(LayToDisable-6,sectorToDisable);
					else BST.DisableModule(LayToDisable,sectorToDisable);
				}
			}
			
		}
		
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/cosmic_mc.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/cos_march.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/out_clas_002467.evio.208.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/3859.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_all.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_off.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/bug.hipo";
		
		while(reader.hasEvent()&&count<main.constant.max_event) {
		  DataEvent event = reader.gotoEvent(count);
		
			count++;
		    	 System.out.println(count);
		    //Load all the constant needed but only for the first event
		   
		    
		    if(event.hasBank("BMT::adc")&&event.hasBank("BST::adc")) {
		    	BMT.fillBarrel(event.getBank("BMT::adc"),main.constant.isMC);
		    	BST.fillBarrel(event.getBank("BST::adc"),main.constant.isMC);
		    	if (BMT.getNbHits()<50&&(BST.getNbHits()<50||!main.constant.isCosmic)) { //Cut on 50 hits if cosmic (most likely shower)
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
		if (main.constant.drawing) {
			Tracky.draw();
			Sherlock.draw();		
		}
		System.out.println("Done! "+count);
 }
}