package gestureinterpreter;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Finger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.application.Platform;

import com.leapmotion.leap.Vector;
import com.leapmotion.leap.Bone.Type;

public class RecognizerListener extends Listener {
	
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
    private ArrayList<Gesture> storedGestures;   
    private PDollarRecognizer pdRec = new PDollarRecognizer();   
    private final RecognizerGUI recGUI;   

    private enum State {
    	IDLE, RECORDING;
    }
    
    public RecognizerListener(RecognizerGUI recGUI) {
    	this.recGUI = recGUI;
    	storedGestures = new ArrayList<Gesture>();
    	gesture = new Gesture("testGesture");
    	state = State.IDLE;
    	File[] files = new File("gestures/").listFiles();
    	for (File file : files) {
	    	try {
	    		FileInputStream inStream = new FileInputStream(file);
	    		ObjectInputStream ObjInStream = new ObjectInputStream(inStream);
	    		Gesture tempGesture = (Gesture) ObjInStream.readObject();
	    		
	    		//System.out.println(Arrays.asList(tempGesture.getPointArray().size()));

	    		tempGesture.setPointArray(PDollarRecognizer.Resample(tempGesture.getPointArray(), PDollarRecognizer.mNumPoints));
	    		tempGesture.setPointArray(PDollarRecognizer.Scale(tempGesture.getPointArray()));
	    		tempGesture.setPointArray(PDollarRecognizer.TranslateTo(tempGesture.getPointArray(), new Point(0.0,0.0,0.0)));
	    		
	    		storedGestures.add(tempGesture);
	    		
	    		//System.out.println(Arrays.asList(tempGesture.getPointArray().size()));   		
	    		
	    		ObjInStream.close();
	    		inStream.close();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
    	}
    }
    
    public void onConnect(Controller controller) {
    	System.out.println("connected recognizer");
    }
    
    public void onExit(Controller controller) {
    	System.out.println("disconnected recognizer");
    }
	
	public void onFrame(Controller controller) {
		validPoseFrame = false;
		Frame frame = controller.frame();
		if (!frame.hands().isEmpty()) {
					
			// enforce delay between recognitions
			if (System.currentTimeMillis() - timeRecognized > 250) {	        
		        if (validFrame(frame, minGestureVelocity, maxPoseVelocity)) {	            		             
		            if (state == State.IDLE) {
		            	gestureFrameCount = 0;
		                state = State.RECORDING; 
		            }      
	                gestureFrameCount++;
	                //System.out.println("gesture frame count: " + gestureFrameCount);
		            storePoint(frame);
		            
		        } 
		        else if (state == State.RECORDING) {
		            //System.out.println("debug record fail state");
		            state = State.IDLE;
		            
		            if (validPose || (gestureFrameCount >= minGestureFrames)) {
		            	if (validPose) {
		            		gesture.setType("pose");
		            	}
		            	else {
		            		gesture.setType("gesture");
		            	}
		            	//System.out.println("debug recognize");
		                RecognizerResults recResult = pdRec.Recognize(gesture, storedGestures);
		                //System.out.println("\nClosest match: " + recResult.getName() + "\nNormalized score: " + recResult.getScore());
		                timeRecognized = System.currentTimeMillis();
		                Platform.runLater(() -> {
				            recGUI.gestureRecognitionProperty().set(recResult);
		                });
		                // reset variables
		                validPoseFrame = false;
		                validPose = false;
		                gestureFrameCount = 0;
		                poseFrameCount = 0;
			            state = State.IDLE; 
		            } else {
		            	//System.out.println("Recognition failed");
		            }
		        }
			}
		} 
		else {
			if (state != State.IDLE) {
				state = State.IDLE;
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
            	//System.out.println("valid palm pose");
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
        	//System.out.println("pose frame count: " + poseFrameCount);
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
   
/*    public void storePointv2(Frame frame) {  	
    	for (Hand hand : frame.hands()) {
    		gesture.addPoint(new Point(hand.stabilizedPalmPosition()));
    		gesture.addPoint(new Point(hand.direction()));
    		gesture.addPoint(new Point(hand.palmNormal()));
    		for (Finger finger : hand.fingers()) {
    			gesture.addPoint(new Point(finger.stabilizedTipPosition()));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_METACARPAL).nextJoint().minus(hand.palmPosition())));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_PROXIMAL).nextJoint().minus(hand.palmPosition())));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_INTERMEDIATE).nextJoint().minus(hand.palmPosition())));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_DISTAL).nextJoint().minus(hand.palmPosition())));
    		}
    	}
    }*/
    
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
    		//System.out.println("saving " + gesture.getName());
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