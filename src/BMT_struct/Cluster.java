package BMT_struct;

import BMT_struct.Hit;
import java.util.*;
import java.math.*;
import java.lang.*;


public class Cluster {
	float t_min;
	float t_max;
	float centroid; //strip info
	float centroid_phi; //info in loc frame, either phi or z.
	double centroid_x; //info in loc frame, either phi or z.
	double centroid_y; //info in loc frame, either phi or z.
	double centroid_z; //info in loc frame, either phi or z.
	double centroid_r;
	int size;
	int Edep;
	ArrayList<Integer> hit_id;
	boolean Complete;
	
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
		Complete=false;
	}
	
	public void add(int id_hit, Hit aHit) {
		if (hit_id.size()==0) {
			t_min=aHit.getTime();
			t_max=aHit.getTime();
		}
		hit_id.add(id_hit);
		if (t_min>aHit.getTime()) t_min=aHit.getTime();
		if (t_max<aHit.getTime()) t_max=aHit.getTime();
		Edep+=aHit.getADC();
		if(!Double.isNaN(aHit.getPhi())) {
			centroid_phi+=aHit.getADC()*aHit.getPhi();
			centroid_x+=aHit.getADC()*Math.cos(aHit.getPhi());
			centroid_y+=aHit.getADC()*Math.sin(aHit.getPhi());
		}
		if(!Double.isNaN(aHit.getZ()))centroid_z+=aHit.getADC()*aHit.getZ();
		centroid+=id_hit*aHit.getADC();
		centroid_r=aHit.getRadius();
	}
	
	public void ComputeProperties() {
		if (hit_id.size()!=0) {
			centroid_phi=centroid_phi/Edep;
			centroid_x=centroid_x/Edep;
			centroid_y=centroid_y/Edep;
			centroid_z=centroid_z/Edep;
			centroid=centroid/Edep;
			size=hit_id.size();
			Complete=true;
		}
		else System.out.println("Sorry... your BMT cluster is empty.");
	}
	
	public boolean IsComplete() {
		return Complete;
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
	
	public float getPhi() {
		return centroid_phi;
	}
	
	public float getCentroid() {
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
}
