package main;

import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
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
	static ArrayList<Integer> DisabledLayer;
	static ArrayList<Integer> DisabledSector;
	
	public StraightTracker() throws IOException {
		BST=new Barrel_SVT();
		BMT=new Barrel();
		MCParticles=new ParticleEvent();
		Tracky=new Tracker("CVT");
		Sherlock=new Analyzer();
		Asimov=new CentralWriter();
		
		DisabledLayer=new ArrayList<Integer>();
		DisabledSector=new ArrayList<Integer>();
		
	}
	
	public boolean IsGoodEvent() {
		boolean Interesting=false;
		
		if (BMT.getNbHits()<50&&(BST.getNbHits()<50||!main.constant.isCosmic)) {//Cut on 50 hits if cosmic (most likely shower)
			if (DisabledLayer.size()==0) Interesting=true;
			else {
				// If a tile or module is disabled, we are interested in aligning or analyzing this specific one... 
				//so no need to do tracking for events without a single hit in this specific module
				for (int lay=0;lay<DisabledLayer.size();lay++) {
					if (DisabledLayer.get(lay)<7) {
						if (BST.getModule(DisabledLayer.get(lay), DisabledSector.get(lay)).getHits().size()!=0) Interesting=true;
					}
					if (DisabledLayer.get(lay)>=7) {
						if (BMT.getTile(DisabledLayer.get(lay)-7, DisabledSector.get(lay)-1).getHits().size()!=0) Interesting=true;
					}
				}
			}
		}
		return Interesting;
	}
	
	public static void main(String[] args) throws IOException {
		
		if (args.length<4) {
			System.out.println("Execution line is as follows:\n");
			System.out.println("java -jar Tracker.jar TRACKER_TYPE RUN_TYPE -i INPUT_FILE -o OUTPUT_FILE(-d DRAW -n NUM_EVENTS -m MODE -l X/Y -a ALIGNFILE)");
			System.out.println("TRACKER_TYPE: MVT, SVT or CVT");
			System.out.println("RUN_TYPE: cosmic or target\n");
			System.out.println("with a few options which might be useful");
			System.out.println("NUM_EVENTS: to set a maximum number of events");
			System.out.println("DRAW: Display residuals and beam info if DRAW is entered");
			System.out.println("X/Y: will disable layer X sector Y. If Y=*, disable an entire layer");
			System.out.println("ALIGNFILE: If path to a file with misalignement parameters is entered, reconstruction will use it");
			System.out.println("MODE can be chosen among:");
			System.out.println("       -EFFICENCY: Prevent from merging tracks from different sectors in cosmic mode\n");
			System.out.println("       -MILLEPEDE: Produce a binary file Mille.dat for Millepede alignment\n");
			System.out.println("For more info, please contact Maxime DEFURNE");
			System.out.println("maxime.defurne@cea.fr");
			System.exit(0);
		}
		
		ArrayList<String> fileName=new ArrayList<String>();
		String Output="";
		String TrackerType=args[0];
		String RunType=args[1];
												
		if (RunType.equals("cosmic")) main.constant.setCosmic(true);
		
		main.constant.setTrackerType(TrackerType);
		
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-i")) fileName.add(args[i+1]);
			if (args[i].equals("-o")) Output=args[i+1];
			if (args[i].equals("-d")&&args[i+1].equals("DRAW")) main.constant.drawing=true;
			if (args[i].equals("-n")) main.constant.max_event=Integer.parseInt(args[i+1]);
			if (args[i].equals("-m")&&args[i+1].equals("EFFICIENCY")) main.constant.efficiency=true;
			if (args[i].equals("-m")&&args[i+1].equals("MILLEPEDE")) main.constant.millepede=true;
		}
		
		//All modes and parameters are defined. Now we can create StraightTracker
		StraightTracker Straight=new StraightTracker();
		String AlignFileSVT="";
		String AlignFileMVT="";
		String AlignFileCVT="";
		ArrayList<String> AlignFilePEDE=new ArrayList<String>();
		for (int i=4; i<args.length; i++) {
			if (args[i].equals("-svt")) AlignFileSVT=args[i+1];
			if (args[i].equals("-mvt")) AlignFileMVT=args[i+1];
			if (args[i].equals("-cvt")) AlignFileCVT=args[i+1];
			if (args[i].equals("-pede")) AlignFilePEDE.add(args[i+1]);
			if (args[i].equals("-l")) {
				int LayToDisable=Integer.parseInt(args[i+1].substring(0, args[i+1].indexOf('/')));
				if (args[i+1].charAt(args[i+1].length()-1)=='*') {
					if (LayToDisable>=7) {
						BMT.DisableLayer(LayToDisable-6);
						for (int sec=1;sec<=3;sec++) {
							DisabledLayer.add(LayToDisable); DisabledSector.add(sec);	
						}
					}
					else {
						BST.DisableLayer(LayToDisable);
						for (int sec=1;sec<=BST.getGeometry().getNbModule(LayToDisable);sec++) {
							DisabledLayer.add(LayToDisable); DisabledSector.add(sec);	
						}
					}
				}
				else {
					int sectorToDisable=Integer.parseInt(args[i+1].substring(args[i+1].indexOf('/')+1,args[i+1].length()));
					if (LayToDisable>=7) BMT.DisableTile(LayToDisable-6,sectorToDisable);
					else BST.DisableModule(LayToDisable,sectorToDisable);
					DisabledLayer.add(LayToDisable); DisabledSector.add(sectorToDisable);
				}
			}
			
		}
		
		//Try to fetch alignment file if one is entered
		try {
			BMT.getGeometry().LoadMisalignmentFromFile(AlignFileMVT);
			BMT.getGeometry().LoadMVTSVTMisalignment(AlignFileCVT);
			BST.getGeometry().LoadMisalignmentFromFile(AlignFileSVT);
			Straight.LoadMillepedeMisalignment(BMT,BST,AlignFilePEDE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Asimov.setOuputFileName(Output);
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/cosmic_mc.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/cos_march.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/out_clas_002467.evio.208.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/3859.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_all.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_off.hipo";
		//fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/bug.hipo";
		
		System.out.println("Starting Reconstruction.....");
		HipoDataSource[] reader = new HipoDataSource[fileName.size()];
		int count=0;
		for (int ff=0;ff<fileName.size();ff++) {
			reader[ff]=new HipoDataSource();
			reader[ff].open(fileName.get(ff));
				
			for (int i=0; i<reader[ff].getSize();i++) {
			/*while(reader[ff].hasEvent()&&count<main.constant.max_event) {
				DataEvent event = reader[ff].getNextEvent();*/
				DataEvent event = reader[ff].gotoEvent(i);
				if (!main.constant.isLoaded) {
					if (event.hasBank("MC::Particle")) main.constant.setMC(true);
					main.constant.setLoaded(true);
					main.constant.setSolenoidscale(0);
				}
				count++;
			//System.out.println(count);
			
		    //Load all the constant needed but only for the first event
		   
		    
				if(event.hasBank("BMT::adc")&&event.hasBank("BST::adc")) {
					BMT.fillBarrel(event.getBank("BMT::adc"),main.constant.isMC);
					BST.fillBarrel(event.getBank("BST::adc"),main.constant.isMC);
					if (Straight.IsGoodEvent()) { 
						TrackFinder Lycos=new TrackFinder(BMT,BST);
						Lycos.BuildCandidates();
						Lycos.FetchTrack();
						if (event.hasBank("MC::Particle")) MCParticles.readMCBanks(event);
						Tracky.addCVTEvent(count, Lycos.get_Candidates());
						Asimov.WriteEvent(count,BMT, BST, Tracky.CentralDuplicateRemoval(Lycos.get_Candidates()), MCParticles);
						Sherlock.analyze(BST, Lycos.get_Candidates(), MCParticles);
						//System.out.println(BMT.getGeometry().getCz(6,3)+" "+BST.getGeometry().getRx(2, 6));
						///////////////////////////////////////
					}
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

	private void LoadMillepedeMisalignment(Barrel bMT2, Barrel_SVT bST2, ArrayList<String> alignFilePEDE) throws FileNotFoundException {
		// TODO Auto-generated method stub
		for (int i=0;i<alignFilePEDE.size();i++) {
			File GeoTrans=new File(alignFilePEDE.get(i));
		
			String separator = "\\s+";
		
			int[] reverseLabeling=new int[3];
		
			if (GeoTrans.exists()) {
				System.out.println("Opening misalignment file from Millepede: "+alignFilePEDE.get(i));
				String[] line=new String[2];
				Scanner input = new Scanner(GeoTrans);
				//System.out.println(Integer.parseInt(line[0])+" "+Double.parseDouble(line[1]));
				while (input.hasNextLine()) {
					line = input.nextLine().trim().replaceAll(separator, " ").split(separator);
					reverseLabeling=bST2.ReverseMillepedeLabel(Integer.parseInt(line[0]));
					System.out.println(Integer.parseInt(line[0])+" "+Double.parseDouble(line[1]));
					if (reverseLabeling[2]!=-1) {
						if (reverseLabeling[2]==0) bST2.getGeometry().setRx(reverseLabeling[0],reverseLabeling[1], bST2.getGeometry().getRx(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
						if (reverseLabeling[2]==1) bST2.getGeometry().setRy(reverseLabeling[0],reverseLabeling[1], bST2.getGeometry().getRy(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
						if (reverseLabeling[2]==2) bST2.getGeometry().setRz(reverseLabeling[0],reverseLabeling[1], bST2.getGeometry().getRz(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
						if (reverseLabeling[2]==3) bST2.getGeometry().setCx(reverseLabeling[0],reverseLabeling[1], bST2.getGeometry().getCx(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
						if (reverseLabeling[2]==4) bST2.getGeometry().setCy(reverseLabeling[0],reverseLabeling[1], bST2.getGeometry().getCy(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
						if (reverseLabeling[2]==5) bST2.getGeometry().setCz(reverseLabeling[0],reverseLabeling[1], bST2.getGeometry().getCz(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
					
					}
					else {
						reverseLabeling=bMT2.ReverseMillepedeLabel(Integer.parseInt(line[0]));
						if (reverseLabeling[2]==0) bMT2.getGeometry().setRx(reverseLabeling[0],reverseLabeling[1], bMT2.getGeometry().getRx(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
						if (reverseLabeling[2]==1) bMT2.getGeometry().setRy(reverseLabeling[0],reverseLabeling[1], bMT2.getGeometry().getRy(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
						if (reverseLabeling[2]==2) bMT2.getGeometry().setRz(reverseLabeling[0],reverseLabeling[1], bMT2.getGeometry().getRz(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
						if (reverseLabeling[2]==3) bMT2.getGeometry().setCx(reverseLabeling[0],reverseLabeling[1], bMT2.getGeometry().getCx(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
						if (reverseLabeling[2]==4) bMT2.getGeometry().setCy(reverseLabeling[0],reverseLabeling[1], bMT2.getGeometry().getCy(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
						if (reverseLabeling[2]==5) bMT2.getGeometry().setCz(reverseLabeling[0],reverseLabeling[1], bMT2.getGeometry().getCz(reverseLabeling[0],reverseLabeling[1])-Double.parseDouble(line[1]));
					}
				}
			}
		}
		
		//Need to write down the file
		File CCDBMVTCst=new File("mvt.txt");
		File CCDBSVTCst=new File("svt.txt");
		//Writing MVT constant
		try {
			if (CCDBMVTCst.exists()) CCDBMVTCst.delete();
			CCDBMVTCst.createNewFile();
			FileWriter Writer=new FileWriter(CCDBMVTCst, true);
			try {
				for (int lay=1; lay<=6;lay++) {
					for (int sec=1; sec<=3;sec++) {
						Writer.write((lay+6)+" "+sec+" "+BMT_geo.Constants.getRx(lay, sec)+" "+BMT_geo.Constants.getRy(lay, sec)+" "+BMT_geo.Constants.getRz(lay, sec)+" "+
								BMT_geo.Constants.getCx(lay, sec)+" "+BMT_geo.Constants.getCy(lay, sec)+" "+BMT_geo.Constants.getCz(lay, sec)+"\n");
						}
					}
				}
			finally {
				
				Writer.close();
			}
		} catch (Exception e) {
			System.out.println("Impossible to write results in MVT file");
		}
		//Writing SVT constant
		try {
			if (CCDBSVTCst.exists()) CCDBSVTCst.delete();
			CCDBSVTCst.createNewFile();
			FileWriter Writer=new FileWriter(CCDBSVTCst, true);
			try {
				int num_sector=10;
				for (int lay=1; lay<=6;lay++) {
					for (int sec=1; sec<=num_sector;sec++) {
						Writer.write(lay+" "+sec+" "+bST2.getGeometry().getRx(lay, sec)+" "+bST2.getGeometry().getRy(lay, sec)+" "+bST2.getGeometry().getRz(lay, sec)+" "+
								bST2.getGeometry().getCx(lay, sec)+" "+bST2.getGeometry().getCy(lay, sec)+" "+bST2.getGeometry().getCz(lay, sec)+" "+0.0+"\n");
						}
					if (lay==2||lay==4) num_sector+=4;
					}
				
				}
			finally {
				
				Writer.close();
			}
		} catch (Exception e) {
			System.out.println("Impossible to write results in SVT file");
		}
	}
	
}