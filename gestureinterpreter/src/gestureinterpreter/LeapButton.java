package gestureinterpreter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class LeapButton extends Group {
	
	private Box box;
	private Text text;
	private final Double oldDepth = 50.0;

    public static final EventType<LeapEvent> OPTIONS_ALL = new EventType<>("OPTIONS_ALL");
    public static final EventType<LeapEvent> PRESS = new EventType<>(OPTIONS_ALL, "PRESS");
    public static final EventType<LeapEvent> RELEASE = new EventType<>(OPTIONS_ALL, "RELEASE");
	
    private BooleanProperty touchStatus = new SimpleBooleanProperty();
    
    public BooleanProperty touchStatusProperty() {
    	return touchStatus;
    }
	
	public LeapButton(double appWidth, double appHeight, Color diffuse, Color specular, String givenText) {
    
        this.createBox(appWidth, appHeight, diffuse, specular);
        this.createText(appHeight, givenText);
        this.createEvents();
         
		this.getChildren().addAll(box, text);
	}
	
	private void createBox(double appWidth, double appHeight, Color diffuse, Color specular) {
		box = ShapeCreator.createBox(200.0, 75.0,  50.0,  Color.RED, Color.GOLDENROD);
        //System.out.println(box.localToScene(box.getBoundsInLocal()));
        
        //box.setLayoutX((appWidth/2)-appHeight);
        //box.setLayoutY((appHeight/2)-appHeight);
        //box.setTranslateZ(110);
        
        //System.out.println("1 :" + box.getBoundsInLocal());
        //System.out.println("2 :" + box.getBoundsInParent());
        
        //box.setTranslateX(40);
        //box.setTranslateY(-285);
        //box.setTranslateZ(110);
        
        //System.out.println("1- : " + box.getBoundsInLocal());
        //System.out.println("2- : " + box.getBoundsInParent());

        //box.setRotationAxis(new Point3D(20, 0, 0));
        //box.setRotate(30);
        
        //System.out.println(box.localToScene(box.getBoundsInLocal()));
        
        box.setOnMousePressed((me) -> {
        	box.setDepth(0);
        });
        box.setOnMouseReleased((me) -> {
        	box.setDepth(oldDepth);
        });
	}
	
	private void createText(double appHeight, String givenText) {
        text = new Text(givenText);
        text.setStyle("-fx-font-size: 20; -fx-font-smoothing-type: lcd;");
        //Bounds temp = text.localToScene(text.getBoundsInLocal());
        //text.setLayoutX(-10);
        //text.setLayoutY(((appHeight/2)-appHeight) + temp.getHeight());
        //text.setTranslateZ(110-box.getDepth());
        //text.setCache(true);
        //text.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        //text.setRotationAxis(new Point3D(20, 0, 0));
        //text.setRotate(30);
	}
	
	public void setPosition (double xPos, double yPos, double zPos) {		
		Translate translate = new Translate(xPos, yPos, zPos);
		box.getTransforms().addAll(translate);
		
		Bounds textBounds = text.localToScene(text.getBoundsInLocal());
		text.setLayoutX(xPos-box.getWidth()/4);
		text.setLayoutY(yPos + (textBounds.getHeight()/2) -5);
		text.setTranslateZ(zPos-box.getDepth());
	}
	
	public void setRotation(double rotation, Point3D axis) {
		box.getTransforms().addAll(new Rotate(rotation, axis));
		
        text.setCache(true);
        text.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        text.getTransforms().addAll(new Rotate (rotation, axis));
	}
	
	private void createEvents() {


        Event leapPressEvent = new LeapEvent(PRESS);
        Event leapReleaseEvent = new LeapEvent(RELEASE);
        
        box.addEventHandler(PRESS, (leapEv) -> {
        	box.setDepth(0);
        	//text.setTranslateY(-10);
        	text.setTranslateZ(text.getTranslateZ() + 40);
        	System.out.println("Leap press event");
        });
        
        box.addEventHandler(RELEASE, (leapEv) -> {
        	box.setDepth(oldDepth);
        	//text.setTranslateY(5);
        	text.setTranslateZ(text.getTranslateZ()- 40);
        	System.out.println("Leap release event");
        });
        
        box.setOnMousePressed(new EventHandler<MouseEvent>() {
        	
            public void handle(MouseEvent event) {
                System.out.println("Custom box mouse press!");
            }
        });
        
        this.touchStatusProperty().addListener((boxVal, oldVal, newVal) -> {
        	if (newVal) {
        		box.fireEvent(leapPressEvent);
        		//System.out.println("Fired press event");
        	} else if (!newVal) {
        		box.fireEvent(leapReleaseEvent);
        		//System.out.println("Fired release event");
        	}
        });
	}
}
