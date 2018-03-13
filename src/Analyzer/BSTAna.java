package Analyzer;

import org.jlab.groot.data.*;
import TrackFinder.*;
import org.jlab.groot.ui.TCanvas;
import BST_struct.*;
import org.jlab.geom.prim.Vector3D;


public class BSTAna {
	H1F[][] SVT_residual=new H1F[6][18];
	H2F[][] residual_vs_z=new H2F[6][18];
	
	public BSTAna() {
		for (int lay=0; lay<6;lay++) {
			for (int sec=0; sec<18;sec++) {
				SVT_residual[lay][sec]=new H1F("Residuals for L"+(lay+1)+" S"+(sec+1)+" in mm","Residuals for L"+(lay+1)+" S"+(sec+1)+" in mm",200,-2,2);
				residual_vs_z[lay][sec]=new H2F("Residuals for L"+(lay+1)+" S"+(sec+1)+" in mm","Residuals for L"+(lay+1)+" S"+(sec+1)+" in mm",28,-100, 180, 10,-1,1);
			}
		}
	}
	
	public void analyze(Barrel_SVT BST, TrackCandidate cand) {
		
		if (cand.IsGoodCandidate()&&cand.get_FitStatus()) {
			for (int lay=1;lay<7;lay++) {
					Vector3D inter=new Vector3D(BST.getGeometry().getIntersectWithRay(lay, cand.get_VectorTrack(), cand.get_PointTrack()));
					if (!Double.isNaN(inter.x())) {
					int sec=BST.getGeometry().findSectorFromAngle(lay, inter);
					double strip=BST.getGeometry().calcNearestStrip(inter.x(), inter.y(), inter.z(), lay, BST.getGeometry().findSectorFromAngle(lay, inter));
					double ClosestStrip=-20;
					for (int clus=0;clus<BST.getModule(lay, sec).getClusters().size();clus++) {
						//System.out.println(BST.getModule(lay, sec).getClusters().size()+" ");
						if (Math.abs(BST.getModule(lay, sec).getClusters().get(clus+1).getCentroid()-strip)<Math.abs(ClosestStrip-strip)) ClosestStrip=BST.getModule(lay, sec).getClusters().get(clus+1).getCentroid();
					}
					double residual=BST.getGeometry().getResidual_line(lay, BST.getGeometry().findSectorFromAngle(lay, inter), (int)ClosestStrip, inter);
					SVT_residual[lay-1][sec-1].fill(residual);
					residual_vs_z[lay-1][sec-1].fill(inter.z(),residual);
				}
				//System.out.println(BST.getGeometry().getResidual(1, BST.getGeometry().findSectorFromAngle(1, inter), 63, inter));
			}
		}
	}
	
	public void draw() {
		 TCanvas[] residual = new TCanvas[6];
		 for (int lay=0;lay<6;lay++) {
		 residual[lay]= new TCanvas("Residual for layer "+(lay+1), 1100, 700);
		 residual[lay].divide(4, 5);
		 for (int sec=0;sec<18;sec++) {
					residual[lay].cd(sec);
					//residual[lay].draw(SVT_residual[lay][sec]);
					residual[lay].draw(residual_vs_z[lay][sec]);
					
		 	}
		 }
	}

}
