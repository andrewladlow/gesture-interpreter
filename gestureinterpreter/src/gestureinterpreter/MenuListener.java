package gestureinterpreter;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Pointable.Zone;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.leapmotion.leap.Vector;

public class MenuListener extends Listener {
	
	private final Menu app;
	private boolean touchedRecognizer = false;
	private boolean touchedRecorder = false;
	private BooleanProperty frameReady = new SimpleBooleanProperty();
	
	public MenuListener(Menu app) {
		this.app = app;
	}

	public BooleanProperty frameReadyProperty() {
		return frameReady;
	}
	
	public void onConnect(Controller controller) {
		System.out.println("connected leap");
	}
	
	public void onExit(Controller controller) {
		System.out.println("disconnected leap");
	}
    
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
		frameReady.set(false);
		if (!frame.hands().isEmpty()) {
			frameReady.set(true);
		}	
	}	
}