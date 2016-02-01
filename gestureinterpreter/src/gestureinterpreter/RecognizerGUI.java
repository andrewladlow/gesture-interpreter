package gestureinterpreter;

import java.util.HashMap;
import java.util.Map;
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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RecognizerGUI {

	private Menu app;
	private Controller controller;

	private static RecognizerGUI INSTANCE;
	private RecognizerListener recognizerListener;
	private ObjectProperty gestureRecognition = new SimpleObjectProperty();

	public LeapButton backButton;

	public ObjectProperty<RecognizerResults> gestureRecognitionProperty() {
		return gestureRecognition;
	}

	private RecognizerGUI() {
	}

	public static RecognizerGUI getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RecognizerGUI();
		}
		return INSTANCE;
	}

	public void init(Menu app, Controller controller) {

		this.app = app;
		this.controller = controller;
		recognizerListener = new RecognizerListener(this);
		controller.addListener(recognizerListener);

		Label titleLabel = new Label();
		titleLabel.textProperty().set("Gesture Recognizer");
		titleLabel.setFont(Font.font("Times New Roman", 24));

		Label resultLabel = new Label();
		// resultLabel.textProperty().bind(gestureRecognition);
		resultLabel.setFont(Font.font("Times New Roman", 24));

		//this.getChildren().addAll(titleLabel, resultLabel);


		StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
		StackPane.setAlignment(resultLabel, Pos.TOP_CENTER);
		
		
		app.get2D().getChildren().addAll(titleLabel, resultLabel);
		
		this.gestureRecognitionProperty().addListener((gestureRecognition, oldVal, newVal) -> {
			resultLabel.textProperty().set("Closest match: " + newVal.getName() + "\nMatch score: " + newVal.getScore());
			Helper.textFadeOut(3000, resultLabel);
		});

		backButton = new LeapButton(Menu.APPWIDTH, Menu.APPHEIGHT, Color.RED, Color.GOLDENROD, "Return");
		backButton.setPosition(-10, -50, 110);
		backButton.setRotation(5, Rotate.X_AXIS);

		app.get3D().getChildren().addAll(backButton);
	}

	public void goBack() {
		//System.out.println("Going back!");
		controller.removeListener(recognizerListener);
		app.swapScene("Menu");

	}
	

}