package gestureinterpreter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import com.leapmotion.leap.Bone.Type;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;

public class HandFX extends Group {
	private Sphere palm;
	
	private ArrayList<Sphere> fingers = new ArrayList<Sphere>(5);
	private ArrayList<Sphere> distals = new ArrayList<Sphere>(5);
	private ArrayList<Sphere> proximals = new ArrayList<Sphere>(5);
	private ArrayList<Sphere> intermediates = new ArrayList<Sphere>(5);
	private ArrayList<Sphere> metacarpals = new ArrayList<Sphere>(5);

	private ArrayList<JointFX> joints;

	public HandFX() {
		joints = new ArrayList<JointFX>();

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
		
		this.getChildren().addAll(palm);
		this.getChildren().addAll(fingers);
		this.getChildren().addAll(distals);
		this.getChildren().addAll(intermediates);
		this.getChildren().addAll(proximals);
		this.getChildren().addAll(metacarpals);
	}

	private void connectJoints(Sphere fromJoint, Sphere toJoint) {
		JointFX jointFX = new JointFX(fromJoint, toJoint);
		joints.add(jointFX);
		this.getChildren().add(jointFX.getBone());
	}

	public void update(Hand hand) {
		LeapToFX.move(palm, hand.palmPosition());

		Iterator<Finger> itFinger = hand.fingers().iterator();

		Finger finger = null;
		for (int i = 0; i < 5; i++) {
			finger = itFinger.next();

			LeapToFX.move(fingers.get(i), finger.tipPosition());
			LeapToFX.move(distals.get(i), finger.bone(Type.TYPE_DISTAL).prevJoint());
			LeapToFX.move(intermediates.get(i), finger.bone(Type.TYPE_INTERMEDIATE).prevJoint());
			LeapToFX.move(proximals.get(i), finger.bone(Type.TYPE_PROXIMAL).prevJoint());
			if (i == 1 || i == 0 || i == 4) {
				LeapToFX.move(metacarpals.get(i), finger.bone(Type.TYPE_METACARPAL).prevJoint());
			}
		}
		
		for (JointFX joint : joints) {
			joint.update();
		}
	}

	private class JointFX {
		private Sphere fromSphere;
		private Sphere toSphere;
		private Cylinder bone;
		private Rotate joint;

		public JointFX(Sphere fromSphere, Sphere toSphere) {
			this.fromSphere = fromSphere;
			this.toSphere = toSphere;
			this.joint = new Rotate();
			this.bone = ShapeCreator.createCylinder(3, Color.LIGHTGREY, Color.WHITE, joint);
		}

		public void update() {
			double dx = (float) (fromSphere.getTranslateX() - toSphere.getTranslateX());
			double dy = (float) (fromSphere.getTranslateY() - toSphere.getTranslateY());
			double dz = (float) (fromSphere.getTranslateZ() - toSphere.getTranslateZ());

			bone.setHeight(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2)));
			bone.setTranslateX(fromSphere.getTranslateX());
			bone.setTranslateY(fromSphere.getTranslateY() - bone.getHeight() / 2);
			bone.setTranslateZ(fromSphere.getTranslateZ());

			joint.setPivotY(bone.getHeight() / 2);
			joint.setAxis(new Point3D(dz, 0, -dx));
			joint.setAngle(180 - new Point3D(dx, -dy, dz).angle(Rotate.Y_AXIS));
		}

		public Cylinder getBone() {
			return bone;
		}
	}
}