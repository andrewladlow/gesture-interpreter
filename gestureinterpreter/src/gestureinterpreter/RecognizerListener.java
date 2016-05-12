package gestureinterpreter;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Finger;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.application.Platform;

import com.leapmotion.leap.Vector;
import com.leapmotion.leap.Bone.Type;

/**
 * Class handling recognition of gestures, extends the 
 * Leap Motion listener class. 
 */
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
    private PDollarRecognizer pdRec;  
    private RecognizerGUI recGUI; 

    /**
     * Handles the listener's state - either
     * 'idle' or 'recording'. 
     */
    private enum State {
    	IDLE, RECORDING;
    }
    
    /**
     * Creates a new instance of a recognizer listener.
     * @param recGUI The gui this listener is associated with. 
     */
    public RecognizerListener(RecognizerGUI recGUI) {
    	pdRec = new PDollarRecognizer();
    	this.recGUI = recGUI;
    	storedGestures = new ArrayList<Gesture>();
    	state = State.IDLE;
		loadFiles(new File("."));
    }
    
    /**
     * Loads gesture files recursively to account for
     * each gesture sample set in different folders
     * @param filePath The path to load.
     */
	public void loadFiles(File filePath) {
    	try {
    	   	File[] files = filePath.listFiles();
			for (File file : files) {
				if (file.getName().startsWith("gestureSet")) {
					loadFiles(file);
				}
				else if (file.getParentFile().getName().startsWith("gestureSet")) {
					Gesture storedGesture = loadGesture(file);

					storedGesture.setPointArray(PDollarRecognizer.Resample(storedGesture.getPointArray(), PDollarRecognizer.mNumPoints));
					storedGesture.setPointArray(PDollarRecognizer.Scale(storedGesture.getPointArray()));
					storedGesture.setPointArray(PDollarRecognizer.TranslateTo(storedGesture.getPointArray(), new Point(0.0,0.0,0.0)));
					
					storedGestures.add(storedGesture);
				}
			}
    	} 
    	catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
    /**
     * Loads gesture from a given file name.
     * @param fileName The file containing the gesture.
     */
	public Gesture loadGesture(File fileName) {
		Gesture gesture = null;
		try {
			FileInputStream inStream = new FileInputStream(fileName);
			ObjectInputStream ObjInStream = new ObjectInputStream(inStream);
			gesture = (Gesture) ObjInStream.readObject();
			
			ObjInStream.close();
			inStream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return gesture;
	}
    
    /**
     * Called when this listener is added to a controller.
     * @param controller The leap motion controller to check. 
     */
    public void onConnect(Controller controller) {
    	System.out.println("connected recognizer");
    }
    
    /**
     * Called when this listener is disconnected from a controller.
     * @param controller The leap motion controller to check. 
     */
    public void onExit(Controller controller) {
    	System.out.println("disconnected recognizer");
    }
	
    /**
     * Called when a new frame of tracking data is available.
     * @param controller The leap motion controller to poll. 
     */	
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
		if (!frame.hands().isEmpty()) {					
			// enforce small delay between recognitions to reduce recognition spam
			if (System.currentTimeMillis() - timeRecognized > 200) {	        
		        if (validFrame(frame, minGestureVelocity, maxPoseVelocity)) {	            	          
		            if (state == State.IDLE) {
		            	gestureFrameCount = 0;
		            	poseFrameCount = 0;
		            	gesture = new Gesture("testGesture");
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
		            	long time1 = System.nanoTime();
		                RecognizerResults recResult = pdRec.Recognize(gesture, storedGestures);
		                long time2 = System.nanoTime();
		                // convert ns to ms, rounded to 2 decimal places
		                DecimalFormat df = new DecimalFormat("###.##");
		                System.out.println("Time taken: " + df.format(Math.round(time2 - time1) / 1e6) + " ms");
		                timeRecognized = System.currentTimeMillis();
		                Platform.runLater(() -> {
				            recGUI.gestureRecognitionProperty().set(recResult);
		                });
		            }		            
	                validPose = false;
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
     * @param frame The current frame to add gesture points from.
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
    			// testing alternate features
/*    			gesture.addPoint(new Point(finger.bone(Type.TYPE_METACARPAL).nextJoint().minus(hand.palmPosition())));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_PROXIMAL).nextJoint().minus(hand.palmPosition())));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_INTERMEDIATE).nextJoint().minus(hand.palmPosition())));
    			gesture.addPoint(new Point(finger.bone(Type.TYPE_DISTAL).nextJoint().minus(hand.palmPosition())));*/

    		}
    	}
    } 
}