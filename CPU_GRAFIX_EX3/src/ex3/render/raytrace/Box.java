package ex3.render.raytrace;

import java.util.Map;

import math.Ray;
import math.Vec;

public class Box extends Surface {
	// the variables for the box
	private static final int BASE = 1;
	private static final int FACE1 = 2;
	private static final int FACE2 = 3;
	private static final int FACE3 = 4;
	private static final int FACE4 = 5;
	private static final int TOP = 6;
	protected Vec p0;
	protected Vec p1;
	protected Vec p2;
	protected Vec p3;
	protected Vec p4;
	protected Vec p5;
	protected Vec p6;
	protected Rectangle base;
	protected Rectangle facep0p2p1;
	protected Rectangle facep0p2p3;
	protected Rectangle facep3p4p5;
	protected Rectangle facep4p1p6;
	protected Rectangle top;
	protected int numOfFaceIntersect;

	@Override
	public double nearestIntersection(Ray ray) {
		double distBase = base.nearestIntersection(ray);
		double distF1 = facep0p2p1.nearestIntersection(ray);
		double distF2 = facep0p2p3.nearestIntersection(ray);
		double distF3 = facep3p4p5.nearestIntersection(ray);
		double distF4 = facep4p1p6.nearestIntersection(ray);
		double distTop = top.nearestIntersection(ray);

		if (distBase < distF1 && distBase < distF2 && distBase < distF3
				&& distBase < distF4 && distF1 < distTop) {
			numOfFaceIntersect = BASE;
			return distBase;
		}
		
		if (distF1 < distF2 && distF1 < distF3 && distF1 < distF4
				&& distF1 < distTop) {
			numOfFaceIntersect = FACE1;
			return distF1;
		}
		
		if (distF2 < distF3 && distF2 < distF4 && distF2 < distTop) {
			numOfFaceIntersect = FACE2;
			return distF2;
		}
		
		if (distF3 < distF4 && distF3 < distTop) {

			numOfFaceIntersect = FACE3;
			return distF3;
		}
		
		if (distF4 < distTop) {
			numOfFaceIntersect = FACE4;
			return distF4;
		}

		numOfFaceIntersect = TOP;
		return distTop;

	}

	@Override
	public Vec normalAt(Vec intersection, Ray ray) {
		switch (numOfFaceIntersect) {
		case BASE:
			return base.normalAt(intersection, ray);
		case FACE1:
			return facep0p2p1.normalAt(intersection, ray);
		case FACE2:
			return facep0p2p3.normalAt(intersection, ray);
		case FACE3:
			return facep3p4p5.normalAt(intersection, ray);
		case FACE4:
			return facep4p1p6.normalAt(intersection, ray);
		default:
			return top.normalAt(intersection, ray); // if it's the top
		}
	}

	/**
	 * initialize the box in the given coordinations
	 */
	public void init(Map<String, String> attributes) {

		if (attributes.containsKey("p0"))
			p0 = new Vec(attributes.get("p0"));
		if (attributes.containsKey("p1"))
			p1 = new Vec(attributes.get("p1"));
		if (attributes.containsKey("p2"))
			p2 = new Vec(attributes.get("p2"));
		if (attributes.containsKey("p3"))
			p3 = new Vec(attributes.get("p3"));

		p4 = Vec.add(Vec.add(Vec.sub(p1, p0), Vec.sub(p3, p0)), p0);
		p5 = Vec.add(Vec.add(Vec.sub(p2, p0), Vec.sub(p3, p0)), p0);
		p6 = Vec.add(Vec.add(Vec.sub(p5, p3), Vec.sub(p4, p3)), p3);

		base = new Rectangle(p0, p1, p3, attributes);
		facep0p2p1 = new Rectangle(p0, p2, p1, attributes);
		facep0p2p3 = new Rectangle(p0, p2, p3, attributes);
		facep3p4p5 = new Rectangle(p3, p5, p4, attributes);
		facep4p1p6 = new Rectangle(p4, p1, p6, attributes);
		top = new Rectangle(p5, p2, p6, attributes);

		super.init(attributes);
	}
}
