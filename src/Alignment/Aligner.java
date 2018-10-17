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
				upar.add("Cx", 0, 6, -3, 3);
				upar.add("Cy", 0, 6, -3, 3);
				upar.add("Cz", 0, 6, -3, 3);
				if (BMT.getGeometry().getZorC(layer-6)==1) upar.fix("Cz");
				if (BMT.getGeometry().getZorC(layer-6)==0) upar.fix("Rz");
							  	    
			    //Create function to minimize
			    FCNChi2 Align=new FCNChi2();
			    
			    //Give clusters to Chi2 to compute distance
			    Align.SetDetectorToAlign(BMT,BST,reader,layer,sector);
			    
			 
			    //Create Minuit (parameters and function to minimize)
			    MnMigrad migrad = new MnMigrad(Align, upar);
			 			    
			    //Haven t checked if it is necessarry... might duplicate Straight to parameters for minimum
			    FunctionMinimum min = migrad.minimize();
			    
			    if (min.isValid()) {
			    	double[] res=migrad.params(); //res[0] and res[1] are phi and theta for vec, res[2] is phi for intersection point on cylinder and  res[3] is z_inter
			    	double[] err_res=new double[migrad.covariance().nrow()];
			    	for (int i=0;i<migrad.covariance().nrow();i++) {
			    		err_res[i]=Math.sqrt(migrad.covariance().get(i, i));
			    	}
			    	if (layer>6) {
			    		BMT_geo.Constants.setRx(layer-6, sector, res[0]);
			    		BMT_geo.Constants.setRy(layer-6, sector, res[1]);
			    		BMT_geo.Constants.setRz(layer-6, sector, res[2]);
			    		BMT_geo.Constants.setCx(layer-6, sector, res[3]);
			    		BMT_geo.Constants.setCy(layer-6, sector, res[4]);
			    		BMT_geo.Constants.setCz(layer-6, sector, res[5]);
			    	}
			    	else{
			    		BST.getGeometry().setRx(layer, sector, res[0]);
			    		BST.getGeometry().setRy(layer, sector, res[1]);
			    		BST.getGeometry().setRz(layer, sector, res[2]);
			    		BST.getGeometry().setCx(layer, sector, res[3]);
			    		BST.getGeometry().setCy(layer, sector, res[4]);
			    		BST.getGeometry().setCz(layer, sector, res[5]);
			    	}
			   	}
			    
			
	}

}
