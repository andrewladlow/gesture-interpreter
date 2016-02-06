package gestureinterpreter;

import com.leapmotion.leap.Controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;

public class RecognizerGUI {

	private Menu app;
	private Controller controller;
	private RecognizerListener recognizerListener;
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
			
			this.app = app;
			this.controller = controller;
			recognizerListener = new RecognizerListener(this);
	
			titleLabel = new Label();
			titleLabel.textProperty().set("Gesture Recognizer");
			titleLabel.setFont(Font.font("Times New Roman", 24));
	
			resultLabel = new Label();
			// resultLabel.textProperty().bind(gestureRecognition);
			resultLabel.setFont(Font.font("Times New Roman", 24));
	
			curWordLabel = new Label();
			curWordLabel.setText("Hello World");
			curWordLabel.setFont(Font.font("Times New Roman", 24));
			
			scoreLabel = new Label();
			scoreLabel.setFont(Font.font("Times New Roman", 24));
	
			StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
			StackPane.setAlignment(resultLabel, Pos.TOP_CENTER);
			StackPane.setAlignment(curWordLabel, Pos.CENTER);
			StackPane.setAlignment(scoreLabel, Pos.TOP_RIGHT);
			
			this.gestureRecognitionProperty().addListener((gestureRecognition, oldVal, newVal) -> {
				resultLabel.textProperty().set("Closest match: " + newVal.getName() + "\nMatch score: " + newVal.getScore());
				TextHelper.textFadeOut(1500, resultLabel);
			});

			backButton = new LeapButton(Menu.APPWIDTH, Menu.APPHEIGHT, Color.RED, Color.GOLDENROD, "Return");
			backButton.setPosition(0, -50, 110);
			backButton.setRotation(5, Rotate.X_AXIS);
			
			alreadyActivated = true;
		}
		
		controller.addListener(recognizerListener);
		app.get2D().getChildren().addAll(titleLabel, resultLabel, curWordLabel);
		app.get3D().getChildren().addAll(backButton);
		//controller.
	}

	public void goBack() {
		//System.out.println("Going back!");
		controller.removeListener(recognizerListener);
		app.swapScene("Menu");

	}
}