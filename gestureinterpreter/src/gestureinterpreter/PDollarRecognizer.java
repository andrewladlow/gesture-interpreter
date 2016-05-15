/**
 * The $P Point-Cloud Recognizer
 *
 * 	Radu-Daniel Vatavu, Ph.D.
 *	University Stefan cel Mare of Suceava
 *	Suceava 720229, Romania
 *	vatavu@eed.usv.ro
 *
 *	Lisa Anthony, Ph.D.
 *      UMBC
 *      Information Systems Department
 *      1000 Hilltop Circle
 *      Baltimore, MD 21250
 *      lanthony@umbc.edu
 *
 *	Jacob O. Wobbrock, Ph.D.
 * 	The Information School
 *	University of Washington
 *	Seattle, WA 98195-2840
 *	wobbrock@uw.edu
 *
 * The academic publication for the $P recognizer, and what should be 
 * used to cite it, is:
 *
 *	Vatavu, R.-D., Anthony, L. and Wobbrock, J.O. (2012).  
 *	  Gestures as point clouds: A $P recognizer for user interface 
 *	  prototypes. Proceedings of the ACM Int'l Conference on  
 *	  Multimodal Interfaces (ICMI '12). Santa Monica, California  
 *	  (October 22-26, 2012). New York: ACM Press, pp. 273-280.
 *
 * This software is distributed under the "New BSD License" agreement:
 *
 * Copyright (c) 2012, Radu-Daniel Vatavu, Lisa Anthony, and 
 * Jacob O. Wobbrock. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the names of the University Stefan cel Mare of Suceava, 
 *	University of Washington, nor UMBC, nor the names of its contributors 
 *	may be used to endorse or promote products derived from this software 
 *	without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Radu-Daniel Vatavu OR Lisa Anthony
 * OR Jacob O. Wobbrock BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
**/
package gestureinterpreter;

import java.util.ArrayList;
import java.util.List;
import java.lang.Double;

/**
 * Class handling recognition of gestures.
 */
public class PDollarRecognizer {

    static int mNumPoints = 32;
    static Point mPointOrig = new Point(0.0, 0.0, 0.0, 0);

    /**
     * Calculates the closest match between a given gesture and an array of
     * stored gestures.
     * 
     * @param currentGesture The testing gesture.
     * @param storedGestures An array of stored gestures.
     */
    public RecognizerResults Recognize(Gesture currentGesture, List<Gesture> storedGestures) {
        Gesture foundGesture = null;

        // Normalise gesture before it is compared
        currentGesture.setPointArray(Resample(currentGesture.getPointArray(), mNumPoints));
        currentGesture.setPointArray(Scale(currentGesture.getPointArray()));
        currentGesture.setPointArray(TranslateTo(currentGesture.getPointArray(), mPointOrig));

        double score = Double.POSITIVE_INFINITY;

        // For each point-cloud template
        for (Gesture storedGesture : storedGestures) {

            // System.out.println("TEST 1: " + storedGesture.getName());
            // System.out.println("TEST 2: " + storedGesture.getType());

            // Skip if gestures are not of same type ("pose" and "gesture")
            if (!currentGesture.getType().equals(storedGesture.getType())) {
                // System.out.println(storedGesture.getType() + ": " + storedGesture.getName());
                // System.out.println("Type does not match" + "(" + storedGesture.getType() + " : " + currentGesture.getType() + ")");
                continue;
            }

            double distScore = GreedyCloudMatch(currentGesture.getPointArray(), storedGesture.getPointArray());
            // System.out.println("Gesture: " + storedGesture.getName() + "\ndistScore: " + distScore);
            if (distScore < score) {
                score = distScore; // best (least) distance
                foundGesture = storedGesture;
            }
        }
        // Show raw score value in console
        // System.out.println("\nClosest match: " + foundGesture.getName() + "\nScore: " + score);

        // Translates score to value in range 0-100 (percentage of similarity)
        double finalScore = Math.max(Math.min(Math.round(100 - (100 * (score - 4.0) / 3.5)), 100), 0);
        // double finalScore = score;

        return new RecognizerResults(foundGesture.getName(), finalScore);
    }

    /**
     * Returns the overall Euclidean distance between two arrays of points.
     * 
     * @param currentPoints The first point array.
     * @param storedPoints The second point array.
     */
    public static double GreedyCloudMatch(ArrayList<Point> currentPoints, ArrayList<Point> storedPoints) {
        double e = 0.50;
        double step = Math.floor(Math.pow(currentPoints.size(), 1 - e));

        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < currentPoints.size(); i += step) {
            double d1 = CloudDistance(currentPoints, storedPoints, i);
            double d2 = CloudDistance(storedPoints, currentPoints, i);
            min = Math.min(min, Math.min(d1, d2)); // min3
        }
        return min;
    }

    /**
     * Returns the distance between two arrays of points, starting at a specific
     * point.
     * 
     * @param pts1 The first array of points.
     * @param pts2 The second array of points.
     * @param start The starting point.
     */
    public static double CloudDistance(ArrayList<Point> pts1, ArrayList<Point> pts2, int start) {
        // pts1.size() == pts2.size()
        boolean[] matched = new boolean[pts1.size()];
        for (int k = 0; k < pts1.size(); k++) {
            matched[k] = false;
        }
        double sum = 0;
        int i = start;
        do {
            int index = -1;
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < matched.length; j++) {
                if (!matched[j]) {
                    double d = EuclideanDistance(pts1.get(i), pts2.get(j));
                    if (d < min) {
                        min = d;
                        index = j;
                    }
                }
            }
            matched[index] = true;
            double weight = 1 - ((i - start + pts1.size()) % pts1.size()) / pts1.size();
            sum += weight * min;
            i = (i + 1) % pts1.size();
        } while (i != start);
        return sum;
    }

    /**
     * Resample an array of points to a given amount.
     * 
     * @param points The array of points to resample.
     * @param n The amount to resample to.
     */
    public static ArrayList<Point> Resample(ArrayList<Point> points, int n) {
        double I = PathLength(points) / (n - 1); // interval length
        double D = 0.0;

        ArrayList<Point> newpoints = new ArrayList<Point>();
        newpoints.add(points.get(0));

        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).getID() == points.get(i - 1).getID()) {
                double d = EuclideanDistance(points.get(i - 1), points.get(i));
                if ((D + d) >= I) {
                    double qx = points.get(i - 1).getX() + ((I - D) / d) * (points.get(i).getX() - points.get(i - 1).getX());
                    double qy = points.get(i - 1).getY() + ((I - D) / d) * (points.get(i).getY() - points.get(i - 1).getY());
                    double qz = points.get(i - 1).getZ() + ((I - D) / d) * (points.get(i).getZ() - points.get(i - 1).getZ());
                    Point q = new Point(qx, qy, qz, points.get(i).getID());
                    newpoints.add(q); // append new point 'q'
                    points.add(i, q); // insert 'q' at position i in points s.t.
                                      // 'q' will be the next i
                    D = 0.0;
                } else {
                    D += d;
                }
            }
        }

        // sometimes we fall a rounding-error short of
        // adding the last point, so add it if so
        if (newpoints.size() == n - 1) {
            newpoints.add(new Point(points.get(points.size() - 1).getX(), points.get(points.size() - 1).getY(), points.get(points.size() - 1).getZ(), points.get(points.size() - 1).getID()));
        }
        return newpoints;
    }

    /**
     * Normalises points in the range 0-1.
     * 
     * @param points The array of points to normalise.
     */
    public static ArrayList<Point> Scale(ArrayList<Point> points) {
        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < points.size(); i++) {
            minX = Math.min(minX, points.get(i).getX());
            minY = Math.min(minY, points.get(i).getY());
            minZ = Math.min(minZ, points.get(i).getZ());
            maxX = Math.max(maxX, points.get(i).getX());
            maxY = Math.max(maxY, points.get(i).getY());
            maxZ = Math.max(maxZ, points.get(i).getZ());
        }

        double size = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
        ArrayList<Point> newpoints = new ArrayList<Point>();

        for (int i = 0; i < points.size(); i++) {
            double qx = (points.get(i).getX() - minX) / size;
            double qy = (points.get(i).getY() - minY) / size;
            double qz = (points.get(i).getZ() - minZ) / size;
            newpoints.add(new Point(qx, qy, qz, points.get(i).getID()));
        }
        return newpoints;
    }

    // translates points to (0,0,0)
    /**
     * Translates points to a given point.
     * 
     * @param points Array of points to translate.
     * @param pt The point to translate to.
     */
    public static ArrayList<Point> TranslateTo(ArrayList<Point> points, Point pt) {
        Point c = Centroid(points);
        ArrayList<Point> newpoints = new ArrayList<Point>();
        for (int i = 0; i < points.size(); i++) {
            double qx = points.get(i).getX() + pt.getX() - c.getX();
            double qy = points.get(i).getY() + pt.getY() - c.getY();
            double qz = points.get(i).getZ() + pt.getZ() - c.getZ();
            newpoints.add(new Point(qx, qy, qz, points.get(i).getID()));
        }
        return newpoints;
    }

    /**
     * Returns the centre point of an array of points.
     * 
     * @param points The array of points.
     */
    public static Point Centroid(ArrayList<Point> points) {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        for (int i = 0; i < points.size(); i++) {
            x += points.get(i).getX();
            y += points.get(i).getY();
            z += points.get(i).getZ();
        }
        x /= points.size();
        y /= points.size();
        z /= points.size();
        return new Point(x, y, z, 0);
    }

    /**
     * Returns the length traversed by a point path.
     * 
     * @param points The path of points.
     */
    public static double PathLength(ArrayList<Point> points) {
        double d = 0.0;
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).getID() == points.get(i - 1).getID()) {
                d += EuclideanDistance(points.get(i - 1), points.get(i));
            }
        }
        return d;
    }

    /**
     * Returns the Euclidean distance between two points.
     * 
     * @param p1 The first point.
     * @param p2 The second point.
     */
    public static double EuclideanDistance(Point p1, Point p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double dz = p2.getZ() - p1.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
