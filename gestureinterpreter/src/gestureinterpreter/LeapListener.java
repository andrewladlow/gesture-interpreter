package gestureinterpreter;

import java.util.HashMap;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;

import javafx.application.Platform;
import javafx.scene.Group;

public class LeapListener extends Listener {
	
	private Group handGroup;
	private HashMap<Integer, HandFX> hands;
	private Menu app;
	
	public LeapListener(HashMap<Integer, HandFX> hands, Menu app) {
		this.hands = hands;
		this.app = app;
		handGroup = new Group();
		app.get3D().getChildren().add(handGroup);
	}
	
	public void onConnect(Controller controller) {
		System.out.println("connected leap");
	}
	
	public void onExit(Controller controller) {
		System.out.println("disconnected leap");
	}
    
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
	
		Platform.runLater(() -> {
			// refresh frame if there are less hands in this frame than in the last one
			if (frame.hands().count() < controller.frame(1).hands().count()) {
				//hands.clear();
				handGroup.getChildren().clear();
			}
			if (!frame.hands().isEmpty()) {
				for (Hand leapHand : frame.hands()) {
					int handId = leapHand.id();
					HandFX hand = hands.get(handId);
					
					if (!hands.containsKey(handId)) {
						hand = new HandFX(app);
						hands.put(leapHand.id(), hand);
						handGroup.getChildren().add(hand);
					}							
					if (hand != null) {
						hand.update(frame.hand(leapHand.id()));
					}
				}
			}
		});
	}
	
}