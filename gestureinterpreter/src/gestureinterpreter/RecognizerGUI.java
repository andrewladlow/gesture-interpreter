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
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RecognizerGUI extends Group {
	
	private Menu app;
	
	private static RecognizerGUI INSTANCE;
	private RecognizerListener recognizerListener; 
	private ObjectProperty gestureRecognition = new SimpleObjectProperty();
	
	public LeapButton backButton;

	public ObjectProperty<RecognizerResults> gestureRecognitionProperty() {
		return gestureRecognition;
	}
		
	private RecognizerGUI() {}
	
	public static RecognizerGUI getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RecognizerGUI();
		}
		return INSTANCE;
	}
	
    public void init(Menu app, Controller controller) {
	    
    	this.app = app;
        recognizerListener = new RecognizerListener(this);
        controller.addListener(recognizerListener);
        
        Label titleLabel = new Label();
        titleLabel.textProperty().set("Gesture Recognizer");
        titleLabel.setTranslateX(10);
        titleLabel.setTranslateY(10);
        titleLabel.setFont(Font.font("Times New Roman", 24));
        
    	Label resultLabel = new Label();
    	//resultLabel.textProperty().bind(gestureRecognition);
    	resultLabel.setFont(Font.font("Times New Roman", 24));
    	resultLabel.setTranslateX(Menu.APPWIDTH-350);
    	resultLabel.setTranslateY(10);

        this.getChildren().addAll(titleLabel, resultLabel);
    	
        this.gestureRecognitionProperty().addListener((gestureRecognition, oldVal, newVal) -> {
        	resultLabel.textProperty().set("Closest match: " +  newVal.getName() + "\nMatch score: " + newVal.getScore());
        });      
        
        Group root3D = new Group();
        SubScene subScene = new SubScene(root3D, Menu.APPWIDTH, Menu.APPHEIGHT, true, SceneAntialiasing.BALANCED);
        
        this.getChildren().addAll(subScene);
        
        backButton = new LeapButton(Menu.APPWIDTH, Menu.APPHEIGHT, Color.RED, Color.GOLDENROD, "Return");     
        backButton.setPosition(-10, -50, 110);
        backButton.setRotation(1, 5);
        
        root3D.getChildren().addAll(backButton);
    }
    
    public void goBack() {
    	
    }
    
}