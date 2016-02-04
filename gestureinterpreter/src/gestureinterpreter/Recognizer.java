package gestureinterpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Arrays;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

public class Recognizer {
	public void compareFrame (Frame curFrame) {
		// from https://developer.leapmotion.com/documentation/java/devguide/Leap_Serialization.html
		// deserialize frames from file
		try {
			
			Path inFilepath = Paths.get("gestures/frames.data");
			byte[] data = Files.readAllBytes(inFilepath);
			int c = 0;
			int f = 0;
			int nextBlockSize = 0;
			if(data.length > 4) nextBlockSize = (data[c++] & 0x000000ff) << 24 |
		                                      	(data[c++] & 0x000000ff) << 16 |
		                                      	(data[c++] & 0x000000ff) <<  8 |
		                                      	(data[c++] & 0x000000ff);
			while (c + nextBlockSize <= data.length) {
				byte[] frameData = Arrays.copyOfRange(data, c, c + nextBlockSize);
				c += nextBlockSize;
				Frame storedFrame = new Frame();
				storedFrame.deserialize(frameData);
				frameCompare(curFrame, storedFrame);
				if(data.length - c > 4)  nextBlockSize = (data[c++] & 0x000000ff) << 24 |
														 (data[c++] & 0x000000ff) << 16 |
														 (data[c++] & 0x000000ff) <<  8 |
														 (data[c++] & 0x000000ff);  
			}
		} catch (IOException e) {
		  System.out.println("Error reading file: " + e);  
		}
	}
	
	public void frameCompare (Frame curFrame, Frame storedFrame) {
		
		for (Hand curHand : curFrame.hands()) {
			System.out.println("test");
			for (Hand storedHand : storedFrame.hands()) {
				for (Finger curFinger : curHand.fingers()) {
					Vector curTip = curFinger.tipPosition();
					for (Finger storedFinger : storedHand.fingers()) {
						Vector storedTip = storedFinger.tipPosition();
						float result = curTip.dot(storedTip);
						System.out.println(result);
					}
				}	
			}
		}
	}
}
