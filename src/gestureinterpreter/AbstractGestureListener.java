package gestureinterpreter;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;
import com.leapmotion.leap.Bone.Type;

/**
 * Abstract listener class handling gesture storing & recognition. 
 */
abstract class AbstractGestureListener extends Listener {
    private final int MIN_GESTURE_FRAMES = 10;
    private final int MIN_GESTURE_VELOCITY = 300;
    private final int MIN_POSE_FRAMES = 50;
    private final int MAX_POSE_VELOCITY = 30;
    
    private int gestureFrameCount = 0;
    private int poseFrameCount = 0;
    private boolean validPoseFrame = false;
    private boolean validPose = false;
    private boolean recording = false;
    private long timeRecognized = 0;
    
    public int getGestureFrameCount() {
        return gestureFrameCount;
    }

    public void setGestureFrameCount(int gestureFrameCount) {
        this.gestureFrameCount = gestureFrameCount;
    }

    public boolean isValidPose() {
        return validPose;
    }

    public void setValidPose(boolean validPose) {
        this.validPose = validPose;
    }

    public boolean isValidPoseFrame() {
        return validPoseFrame;
    }

    public void setValidPoseFrame(boolean validPoseFrame) {
        this.validPoseFrame = validPoseFrame;
    }

    public int getMinGestureFrames() {
        return MIN_GESTURE_FRAMES;
    }

    public int getMinGestureVelocity() {
        return MIN_GESTURE_VELOCITY;
    }
    
    public int getMinPoseFrames() {
        return MIN_POSE_FRAMES;
    }
    
    public int getMaxPoseVelocity() {
        return MAX_POSE_VELOCITY;
    }

    public int getPoseFrameCount() {
        return poseFrameCount;
    }

    public void setPoseFrameCount(int poseFrameCount) {
        this.poseFrameCount = poseFrameCount;
    }

    public long getTimeRecognized() {
        return timeRecognized;
    }

    public void setTimeRecognized(long timeRecognized) {
        this.timeRecognized = timeRecognized;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    /**
     * Returns a boolean signifying whether a given frame should be recorded or
     * not.
     * 
     * @param frame The frame to check.
     * @param minVelocity The minimum velocity which a hand must move at to
     * return true.
     * @param maxVelocity The maximum velocity a hand must remain under to
     * trigger a pose.
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
            } else if (palmVelocity <= maxVelocity) {
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
                } else if (fingerVelocity <= maxVelocity) {
                    validPoseFrame = true;
                    break;
                }
            }
        }
        if (validPoseFrame) {
            setPoseFrameCount(getPoseFrameCount() + 1);
            // System.out.println("pose frame count: " + poseFrameCount);
            if (getPoseFrameCount() >= getMinPoseFrames()) {
                setValidPose(true);
                return true;
            }
        } else {
            setPoseFrameCount(0);
        }
        return false;
    }

    /**
     * Stores points from a given frame to the current gesture's array.
     * 
     * @param frame The current frame to add gesture points from.
     * @param gesture The gesture to add points to.
     */
    public void storePoint(Frame frame, Gesture gesture) {
        for (Hand hand : frame.hands()) {
            for (Finger finger : hand.fingers()) {
                gesture.addPoint(new Point(finger.bone(Type.TYPE_METACARPAL).nextJoint().minus(hand.palmPosition())));
                gesture.addPoint(new Point(finger.bone(Type.TYPE_PROXIMAL).nextJoint().minus(hand.palmPosition())));
                gesture.addPoint(new Point(finger.bone(Type.TYPE_INTERMEDIATE).nextJoint().minus(hand.palmPosition())));
                gesture.addPoint(new Point(finger.bone(Type.TYPE_DISTAL).nextJoint().minus(hand.palmPosition())));
            }
        }
    }
    
}
