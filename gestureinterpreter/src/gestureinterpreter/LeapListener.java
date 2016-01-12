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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.geometry.Point2D;

public class LeapListener extends Listener {
	
	private final Visualizer app;
	
	private boolean touched = false;
	
	private BooleanProperty frameReady = new SimpleBooleanProperty();
	
	public LeapListener(Visualizer main) {
		this.app = main;
	}

	public LeapListener() {
		this.app = null;
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
/*			
			InteractionBox iBox = controller.frame().interactionBox();
			Finger frontFinger = frame.hands().frontmost().fingers().frontmost();
			
			Pointable pointable = controller.frame().pointables().frontmost();


			Vector frontFingerTip = frontFinger.tipPosition();
			Vector leapPoint = frontFinger.stabilizedTipPosition();
			Vector normalizedPoint = iBox.normalizePoint(leapPoint, true);

			float appX = normalizedPoint.getX() * 1280;
			float appY = (1 - normalizedPoint.getY()) * 600;
			
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
				
						if (!touched) {
							if (frame.hands().frontmost().fingers().frontmost().touchZone() == Zone.ZONE_TOUCHING) {
								touched = true;
								//System.out.println("Finger touching");
								app.leapButton1.boxValProperty().set(true);
							}
						}
					}
				}
			}
			
			
			// Better solution using ibox for x and y but reusing above z check?
			//465 1140
			//203 313
			
			
			if (touched && frame.hands().frontmost().fingers().frontmost().touchZone() != Zone.ZONE_TOUCHING) {
				touched = false;
				//System.out.println("Finger no longer touching");
				app.leapButton1.boxValProperty().set(false);
				app.swapScene("Recognizer");
				//app.stop();
				//RecognizerGUI.launch(RecognizerGUI.class, args);
			}*/
		}
	}
	

	public BooleanProperty frameReadyProperty() {
		return frameReady;
	}
	
}