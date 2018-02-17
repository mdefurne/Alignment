package BMT_struct;

import java.util.*;
import java.util.stream.Collectors;

import BMT_struct.Hit;
import BMT_struct.Cluster;

public class Tile {
	
	HashMap<Integer, Hit> hitmap;
	HashMap<Integer, Hit> sorted_hitmap;
	HashMap<Integer, Cluster> clustermap;
	int layer_id;
	int sector_id;
	
	public Tile() {
		layer_id=0;
		sector_id=0;
		hitmap = new HashMap<Integer, Hit>();
		sorted_hitmap = new HashMap<Integer, Hit>();
		clustermap = new HashMap<Integer, Cluster>();
	}
	
	public Tile(int layer, int sector) {
		layer_id=layer;
		sector_id=sector;
		hitmap = new HashMap<Integer, Hit>();
		sorted_hitmap = new HashMap<Integer, Hit>();
		clustermap = new HashMap<Integer, Cluster>();
	}
	
	public void addHit(int strip, double radius, double phi, double z, int adc, float time) {
		Hit aHit=new Hit(radius, phi, z, adc, time);
		hitmap.put(strip, aHit);
	}
	
	public void SortHitmap() {
		sorted_hitmap.clear();
		hitmap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> sorted_hitmap.put(x.getKey(), x.getValue()));
	}
	
	public void DoClustering() {
		SortHitmap();
		int num_hit=sorted_hitmap.size();
		if (num_hit!=0) {
			Set set = sorted_hitmap.entrySet();
		    Iterator iterator = set.iterator();
		    while(iterator.hasNext()) {
		    	Map.Entry me = (Map.Entry)iterator.next();
		    	if (clustermap.size()==0) {
		    		Cluster clus=new Cluster();
		    		System.out.println(me.getKey());
		    		//Cluster.add(me.getKey(),sorted_hitmap.get(me.getKey()));
		    		clustermap.put(clustermap.size()+1,clus);
		    	}	
		    	
			}
		}
		else System.out.println("No Hit recorded in the tile sector "+sector_id+" Layer "+layer_id);
	}
	
	public void clear() {
		sorted_hitmap.clear();
		hitmap.clear();
		clustermap.clear();
	}

}
