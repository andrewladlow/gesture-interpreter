package gestureinterpreter;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class JointFX {
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