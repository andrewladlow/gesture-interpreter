package gestureinterpreter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.leapmotion.leap.Controller;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * Class handling the GUI of the recording section of the application.
 */
public class RecorderGUI {
    private static RecorderGUI instance;
    
    private Object lock = new Object();
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private Boolean alreadyActivated = false;
    private Label titleLabel;
    private Label resultLabel;
    private Rectangle gestureImgRect;

    private RecorderGUI() {
    }

    /**
     * Singleton only allows a single instance of this class to be created.
     */
    public static RecorderGUI getInstance() {
        if (instance == null) {
            instance = new RecorderGUI();
        }
        return instance;
    }
    
    /**
     * Creates all nodes for the recorder screen. 
     */
    private void createNodes() {
        titleLabel = new Label();
        titleLabel.textProperty().set("Gesture Recorder");
        titleLabel.setFont(Font.font("Times New Roman", 24));

        resultLabel = new Label();
        resultLabel.setFont(Font.font("Times New Roman", 24));

        gestureImgRect = new Rectangle();
        gestureImgRect.setWidth(300);
        gestureImgRect.setHeight(260);
        gestureImgRect.setStroke(Color.BLACK);
        gestureImgRect.setScaleX(0.625);
        gestureImgRect.setScaleY(0.625);
    }
    
    /**
     * Aligns all nodes on the recorder screen. 
     */
    private void alignNodes() {
        StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
        StackPane.setMargin(titleLabel, new Insets(10, 0, 0, 10));
        StackPane.setAlignment(resultLabel, Pos.TOP_CENTER);
        StackPane.setMargin(resultLabel, new Insets(10, 0, 0, 0));
        StackPane.setAlignment(gestureImgRect, Pos.TOP_CENTER);
        StackPane.setMargin(gestureImgRect, new Insets(10, 0, 0, 0));
    }

    /**
     * Starts the recording process, training each gesture candidate
     * before returning to the main menu. 
     * @param app The application menu. 
     * @param controller The Leap Motion controller. 
     */
    private void beginRecording(Menu app, Controller controller) {
        // begin on new thread so as to not block rendering of hand movement
        executor.execute(() -> {
            for (char c = 'A'; c <= 'Z'; c++) {
                char tempChar = c;
                // load example image for each gesture and add to render
                Image gestureImg = new Image("file:images/" + Character.toLowerCase(tempChar) + ".png");
                Platform.runLater(() -> {
                    gestureImgRect.setFill(new ImagePattern(gestureImg));
                });
                try {
                    for (int i = 3; i > 0; i--) {
                        int count = i;
                        Platform.runLater(() -> {
                            resultLabel.textProperty().set("Recording " + tempChar + " in " + count + "...");
                            TextHelper.textFadeOut(1000, resultLabel);
                        });
                        Thread.sleep(1000);
                    }
                    TextHelper.textFadeIn(50, resultLabel);
                    Platform.runLater(() -> {
                        resultLabel.textProperty().set("Now recording " + tempChar + "...");
                    });
                    System.out.println(c);
                    // create new trainer instance to record a provided gesture
                    RecorderListener tempRecorderListener = new RecorderListener(c, lock);
                    controller.addListener(tempRecorderListener);
                    // wait on this thread until recording is finished
                    synchronized (lock) {
                        while (!tempRecorderListener.gestureDoneProperty().get()) {
                            lock.wait();
                        }
                    }
                    if (tempRecorderListener.gestureDoneProperty().get()) {
                        Platform.runLater(() -> {
                            resultLabel.textProperty().set("Successfully recorded " + tempChar + "!");
                            TextHelper.textFadeOut(2500, resultLabel);
                        });
                    }
                    Thread.sleep(2000);
                    controller.removeListener(tempRecorderListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // return once all gestures have been recorded
            app.swapScene("Menu");
        });
    }
    
    /**
     * Renders the GUI.
     * 
     * @param app The application menu.
     * @param controller The associated Leap Motion controller.
     */
    public void init(Menu app, Controller controller) {
        if (!alreadyActivated) {
            createNodes();
            alignNodes();
            alreadyActivated = true;
        }
        app.get2D().getChildren().addAll(titleLabel, resultLabel, gestureImgRect);
        
        System.out.println("Recorder active");
        beginRecording(app, controller);
    }
}
