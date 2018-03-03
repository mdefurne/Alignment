package BST_struct;

import java.util.*;
import java.util.stream.Collectors;
import org.jlab.io.base.DataBank;

import BST_struct.Hit;
import BST_struct.Cluster;
import BST_struct.Module;
import BST_geo.Geometry;


public class Barrel_SVT {
	
	Module[][] Modules=new Module[6][18]; 
	Geometry geo;
	
	public Barrel_SVT(Geometry BSTgeo){
		geo=BSTgeo;
		for (int lay=0; lay<6;lay++) {
			for (int sec=0; sec<3;sec++) {
				Modules[lay][sec]=new Module(lay+1,sec+1);
			}
		}
	}
	
	public void clear() {
		for (int lay=0; lay<6;lay++) {
			for (int sec=0; sec<3;sec++) {
				Modules[lay][sec].clear();
			}
		}
	}
	
	public Module getModule(int lay, int sec) {
		return Modules[lay][sec];
	}

}
