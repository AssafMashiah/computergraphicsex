package ex3.render.raytrace;

import java.util.Map;

import math.Ray;
import math.Vec;

/**
 * Simple Sphere object
 * 
 */
public class Sphere extends Surface {
	// values for the sphere object
	protected Vec center;
	protected double radius;


	@Override
	public double nearestIntersection(Ray ray) {
		Vec L = Vec.sub(center, ray.p);
		double Tca = Vec.dotProd(L , ray.v);
		if (Tca < 0)
			return Double.POSITIVE_INFINITY;
		double dSqr = L.lengthSquared() - Math.pow(Tca, 2);
		if (dSqr > Math.pow(radius,2))
			return Double.POSITIVE_INFINITY;
		double Thc = Math.sqrt(Math.pow(radius, 2) - dSqr);
		
		return Math.min(Tca-Thc, Tca+Thc);
	}

	@Override
	public Vec normalAt(Vec intersection, Ray ray) {
		// Normal : (P - O) / ||P - O||
		Vec normal = new Vec(intersection);
		normal.sub(center);
		normal.normalize();
		return normal;
	}

	@Override
	public void init(Map<String, String> attributes) {

		if (attributes.containsKey("center"))
			center = new Vec(attributes.get("center"));
		if (attributes.containsKey("radius"))
			radius = Double.valueOf(attributes.get("radius"));

		super.init(attributes);
	}
}
