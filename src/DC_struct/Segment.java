package DC_struct;

import java.util.ArrayList;

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
	
	public void Merge(Segment ToMerge) {
		for (int cl=0;cl<ToMerge.getSize();cl++) {
			this.addCluster(ToMerge.getClusters().get(cl));
		}
		SuperLayer=ToMerge.getSuperLayer();
		nbSuperLayer++;
	}
	
	public int getNbLayer() {
		return nbLayer;
	}
	
	public int getNbSuperLayer() {
		return nbLayer;
	}
	
	public int getSuperLayer() {
		return SuperLayer;
	}
	
	public boolean isGoodSLSegment() {
		boolean good=false;
		if (this.nbLayer>=3&&(this.getClusters().get(0).getLayer()==1||this.getClusters().get(0).getLayer()==2)&&(this.getClusters().get(this.getSize()-1).getLayer()==5||this.getClusters().get(this.getSize()-1).getLayer()==6)) good=true;
		return good;
	}
}
