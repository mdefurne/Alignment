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
	
	public void WriteEvent(HashMap<Integer,TrackCandidate> candidates) {
		 HipoEvent event = writer.createEvent();
		 
		 int groupsize=0;
		 for (int i=0; i<candidates.size();i++) {
			 groupsize+=candidates.get(i+1).size();
		 }
		 
		 HipoGroup bank = writer.getSchemaFactory().getSchema("BMTRec::Crosses").createGroup(groupsize);
		 for (int i=0;i<candidates.size();i++) {
			 this.fillBMTCrossesBank(bank, candidates.get(i+1));
		 }
		 event.writeGroup(bank);
		 event.show();
		 writer.writeEvent( event );
	}

	public void fillBMTCrossesBank(HipoGroup bank, TrackCandidate cand) {
               
        for (int j = 0; j < cand.size(); j++) {
            bank.getNode("ID").setShort(j, (short) j);
            bank.getNode("sector").setByte(j, (byte) cand.GetBMTCluster(j).getSector());
            bank.getNode("region").setByte(j , (byte) ((cand.GetBMTCluster(j).getLayer()-1)/2));
            bank.getNode("x").setFloat(j, (float) cand.GetBMTCluster(j).getX());
            bank.getNode("y").setFloat(j, (float) cand.GetBMTCluster(j).getY());
            bank.getNode("z").setFloat(j, (float) cand.GetBMTCluster(j).getZ());
        }
        
	}
	
	public void close() {
		writer.close();
	}
}
