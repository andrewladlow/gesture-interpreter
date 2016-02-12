package gestureinterpreter;

import com.leapmotion.leap.Vector;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class JointFX {
	private Sphere fromSphere;
	private Sphere toSphere;
	private Cylinder bone;
	private Rotate rotation;

	public JointFX(Sphere fromJoint, Sphere toJoint) {
		fromSphere = fromJoint;
		toSphere = toJoint;
		rotation = new Rotate();
		bone = ShapeCreator.createCylinder(3, Color.LIGHTGREY, Color.WHITE);
		bone.getTransforms().add(rotation);
	}

	
	public void update() {
		// distance between two joints
/*		float dx = (float) (fromSphere.getTranslateX() - toSphere.getTranslateX());
		float dy = (float) (fromSphere.getTranslateY() - toSphere.getTranslateY());
		float dz = (float) (fromSphere.getTranslateZ() - toSphere.getTranslateZ());*/
		
		float dx = (float) (toSphere.getTranslateX() - fromSphere.getTranslateX());
		float dy = (float) (toSphere.getTranslateY() - fromSphere.getTranslateY());
		float dz = (float) (toSphere.getTranslateZ() - fromSphere.getTranslateZ());
		
		// align bone to position of 'lower' joint
		bone.setHeight(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2)));
		bone.setTranslateX(fromSphere.getTranslateX());
		bone.setTranslateY(fromSphere.getTranslateY() - bone.getHeight() / 2);
		bone.setTranslateZ(fromSphere.getTranslateZ());

		// apply rotations to bone to align from lower to upper joints
		// below created with guidance from http://ruzman.de/blog/leap-motion-javafx-3d-hand-2
		rotation.setPivotY(bone.getHeight() / 2);
		Vector leapVector = new Vector(dx, dy, dz);
		Vector leapAxis = leapVector.cross(Vector.down());
		Point3D axis = new Point3D(-leapAxis.getX(), leapAxis.getY(), leapAxis.getZ());
		rotation.setAxis(axis);
		rotation.setAngle(180 - new Point3D(dx, dy, dz).angle(new Point3D(0, -1, 0)));
	}

	public Cylinder getBone() {
		return bone;
	}
}