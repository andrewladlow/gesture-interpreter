package gestureinterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final float APP_WIDTH = 1280;
    public static final float APP_HEIGHT = 720;
    
    private Stage stage;
    private Scene scene;
    private SubScene subScene;
    private Group root3D;
    private StackPane root2D;
    private LeapListener leapListener;
    private Controller controller;
    private List<LeapButton> leapButtons;
    private Map<Integer, HandFX> hands;
    private Label titleLabel;   
    private LeapButton recognizerButton;
    private LeapButton recorderButton;
    
    public StackPane get2D() {
        return root2D;
    }

    public Group get3D() {
        return root3D;
    }

    public List<LeapButton> getLeapButtons() {
        return leapButtons;
    }

    /**
     * Handles the transition of the application's state. A state represents the
     * application window's content.
     * 
     * @param sceneName The name of the new scene to transition to.
     */
    public void swapScene(String sceneName) {

        Platform.runLater(() -> {
            // clear all buttons on 3D pane, clear all 2D pane
            leapButtons.clear();
            root3D.getChildren().removeIf((obj) -> (obj.getClass().equals(LeapButton.class)));
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
                leapButtons.add(recognizerButton);
                leapButtons.add(recorderButton);
                break;
            }
        });
    }
    
    /**
     * Creates the objects required for scene composition. 
     */
    private void createLayout() {
        root2D = new StackPane();
        root2D.setPrefSize(APP_WIDTH, APP_HEIGHT);

        root3D = new Group();
        root3D.setDepthTest(DepthTest.ENABLE);

        hands = new HashMap<Integer, HandFX>();
        leapListener = new LeapListener(hands, this);
        controller = new Controller();
        controller.addListener(leapListener);

        scene = new Scene(root2D, APP_WIDTH, APP_HEIGHT, false, SceneAntialiasing.BALANCED);
        subScene = new SubScene(root3D, APP_WIDTH, APP_HEIGHT, true, SceneAntialiasing.BALANCED);

        root2D.getChildren().addAll(subScene);
        root2D.getChildren().addAll(titleLabel);
        
        final PerspectiveCamera camera = new PerspectiveCamera(true);
        Translate cameraTranslation = new Translate(0, -200, -500);
        Rotate cameraRotation = new Rotate(0, 0, 0);
        camera.getTransforms().addAll(cameraTranslation, cameraRotation);
        camera.setFieldOfView(50);
        camera.setFarClip(750);
        camera.setNearClip(1);
        subScene.setCamera(camera);
    }
    
    /**
     * Creates the 2D text labels for the menu screen. 
     */
    private void createLabels() {
        titleLabel = new Label();
        titleLabel.textProperty().set("Menu");
        titleLabel.setFont(Font.font("Times New Roman", 24));
        StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
        StackPane.setMargin(titleLabel, new Insets(10, 0, 0, 10));
    }
    
    /**
     * Creates the 'recognizer' and 'recorder' buttons 
     * to be displayed on the application menu screen. 
     */
    private void createButtons() {
        recognizerButton = new LeapButton(APP_WIDTH, APP_HEIGHT, Color.CRIMSON, Color.SILVER, "Recognition");
        recognizerButton.setPosition(-170, -200, 110);
        recognizerButton.setRotation(0, Rotate.Z_AXIS);
        // System.out.println(recognizerButton.localToScene(recognizerButton.getBoundsInLocal()));
        root3D.getChildren().add(recognizerButton);

        recorderButton = new LeapButton(APP_WIDTH, APP_HEIGHT, Color.CRIMSON, Color.SILVER, "Calibration");
        recorderButton.setPosition(170, -200, 110);
        recorderButton.setRotation(0, Rotate.Z_AXIS);
        // System.out.println(recorderButton.localToScene(recorderButton.getBoundsInLocal()));
        root3D.getChildren().add(recorderButton);

        leapButtons = new ArrayList<LeapButton>();
        leapButtons.add(recognizerButton);
        leapButtons.add(recorderButton);
    }

    /**
     * The main entry point for all JavaFX applications. The start method is
     * called after the init method has returned, and after the system is ready
     * for the application to begin running.
     * 
     * @param primaryStage The primary stage for this application, onto which
     * the application scene can be set.
     */
    public void start(Stage primaryStage) {
        stage = primaryStage;

        createLabels();
        createLayout();
        createButtons();

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


}