package ex3.render.raytrace;

import math.Vec;

/**
 * Contains information regarding a ray-surface intersection.
 */
public class Hit {
	
	// the point of hit
	public Vec intersection;
	// the surface the in is on
	public Surface surface;

	/**
	 * constructs a new hit object
	 * @param intersection
	 * @param surface
	 */
	public Hit(Vec intersection, Surface surface) {
		this.intersection = intersection;
		this.surface = surface;
	}
}
