package gestureinterpreter;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

public class RecorderListener extends Listener {
	
	private BooleanProperty frameReady = new SimpleBooleanProperty();
	
	private Gesture gesture;
    private int frameCount = 0;
    
    private int minGestureFrames = 5;
    private int minGestureVelocity = 150;
    
    private int minPoseFrames = 5;
    private int maxPoseVelocity = 50;	
	
    private enum State {
    	IDLE, RECORDING, STOPPED;
    }
    
    private State state = State.IDLE;
    
    public void RecorderListener(String gestureName) {
    	gesture = new Gesture(gestureName);
    }
    
    public void onConnect(Controller controller) {
    	System.out.println("connected trainer");
    }
    
    public void onExit(Controller controller) {
    	System.out.println("disconnected trainer");
    }
	
	
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
		frameReady.set(false);
		if (!frame.hands().isEmpty()) {
			frameReady.set(true);
			
			if (state == State.STOPPED) {
				return;
			}
	        
	        if (validFrame(frame, minGestureVelocity, maxPoseVelocity)) {
	    		System.out.println("Debug 3");	            
	             
	            if (state == State.IDLE) {
	                frameCount = 0;
	                state = State.RECORDING; 
	            }
	            
	            
	            System.out.println("Debug frame: " + frame.id());
	            storeFrame(frame);
	            frameCount++;
	            System.out.println("Debug record");
	            
	        } else if (state == State.RECORDING) {
	                      
	            state = State.STOPPED;
	                
	            if (frameCount >= minGestureFrames) {
	            	saveGesture();

	                System.out.println("Debug save 2");
	            }
	        }
		}
	}

	public BooleanProperty frameReadyProperty() {
		return frameReady;
	}
	
	
    public Boolean validFrame(Frame frame, int min, int max) {
        
        for (Hand hand : frame.hands()) {
        	
            Vector palmVelocityTemp = hand.palmVelocity(); 
            float palmVelocity = Math.max(Math.abs(palmVelocityTemp.getX()), Math.max(Math.abs(palmVelocityTemp.getY()), Math.abs(palmVelocityTemp.getZ())));             
            
            if (palmVelocity >= min) {
            	return true;
            }
                  
            for (Finger finger : hand.fingers()) {
            	 
            	Vector fingerVelocityTemp = finger.tipVelocity();
                float fingerVelocity = Math.max(Math.abs(fingerVelocityTemp.getX()), Math.max(Math.abs(fingerVelocityTemp.getY()), Math.abs(fingerVelocityTemp.getZ())));
                    
                if (fingerVelocity >= min) { 
                	return true; 
                }

            }
             
            // some way of checking static gestures with max velocity? held position over x frames...
        }
        
        if (poseRecordable) {
        	this.recordedPoseFrames++;
        	
        	if (this.recordedPoseFrames >= this.minPoseFrames) {
        		this.recordingPose = true;
        		return true;
        	} else {
        		this.recordedPoseFrames = 0;
        	}
        }
            
        return false;
    }
   
    public void storeFrame(Frame frame) {
    	
    	for (Hand hand : frame.hands()) {
    		gesture.addPoint(new Point());
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