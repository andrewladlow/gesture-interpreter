package gestureinterpreter;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;

public class ShapeCreator {
	public static Sphere createSphere(double radius, Color diffuse, Color specular) {		
		Sphere sphere = new Sphere(radius);
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(diffuse);
		mat.setSpecularColor(specular);
		sphere.setMaterial(mat);
		
		return sphere;
	}
	
	public static Cylinder createCylinder(double radius, Color diffuse, Color specular) {	
		Cylinder cylinder = new Cylinder();		
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(diffuse);
		mat.setSpecularColor(specular);
		cylinder.setMaterial(mat);
		cylinder.setRadius(radius);

		return cylinder;
	}
	
	public static Box createBox(double width, double height, double depth, Color diffuse, Color specular)  {	
		Box box = new Box();		
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(diffuse);
		mat.setSpecularColor(specular);
		box.setMaterial(mat);		
		box.setWidth(width);
		box.setHeight(height);
		box.setDepth(depth);
		
		return box;
	}
	
	public static <T extends Shape3D> void changeColour(T shape, Color diffuse, Color specular) {
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(diffuse);
		mat.setSpecularColor(specular);
		shape.setMaterial(mat);
	}
}
