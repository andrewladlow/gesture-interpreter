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

public class LeapButton extends Group {
	
	private Box box;
	private Text text;
	private final Double oldDepth = 50.0;
	
    private BooleanProperty boxVal = new SimpleBooleanProperty();
    
    public BooleanProperty boxValProperty() {
    	return boxVal;
    }
	
	public LeapButton(float appWidth, float appHeight, Color diffuse, Color specular, String givenText) {
    
        this.createBox(appWidth, appHeight, diffuse, specular);
        this.createText(appHeight, givenText);
        this.createEvents();
        

        
        
		this.getChildren().addAll(box, text);
	}
	
	private void createBox(float appWidth, float appHeight, Color diffuse, Color specular) {
		box = ShapeCreator.createBox(200.0, 75.0,  50.0,  Color.RED, Color.GOLDENROD);
        double oldDepth = 50.0;
        //System.out.println(box.localToScene(box.getBoundsInLocal()));
        box.setLayoutX((appWidth/2)-600);
        box.setLayoutY((appHeight/2)-600);
        box.setTranslateZ(110);

        box.setRotationAxis(new Point3D(20, 0, 0));
        box.setRotate(30);
        box.setOnMousePressed((me) -> {
        	box.setDepth(0);
        });
        box.setOnMouseReleased((me) -> {
        	box.setDepth(oldDepth);
        });
	}
	
	private void createText(float appHeight, String givenText) {
        text = new Text(givenText);
        text.setStyle("-fx-font-size: 20; -fx-font-smoothing-type: lcd;");
        Bounds temp = text.localToScene(text.getBoundsInLocal());
        
        text.setLayoutX(-10);
        text.setLayoutY(((appHeight/2)-600) + temp.getHeight());
        text.setTranslateZ(110-box.getDepth());
        text.setCache(true);
        text.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        text.setRotationAxis(new Point3D(20, 0, 0));
        text.setRotate(30);
	}
	
	private void createEvents() {
        EventType<LeapEvent> OPTIONS_ALL = new EventType<>("OPTIONS_ALL");

        EventType<LeapEvent> PRESS = new EventType<>(OPTIONS_ALL, "PRESS");
        
        EventType<LeapEvent> RELEASE = new EventType<>(OPTIONS_ALL, "RELEASE");

        Event leapPressEvent = new LeapEvent(PRESS);
        Event leapReleaseEvent = new LeapEvent(RELEASE);
        
        box.addEventHandler(PRESS, (leapEv) -> {
        	box.setDepth(0);
        	text.setTranslateY(-10);
        	text.setTranslateZ(text.getTranslateZ()-oldDepth);
        	System.out.println("Leap press event");
        });
        
        box.addEventHandler(RELEASE, (leapEv) -> {
        	box.setDepth(oldDepth);
        	text.setTranslateY(5);
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
        });
	}
}
