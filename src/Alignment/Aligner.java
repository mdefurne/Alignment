package Alignment;

import BMT_struct.*;
import BST_struct.*;
import java.util.*;
import Alignment.*;
import org.freehep.math.minuit.*;
import org.jlab.geom.prim.Vector3D;
import org.jlab.io.hipo.HipoDataSource;
import Trajectory.*;

public class Aligner {
		
	public Aligner() {
		
	}
			
	public void DoAlignment(Barrel BMT, Barrel_SVT BST, HipoDataSource reader, int layer, int sector) {
		//Use minimizer
										
				//Create parameters
				MnUserParameters upar = new MnUserParameters();
			
				upar.add("Rx", 0, Math.toRadians(10), Math.toRadians(-5), Math.toRadians(5));
				upar.add("Ry", 0, Math.toRadians(10), Math.toRadians(-5), Math.toRadians(5));
				upar.add("Rz", 0, Math.toRadians(10), Math.toRadians(-5), Math.toRadians(5));
				upar.add("Cx", 0, 2, -1, 1);
				upar.add("Cy", 0, 2, -1, 1);
				upar.add("Cz", 0, 2, -1, 1);
							  	    
			    //Create function to minimize
			    FCNChi2 Align=new FCNChi2();
			    
			    //Give clusters to Chi2 to compute distance
			    Align.SetDetectorToAlign(BMT,BST,reader,layer,sector);
			    
			 
			    //Create Minuit (parameters and function to minimize)
			    MnMigrad migrad = new MnMigrad(Align, upar);
			 			    
			    //Haven t checked if it is necessarry... might duplicate Straight to parameters for minimum
			    FunctionMinimum min = migrad.minimize();
			   
			    if (min.isValid()) {
			    	
			   	}
			    
			
	}

}
