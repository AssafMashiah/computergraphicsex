package ex3.render.raytrace;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import math.Ray;
import math.Vec;

/**
 * A Scene class containing all the scene objects including camera, lights and
 * surfaces.
 * 
 */
public class Scene implements IInitable {

	protected Vec backTextur;
	protected int superSamplecCount;
	protected int useAcceleration;
	protected Vec backgroundCol;
	protected Vec ambientLight;
	protected List<Surface> surfaces;
	protected List<Light> lights;
	protected Camera camera;
	protected int picHeight;
	protected int picWidth;
	protected int maxRecursionLevel;
	// represent a really small figure
	protected final double EPSILON = 0.001;
	protected String backgroundTex;
	protected String filePath;
	public BufferedImage image;
	private int threadCount;
	protected File path;
	public BufferedImage backgroundTexture; // getter setter
	protected int width;
	protected int height;

	public Scene(int height, int width, File directory) {
		this.backgroundCol = new Vec(0, 0, 0);
		this.ambientLight = new Vec(0, 0, 0);
		this.surfaces = new LinkedList<Surface>();
		this.lights = new LinkedList<Light>();
		this.camera = new Camera(width, height);
		this.picHeight = height;
		this.picWidth = width;
		this.superSamplecCount = 1;
		this.useAcceleration = 0;
		this.maxRecursionLevel = 10;
		this.threadCount = 2;
		this.path = directory;
		this.height = height;
		this.width = width;
	}

	public void init(Map<String, String> attributes) {
		if (attributes.containsKey("background-col"))
			backgroundCol = new Vec(attributes.get("background-col"));
		if (attributes.containsKey("ambient-light"))
			ambientLight = new Vec(attributes.get("ambient-light"));
		if (attributes.containsKey("super-samp-width"))
			superSamplecCount = Integer.parseInt(attributes
					.get("super-samp-width"));
		if (attributes.containsKey("max-recursion-level"))
			maxRecursionLevel = Integer.parseInt(attributes
					.get("max-recursion-level"));
		if (attributes.containsKey("thread-count"))
			threadCount = Integer.parseInt(attributes.get("thread-count"));

		if (attributes.containsKey("background-tex")) {
			try {
				String filePath = new String(path.getParent()
						+ File.separatorChar + attributes.get("background-tex"));
				File file = new File(filePath);

				BufferedImage bsrc = ImageIO.read(file);
				this.backgroundTexture = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = backgroundTexture.createGraphics();
				AffineTransform at = AffineTransform.getScaleInstance(
						(double) width / bsrc.getWidth(), (double) height
								/ bsrc.getHeight());
				g.drawRenderedImage(bsrc, at);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Send ray return the nearest intersection. Return null if no intersection
	 * 
	 * @param ray
	 * @return
	 */
	public Hit findIntersection(Ray ray) {

		double minDistance = Double.POSITIVE_INFINITY;
		Surface minSurface = null;
		for (Surface surface : surfaces) {
			double d = surface.nearestIntersection(ray);
			if (minDistance > d) {
				minDistance = d;
				minSurface = surface;
			}
		}

		if (Double.isInfinite(minDistance))
			return null;

		Vec intersection = new Vec(ray.p);
		intersection.mac(minDistance, ray.v);

		return new Hit(intersection, minSurface);
	}

	/**
	 * Calculates the color at a given intersection point
	 * 
	 * @param hit
	 *            The intersection point and surface
	 * @param ray
	 *            Hitting ray
	 * @return
	 */
	public Vec calcColor(Hit hit, Ray ray, int recursionLevel) {
		if (hit == null)
			return backgroundCol;

		Vec pixelRGB = new Vec(ambientLight);

		// get the ambient light from the material
		pixelRGB.scale(hit.surface.material.ambient);

		// gets the emission light
		pixelRGB.add(hit.surface.material.emission);
		Vec normal = new Vec(hit.surface.normalAt(hit.intersection, ray));

		for (int i = 0; i < lights.size(); i++) {
			// new light source
			Vec l = Vec.sub(lights.get(i).pos, hit.intersection);
			l.normalize();
			// N dot product L
			double cosAngel = normal.dotProd(l);

			/*
			 * If we do not add this epsilon we will always hit an object , we
			 * need to take the point of hit an epsilon distance forward to
			 * Separate the point from the surface
			 */
			Ray toTheLight = new Ray(Vec.add(hit.intersection, Vec.scale(
					EPSILON, l)), l);

			// shadows
			Hit testHit = findIntersection(toTheLight);
			Vec hitToObject = new Vec();

			if (testHit != null) {
				hitToObject = Vec.sub(testHit.intersection, hit.intersection);
			}

			Vec hitToLight = Vec.sub(lights.get(i).pos, hit.intersection);
			if (cosAngel >= 0
					&& (testHit == null || (hitToObject.length() >= hitToLight
							.length()))) {

				Vec diffusion = new Vec(hit.surface.material.diffuse);
				diffusion.scale(cosAngel);

				// K_s (V dot R)^ n
				Vec reflection = new Vec(l.reflect(normal));
				reflection.normalize();

				// Calculating the angle between the reflection of the light and
				Vec veiwerPointOfView = new Vec(ray.v);
				veiwerPointOfView.normalize();
				Vec r = veiwerPointOfView.clone();
				r.reflect(normal);
				r.add(hit.intersection);
				double alpha = veiwerPointOfView.dotProd(reflection);

				// the correct calculation of the light in the angle
				if (alpha >= 0) {
					alpha = Math.pow(alpha, hit.surface.material.shininess);
					Vec specular = new Vec(hit.surface.material.specular);
					specular.scale(alpha);
					// adding specular
					diffusion.add(specular);
				}

				// set the correct color due to the light distance from the
				// object
				Vec IL = new Vec(lights.get(i).color);
				double calc = (lights.get(i).attenuation.x)
						+ (hitToLight.length() * lights.get(i).attenuation.y)
						+ (hitToLight.lengthSquared() * lights.get(i).attenuation.z);
				calc = (1.0 / calc);
				IL.scale(calc);

				// adding light
				diffusion.scale(IL);
				/* turn vector to color */
				pixelRGB.add(diffusion);

			}
		}

		if (hit.surface.material.reflectance != 0) {
			if (recursionLevel > 0) {
				Vec reflect = new Vec(ray.v);
				reflect = new Vec(reflect.reflect(normal));
				reflect.normalize();
				Ray newRay = new Ray(Vec.add(hit.intersection, Vec.scale(
						EPSILON, reflect)), reflect);
				pixelRGB
						.mac(hit.surface.material.reflectance, calcColor(
								findIntersection(newRay), newRay,
								(recursionLevel - 1)));
			}
		}
		return pixelRGB;
	}

	/**
	 * Add objects to the scene by name
	 * 
	 * @param name
	 *            Object's name
	 * @param attributes
	 *            Object's attributes
	 */
	public void addObjectByName(String name, Map<String, String> attributes) {
		if (name == "sphere") {
			Surface sphere = new Sphere();
			sphere.init(attributes);
			surfaces.add(sphere);
		}
		if (name == "light-point") {
			Light light = new Light();
			light.init(attributes);
			lights.add(light);
		}
		if (name == "triangle") {
			Surface triangle = new Triangle();
			triangle.init(attributes);
			surfaces.add(triangle);
		}
		if (name == "rectangle") {
			Surface rectangle = new Rectangle();
			rectangle.init(attributes);
			surfaces.add(rectangle);
		}
		if (name == "pyramid") {
			Surface pyramid = new Pyramid();
			pyramid.init(attributes);
			surfaces.add(pyramid);
		}
		if (name == "box") {
			Surface box = new Box();
			box.init(attributes);
			surfaces.add(box);
		}
	}

	/**
	 * returns a back ground color from the image
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Color getColorAt(int x, int y) {
		if (image != null) {
			Color c = new Color(image.getRGB(image.getWidth() - x - 1, image
					.getHeight()
					- y - 1));
			return new Vec((c.getRed() / 255.), (c.getGreen() / 255.), (c
					.getBlue() / 255.)).toColor();
		}
		return backgroundCol.toColor();
	}

	/**
	 * getter for the number of threads
	 * 
	 * @return the thread count
	 */
	public int getThreadCount() {
		return this.threadCount;
	}

	public void setWidthHeight(int width, int height) {
		this.width = width;
		this.height = height;
	}

}
