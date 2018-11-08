package DC_struct;

import java.util.ArrayList;
import java.util.HashMap;

public class SuperLayer {
	//0 is upstream SL, and 1 downstream
	public int region;
	public int SuperLayer;
	public Layer[] StackLayer= new Layer[6];
	ArrayList<Segment> segmap;
	public double DeltaCluster; //Authorized to associate to layer from one to the next if DeltaCluster<1.5
	
	ArrayList<Segment> BufferLayer;
	
	public SuperLayer(int SL) {
		region=(SL-1)/2+1;
		SuperLayer=SL;
		segmap = new ArrayList<Segment>();
		BufferLayer = new ArrayList<Segment>();
		
		for (int lay=0; lay<6;lay++) {
			StackLayer[lay]=new Layer(lay+1);
			
		}
		DeltaCluster=1.5;
	}
	
	public Layer getLayer(int lay) {
		return StackLayer[lay-1];
	}
	
	public int getSuperLayerNbHits() {
		int nHit=0;
		for (int i=0; i<6;i++) {
			nHit+=StackLayer[i].getNbLayerHit();
		}
		return nHit;
	}
	
	public void MakeSegment() {
		boolean IsAttributed=true;
		boolean noHit_yet_SL=true;
		int cand_newlay=0;
		
		for (int lay=6; lay>0;lay--) {
			
			if (this.getLayer(lay).getClusterList().size()<7) {
				//If we have already some hit in the sector, there are track candidate to check
				cand_newlay=segmap.size();
				if (!noHit_yet_SL) {
					for (int clus=0;clus<this.getLayer(lay).getClusterList().size();clus++) {
						//Here we always test if we have a match by time
						IsAttributed=false;
						for (int num_seg=cand_newlay;num_seg<segmap.size();num_seg++) {
							//If we have a match in time and will add a new layer
							if (!this.IsCompatible(this.getLayer(lay).getClusterList().get(clus+1),segmap.get(num_seg+1))) {
								Segment seg=segmap.get(num_seg+1).Duplicate();
								//if (this.IsCompatible(get(clus+1),cand)) { /// Commented because it might be useful to deal with time info
									seg.addCluster(this.getLayer(lay).getClusterList().get(clus+1));
									BufferLayer.add(seg);
									IsAttributed=true;
								//}
							}
					
							if (this.IsCompatible(this.getLayer(lay).getClusterList().get(clus+1),segmap.get(num_seg+1))) {
								segmap.get(num_seg+1).addCluster(this.getLayer(lay).getClusterList().get(clus+1));
								IsAttributed=true;
							}
					
						}
				
						if (!IsAttributed) {
							Segment seg=new Segment(SuperLayer);
							seg.addCluster(this.getLayer(lay).getClusterList().get(clus+1));
							segmap.add(seg);
						}
				
					}
					for (int buf=0;buf<BufferLayer.size();buf++) {
						segmap.add(BufferLayer.get(buf));
					}
					BufferLayer.clear();
				}	
	
				//We just enter the sector
				if (noHit_yet_SL) {
					//Create a new Track Candidate for each cluster of first layer
					for (int clus=0;clus<this.getLayer(lay).getClusterList().size();clus++) {
						Segment cand=new Segment(SuperLayer);
						cand.addCluster(this.getLayer(lay).getClusterList().get(clus+1));
						segmap.add(cand);
						noHit_yet_SL=false;
					}
			
				}
			}
		
	
		}
		
	}
	
	public boolean IsCompatible(DC_struct.Cluster clus,Segment seg) {
		boolean Compatible=true;
		if (Math.abs(clus.getLayer()-seg.getLayerLastEntry())>2) Compatible=false;
		if (Math.abs(clus.getCentroid()-seg.getLastCentroid())>DeltaCluster*Math.abs(clus.getLayer()-seg.getLayerLastEntry())) Compatible=false;
		return Compatible;
	}
	
	public ArrayList<Segment> getSegments(){
		return segmap;
	}
	
	public void clear() {
		for (int lay=0; lay<6;lay++) {
			StackLayer[lay].clear();
		}
		segmap.clear();
	}

}
