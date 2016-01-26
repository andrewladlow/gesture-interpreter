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
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RecorderGUI extends Group {
	
	private static RecorderGUI INSTANCE;
    private Controller controller;

	private RecorderGUI() {}
	
	public static RecorderGUI getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RecorderGUI();
		}
		return INSTANCE;
	}
    
    public void init(Controller controller) {

        Label titleLabel = new Label();
        titleLabel.textProperty().set("Gesture Recorder");
        titleLabel.setTranslateX(10);
        titleLabel.setTranslateY(10);
        titleLabel.setFont(Font.font("Times New Roman", 24));
    	
    	Label resultLabel = new Label();
    	resultLabel.setTranslateX(525);
    	resultLabel.setTranslateY(100);
    	resultLabel.setFont(Font.font("Times New Roman", 24));
    	
        this.getChildren().addAll(resultLabel, titleLabel);
        
        Thread t = new Thread(() -> {
        	for (char c = 'H'; c <= 'M'; c++) {
        		char tempChar = c;
    			for (int i = 3; i > 0; i--) {
        			int count = i;
        			Platform.runLater(() -> {
        				resultLabel.textProperty().set("Recording " + tempChar + " in " + count + "...");
        			});
    				try {
    					Thread.sleep(1000);
            		} catch (Exception e) {}
    			}
    			Platform.runLater(() -> {
    				resultLabel.textProperty().set("Now recording " + tempChar + "...");
    			});
        		
        		System.out.println(c);
        		RecorderListener tempRecorderListener = new RecorderListener(c);
        		controller.addListener(tempRecorderListener);
        		while (tempRecorderListener.gestureDoneProperty().get() != true) {
        			try {
        				Thread.sleep(200);
        			} catch (Exception e) {}
        		}
    			char temp = c;
    			Platform.runLater(() -> {
    				resultLabel.textProperty().set("Successfully recorded " + temp + "!");
    			});
    			try {
    				Thread.sleep(2000);
        		} catch (Exception e) {}
        		controller.removeListener(tempRecorderListener);
        	}
        });
        t.start();  
    }
}
