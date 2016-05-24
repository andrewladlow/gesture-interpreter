package gestureinterpreter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 3D gesture. Holds an array of {@link Point} objects, each of
 * which hold a 3D co-ordinate.
 */
public class Gesture implements Serializable {
    private static final long serialVersionUID = 8793521708114985027L;
    
    private String name;
    private String type;
    private List<Point> pointArray;

    /**
     * Class constructor specifying name of this gesture.
     * 
     * @param n Name of the gesture.
     */
    public Gesture(String n) {
        name = n;
        type = "none";
        pointArray = new ArrayList<Point>();
    }

    /**
     * Clsas constructor specifying name of this gesture.
     * 
     * @param c Name of the gesture.
     */
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
        return (ArrayList<Point>) pointArray;
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

    /**
     * Adds the given point to the given index of this gesture's point array.
     * 
     * @param index The position in this gesture's point array.
     * @param p The point to be added.
     */
    public void setPoint(int index, Point p) {
        pointArray.set(index, p);
    }
}