package gestureinterpreter;

import com.leapmotion.leap.Arm;
import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
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
		Frame frame = controller.frame();
		frameReady.set(false);
		if (!frame.hands().isEmpty()) {
			frameReady.set(true);
			// observer interrupts before method ends? no frames skipped? needs testing...
			if (!touched) {
				if (frame.hands().frontmost().fingers().frontmost().touchZone() == Zone.ZONE_TOUCHING) {
					touched = true;
					System.out.println("Finger touching");
					app.boxValProperty().set(true);
				}
			} else if (touched && frame.hands().frontmost().fingers().frontmost().touchZone() != Zone.ZONE_TOUCHING) {
				touched = false;
				System.out.println("Finger no longer touching");
				app.boxValProperty().set(false);
			}
		}
	}
	

	public BooleanProperty frameReadyProperty() {
		return frameReady;
	}
	
}