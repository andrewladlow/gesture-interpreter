package gestureinterpreter;

import com.leapmotion.leap.Arm;
import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;
import com.leapmotion.leap.Vector;
import java.util.UUID;
import javafx.application.Platform;
import javafx.geometry.Point2D;

public class LeapListener extends Listener {

	private Frame frame;
	
	private BooleanProperty frameReady = new SimpleBooleanProperty();
	
	public void onFrame(Controller controller) {
		frameReady.set(false);;
		if (!controller.frame().hands().isEmpty()) {
			System.out.println("Debug 0  " + controller.frame().id());
			frameReady.set(true);
		}
	}

	public BooleanProperty frameReadyProperty() {
		return frameReady;
	}
}

/*public class LeapListener extends Listener {

private final Driver app;	

public LeapListener() {this.app=null;}

public LeapListener(Driver main) {
    this.app = main;
}

public void onFrame(Controller controller) {
	Frame frame = controller.frame();
	if (!frame.hands().isEmpty()) {
		for (Hand hand : frame.hands()) {
			float x = hand.palmPosition().getX();
			float y = hand.palmPosition().getY();
			float z = hand.palmPosition().getZ();
			Platform.runLater(() -> {
				app.centerX().set(x);
				app.centerY().set(z);
				app.radius().set(50. - y / 5);
		});
		}
	}
}
}*/

/*public class LeapListener extends Listener {
	
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
	
}*/