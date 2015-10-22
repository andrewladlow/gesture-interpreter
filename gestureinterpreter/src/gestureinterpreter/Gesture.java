package gestureinterpreter;

import java.io.Serializable;
import java.util.ArrayList;


public class Gesture implements Serializable {
    String name;
    ArrayList<Point> pointArray = new ArrayList<Point>();
    
    public Gesture (String name) {
        this.name = name;
    }
    
    public void addPoint (Point point) {
        pointArray.add(point);
    }
    
    public Point getPoint (int index) {
        return pointArray.get(index);
    }
    
    public void setPoint (int index, Point p) {
        pointArray.set(index, p);
    }
}