package gestureinterpreter;

/**
 * Class containing a matching gesture's
 * parameters.  
 */
public class RecognizerResults {
	private String name;
	private double score;

	/**
	 * Creates a new result.
	 * @param name The name of the gesture.
	 * @param score The distance value of the gesture match.
	 */
	public RecognizerResults(String name, double score) { 
		this.name = name; 
		this.score = score; 
	}
	
	/**
	 * Returns the gesture name.
	 * @return Gesture name. 
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the gesture's score.
	 * @return Gesture score. 
	 */
	public double getScore() {
		return score;
	}
}
