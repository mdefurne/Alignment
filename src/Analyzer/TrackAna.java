package Analyzer;

import org.jlab.groot.data.*;
import TrackFinder.*;
import org.jlab.groot.ui.TCanvas;

public class TrackAna {
	H1F Theta_track;
	H1F Phi_track;
	H1F Chi2_track;
	H1F[][] Z_residual=new H1F[3][3];
	H1F[][] C_residual=new H1F[3][3];
	
	public TrackAna() {
		Theta_track=new H1F("Theta angle of track","Theta angle for track",90,0,180);
		Phi_track=new H1F("Phi angle of track","Phi angle for track",90,-180,180);
		Chi2_track=new H1F("Chi2 of track","Chi2 angle for track",90,0,100);
		for (int lay=0;lay<3;lay++) {
			for (int sec=0;sec<3;sec++) {
				Z_residual[lay][sec]=new H1F("Residuals for Z-tile L"+(lay+1)+" S"+(sec+1)+" in mm","Residuals for Z-tile L"+(lay+1)+" S"+(sec+1)+" in mm",100,-1,1);
				C_residual[lay][sec]=new H1F("Residuals for C-tile L"+(lay+1)+" S"+(sec+1)+" in mm","Residuals for C-tile L"+(lay+1)+" S"+(sec+1)+" in mm",100,-1,1);
			}
		}
	}
	
	public void analyze(TrackCandidate cand) {
		
		if (cand.get_FitStatus()) {
			Theta_track.fill(Math.toDegrees(Math.acos(cand.get_VectorTrack().z())));
			Phi_track.fill(Math.toDegrees(Math.atan2(cand.get_VectorTrack().y(),cand.get_VectorTrack().x())));
			if (cand.get_Nc()==3&&cand.get_Nz()==3) Chi2_track.fill(cand.get_chi2());
			for (int clus=0; clus<cand.size();clus++) {
				
				if (cand.get_Nc()==3&&cand.IsVeryGoodCandidate()) {
					//System.out.println(clus+" "+cand.GetCluster(clus).getLayer());
				if (cand.get_Nz()==3&&(cand.GetBMTCluster(clus).getLayer()==2||cand.GetBMTCluster(clus).getLayer()==3||cand.GetBMTCluster(clus).getLayer()==5)) {
					Z_residual[(cand.GetBMTCluster(clus).getLayer()-1)/2][cand.GetBMTCluster(clus).getSector()-1].fill(cand.getResidual(clus));
					}
				if (cand.get_Nc()==3&&(cand.GetBMTCluster(clus).getLayer()==1||cand.GetBMTCluster(clus).getLayer()==4||cand.GetBMTCluster(clus).getLayer()==6)) {
					C_residual[(cand.GetBMTCluster(clus).getLayer()-1)/2][cand.GetBMTCluster(clus).getSector()-1].fill(cand.getResidual(clus));
				
					}
				}
			}
		}
	}
	
	public void draw() {
		 TCanvas theta = new TCanvas("theta", 1100, 700);
		 theta.draw(Theta_track);
		 TCanvas phi = new TCanvas("phi", 1100, 700);
		 phi.draw(Phi_track);
		 TCanvas z_res = new TCanvas("Z layers", 1100, 700);
		 z_res.divide(3, 3);
		 TCanvas c_res = new TCanvas("C_layers", 1100, 700);
		 c_res.divide(3, 3);
		 for (int lay=0;lay<3;lay++) {
				for (int sec=0;sec<3;sec++) {
					c_res.cd(3*lay+sec);
					c_res.draw(C_residual[lay][sec]);
					z_res.cd(3*lay+sec);
					z_res.draw(Z_residual[lay][sec]);
				}
		 }
	}
}
