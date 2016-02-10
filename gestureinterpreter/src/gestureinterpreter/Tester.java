package gestureinterpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Tester {
	private ArrayList<Gesture> storedGestures;
	
	public void test() {
    	storedGestures = new ArrayList<Gesture>();
		loadFiles(new File("."));
		
		PDollarRecognizer pdRec = new PDollarRecognizer();
				
		File testGestureFolder = new File("gestures");
		File[] testGestures = testGestureFolder.listFiles();
		Gesture curGesture;
		
        DecimalFormat df = new DecimalFormat("###.##");
		for (File testGesture : testGestures) {
			curGesture = loadGesture(testGesture);
			System.out.println("--------------------------------");
			System.out.println("Testing gesture: " + curGesture.getName());
        	long time1 = System.nanoTime();
			RecognizerResults recResult = pdRec.Recognize(curGesture, storedGestures);
            long time2 = System.nanoTime();
			System.out.println("Matching gesture: " + recResult.getName());
			System.out.println("Matching distance: " + recResult.getScore());
            System.out.println("Time taken: " + df.format(Math.round(time2 - time1) / 1e6) + " ms");
		}
	}
	
	// load files recursively to account for each gesture sample set in different folders
	public void loadFiles(File filePath) {
    	try {
    	   	File[] files = filePath.listFiles();
			for (File file : files) {
				//System.out.println(file.getName());
				if (file.getName().startsWith("gestureSet")) {
					loadFiles(file);
				}
				else if (file.getParentFile().getName().startsWith("gestureSet")) {
					Gesture storedGesture = loadGesture(file);
					storedGesture.setPointArray(PDollarRecognizer.Resample(storedGesture.getPointArray(), PDollarRecognizer.mNumPoints));
					storedGesture.setPointArray(PDollarRecognizer.Scale(storedGesture.getPointArray()));
					storedGesture.setPointArray(PDollarRecognizer.TranslateTo(storedGesture.getPointArray(), new Point(0.0,0.0,0.0)));
					
					storedGestures.add(storedGesture);
				}
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}

	public Gesture loadGesture(File fileName) {
		Gesture gesture = null;
		try {
			FileInputStream inStream = new FileInputStream(fileName);
			ObjectInputStream ObjInStream = new ObjectInputStream(inStream);
			gesture = (Gesture) ObjInStream.readObject();
			
			ObjInStream.close();
			inStream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return gesture;
	}
	
	public static void main(String[] args) {
		Tester t = new Tester();
		t.test();
	}

}
