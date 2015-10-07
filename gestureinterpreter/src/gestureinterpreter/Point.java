package gestureinterpreter;

import java.io.Serializable;

public class Point implements Serializable {
   public double x;
   public double y;
   public double z;
   public int stroke;
         
   public Point(double xIn, double yIn, double zIn, int strokeIn) {
        x = xIn;
        y = yIn;
        z = zIn;
        stroke = strokeIn; // stroke ID to which this point belongs (1,2,...)
   }
           
   public Point(double xIn, double yIn, double zIn) {
        x = xIn;
        y = yIn;
        z = zIn;
        stroke = 0;
        //stroke = strokeIn; // stroke ID to which this point belongs (1,2,...)
   }
}