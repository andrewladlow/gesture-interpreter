package gestureinterpreter;

import com.leapmotion.leap.Arm;
import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Bone.Type;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Matrix;
import com.leapmotion.leap.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class Driver extends Application {
    
    private LeapListener listener = null;
    private Controller controller = null;
    
    // possibly declare shapes around here and have runlater just modify them
    // instead of recreating over and over
    // java gc not freeing them properly?
    
/*  private Sphere[] palms = new Sphere[2];
	private Sphere[] fingers = new Sphere[10];
	private Sphere[] metacarpals = new Sphere[10];
	private Sphere[] proximals = new Sphere[10];
	private Sphere[] intermediates = new Sphere[10];    
	private Sphere[] distals = new Sphere[10];
	private Cylinder[] bones = new Cylinder[20];*/
	
    // possible alternative solution - obtain data directly, no lists etc?
    // http://www.oracle.com/technetwork/articles/java/rich-client-leapmotion-2227139.html
    // private final ListPropertyBone> testBones = new SimpleListProperty<Bone>(); 
    
    public void start(Stage primaryStage) {
        listener = new LeapListener();
        controller = new Controller();
        //controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
        //controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
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
       
/*      Group root = new Group();
        Scene scene = new Scene(root, 1280, 800, true, SceneAntialiasing.BALANCED);
        
        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setFieldOfView(50);
		camera.setTranslateX(-600);
		camera.setTranslateY(-600);
		camera.setTranslateZ(300);
        scene.setCamera(camera);
        */
        
        
        primaryStage.setTitle("Test Tracking");
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
        

        listener.frameReadyProperty().addListener(new ChangeListener<Boolean>() {
        	public void changed(ObservableValue<? extends Boolean> frameReady, Boolean oldVal, Boolean newVal) {
        		//System.out.println("Debug 1  " + frameReady + "  " + oldVal + "  " + newVal);
        		if (newVal) {
        			Frame frameCopy = controller.frame();    
					//List<Hand> handsCopy = listener.getHands();
        			//Frame frameCopy1 = listener.getFrame();
        			//System.out.println(frameCopy1.id());
					
					//System.out.println(controller.frame().id());
					//System.out.println(controller.frame(1).id());
        			
        			// Move to JavaFX thread? Required to avoid exceptions...
        			Platform.runLater(new Runnable() {
        				public void run() {			
        					//System.out.println("Debug 2  " + frameCopy.id());
        					root3D.getChildren().clear();

        					for (Hand hand : frameCopy.hands()) {
        						//System.out.println("Debug 3");
        			
        						Sphere handSphere = ShapeCreator.createSphere(root3D, 15, Color.GREEN, Color.LIGHTGREEN);
        						LeapToFX.move(handSphere, hand.palmPosition());
        						//LeapToFX.move(palms[hand.get(0)], hand.palmPosition());
        						
        						//Bone bone = bonesCopy.get(i);
        						//Hand hand = handsCopy.get(i);
        						
        						for (Finger finger : hand.fingers()) {       							
        							//System.out.println("Debug 4");
	        						//Finger finger = hand.fingers().get(i);
        							Sphere fingerSphere = ShapeCreator.createSphere(root3D, 7.5, Color.LIGHTGREEN, Color.GREENYELLOW);
        							LeapToFX.move(fingerSphere, finger.tipPosition());
        							
        							//fingers[i] = ShapeCreator.createSphere(root, 7.5, Color.DARKGREEN, Color.GREEN);
        							//LeapToFX.move(fingers[i], finger.tipPosition());
        								
        				/*			System.out.println("    " + finger.type() + ", id: " + finger.id()
    								+ ", length: " + finger.length()
    								+ "mm, width: " + finger.width() + "mm");*/
        							
        							for (Bone.Type boneType : Bone.Type.values()) {
        								//System.out.println("Debug 5");
        								Bone bone = finger.bone(boneType);
        								
        								// calculating similarity between a gesture and current hand frame
        								// can do this by taking the direction of bones which are normalized (magnitude of 1)
        								// comparison between 2 directions gives float between -1 and 1
        								// range defined as -1 being nothing alike and 1 being perfect match
        								// see http://xdpixel.com/dot-product-101/
        								// see http://betterexplained.com/articles/vector-calculus-understanding-the-dot-product/
        								
        								Vector currentDir = bone.direction();
        						        float result = currentDir.dot(new Vector(0f,0f,0f));
        						      //  System.out.println(currentDir);
        						       // System.out.println(result);
        						        
	        		/*						System.out.println("      " + bone.type()
	    									+ " bone, start: " + bone.prevJoint()
	    									+ ", end: " + bone.nextJoint()
	    									+ ", direction: " + bone.direction());*/
        						        
    				
        						        

        								Sphere jointSphere = ShapeCreator.createSphere(root3D, 7.5, Color.LIGHTGREEN, Color.GREENYELLOW);
        								LeapToFX.move(jointSphere, bone.prevJoint());
        								
        					        	// Move shapes instead of recreating? possible gc memory concern
        								
        								// this solution works - but inefficient?
        								// 0,1,0 because shape starts by pointing upwards
        								// y converted to -1, gety and getz negative due to inversion between leapmotion cartesian and javafx coordinates
        								// cross generates the axis that the shape spins around to fit
        								// cross is the same as (-direction Z, 0, direction X) vector
        								Cylinder boneCylinder = ShapeCreator.createCylinder(root3D, bone.width()/4, bone.length(), Color.LIGHTGREY, Color.WHITE);
        								
        	                            double angle = (new Vector(bone.direction().getX(), -bone.direction().getY(), -bone.direction().getZ())).angleTo(new Vector(0,-1,0));
        	                            Vector cross = (new Vector(bone.direction().getX(), -bone.direction().getY(), -bone.direction().getZ())).cross(new Vector(0,-1,0));
        								
        								boneCylinder.getTransforms().add(new Rotate(-Math.toDegrees(angle), 
        																0, 0, 0,
        																new Point3D(cross.getX(),cross.getY(),cross.getZ())));       								
        			
        								LeapToFX.move(boneCylinder, bone.center());       					
        								
/*        								System.out.println(dx);
        								System.out.println(cross.getX());
        								System.out.println(cross.getY());
        								System.out.println(cross.getZ());
        								System.out.println(bone.direction().getZ());
        								System.out.println(bone.direction());

        								// this solution looks like it may work?
        								// however bones are reflected on x axis and rotated 180 around y
        								// also requires omission of leaptofx.move function
        								// memory usage seems much lower?
        								// see ruzman.de leap motion articles
        								
        								Rotate rotation = new Rotate();
        								double dx = (float) (bone.prevJoint().getX() - bone.nextJoint().getX());
        								double dy = (float) (bone.prevJoint().getY() - bone.nextJoint().getY());
        								double dz = (float) (bone.prevJoint().getZ() - bone.nextJoint().getZ());

        								rotation.setPivotY(boneCylinder.getHeight() / 2);
        								rotation.setAxis(new Point3D(dz, 0, -dx));
        								rotation.setAngle(180 - new Point3D(dx, -dy, dz).angle(Rotate.Y_AXIS));       								
        								boneCylinder.getTransforms().addAll(rotation);
        								
        								//System.out.println(boneCylinder.getHeight());
        								//System.out.println(bone.length());
        								
        								boneCylinder.setTranslateX(bone.prevJoint().getX());
        								boneCylinder.setTranslateY(bone.prevJoint().getY() - boneCylinder.getHeight() / 2);
        								boneCylinder.setTranslateZ(bone.prevJoint().getZ());       								
        								//LeapToFX.move(boneCylinder, bone.center());
*/        							
        								}
        						}
	/*	    					System.out.println("      " + bone.type()
								+ " bone, start: " + bone.prevJoint()
								+ ", end: " + bone.nextJoint()
								+ ", direction: " + bone.direction());   */
		        			}
        				}
        			});
        		}
        		else if (controller.frame().hands().isEmpty()){
        			Platform.runLater(new Runnable() {
        				public void run() {
        					System.out.println("Debug 6");
        					root3D.getChildren().clear();
        				}
        			});
        		}
        	}
        });
        
    }
          
    public void stop(){
        controller.removeListener(listener);
    }
    
    public static void main(String[] args) {
        launch(args);
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