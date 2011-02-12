package ex1.model;

import java.awt.image.BufferedImage;

/**
 * Class for performing content aware image resizing using the Seam Carving method
 */
public class BiliteralSmoother
{
	private BufferedImage m_CurImage;
	private BufferedImage m_CurSobel;
	private BufferedImage m_GrayImage;

	private double[][] m_GrayImageArray;
	private static BufferedImage m_SmoothImage;

	/**
	 * Initializes the Seam Carving algorithm with the given parameters. Any old
	 * data should be disposed.
	 * 
	 * @param img
	 *            The original RGB image the user selected
	 * @param grayscaleMethod
	 *            Which method to use for rgb2gray conversion
	 */
	public void init(BufferedImage img)
	{
		// Save a copy of the image so we don't loos it :)
		m_CurImage = copyImage(img);

		// We use this private member to save redundant calculations
		m_GrayImageArray = ImageProcessor.rgb2gray(m_CurImage);

		m_SmoothImage = copyImage(img); 
		
		// As with curImage and origImage we want to change the Sobel matrix
		// accordingly without having to call the Sobel edge detection too much.
		m_CurSobel = ImageProcessor.gray2rgb(ImageProcessor.sobelEdgeDetect(m_GrayImageArray));
	}

	/**
	 * We can think of this method like a copy constructor for BufferedImage
	 * 
	 * @param img - the BufferedImage to copy
	 * @return - a copied BufferedImage
	 */
	private BufferedImage copyImage(BufferedImage img) 
	{
		BufferedImage temp = new BufferedImage(img.getWidth(), img.getHeight(),	BufferedImage.TYPE_INT_RGB);
		for (int col = 0; col < temp.getWidth(); col++)
		{
			for (int row = 0; row < temp.getHeight(); row++)
			{
				temp.setRGB(col, row, img.getRGB(col, row));
			}
		}
		return temp;
	}

	/**
	 * Gets the smooth image
	 * @param img
	 * @return
	 */
	public static BufferedImage RunBiliteralSmooth(BufferedImage img)
	{
		return m_SmoothImage;
	}
	
	/**
	 * Retrieves an image of the first step of the algorithm: The grayscale
	 * image of the original RGB image.
	 * 
	 * @return Grayscale image
	 */
	public BufferedImage getGrayscaleImage() 
	{
		m_GrayImage = ImageProcessor.gray2rgb(m_GrayImageArray);; 
		return m_GrayImage;
	}

	/**
	 * Retrieves an image of the second step of the algorithm: The edge image/
	 * energy image for the resized image. This method is called by the GUI
	 * framework after each "resize".
	 * 
	 * @return Edge image
	 */
	public BufferedImage getEdgeImage() 
	{
		return m_CurSobel;
	}

	/**
	 * Retrieves an image of the third and final step of the algorithm: The
	 * actual resized image.
	 * 
	 * @return Resized image
	 */
	public BufferedImage getImage() 
	{
		return m_CurImage;
	}
}