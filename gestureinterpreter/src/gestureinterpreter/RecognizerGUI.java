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

public class RecognizerGUI {
	private RecognizerListener recognizerListener;
	private ObjectProperty<RecognizerResults> gestureRecognition = new SimpleObjectProperty<RecognizerResults>();
	private Label titleLabel;
	private Label resultLabel;
	private Label curWordLabel;
	private Label scoreLabel;
	private Label timerLabel;
	private String curLetter;
	private int curScore;
	private boolean alreadyActivated = false;	
	private static RecognizerGUI instance;
	private ExecutorService executor;
	

	public ObjectProperty<RecognizerResults> gestureRecognitionProperty() {
		return gestureRecognition;
	}

	private RecognizerGUI() {
		executor = Executors.newCachedThreadPool();
	}

	public static RecognizerGUI getInstance() {
		if (instance == null) {
			instance = new RecognizerGUI();
		}
		return instance;
	}

	public void init(Menu app, Controller controller) {		
		if (!alreadyActivated) {
			recognizerListener = new RecognizerListener(this);
	
			titleLabel = new Label();
			titleLabel.textProperty().set("Gesture Recognizer");
			titleLabel.setFont(Font.font("Times New Roman", 24));
	
			resultLabel = new Label();
			// resultLabel.textProperty().bind(gestureRecognition);
			resultLabel.setFont(Font.font("Times New Roman", 24));
	
			Random rand = new Random();
			curLetter = Character.toString((char)(rand.nextInt(5) + 'A'));
			curWordLabel = new Label();
			curWordLabel.setText("Make a: " + curLetter);
			curWordLabel.setFont(Font.font("Times New Roman", 32));
			
			timerLabel = new Label();
			timerLabel.setText("Time left: 60s");
			timerLabel.setFont(Font.font("Times New Roman", 24));	
			
			scoreLabel = new Label();
			scoreLabel.setText("Score: " + curScore);
			scoreLabel.setFont(Font.font("Times New Roman", 24));
					
			StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
			StackPane.setMargin(titleLabel, new Insets(10,0,0,10));
			StackPane.setAlignment(curWordLabel, Pos.TOP_CENTER);
			StackPane.setMargin(curWordLabel, new Insets(10,0,0,0));
			StackPane.setAlignment(resultLabel, Pos.CENTER);
			resultLabel.setTranslateY(-200);
			StackPane.setAlignment(timerLabel, Pos.TOP_RIGHT);
			StackPane.setMargin(timerLabel, new Insets(10,10,0,0));
			StackPane.setAlignment(scoreLabel, Pos.TOP_RIGHT);
			StackPane.setMargin(scoreLabel, new Insets(10,10,0,0));
			scoreLabel.setTranslateY(30);
			
			gestureRecognitionProperty().addListener((gestureRecognition, oldVal, newVal) -> {
				resultLabel.textProperty().set("Closest match: " + newVal.getName() + "\nMatch score: " + newVal.getScore() + "%");
				TextHelper.textFadeOut(1500, resultLabel);
				if (newVal.getName().equals(curLetter)) {
					curScore += 10;
					scoreLabel.setText("Score: " + curScore);
					curLetter = Character.toString((char)(rand.nextInt(5) + 'A'));
					TextHelper.textFadeIn(1000, curWordLabel);
					curWordLabel.setText("Make a: " + curLetter);
				}
			});
		
			alreadyActivated = true;
		}
		
		controller.addListener(recognizerListener);
		app.get2D().getChildren().addAll(titleLabel, resultLabel, curWordLabel, scoreLabel, timerLabel);
		app.getLeapButtons().clear();
		
		executor.execute(() -> {
			for (int i = 6000; i >= 0; i--) {
				int time = i;
				Platform.runLater(() -> {
					timerLabel.textProperty().set("Time left: " + time + "s");
				});
				try {
					Thread.sleep(1000);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}		
			}
			controller.removeListener(recognizerListener);
			Platform.runLater(() -> {
				app.get2D().getChildren().removeAll(titleLabel, resultLabel, curWordLabel, scoreLabel, timerLabel);
				Label finalScoreLabel = new Label();
				finalScoreLabel.setText("Final score: " + curScore);
				finalScoreLabel.setFont(Font.font("Times New Roman", 40));
				StackPane.setAlignment(resultLabel, Pos.CENTER);
				app.get2D().getChildren().add(finalScoreLabel);
			});
			try {
				Thread.sleep(5000);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			app.swapScene("Menu");
		});
	}
}