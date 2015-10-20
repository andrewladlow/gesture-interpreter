package gestureinterpreter;

import com.leapmotion.leap.Arm;
import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;
import com.leapmotion.leap.Vector;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.geometry.Point2D;

public class LearnerListener extends Listener {
	
	private BooleanProperty frameReady = new SimpleBooleanProperty();
	
	private Gesture gesture;
    private Boolean recording = false; 
    private int frameCount = 0;
    private int minGestureFrames = 5;	
    private int maxVelocity = 200;	
    private int minVelocity = 30; 
    private Boolean stopRecording = false;  
	
    private List<byte[]> frameList = new ArrayList<byte[]>();
    
    
    public void onConnect(Controller controller) {
    	System.out.println("connected trainer");
    }
    
    public void onExit(Controller controller) {
    	System.out.println("disconnected trainer");
    }
	
	
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
		//System.out.println("1");
		frameReady.set(false);
		if (!frame.hands().isEmpty()) {
			//System.out.println("Debug 0  " + frame.id());
			frameReady.set(true);
		
		//System.out.println("2");
			if (stopRecording) { 
				return;
			}
	        
	        if (recordableFrame(frame, minVelocity, maxVelocity)){
	    		System.out.println("Debug 3");	            
	             
	            if (!recording) {
	                recording = true; 
	                frameCount = 0;
	            }
	            
	            
	            System.out.println("Debug frame: " + frame.id());
	            recordFrame(frame);
	            frameCount++;
	            System.out.println("Debug record");
	            
	        } else if (recording) {
	            
	             
	            recording = false;
	            	             
	            stopRecording();
	                
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
	
	
    public void recordFrame(Frame frame) {
    	frameList.add(frame.serialize());

        System.out.println("Debug add");
        System.out.println(frameList.size());


    }

    public Boolean recordableFrame(Frame frame, int min, int max){
        
            
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
            
            
        return false;
    }
        
    
    public void saveGesture() {
    	System.out.println("Debug save attempt");
    	try {
    		for (int i = 0; i < frameList.size(); i++) {
    			// from https://developer.leapmotion.com/documentation/java/devguide/Leap_Serialization.html
    			// write 4 bytes detailing size of frame
    			Files.newOutputStream(Paths.get("frames.data")).write(ByteBuffer.allocate(4).putInt(frameList.get(i).length).array());
    			// write actual frame data afterwards
    			Files.newOutputStream(Paths.get("frames.data")).write(frameList.get(i));
    		}
    	} catch (IOException e) {
    		System.out.println(e);
    	}
    	
    	System.out.println("Debug save 1");
    	
    }
    
    public void stopRecording(){
        stopRecording=true;
    }	
	
	
}