package Analyzer;

import org.jlab.groot.data.*;
import TrackFinder.*;
import org.jlab.groot.ui.TCanvas;
import BST_struct.*;
import org.jlab.geom.prim.Vector3D;


public class BSTAna {
	
	
	public BSTAna() {
		
	}
	
	public void analyze(Barrel_SVT BST, TrackCandidate cand) {
		
		if (cand.get_Nc()==3&&cand.get_Nz()>=2&&cand.get_chi2()<50) {
			System.out.println(cand.get_PointTrack());
			Vector3D inter=new Vector3D(BST.getGeometry().getIntersectWithRay(1, cand.get_VectorTrack(), cand.get_PointTrack()));
			System.out.println(BST.getGeometry().calcNearestStrip(inter.x(), inter.y(), inter.z(), 1, BST.getGeometry().findSectorFromAngle(1, inter)));
		}
	}

}
