package ex3.render;

import ex3.render.raytrace.RayTracer;

/**
 * Returns the default renderer
 * 
 */
public class RendererFactory {

	/**
	 * Instantiates a new renderer and returns it
	 * 
	 * @return
	 */
	public static IRenderer newInstance() {
		return new RayTracer();  
	}
}
