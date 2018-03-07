package main;

import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.geom.prim.Vector3D;
import org.jlab.io.hipo.HipoDataSource;
import BMT_struct.Barrel;
import BST_struct.Barrel_SVT;
import TrackFinder.*;
import Analyzer.*;

public class alignement {
	static Barrel BMT;
	static Barrel_SVT BST;
	static Analyzer Holmes;
	
	public alignement() {
		BST=new Barrel_SVT();
		BMT=new Barrel();
		Holmes=new Analyzer();
	}
	
	public static void main(String[] args) {
		
		alignement MVTAli=new alignement();
		
		String fileName;
		//fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/out_clas_002467.evio.208.hipo";
		fileName = "/home/mdefurne/Bureau/CLAS12/GEMC_File/output/muon_all.hipo";
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		int count=0;
		boolean isMC=false;
		
		while(reader.hasEvent()) {
		    DataEvent event = reader.getNextEvent();
		    count++;
		    
		    if (event.hasBank("MC::True")) isMC=true;
		    
		    if(event.hasBank("BMT::adc")&&event.hasBank("BST::adc")) {
		    	BMT.fillBarrel(event.getBank("BMT::adc"),isMC);
		    	BST.fillBarrel(event.getBank("BST::adc"),isMC);
		    	TrackFinder tracky=new TrackFinder();
		    	tracky.BuildCandidates(BMT);
		    	tracky.FetchTrack();
		    	Holmes.analyze(BMT, BST, tracky.get_Candidates());
		    }
		         
		}
		Holmes.draw();
		
		System.out.println("Done!");
 }
}