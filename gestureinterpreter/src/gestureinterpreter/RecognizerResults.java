package gestureinterpreter;

/**
 * Class containing a matching gesture's parameters.
 */
public class RecognizerResults {
    private String name;
    private double score;

    /**
     * Creates a new result.
     * 
     * @param name The name of the gesture.
     * @param score The distance value of the gesture match.
     */
    public RecognizerResults(String name, double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }
}
