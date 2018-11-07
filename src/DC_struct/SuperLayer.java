package DC_struct;

import java.util.HashMap;

public class SuperLayer {
	
	public int region;
	public int SuperLayer;
	public Layer[] StackLayer= new Layer[6];
	HashMap<Integer, Segment> segmap;
	
	public SuperLayer(int reg, int SL) {
		region=reg;
		SuperLayer=SL;
		segmap = new HashMap<Integer, Segment>();
		for (int lay=0; lay<6;lay++) {
			StackLayer[lay]=new Layer(lay+1);
		}
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
		
		for (int lay=5; lay>-1;lay--) {
			
		}
		
	}

}
