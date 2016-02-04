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
			
			Finger frontFinger = frame.fingers().frontmost();
			Vector frontFingerTip = frontFinger.tipPosition();
			
			if (frontFingerTip.getZ() < -75) {
				if (frontFingerTip.getY() > 150 && frontFingerTip.getY() < 250) {
					if (frontFingerTip.getX() > -240 && frontFingerTip.getX() < -60) {
						
						if (!touchedRecognizer && !touchedRecorder) {
							if (frontFinger.touchZone() == Zone.ZONE_TOUCHING) {
								touchedRecognizer = true;
								//System.out.println("Finger touching");
								app.recognizerButton.touchStatusProperty().set(true);
							}
						}
						
					} 
					else if (frontFingerTip.getX() > 30 && frontFingerTip.getX() < 210) {
						
						if (!touchedRecorder && !touchedRecognizer) {
							if (frontFinger.touchZone() == Zone.ZONE_TOUCHING) {
								touchedRecorder = true;
								//System.out.println("Finger touching");
								app.recorderButton.touchStatusProperty().set(true);
							}
						}
					}
				}
			}			
			if (touchedRecognizer && frontFinger.touchZone() != Zone.ZONE_TOUCHING) {
				touchedRecognizer = false;
				app.recognizerButton.touchStatusProperty().set(false);
				app.swapScene("Recognizer");
			} 
			else if (touchedRecorder && frontFinger.touchZone() != Zone.ZONE_TOUCHING) {
				touchedRecorder = false;
				app.recorderButton.touchStatusProperty().set(false);
				app.swapScene("Recorder");	
			}		
		}	
	}	
}