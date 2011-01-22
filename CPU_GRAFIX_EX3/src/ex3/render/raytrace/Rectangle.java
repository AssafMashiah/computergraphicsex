package ex3.render.raytrace;

import java.util.Map;

import math.Plane;
import math.Ray;
import math.Vec;

public class Rectangle extends Surface {

	/* Rectangle needs 3 points in space */
	protected Vec P0;
	protected Vec P1;
	protected Vec P2;
	protected Vec P0P1;
	protected Vec P0P2;
	protected Vec rNormal;

	/**
	 * an empty constructor
	 */
	public Rectangle()
	{
		this.P0 = null;
		this.P1 = null;
		this.P2 = null;
		this.P0P1 = null;
		this.P0P2 = null;
		this.rNormal = null;
	}
	
	/**
	 * constructs a rectangle with 3 points and the give attributs
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param attributes - attributes
	 */
	public Rectangle(Vec p0, Vec p1, Vec p2 , Map<String, String> attributes) {
		this.P0 = p0;
		this.P1 = p1;
		this.P2 = p2;
		rNormal = Plane.getNormal(p0, p1, p2);
		
		setAttributes();		
		super.init(attributes);
	}

	@Override
	public double nearestIntersection(Ray ray) {
		// construct the plane of the new plane
		Plane plane = new Plane(P0, P1, P2);
		double distance = Plane.intersectWithPlane(ray, plane);
		// if the ray hits the plane...
		if (!Double.isInfinite(distance)) {
			Vec p = Vec.add(ray.p, Vec.scale(distance, ray.v));
			p.sub(P0);
			double res1 = Vec.dotProd(p, P0P1);
			double res2 = Vec.dotProd(p, P0P2);
			// find out if the ray hits the surface of the plane
			if ( (res1 <= P0P1.lengthSquared() && res1 >= 0) && (res2 <= P0P2.lengthSquared() && res2 >= 0) )
			{
				if (this.material.type.equals("checkers"))
				{
					double width = 0;
					double height = 0; 
					width = P0P1.lengthSquared() * this.material.checkersSize;
					height = P0P2.lengthSquared() * this.material.checkersSize;
					int xVal = (int) (Vec.dotProd(p, P0P1) / width);
					int yVal = (int) (Vec.dotProd(p, P0P2) / height);
					int checkerNum = (yVal + xVal) % 2;
					if (checkerNum == 0 ){
						this.material.diffuse = new Vec(this.material.diffuse1);
					} else {
						this.material.diffuse = new Vec(this.material.diffuse2);
					}
					
				}
				return distance;
			}
		}
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public Vec normalAt(Vec intersection, Ray ray) {
		return Plane.getNormal(P0, P1, P2, ray);
	}

	@Override
	public void init(Map<String, String> attributes) {
		if (attributes.containsKey("p0"))
			P0 = new Vec(attributes.get("p0"));
		if (attributes.containsKey("p1"))
			P1 = new Vec(attributes.get("p1"));
		if (attributes.containsKey("p2")) 
			P2 = new Vec(attributes.get("p2"));
		
		setAttributes();
		super.init(attributes);
	}

	/**
	 * set's the attributes
	 */
	private void setAttributes() {
		// calculating the vectors that define the triangle
		P0P1 = Vec.sub(P1, P0);
		P0P2 = Vec.sub(P2, P0);
		
		// The normal of the triangle plane 
		this.rNormal = Plane.getNormal(P0, P1, P2);
	}
}
