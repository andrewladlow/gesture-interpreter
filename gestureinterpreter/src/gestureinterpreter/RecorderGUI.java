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

public class RecorderGUI extends Application {

    private LeapListener listener = null;
	//private RecorderListener recorderListener = null;
    private Controller controller = null;
    
	private HashMap<Integer, HandFX> hands;
    
    public void start(Stage primaryStage) {
    	listener = new LeapListener();
        //recorderListener = new RecorderListener();
        controller = new Controller();
        controller.addListener(listener);
        
        hands = new HashMap<Integer, HandFX>();

        
        Group root2D = new Group();

        Scene scene = new Scene(root2D, 1280, 600);

        Label titleLabel = new Label();
        titleLabel.textProperty().set("Gesture Recorder");
        titleLabel.setTranslateX(10);
        titleLabel.setTranslateY(10);
        titleLabel.setFont(Font.font("Times New Roman", 24));
    	
    	Label resultLabel = new Label();
    	resultLabel.setTranslateX(450);
    	resultLabel.setTranslateY(100);
    	resultLabel.setFont(Font.font("Times New Roman", 24));
        
    	Label timerLabel = new Label();
    	timerLabel.setTranslateX(200);
    	timerLabel.setTranslateY(200);
    	timerLabel.setFont(Font.font("Times New Roman", 24));
    	

        root2D.getChildren().addAll(timerLabel, resultLabel, titleLabel);
        
        Thread t = new Thread(() -> {
        	for (char c = 'C'; c <= 'C'; c++) {
        		try {
        			char temp = c;
        			Platform.runLater(() -> {
        				resultLabel.textProperty().set("Recording " + temp + " in 3...");
        			});
        			Thread.sleep(1000);
        			Platform.runLater(() -> {
        				resultLabel.textProperty().set("Recording " + temp + " in 3...2...");
        			});
        			Thread.sleep(1000);
        			Platform.runLater(() -> {
        				resultLabel.textProperty().set("Recording " + temp + " in 3...2...1...");
        			});
        			Thread.sleep(1000);
        			Platform.runLater(() -> {
        				resultLabel.textProperty().set("Now recording " + temp);
        			});
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		
        		System.out.println(c);
        		RecorderListener tempRecorderListener = new RecorderListener(c);
        		controller.addListener(tempRecorderListener);
        		while (tempRecorderListener.gestureDoneProperty().get() != true) {
        			try {
        				Thread.sleep(200);
        			} catch (Exception e) {
        				e.printStackTrace();
        			}
        		}
    			char temp = c;
    			Platform.runLater(() -> {
    				resultLabel.textProperty().set("Recorded " + temp + "!");
    			});
    			try {
    				Thread.sleep(1000);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		controller.removeListener(tempRecorderListener);
        	}
        });
        t.start();
           
        
        final PerspectiveCamera camera = new PerspectiveCamera();
        camera.setFieldOfView(50);
		camera.setTranslateX(-600);
		camera.setTranslateY(-600);
		camera.setTranslateZ(300);
		
        Group root3D = new Group();
        root3D.getChildren().addAll(camera);
        SubScene subScene = new SubScene(root3D, 1280, 800, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        root2D.getChildren().addAll(subScene);
       
        

        FXHandListener handRenderer = new FXHandListener(controller, listener, hands);
        root3D.getChildren().add(handRenderer);
        
        primaryStage.setTitle("Recorder");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
    }
    

    public void stop() {
        controller.removeListener(listener);
        Platform.exit();
    }
    
}
