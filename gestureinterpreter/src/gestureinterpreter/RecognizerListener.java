package gestureinterpreter;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.leapmotion.leap.Screen;
import com.leapmotion.leap.Vector;

import javafx.geometry.Point2D;

public class RecognizerListener extends Listener {
	
	private BooleanProperty frameReady = new SimpleBooleanProperty();
	
	private Gesture gesture;
	
    private int gestureFrameCount = 0;  
    private int minGestureFrames = 5;
    private int minGestureVelocity = 3300;
    
    private int poseFrameCount = 0;
    private int minPoseFrames = 50;
    private int maxPoseVelocity = 30;	
    private boolean validPoseFrame = false;
    private boolean validPose = false;    
	
    private enum State {
    	IDLE, RECORDING, STOPPED;
    }
    
    private State state;
    
    private ArrayList<Gesture> storedGestures;
    
    private PDollarRecognizer pdRec = new PDollarRecognizer();
    
    public RecognizerListener() {
    	storedGestures = new ArrayList<Gesture>();
    	gesture = new Gesture("testGesture");
    	state = State.IDLE;
    	File[] files = new File("gestures/").listFiles();
    	for (File file : files) {
	    	try {
	    		FileInputStream inStream = new FileInputStream(file);
	    		ObjectInputStream ObjInStream = new ObjectInputStream(inStream);
	    		Gesture tempGesture = (Gesture) ObjInStream.readObject();
	 		
	    		tempGesture.setPointArray(PDollarRecognizer.Resample(tempGesture.getPointArray(), 32));
	    		tempGesture.setPointArray(PDollarRecognizer.Scale(tempGesture.getPointArray()));
	    		tempGesture.setPointArray(PDollarRecognizer.TranslateTo(tempGesture.getPointArray(), new Point(0.0,0.0,0.0)));
	    		
	    		storedGestures.add(tempGesture);
	    		
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
		//System.out.println("Frame: " + controller.frame().id() + " State: " + state);
		validPoseFrame = false;
		Frame frame = controller.frame();
		frameReady.set(false);
		if (!frame.hands().isEmpty()) {
			frameReady.set(true);
			
			if (state == State.STOPPED) {
				return;
			}
	        
	        if (validFrame(frame, minGestureVelocity, maxPoseVelocity)) {	            
	             
	            if (state == State.IDLE) {
	                state = State.RECORDING; 
	            }
	            
	         
	            
	            storePoint(frame);
	            System.out.println("Debug record");
	            
	        } else if (state == State.RECORDING) {
	            System.out.println("debug record state");
	            state = State.STOPPED;
	            
	            if ((gestureFrameCount >= minGestureFrames) || validPose) {
	            	
	            	//saveGesture(gesture);
		               // System.out.println("Debug store");
		                //validPose = false;
		            	System.out.println("debug recognize");
		                RecognizerResults recResult = pdRec.Recognize(gesture, storedGestures);
		                System.out.println("\nClosest match: " + recResult.mName + "\nNormalized score: " + recResult.mScore);
	            }
	        }
		}
	}

	public BooleanProperty frameReadyProperty() {
		return frameReady;
	}
	
	
    public Boolean validFrame(Frame frame, int minVelocity, int maxVelocity) {
        
        for (Hand hand : frame.hands()) {
        	
            Vector palmVelocityTemp = hand.palmVelocity(); 
            float palmVelocity = Math.max(Math.abs(palmVelocityTemp.getX()), Math.max(Math.abs(palmVelocityTemp.getY()), Math.abs(palmVelocityTemp.getZ())));             
            
            //System.out.println("palm velocity: " + palmVelocity);
            
            if (palmVelocity >= minVelocity) {
            	return true;
            } else if (palmVelocity <= maxVelocity) {
            	validPoseFrame = true;
            	System.out.println("valid palm pose");
            	break;
            }
                  
            for (Finger finger : hand.fingers()) {
            	 
            	Vector fingerVelocityTemp = finger.tipVelocity();
                float fingerVelocity = Math.max(Math.abs(fingerVelocityTemp.getX()), Math.max(Math.abs(fingerVelocityTemp.getY()), Math.abs(fingerVelocityTemp.getZ())));
                    
                if (fingerVelocity >= minVelocity) { 
                	return true; 
                } else if (fingerVelocity <= maxVelocity) {
                	validPoseFrame = true;
                	break;
                }
            }
        }
        
        if (validPoseFrame) {
        	poseFrameCount++;
        	System.out.println("pose frame count: " + poseFrameCount);
        	if (poseFrameCount > minPoseFrames) {
        		validPose = true;
        		return true;
        	}
        } else {
            gestureFrameCount++;
           // System.out.println("gesture frame count: " + gestureFrameCount);
    		poseFrameCount = 0;
    	}
        
        
        return false;
    }
   
    public void storePoint(Frame frame) {
    	
    	for (Hand hand : frame.hands()) {
    		//gesture.addPoint(new Point(hand.palmVelocity().getX(), hand.palmVelocity().getY(), hand.palmVelocity().getZ()));
    		gesture.addPoint(new Point(hand.stabilizedPalmPosition().getX(), hand.stabilizedPalmPosition().getY(), hand.stabilizedPalmPosition().getZ()));
    		
    		for (Finger finger : hand.fingers()) {
    			//gesture.addPoint(new Point(finger.tipVelocity().getX(), finger.tipVelocity().getY(), finger.tipVelocity().getZ()));
    			gesture.addPoint(new Point(finger.stabilizedTipPosition().getX(), finger.stabilizedTipPosition().getY(), finger.stabilizedTipPosition().getZ()));
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
    
/*    public void saveGesture(Controller controller) {
    	System.out.println("Debug save attempt");
    	try {
    		Path outPath = Paths.get("frames.data");
    		OutputStream out = Files.newOutputStream(outPath);
    		  for (int f = 120; f >= 0; f--) {
    		      Frame frameToSerialize = controller.frame(f);
    		      byte[] serialized = frameToSerialize.serialize();
    		      out.write( ByteBuffer.allocate(4).putInt(serialized.length).array() );
    		      out.write(serialized);
    		  }
    		  out.flush();
    		  out.close();
    	} catch (IOException e) {
    		System.out.println(e);
    	}
    	
    	System.out.println("Debug save 1");
    	
    }*/
    
	
	
}