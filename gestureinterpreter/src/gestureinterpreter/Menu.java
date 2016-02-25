package gestureinterpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.leapmotion.leap.Controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
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

/**
 * The main class of the application, initiated by driver class.
 */
public class Menu extends Application {

	public static final float APPWIDTH = 1280;
	public static final float APPHEIGHT = 720;

	private Stage stage;
	private Scene scene;
	private SubScene subScene;
    private Group root3D;
	private StackPane root2D;
	private LeapListener leapListener;
    private Controller controller;
    private List<LeapButton> leapButtons;

	private HashMap<Integer, HandFX> hands;

	private Label titleLabel;

	public LeapButton recognizerButton;
	public LeapButton recorderButton;


	/**
	 * The main entry point for all JavaFX applications.
	 * The start method is called after the init method has returned,
	 * and after the system is ready for the application to begin running.
	 * @param primaryStage The primary stage for this application, onto
	 * which the application scene can be set.
	 */
    public void start(Stage primaryStage) {
    	stage = primaryStage;
        root2D = new StackPane();
        root2D.setPrefSize(APPWIDTH, APPHEIGHT);

        root3D = new Group();
		root3D.setDepthTest(DepthTest.ENABLE);

        hands = new HashMap<Integer, HandFX>();
        leapListener = new LeapListener(hands, this);
        controller = new Controller();
        controller.addListener(leapListener);

        scene = new Scene(root2D, APPWIDTH, APPHEIGHT, false, SceneAntialiasing.BALANCED);
        subScene = new SubScene(root3D, APPWIDTH, APPHEIGHT, true, SceneAntialiasing.BALANCED);

        //scene.setCursor(Cursor.NONE);
        root2D.getChildren().addAll(subScene);

        titleLabel = new Label();
        titleLabel.textProperty().set("Menu");
        titleLabel.setFont(Font.font("Times New Roman", 24));
        root2D.getChildren().addAll(titleLabel);

        StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
		StackPane.setMargin(titleLabel, new Insets(10,0,0,10));

        final PerspectiveCamera camera = new PerspectiveCamera(true);
        Translate cameraTranslation = new Translate(0, -200, -500);
        Rotate cameraRotation = new Rotate(0, 0, 0);
        camera.getTransforms().addAll(cameraTranslation, cameraRotation);
		camera.setFieldOfView(50);
		camera.setFarClip(750);
		camera.setNearClip(1);
        subScene.setCamera(camera);

        recognizerButton = new LeapButton(APPWIDTH, APPHEIGHT, Color.CRIMSON, Color.SILVER, "Recognition");
        recognizerButton.setPosition(-170, -200, 110);
        recognizerButton.setRotation(0, Rotate.Z_AXIS);

        //System.out.println(recognizerButton.localToScene(recognizerButton.getBoundsInLocal()));

        root3D.getChildren().add(recognizerButton);

        recorderButton = new LeapButton(APPWIDTH, APPHEIGHT, Color.CRIMSON, Color.SILVER, "Calibration");
        recorderButton.setPosition(170, -200, 110);
        recorderButton.setRotation(0, Rotate.Z_AXIS);

        //System.out.println(recorderButton.localToScene(recorderButton.getBoundsInLocal()));

        root3D.getChildren().add(recorderButton);

        leapButtons = new ArrayList<LeapButton>();
        leapButtons.add(recognizerButton);
        leapButtons.add(recorderButton);

        stage.setTitle("Gesture Interpreter");

        stage.setOnCloseRequest((event) -> {
            Platform.runLater(() -> {
            	// force release of any held resources on exit
                System.exit(0);
            });
        });

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles the transition of the application's state.
     * A state represents the application window's content.
     * @param sceneName The name of the new scene to transition to.
     */
    public void swapScene(String sceneName) {

    	Platform.runLater(() -> {
    		// clear all except hand visuals
    		root3D.getChildren().removeIf((obj)->(obj.getClass().equals(LeapButton.class)));
    		root2D.getChildren().clear();
    		root2D.getChildren().add(subScene);

    		switch (sceneName) {
    		case "Recognition":
		    	RecognizerGUI recogGUI = RecognizerGUI.getInstance();
		    	recogGUI.init(this, controller);
		    	break;
    		case "Calibration":
	    		RecorderGUI recordGUI = RecorderGUI.getInstance();
		    	recordGUI.init(this, controller);
		    	break;
    		case "Menu":
	    		controller.addListener(leapListener);
	            root2D.getChildren().addAll(titleLabel);
	            root3D.getChildren().addAll(recognizerButton, recorderButton);
				leapButtons.clear();
				leapButtons.add(recognizerButton);
				leapButtons.add(recorderButton);
				break;
    		}
    	});
    }

    /**
     * Returns the object holding all 2D content.
     * @return The 2D stackPane.
     */
    public StackPane get2D() {
    	return root2D;
    }

    /**
     * Returns the object holding all 3D content.
     * @return The 3D group.
     */
    public Group get3D() {
    	return root3D;
    }

    /**
     * Returns an array of all active leap button objects.
     * @return Leap button array.
     */
    public List<LeapButton> getLeapButtons() {
    	return leapButtons;
    }

}