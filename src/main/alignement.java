package main;

import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.geom.prim.Point3D;
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
	
	public boolean init() {
        System.out.println(" ........................................ trying to connect to db ");
        //BST_geo.CCDBConstantsLoader.Load(new DatabaseConstantProvider(10, "default"));
        BST_geo.Constants.Load();
        return true;
    }
	
	
	public static void main(String[] args) {
		
		alignement MVTAli=new alignement();
		MVTAli.init();
		
		String fileName;
		fileName = "/home/mdefurne/Bureau/CLAS12/MVT/engineering/alignement_run/out_clas_002467.evio.208.hipo";
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		int count=0;
		while(reader.hasEvent()&&count<40) {
		    DataEvent event = reader.getNextEvent();
		    count++;
		    //System.out.println(count);
		    if(event.hasBank("BST::adc")&&count==40) {
		    	BST.fillBarrel(event.getBank("BST::adc"));
		    }
		    
		    if(event.hasBank("BMT::adc")&&count==40) {
		    	BMT.fillBarrel(event.getBank("BMT::adc"));
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