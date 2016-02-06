package gestureinterpreter;

import java.util.ArrayList;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;

import com.leapmotion.leap.Bone.Type;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Pointable.Zone;
import com.leapmotion.leap.Vector;

public class HandFX extends Group {
	private Menu app;
	private Sphere palm;
	private ArrayList<Sphere> fingers = new ArrayList<Sphere>(5);
	private ArrayList<Sphere> distals = new ArrayList<Sphere>(5);
	private ArrayList<Sphere> proximals = new ArrayList<Sphere>(5);
	private ArrayList<Sphere> intermediates = new ArrayList<Sphere>(5);
	private ArrayList<Sphere> metacarpals = new ArrayList<Sphere>(5);
	private ArrayList<JointFX> joints = new ArrayList<JointFX>();

	// handles creation of 3D representation of a user's hand
	public HandFX(Menu app) {
		this.app = app;

		palm = ShapeCreator.createSphere(this, 10, Color.GREEN, Color.LIGHTGREEN);

		for (int i = 0; i < 5; i++) {
			fingers.add(i, ShapeCreator.createSphere(this, 5, Color.LIGHTGREEN, Color.GREENYELLOW));
			distals.add(i, ShapeCreator.createSphere(this, 5, Color.LIGHTGREEN, Color.GREENYELLOW));
			intermediates.add(i, ShapeCreator.createSphere(this, 5, Color.LIGHTGREEN, Color.GREENYELLOW));
			proximals.add(i, ShapeCreator.createSphere(this, 5, Color.LIGHTGREEN, Color.GREENYELLOW));
			metacarpals.add(i, ShapeCreator.createSphere(this, 5, Color.LIGHTGREEN, Color.GREENYELLOW));

			connectJoints(fingers.get(i), distals.get(i));
			connectJoints(distals.get(i), intermediates.get(i));
			connectJoints(intermediates.get(i), proximals.get(i));
		}

		connectJoints(proximals.get(1), proximals.get(2));
		connectJoints(proximals.get(2), proximals.get(3));
		connectJoints(proximals.get(3), proximals.get(4));
		connectJoints(proximals.get(1), metacarpals.get(1));
		connectJoints(proximals.get(4), metacarpals.get(4));
		connectJoints(metacarpals.get(1), metacarpals.get(4));

		this.getChildren().add(palm);
		this.getChildren().addAll(fingers);
		this.getChildren().addAll(distals);
		this.getChildren().addAll(intermediates);
		this.getChildren().addAll(proximals);
		this.getChildren().addAll(metacarpals);
	}

	// associates a given joint with the bones behind and in front of it (bone -
	// joint - bone)
	private void connectJoints(Sphere fromJoint, Sphere toJoint) {
		JointFX jointFX = new JointFX(fromJoint, toJoint);
		joints.add(jointFX);
		this.getChildren().add(jointFX.getBone());
	}

	// updates position of user's hand, taking raw data from LeapMotion listener
	public void update(Hand hand) {
		LeapToFX.move(palm, hand.palmPosition());
		Finger finger;

		for (int i = 0; i < hand.fingers().count(); i++) {
			finger = hand.fingers().get(i);
			LeapToFX.move(fingers.get(i), finger.tipPosition());
			checkIntersect(hand.fingers().frontmost(), fingers.get(i), app);

			LeapToFX.move(distals.get(i), finger.bone(Type.TYPE_DISTAL).prevJoint());
			LeapToFX.move(intermediates.get(i), finger.bone(Type.TYPE_INTERMEDIATE).prevJoint());
			LeapToFX.move(proximals.get(i), finger.bone(Type.TYPE_PROXIMAL).prevJoint());
			// hide 3rd and 4th metacarpals off screen
			if (i == 2 || i == 3) {
				LeapToFX.move(metacarpals.get(i), new Vector(0, 0, 100));
			} else {
				LeapToFX.move(metacarpals.get(i), finger.bone(Type.TYPE_METACARPAL).prevJoint());
			}
		}
		for (JointFX joint : joints) {
			joint.update();
		}
	}

	// handles collision of a finger and a button to trigger button presses by physical actions
	private void checkIntersect(Finger finger, Sphere shape, Menu app) {
		for (LeapButton button : app.getLeapButtons()) {
			// check that there's both an intersect between finger and button, and touch emulation is triggered
			Bounds shapeBounds = shape.localToScene(shape.getBoundsInLocal());
			Bounds buttonBounds = button.localToScene(button.getBoundsInLocal());
			if (!button.touchStatusProperty().getValue() && shapeBounds.intersects(buttonBounds) && finger.touchZone() == Zone.ZONE_TOUCHING) {
				button.touchStatusProperty().set(true);
			} else if (button.touchStatusProperty().getValue() && !shapeBounds.intersects(buttonBounds) && finger.touchZone() != Zone.ZONE_TOUCHING) {
				button.touchStatusProperty().set(false);
				app.swapScene(button.getText());
			}
		}
	}
}