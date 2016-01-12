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
import java.util.Arrays;
import java.util.Collections;
import java.lang.Double;

public class PDollarRecognizer {

	static int mNumPoints = 25;
	static Point mPointOrig = new Point(0.0,0.0,0.0,0);
	static ArrayList<PointCloud> mPntClouds = new ArrayList<PointCloud>();

    public PDollarRecognizer() { 
    	initializePointCloudTable(); 
    }

    public RecognizerResults Recognize(Gesture currentGesture, ArrayList<Gesture> storedGestures) {
	//PointCloud foundPointCloud = null;
	Gesture foundGesture = null;
			
            currentGesture.setPointArray(Resample(currentGesture.getPointArray(), mNumPoints));
            currentGesture.setPointArray(Scale(currentGesture.getPointArray()));
            currentGesture.setPointArray(TranslateTo(currentGesture.getPointArray(), mPointOrig));

            double score = Double.POSITIVE_INFINITY;
            
            System.out.println("\nPossible matches: ");

            // for each point-cloud template
            for (Gesture storedGesture : storedGestures) {
           // for ( int i = 0; i < storedGestures.size(); i++ )
                    double distScore = GreedyCloudMatch(currentGesture.getPointArray(), storedGesture.getPointArray());
                    System.out.println("Gesture: " + storedGesture.getName() + "\ndistScore: " + distScore);
                    if (distScore < score) {
                            score = distScore; // best (least) distance
                            //foundPointCloud = mPntClouds.get(i); // point-cloud
                            foundGesture = storedGesture;
                           // Math.max(50, 51);
                    }
            }
            
            
            if (foundGesture == null) {
            	return new RecognizerResults("None", 0.0);
            } else {
            	return new RecognizerResults(foundGesture.getName(), score);
            	//double test = (Math.min(parseInt(100 * Math.max(nearest - 4.0) / -4.0, 0.0), 100)/100.0)
            	//return new RecognizerResults(foundGesture.getName(), Math.min(parseInt(100 * Math.max(score - 4.0) / -4.0, 0.0), 100)/100.0);
            	//return new RecognizerResults(foundGesture.getName(), Math.max((score - 3.0) / 3.0, 0.0));
            }
           
    }

	public int addGesture(String name, ArrayList<Point> points) {
		mPntClouds.add(new PointCloud(name, points, mNumPoints));
		int num = 0;
		for (int i = 0; i < mPntClouds.size(); i++) {
			if (mPntClouds.get(i).mName.equals(name)) {
				num++;
			}
		}
		return num;
	}

	public static double GreedyCloudMatch(ArrayList<Point> currentPoints, ArrayList<Point> storedPoints) {
		double e = 0.50;
		double step = Math.floor(Math.pow(currentPoints.size(), 1 - e));
		
		double min = Double.POSITIVE_INFINITY;
		for (int  i = 0; i < currentPoints.size(); i += step) {
			double d1 = CloudDistance(currentPoints, storedPoints, i);
			double d2 = CloudDistance(storedPoints, currentPoints, i);
			min = Math.min(min, Math.min(d1, d2)); // min3
		}
		return min;
	}

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
			for(int j = 0; j < matched.length; j++) {
				if(!matched[j]) {
					double d = EuclideanDistance(pts1.get(i), pts2.get(j));
					if(d < min) {
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

	public static ArrayList<Point> Resample(ArrayList<Point> points, int n) {
		double I = PathLength(points) / (n - 1); // interval length
		double D = 0.0;
		
		ArrayList<Point> newpoints = new ArrayList<Point>(); 
		newpoints.add(points.get(0));

		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).getID() == points.get(i-1).getID()) {
				double d = EuclideanDistance(points.get(i - 1), points.get(i));
				if ((D + d) >= I) {
					double qx = points.get(i - 1).getX() + ((I - D) / d) * (points.get(i).getX() - points.get(i - 1).getX());
					double qy = points.get(i - 1).getY() + ((I - D) / d) * (points.get(i).getY() - points.get(i - 1).getY());
					double qz = points.get(i - 1).getZ() + ((I - D) / d) * (points.get(i).getZ() - points.get(i - 1).getZ());
					Point q = new Point(qx, qy, qz, points.get(i).getID());
					newpoints.add(q); // append new point 'q'
					points.add(i, q); // insert 'q' at position i in points s.t. 'q' will be the next i
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

	public static ArrayList<Point> Scale(ArrayList<Point> points) {
		double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < points.size(); i++) {
			minX = Math.min(minX, points.get(i).getX());
			minY = Math.min(minY, points.get(i).getY());
			maxX = Math.max(maxX, points.get(i).getX());
			maxY = Math.max(maxY, points.get(i).getY());
			minZ = Math.min(minZ,  points.get(i).getZ());
			maxZ = Math.max(maxZ,  points.get(i).getZ());
		}

		double size = Math.max(maxX - minX, maxY - minY);
		ArrayList<Point> newpoints = new ArrayList<Point>();

		for (int i = 0; i < points.size(); i++) {
			double qx = (points.get(i).getX() - minX) / size;
			double qy = (points.get(i).getY() - minY) / size;
			double qz = (points.get(i).getZ() - minZ) / size;
			newpoints.add(new Point(qx, qy, qz, points.get(i).getID()));
		}
		return newpoints;
	}

	// translates points' centroid
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

	// average distance between corresponding points in two paths
	public static double PathDistance(ArrayList<Point> pts1, ArrayList<Point> pts2) {
		double d = 0.0;
		for (int i = 0; i < pts1.size(); i++) { // assumes pts1.size() == pts2.size()
			d += EuclideanDistance(pts1.get(i), pts2.get(i));
		}
		return d / pts1.size();
	}

	// length traversed by a point path
	public static double PathLength(ArrayList<Point> points)
	{
		double d = 0.0;
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).getID() == points.get(i-1).getID()) {
				d += EuclideanDistance(points.get(i - 1), points.get(i));
			}
		}
		return d;
	}

	// Euclidean distance between two points
	public static double EuclideanDistance(Point p1, Point p2) {
		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		double dz = p2.getZ() - p1.getZ();
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	//
	// PointCloud class: a point-cloud template
	//
	public class PointCloud {
		public ArrayList<Point> mPoints;
		public String mName;

		PointCloud(String name, ArrayList<Point> points, int numPoints) {

			mName = name;
			mPoints = points;

			mPoints = PDollarRecognizer.Resample(mPoints, numPoints);
			mPoints = PDollarRecognizer.Scale(mPoints);
			mPoints = PDollarRecognizer.TranslateTo(mPoints, mPointOrig);
		}
	}

	private void initializePointCloudTable() {
/*		mPntClouds.add(new PointCloud("T", new ArrayList<Point>(Arrays.asList(
			new Point(30,7,1),
			new Point(103,7,1),
			new Point(66,7,2),
			new Point(66,87,2))),
			mNumPoints)
		);*/
	}
}
