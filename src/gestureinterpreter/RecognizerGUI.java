package gestureinterpreter;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.leapmotion.leap.Controller;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * Class handling the GUI of the recognizing section of the application.
 */
public class RecognizerGUI {
    private final int TIME_ALLOWED = 60;
    
    private static RecognizerGUI instance;
    
    private RecognizerListener recognizerListener;
    private ObjectProperty<RecognizerResults> gestureRecognition = new SimpleObjectProperty<RecognizerResults>();
    private Label titleLabel;
    private Label resultLabel;
    private Label curWordLabel;
    private Label scoreLabel;
    private Label timerLabel;
    private Label finalScoreLabel;
    private String curLetter;
    private int curScore;
    private boolean alreadyActivated;
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    private RecognizerGUI() {
    }

    /**
     * Singleton only allows a single instance of this class to be created.
     */
    public static RecognizerGUI getInstance() {
        if (instance == null) {
            instance = new RecognizerGUI();
        }
        return instance;
    }

    public ObjectProperty<RecognizerResults> gestureRecognitionProperty() {
        return gestureRecognition;
    }

    /**
     * Creates all nodes for the recognition screen. 
     */
    private void createNodes() {
        titleLabel = new Label();
        titleLabel.textProperty().set("Gesture Recognizer");
        titleLabel.setFont(Font.font("Times New Roman", 24));

        resultLabel = new Label();
        // resultLabel.textProperty().bind(gestureRecognition);
        resultLabel.setFont(Font.font("Times New Roman", 24));

        Random rand = new Random();
        curLetter = Character.toString((char) (rand.nextInt(26) + 'A'));
        curWordLabel = new Label();
        curWordLabel.setText("Make the gesture for: " + curLetter);
        curWordLabel.setFont(Font.font("Times New Roman", 32));

        timerLabel = new Label();
        timerLabel.setText("Time left: " + TIME_ALLOWED + "s ");
        timerLabel.setFont(Font.font("Times New Roman", 24));

        scoreLabel = new Label();
        scoreLabel.setText("Score: " + curScore);
        scoreLabel.setFont(Font.font("Times New Roman", 24));

        finalScoreLabel = new Label();
    }

    /**
     * Aligns all nodes on the recognizer page. 
     */
    private void alignNodes() {
        StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
        StackPane.setMargin(titleLabel, new Insets(10, 0, 0, 10));
        StackPane.setAlignment(curWordLabel, Pos.TOP_CENTER);
        StackPane.setMargin(curWordLabel, new Insets(10, 0, 0, 0));
        StackPane.setAlignment(resultLabel, Pos.CENTER);
        resultLabel.setTranslateY(-200);
        StackPane.setAlignment(timerLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(timerLabel, new Insets(10, 10, 0, 0));
        StackPane.setAlignment(scoreLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(scoreLabel, new Insets(10, 10, 0, 0));
        scoreLabel.setTranslateY(30);
    }
    
    /**
     * Adds an event listener for the gestureRecognitionProperty.
     * Triggered by an instance of RecognizerListener. Represents the
     * confirmation of a performed gesture. 
     */
    private void createListener() {
        // event listener triggered when a gesture match is completed
        gestureRecognitionProperty().addListener((gestureRecognition, oldVal, newVal) -> {
            resultLabel.textProperty().set("Closest match: " + newVal.getName() + "\nMatch score: " + newVal.getScore() + "%");
            TextHelper.textFadeOut(2000, resultLabel);
            if (newVal.getName().equals(curLetter)) {
                curScore += 10;
                scoreLabel.setText("Score: " + curScore);
                Random rand = new Random();
                curLetter = Character.toString((char) (rand.nextInt(26) + 'A'));
                TextHelper.textFadeIn(1000, curWordLabel);
                curWordLabel.setText("Make the gesture for: " + curLetter);
            }
        });
    }

    /**
     * Starts the recognition timer, resulting in a 'final score' screen
     * when the timer has reached 0. 
     * @param app The application menu. 
     * @param controller The Leap Motion controller. 
     */
    private void beginRecognition(Menu app, Controller controller) {
        // begin on new thread so as to not block rendering of hand movement
        executor.execute(() -> {
            for (int i = TIME_ALLOWED; i >= 0; i--) {
                int time = i;
                Platform.runLater(() -> {
                    timerLabel.textProperty().set("Time left: " + time + "s");
                });
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // when timer expires, show final score screen
            controller.removeListener(recognizerListener);
            Platform.runLater(() -> {
                app.get2D().getChildren().removeAll(titleLabel, resultLabel, curWordLabel, scoreLabel, timerLabel);
                finalScoreLabel.setText("Final score: " + curScore);
                finalScoreLabel.setFont(Font.font("Times New Roman", 40));
                StackPane.setAlignment(finalScoreLabel, Pos.CENTER);
                app.get2D().getChildren().add(finalScoreLabel);
            });
            // wait 5 seconds then return to menu
            try {
                Thread.sleep(5000);
                curScore = 0;
                scoreLabel.setText("Score: " + curScore);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                app.get2D().getChildren().remove(finalScoreLabel);
            });
            app.swapScene("Menu");
        });
    }

    /**
     * Renders the GUI.
     * 
     * @param controller The associated Leap Motion controller.
     * @param app The application menu.
     */
    public void init(Menu app, Controller controller) {
        if (!alreadyActivated) {
            createNodes();
            alignNodes();
            createListener();
            alreadyActivated = true;
        }
        app.get2D().getChildren().addAll(titleLabel, resultLabel, curWordLabel, scoreLabel, timerLabel);

        recognizerListener = new RecognizerListener(this);
        controller.addListener(recognizerListener);
        System.out.println("Recognizer active");
        beginRecognition(app, controller);
    }
}