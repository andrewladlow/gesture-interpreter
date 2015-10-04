package gestureinterpreter;

import com.leapmotion.leap.HandList;
import java.io.Serializable;
import java.util.ArrayList;


public class Gesture implements Serializable{
    String name;
    ArrayList<Point> points = new ArrayList<Point>();
    HandList hands;
    
    public Gesture(String input){
        name = input;
    }
    
    public Gesture(){
        name = null;
    }
    
    public void add(Point point){
        points.add(point);
    }
    
    public Point get(int i){
        return points.get(i);
    }
    
    public void set(int i, Point p){
        points.set(i, p);
    }
}