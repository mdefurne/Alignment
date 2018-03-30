package Analyzer;

import org.jlab.groot.data.*;
import TrackFinder.*;
import org.jlab.groot.ui.*;
import java.util.*;
import Trajectory.StraightLine;

public class BeamAna {
	
	H1F vz;
	H1F vy;
	H1F vx;
	
	H2F xy_beam;
	H1F xz_beam;
	H1F yz_beam;
	
	public BeamAna() {
		
		vz=new H1F("Z-vertex","Z-vertex",150,-50,50);
		vy=new H1F("Y-vertex","Y-vertex",150,-25,25);
		vx=new H1F("X-vertex","X-vertex",150,-25,25);
		
		xz_beam=new H1F("xz_beam angle (degrees)","xz_beam angle (degrees)",50,-10,10);
		yz_beam=new H1F("yz_beam angle (degrees)","yz_beam angle (degrees)",50,-10,10);
		
		xy_beam=new H2F("Beam position in z=0",50,-5,5,50,-5,5);
	}
	
	public void Analyze(StraightLine Beam, HashMap<Integer, ArrayList<TrackCandidate> > Events) {
		
		for (int i=0; i<Events.size();i++) {
			 for (int j=0; j<Events.get(i+1).size();j++) {
				if (Events.get(i+1).get(j).IsFromTarget()) {
					vz.fill(Events.get(i+1).get(j).getVertex().z());
					vy.fill(Events.get(i+1).get(j).getVertex().y());
					vx.fill(Events.get(i+1).get(j).getVertex().x());
					
					xy_beam.fill(Beam.getPoint().x(), Beam.getPoint().y());
					xz_beam.fill(Math.toDegrees(Math.atan(Beam.getSlope().x())));
					yz_beam.fill(Math.toDegrees(Math.atan(Beam.getSlope().y())));
				}
			 }
		}
	}
	
	public void draw() {
		 TCanvas BeamViewer = new TCanvas("Beam Viewer", 1100, 700);
		 BeamViewer.divide(3, 1);
		 BeamViewer.cd(0);
		 BeamViewer.draw(xy_beam);
		 BeamViewer.cd(1);
		 BeamViewer.draw(xz_beam);
		 BeamViewer.cd(2);
		 BeamViewer.draw(yz_beam);
		 
		 TCanvas TargetViewer = new TCanvas("Target Viewer", 1100, 700);
		 TargetViewer.divide(1, 3);
		 TargetViewer.cd(0);
		 TargetViewer.draw(vx);
		 TargetViewer.cd(1);
		 TargetViewer.draw(vy);
		 TargetViewer.cd(2);
		 TargetViewer.draw(vz);
	}

}
