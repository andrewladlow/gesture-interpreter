package gestureinterpreter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class LeapButton extends Group {
	
	private Box box;
	private Text text;
	private final Double oldDepth = 50.0;
    private BooleanProperty touchStatus = new SimpleBooleanProperty();
    
	public LeapButton(double appWidth, double appHeight, Color diffuse, Color specular, String givenText) {    
        createBox(appWidth, appHeight, diffuse, specular);
        createText(appHeight, givenText);
        addListener();
         
		this.getChildren().addAll(box, text);
	}
	
    public BooleanProperty touchStatusProperty() {
    	return touchStatus;
    }
	
	private void createBox(double appWidth, double appHeight, Color diffuse, Color specular) {
		box = ShapeCreator.createBox(200.0, 65.0,  50.0,  diffuse, specular);
	}
	
	private void createText(double appHeight, String givenText) {
        text = new Text(givenText);
        text.setStyle("-fx-font-size: 20; -fx-font-smoothing-type: lcd;");
        //text.setFill(Color.SILVER);
	}
	
	public String getText() {
		return text.textProperty().getValue();
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
	
	private void addListener() {    
        touchStatusProperty().addListener((boxVal, oldVal, newVal) -> {
        	if (newVal && !oldVal) {
            	box.setDepth(25);
            	text.setTranslateZ(text.getTranslateZ() + 15);
            	System.out.println("Leap press event fired");
        	} 
        	else if (!newVal && oldVal) {
            	box.setDepth(oldDepth);
            	text.setTranslateZ(text.getTranslateZ() - 15);
            	System.out.println("Leap release event fired");
        	}
        });
	}
}
