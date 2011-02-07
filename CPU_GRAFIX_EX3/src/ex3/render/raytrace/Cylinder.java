package ex3.render.raytrace;

import java.util.Map;

import math.CylindarVec;
import math.Ray;
import math.Vec;

public class Cylinder extends Surface
{
	protected Vec origin;
	protected double radius;
	protected double height;
	
	public Cylinder()
	{
		this.origin = null;
		this.radius = 0;
		this.height = 0;
	}
	
	public Cylinder(Vec origin, double radius, double height)
	{
		this.origin = origin;
		this.radius = radius;
		this.height = height;
	}
	
	/**
	 * return the normal
	 * @return
	 */
	public Vec normalAt(CylindarVec intersection, Ray ray)
	{
		return normalAt(intersection.toCartesian(), ray);
	}

	public Vec normalAt(Vec intersection, Ray ray)
	{
		// intersection - (0,0,z)
		return Vec.sub(intersection, new Vec(this.origin.x,this.origin.y,intersection.z));
	}
	
	@Override
	public void init(Map<String, String> attributes) {

		if (attributes.containsKey("origin"))
			origin = new Vec(attributes.get("origin"));
		if (attributes.containsKey("radius"))
			radius = Double.valueOf(attributes.get("radius"));
		if (attributes.containsKey("height"))
			height = Double.valueOf(attributes.get("height"));

		super.init(attributes);
	}

	@Override
	public double nearestIntersection(Ray ray) {
		// first if
		// origin <= arbitraryPoint.z <= origin + height
		// see http://www.cl.cam.ac.uk/teaching/1999/AGraphHCI/SMAG/node2.html for more info
		double a = Math.pow(ray.v.x, 2) + Math.pow(ray.v.x, 2);
		double b = 2 * ((ray.p.x - this.origin.x) * ray.v.x + (ray.p.y - this.origin.y) * ray.v.y); 
		double c = Math.pow((ray.p.x - this.origin.x), 2) + Math.pow((ray.p.y - this.origin.y), 2) - Math.pow(this.radius, 2);
		System.out.println(this.radius);
		double delta = Math.sqrt(Math.pow(b, 2) - 4 * a * c);
		// System.out.println(delta);
		if(delta < 0)
		{
			return Double.POSITIVE_INFINITY;
		}
		
		double t1 = (b * (-1) + delta) / (2 * a);
		double t2 = (b * (-1) - delta) / (2 * a);
		
		// gets the point of the intersection with the cylinder
		Vec firstIn = Vec.add(ray.p, Vec.scale(t1, ray.v));
		Vec secondIn = Vec.add(ray.p, Vec.scale(t2, ray.v));
		
		if(firstIn.z < (this.origin.z + this.height) 
			&& firstIn.z > this.origin.z &&
			secondIn.z < (this.origin.z + this.height) 
			&& secondIn.z > this.origin.z)
		{
			double t1In = firstIn.length();
			double t2In = secondIn.length();
			
			return Math.min(t1In, t2In);
		}
		
		if(firstIn.z < (this.origin.z + this.height) 
				&& firstIn.z > this.origin.z)
		{
			return firstIn.length();
		}
		
		if(secondIn.z < (this.origin.z + this.height) 
				&& secondIn.z > this.origin.z)
		{
			return secondIn.length();
		}
		return Double.POSITIVE_INFINITY;
	}
}