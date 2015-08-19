package gestureinterpreter;

import javafx.scene.Node;
import com.leapmotion.leap.*;

public class LeapToFX {
	public static void move (Node node, Vector vector) {
		node.setTranslateX(vector.getX());
		node.setTranslateY(-vector.getY());
		node.setTranslateZ(-vector.getZ());
	}
}
	

