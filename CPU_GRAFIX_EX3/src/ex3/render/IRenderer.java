package ex3.render;

import java.awt.image.BufferedImage;
import java.io.File;

import ex3.parser.SceneDescriptor;

/**
 * Interface for rendering classes that receive string description and return a
 * rendered image
 * 
 */
public interface IRenderer {

	/**
	 * Inits the renderer with scene description and sets the target canvas to
	 * size (width X height). After init renderLine may be called
	 * 
	 * @param sceneDesc
	 *            Description data structure of the scene
	 * @param width
	 *            Width of the canvas
	 * @param height
	 *            Height of the canvas
	 * @param path
	 *            File path to the location of the scene. Should be used as a
	 *            basis to load external resources (e.g. background image)
	 */
	public void init(SceneDescriptor sceneDesc, int width, int height, File path);

	/**
	 * Renders the given line to the given canvas. Canvas is of the exact size
	 * given to init. This method must be called only after init.
	 * 
	 * @param canvas
	 *            BufferedImage containing the partial image
	 * @param line
	 *            The line of the image that should be rendered.
	 */
	public void renderLine(BufferedImage canvas, int line);
}
