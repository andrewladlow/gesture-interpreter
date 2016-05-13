package gestureinterpreter;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Bone.Type;

/** 
 * Helper class used to store methods shared between recognizer and recorder listeners.
 */
public class ListenerHelper {
	
    /**
     * Stores points from a given frame to the current gesture's array.
     * @param frame The current frame to add gesture points from.
     * @param gesture The gesture to add points to. 
     */
	// feature set a
/*    public static void storePoint(Frame frame, Gesture gesture) {  	
    	for (Hand hand : frame.hands()) {
    		gesture.addPoint(new Point(hand.stabilizedPalmPosition()));
    		gesture.addPoint(new Point(hand.palmNormal()));
    		gesture.addPoint(new Point(hand.direction()));
    		for (Finger finger : hand.fingers()) {
    			gesture.addPoint(new Point(finger.direction()));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_METACARPAL).prevJoint()));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_METACARPAL).nextJoint()));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_PROXIMAL).nextJoint()));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_INTERMEDIATE).nextJoint()));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_DISTAL).nextJoint()));
    		}
    	}
    }
    */
	
    // testing alternate feature set b 
    public static void storePoint(Frame frame, Gesture gesture) { 
    	for (Hand hand : frame.hands()) {
    		for (Finger finger : hand.fingers()) {
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_METACARPAL).nextJoint().minus(hand.palmPosition())));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_PROXIMAL).nextJoint().minus(hand.palmPosition())));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_INTERMEDIATE).nextJoint().minus(hand.palmPosition())));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_DISTAL).nextJoint().minus(hand.palmPosition())));
    		}
    	}
    }
    
    // testing alternate feature set c 
/*    public static void storePoint(Frame frame, Gesture gesture) { 
    	for (Hand hand : frame.hands()) {
    		gesture.addPoint(new Point(hand.direction()));
    		for (Finger finger : hand.fingers()) {
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_METACARPAL).direction()));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_PROXIMAL).direction()));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_INTERMEDIATE).direction()));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_DISTAL).direction()));
    		}
    	}
    }*/
}
