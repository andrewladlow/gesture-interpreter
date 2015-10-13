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

public class LeapListener extends Listener {
	
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
		System.out.println("connected leap");
	}
	
	public void onExit(Controller controller) {
		System.out.println("disconnected leap");
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
	            
	            
	            System.out.println("Debug frame found: " + frame.id());
	            recordFrame(frame);
	            frameCount++;
	            System.out.println("Debug record");
	            
	        } else if(recording) {

	            recording = false;

	            stopRecording();
	                
	            if (frameCount >= minGestureFrames){
	                try {
	                    saveGesture();
	                } catch (IOException ex) {
	                    Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
	                } finally {
	                    System.out.println("Debug save 2");
	                }
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
        
        FingerList fingers;
        Boolean poseRecordable = false;
            
        for(Hand hand : frame.hands()) {
        	
            Vector palmVelocityTemp = hand.palmVelocity();
            int palmVelocity = Math.max(Math.abs(palmVelocityTemp.getX()), Math.max(Math.abs(palmVelocityTemp.getY()), Math.abs(palmVelocityTemp.getZ())));
                
            /*
             * We return true if there is a hand moving above the minimum recording velocity
             */
             if (palmVelocity >= min) {
            	 return true;
             }
                
             fingers = hand.fingers(); 
             
             for (Finger finger : hand.fingers()) {
                 Vector fingerVelocityTemp = finger.tipVelocity();
                 tipVelocity = Math.max(Math.abs(tipVelocityTemp.getX()), Math.abs(tipVelocityTemp.getY()));
                 tipVelocity = Math.max(tipVelocity, Math.abs(tipVelocitys.getZ()));
                    
                /*
                 * Or if there's a finger tip moving above the minimum recording velocity
                 */
                if (tipVelocity >= min) { return true; }
                //if (tipVelocity <= max) { poseRecordable = true; break; }
            }
        }
            
            
        return false;
    }
    
    public void saveGesture() throws IOException {
    	System.out.println("Debug save attempt");
    	try {
    		for (int i = 0; i < frameList.size(); i++) {
    			// see https://developer.leapmotion.com/documentation/java/devguide/Leap_Serialization.html
    			// write 4 bytes detailing size of frame
    			Files.newOutputStream(Paths.get("frames.data")).write(ByteBuffer.allocate(4).putInt(frameList.get(i).length).array());
    			// write actual frame afterwards
    			Files.newOutputStream(Paths.get("frames.data")).write(frameList.get(i));
    		}
    	} catch (IOException e) {
    		System.out.println("Debug ERROR");
    	}
    	
    	System.out.println("Debug save 1");
    	
/*        String fileName= gestureIn.name;
        ObjectOutputStream out = null;
        
        try{
            System.out.println("Saving Gesture...");
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
            out.writeObject(gestureIn);
        } catch (IOException ex) {
            Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }*/
    }
    
    public void stopRecording(){
        stopRecording=true;
    }	
	
	
}



//-----------------------



//package gestureinterpreter;
//
//import com.leapmotion.leap.Arm;
//import com.leapmotion.leap.Bone;
//import com.leapmotion.leap.Controller;
//import com.leapmotion.leap.Finger;
//import com.leapmotion.leap.FingerList;
//import com.leapmotion.leap.Frame;
//import com.leapmotion.leap.Hand;
//import com.leapmotion.leap.HandList;
//import com.leapmotion.leap.Listener;
//
//import java.io.BufferedOutputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import javafx.application.Platform;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import com.leapmotion.leap.Controller;
//import com.leapmotion.leap.Frame;
//import com.leapmotion.leap.Hand;
//import com.leapmotion.leap.HandList;
//import com.leapmotion.leap.Listener;
//import com.leapmotion.leap.Screen;
//import com.leapmotion.leap.Vector;
//import java.util.UUID;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javafx.application.Platform;
//import javafx.geometry.Point2D;
//
//public class LeapListener extends Listener {
//	
//	private BooleanProperty frameReady = new SimpleBooleanProperty();
//	
//	
//	 Gesture gesture;
//     Boolean recording = false; 
//     int frameCount = 0;
//     int minGestureFrames = 5;	// The minimum number of recorded frames considered as possibly containing a recognisable gesture 
//     int minRecordingVelocity = 60; // The minimum velocity a frame needs to clock in at to trigger gesture recording, or below to stop gesture recording (by default)
//     int maxRecordingVelocity = 30;	// The maximum velocity a frame can measure at and still trigger pose recording, or above which to stop pose recording (by default)
//     Boolean stopRecording = false;  //says if recording should be stopped
//	
//	
//	
//	public void onFrame(Controller controller) {
//		Frame frame = controller.frame();
//		//System.out.println("1");
//		frameReady.set(false);
//		if (!frame.hands().isEmpty()) {
//			//System.out.println("Debug 0  " + frame.id());
//			frameReady.set(true);
//		
//		//System.out.println("2");
//	       if (stopRecording){ return;}
//	        
//	        if (recordableFrame(frame, minRecordingVelocity)){
//	    		System.out.println("Debug 3");
//	            /*
//	             * If this is the first frame in a gesture, we clean up some running values and fire the 'started-recording' event.
//	             */
//	            if (!recording) {
//	                recording = true; 
//	                frameCount = 0;
//	            }
//	            
//	            
//	            System.out.println("in frame... " + Long.toString(frame.id()));
//	            recordFrame(frame);
//	            frameCount++;
//	            System.out.println("Recording Frame...");
//	        }else if(recording){
//	            /*
//	             * If the frame should not be recorded but recording was active, then we deactivate recording and check to see if enough 
//	             * frames have been recorded to qualify for gesture recognition.
//	             */
//	            recording = false;
//	            /*
//	             * As soon as we're no longer recording, we fire the 'stopped-recording' function.
//	             */
//	            stopRecording();
//	                
//	            if (frameCount >= minGestureFrames){
//	                try {
//	                    saveTrainingGesture(gesture);
//	                } catch (IOException ex) {
//	                    Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
//	                }finally{
//	                    System.out.println("Gsture Saved");
//	                }
//	            }
//	        }
//		}
//	}
//
//	public BooleanProperty frameReadyProperty() {
//		return frameReady;
//	}
//	
//	
//    
//    /**
//     * This function is called for each frame during gesture recording, and it is responsible for adding values in frames using the provided 
//     * recordVector and recordValue functions (which accept a 3-value numeric array and a single numeric value respectively).
//     */
//    public void recordFrame(Frame frame) {
//        //HandList hands = frame.hands();
//        //gesture.hands = hands;
//        HandList hands = frame.hands();
//        int handCount = hands.count();
//
//        Hand hand; 
//        Finger finger; 
//        FingerList fingers; 
//        int fingerCount;
//               
//        int l = handCount;
//        for (int i = 0; i < l; i++) {   //for each hand in the frame
//            hand = hands.get(i);
//                   
//            recordPoint(hand.stabilizedPalmPosition());     //record the palm position
//            //System.out.println("Debug record");
//            fingers = hand.fingers();
//            fingerCount = fingers.count();
//		
//            int k = fingerCount;
//            for (int j = 0; j < k; j++) {   //for each finger in the hand
//                finger = fingers.get(j);
//                recordPoint(finger.stabilizedTipPosition());	//record the fingertip position.
//            }
//        }
//        
//        System.out.println("Recording Frame...");
//    }
//    
//    /**
//     * This function records a point to the gesture
//     * @param val 
//     */
//    public void recordPoint(Vector val){
//    	
//        double x,y,z;
//        //NaNs are replaced with 0.0, though they shouldn't occur!
//        if (Double.isNaN(val.getX())){
//            x=0.0;
//        }else{
//            x=val.getX();
//        }
//        
//        if (Double.isNaN(val.getY())){
//            y=0.0;
//        }else{
//            y=val.getY();
//        }
//        
//        if (Double.isNaN(val.getZ())){
//            z=0.0;
//        }else{
//            z=val.getZ();
//        }
//        
//        Point point = new Point(x, y, z);
//        System.out.println("Debug record 1");
//        gesture.add(point);
//        
//        System.out.println("Debug record 2");
//        
//    }
//    
//    /**
//     * This function returns TRUE if the provided frame should trigger recording and FALSE if it should stop recording.  
//     * 
//     * Of course, if the system isn't already recording, returning FALSE does nothing, and vice versa.. So really it returns 
//     * whether or not a frame may possibly be part of a gesture.
//     * 
//     * By default this function makes its decision based on one or more hands or fingers in the frame moving faster than the 
//     * configured minRecordingVelocity, which is provided as a second parameter.
//     * 
//     * @param frame
//     * @param min
//     * @param max
//     * @returns {Boolean}
//     */
//    public Boolean recordableFrame(Frame frame, int min){
//        
//        HandList hands = frame.hands();
//        int j;
//        Hand hand;
//        FingerList fingers;
//        double palmVelocity;
//        double tipVelocity;
//        Boolean poseRecordable = false;
//            
//        int l=hands.count();
//        //System.out.println(l);
//        for(int i=0; i<l; i++){
//            hand= hands.get(i); 
//            Vector palmVelocitys = hand.palmVelocity();
//            palmVelocity = Math.max(Math.abs(palmVelocitys.getX()), Math.abs(palmVelocitys.getY()));
//            palmVelocity = Math.max(palmVelocity, Math.abs(palmVelocitys.getZ()));
//                
//            /*
//             * We return true if there is a hand moving above the minimum recording velocity
//             */
//             if (palmVelocity >= min){return true;}
//                
//             fingers = hand.fingers(); 
//             int k = fingers.count();
//             for (j=0; j<k; j++){
//                 Vector tipVelocitys = fingers.get(j).tipVelocity();
//                 tipVelocity = Math.max(Math.abs(tipVelocitys.getX()), Math.abs(tipVelocitys.getY()));
//                 tipVelocity = Math.max(tipVelocity, Math.abs(tipVelocitys.getZ()));
//                    
//                /*
//                 * Or if there's a finger tip moving above the minimum recording velocity
//                 */
//                if (tipVelocity >= min) { return true; }
//            }
//        }
//            
//            
//        return false;
//    }
//    
//    public void saveTrainingGesture(Gesture gestureIn) throws IOException{
//        String fileName= gestureIn.name;
//        ObjectOutputStream out = null;
//        
//        try{
//            System.out.println("Saving Gesture...");
//            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
//            out.writeObject(gestureIn);
//        } catch (IOException ex) {
//            Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            out.close();
//        }
//    }
//    
//    public void stopRecording(){
//        stopRecording=true;
//    }	
//	
//	
//}




/*public class LeapListener extends Listener {

private final Driver app;	

public LeapListener() {this.app=null;}

public LeapListener(Driver main) {
    this.app = main;
}

public void onFrame(Controller controller) {
	Frame frame = controller.frame();
	if (!frame.hands().isEmpty()) {
		for (Hand hand : frame.hands()) {
			float x = hand.palmPosition().getX();
			float y = hand.palmPosition().getY();
			float z = hand.palmPosition().getZ();
			Platform.runLater(() -> {
				app.centerX().set(x);
				app.centerY().set(z);
				app.radius().set(50. - y / 5);
		});
		}
	}
}
}*/

/*public class LeapListener extends Listener {
	
	public void onConnect(Controller controller) {
		controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
		System.out.println("Connected");
	}

	public void onFrame(Controller controller) {
		System.out.println("Frame available");
		Frame frame = controller.frame();

		System.out.println("Frame id: " + frame.id()
						+ ", timestamp: " + frame.timestamp()
						+ ", hands : " + frame.hands().count()
						+ ", fingers: " + frame.fingers().count()
						+ ", tools : " + frame.tools().count()
						+ ", gestures : " + frame.gestures().count());

		for (Hand hand : frame.hands()) {
			String handType = hand.isLeft() ? "Left hand" : "Right hand";
			System.out.println("  " + handType + ", id: " + hand.id()
							+ ", palm position : " + hand.palmPosition());

			// Get the hand's normal vector and direction.
			Vector normal = hand.palmNormal();
			Vector direction = hand.direction();

			System.out.println("  pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
							+ "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
							+ "yaw: " + Math.toDegrees(direction.yaw()) + " degrees");

			Arm arm = hand.arm();
			System.out.println("  Arm direction: " + arm.direction()
							+ ", wrist position: " + arm.wristPosition()
							+ ", elbow position: " + arm.elbowPosition());

			for (Finger finger : hand.fingers()) {
				System.out.println("    " + finger.type() + ", id: " + finger.id()
								+ ", length: " + finger.length()
								+ "mm, width: " + finger.width() + "mm");

				for (Bone.Type boneType : Bone.Type.values()) {
					Bone bone = finger.bone(boneType);
					System.out.println("      " + bone.type()
									+ " bone, start: " + bone.prevJoint()
									+ ", end: " + bone.nextJoint()
									+ ", direction: " + bone.direction());
				}

			}

		}

		if (!frame.hands().isEmpty()) {
			System.out.println();
		}
	}
	
}*/