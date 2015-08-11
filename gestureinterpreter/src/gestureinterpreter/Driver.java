package gestureinterpreter;

import com.leapmotion.leap.*;

class SampleListener extends Listener {
	public void onConnect(Controller controller) {
		controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
		System.out.println("Connected");
	}

	public void onFrame(Controller controller) {
		System.out.println("Frame available");
		Frame frame = controller.frame();

		System.out.println("Frame id: " + frame.id()
						+ ", timestamp: " + frame.timestamp()
						+ ", hands : " + frame.hands().count()
						+ ", fingers: " + frame.fingers().count()
						+ ", tools : " + frame.tools().count()
						+ ", gestures : " + frame.gestures().count());

		for (Hand hand : frame.hands()) {
			String handType = hand.isLeft() ? "Left hand" : "Right hand";
			System.out.println("  " + handType + ", id: " + hand.id()
							+ ", palm position : " + hand.palmPosition());

			// Get the hand's normal vector and direction.
			Vector normal = hand.palmNormal();
			Vector direction = hand.direction();

			System.out.println("  pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
							+ "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
							+ "yaw: " + Math.toDegrees(direction.yaw()) + " degrees");

			Arm arm = hand.arm();
			System.out.println("  Arm direction: " + arm.direction()
							+ ", wrist position: " + arm.wristPosition()
							+ ", elbow position: " + arm.elbowPosition());

			for (Finger finger : hand.fingers()) {
				System.out.println("    " + finger.type() + ", id: " + finger.id()
								+ ", length: " + finger.length()
								+ "mm, width: " + finger.width() + "mm");

				for (Bone.Type boneType : Bone.Type.values()) {
					Bone bone = finger.bone(boneType);
					System.out.println("      " + bone.type()
									+ " bone, start: " + bone.prevJoint()
									+ ", end: " + bone.nextJoint()
									+ ", direction: " + bone.direction());
				}

			}

		}

		if (!frame.hands().isEmpty()) {
			System.out.println();
		}
	}
}

public class Driver {
	public static void main(String[] args) {
		SampleListener listener = new SampleListener();
		Controller controller = new Controller();

		controller.addListener(listener);

		System.out.println("Press Enter to quit");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		controller.removeListener(listener);
	}
}