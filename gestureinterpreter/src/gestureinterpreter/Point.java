package gestureinterpreter;


public class Point {
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
        //stroke = strokeIn; // stroke ID to which this point belongs (1,2,...)
   }
}