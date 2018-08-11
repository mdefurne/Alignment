package HipoWriter;

import org.jlab.jnp.hipo.data.*;
import org.jlab.jnp.hipo.io.HipoWriter;
import org.jlab.io.base.*;
import org.jlab.jnp.hipo.schema.*;
import BST_struct.*;
import BMT_struct.*;
import TrackFinder.*;
import java.util.*;

public class CentralWriter {
	HipoWriter writer;
	SchemaFactory factory;
	
	public CentralWriter() {
		writer=new HipoWriter();
		factory = new SchemaFactory();
		factory.addSchema(new Schema("{1,BMTRec::Crosses}[1,ID,SHORT][2,sector,BYTE][3,region,BYTE][4,x,FLOAT][5,y,FLOAT][6,z,FLOAT]"));
		 writer.appendSchemaFactory(factory);
		 writer.open("/home/mdefurne/Bureau/CLAS12/customBank.hipo");
	}
	
	public void WriteEvent(Barrel BMT ,Barrel_SVT BST ,HashMap<Integer,TrackCandidate> candidates) {
		 HipoEvent event = writer.createEvent();
		 	 
		 event.writeGroup(this.fillBMTCrossesBank(BMT));
		 event.show();
		 writer.writeEvent( event );
	}

	public HipoGroup fillBMTCrossesBank(Barrel BMT) {
		int groupsize=0;
		for (int lay=0; lay<6; lay++) {
			for (int sec=0; sec<3; sec++) {
			groupsize+=BMT.getTile(lay,sec).getClusters().size();
			}
		}
		
		HipoGroup bank = writer.getSchemaFactory().getSchema("BMTRec::Crosses").createGroup(groupsize);
		int index=0;
		
		for (int lay=0; lay<6; lay++) {
			for (int sec=0; sec<3; sec++) {
				for (int j = 0; j < BMT.getTile(lay,sec).getClusters().size(); j++) {
					bank.getNode("ID").setShort(index, (short) index);
					bank.getNode("sector").setByte(index, (byte) (sec+1));
					bank.getNode("region").setByte(index, (byte) (lay/2));
					bank.getNode("x").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getX());
					bank.getNode("y").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getY());
					bank.getNode("z").setFloat(index, (float) BMT.getTile(lay,sec).getClusters().get(j+1).getZ());
					index++;
				}
			}
		}
        return bank;
	}
	
	public void close() {
		writer.close();
	}
}
