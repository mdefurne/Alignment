package DC_struct;

import java.util.ArrayList;

public class Sector {
	public SuperLayer[] StackSL= new SuperLayer[6];
	public int DeltaInterSL;
	public int sector_number;
	private ArrayList<Segment> SectorSegments;
	
	public Sector(int num_sec) {
		for (int lay=0; lay<6;lay++) {
			StackSL[lay]=new SuperLayer(lay+1);
		}
		 DeltaInterSL=10;
		 sector_number=num_sec;
		 SectorSegments=new ArrayList<Segment>();
	}
	
	public SuperLayer getSuperLayer(int SL) {
		return StackSL[SL-1];
	}
	
	public void MakeRaySegments() {
		//Only interested in segments going from SL 6 to SL 1
		StackSL[5].MakeSegment();
		
		if (StackSL[5].hasGoodSegments()) {
			
			//Make the segments in all superlayers
			for (int slay=4;slay>-1;slay--) {
				StackSL[slay].MakeSegment();
			}
			
			for (int segU=0;segU<this.getSuperLayer(6).getSegments().size();segU++) {
				if (this.getSuperLayer(6).getSegments().get(segU).isGoodSLSegment()) {
					//Check sSL5 to start segments
					for (int segD=0;segD<this.getSuperLayer(5).getSegments().size();segD++) {
						if (this.getSuperLayer(5).getSegments().get(segD).isGoodSLSegment()) {
				
							if (this.AreCompatible(this.getSuperLayer(6).getSegments().get(segU),this.getSuperLayer(5).getSegments().get(segD))) 
								SectorSegments.add(this.getSuperLayer(6).getSegments().get(segU).Merge(this.getSuperLayer(5).getSegments().get(segD)));
						}
					}
				}
			}
		
			if (!SectorSegments.isEmpty()) {
				ArrayList<Segment> BufTemp=new ArrayList<Segment>();
				for (int slay=4; slay>0; slay--) {
					for (int seg=0;seg<this.getSuperLayer(slay).getSegments().size();seg++) {
						if (this.getSuperLayer(slay).getSegments().get(seg).isGoodSLSegment()) {
							//If we have a good segment in the layer, we try to merge it track candidate
					
							for (int track=0;track<SectorSegments.size();track++) {
								if (this.AreCompatible(this.getSuperLayer(slay).getSegments().get(seg), SectorSegments.get(track))) {
									BufTemp.add(SectorSegments.get(track).Merge(this.getSuperLayer(slay).getSegments().get(seg)));
								}
							}
						}
					}
					//Clear SectorSegment and fill it with BufTemp... then clear BufTemp for next SuperLayer
					SectorSegments.clear();
					for (int track=0;track<BufTemp.size();track++) {
						SectorSegments.add(BufTemp.get(track));
					}
					BufTemp.clear();
				}
			}
		}
	}
	
	public boolean AreCompatible(Segment SegLDown, Segment SegLUp) {
		boolean arecompatible=true;
		if (Math.abs(SegLDown.getSuperLayer()-SegLUp.getSuperLayer())>1) arecompatible=false;
		if (Math.abs(SegLUp.getLastCentroid()-SegLDown.getFirstCentroid())>DeltaInterSL) arecompatible=false;
		return arecompatible;
	}
	
	public void clear() {
		for (int lay=0; lay<6;lay++) {
			StackSL[lay].clear();
		}
		SectorSegments.clear();
	}
	
	public ArrayList<Segment> getSectorSegments() {
		return SectorSegments;
	}
	
	public int getSectorNumber() {
		return sector_number;
	}
	
}
