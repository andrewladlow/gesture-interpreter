package gestureinterpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * Calls the launch method for the application's main menu class. 
 */
public class Driver {
	
	/**
	 * Starts the application.
	 * @param args None required. 
	 */
    public static void main(String[] args) {
    	List<String> test = new ArrayList<>();
    	test.add("hello");
    	Menu.launch(Menu.class, args);
    }
}