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
	private double phi_mid;
	private double z_mid;
	private double err_phi_mid;
	private double err_z_mid;
	private ArrayList<Integer> hit_id;
	
	public Cluster() {
		size=0;
		Edep=0;
		phi_mid=0;
		z_mid=0;
		err_phi_mid=0;
		err_z_mid=0;
		hit_id= new ArrayList();
	}
	
	public void add(int id_hit, Hit aHit) {
		hit_id.add(id_hit);
		size++;
		phi_mid=Edep*phi_mid+aHit.getPhi()*aHit.getADC();
		z_mid=Edep*z_mid+aHit.getZ()*aHit.getADC();
		err_phi_mid=Edep*err_phi_mid+aHit.getErrPhi()*aHit.getADC();
		err_z_mid=Edep*err_z_mid+aHit.getErrZ()*aHit.getADC();
		Edep+=aHit.getADC();
		if (Edep!=0) {
			phi_mid=phi_mid/Edep;
			z_mid=z_mid/Edep;
			err_phi_mid=err_phi_mid/Edep;
			err_z_mid=err_z_mid/Edep;
		}
	}

	public int getLastEntry() {
		return hit_id.get(hit_id.size()-1);
	}
	
	public double getPhi() {
		return phi_mid;
	}
	
	public double getErrPhi() {
		return err_phi_mid;
	}
	
	public double getZ() {
		return z_mid;
	}
	
	public double getErrZ() {
		return err_z_mid;
	}
	
}
