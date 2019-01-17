package DC_struct;

import org.jlab.io.base.DataBank;

import eu.mihosoft.vrl.v3d.Vector3d;

import org.jlab.detector.geant4.v2.DCGeant4Factory;



public class DriftChambers {
	public Sector[] DCSector= new Sector[6];
	public org.jlab.detector.geant4.v2.DCGeant4Factory DCgeo;
	private int NbTotalHits;
	public DriftChambers(org.jlab.detector.geant4.v2.DCGeant4Factory DCFactory) {
		for (int sec=0; sec<6;sec++) {
			DCSector[sec]=new Sector((sec+1));
		}
		NbTotalHits=0;
		DCgeo=DCFactory;
		
	}
	
	@SuppressWarnings("static-access")
	public void fillDCs(DataBank pbank) {
		this.clear();
		
		for (int row=0;row<pbank.rows();row++){
			int layer= pbank.getByte("layer",row );
			int sector= pbank.getByte("sector",row );
			int strip= pbank.getShort("component",row );
			int TDC= pbank.getInt("TDC",row );
			Vector3d ptRight=DCgeo.getWireRightend(sector-1, (layer-1)/6, (layer-1)%6, strip-1);
			Vector3d ptLeft=DCgeo.getWireLeftend(sector-1, (layer-1)/6, (layer-1)%6, strip-1);
			Vector3d pt=DCgeo.getWireMidpoint(sector-1, (layer-1)/6, (layer-1)%6, strip-1);
			Vector3d dir=new Vector3d(ptRight.x-ptLeft.x,ptRight.y-ptLeft.y,ptRight.z-ptLeft.z);
			
			pt.rotateY(Math.toRadians(25));pt.rotateZ((sector-1)*Math.toRadians(60));
			dir.rotateY(Math.toRadians(25));dir.rotateZ((sector-1)*Math.toRadians(60));
			NbTotalHits++;
			
			DCSector[sector-1].getSuperLayer((layer-1)/6+1).getLayer((layer-1)%6+1).addWire(strip, TDC, dir, pt, DCSector[sector-1].getSuperLayer((layer-1)/6+1).getCellSize()/Math.sqrt(12));
		}
		
	}
	
	public void FindTrackCandidate() {
		for (int sec=0; sec<6;sec++) {
			DCSector[sec].MakeRaySegments();
		}
	}
	
	public Sector getSector(int sec) {
		return DCSector[sec-1];
	}
	
	public void clear() {
		NbTotalHits=0;
		for (int sec=0; sec<6;sec++) {
			DCSector[sec].clear();
		}
	}
	
	public int getNbHits() {
		return NbTotalHits;
	}
}
