package gestureinterpreter;

import com.leapmotion.leap.*;

public class Driver {
	public static void main(String[] args) {
		SampleListener listener = new SampleListener();
		Controller controller = new Controller();
		
		controller.addListener(listener);
		
		System.out.println("Press Enter to quit");
		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		controller.removeListener(listener);
	}
}

class SampleListener extends Listener {
	public void onConnect(Controller controller) {
		System.out.println("Connected");
	}
	
	public void onFrame(Controller controller) {
		System.out.println("Frame available");
	}
}