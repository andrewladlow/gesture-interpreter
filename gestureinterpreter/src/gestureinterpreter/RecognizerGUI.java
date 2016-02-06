package gestureinterpreter;

import com.leapmotion.leap.Controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;

public class RecognizerGUI {
	static RecognizerListener recognizerListener;
	private ObjectProperty<RecognizerResults> gestureRecognition = new SimpleObjectProperty<RecognizerResults>();
	private Label titleLabel;
	private Label resultLabel;
	private Label curWordLabel;
	private Label scoreLabel;
	private boolean alreadyActivated = false;	
	private static RecognizerGUI instance;
	public LeapButton backButton;	

	public ObjectProperty<RecognizerResults> gestureRecognitionProperty() {
		return gestureRecognition;
	}

	private RecognizerGUI() {
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
	
			curWordLabel = new Label();
			curWordLabel.setText("ABCDE");
			curWordLabel.setFont(Font.font("Times New Roman", 32));
			
			scoreLabel = new Label();
			scoreLabel.setText("Score: 0");
			scoreLabel.setFont(Font.font("Times New Roman", 24));
	
			StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
			StackPane.setMargin(titleLabel, new Insets(10,0,0,10));
			StackPane.setAlignment(resultLabel, Pos.TOP_CENTER);
			StackPane.setMargin(resultLabel, new Insets(10,0,0,0));
			StackPane.setAlignment(curWordLabel, Pos.CENTER);
			curWordLabel.setTranslateY(-200);
			StackPane.setAlignment(scoreLabel, Pos.TOP_RIGHT);
			StackPane.setMargin(scoreLabel, new Insets(10,10,0,0));
			
			gestureRecognitionProperty().addListener((gestureRecognition, oldVal, newVal) -> {
				resultLabel.textProperty().set("Closest match: " + newVal.getName() + "\nMatch score: " + newVal.getScore());
				TextHelper.textFadeOut(1500, resultLabel);
			});

			backButton = new LeapButton(Menu.APPWIDTH, Menu.APPHEIGHT, Color.CRIMSON, Color.SILVER, "Return");
			backButton.setPosition(0, 0, 110);
			backButton.setRotation(5, Rotate.X_AXIS);
		
			alreadyActivated = true;
		}
		
		controller.addListener(recognizerListener);
		app.get2D().getChildren().addAll(titleLabel, resultLabel, curWordLabel, scoreLabel);
		app.get3D().getChildren().addAll(backButton);
		app.getLeapButtons().clear();
		app.getLeapButtons().add(backButton);
	}
}