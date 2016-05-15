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

/**
 * Custom 3D button class which supports Leap Motion interaction.
 */
public class LeapButton extends Group {
    private final Double OLD_DEPTH = 50.0;

    private Box box;
    private Text text;
    private BooleanProperty touchStatus = new SimpleBooleanProperty();

    /**
     * Class constructor which creates a new Leap Button.
     * 
     * @param appWidth The width of the application this button is placed in.
     * @param appHeight The height of the application this button is placed in.
     * @param diffuse The diffuse colour of this button.
     * @param specular The specular colour of this button.
     * @param givenText The text to be displayed on this button.
     */
    public LeapButton(double appWidth, double appHeight, Color diffuse, Color specular, String givenText) {
        createBox(appWidth, appHeight, diffuse, specular);
        createText(appHeight, givenText);
        addListener();

        this.getChildren().addAll(box, text);
    }

    /**
     * Returns this objects touch status boolean property.
     * 
     * @return The touch status boolean property.
     */
    public BooleanProperty touchStatusProperty() {
        return touchStatus;
    }

    /**
     * Creates a 3D box representing this leap button.
     * 
     * @param appWidth The width of the application this button is placed in.
     * @param appHeight The height of the application this button is placed in.
     * @param diffuse The diffuse colour of this button.
     * @param specular The specular colour of this button.
     */
    private void createBox(double appWidth, double appHeight, Color diffuse, Color specular) {
        box = ShapeHelper.createBox(200.0, 65.0, 50.0, diffuse, specular);
    }

    /**
     * Creates the text shown on this leap button.
     * 
     * @param appHeight The height of the application this button is placed in.
     * @param givenText The text to be displayed on this button.
     */
    private void createText(double appHeight, String givenText) {
        text = new Text(givenText);
        text.setStyle("-fx-font-size: 20; -fx-font-smoothing-type: lcd;");
        // text.setFill(Color.SILVER);
    }

    /**
     * Returns this button's text.
     * 
     * @return The text of this button.
     */
    public String getText() {
        return text.textProperty().getValue();
    }

    /**
     * Sets the location of this button within the application.
     * 
     * @param xPos The new X co-ordinate of this button.
     * @param yPos The new Y co-ordinate of this button.
     * @param zPos The new Z co-ordinate of this button.
     */
    public void setPosition(double xPos, double yPos, double zPos) {
        Translate translate = new Translate(xPos, yPos, zPos);
        box.getTransforms().addAll(translate);

        Bounds textBounds = text.localToScene(text.getBoundsInLocal());
        text.setLayoutX(xPos - box.getWidth() / 4);
        text.setLayoutY(yPos + (textBounds.getHeight() / 2) - 5);
        text.setTranslateZ(zPos - box.getDepth());
    }

    /**
     * Sets the rotation of this button within the application.
     * 
     * @param rotation The degree of rotation applied to this button.
     * @param axis The axis to rotate this button around.
     */
    public void setRotation(double rotation, Point3D axis) {
        box.getTransforms().addAll(new Rotate(rotation, axis));

        text.setCache(true);
        text.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        text.getTransforms().addAll(new Rotate(rotation, axis));
    }

    /**
     * Adds an interaction listener which is triggered when the button is
     * pressed.
     */
    public void addListener() {
        touchStatusProperty().addListener((boxVal, oldVal, newVal) -> {
            if (newVal && !oldVal) {
                box.setDepth(25);
                text.setTranslateZ(text.getTranslateZ() + 15);
                System.out.println("Leap press event fired");
            } else if (!newVal && oldVal) {
                box.setDepth(OLD_DEPTH);
                text.setTranslateZ(text.getTranslateZ() - 15);
                System.out.println("Leap release event fired");
            }
        });
    }
}
