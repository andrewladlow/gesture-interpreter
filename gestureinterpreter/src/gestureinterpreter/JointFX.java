package gestureinterpreter;

import com.leapmotion.leap.Vector;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

/**
 * Class handling the joints of a human hand,
 * and the bones connecting them together.
 */
public class JointFX {
	private Sphere fromSphere;
	private Sphere toSphere;
	private Cylinder bone;
	private Rotate rotation;

	/**
	 * Links the two given joint positions together.
	 * @param fromJoint The first joint position.
	 * @param toJoint The second joint position. 
	 */
	public JointFX(Sphere fromJoint, Sphere toJoint) {
		fromSphere = fromJoint;
		toSphere = toJoint;
		rotation = new Rotate();
		bone = ShapeCreator.createCylinder(3, Color.LIGHTGREY, Color.WHITE);
		bone.getTransforms().add(rotation);
	}

	/**
	 * Updates the position and rotation of a bone
	 * between it's two joints. 
	 */
	public void update() {
		// distance between the two joints
		float dx = (float) (toSphere.getTranslateX() - fromSphere.getTranslateX());
		float dy = (float) (toSphere.getTranslateY() - fromSphere.getTranslateY());
		float dz = (float) (toSphere.getTranslateZ() - fromSphere.getTranslateZ());
		
		// align bone to position of from joint
		bone.setHeight(Math.sqrt(dx * dx + dy * dy + dz * dz));
		bone.setTranslateX(fromSphere.getTranslateX());
		bone.setTranslateY(fromSphere.getTranslateY() - bone.getHeight() / 2);
		bone.setTranslateZ(fromSphere.getTranslateZ());
		
		// apply rotations to bone to align it correctly between both joints
		// below created with guidance from http://ruzman.de/blog/leap-motion-javafx-3d-hand-2
		rotation.setPivotY(bone.getHeight() / 2);
		Vector leapDistance = new Vector(dx, dy, dz);
		Vector leapAxis = leapDistance.cross(Vector.up());
		rotation.setAxis(new Point3D(leapAxis.getX(), leapAxis.getY(), leapAxis.getZ()));
		rotation.setAngle(new Point3D(dx, dy, dz).angle(new Point3D(0, -1, 0)));
	}

	/**
	 * Returns the bone associated with this object. 
	 * @return Bone cylinder. 
	 */
	public Cylinder getBone() {
		return bone;
	}
}