package BMT_struct;

import java.util.*;
import java.util.stream.Collectors;

import BMT_struct.Hit;
import BMT_struct.Cluster;

public class Tile {
	
	HashMap<Integer, Hit> hitmap;
	TreeMap<Integer, Hit> sorted_hitmap;
	HashMap<Integer, Cluster> clustermap;
	int layer_id;
	int sector_id;
	
	public Tile() {
		layer_id=0;
		sector_id=0;
		hitmap = new HashMap<Integer, Hit>();
		sorted_hitmap = new TreeMap<Integer, Hit>();
		clustermap = new HashMap<Integer, Cluster>();
	}
	
	public Tile(int layer, int sector) {
		layer_id=layer;
		sector_id=sector;
		hitmap = new HashMap<Integer, Hit>();
		sorted_hitmap = new TreeMap<Integer, Hit>();
		clustermap = new HashMap<Integer, Cluster>();
	}
	
	public void addHit(int strip, double radius, double phi, double z, int adc, float time) {
		Hit aHit=new Hit(radius, phi, z, adc, time);
		hitmap.put(strip, aHit);
	}
	
	public void SortHitmap() {
		sorted_hitmap.clear();
		sorted_hitmap.putAll(hitmap);
	}
	
	public void DoClustering() {
		SortHitmap();
		int num_hit=sorted_hitmap.size();
		int last_hit=-3;
		float last_time=-1000;
		if (num_hit!=0) {
			for(HashMap.Entry<Integer,Hit> m:sorted_hitmap.entrySet()) {
		    	if (clustermap.size()!=0) {
		    		last_hit=clustermap.get(clustermap.size()).getLastEntry();
		    		last_time=hitmap.get(last_hit).getTime();
		    	}	
		       	if ((m.getKey()-last_hit>2)||Math.abs(sorted_hitmap.get(m.getKey()).getTime()-last_time)>50) {
		    		if (clustermap.size()!=0) clustermap.get(clustermap.size()).ComputeProperties();
		    		Cluster clus=new Cluster();
		    		clus.add(m.getKey(),sorted_hitmap.get(m.getKey()));
		    		clustermap.put(clustermap.size()+1,clus);
		    	}
		    	if ((m.getKey()-last_hit<=2)&&Math.abs(sorted_hitmap.get(m.getKey()).getTime()-last_time)<=50) {
		    		clustermap.get(clustermap.size()).add(m.getKey(),sorted_hitmap.get(m.getKey()));
		    	}
			}
		}
		if (clustermap.size()!=0) clustermap.get(clustermap.size()).ComputeProperties();
			//if (num_hit==0) System.out.println("No Hit recorded in the tile sector "+sector_id+" Layer "+layer_id);
	}
	
	public void clear() {
		sorted_hitmap.clear();
		hitmap.clear();
		clustermap.clear();
	}
	
	public HashMap<Integer, Cluster> getClusters(){
		return clustermap;
	}
}


