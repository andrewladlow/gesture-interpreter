package gestureinterpreter;

/**
 * Calls the launch method for the application's main menu class. 
 */
public class Driver {
	
	/**
	 * Starts the application.
	 * @param args None required. 
	 */
    public static void main(String[] args) {
    	Menu.launch(Menu.class, args);
    }
}