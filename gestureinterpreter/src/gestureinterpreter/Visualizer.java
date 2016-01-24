package gestureinterpreter;

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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Visualizer extends Application {
	
	private Stage stage;
	private Scene scene;
	private Group root;
    private LeapListener listener = null;
    private Controller controller = null;
    
    private FXHandListener handRenderer;
	private HashMap<Integer, HandFX> hands;
	
	public LeapButton leapButton1;
	
    private BooleanProperty swapVal = new SimpleBooleanProperty(false);
    
    public BooleanProperty swapValProperty() {
    	return swapVal;
    }
	
    
    public void start(Stage primaryStage) {
    	this.stage = primaryStage;

        hands = new HashMap<Integer, HandFX>();
        
        listener = new LeapListener(this);
        controller = new Controller();
        controller.addListener(listener);
        
        root = new Group();
      
        scene = new Scene(root, 1280, 600, true, SceneAntialiasing.BALANCED);

        final PerspectiveCamera camera = new PerspectiveCamera();
        camera.setFieldOfView(50);
		camera.setTranslateX(-600);
		camera.setTranslateY(-600);
		camera.setTranslateZ(250);
			
        scene.setCamera(camera);
        
        leapButton1 = new LeapButton(1280, 600, Color.RED, Color.GOLDENROD, "Recognition");
        root.getChildren().add(leapButton1);     
        
        handRenderer = new FXHandListener(controller, listener, hands);
        root.getChildren().add(handRenderer);
        
        stage.setTitle("Gesture Interpreter");
        stage.setScene(scene);
        stage.show();
        
/*        this.swapValProperty().addListener((swapVal, oldVal, newVal) -> {
        	if (newVal) {
        		Platform.runLater(() -> {
            		RecognizerGUI RecognizerGUI = new RecognizerGUI();
            		root.getChildren().clear();
            		root.getChildren().add(RecognizerGUI);
        		});
        	}
        });*/
    }
    
    public void swapScene(String sceneName) {
    	//if (sceneName.equals("Recognizer")) {
    		//stage.hide();
        	System.out.println("INIT1");
            //controller.removeListener(listener);
        	Platform.runLater(() -> {
	    		RecognizerGUI RecognizerGUI = new RecognizerGUI();
	    		// clear all except hand visuals
				root.getChildren().removeIf((obj)->(!obj.getClass().equals(FXHandListener.class)));
	    		root.getChildren().add(RecognizerGUI);
	            //root.getChildren().add(handRenderer);
        	});
    		//root.getChildren().add(handRenderer);
    		//Scene RecogScene = RecognizerGUI.start();
    		//stage.setScene(RecogScene);
    	//	stage.show();
    	//}
    }
          
    public void stop() {
    	System.out.println("Stopping...");
        controller.removeListener(listener);
        Platform.exit();
    }
    
    public static void main(String[] args) {
    	launch(args);
    }
    
}