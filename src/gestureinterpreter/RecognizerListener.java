package gestureinterpreter;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.ArrayList;

import javafx.application.Platform;

/**
 * Class handling recognition of gestures, extends the Leap Motion listener
 * class.
 */
public class RecognizerListener extends AbstractGestureListener {
    private Gesture gesture;
    private List<Gesture> storedGestures;
    private PDollarRecognizer pdRec;
    private RecognizerGUI recGUI;

    /**
     * Creates a new instance of a recognizer listener.
     * 
     * @param recGUI The gui this listener is associated with.
     */
    public RecognizerListener(RecognizerGUI recGUI) {
        pdRec = new PDollarRecognizer();
        this.recGUI = recGUI;
        storedGestures = new ArrayList<Gesture>();
        loadFiles(new File("."));
    }

    /**
     * Called when this listener is added to a controller.
     * 
     * @param controller The leap motion controller to check.
     */
    public void onConnect(Controller controller) {
        System.out.println("Connected recognizer");
    }

    /**
     * Called when this listener is disconnected from a controller.
     * 
     * @param controller The leap motion controller to check.
     */
    public void onExit(Controller controller) {
        System.out.println("Disconnected recognizer");
    }

    /**
     * Called when a new frame of tracking data is available.
     * 
     * @param controller The leap motion controller to poll.
     */
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        if (!frame.hands().isEmpty()) {
            // enforce small delay between recognitions to reduce recognition spam
            if (System.currentTimeMillis() - getTimeRecognized() > 200) {
                if (validFrame(frame, getMinGestureVelocity(), getMaxPoseVelocity())) {
                    if (!isRecording()) {
                        setGestureFrameCount(0);
                        setPoseFrameCount(0);
                        gesture = new Gesture("testGesture");
                        setRecording(true);
                    }
                    setGestureFrameCount(getGestureFrameCount() + 1);
                    // System.out.println("gesture frame count: " + gestureFrameCount);
                    storePoint(frame, gesture);
                } else if (isRecording()) {
                    setRecording(false);
                    if (isValidPose() || (getGestureFrameCount() >= getMinGestureFrames())) {
                        if (isValidPose()) {
                            gesture.setType("pose");
                        } else {
                            gesture.setType("gesture");
                        }
                        RecognizerResults recResult = pdRec.recognize(gesture, storedGestures);
                        setTimeRecognized(System.currentTimeMillis());
                        Platform.runLater(() -> {
                            recGUI.gestureRecognitionProperty().set(recResult);
                        });
                    }
                    setValidPose(false);
                }
            }
        }
    }
    
    /**
     * Loads gesture files recursively to account for each gesture sample set in
     * different folders
     * 
     * @param filePath The path to load.
     */
    public void loadFiles(File filePath) {
        try {
            File[] files = filePath.listFiles();
            for (File file : files) {
                if (file.getName().startsWith("gestureSet")) {
                    loadFiles(file);
                } else if (file.getParentFile().getName().startsWith("gestureSet")) {
                    Gesture storedGesture = loadGesture(file);

                    // apply pre-processing prior to memory storage (normalisation)
                    storedGesture.setPointArray(PDollarRecognizer.resample(storedGesture.getPointArray(), PDollarRecognizer.getNumPoints()));
                    storedGesture.setPointArray(PDollarRecognizer.scale(storedGesture.getPointArray()));
                    storedGesture.setPointArray(PDollarRecognizer.translateTo(storedGesture.getPointArray(), new Point(0.0, 0.0, 0.0)));

                    storedGestures.add(storedGesture);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads gesture from a given file name.
     * 
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gesture;
    }

}