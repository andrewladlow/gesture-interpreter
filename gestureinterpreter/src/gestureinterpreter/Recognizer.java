package gestureinterpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.leapmotion.leap.Frame;

public class Recognizer {
	public void loadFrames() {
		// from https://developer.leapmotion.com/documentation/java/devguide/Leap_Serialization.html
		// deserializes frames from file
		try {
			Path inFilepath = Paths.get("gestures/d.data");
			byte[] data = Files.readAllBytes(inFilepath);
			int c = 0;
			int nextBlockSize = 0;
			if(data.length > 4) nextBlockSize = (data[c++] & 0x000000ff) << 24 |
		                                      	(data[c++] & 0x000000ff) << 16 |
		                                      	(data[c++] & 0x000000ff) <<  8 |
		                                      	(data[c++] & 0x000000ff);
			
			while (c + nextBlockSize <= data.length) {
				byte[] frameData = Arrays.copyOfRange(data, c, c + nextBlockSize);
				c += nextBlockSize;
				Frame newFrame = new Frame();
				newFrame.deserialize(frameData);
				if(data.length - c > 4)  nextBlockSize = (data[c++] & 0x000000ff) << 24 |
														 (data[c++] & 0x000000ff) << 16 |
														 (data[c++] & 0x000000ff) <<  8 |
														 (data[c++] & 0x000000ff);  
			}
		} catch (IOException e) {
		  System.out.println("Error reading file: " + e);  
		}
	}
	
	public void compareTo (Frame currentFrame) {
		
	}
}
