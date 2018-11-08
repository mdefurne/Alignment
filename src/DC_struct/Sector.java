package DC_struct;

public class Sector {
	public SuperLayer[] StackSL= new SuperLayer[6];
	public int DeltaInterRegion;
	public int DeltaIntraRegion;
	public int sector_number;
	
	public Sector(int num_sec) {
		for (int lay=0; lay<6;lay++) {
			StackSL[lay]=new SuperLayer(lay+1);
		}
		 DeltaInterRegion=10;
		 DeltaIntraRegion=3;
		 sector_number=num_sec;
	}
	
	public SuperLayer getSuperLayer(int SL) {
		return StackSL[SL-1];
	}
	
	public void MakeRaySegments() {
		//Only interested in segments going from SL 6 to SL 1
		for (int seg=0;seg<this.getSuperLayer(6).getSegments().size();seg++) {
			if (this.getSuperLayer(6).getSegments().get(seg).isGoodSLSegment()) {
				//Will check and merge if compatible the other segments in other superlayer
				for (int lay=5; lay>0;lay--) {
					for (int test=0;test<this.getSuperLayer(lay).getSegments().size();test++) {
						if (this.AreCompatible(this.getSuperLayer(6).getSegments().get(seg),this.getSuperLayer(lay).getSegments().get(test))) 
								this.getSuperLayer(6).getSegments().get(seg).Merge(this.getSuperLayer(lay).getSegments().get(test));
					}
				}
			}
		}
	}
	
	public boolean AreCompatible(Segment SegLDown, Segment SegLUp) {
		boolean arecompatible=true;
		if (Math.abs(SegLDown.getSuperLayer()-SegLUp.getSuperLayer())>1) arecompatible=false;
		int Delta=0;
		if (SegLDown.getRegion()==SegLUp.getRegion()) Delta=DeltaIntraRegion;
		else Delta=DeltaInterRegion;
		if (Math.abs(SegLUp.getLastCentroid()-SegLDown.getFirstCentroid())>Delta) arecompatible=false;
		return arecompatible;
	}
	
	public void clear() {
		for (int lay=0; lay<6;lay++) {
			StackSL[lay].clear();
		}
	}
	
	public int getSectorNumber() {
		return sector_number;
	}
	
}
