package gestureinterpreter;

import java.io.Serializable;

public class Point implements Serializable {
   private double x;
   private double y;
   private double z;
   public int ID = 0;

   public Point (double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
   }
   
   public double getX() {
	   return x;
   }
   
   public double getY() {
	   return y;
   }
   
   public double getZ() {
	   return z;
   }
   
   public int getID() {
	   return ID;
   }
}
