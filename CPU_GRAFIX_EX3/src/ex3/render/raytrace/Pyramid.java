package ex3.render.raytrace;

import java.util.Map;
import math.Ray;
import math.Vec;

public class Pyramid extends Surface {
	// values for a pyramid
	private static final int BASE = 1;
	private static final int FACE1 = 2;
	private static final int FACE2 = 3;
	protected Vec p0;
	protected Vec p1;
	protected Vec p2;
	protected Vec p3;
	protected Vec p0p1;
	protected Vec p0p2;
	protected Triangle base;
	protected Triangle facep0p1p3;
	protected Triangle facep2p0p3;
	protected Triangle facep1p2p3;
	protected int numOfFaceIntersect;

	@Override
	public double nearestIntersection(Ray ray) {

		// 4 doubles that are associated with 4 distances to the faces of the
		// pyramid
		double distBase = base.nearestIntersection(ray);
		double distF1 = facep0p1p3.nearestIntersection(ray);
		double distF2 = facep2p0p3.nearestIntersection(ray);
		double distF3 = facep1p2p3.nearestIntersection(ray);

		/*
		 * A pretty annoying 'if' that its sole purpose is to determine the
		 * appropriate face to the minimum value of distance using a private
		 * integer which later help the calculation of the normal at the hit
		 * point
		 */
		if (distBase < distF1 && distBase < distF2 && distBase < distF3) {
			numOfFaceIntersect = 1;
			return distBase;
		}

		if (distF1 < distF2 && distF1 < distF3) {
			numOfFaceIntersect = 2;
			return distF1;

		}
		if (distF2 < distF3) {
			numOfFaceIntersect = 3;
			return distF2;
		}

		numOfFaceIntersect = 4;
		return distF3;
	}

	@Override
	public Vec normalAt(Vec intersection, Ray ray) {

		switch (numOfFaceIntersect) {
		case BASE:
			return base.normalAt(intersection, ray);
		case FACE1:
			return facep0p1p3.normalAt(intersection, ray);
		case FACE2:
			return facep2p0p3.normalAt(intersection, ray);
		default:
			return facep1p2p3.normalAt(intersection, ray);
		}

	}

	@Override
	public void init(Map<String, String> attributes) {


		if (attributes.containsKey("p0"))
			p0 = new Vec(attributes.get("p0"));
		if (attributes.containsKey("p1"))
			p1 = new Vec(attributes.get("p1"));
		if (attributes.containsKey("p2"))
			p2 = new Vec(attributes.get("p2"));
		if (attributes.containsKey("p3"))
			p3 = new Vec(attributes.get("p3"));

		setAttributes();
		super.init(attributes);
	}

	/**
	 * set's the attributes
	 */
	private void setAttributes() {
		/* calculating the vectors that define the triangle */
		base = new Triangle(p0, p1, p2);
		facep0p1p3 = new Triangle(p0, p1, p3);
		facep2p0p3 = new Triangle(p2, p0, p3);
		facep1p2p3 = new Triangle(p1, p2, p3);

	}

}
