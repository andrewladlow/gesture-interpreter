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
    private LeapListener listener = null;
    private Controller controller = null;
    
/*    private BooleanProperty boxVal = new SimpleBooleanProperty();
    
    public BooleanProperty boxValProperty() {
    	return boxVal;
    }*/
    
	private HashMap<Integer, HandFX> hands;
	
	public LeapButton leapButton1;
    
    public void start(Stage primaryStage) {
    	this.stage = primaryStage;
    	this.createMenu() {
    		
    	}
        hands = new HashMap<Integer, HandFX>();
        
        listener = new LeapListener(this);
        controller = new Controller();
        //controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
        //controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
        //controller.enableGesture(Type.TYPE_SCREEN_TAP);
        controller.addListener(listener);
        
        
        Group root2D = new Group();
        //StackPane root = new StackPane();
        //AnchorPane root = new AnchorPane();
        //GridPane root = new GridPane();
        
        Scene scene = new Scene(root2D, 1280, 600);
        Button btn = new Button();
        btn.setText("Hello world");
        root2D.getChildren().add(btn);
        btn.setOnAction(new EventHandler<ActionEvent>() {
        	
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
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
        
        leapButton1 = new LeapButton(1280, 600, Color.RED, Color.GOLDENROD, "Recognition");
        
        root3D.getChildren().addAll(leapButton1);
        
/*        box = ShapeCreator.createBox(200.0, 75.0,  50.0,  Color.RED, Color.GOLDENROD);
        double oldDepth = 50.0;
        System.out.println(box.localToScene(box.getBoundsInLocal()));
        box.setTranslateX((scene.getWidth()/2)-600);
        box.setTranslateY((scene.getHeight()/2)-600);
        box.setTranslateZ(110);

        System.out.println(box.localToScene(box.getBoundsInLocal()));

        box.setRotationAxis(new Point3D(20, 0, 0));
        box.setRotate(30);
        
        box.setOnMousePressed((me) -> {
        	box.setDepth(0);
        });
        box.setOnMouseReleased((me) -> {
        	box.setDepth(oldDepth);
        });
        
        Button button1 = new Button("Test Button");
        button1.setTranslateX(500);
        button1.setTranslateY(100);
        button1.getStyleClass().add("leap-button");
        scene.getStylesheets().add("gestureinterpreter/Buttons.css");
        
        //root2D.getChildren().addAll(button1);
        
        
        Text text = new Text("TEST TEXT");
        text.setStyle("-fx-font-size: 20;-fx-font-smoothing-type: lcd;");
        Bounds temp = text.localToScene(text.getBoundsInLocal());
        
        text.setTranslateZ(110-box.getDepth());
        text.setTranslateY(((scene.getHeight()/2)-600) + temp.getHeight());
        //text.setTranslateY(temp.getMaxY()-temp.getMinY());
        text.setCache(true);
        text.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        text.setRotationAxis(new Point3D(20, 0, 0));
        text.setRotate(30);
        System.out.println(text.localToScene(text.getBoundsInLocal()));
        
        
        
        
        EventType<LeapEvent> OPTIONS_ALL = new EventType<>("OPTIONS_ALL");

        EventType<LeapEvent> PRESS = new EventType<>(OPTIONS_ALL, "PRESS");
        
        EventType<LeapEvent> RELEASE = new EventType<>(OPTIONS_ALL, "RELEASE");


        Event leapPressEvent = new LeapEvent(PRESS);
        Event leapReleaseEvent = new LeapEvent(RELEASE);
        
        box.addEventHandler(PRESS, (leapEv) -> {
        	box.setDepth(0);
        	text.setTranslateZ(text.getTranslateZ()-oldDepth);
        	System.out.println("Leap press event");
        });
        
        box.addEventHandler(RELEASE, (leapEv) -> {
        	box.setDepth(oldDepth);
        	text.setTranslateZ(text.getTranslateZ()+oldDepth);
        	System.out.println("Leap release event");
        });
        
        box.setOnMousePressed(new EventHandler<MouseEvent>() {
        	
            public void handle(MouseEvent event) {
                System.out.println("Custom box mouse press!");
            }
        });
        
        
        this.boxValProperty().addListener((boxVal, oldVal, newVal) -> {
        	if (newVal) {
        		box.fireEvent(leapPressEvent);
        		//System.out.println("Fired press event");
        	} else if (!newVal) {
        		box.fireEvent(leapReleaseEvent);
        		//System.out.println("Fired release event");
        	}
        });*/
        
        
/*        BorderPane borderPane = new BorderPane();
        //borderPane.setStyle("-fx-border-color: black;-fx-background-color: #66CCFF;");
        borderPane.setTop(text);

        borderPane.setTranslateX((scene.getWidth()/2)-600);
        borderPane.setTranslateY((scene.getHeight()/2)-600);
        borderPane.setTranslateZ(110);
        borderPane.setRotationAxis(new Point3D(20, 0, 0));
        borderPane.setRotate(30);

        //borderPane.setCache(true);
        //borderPane.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        
        //root3D.getChildren().addAll(borderPane);
        
        StackPane stack = new StackPane();
        //stack.getChildren().addAll(text);
        
        //root3D.getChildren().addAll(stack);
*/        
        //root3D.getChildren().addAll(box, text);

        
       
/*      Group root = new Group();
        Scene scene = new Scene(root, 1280, 800, true, SceneAntialiasing.BALANCED);
        
        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setFieldOfView(50);
		camera.setTranslateX(-600);
		camera.setTranslateY(-600);
		camera.setTranslateZ(300);
        scene.setCamera(camera);
        */
        
        
        primaryStage.setTitle("Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
/*        for (int i = 0; i < palms.length; i++) {
        	palms[i] = ShapeCreator.createSphere(root, 10, Color.GREEN, Color.LIGHTGREEN);
        }
        for (int i = 0; i < fingers.length; i++) {
        	fingers[i] = ShapeCreator.createSphere(root, 7.5, Color.GREEN, Color.LIGHTGREEN);
        	metacarpals[i] = ShapeCreator.createSphere(root, 7.5, Color.GREEN, Color.LIGHTGREEN);
        	proximals[i] = ShapeCreator.createSphere(root, 7.5, Color.GREEN, Color.LIGHTGREEN);
        	intermediates[i] = ShapeCreator.createSphere(root, 7.5, Color.GREEN, Color.LIGHTGREEN);
        	distals[i] = ShapeCreator.createSphere(root, 7.5, Color.GREEN, Color.LIGHTGREEN);
        }
        for (int i = 0; i< bones.length; i++) {
        	bones[i] = ShapeCreator.createCylinder(root, 4, 4, Color.LIGHTGREY, Color.WHITE);
        }*/
        
/*        Vector origVec = new Vector(2f,2f,0f);
        Vector newVec = new Vector(4f,2f,0f);
        
        float result = origVec.dot(newVec);
        System.out.println(result);*/
        

        listener.frameReadyProperty().addListener((frameReady, oldVal, newVal) -> {
    		Frame frame = controller.frame();
    		
    		// draw hands if at least one is present in tracking area
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
 	
    		// remove hand if it leaves tracking area
    		} else if (frame.hands().count() < controller.frame(1).hands().count()) {
    			Platform.runLater(() -> {
					System.out.println("Debug 6");
					for (Hand leapHand : frame.hands()) {
						// first remove from storage hashmap
						hands.remove(leapHand.id());
					}
					// then remove from display (filtering for only hand objects)
					root3D.getChildren().removeIf((obj)->obj.getClass().equals(HandFX.class));
    			});
    		}
    	});
        
    }
          
    public void stop(){
        controller.removeListener(listener);
    }
    
}

/*public class Driver extends Application {
	
	private final DoubleProperty centerY = new SimpleDoubleProperty(0);
	private final DoubleProperty centerX = new SimpleDoubleProperty(0);
	private final DoubleProperty radius = new SimpleDoubleProperty(10);
	
	private Controller controller;
	private LeapListener listener;
	
	public DoubleProperty centerX() {
		return centerX;
	}
	
	public DoubleProperty centerY() {
		return centerY;
	}
	
	public DoubleProperty radius() {
		return radius;
	}
	
	public void start(Stage primaryStage) {
		PerspectiveCamera camera = new PerspectiveCamera(true);
		
		camera.setTranslateY(10);
		camera.setRotationAxis(Rotate.X_AXIS);
		Sphere sphere = new Sphere(20);
		
		final PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(Color.DARKRED);
		mat.setSpecularColor(Color.RED);
		
		sphere.setMaterial(mat);
		sphere.translateXProperty().bind(centerX);
		sphere.translateYProperty().bind(centerY);
		
		StackPane root = new StackPane();
		root.getChildren().add(sphere);
		Scene scene = new Scene(root, 500, 500);
		primaryStage.setTitle("Leap Motion Testing");
		primaryStage.setScene(scene);
		primaryStage.show();
	
		controller = new Controller();
		listener = new LeapListener(this);
		controller.addListener(listener);
	}
	
	public void stop() {
		controller.removeListener(listener);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}*/