package ex3.render.raytrace;

import java.util.Map;

/**
 * Interface for objects that can initialize from attribute pairs
 * 
 */
public interface IInitable {

	/**
	 * Initialize from attribute pairs
	 * 
	 * @param attributes
	 */
	public void init(Map<String, String> attributes);

}
