package main;

import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;
import java.util.*;
import org.jlab.io.base.DataBank;


public class Efficiency {

	public static void main(String[] args) {
		
		int[] NumStrip= {896,640,640,1024,768,1152};
		
		int count=0;
		
		double[][] GotHit=new double[6][3];
		double[][] RefTrack=new double[6][3];
		for (int i=0;i<6;i++) {
			for (int j=0;j<3;j++) {
				GotHit[i][j]=0;
				RefTrack[i][j]=0;
			}
		}
		
		ArrayList<Short> good_track=new ArrayList<Short>(); //Store indexes for good tracks
		
		String fileName=args[0];
		
		HipoDataSource reader = new HipoDataSource();
		reader.open(fileName);
		
		while(reader.hasEvent()) {
			
			count++;
		  DataEvent event = reader.getNextEvent();
		  //Look if a track has been found
		  if(event.hasBank("CVTRec::Cosmics")&&event.hasBank("CVTRec::Trajectory")) {
			  DataBank Cbank=event.getBank("CVTRec::Cosmics");
			  DataBank Bbank=event.getBank("BMTRec::Clusters");
			  DataBank Tbank=event.getBank("CVTRec::Trajectory");
			  for (int Crow=0;Crow<Cbank.rows();Crow++){
					if (Cbank.getShort("NbBSTHits",Crow )>8&&Cbank.getShort("NbBMTHits",Crow )>3) {
						// Let's find the sector of the good track
						boolean[] LayerHit= {false,false,false,false,false,false};
						int sector=-1;
						int counter=0;
						while (sector==-1) {
							if (Bbank.getShort("trkID",counter)==Cbank.getShort("ID",Crow)) sector=Bbank.getByte("sector",counter);
							counter++;
						}
						
						//Now let us check the efficiency in the opposite sector giving the track 
						for (int Trow=0;Trow<Tbank.rows();Trow++) {
							//The intercept must belong to the track (might have several track)
							if (Cbank.getShort("ID",Crow)==Tbank.getShort("ID",Trow)) {
								//Need to not look at intercept in same sector than the track was found
								if (Tbank.getByte("LayerTrackIntersPlane",Trow)>=7&&sector!=Tbank.getByte("SectorTrackIntersPlane",Trow)) {
									
									if (Tbank.getFloat("CalcCentroidStrip",Trow)>25&&Tbank.getFloat("CalcCentroidStrip",Trow)<(NumStrip[Tbank.getByte("LayerTrackIntersPlane",Trow)-7]-25)) {
										// The track must intercept a layer in the opposite sector and not close to the edge (Need to add Phi condition for C and Z condition for Z-tile)
										// Need now to check if a cluster match
										// We are looking for a good hit
										RefTrack[Tbank.getByte("LayerTrackIntersPlane",Trow)-7][Tbank.getByte("SectorTrackIntersPlane",Trow)-1]++;
										
										for (int Brow=0;Brow<Bbank.rows();Brow++) {
											if (Math.abs(Bbank.getFloat("centroid",Brow)-Tbank.getFloat("CalcCentroidStrip",Trow))<20&&
													(Tbank.getByte("LayerTrackIntersPlane",Trow)-6)==Bbank.getByte("layer",Brow)&&
													Tbank.getByte("SectorTrackIntersPlane",Trow)==Bbank.getByte("sector",Brow)&&
													!LayerHit[Tbank.getByte("LayerTrackIntersPlane",Trow)-7]) {
													//On a trouve un bon hit dans la tuile, donc on marque la tuile pour pas compter deux fois si multiple cluster
													LayerHit[Tbank.getByte("LayerTrackIntersPlane",Trow)-7]=true;
													GotHit[Bbank.getByte("layer",Brow)-1][Bbank.getByte("sector",Brow)-1]++;
											}
										}
									}
								}
								
							}
						}
					}
			  }
			  			  
		  }
		}
		
		//Printout the results
		for (int i=0;i<6;i++) {
			for (int j=0;j<3;j++) {
				System.out.println("Layer "+(i+1)+" sector "+(j+1)+" Efficiency "+(GotHit[i][j]/RefTrack[i][j])+" with "+RefTrack[i][j]+" reference tracks");
			}
		}
	}
}
