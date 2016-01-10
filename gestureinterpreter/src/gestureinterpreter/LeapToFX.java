package gestureinterpreter;

import javafx.scene.Node;
import com.leapmotion.leap.*;

public class LeapToFX {
	public static void move (Node node, Vector vector) {
		node.setTranslateX(vector.getX());
		node.setTranslateY(-vector.getY());
		node.setTranslateZ(-vector.getZ());
	}
	
	public static Vector swap (Vector leapVector) {
		Vector fxVector = new Vector();
		fxVector.setX(leapVector.getX());
		fxVector.setY(-leapVector.getY());
		fxVector.setZ(-leapVector.getZ());
		return fxVector;
	}
}
	

