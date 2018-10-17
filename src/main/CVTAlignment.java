package main;

import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import java.io.*;

import BMT_struct.Barrel;
import BST_struct.Barrel_SVT;
import Alignment.Aligner;


public class CVTAlignment {
	
	static Barrel BMT;
	static Barrel_SVT BST;
		
	public CVTAlignment() {
		BST=new Barrel_SVT();
		BMT=new Barrel();
	}
	
	public static void main(String[] args) throws IOException {
		
		String ConstantFile="/home/mdefurne/Bureau/CLAS12/Test.txt";
		String fileName=args[0];
		HipoDataSource reader=new HipoDataSource();
		reader.open(fileName);
				
		CVTAlignment CVTAli=new CVTAlignment();
				
		BMT.getGeometry().LoadMisalignmentFromFile(ConstantFile);
		
		Aligner Alignment=new Aligner();
		
		int layer=Integer.parseInt(args[1]);
		int sector=Integer.parseInt(args[2]);
		
		Alignment.DoAlignment(BMT, BST, reader, layer, sector);
		if (layer>6) System.out.println(BMT_geo.Constants.getRx(layer-6, sector)+" "+BMT_geo.Constants.getRy(layer-6, sector)+" "+BMT_geo.Constants.getRz(layer-6, sector)+" "+
				BMT_geo.Constants.getCx(layer-6, sector)+" "+BMT_geo.Constants.getCy(layer-6, sector)+" "+BMT_geo.Constants.getCz(layer-6, sector));
		else System.out.println(BST.getGeometry().getRx(layer-6, sector)+" "+BST.getGeometry().getRy(layer-6, sector)+" "+BST.getGeometry().getRz(layer-6, sector)+" "+
				BST.getGeometry().getCx(layer-6, sector)+" "+BST.getGeometry().getCy(layer-6, sector)+" "+BST.getGeometry().getCz(layer-6, sector));
	}
			 
	
	
}