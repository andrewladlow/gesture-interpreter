package gestureinterpreter;

import java.util.List;
import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.Cylinder;
import javafx.scene.paint.Color;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Bone.Type;
import com.leapmotion.leap.Pointable.Zone;
import com.leapmotion.leap.Vector;

/**
 * Class representing a skeletal model of a human hand, taking position values from a LeapMotion sensor. 
 */
public class HandFX extends Group {
	private Menu app;
	private Sphere palm;
	private List<Sphere> fingerTips = new ArrayList<Sphere>(5);
	private List<Sphere> distals = new ArrayList<Sphere>(5);
	private List<Sphere> proximals = new ArrayList<Sphere>(5);
	private List<Sphere> intermediates = new ArrayList<Sphere>(5);
	private List<Sphere> metacarpals = new ArrayList<Sphere>(5);
	private List<Cylinder> bones = new ArrayList<Cylinder>(21);

    /**
     * Constructor to create this hand's bone and joint shape objects.
     * @param app The parent class of this object. 
     */
	public HandFX(Menu app) {
		this.app = app;
		palm = ShapeCreator.createSphere(10, Color.GREY, Color.SILVER);

		// create empty bone transforms 
		Rotate rotate = new Rotate();
		Translate translate = new Translate();
		
		// create joint (bone start / end point) shapes
		for (int i = 0; i < 5; i++) {
			fingerTips.add(i, ShapeCreator.createSphere(5, Color.GREY, Color.SILVER));
			distals.add(i, ShapeCreator.createSphere(5, Color.GREY, Color.SILVER));
			intermediates.add(i, ShapeCreator.createSphere(5, Color.GREY, Color.SILVER));
			proximals.add(i, ShapeCreator.createSphere(5, Color.GREY, Color.SILVER));
			metacarpals.add(i, ShapeCreator.createSphere(5, Color.GREY, Color.SILVER));
		}
		
		// create bone shapes for each finger
		// include extra bones for knuckles and bones to connect hand metacarpals
		for (int i = 0; i < 21; i++) {
			bones.add(i, ShapeCreator.createCylinder(3, Color.LIGHTGREY, Color.WHITE));
			bones.get(i).getTransforms().add(translate);
			bones.get(i).getTransforms().add(rotate);
		}

		// add all shapes to the single HandFX object instance
		this.getChildren().add(palm);
		this.getChildren().addAll(fingerTips);
		this.getChildren().addAll(distals);
		this.getChildren().addAll(intermediates);
		this.getChildren().addAll(proximals);
		this.getChildren().addAll(metacarpals);
		this.getChildren().addAll(bones);
	}

    /**
     * Handles collision of a finger and a button to trigger button presses by physical action.
     * @param finger The finger to check for interaction.
     * @param shape The shape representing this finger.
     * @param app The parent class of this object.
     */
	public void checkIntersect(Finger finger, Sphere shape, Menu app) {
		Boolean touchFlag = false;
		String text = "";
		for (LeapButton button : app.getLeapButtons()) {
			// check that there's both an intersect between finger and button, and touch emulation is triggered
			Bounds shapeBounds = shape.localToScene(shape.getBoundsInLocal());
			Bounds buttonBounds = button.localToScene(button.getBoundsInLocal());
			if (!button.touchStatusProperty().getValue() && shapeBounds.intersects(buttonBounds) && finger.touchZone() == Zone.ZONE_TOUCHING) {
				button.touchStatusProperty().set(true);
			} 
			else if (button.touchStatusProperty().getValue() && !shapeBounds.intersects(buttonBounds) && finger.touchZone() != Zone.ZONE_TOUCHING) {
				touchFlag = true;
				button.touchStatusProperty().set(false);
				text = button.getText();
			}
		}
		if (touchFlag) {
			app.swapScene(text);
			touchFlag = false;
		}	
	}

    /**
     * Updates position of this hand, using the raw data for this hand from LeapMotion. 
     * @param hand The LeapMotion hand to take position data from.
     */
	public void update(Hand hand) {
		LeapToFX.move(palm, hand.palmPosition());
		
		Finger finger;
		// update positions of all finger joints
		for (int i = 0; i < hand.fingers().count(); i++) {
			finger = hand.fingers().get(i);
			LeapToFX.move(fingerTips.get(i), finger.tipPosition());
			// check for button presses on finger tips
			checkIntersect(hand.fingers().frontmost(), fingerTips.get(i), app);
			LeapToFX.move(distals.get(i), finger.bone(Type.TYPE_DISTAL).prevJoint());
			LeapToFX.move(intermediates.get(i), finger.bone(Type.TYPE_INTERMEDIATE).prevJoint());
			LeapToFX.move(proximals.get(i), finger.bone(Type.TYPE_PROXIMAL).prevJoint());
			// hide 3rd and 4th metacarpals off screen
			if (i == 2 || i == 3) {
				LeapToFX.move(metacarpals.get(i), new Vector(0, -50, 0));
			} 
			else {
				LeapToFX.move(metacarpals.get(i), finger.bone(Type.TYPE_METACARPAL).prevJoint());
			}

			// update positions of all finger bones
			updateBone(distals.get(i), fingerTips.get(i), i);
			updateBone(intermediates.get(i), distals.get(i), i+5);
			updateBone(proximals.get(i), intermediates.get(i), i+10);
		}
		// update positions of other bones (knuckles, index and pinky metacarpals to proximals + metacarpals to metacarpals)
		updateBone(proximals.get(1), proximals.get(2), 15);
		updateBone(proximals.get(2), proximals.get(3), 16);
		updateBone(proximals.get(3), proximals.get(4), 17);
		updateBone(proximals.get(1), metacarpals.get(1), 18);
		updateBone(proximals.get(4), metacarpals.get(4), 19);
		updateBone(metacarpals.get(1), metacarpals.get(4), 20);
	}
	
	/**
	 * Updates the position and rotation of a bone
	 * between it's two joints.
	 * @param toPos The end joint of the two joints connected by the bone
     * @param fromPos The start joint of the two joints connected by the bone
     * @param boneID The index of the bone to update
	 */	
	public void updateBone(Sphere toPos, Sphere fromPos, int boneID) {
		// distance between the two joints
		double distX = toPos.getTranslateX() - fromPos.getTranslateX();
		double distY = toPos.getTranslateY() - fromPos.getTranslateY();
		double distZ = toPos.getTranslateZ() - fromPos.getTranslateZ();
		
		Cylinder bone = bones.get(boneID);
		
		// set bone length to fit between both joints
		double boneLength = Math.sqrt((distX * distX) + (distY * distY) + (distZ * distZ));
		bone.setHeight(boneLength);
		
		// set bone rotation to align it correctly between both joints
		Rotate rotate = new Rotate();
		rotate.setPivotY(boneLength / 2);		
		Vector distance = new Vector((float) distX, (float) distY, (float) distZ);
		double angle = distance.angleTo(Vector.down());
        Vector cross = distance.cross(Vector.up());       
        rotate.setAngle(Math.toDegrees(angle));
        rotate.setAxis(new Point3D(cross.getX(), cross.getY(), cross.getZ()));  
		
        // apply translation and rotation to bone 
		bone.getTransforms().set(0, new Translate(fromPos.getTranslateX(), 
											      fromPos.getTranslateY() - boneLength / 2, 
											      fromPos.getTranslateZ()));	
		bone.getTransforms().set(1, rotate);
	}
}