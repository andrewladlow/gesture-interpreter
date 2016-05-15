package gestureinterpreter;

import javafx.scene.Node;
import com.leapmotion.leap.*;

/**
 * Helper class to convert Leap Motion co-ordinates into JavaFX co-ordinates.
 */
public class LeapToFXHelper {
    /**
     * Translates a given node by the given vector.
     * 
     * @param node The node to translate.
     * @param vector The vector containing the translation parameters.
     */
    public static void move(Node node, Vector vector) {
        node.setTranslateX(vector.getX());
        node.setTranslateY(-vector.getY());
        node.setTranslateZ(-vector.getZ());
    }

}
