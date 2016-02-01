package gestureinterpreter;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Helper {
	public static void textFadeOut (double duration, Node node) {
	     FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
	     ft.setFromValue(1.0);
	     ft.setToValue(0.0);
	 
	     ft.play();
	}
	
	public static void textFadeIn (double duration, Node node) {
	     FadeTransition ft = new FadeTransition(Duration.millis(duration), node);
	     ft.setFromValue(0.0);
	     ft.setToValue(1.0);
	 
	     ft.play();
	}
}
