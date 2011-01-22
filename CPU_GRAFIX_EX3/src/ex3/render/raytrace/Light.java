package ex3.render.raytrace;

import java.util.Map;

import math.Vec;

/**
 * Represent a point light
 * 
 */
public class Light {

	// Position of the light
	protected Vec pos;
	// The color of the light
	protected Vec color;
	// The attenuation factor of the light
	protected Vec attenuation;

	/**
	 * constructs a new light object with default values 
	 */
	public Light() {
		pos = new Vec(0, 0, 0);
		color = new Vec(1,1,1);
		attenuation = new Vec (1 , 0 , 0);
	}

	/**
	 * initialize the light object according the XML file
	 * @param attributes
	 */
	public void init(Map<String, String> attributes) {
		if (attributes.containsKey("pos"))
			pos = new Vec(attributes.get("pos"));
		if (attributes.containsKey("color"))
			color = new Vec(attributes.get("color"));		
		if (attributes.containsKey("attenuation"))
			attenuation = new Vec(attributes.get("attenuation"));
	}
}
