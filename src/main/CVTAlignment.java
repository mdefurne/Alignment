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
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		
		CVTAlignment CVTAli=new CVTAlignment();
				
		BMT.getGeometry().LoadMisalignmentFromFile(ConstantFile);
		
		Aligner Alignment=new Aligner();
		
		Alignment.DoAlignment(BMT, BST, reader, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		
	}
			 
	
	
}