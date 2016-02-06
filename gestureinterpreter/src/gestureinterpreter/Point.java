package gestureinterpreter;

import java.io.Serializable;

import com.leapmotion.leap.Vector;

public class Point implements Serializable {
	
	private static final long serialVersionUID = -7199124440157829270L;
	private double x;
	private double y;
	private double z;
	private int ID = 0;

	public Point (double x, double y, double z) {
		this.x = x;
        this.y = y;
        this.z = z;
	}
	
	public Point (Vector v) {
		x = v.getX();
		y = v.getY();
		z = v.getZ();
	}
   
	public Point (double x, double y, double z, int ID) {
		this.x = x;
	   	this.y = y;
	   	this.z = z;
	   	this.ID = ID;
   	}
	
   
   	public double getX() {
   		return x;
   	}
   
   	public double getY() {
	   	return y;
   	}
   
   	public double getZ() {
	   	return z;
   	}
   
   	public int getID() {
	   	return ID;
   	}
}
