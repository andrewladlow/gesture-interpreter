package gestureinterpreter;

import java.io.Serializable;
import java.util.ArrayList;


public class Gesture implements Serializable {
	
	private static final long serialVersionUID = 8793521708114985027L;
    private String name;
    private String type;
    private ArrayList<Point> pointArray;
    
    public Gesture(String n) {
        name = n;
        type = "none";
        pointArray = new ArrayList<Point>();
    }
    
    public Gesture(char c) {
    	name = Character.toString(c);
    	type = "none";
    	pointArray = new ArrayList<Point>();
    }
    
    public String getName() {
    	return name;
    }
    
    public String getType() {
    	return type;
    }
    
    public void setType(String t) {
    	type = t;
    }
    
    public ArrayList<Point> getPointArray() {
    	return pointArray;
    }
    
    public void setPointArray(ArrayList<Point> newPointArray) {
    	pointArray = newPointArray;
    }
    
    public void addPoint(Point point) {
        pointArray.add(point);
    }
    
    public Point getPoint(int index) {
        return pointArray.get(index);
    }
    
    public void setPoint(int index, Point p) {
        pointArray.set(index, p);
    }
}