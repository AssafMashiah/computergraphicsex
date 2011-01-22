package ex3.render.raytrace;

import java.util.Map;

import math.Vec;

/**
 * Represent's a surface's material
 * 
 */
public class Material implements IInitable {

	protected Vec diffuse;
	protected Vec diffuse1;
	protected Vec diffuse2;
	protected Vec ambient;
	protected Vec emission;
	protected Vec specular;
	protected double shininess;
	protected double reflectance;
	protected double checkersSize;
	protected String type;
	
	/**
	 * A new material object with default values
	 */
	public Material() {
		diffuse = new Vec(0.7, 0.7, 0.7);
		ambient = new Vec(0.1, 0.1, 0.1);
		specular = new Vec(1, 1, 1);
		emission = new Vec(0, 0, 0);
		shininess = 101;
		reflectance = 0;
		type = "flat";
		checkersSize = 0.1;
		diffuse1 = new Vec(1 , 1 , 1);
		diffuse2 = new Vec(0.2 , 0.2 , 0.2);
	}

	/**
	 * Returns the diffuse value at a given parameterization (u,v)
	 * 
	 * @param u
	 * @param v
	 * @return
	 */
	public Vec diffuseAt(double u, double v) {
		return diffuse;
	}

	public void init(Map<String, String> attributes) {
		if (attributes.containsKey("mtl-diffuse"))
			diffuse = new Vec(attributes.get("mtl-diffuse"));
		if (attributes.containsKey("mtl-specular"))
			specular = new Vec(attributes.get("mtl-specular"));
		if (attributes.containsKey("mtl-emission"))
			emission = new Vec(attributes.get("mtl-emission"));
		if (attributes.containsKey("mtl-ambient"))
			ambient = new Vec(attributes.get("mtl-ambient"));
		if (attributes.containsKey("mtl-shininess"))
			shininess = Double.valueOf(attributes.get("mtl-shininess"));
		if (attributes.containsKey("reflectance"))
			reflectance = Double.valueOf(attributes.get("reflectance"));
		if (attributes.containsKey("checkers-diffuse1"))
			diffuse1 = new Vec(attributes.get("checkers-diffuse1"));
		if (attributes.containsKey("checkers-diffuse2"))
			diffuse2 = new Vec(attributes.get("checkers-diffuse2"));
		if (attributes.containsKey("checkers-size"))
			checkersSize = Double.valueOf(attributes.get("checkers-size"));
		if (attributes.containsKey("mtl-type"))
			type = String.valueOf(attributes.get("mtl-type"));
			
	}
}
