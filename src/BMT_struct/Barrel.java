package BMT_struct;

import java.util.*;
import java.util.stream.Collectors;
import org.jlab.io.base.DataBank;

import BMT_struct.Hit;
import BMT_struct.Cluster;
import BMT_struct.Tile;
import BMT_geo.Geometry;

public class Barrel {
	Tile[][] Tiles=new Tile[6][3]; 
	Geometry geo;
	
	public Barrel(Geometry BMTgeo){
		geo=BMTgeo;
		for (int lay=0; lay<6;lay++) {
			for (int sec=0; sec<3;sec++) {
				Tiles[lay][sec]=new Tile(lay+1,sec+1);
			}
		}
	}
	
	public void clear() {
		for (int lay=0; lay<6;lay++) {
			for (int sec=0; sec<3;sec++) {
				Tiles[lay][sec].clear();
			}
		}
	}
	
	public void MakeCluster() {
		for (int lay=0; lay<6;lay++) {
			for (int sec=0; sec<3;sec++) {
				Tiles[lay][sec].DoClustering();
			}
		}
	}
	
	public void fillBarrel(DataBank pbank) {
		clear();
		for (int row=0;row<pbank.rows();row++){
			int layer= pbank.getByte("layer",row );
			int sector= pbank.getByte("sector",row );
			int strip= pbank.getShort("component",row );
			int ADC= pbank.getInt("ADC",row );
			float time= pbank.getFloat("time",row );
			
			if (geo.getZorC(layer)==1) 
				Tiles[layer-1][sector-1].addHit(strip, geo.getRadius(layer) , geo.CRZStrip_GetPhi(sector, layer, strip), Double.NaN, ADC, time);
			if (geo.getZorC(layer)==0) 
				Tiles[layer-1][sector-1].addHit(strip, geo.getRadius(layer) , Double.NaN, geo.CRCStrip_GetZ(layer, strip), ADC, time);
		}
		MakeCluster();
		
	}
	
	
	
}
