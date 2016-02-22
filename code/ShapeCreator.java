package gestureinterpreter;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;

/**
 * Helper class used to create various JavaFX shapes. 
 */
public class ShapeCreator {
	/**
	 * Creates a sphere shape with the provided parameters. 
	 * @param radius The radius of the sphere.
	 * @param diffuse The diffuse colour of the sphere.
	 * @param specular The specular colour of the sphere.
	 * @return The created sphere object. 
	 */
	public static Sphere createSphere(double radius, Color diffuse, Color specular) {		
		Sphere sphere = new Sphere(radius);
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(diffuse);
		mat.setSpecularColor(specular);
		sphere.setMaterial(mat);
		
		return sphere;
	}
	
	/**
	 * Creates a cylinder shape with the provided parameters. 
	 * @param radius The radius of the cylinder.
	 * @param diffuse The diffuse colour of the cylinder.
	 * @param specular The specular colour of the cylinder.
	 * @return The created cylinder object. 
	 */
	public static Cylinder createCylinder(double radius, Color diffuse, Color specular) {	
		Cylinder cylinder = new Cylinder();		
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(diffuse);
		mat.setSpecularColor(specular);
		cylinder.setMaterial(mat);
		cylinder.setRadius(radius);

		return cylinder;
	}
	
	/**
	 * Creates a box shape with the provided parameters. 
	 * @param width The width of the box. 
	 * @param height The height of the box.
	 * @param depth The depth of the box. 
	 * @param diffuse The diffuse colour of the box.
	 * @param specular The specular colour of the box.
	 * @return The created box object. 
	 */
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
	
	/**
	 * Changes the colour of a given shape. 
	 * @param shape The shape whose colour is being modified. 
	 * @param diffuse The new diffuse colour of the shape.
	 * @param specular The new specular colour of the shape.
	 */
	public static <T extends Shape3D> void changeColour(T shape, Color diffuse, Color specular) {
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(diffuse);
		mat.setSpecularColor(specular);
		shape.setMaterial(mat);
	}
}
