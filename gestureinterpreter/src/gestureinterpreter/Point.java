package gestureinterpreter;

import java.io.Serializable;

public class Point implements Serializable {
   public double x;
   public double y;
   public double z;

   public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
   }
}