package BMT_struct;

import BMT_struct.Hit;
import java.util.*;
import java.math.*;
import java.lang.*;
import org.jlab.geom.prim.Point3D;
import org.jlab.geom.prim.Vector3D;


public class Cluster {
	private float t_min;
	private float t_max;
	private Vector3D XYZ;
	private Vector3D RPhiZ;
	private double centroid; //strip info
	private double centroid_phi; //info in loc frame, either phi or z.
	private double centroid_x; //info in loc frame, either phi or z.
	private double centroid_y; //info in loc frame, either phi or z.
	private double centroid_z; //info in loc frame, either phi or z.
	private double centroid_r;
	private int size;
	private int Edep;
	private ArrayList<Integer> hit_id;
	private double Err;
	private int layer_clus;
	private int sector_clus;
		
	public Cluster() {
		t_min=0;
		t_max=0;
		centroid=0;
		centroid_phi=0;
		centroid_r=0;
		centroid_x=0;
		centroid_y=0;
		centroid_z=0;
		size=0;
		Edep=0;
		hit_id=new ArrayList();
		Err=0.1;//mm
		}
	
	public void add(int id_hit, Hit aHit) {
		if (hit_id.size()==0) {
			t_min=aHit.getTime();
			t_max=aHit.getTime();
		}
		hit_id.add(id_hit);
		if (t_min>aHit.getTime()) t_min=aHit.getTime();
		if (t_max<aHit.getTime()) t_max=aHit.getTime();
		
		centroid_r=aHit.getRadius();
		
		if(!Double.isNaN(aHit.getPhi())) {
			centroid_phi=Edep*centroid_phi+aHit.getADC()*aHit.getPhi();
			centroid_x=Edep*centroid_x+centroid_r*aHit.getADC()*Math.cos(aHit.getPhi());
			centroid_y=Edep*centroid_y+centroid_r*aHit.getADC()*Math.sin(aHit.getPhi());
			Edep+=aHit.getADC();
			centroid_phi=centroid_phi/Edep;
			centroid_x=centroid_x/Edep;
			centroid_y=centroid_y/Edep;
			centroid_z=Double.NaN;
		}
		if(!Double.isNaN(aHit.getZ())) {
			centroid_x=Double.NaN;
			centroid_y=Double.NaN;
			centroid_z=Edep*centroid_z+aHit.getADC()*aHit.getZ();
			Edep+=aHit.getADC();
			centroid_z=centroid_z/Edep;
		}
		centroid+=id_hit*aHit.getADC();
		
	}
	
	public double getX() {
		return centroid_x;
	}
	
	public double getY() {
		return centroid_y;
	}
	
	public double getZ() {
		return centroid_z;
	}
	
	public double getRadius() {
		return centroid_r;
	}
	
	public double getPhi() {
		return centroid_phi;
	}
	
	public double getCentroid() {
		return centroid;
	}
	
	public float getT_min() {
		return t_min;
	}
	
	public float getT_max() {
		return t_max;
	}
	
	public float getTimeWalk() {
		return t_max-t_min;
	}
	
	public int getEdep() {
		return Edep;
	}
	
	public int getSize() {
		return hit_id.size();
	}
	
	public ArrayList<Integer> getHit_id(){
		return hit_id;
	}
	
	public int getLastEntry(){
		return hit_id.get(hit_id.size()-1);
	}
	
	public int getLayer(){
		return layer_clus;
	}
	
	public int getSector(){
		return sector_clus;
	}
	
	public void setLayer(int layer){
		layer_clus=layer;
	}
	
	public void setSector(int sector){
		sector_clus=sector;
	}
	
	public double getErr(){
		return Err;
	}
				
}
