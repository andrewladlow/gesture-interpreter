package gestureinterpreter;

import java.io.Serializable;

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
