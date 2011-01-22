package ex3.render.raytrace;

import java.util.Map;

import math.Plane;
import math.Ray;
import math.Vec;

/**
 * A simple Triangle surface
 */
public class Triangle extends Surface {

	/* triangle needs 3 points in space */
	protected Vec P0;
	protected Vec P1;
	protected Vec P2;
	protected Vec normal;

	/**
	 * an enpty constructor
	 */
	public Triangle() {
		this.P0 = null;
		this.P1 = null;
		this.P2 = null;
		this.normal = null;
	}

	/**
	 * construct a triangle with the given points
	 * @param p0
	 * @param p1
	 * @param p2
	 */
	public Triangle(Vec p0, Vec p1, Vec p2) {
		this.P0 = p0;
		this.P1 = p1;
		this.P2 = p2;

		normal = Plane.getNormal(p0, p1, p2);
	}

	/**
	 * returns a vec of the normal
	 * 
	 * @return normal
	 */
	public Vec getNormal() {
		return new Vec(normal);
	}

	@Override
	public double nearestIntersection(Ray ray) {
		Plane plane = new Plane(P0, P1, P2);
		double distance = Plane.intersectWithPlane(ray, plane);
		if (!Double.isInfinite(distance)) {

			Vec p = Vec.add(ray.p, Vec.scale(distance, ray.v));

			Vec check1 = Vec.crossProd(Vec.sub(P1, p), Vec.sub(p, P0));
			Vec check2 = Vec.crossProd(Vec.sub(P2, P1), Vec.sub(p, P1));
			Vec check3 = Vec.crossProd(Vec.sub(P0, P2), Vec.sub(p, P2));

			double check11 = Vec.dotProd(check1, normal);
			double check22 = Vec.dotProd(check2, normal);
			double check33 = Vec.dotProd(check3, normal);

			if (check11 > 0 && check22 > 0 && check33 > 0)
				return distance;
		}
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public Vec normalAt(Vec intersection, Ray ray) {
		return new Vec(normal);
	}

	@Override
	public void init(Map<String, String> attributes) {
		if (attributes.containsKey("p0"))
			P0 = new Vec(attributes.get("p0"));
		if (attributes.containsKey("p1"))
			P1 = new Vec(attributes.get("p1"));
		if (attributes.containsKey("p2"))
			P2 = new Vec(attributes.get("p2"));
		this.normal = Plane.getNormal(P0, P1, P2);
		super.init(attributes);
	}
}