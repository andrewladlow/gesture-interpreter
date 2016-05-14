package gestureinterpreter;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Helper class for specific text animation methods.
 */
public class TextHelper {
    /**
     * Creates a fade out animation for a given node.
     * 
     * @param duration The fade out duration.
     * @param node The node to apply this animation to.
     */
    public static void textFadeOut(double duration, Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        ft.play();
    }

    /**
     * Creates a fade in animation for a given node.
     * 
     * @param duration The fade in duration.
     * @param node The node to apply this animation to.
     */
    public static void textFadeIn(double duration, Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);

        ft.play();
    }
}
