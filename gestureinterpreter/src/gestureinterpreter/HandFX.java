package gestureinterpreter;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;

import com.leapmotion.leap.Bone.Type;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Pointable.Zone;
import com.leapmotion.leap.Vector;

/**
 * Class representing a skeletal model of a human hand, taking position values from a LeapMotion sensor. 
 */
public class HandFX extends Group {
	private Menu app;
	private Sphere palm;
	private List<Sphere> fingerTips = new ArrayList<Sphere>();
	private List<Sphere> distals = new ArrayList<Sphere>();
	private List<Sphere> proximals = new ArrayList<Sphere>();
	private List<Sphere> intermediates = new ArrayList<Sphere>();
	private List<Sphere> metacarpals = new ArrayList<Sphere>();
	private List<JointFX> joints = new ArrayList<JointFX>();

    /**
     * Constructor to create this hand's bone and joint shape objects.
     * @param app The parent class of this object. 
     */
	public HandFX(Menu app) {
		this.app = app;
		palm = ShapeCreator.createSphere(10, Color.GREY, Color.SILVER);

		for (int i = 0; i < 5; i++) {
			fingerTips.add(i, ShapeCreator.createSphere(5, Color.GREY, Color.SILVER));
			distals.add(i, ShapeCreator.createSphere(5, Color.GREY, Color.SILVER));
			intermediates.add(i, ShapeCreator.createSphere(5, Color.GREY, Color.SILVER));
			proximals.add(i, ShapeCreator.createSphere(5, Color.GREY, Color.SILVER));
			metacarpals.add(i, ShapeCreator.createSphere(5, Color.GREY, Color.SILVER));

			connectJoints(distals.get(i), fingerTips.get(i));
			connectJoints(intermediates.get(i), distals.get(i));
			connectJoints(proximals.get(i), intermediates.get(i));
		}

		connectJoints(proximals.get(1), proximals.get(2));
		connectJoints(proximals.get(2), proximals.get(3));
		connectJoints(proximals.get(3), proximals.get(4));
		connectJoints(proximals.get(1), metacarpals.get(1));
		connectJoints(proximals.get(4), metacarpals.get(4));
		connectJoints(metacarpals.get(1), metacarpals.get(4));

		this.getChildren().add(palm);
		this.getChildren().addAll(fingerTips);
		this.getChildren().addAll(distals);
		this.getChildren().addAll(intermediates);
		this.getChildren().addAll(proximals);
		this.getChildren().addAll(metacarpals);
	}

    /**
     * Links two given joint positions together, handles the properties of
     * the bone in between these two joints.
     * @param fromJoint The first of the two joints to be linked.
     * @param toJoint The second of the two joints to be linked.
     */
	public void connectJoints(Sphere fromJoint, Sphere toJoint) {
		JointFX jointFX = new JointFX(fromJoint, toJoint);
		joints.add(jointFX);
		this.getChildren().add(jointFX.getBone());
	}

    /**
     * Updates position of this hand, using the raw data for this hand from LeapMotion. 
     * @param hand The LeapMotion hand to take position data from.
     */
	public void update(Hand hand) {
		LeapToFX.move(palm, hand.palmPosition());
		
		Finger finger;
		for (int i = 0; i < hand.fingers().count(); i++) {
			finger = hand.fingers().get(i);
			LeapToFX.move(fingerTips.get(i), finger.tipPosition());
			checkIntersect(hand.fingers().frontmost(), fingerTips.get(i), app);

			LeapToFX.move(distals.get(i), finger.bone(Type.TYPE_DISTAL).prevJoint());
			LeapToFX.move(intermediates.get(i), finger.bone(Type.TYPE_INTERMEDIATE).prevJoint());
			LeapToFX.move(proximals.get(i), finger.bone(Type.TYPE_PROXIMAL).prevJoint());
			// hide 3rd and 4th metacarpals off screen
			if (i == 2 || i == 3) {
				LeapToFX.move(metacarpals.get(i), new Vector(0, 0, 100));
			} 
			else {
				LeapToFX.move(metacarpals.get(i), finger.bone(Type.TYPE_METACARPAL).prevJoint());
			}
		}
		for (JointFX joint : joints) {
			joint.update();
		}
	}

    /**
     * Handles collision of a finger and a button to trigger button presses by physical action.
     * @param finger The finger to check for interaction.
     * @param shape The shape representing this finger.
     * @param app The parent class of this object.
     */
	private void checkIntersect(Finger finger, Sphere shape, Menu app) {
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
			touchFlag = false;
			app.swapScene(text);
		}
		
		
	}
	
	public void clear() {
		this.clear();
	}
}