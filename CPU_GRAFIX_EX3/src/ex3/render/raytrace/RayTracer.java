package ex3.render.raytrace;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import math.Ray;
import math.Vec;
import ex3.parser.Element;
import ex3.parser.SceneDescriptor;
import ex3.render.IRenderer;

public class RayTracer implements IRenderer {

	/**
	 * For optimizations the use of threads
	 */
	private class MyThread implements Runnable {
		private BufferedImage canvas;
		private int startIndex;
		private int endIndex;
		private int line;

		/**
		 * construct a new thread
		 * 
		 * @param canvas
		 * @param startIndex
		 * @param endIndex
		 */
		public MyThread(BufferedImage canvas, int startIndex, int endIndex,
				int line) {
			this.startIndex = startIndex;
			this.canvas = canvas;
			this.line = line;
			this.endIndex = endIndex;
		}

		@Override
		public void run() {
			for (int x = startIndex; x < endIndex; x++) {
				canvas.setRGB(x, line, castRay(x, height - line - 1).getRGB());
			}
		}
	}

	protected int width;
	protected int height;
	protected Scene scene;
	protected File path;

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
	@Override
	public void init(SceneDescriptor sceneDesc, int width, int height, File path) {
		this.width = width;
		this.height = height;
		this.path = path;
		scene = new Scene(height, width, path);
		scene.init(sceneDesc.getSceneAttributes());
		scene.camera.init(sceneDesc.getCameraAttributes());
		for (Element e : sceneDesc.getObjects()) {
			scene.addObjectByName(e.getName(), e.getAttributes());
		}
	}

	/**
	 * Renders the given line to the given canvas. Canvas is of the exact size
	 * given to init. This method must be called only after init.
	 * 
	 * @param canvas
	 *            BufferedImage containing the partial image
	 * @param line
	 *            The line of the image that should be rendered.
	 */
	@Override
	public void renderLine(BufferedImage canvas, int line) {
		int threadCount = scene.getThreadCount();
		if (line == 2)
			System.out.println("Recognized " + threadCount + " Threads!");
		MyThread threads[] = new MyThread[threadCount];
		int jumpSize = (width / threadCount);
		int i;
		for (i = 0 ; i < threadCount - 1 ; i++)
		{
			threads[i] = new MyThread (canvas , i* jumpSize, (i+1) * jumpSize , line);
			threads[i].run();
		}
		threads[i] = new MyThread ( canvas , i * jumpSize , width , line);
		threads[i].run();
	}

	/**
	 * Compute color for given image coordinates (x,y)
	 * 
	 * @param x
	 * @param y
	 * @return Color at coordinate
	 */
	private Color castRay(int x, int y) {
		Ray sumRay = null;
		Hit hit = null;
		Vec valuesSum = new Vec();
		boolean didHit = false;
		double start = (-1 * (scene.superSamplecCount - 1))
				/ (scene.superSamplecCount * 2.0);
		double step = 1.0 / (scene.superSamplecCount);

		for (double i = start; i < 0.5; i += step) {
			for (double j = start; j < 0.5; j += step) {
				sumRay = scene.camera.constructRayThroughPixel(x + i, y + j);
				hit = scene.findIntersection(sumRay);
				// sum the values of the light
				valuesSum.add(scene.calcColor(hit, sumRay,
						scene.maxRecursionLevel));
			}
		}
		if (hit != null)
			didHit = true;
		// scales the pixel to the correct range
		valuesSum.scale(1 / Math.pow(scene.superSamplecCount, 2.0));

		if (didHit)
			return valuesSum.toColor();
		else
		{
			if (scene.backgroundTexture != null)
				return new Color(scene.backgroundTexture.getRGB(x,height - y - 1));		
		}
		return scene.backgroundCol.toColor();
	}
		

}