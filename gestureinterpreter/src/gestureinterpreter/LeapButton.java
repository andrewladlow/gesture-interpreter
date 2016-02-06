package gestureinterpreter;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class LeapButton extends Group {
	
	private Box box;
	private Text text;
	private final Double oldDepth = 50.0;
    public static final EventType<LeapEvent> OPTIONS_ALL = new EventType<>("OPTIONS_ALL");
    public static final EventType<LeapEvent> PRESS = new EventType<>(OPTIONS_ALL, "PRESS");
    public static final EventType<LeapEvent> RELEASE = new EventType<>(OPTIONS_ALL, "RELEASE");
    private BooleanProperty touchStatus = new SimpleBooleanProperty();
    private long startTime = 0;
    private long endTime = 0;
    
	public LeapButton(double appWidth, double appHeight, Color diffuse, Color specular, String givenText) {    
        this.createBox(appWidth, appHeight, diffuse, specular);
        this.createText(appHeight, givenText);
        this.createEvents();
         
		this.getChildren().addAll(box, text);
	}
	
    public BooleanProperty touchStatusProperty() {
    	return touchStatus;
    }
	
	private void createBox(double appWidth, double appHeight, Color diffuse, Color specular) {
		box = ShapeCreator.createBox(200.0, 75.0,  50.0,  diffuse, specular);
        
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
        text.setFill(Color.WHITE);
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
        	//if (System.currentTimeMillis() - endTime > 500) {
	        	box.setDepth(0);
	        	text.setTranslateZ(text.getTranslateZ() + 40);
	        	System.out.println("Leap press event fired");
/*	        	Timeline timeline = new Timeline();
	        	timeline.getKeyFrames().add(new KeyFrame(Duration.millis(250),
	        								new KeyValue (box.depthProperty(), 0),
	        								new KeyValue (text.translateZProperty(), 40)));
	        	timeline.play();*/
        	//}
        });
        
        box.addEventHandler(RELEASE, (leapEv) -> {
        	//if (System.currentTimeMillis() - startTime > 500) {
	        	box.setDepth(oldDepth);
	        	text.setTranslateZ(text.getTranslateZ() - 40);
	        	System.out.println("Leap release event fired");
/*	        	Timeline timeline = new Timeline();
	        	timeline.getKeyFrames().add(new KeyFrame(Duration.millis(250),
	        								new KeyValue (box.depthProperty(), oldDepth),
	        								new KeyValue (text.translateZProperty(), (text.getTranslateZ()-40))));
	        	timeline.play();*/
        	//}
        });
        
        box.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                System.out.println("Custom box mouse press!");
            }
        });
        
        this.touchStatusProperty().addListener((boxVal, oldVal, newVal) -> {
        	if (newVal) {
        		box.fireEvent(leapPressEvent);
        	} 
        	else if (!newVal) {
        		box.fireEvent(leapReleaseEvent);
        	}
        });
	}
}
