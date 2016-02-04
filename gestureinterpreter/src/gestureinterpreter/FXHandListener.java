package gestureinterpreter;

import java.util.HashMap;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;

import javafx.application.Platform;
import javafx.scene.Group;

public class FXHandListener extends Group {
	
	public FXHandListener(Controller controller, LeapListener listener, HashMap<Integer, HandFX> hands) {
        listener.frameReadyProperty().addListener((frameReady, oldVal, newVal) -> {
    		Frame frame = controller.frame();   		
    		// draw hands if at least one is present in tracking area
    		if (newVal) {
    			Platform.runLater(() -> {
					for (Hand leapHand : frame.hands()) {
						int handId = leapHand.id();
						HandFX hand = hands.get(handId);
						
						if(!hands.containsKey(handId)) {
							hand = new HandFX();
							hands.put(leapHand.id(), hand);
							this.getChildren().add(hand);
						}		
						
						if(hand != null) {
							hand.update(frame.hand(leapHand.id()));
						}
    				}
    			});	
    		} 	
     		// remove hand if it leaves tracking area
    		else if (frame.hands().count() < controller.frame(1).hands().count()) {
    			Platform.runLater(() -> {
					System.out.println("Debug 6");
					for (Hand leapHand : frame.hands()) {
						// first remove from storage hashmap
						hands.remove(leapHand.id());
					}
					// then remove from display (filtering for only hand objects)
					this.getChildren().removeIf((obj)->obj.getClass().equals(HandFX.class));
    			});
    		}
    	});       
	}
}
