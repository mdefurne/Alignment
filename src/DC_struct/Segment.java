package DC_struct;

import java.util.ArrayList;

import Trajectory.StraightLine;

public class Segment {
	
	private ArrayList<DC_struct.Cluster> clusterlist;
	private int nbSuperLayer;
	private int nbLayer;
	private int SuperLayer;
	private int region;
	
	public Segment(int Super_Layer) {
		clusterlist=new ArrayList<DC_struct.Cluster>();
		nbSuperLayer=0;
		nbLayer=0;
		SuperLayer=Super_Layer;
		region=(SuperLayer+1)/2;
	}
	
	public int getRegion() {
		return region;
	}
	
	public void addCluster(Cluster clus) {
		clusterlist.add(clus);
		clusterlist.get(clusterlist.size()-1).setLayerInSector(clus.getLayer()+6*(SuperLayer-1));
		nbLayer++;
	}
	
	public double getLastCentroid() {
		return clusterlist.get(clusterlist.size()-1).getCentroid();
	}
	
	public double getFirstCentroid() {
		return clusterlist.get(0).getCentroid();
	}
	
	public int getSize() {
		return clusterlist.size();
	}
	
	public int getLayerLastEntry() {
		return clusterlist.get(clusterlist.size()-1).getLayer();
	}
	
	public ArrayList<DC_struct.Cluster> getClusters(){
		return clusterlist;
	}
	
	public Segment Duplicate() {
		Segment Dupli=new Segment(SuperLayer);
		for (int cl=0;cl<this.getClusters().size(); cl++) {
			Dupli.addCluster(this.getClusters().get(cl));
		}
		return Dupli;
	}
	
	public Segment Merge(Segment ToMerge) {
		Segment Merged=new Segment(ToMerge.getSuperLayer());
		for (int cl=0;cl<this.getSize();cl++) {
			Merged.addCluster(this.getClusters().get(cl));
		}
		for (int cl=0;cl<ToMerge.getSize();cl++) {
			Merged.addCluster(ToMerge.getClusters().get(cl));
		}
		Merged.setNbSuperLayer(this.getNbSuperLayer()+ToMerge.getNbSuperLayer());
		return Merged;
	}
	
	public int getNbLayer() {
		return nbLayer;
	}
	
	public int getNbSuperLayer() {
		return nbSuperLayer;
	}
	
	
	public void setNbSuperLayer(int nSL) {
		nbSuperLayer=nSL;
	}
	public int getSuperLayer() {
		return SuperLayer;
	}
	
	public boolean isGoodSLSegment() {
		boolean good=false;
		if (this.nbLayer>=3&&(this.getClusters().get(0).getLayer()==6||this.getClusters().get(0).getLayer()==5)&&(this.getClusters().get(this.getSize()-1).getLayer()==1||this.getClusters().get(this.getSize()-1).getLayer()==2)) good=true;
		return good;
	}
	
	public boolean isGoodSectorSegment() {
		boolean good=false;
		if (this.nbSuperLayer==6) good=true;
		return good;
	}
	
	public void PrintSegment() {
		for (int clus=0;clus<clusterlist.size();clus++) {
			System.out.println(clusterlist.get(clus).getLayerInSector()+" "+clusterlist.get(clus).getCentroid()+" "+clusterlist.get(clus).getWires().get(0).getWirePoint().x+" "+clusterlist.get(clus).getWires().get(0).getWirePoint().y);
		}
	}

	public double ComputeChi2(StraightLine line) {
		double chi2=0;
		for (int clus=0;clus<this.getSize();clus++) {
			if (this.getClusters().get(clus).getSize()<3) {
				for (int wire=0;wire<this.getClusters().get(clus).getSize();wire++) {
					chi2+=this.getClusters().get(clus).getWires().get(wire).getWire().getDistanceToLine(line);
				}
			}
		}
		return chi2;
	}
}
