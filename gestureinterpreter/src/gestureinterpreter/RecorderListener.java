package gestureinterpreter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Class handling recording of new gestures, extends the Leap Motion listener
 * class.
 *
 * @see RecorderEvent
 */
public class RecorderListener extends AbstractGestureListener {
    private final Object lock;  
    
    private Gesture gesture;
    private BooleanProperty gestureDone = new SimpleBooleanProperty();

    /**
     * Creates a new instance of a recorder listener.
     * 
     * @param c The character this listener is recording.
     * @param lock The shared lock between this listener and the gui.
     */
    public RecorderListener(char c, Object lock) {
        this.lock = lock;
        setGesture(new Gesture(c));
    }

    public Gesture getGesture() {
        return gesture;
    }

    public void setGesture(Gesture gesture) {
        this.gesture = gesture;
    } 
    
    public BooleanProperty gestureDoneProperty() {
        return gestureDone;
    }

    /**
     * Called when this listener is added to a controller.
     * 
     * @param controller The leap motion controller to check.
     */
    public void onConnect(Controller controller) {
        System.out.println("Connected recorder");
    }

    /**
     * Called when this listener is disconnected from a controller.
     * 
     * @param controller The leap motion controller to check.
     */
    public void onExit(Controller controller) {
        System.out.println("Disconnected recorder");
    }

    /**
     * Called when a new frame of tracking data is available.
     * 
     * @param controller The leap motion controller to poll.
     */
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        if (!frame.hands().isEmpty()) {
            // enforce delay between recognitions
            if (System.currentTimeMillis() - getTimeRecognized() > 5000) {
                if (validFrame(frame, getMinGestureVelocity(), getMaxPoseVelocity())) {
                    if (!isRecording()) {
                        setGestureFrameCount(0);
                        setPoseFrameCount(0);
                        setRecording(true);
                    }
                    setGestureFrameCount(getGestureFrameCount() + 1);
                    // System.out.println("gesture frame count: " +
                    // gestureFrameCount);
                    storePoint(frame, getGesture());
                } else if (isRecording()) {
                    setRecording(false);
                    if (isValidPose() || (getGestureFrameCount() >= getMinGestureFrames())) {
                        if (isValidPose()) {
                            getGesture().setType("pose");
                        } else {
                            getGesture().setType("gesture");
                        }
                        saveGesture(getGesture());
                        setTimeRecognized(System.currentTimeMillis());
                        gestureDone.set(true);
                    }
                    setValidPose(false);
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            }
        }
    }
    
    /**
     * Saves a given gesture to disk.
     * 
     * @param gesture The gesture to save.
     */
    public void saveGesture(Gesture gesture) {
        try {
            // find the last created gestureSet folder
            File topDir = new File(".");
            File[] files = topDir.listFiles(fileName -> fileName.getName().startsWith("gestureSet"));
            int setDirCount = 1;
            // if there are no previous gesture sets, create first folder
            if (files.length == 0) {
                File newSetDir = new File("gestureSet1");
                newSetDir.mkdir();
            }
            // else find last folder number
            else {
                String lastDir = files[files.length - 1].getName();
                setDirCount = Integer.parseInt(lastDir.replaceAll("\\D", ""));

                // if the gesture set is full (all 26 letters present) we need
                // to create a new set folder
                // otherwise we use the last created folder
                if (files[files.length - 1].list().length == 26) {
                    setDirCount++;
                    File newSetDir = new File("gestureSet" + setDirCount);
                    newSetDir.mkdir();
                }
            }

            System.out.println("saving " + gesture.getName());
            FileOutputStream outStream = new FileOutputStream(new File("gestureSet" + setDirCount + "/" + gesture.getName()), false);
            ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
            objOutStream.writeObject(gesture);
            objOutStream.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}