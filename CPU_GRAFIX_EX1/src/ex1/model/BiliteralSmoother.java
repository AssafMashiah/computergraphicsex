package ex1.model;

import java.awt.image.BufferedImage;

/**
 * Class for performing content aware image resizing using the Seam Carving
 * method
 * 
 */
public class BiliteralSmoother
{
	private BufferedImage m_OrigImage;
	private BufferedImage m_CurImage;
	private BufferedImage m_CurSobel;

	private double[][] m_GrayImage;

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
		m_CurImage = copyImage(img);
		m_OrigImage = copyImage(img);

		// We use this private member to save redundant calculations
		m_GrayImage = ImageProcessor.rgb2gray(m_CurImage);

		// As with curImage and origImage we want to change the Sobel matrix
		// accordingly without having to call the Sobel edge detection too much.
		m_CurSobel = 
			ImageProcessor.gray2rgb(ImageProcessor.sobelEdgeDetect(m_GrayImage));
	}

	/**
	 * We can think of this method like a copy constructor for BufferedImage
	 * 
	 * @param img
	 *            - the BufferedImage to copy
	 * @return - a copied BufferedImage
	 */
	private BufferedImage copyImage(BufferedImage img) 
	{
		BufferedImage temp = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		for (int col = 0; col < temp.getWidth(); col++) {
			for (int row = 0; row < temp.getHeight(); row++) {
				temp.setRGB(col, row, img.getRGB(col, row));
			}
		}
		return temp;
	}

	public void Smoother(double[][] kernel, BufferedImage img)
	{
		double[][] image = new double[img.getWidth()][img.getHeight()];
		for(int i = 0; i < img.getWidth() ; i++)
		{
			for (int j = 0; j < img.getHeight() ; j++)
			{
				image[i][j] = img.getRGB(i, j);
			}
		}
		
		m_CurImage = ImageProcessor.BiliteralConvolve(image, kernel);
	}
	
	
	
/////////////////////////////////////////////////////
	
	
	
	public static BufferedImage SetBiliteralKernel(BufferedImage img, int x, int y)
	{
		double[][] biliteralKernel = new double[ImageProcessor.gaussianBlur.length][ImageProcessor.gaussianBlur[0].length];
		
		int indexX = (x - 1) / 2;
		int indexY = (y - 1) / 2;
		
		BufferedImage sub = img.getSubimage(indexX, indexY, ImageProcessor.gaussianBlur.length, ImageProcessor.gaussianBlur[0].length);
		
		for(int i = 0; i < ImageProcessor.gaussianBlur.length ; i++)
		{
			for(int j = 0 ; j < ImageProcessor.gaussianBlur[0].length ; j++)
			{
				biliteralKernel[i][j] = sub.getRGB(i, j);
			}
		}
		
		System.out.println("sadfasdfasdf");
		return sub;
	}

//////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Retrieves an image of the first step of the algorithm: The grayscale
	 * image of the original RGB image.
	 * 
	 * @return Grayscale image
	 */
	public BufferedImage getGrayscaleImage() 
	{
		return ImageProcessor.gray2rgb(m_GrayImage);
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