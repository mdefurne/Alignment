package main;

import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.detector.calib.utils.DatabaseConstantProvider;
import org.jlab.geom.prim.Point3D;
import org.jlab.io.hipo.HipoDataSource;
import BMT_struct.Barrel;
import TrackFinder.*;
import Analyzer.*;

public class alignement {
	BMT_geo.Geometry BMTGeom;
	static BST_geo.Geometry BSTGeom;
	static Barrel BMT;
	static Analyzer Holmes;
	
	public alignement() {
		BMTGeom = new BMT_geo.Geometry();
		BSTGeom = new BST_geo.Geometry();
		BMT=new Barrel(BMTGeom);
		Holmes=new Analyzer();
	}
	
	public boolean init() {
        System.out.println(" ........................................ trying to connect to db ");
        BMT_geo.CCDBConstantsLoader.Load(new DatabaseConstantProvider(10, "default"));
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
		while(reader.hasEvent()&&count<1) {
		    DataEvent event = reader.getNextEvent();
		    count++;
		    System.out.println(count);
		    	if(event.hasBank("BMT::adc")) {
		    		BMT.fillBarrel(event.getBank("BMT::adc"));
		    		TrackFinder tracky=new TrackFinder();
		    		tracky.BuildCandidates(BMT);
		    		tracky.FetchTrack();
		    		Holmes.analyze(BMT, BSTGeom, tracky.get_Candidates());
		    	}
		         
		}
		Holmes.draw();
		
		System.out.println("Done!");
 }
}