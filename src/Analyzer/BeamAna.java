package Analyzer;

import org.jlab.groot.data.*;
import TrackFinder.*;
import org.jlab.groot.ui.TCanvas;
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
		
		vz=new H1F("Z-vertex","Z-vertex",100,-50,50);
		vy=new H1F("Y-vertex","Y-vertex",50,-25,25);
		vx=new H1F("X-vertex","X-vertex",50,-25,25);
		
		xz_beam=new H1F("xz_beam angle (degrees)","xz_beam angle (degrees)",50,-10,10);
		yz_beam=new H1F("yz_beam angle (degrees)","yz_beam angle (degrees)",50,-10,10);
		
		xy_beam=new H2F("Beam position in z=0",50,-25,25,50,-25,25);
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
		
	}

}
