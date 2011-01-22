package math;


public class Ray {

	// point of origin
	public Vec p;
	// ray direction
	public Vec v;
	
	/**
	 * constructs a new ray
	 * @param p - point of origin
	 * @param v - ray direction
	 */
	public Ray(Vec p, Vec v) {
		this.p = p;
		this.v = v;
	}
}
