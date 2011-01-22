package ex3.render.raytrace;

import java.util.Map;

import math.Ray;
import math.Vec;

/**
 * Represents the scene's camera.
 * 
 */
public class Camera {

	// canvas related data
	protected double picRatio;
	protected double picWidth;
	protected double picHeight;
	protected Vec eye;// point
	protected Vec direction;// vector of the camera
	protected Vec lookAt;// point
	protected Vec upDirection;// vector - orthogonal to the direction
	protected double screenDistance; // distance to the screen
	protected int screenWidth; // screen width
	protected Vec rightOrientation; // to get the right perspective
	
	/**
	 * construct an empty camera this camera will need to be edited from the XML
	 * file
	 */
	public Camera(int width, int height) {
		this.picRatio = width / (double) height;
		this.picHeight = height;
		this.picWidth = width;
		// area related data
		this.screenDistance = 1;
		this.screenWidth = 2;
	}

	/**
	 * gets the parameters of the camera object from the given XML file
	 * 
	 * @param attributes
	 *            - list of attributes of all the objects
	 */
	public void init(Map<String, String> attributes) {
		if (attributes.containsKey("eye")) {
			eye = new Vec(attributes.get("eye"));
		}
		if (attributes.containsKey("direction")) {
			direction = new Vec(attributes.get("direction"));
		}
		if (attributes.containsKey("look-at")) {
			lookAt = new Vec(attributes.get("look-at"));
			direction = Vec.sub(lookAt, eye);
		}
		if (attributes.containsKey("up-direction")) {
			upDirection = new Vec(attributes.get("up-direction"));
		}
		if (attributes.containsKey("screen-dist")) {
			screenDistance = Double.parseDouble(attributes.get("screen-dist"));
		}
		if (attributes.containsKey("screen-width")) {
			screenWidth = Integer.parseInt(attributes.get("screen-width"));
		}
		// sets and normalize right orientation vector
		rightOrientation = Vec.crossProd(direction, upDirection);
		upDirection = Vec.crossProd(rightOrientation, direction);
		upDirection.normalize();
		direction.normalize();
		rightOrientation.normalize();
	} 

	/**
	 * Transforms image xy coordinates to view pane xyz coordinates. Returns the
	 * ray that goes through it.
	 * 
	 * @param x
	 * @param y
	 * @return a new ray
	 */
	public Ray constructRayThroughPixel(double x, double y) {
		Vec result = Vec.scale(screenDistance, direction);
		// setting the x and y coordination
		double actualX, actualY, factor = (double) this.screenWidth / this.picWidth;
		actualY = (y - (picHeight / 2.0)) * factor;
		actualX = (x - (picWidth / 2.0)) * factor;
		// Calculate a point on the screen
		Vec arbitarPoint = new Vec(result);
		arbitarPoint.add(Vec.scale((actualY), upDirection));
		arbitarPoint.add(Vec.scale((actualX), rightOrientation));
		// Normalize this vector so we get only the direction
		arbitarPoint.normalize();
		return new Ray(new Vec(eye), new Vec(arbitarPoint));
	} 
}