package gestureinterpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to test the accuracy of recognition, as well as the average time
 * taken to complete.
 */
public class Tester {
    private List<Gesture> storedGestures;
    private int matchCount;
    private double totalTimeTaken;

    /**
     * Begins the test.
     */
    public void test() {
        storedGestures = new ArrayList<Gesture>();
        matchCount = 0;

        loadFiles(new File("."));

        PDollarRecognizer pdRec = new PDollarRecognizer();

        File testGestureFolder = new File("testGestures");
        File[] testGestures = testGestureFolder.listFiles();
        Gesture curGesture;

        DecimalFormat df = new DecimalFormat("###.##");

        for (File testGesture : testGestures) {
            curGesture = loadGesture(testGesture);
            System.out.println("--------------------------------");
            System.out.println("Testing gesture: " + curGesture.getName());
            long time1 = System.nanoTime();
            RecognizerResults recResult = pdRec.recognize(curGesture, storedGestures);
            long time2 = System.nanoTime();
            System.out.println("Matching gesture: " + recResult.getName());
            if (curGesture.getName().equals(recResult.getName())) {
                matchCount++;
            }
            System.out.println("Matching distance: " + recResult.getScore());
            String timeTaken = df.format(Math.round(time2 - time1) / 1e6);
            System.out.println("Time taken: " + timeTaken + " ms");
            totalTimeTaken += Double.parseDouble(timeTaken);
        }

        System.out.println("\nTotal match count: " + matchCount);
        System.out.println("Average time taken: " + df.format(totalTimeTaken / 26) + "ms");

    }

    /**
     * Loads gesture files recursively to account for each gesture sample set in
     * different folders
     * 
     * @param filePath The path to load.
     */
    public void loadFiles(File filePath) {
        try {
            File[] files = filePath.listFiles();
            for (File file : files) {
                // folders are explicitly called to allow for ease of inclusion / exclusion of folders
                if (file.getName().startsWith("gestureSet")) {
                    loadFiles(file);
                } else if (file.getParentFile().getName().startsWith("gestureSet")) {
                    Gesture storedGesture = loadGesture(file);
                    ArrayList<Point> test = storedGesture.getPointArray();
                    storedGesture.setPointArray(test);
                    storedGesture.setPointArray(PDollarRecognizer.resample(storedGesture.getPointArray(), PDollarRecognizer.getNumPoints()));
                    storedGesture.setPointArray(PDollarRecognizer.scale(storedGesture.getPointArray()));
                    storedGesture.setPointArray(PDollarRecognizer.translateTo(storedGesture.getPointArray(), new Point(0.0, 0.0, 0.0)));

                    storedGestures.add(storedGesture);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads gesture from a given file name.
     * 
     * @param fileName The file containing the gesture.
     */
    public Gesture loadGesture(File fileName) {
        Gesture gesture = null;
        try {
            FileInputStream inStream = new FileInputStream(fileName);
            ObjectInputStream ObjInStream = new ObjectInputStream(inStream);
            gesture = (Gesture) ObjInStream.readObject();

            ObjInStream.close();
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gesture;
    }

    /**
     * Calls the testing procedure.
     */
    public static void main(String[] args) {
        Tester t = new Tester();
        t.test();
    }

}
