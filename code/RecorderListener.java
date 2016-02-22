package gestureinterpreter;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Finger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.leapmotion.leap.Vector;
import com.leapmotion.leap.Bone.Type;

/**
 * Class handling recording of new gestures, extends the 
 * Leap Motion listener class. 
 */
public class RecorderListener extends Listener {
	private final Object lock;
	private BooleanProperty gestureDone = new SimpleBooleanProperty();
	private Gesture gesture;
    private int gestureFrameCount = 0;  
    private int minGestureFrames = 10;
    private int minGestureVelocity = 300;
    private int poseFrameCount = 0;
    private int minPoseFrames = 50;
    private int maxPoseVelocity = 30;	
    private boolean validPoseFrame = false;
    private boolean validPose = false;      
    private long timeRecognized = 0;
    private State state;
	
    /**
     * Handles the listener's state - either
     * 'idle' or 'recording'. 
     */
    private enum State {
    	IDLE, RECORDING;
    }

    /**
     * Creates a new instance of a recorder listener.
     * @param c The character this listener is recording.
     * @param lock The shared lock between this listener and the gui. 
     */
    public RecorderListener(char c, Object lock) {
    	this.lock = lock;
    	gesture = new Gesture(c);
    	state = State.IDLE;
    }
    
    /**
     * Returns the gestureDone boolean property.
     */
    public BooleanProperty gestureDoneProperty() {
    	return gestureDone;
    }

    /**
     * Called when this listener is added to a controller.
     * @param controller The leap motion controller to check. 
     */
    public void onConnect(Controller controller) {
    	System.out.println("connected trainer");
    }

    /**
     * Called when this listener is disconnected from a controller.
     * @param controller The leap motion controller to check. 
     */
    public void onExit(Controller controller) {
    	System.out.println("disconnected trainer");
    }
	
    /**
     * Called when a new frame of tracking data is available.
     * @param controller The leap motion controller to poll. 
     */	
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
		if (!frame.hands().isEmpty()) {					
			// enforce delay between recognitions
			if (System.currentTimeMillis() - timeRecognized > 5000) {	        
		        if (validFrame(frame, minGestureVelocity, maxPoseVelocity)) {	            	          
		            if (state == State.IDLE) {
		            	gestureFrameCount = 0;
		            	poseFrameCount = 0;
		                state = State.RECORDING; 
		            }
		            gestureFrameCount++;
		            //System.out.println("gesture frame count: " + gestureFrameCount);
		            storePoint(frame);		            
		        } 
		        else if (state == State.RECORDING) {
		            state = State.IDLE;	            
		            if (validPose || (gestureFrameCount >= minGestureFrames)) {
		            	if (validPose) {
		            		gesture.setType("pose");
		            	}
		            	else {
		            		gesture.setType("gesture");
		            	}
		            	saveGesture(gesture);
		                timeRecognized = System.currentTimeMillis();
		                gestureDone.set(true);
		            }
	                validPose = false;
	                synchronized(lock) {
		                lock.notify();
	                }
		        }
			}
		}
	}
	
    /**
     * Returns a boolean signifying whether a given frame should be recorded or not.
     * @param frame The frame to check.
     * @param minVelocity The minimum velocity which a hand must move at to return true.
     * @param maxVelocity The maximum velocity a hand must remain under to trigger a pose. 
     */
    public Boolean validFrame(Frame frame, int minVelocity, int maxVelocity) {     
    	validPoseFrame = false;
        for (Hand hand : frame.hands()) {	
            Vector palmVelocityTemp = hand.palmVelocity(); 
            float palmVelocity = Math.max(Math.abs(palmVelocityTemp.getX()), 
            							  Math.max(Math.abs(palmVelocityTemp.getY()), 
            									   Math.abs(palmVelocityTemp.getZ())));                        
            if (palmVelocity >= minVelocity) {
            	return true;
            } 
            else if (palmVelocity <= maxVelocity) {
            	validPoseFrame = true;
            	break;
            }           
            for (Finger finger : hand.fingers()) {           	 
            	Vector fingerVelocityTemp = finger.tipVelocity();
                float fingerVelocity = Math.max(Math.abs(fingerVelocityTemp.getX()), 
                								Math.max(Math.abs(fingerVelocityTemp.getY()), 
                										 Math.abs(fingerVelocityTemp.getZ())));                 
                if (fingerVelocity >= minVelocity) {
                	return true; 
                } 
                else if (fingerVelocity <= maxVelocity) {
                	validPoseFrame = true;
                	break;
                }
            }
        }        
        if (validPoseFrame) {
        	poseFrameCount++;
        	//System.out.println("pose frame count: " +  poseFrameCount);
        	if (poseFrameCount >= minPoseFrames) {
        		validPose = true;
        		return true;
        	}
        } 
        else {
    		poseFrameCount = 0;
    	}      
        return false;
    }
    
    
    /**
     * Stores points from a given frame to the current gesture's array.
     * @param frame The current frame to check. 
     */
    public void storePoint(Frame frame) {  	
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
    
    /**
     * Saves a given gesture to disk.
     * @param gesture The gesture to save. 
     */
    public void saveGesture(Gesture gesture) {
    	try {
    		// find the last created gestureSet folder
    	   	File topDir = new File(".");
        	File[] files = topDir.listFiles(fileName -> fileName.getName().startsWith("gestureSet"));  	
    		String lastDir = files[files.length-1].getName();
    		int setDirCount = 0;
        	// if there are no previous gesture sets, create first folder
        	if (files.length == 0) {
            	File newSetDir = new File("gestureSet1");
            	newSetDir.mkdir();
        	}
        	// else find last folder number
        	else {
        		setDirCount = Integer.parseInt(lastDir.replaceAll("\\D", ""));
        	}
        	
        	// if the gesture set is full (all 26 letters present) we need to create a new set folder
        	// otherwise we use the last created folder
        	if (files[files.length-1].list().length == 26) {
            	setDirCount++;
            	File newSetDir = new File("gestureSet" + setDirCount);
            	newSetDir.mkdir();
        	}
        	      	
    		System.out.println("saving " + gesture.getName());
    		FileOutputStream outStream = new FileOutputStream(new File("gestureSet" + setDirCount + "/" + gesture.getName()), false);
    		ObjectOutputStream objOutStream = new ObjectOutputStream (outStream);
    		objOutStream.writeObject(gesture);
    		objOutStream.close();
    		outStream.close();
    	}
		catch (Exception e) {
    		e.printStackTrace();
    	}
    }	
}