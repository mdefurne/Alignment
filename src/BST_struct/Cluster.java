package BST_struct;

import BST_struct.Hit;
import java.util.*;
import java.math.*;
import java.lang.*;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;

public class Cluster {
	
	private int size;
	private int Edep;
	private ArrayList<Integer> hit_id;
	
	public Cluster() {
		size=0;
		Edep=0;
		hit_id= new ArrayList();
	}
	
	public void add(int id_hit, Hit aHit) {
		hit_id.add(id_hit);
		size++;
		Edep+=aHit.getADC();
	}

	public int getLastEntry() {
		return hit_id.get(hit_id.size()-1);
	}
}
