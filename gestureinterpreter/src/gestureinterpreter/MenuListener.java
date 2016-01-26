package gestureinterpreter;

import com.leapmotion.leap.Arm;
import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture.Type;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Pointable;
import com.leapmotion.leap.Pointable.Zone;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Screen;
import com.leapmotion.leap.Vector;
import com.sun.javafx.geom.Rectangle;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.geometry.Point2D;

public class MenuListener extends Listener {
	
	private final Menu app;
	
	private boolean touchedRecognizer = false;
	private boolean touchedRecorder = false;
	
	private BooleanProperty frameReady = new SimpleBooleanProperty();
	
	public MenuListener(Menu main) {
		this.app = main;
	}
	
	public void onConnect(Controller controller) {
		System.out.println("connected leap");
	}
	
	public void onExit(Controller controller) {
		System.out.println("disconnected leap");
	}
    
	public void onFrame(Controller controller) {
		//System.out.println(app.box.localToScene(app.box.getBoundsInLocal()));
		Frame frame = controller.frame();
		frameReady.set(false);
		if (!frame.hands().isEmpty()) {
			frameReady.set(true);
			
			
			InteractionBox iBox = frame.interactionBox();
			
			Finger frontFinger = frame.fingers().frontmost();
			Vector frontFingerTip = frontFinger.tipPosition();
			
			//Pointable pointable = controller.frame().pointables().frontmost();
			
			System.out.println("XLEAP: " + frontFingerTip.getX() + "             YLEAP: " + frontFingerTip.getY() + "      ZLEAP: " + frontFingerTip.getZ());
			
			
			Vector leapPoint = frontFinger.tipPosition();
			Vector normalizedPoint = iBox.normalizePoint(leapPoint, true);
			//normalizedPoint = normalizedPoint.plus(new Vector((float) 0.2, (float) 0, (float) 1));
			
			//normalizedPoint = normalizedPoint.times((float) 0.35);
			//Vector position = new Vector((float) .175, (float) .75, (float) .75);
			//normalizedPoint = normalizedPoint.minus(position);
			
			float appX = normalizedPoint.getX() * app.APPWIDTH;
			float appY = (1 - normalizedPoint.getY()) * app.APPHEIGHT;
			
			
			//System.out.println("XAPP: " +  appX + "             YAPP: " + appY);
			
/*			for (LeapButton button : app.leapButtons) {
				//System.out.println(button.localToScene(button.getBoundsInLocal()));
				if (button.localToScene(button.getBoundsInLocal()).contains(new Point2D(frontFingerTip.getX(), frontFingerTip.getY() * -1))) {
					System.out.println("Hovering over button!");
					//button.touchStatusProperty().set(true);
				}
			}*/
			
			if (frontFingerTip.getZ() < -85) {
				if (frontFingerTip.getY() > 160 && frontFingerTip.getY() < 240) {
					if (frontFingerTip.getX() > -230 && frontFingerTip.getX() < -60) {
						
						if (!touchedRecognizer) {
							if (frontFinger.touchZone() == Zone.ZONE_TOUCHING) {
								touchedRecognizer = true;
								//System.out.println("Finger touching");
								app.recognizerButton.touchStatusProperty().set(true);
							}
						}
						
					} else if (frontFingerTip.getX() > 30 && frontFingerTip.getX() < 200) {
						
						if (!touchedRecorder) {
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
				//app.swapValProperty().set(true);
			} 
			else if (touchedRecorder && frontFinger.touchZone() != Zone.ZONE_TOUCHING) {
				touchedRecorder = false;
				app.recorderButton.touchStatusProperty().set(false);
				app.swapScene("Recorder");
				//app.swapValProperty().set(true);		
			}
			
		}	
	}

/*
			Vector frontFingerTip = frontFinger.tipPosition();
			//Vector leapPoint = frontFinger.stabilizedTipPosition();
			//Vector normalizedPoint = iBox.normalizePoint(leapPoint, true);

			//float appX = normalizedPoint.getX() * 1280;
			//float appY = (1 - normalizedPoint.getY()) * 600;
			
			// observer interrupts before method ends? no frames skipped? needs testing...
			// x = -40 -- 118
			// y = 298 -- 242
			// z = -60 ? 
			
			// javafx box has contains(x, y) method -- possible solution? 
			
			//System.out.println("Debug x: " + frontFingerTip.getX() + ", Debug y: " + frontFingerTip.getY() + ", Debuy z: " + frontFingerTip.getZ());
			
			//System.out.println(normalizedPoint.getX());
			
			//System.out.println(app.box.contains(appX, appY));
			
			//System.out.println("X: " +  appX + "             Y: " + appY);
			
			// Works but seems messy?
			
			//if (app.box.contains(localX, localY))
			
			if (frontFingerTip.getX() > -40 && frontFingerTip.getX() < 118) {
				//System.out.println("Satisfied x");
				
				if (frontFingerTip.getY() > 242 && frontFingerTip.getY() < 298) {
					//System.out.println("Satisified y");
					
					if (frontFingerTip.getZ() < -60) {
						//System.out.println("Satisified z");
				
						if (!touchedRecognizer) {
							if (frontFinger.touchZone() == Zone.ZONE_TOUCHING) {
								touchedRecognizer = true;
								//System.out.println("Finger touching");
								app.recognizerButton.touchStatusProperty().set(true);
							}
						}
					}
				}
			}
			
			
			// Better solution using ibox for x and y but reusing above z check?
			//465 1140
			//203 313
			
			
			if (touchedRecognizer && frontFinger.touchZone() != Zone.ZONE_TOUCHING) {
				touchedRecognizer = false;
				//System.out.println("Finger no longer touching");
				app.recognizerButton.touchStatusProperty().set(false);
				app.swapScene("Recognizer");
				//app.swapValProperty().set(true);
				//app.stop();
				//RecognizerGUI.launch(RecognizerGUI.class, args);
			}
		}
	}*/
	

	public BooleanProperty frameReadyProperty() {
		return frameReady;
	}
	
}