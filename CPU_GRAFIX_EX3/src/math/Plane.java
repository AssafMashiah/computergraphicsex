package math;

public class Plane {
	protected Vec p0;
	protected Vec p1;
	protected Vec p2;

	protected Vec normal;

	protected double dist;

	/**
	 * constructs an empty plane
	 */
	public Plane() {
		this.p0 = null;
		this.p1 = null;
		this.p2 = null;
		this.normal = null;
		this.dist = 0;
	}

	/**
	 * constructs a plane given 3 points
	 * @param p0
	 * @param p1
	 * @param p2
	 */
	public Plane(Vec p0, Vec p1, Vec p2) {
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.normal = new Vec(Vec.crossProd(Vec.sub(p1, p0), Vec.sub(p2, p0)));
		this.normal.normalize();

		this.dist = Vec.dotProd(normal, p0);
	}

	/**
	 * find an intersection of a ray with a given plane
	 * @param ray
	 * @return the distance
	 */
	public double intersectWithPlane(Ray ray) {
		double numerator = Vec.dotProd(ray.v, normal);
		double distance = 0;
		if (numerator != 0) {
			distance = (-Vec.dotProd(ray.p, normal) + dist) / numerator;
		}

		if (distance <= 0)
			return Double.POSITIVE_INFINITY;

		return distance;
	}
	
	/**
	 * return the normal
	 * @return
	 */
	public Vec getNormal() {
		return new Vec(normal);
	}
	
	/**
	 * returns the normal given 3 points in space
	 * @param p0
	 * @param p1
	 * @param p2
	 * @return normal
	 */
	public static Vec getNormal(Vec p0, Vec p1, Vec p2) {
		Vec res = new Vec(Vec.crossProd(Vec.sub(p1, p0), Vec.sub(p2, p0)));
		res.normalize();
		return res;
	}

	/**
	 * this method is for acquiring the correct orientation of a box and pyramid
	 * objects the points represent the plane and the ray is so we can have the
	 * base of the convention of the orientation.
	 * 
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param ray
	 * @return normal
	 */
	public static Vec getNormal(Vec p0, Vec p1, Vec p2, Ray ray) {
		Vec result = getNormal(p0, p1, p2);
		// sets the correct orientation
		if (result.dotProd(ray.v) < 0)
			return result;
		return Vec.negate(result);
	}

	/**
	 * Getting the intersection point of a ray on a given plane.
	 * 
	 * @param ray
	 *            - ray representation
	 * @param plane
	 *            - plane represent by 3 points
	 * @return
	 */
	public static double intersectWithPlane(Ray ray, Plane plane) {
		// the cos(angle) of the dot product
		double numerator = Vec.dotProd(ray.v, plane.normal);
		double distance = 0;
		if (numerator != 0) {
			distance = (-Vec.dotProd(ray.p, plane.normal) + plane.dist)
					/ numerator;
		}
		if (distance <= 0)
			return Double.POSITIVE_INFINITY;

		return distance;
	}

}
