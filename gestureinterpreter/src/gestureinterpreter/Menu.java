package gestureinterpreter;

import java.util.ArrayList;
import java.util.HashMap;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture.Type;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;
import com.sun.corba.se.impl.orbutil.graph.Node;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Menu extends Application {
	
	public static final float APPWIDTH = 1280;
	public static final float APPHEIGHT = 720;
	
	private Stage stage;
	private Scene scene;
	private SubScene subScene;
	private Group root3D;
	private Group root2D;
	private LeapListener leapListener;
    private MenuListener menuListener;
    private Controller controller;
    
    private FXHandListener handRenderer;
	private HashMap<Integer, HandFX> hands;
	
	public LeapButton recognizerButton;
	public LeapButton recorderButton;
	
/*    private BooleanProperty swapVal = new SimpleBooleanProperty(false);
    
    public BooleanProperty swapValProperty() {
    	return swapVal;
    }
	*/
    public void start(Stage primaryStage) {
    	this.stage = primaryStage;
    	
        hands = new HashMap<Integer, HandFX>();
        leapListener = new LeapListener();
        menuListener = new MenuListener(this);
        controller = new Controller();
        controller.addListener(leapListener);
        controller.addListener(menuListener);
        
        root3D = new Group();
        subScene = new SubScene(root3D, APPWIDTH, APPHEIGHT, true, SceneAntialiasing.BALANCED);
        
        root2D = new Group();
        scene = new Scene(root2D, APPWIDTH, APPHEIGHT, true, SceneAntialiasing.BALANCED);
        root2D.getChildren().addAll(subScene);
              
        Label titleLabel = new Label();
        titleLabel.textProperty().set("Main Menu");
        titleLabel.setTranslateX(10);
        titleLabel.setTranslateY(10);
        titleLabel.setFont(Font.font("Times New Roman", 24)); 
        root2D.getChildren().addAll(titleLabel);

        final PerspectiveCamera camera = new PerspectiveCamera();
        camera.setFieldOfView(50);
		camera.setTranslateX(-650);
		camera.setTranslateY(-600);
		camera.setTranslateZ(275);	
        subScene.setCamera(camera); 
        
        
        recognizerButton = new LeapButton(APPWIDTH, APPHEIGHT, Color.RED, Color.GOLDENROD, "Recognition");     
        recognizerButton.setPosition(-170, -200, 110);
        recognizerButton.setRotation(1, 5);
        
/*        System.out.println("1+ : " + recognizerButton.getBoundsInLocal());
        System.out.println("2+ : " + recognizerButton.getBoundsInParent());
        System.out.println(recognizerButton.getLayoutBounds());
        */
        
        System.out.println(recognizerButton.localToScene(recognizerButton.getBoundsInLocal()));
        
        root3D.getChildren().add(recognizerButton);
        
        recorderButton = new LeapButton(APPWIDTH, APPHEIGHT, Color.RED, Color.GOLDENROD, "Calibration");       
        recorderButton.setPosition(150, -200, 110);
        recorderButton.setRotation(1, 5);
        
        System.out.println(recorderButton.localToScene(recorderButton.getBoundsInLocal()));
        
        root3D.getChildren().add(recorderButton);

        handRenderer = new FXHandListener(controller, leapListener, hands);
        root3D.getChildren().add(handRenderer);
             
        stage.setTitle("Gesture Interpreter");
        stage.setScene(scene);
        stage.show();
    }
    
    public void swapScene(String sceneName) {
		controller.removeListener(menuListener);
    	if (sceneName.equals("Recognizer")) {
        	System.out.println("Swapping to recognizer");
        	Platform.runLater(() -> {
	    		RecognizerGUI recogGUI = RecognizerGUI.getInstance();
	    		recogGUI.init(this, controller);
	    		// clear all except hand visuals
				root3D.getChildren().removeIf((obj)->(!obj.getClass().equals(FXHandListener.class)));
				root2D.getChildren().clear();
				root2D.getChildren().add(subScene);
	    		root2D.getChildren().add(recogGUI);
	    		
        	});
    	} 
    	else if (sceneName.equals("Recorder")) {
    		System.out.println("Swapping to recorder");
    		Platform.runLater(() -> {
    			RecorderGUI recordGUI = RecorderGUI.getInstance();
	    		recordGUI.init(controller);
	    		// clear all except hand visuals
				root3D.getChildren().removeIf((obj)->(!obj.getClass().equals(FXHandListener.class)));
				root2D.getChildren().clear();
				root2D.getChildren().add(subScene);
	    		root2D.getChildren().add(recordGUI);
    		});
    	}
    }
          
    public void stop() {
    	System.out.println("Stopping...");
        controller.removeListener(menuListener);
        Platform.exit();
    }
    
    public static void main(String[] args) {
    	launch(args);
    }
    
}