package gestureinterpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Class used to test the accuracy of recognition, as well as the average time
 * taken to complete.
 */
public class Tester {
    private ArrayList<Gesture> storedGestures;
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
            System.out.println("TEST 3: " + curGesture.getType());
            // System.out.println(curGesture.getPointArray().get(0).getX());
            // ArrayList<Point> test = curGesture.getPointArray();
            // test.remove(0);
            // test.remove(3);
            System.out.println("--------------------------------");
            System.out.println("Testing gesture: " + curGesture.getName());
            long time1 = System.nanoTime();
            RecognizerResults recResult = pdRec.Recognize(curGesture, storedGestures);
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
            // System.out.println(filePath.getAbsolutePath());
            for (File file : files) {
                // System.out.println(file.getName());
                // folders are explicitly called to allow for ease of inclusion
                // or exclusion of folders
                if (file.getName().startsWith("gestureSet1")
                                || file.getName().startsWith("gestureSet2")
                                || file.getName().startsWith("gestureSet3")
                                || file.getName().startsWith("gestureSet4")
                                || file.getName().startsWith("gestureSet5")
                                || file.getName().startsWith("gestureSet6")
                                || file.getName().startsWith("gestureSet7")
                                || file.getName().startsWith("gestureSet8")
                                || file.getName().startsWith("gestureSet9")) {
                    // ) {
                    loadFiles(file);
                } else if (file.getParentFile().getName().startsWith("gestureSet")) {
                    Gesture storedGesture = loadGesture(file);
                    ArrayList<Point> test = storedGesture.getPointArray();
                    // test.remove(0);
                    // test.remove(3);
                    storedGesture.setPointArray(test);
                    storedGesture.setPointArray(PDollarRecognizer.Resample(storedGesture.getPointArray(), PDollarRecognizer.mNumPoints));
                    storedGesture.setPointArray(PDollarRecognizer.Scale(storedGesture.getPointArray()));
                    storedGesture.setPointArray(PDollarRecognizer.TranslateTo(storedGesture.getPointArray(), new Point(0.0, 0.0, 0.0)));

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
     * Starts the testing procedure.
     */
    public static void main(String[] args) {
        Tester t = new Tester();
        t.test();
    }

}
