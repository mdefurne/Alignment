package Analyzer;

import org.jlab.groot.data.*;
import TrackFinder.*;
import org.jlab.groot.ui.TCanvas;
import BST_struct.*;
import org.jlab.geom.prim.Point3D;

public class BSTAna {
	
	
	public BSTAna() {
		
	}
	
	public void analyze(Barrel_SVT BST, TrackCandidate cand) {
		Point3D pointtr=new Point3D();
		if (cand.get_Nc()==3&&cand.get_Nz()>=2&&cand.get_chi2()<50) {
			cand.get_VectorTrack(); 
			pointtr.set(cand.get_PointTrack().x(), cand.get_PointTrack().y(), cand.get_PointTrack().z()); 
			BST.getGeometry().findSectorFromAngle(1,pointtr);
		}
	}

}
