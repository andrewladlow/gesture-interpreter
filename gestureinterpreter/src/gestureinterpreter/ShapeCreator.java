package gestureinterpreter;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class ShapeCreator {
	public static Sphere createSphere(Group node, double radius, Color diffuse, Color specular) {
		
		Sphere sphere = new Sphere(radius);

		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(diffuse);
		mat.setSpecularColor(specular);
		sphere.setMaterial(mat);
		
		//node.getChildren().add(sphere);
		
		return sphere;
	}
	
	public static Cylinder createCylinder(Group node, double radius, double height, Color diffuse, Color specular) {
		
		Cylinder cylinder = new Cylinder(radius, height);
		
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(diffuse);
		mat.setSpecularColor(specular);
		cylinder.setMaterial(mat);
		
		node.getChildren().add(cylinder);
		
		return cylinder;
	}
	
	public static Cylinder createCylinder(double radius, Color diffuse, Color specular, Rotate joint) {
		
		Cylinder cylinder = new Cylinder();
		
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(diffuse);
		mat.setSpecularColor(specular);
		cylinder.setMaterial(mat);

		cylinder.setRadius(radius);
		cylinder.getTransforms().add(joint);

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
}
