package gestureinterpreter;

import java.text.DecimalFormat;
import java.util.HashMap;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;

import javafx.application.Platform;
import javafx.scene.Group;

/**
 * Class handling hand tracking, extends the 
 * Leap Motion listener class. 
 */
public class LeapListener extends Listener {
	
	private Group handGroup;
	private HashMap<Integer, HandFX> hands;
	private Menu app;
    //private DecimalFormat df = new DecimalFormat("###.##");
    
    /**
     * Creates a new instance of a leap listener.
     * @param hands The hash map to store handFX objects.
     * @param app The application associated with this listener. 
     */
	public LeapListener(HashMap<Integer, HandFX> hands, Menu app) {
		this.hands = hands;
		this.app = app;
		handGroup = new Group();
		app.get3D().getChildren().add(handGroup);
	}
	
    /**
     * Called when this listener is added to a controller.
     * @param controller The leap motion controller to check. 
     */
	public void onConnect(Controller controller) {
		System.out.println("connected leap");
	}
	
    /**
     * Called when this listener is disconnected from a controller.
     * @param controller The leap motion controller to check. 
     */
	public void onExit(Controller controller) {
		System.out.println("disconnected leap");
	}
    
    /**
     * Called when a new frame of tracking data is available.
     * @param controller The leap motion controller to poll. 
     */	
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
    	//long time1 = System.nanoTime();
		Platform.runLater(() -> {
			if (!frame.hands().isEmpty()) {
				for (Hand leapHand : frame.hands()) {
					int handId = leapHand.id();
					HandFX hand = hands.get(handId);
					
					// check hashmap to avoid recreation of hands on every frame
					if (!hands.containsKey(handId)) {
						hand = new HandFX(app);
						hands.put(leapHand.id(), hand);
						handGroup.getChildren().add(hand);
					}							
					if (hand != null) {
						hand.update(frame.hand(leapHand.id()));
			            //long time2 = System.nanoTime();
						//String timeTaken = df.format(Math.round(time2 - time1) / 1e6);
			            //System.out.println("Time taken: " + timeTaken + " ms");
					}
				}
			}
			// refresh frame if there are less hands in this frame than in the last one
			if (frame.hands().count() < controller.frame(1).hands().count()) {
				hands.clear();
				handGroup.getChildren().clear();
			}
		});
	}
	
}