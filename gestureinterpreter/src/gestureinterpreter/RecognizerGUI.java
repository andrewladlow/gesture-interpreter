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
	
    private LeapListener leapListener = null;
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
        hands = new HashMap<Integer, HandFX>();
    	leapListener = new LeapListener();
        recorderListener = new RecognizerListener(this);
        controller = new Controller();
        controller.addListener(recorderListener);
        
        Group root2D = new Group();
        
        Scene scene = new Scene(root2D, 1280, 600);
        Button btn = new Button();
        btn.setTranslateX(1200);
        btn.setText("Hello world");
        btn.setOnAction(new EventHandler<ActionEvent>() {
        	
            public void handle(ActionEvent event) {
                System.out.println("hello world");    
            }
        });
        
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
    	
        root2D.getChildren().addAll(btn, titleLabel, resultLabel);
    	
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
              
        primaryStage.setTitle("Gesture Interpreter");
        primaryStage.setScene(scene);
        primaryStage.show();

        recorderListener.frameReadyProperty().addListener((frameReady, oldVal, newVal) -> {
    		Frame frame = controller.frame();
    		
    		// draw hands if atleast one is present in capture area
    		if (newVal) {
    			Platform.runLater(() -> {
					for (Hand leapHand : frame.hands()) {
						int handId = leapHand.id();
						HandFX hand = hands.get(handId);
						
						if(!hands.containsKey(handId)) {
							hand = new HandFX();
							hands.put(leapHand.id(), hand);
							root3D.getChildren().add(hand);
						}		
						
						if(hand != null) {
							hand.update(frame.hand(leapHand.id()));
						}
    				}
    			});
 	
    		// remove hand if it leaves leapmotion capture area
    		} else if (frame.hands().count() < controller.frame(1).hands().count()) {
    			Platform.runLater(() -> {
					// remove shapes if hands leave tracking area
					System.out.println("Debug 6");
					for (Hand leapHand : frame.hands()) {
						hands.remove(leapHand.id());
					}
					root3D.getChildren().clear();
    			});
    		}
    	});
    }
    
    public void stop() {
        controller.removeListener(recorderListener);
    }    
}