package Analyzer;

import org.jlab.groot.data.*;
import TrackFinder.*;
import org.jlab.groot.ui.TCanvas;
import java.lang.Object;

import javax.swing.JFrame;

public class ClusterAna {
	
	H1F[] Size=new H1F[6];
	H1F[] Edep=new H1F[6];
	H1F[] Edep_strip=new H1F[6];
	H2F[] Timewalk=new H2F[6];
	H1F[] T_min=new H1F[6];
	H2F[][] Z_size=new H2F[3][3];
	H2F[][] C_size=new H2F[3][3];
	H2F[][] Z_Edep=new H2F[3][3];
	H2F[][] C_Edep=new H2F[3][3];
	H2F[][] Z_Time=new H2F[3][3];
	H2F[][] C_Time=new H2F[3][3];
	
	TCanvas cZ_size;
	TCanvas cC_size;
		
	public ClusterAna() {
		
		for (int lay=0;lay<3;lay++) {
			for (int sec=0;sec<3;sec++) {
				Z_size[lay][sec]=new H2F("Size as a function of Phi Track "+(lay+1)+" S"+(sec+1)+" in mm",50,0,40,15,1,16);
				C_size[lay][sec]=new H2F("Size as a function of Theta Track "+(lay+1)+" S"+(sec+1)+" in mm",30,0,60,15,1,16);
				Z_Edep[lay][sec]=new H2F("ADC sum as a function of Phi Track "+(lay+1)+" S"+(sec+1)+" in mm",50,0,40,100,1,4000);
				C_Edep[lay][sec]=new H2F("ADC sum as a function of Phi Track "+(lay+1)+" S"+(sec+1)+" in mm",30,0,60,100,1,4000);
				Z_Time[lay][sec]=new H2F("Time Spread as a function of Phi Track "+(lay+1)+" S"+(sec+1)+" in mm",50,0,240,15,1,16);
				C_Time[lay][sec]=new H2F("Time Spread as a function of Theta Track "+(lay+1)+" S"+(sec+1)+" in mm",30,0,240,15,1,16);
			}
		}
		
		for (int lay=0;lay<6;lay++) {
			Size[lay]=new H1F("Cluster size","Cluster size",30,0,30);
			if (main.constant.isCosmic) T_min[lay]=new H1F("T_min of track clusters "+(lay+1),"T_min of Track clusters "+(lay+1),48,0,640);
			if (!main.constant.isCosmic) T_min[lay]=new H1F("T_min of track clusters "+(lay+1),"T_min of Track clusters "+(lay+1),48,0,480);
			Timewalk[lay]=new H2F("Time difference between first and last strip in a cluster","Time difference between first and last strip in a cluster",40,0,40,30,0,240);
			Edep[lay]=new H1F("Total deposited energy","Total deposited energy",30,0,30);
			if (main.constant.isMC) {
				Edep_strip[lay]=new H1F("Total deposited energy","Total deposited energy",1000,0,4096);
				Edep[lay]=new H1F("Total deposited energy","Total deposited energy",1000,0,30000);
			}
			if (!main.constant.isMC) {
				Edep_strip[lay]=new H1F("Total deposited energy","Total deposited energy",30,0,100);
				Edep[lay]=new H1F("Total deposited energy","Total deposited energy",100,0,100);
			}
		}
	}
	
	public void analyze(TrackCandidate cand) {
		
		for (int clus=0; clus<cand.size();clus++) {
			int lay=cand.GetBMTCluster(clus).getLayer()-1;
			int sec=cand.GetBMTCluster(clus).getSector()-1;
			Size[lay].fill(cand.GetBMTCluster(clus).getSize());
			Edep[lay].fill(cand.GetBMTCluster(clus).getEdep());
			if (lay==1||lay==2||lay==4) Timewalk[lay].fill(cand.GetBMTCluster(clus).getTrackPhiAngle(),cand.GetBMTCluster(clus).getT_max()-cand.GetBMTCluster(clus).getT_min());
			if (lay==0||lay==3||lay==5) Timewalk[lay].fill(cand.GetBMTCluster(clus).getTrackThetaAngle(),cand.GetBMTCluster(clus).getT_max()-cand.GetBMTCluster(clus).getT_min());
			T_min[lay].fill(cand.GetBMTCluster(clus).getT_min());
			Z_size[(lay-1)/2][sec].fill(cand.GetBMTCluster(clus).getTrackPhiAngle(),cand.GetBMTCluster(clus).getSize());
			C_size[(lay-1)/2][sec].fill(cand.GetBMTCluster(clus).getTrackThetaAngle(),cand.GetBMTCluster(clus).getSize());
			Z_Edep[(lay-1)/2][sec].fill(cand.GetBMTCluster(clus).getTrackPhiAngle(),cand.GetBMTCluster(clus).getEdep());
			C_Edep[(lay-1)/2][sec].fill(cand.GetBMTCluster(clus).getTrackThetaAngle(),cand.GetBMTCluster(clus).getEdep());
			Z_Time[(lay-1)/2][sec].fill(cand.GetBMTCluster(clus).getTrackPhiAngle(),cand.GetBMTCluster(clus).getT_max()-cand.GetBMTCluster(clus).getT_min());
			C_Time[(lay-1)/2][sec].fill(cand.GetBMTCluster(clus).getTrackThetaAngle(),cand.GetBMTCluster(clus).getT_max()-cand.GetBMTCluster(clus).getT_min());
		}
	}
	
	public void draw() {
		if (true) {
			cZ_size = new TCanvas("Edep versus Phi for Z", 1100, 700);
			cZ_size.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			cZ_size.divide(3, 3);
			cC_size = new TCanvas("Edep versus Phi for C", 1100, 700);
			cC_size.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			cC_size.divide(3, 3);
			for (int lay=0;lay<3;lay++) {
				for (int sec=0;sec<3;sec++) {
					cZ_size.cd(3*lay+sec);
					cZ_size.draw(Z_Edep[lay][sec]);
					cC_size.cd(3*lay+sec);
					cC_size.draw(C_Edep[lay][sec]);
					}
			}
		}
		TCanvas cTmin = new TCanvas("tmin distribution", 1100, 700);
		cTmin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		cTmin.divide(2,3);
		for (int lay=0;lay<6;lay++) {
			cTmin.cd(lay);
			cTmin.draw(T_min[lay]);
			
		}
		
		TCanvas cWalk = new TCanvas("TimeWalk distribution", 1100, 700);
		cWalk.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		cWalk.divide(2,3);
		for (int lay=0;lay<6;lay++) {
			cWalk.cd(lay);
			cWalk.draw(Timewalk[lay]);
		}
		
	}

}
