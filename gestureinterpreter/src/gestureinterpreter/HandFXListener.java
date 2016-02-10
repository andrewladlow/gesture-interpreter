package gestureinterpreter;

import java.util.HashMap;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;

import javafx.application.Platform;
import javafx.scene.Group;

public class HandFXListener extends Group {
	
	public HandFXListener(Menu app, Controller controller, LeapListener listener, HashMap<Integer, HandFX> hands) {
        listener.frameReadyProperty().addListener((frameReady, oldVal, newVal) -> {
    		Frame frame = controller.frame();   		
    		// draw hands if at least one is present in tracking area
    		if (newVal) {
    			Platform.runLater(() -> {
					for (Hand leapHand : frame.hands()) {
						int handId = leapHand.id();
						HandFX hand = hands.get(handId);
						
						if(!hands.containsKey(handId)) {
							hand = new HandFX(app);
							hands.put(leapHand.id(), hand);
							this.getChildren().add(hand);
						}							
						if(hand != null) {
							hand.update(frame.hand(leapHand.id()));
						}
    				}
    			});	
    		} 	
     		// clear if there were hands and now there aren't
    		// NB: sometimes causes hands to flash for a frame as the scene is cleared on one listener pulse and reformed the next
    		// previous solution of (frame.hands().count() < controller.frame(1).hands().count()) however caused 'glitchy' hand graphics -
    		// - if leap motion lost track of hands (which is frequently the case with intricate gestures), old hand frames would become stuck on screen
    		else if (oldVal && !newVal) {
    			Platform.runLater(() -> {
					//System.out.println("Debug 6");
					for (Hand leapHand : frame.hands()) {
						// first remove from storage hashmap
						hands.remove(leapHand.id());
					}
					// then remove from display
					this.getChildren().clear();
    			});
    		}
    	});       
	}
}
