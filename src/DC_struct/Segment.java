package DC_struct;

import java.util.ArrayList;

public class Segment {
	
	private ArrayList<DC_struct.Cluster> clusterlist;
	
	public Segment() {
		clusterlist=new ArrayList<DC_struct.Cluster>();
	}
	
	public void addCluster(Cluster clus) {
		clusterlist.add(clus);
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
	
}
