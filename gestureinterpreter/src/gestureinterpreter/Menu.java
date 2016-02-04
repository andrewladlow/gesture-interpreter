package gestureinterpreter;

import java.util.HashMap;

import com.leapmotion.leap.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Menu extends Application {
	
	public static final float APPWIDTH = 1280;
	public static final float APPHEIGHT = 720;
	
	private Stage stage;
	private Scene scene;
	private SubScene subScene;
    private Group root3D;
	private StackPane root2D;
	private LeapListener leapListener;
    private MenuListener menuListener;
    private Controller controller;
    
    private FXHandListener handRenderer;
	private HashMap<Integer, HandFX> hands;
	
	private Label titleLabel;
	
	public LeapButton recognizerButton;
	public LeapButton recorderButton;
	
    public void start(Stage primaryStage) {
    	this.stage = primaryStage;
    	
        hands = new HashMap<Integer, HandFX>();
        leapListener = new LeapListener();
        menuListener = new MenuListener(this);
        controller = new Controller();
        controller.addListener(leapListener);
        controller.addListener(menuListener);
        
        root2D = new StackPane();
        root2D.setPrefSize(APPWIDTH, APPHEIGHT);
        
        root3D = new Group();
		root3D.setDepthTest(DepthTest.ENABLE);

        scene = new Scene(root2D, APPWIDTH, APPHEIGHT, false, SceneAntialiasing.BALANCED);
        subScene = new SubScene(root3D, APPWIDTH, APPHEIGHT, true, SceneAntialiasing.BALANCED);
        
        //scene.setCursor(Cursor.NONE);
        root2D.getChildren().addAll(subScene);
              
        titleLabel = new Label();
        titleLabel.textProperty().set("Main Menu");
        titleLabel.setTranslateX(10);
        titleLabel.setTranslateY(10);
        titleLabel.setFont(Font.font("Times New Roman", 24)); 
        root2D.getChildren().addAll(titleLabel);
        
        StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);

        final PerspectiveCamera camera = new PerspectiveCamera(true);	
        Translate cameraTranslation = new Translate(0, -200, -500);
        Rotate cameraRotation = new Rotate(0, 0, 0);
        camera.getTransforms().addAll(cameraTranslation, cameraRotation);
		camera.setFieldOfView(50);
		camera.setFarClip(750);
		camera.setNearClip(1);
        subScene.setCamera(camera); 
        
        recognizerButton = new LeapButton(APPWIDTH, APPHEIGHT, Color.RED, Color.GOLDENROD, "Recognition");     
        recognizerButton.setPosition(-170, -200, 110);
        recognizerButton.setRotation(0, Rotate.Z_AXIS);
        
        System.out.println(recognizerButton.localToScene(recognizerButton.getBoundsInLocal()));
        
        root3D.getChildren().add(recognizerButton);
        
        recorderButton = new LeapButton(APPWIDTH, APPHEIGHT, Color.RED, Color.GOLDENROD, "Calibration");       
        recorderButton.setPosition(170, -200, 110);
        recorderButton.setRotation(0, Rotate.Z_AXIS);
        
        System.out.println(recorderButton.localToScene(recorderButton.getBoundsInLocal()));
        
        root3D.getChildren().add(recorderButton);

        handRenderer = new FXHandListener(controller, leapListener, hands);
        root3D.getChildren().add(handRenderer);
             
        stage.setTitle("Gesture Interpreter");
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {
                    public void run() {
                    	// force release of any held resources on exit
                        System.exit(0);
                    }
                });
            }
        });
        
        stage.setScene(scene);
        stage.show();
    }
    
    public void swapScene(String sceneName) {
    	
    	Platform.runLater(() -> {
    		controller.removeListener(menuListener);
    		// clear all except hand visuals
    		root3D.getChildren().removeIf((obj)->(!obj.getClass().equals(FXHandListener.class)));
    		root2D.getChildren().clear();
    		root2D.getChildren().add(subScene);
				
	    	if (sceneName.equals("Recognizer")) {
	        	System.out.println("Swapping to recognizer");

		    	RecognizerGUI recogGUI = RecognizerGUI.getInstance();
		    	recogGUI.init(this, controller);
	    	} 
	    	
	    	else if (sceneName.equals("Recorder")) {
	    		System.out.println("Swapping to recorder");
	
	    		RecorderGUI recordGUI = RecorderGUI.getInstance();
		    	recordGUI.init(this, controller);
	    	}
	    	
	    	else if (sceneName.equals("Menu")) {
	    		System.out.println("Swapping to menu");
	    		
	            root2D.getChildren().addAll(titleLabel);
	            root3D.getChildren().addAll(recognizerButton, recorderButton);
	            controller.addListener(menuListener);
	    	}
    	});
    }
    
    public StackPane get2D() {
    	return this.root2D;
    }
    
    public Group get3D() {
    	return this.root3D;
    }
    
}