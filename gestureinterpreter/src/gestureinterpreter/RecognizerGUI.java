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

public class RecognizerGUI extends Application {
	
	private Scene scene;

    private LeapListener listener = null;
	private RecognizerListener recorderListener = null;
    private Controller controller = null;
    
    private IntegerProperty timerCount = new SimpleIntegerProperty();
    
	private ObjectProperty gestureRecognition = new SimpleObjectProperty();
    
	private HashMap<Integer, HandFX> hands;
	
    private Group root3D;

	public ObjectProperty<RecognizerResults> gestureRecognitionProperty() {
		return gestureRecognition;
	}

    public void start(Stage primaryStage) {
    	System.out.println("INIT2");
	    Stage stage = new Stage();
	    
        hands = new HashMap<Integer, HandFX>();
    	listener = new LeapListener();
        recorderListener = new RecognizerListener(this);
        controller = new Controller();
        controller.addListener(recorderListener);
        
        Group root2D = new Group();
        
        scene = new Scene(root2D, 1280, 600);

        Label titleLabel = new Label();
        titleLabel.textProperty().set("Gesture Recognizer");
        titleLabel.setTranslateX(10);
        titleLabel.setTranslateY(10);
        titleLabel.setFont(Font.font("Times New Roman", 24));
    	
    	Label resultLabel = new Label();
    	//resultLabel.textProperty().bind(gestureRecognition);
    	resultLabel.setTranslateX(450);
    	resultLabel.setTranslateY(100);
    	resultLabel.setFont(Font.font("Times New Roman", 24));
    	
        root2D.getChildren().addAll(titleLabel, resultLabel);
    	
        this.gestureRecognitionProperty().addListener((gestureRecognition, oldVal, newVal) -> {
        	resultLabel.textProperty().set("Closest match: " +  newVal.getName() + "\nMatch score: " + newVal.getScore());
        });
    	
        final PerspectiveCamera camera = new PerspectiveCamera();
        camera.setFieldOfView(50);
		camera.setTranslateX(-600);
		camera.setTranslateY(-600);
		camera.setTranslateZ(300);
		
		root3D = new Group();
        root3D.getChildren().addAll(camera);
        SubScene subScene = new SubScene(root3D, 1280, 800, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        root2D.getChildren().addAll(subScene);
              

        FXHandListener handRenderer = new FXHandListener(controller, recorderListener, hands);
        root3D.getChildren().add(handRenderer);
        
        
        primaryStage.setTitle("Gesture Interpreter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void stop() {
        controller.removeListener(recorderListener);
        Platform.exit();
    }    
}