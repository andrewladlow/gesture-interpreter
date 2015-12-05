package gestureinterpreter;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GestureTimer extends Thread {
	private IntegerProperty timerCount = new SimpleIntegerProperty();
	private Thread thread;
	private int timeVal;
	
	public void run() {
		try {
			System.out.println("Timer started");
			for (int i = timeVal; i > 0; i--) {	
				Platform.runLater(new Runnable() {
					public void run() {
						timerCount.set(timeVal--);
						System.out.println(timeVal);
					}
				});
				Thread.sleep(1000);				
			}
			System.out.println("Timer stopped");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start(int timeVal) {
		thread = new Thread(this);
		timerCount.set(timeVal);
		this.timeVal = timeVal;
		thread.start();
	}
	
	public IntegerProperty timerCountProperty() {
		return timerCount;
	}
}
