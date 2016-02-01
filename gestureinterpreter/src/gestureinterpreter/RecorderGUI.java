package gestureinterpreter;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RecorderGUI {
	
	private final Object lock = new Object();

	private Menu app;

	private static RecorderGUI INSTANCE;
	private Controller controller;

	private RecorderGUI() {
	}

	public static RecorderGUI getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RecorderGUI();
		}
		return INSTANCE;
	}

	public void init(Menu app, Controller controller) {

		this.app = app;

		Label titleLabel = new Label();
		titleLabel.textProperty().set("Gesture Recorder");
		titleLabel.setFont(Font.font("Times New Roman", 24));

		Label resultLabel = new Label();
		resultLabel.setFont(Font.font("Times New Roman", 24));

		Rectangle gestureImgRect = new Rectangle();
		gestureImgRect.setWidth(300);
		gestureImgRect.setHeight(260);
		gestureImgRect.setStroke(Color.BLACK);
		gestureImgRect.setScaleX(0.625);
		gestureImgRect.setScaleY(0.625);

		StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
		StackPane.setAlignment(resultLabel, Pos.TOP_CENTER);
		StackPane.setAlignment(gestureImgRect, Pos.TOP_CENTER);

		app.get2D().getChildren().addAll(titleLabel, resultLabel, gestureImgRect);

		Thread t = new Thread(() -> {
			
			for (char c = 'A'; c <= 'Z'; c++) {
				char tempChar = c;
				Image gestureImg = new Image("file:images/" + Character.toLowerCase(tempChar) + ".png");
				gestureImgRect.setFill(new ImagePattern(gestureImg));

				try {			
					for (int i = 3; i > 0; i--) {
						int count = i;
						Platform.runLater(() -> {
							resultLabel.textProperty().set("Recording " + tempChar + " in " + count + "...");
							Helper.textFadeOut(1000, resultLabel);
						});
						Thread.sleep(1000);
						
					}
					Platform.runLater(() -> {
						Helper.textFadeIn(1, resultLabel);
						resultLabel.textProperty().set("Now recording " + tempChar + "...");
					});
	
					System.out.println(c);
					RecorderListener tempRecorderListener = new RecorderListener(c, lock);
					controller.addListener(tempRecorderListener);
					
					// wait on this thread until recording is finished
					synchronized(lock) {
						while (!tempRecorderListener.gestureDoneProperty().get()) {
							//Thread.sleep(200);
							lock.wait();
						}
					}
					
					
					if (tempRecorderListener.gestureDoneProperty().get()) {
						Platform.runLater(() -> {
							resultLabel.textProperty().set("Successfully recorded " + tempChar + "!");
							Helper.textFadeOut(2500, resultLabel);
						});
					}

					Thread.sleep(2000);
					controller.removeListener(tempRecorderListener);		
				} catch (Exception e) {}
			}
		});
		t.start();
	}
	
}
