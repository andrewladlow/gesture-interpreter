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

    /**
     * Gets the assigned name of this gesture, provided in constructor.
     * 
     * @return This gesture's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the assigned type of this gesture, provided in constructor.
     * 
     * @return This gesture's type.
     */
    public String getType() {
        return type;
    }

    /**
     * Assigns this gesture's type.
     * 
     * @param t Type of this gesture.
     */
    public void setType(String t) {
        type = t;
    }

    /**
     * Returns the array of {@link Point} objects representing this gesture.
     * 
     * @return Array list of points.
     */
    public ArrayList<Point> getPointArray() {
        return (ArrayList<Point>) pointArray;
    }

    /**
     * Assigns this gesture's array of points to the given array.
     * 
     * @param newPointArray The desired array list of points.
     */
    public void setPointArray(ArrayList<Point> newPointArray) {
        pointArray = newPointArray;
    }

    /**
     * Assigns a new point to the end of this gesture's current array.
     * 
     * @param point The new point to be added.
     */
    public void addPoint(Point point) {
        pointArray.add(point);
    }

    /**
     * Gets the point held in this gesture's point array at a given index.
     * 
     * @param index The position in this gesture's point array to retrieve from.
     */
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