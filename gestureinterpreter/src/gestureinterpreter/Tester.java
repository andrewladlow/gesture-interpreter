package gestureinterpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class Tester {
	private List<Gesture> storedGestures;
	private PDollarRecognizer pdRec;
	
	public void test() {
    	storedGestures = new ArrayList<Gesture>();
		loadFiles(new File("."));
	}
	
	// load files recursively to account for each gesture sample set in different folders
	public void loadFiles(File filePath) {
    	try {
    	   	File[] files = filePath.listFiles();
			for (File file : files) {
				//if (file.isDirectory() && !file.getName().equals("gestures")) {
				if (file.isDirectory()) {
					System.out.println(file.getName());
					//loadFiles(file);
				}
/*				else {
					Gesture storedGesture = loadGesture(file);
			
					storedGesture.setPointArray(PDollarRecognizer.Resample(storedGesture.getPointArray(), PDollarRecognizer.mNumPoints));
					storedGesture.setPointArray(PDollarRecognizer.Scale(storedGesture.getPointArray()));
					storedGesture.setPointArray(PDollarRecognizer.TranslateTo(storedGesture.getPointArray(), new Point(0.0,0.0,0.0)));
					
					storedGestures.add(storedGesture);
				}*/
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
