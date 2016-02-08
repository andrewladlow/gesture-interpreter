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
	
    private enum State {
    	IDLE, RECORDING;
    }
    
    public RecorderListener(char c, Object lock) {
    	this.lock = lock;
    	gesture = new Gesture(c);
    	state = State.IDLE;
    }
    
    public BooleanProperty gestureDoneProperty() {
    	return gestureDone;
    }

    public void onConnect(Controller controller) {
    	System.out.println("connected trainer");
    }
    
    public void onExit(Controller controller) {
    	System.out.println("disconnected trainer");
    }
	
	
	public void onFrame(Controller controller) {
		validPoseFrame = false;
		Frame frame = controller.frame();
		if (!frame.hands().isEmpty()) {
			// enforce delay between recordings
			if (System.currentTimeMillis() - timeRecognized > 3000) {	        
		        if (validFrame(frame, minGestureVelocity, maxPoseVelocity)) {	            	          
		            if (state == State.IDLE) {
		                state = State.RECORDING; 
		            }
		            
		            gestureFrameCount++;
		            System.out.println("gesture frame count: " + gestureFrameCount);
		                   
		            storePoint(frame);		            
		        } 
		        else if (state == State.RECORDING) {
		            System.out.println("debug record state");
		            state = State.IDLE;
		            if (validPose || (gestureFrameCount >= minGestureFrames)) {
		            	if (validPose) {
		            		gesture.setType("pose");
		            	}
		            	else {
		            		gesture.setType("gesture");
		            	}
		                timeRecognized = System.currentTimeMillis();
		            	saveGesture(gesture);
		                System.out.println("Debug store");
		                validPose = false;
		                gestureDone.set(true);
		            }
	                // reset variables
	                validPoseFrame = false;
	                validPose = false;
	                gestureFrameCount = 0;
	                poseFrameCount = 0;
		            state = State.IDLE; 
	                synchronized(lock) {
		                lock.notify();
	                }
		        }
			}
		}
	}
	
    public Boolean validFrame(Frame frame, int minVelocity, int maxVelocity) {        
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
        	gestureFrameCount = 0;
        	if (poseFrameCount >= minPoseFrames) {
        		validPose = true;
        		poseFrameCount = 0;
        		return true;
        	}
        } 
        else {
    		poseFrameCount = 0;
    	}
        return false;
    }
   
    public void storePoint(Frame frame) {  	
    	for (Hand hand : frame.hands()) {
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
    
    public void saveGesture(Gesture gesture) {
    	try {
    		System.out.println("saving " + gesture.getName());
    		FileOutputStream outStream = new FileOutputStream(new File("gestures/" + gesture.getName()), false);
    		ObjectOutputStream objOutStream = new ObjectOutputStream (outStream);
    		objOutStream.writeObject(gesture);
    		objOutStream.close();
    		outStream.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }	
}