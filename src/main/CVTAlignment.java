package main;

import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

import java.io.*;

import BMT_struct.Barrel;
import BST_struct.Barrel_SVT;


public class CVTAlignment {
	
	static Barrel BMT;
	static Barrel_SVT BST;
		
	public CVTAlignment() {
		BST=new Barrel_SVT();
		BMT=new Barrel();
	}
	
	public static void main(String[] args) throws IOException {
		
		//String ConstantFile=args[0];
		//int layer=Integer.parseInt(args[1]);
		//int sector=Integer.parseInt(args[2]);
		
		String ConstantFile="/home/mdefurne/Bureau/CLAS12/Test.txt";
		
		CVTAlignment CVTAli=new CVTAlignment();
				
		BMT.getGeometry().LoadMisalignmentFromFile(ConstantFile);
	}
			 
	
	
}