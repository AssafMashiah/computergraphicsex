package ex1.model;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Contains a few handy image processing routines
 */
public class ImageProcessor
{

	public static final double MAX_COLOR_SCALE = 255.0;
	
	public static double[][] deriveKernel = { { 1, 0, -1 }, { 2, 0, -2 },//TODO: Remove later
			{ 1, 0, -1 } };
	
	public static double[][] deriveKernel2 = { { +1, +2, +1 }, { 0, 0, 0 },//TODO: Remove later
			{ -1, -2, -1 } };
	
	public static double[][] gaussianBlur = {//TODO: Remove later
			{ 1 / 121., 2 / 121., 3 / 121., 2 / 121., 1 / 121. },
			{ 2 / 121., 7 / 121., 11 / 121., 7 / 121., 2 / 121. },
			{ 3 / 121., 11 / 121., 17 / 121., 11 / 121., 3 / 121. },
			{ 2 / 121., 7 / 121., 11 / 121., 7 / 121., 2 / 121. },
			{ 1 / 121., 2 / 121., 3 / 121., 2 / 121., 1 / 121. } };

	private static BufferedImage m_OriginalImage;
	public static  double[][] m_OriginalImageRed;
	public static  double[][] m_OriginalImageGreen;
	public static  double[][] m_OriginalImageBlue;
	/**
	 * Applies a Convolution operator to a given matrix and kernel.
	 * 
	 * @param I
	 *            A 2d array
	 * @param kernel
	 *            The kernel/filter. Must be square and have odd size!
	 * @return A new 2D array not necessarily of the size as I
	 */
	public static double[][] convolve(double[][] I, double[][] kernel)
	{
		int kernelRows = kernel.length;
		int imageRows = I.length;
		int imageCols = I[0].length;
		int edgeSize = (kernelRows - 1) / 2;
		double[][] output = new double[imageRows][imageCols];
		int mirrorCalc = (2 * edgeSize) - 1;
		int length = output.length - 1;
		int height = output[0].length - 1;

		// The convolution process
		for (int i = edgeSize; i < imageRows - edgeSize; i++)
		{
			for (int j = edgeSize; j < imageCols - edgeSize; j++)
			{
				for (int blockRow = edgeSize; blockRow >= -edgeSize; blockRow--)
				{
					for (int blockCol = edgeSize; blockCol >= -edgeSize; blockCol--)
					{
						output[i][j] += I[i	+ blockRow][j + blockCol] * kernel[edgeSize + blockRow][edgeSize + blockCol];
					}
				}
			}
		}

		SetCorrectEdges(edgeSize, output, mirrorCalc, length, height);
		
		return output;
	}

	private static BufferedImage runBiliteralSmooth(int sigma, int iterations)
	{
		BufferedImage image = new BufferedImage(m_OriginalImage.getWidth(), m_OriginalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		int imageCols = m_OriginalImage.getHeight();
		int imageRows = m_OriginalImage.getWidth();
		
		double divisor = (2 * Math.pow(sigma,2));
		for(int iter = 0; iter < iterations; iter++)
		{
			// The convolution process
			for (int i = sigma; i < imageRows - sigma; i++)
			{
				for (int j = sigma; j < imageCols - sigma; j++)
				{
					int[] originalPixel = new int[3];
					Color originalPointOfRefrencColor = new Color(m_OriginalImage.getRGB(i, j));
					
					originalPixel[0] = originalPointOfRefrencColor.getRed();
					originalPixel[1] = originalPointOfRefrencColor.getGreen();
					originalPixel[2] = originalPointOfRefrencColor.getBlue();
					double sumRed = 0;
					double sumGreen = 0;
					double sumBlue = 0;
					double noramlizerRed = 0;
					double noramlizerGreen = 0;
					double noramlizerBlue = 0;
					
					// this is where the magic happens
					for (int blockRow = sigma - 1; blockRow >= -(sigma - 1); blockRow--)
					{
						for (int blockCol = sigma - 1; blockCol >= -(sigma - 1); blockCol--)
						{
							double gaussian = Math.exp(-(Math.pow(blockRow, 2) + Math.pow(blockCol, 2))/divisor); 
							double rangeRed = Math.exp(-Math.pow((m_OriginalImageRed[i - blockRow][j - blockCol] - originalPixel[0]),2)/divisor); 
							double rangeGreen = Math.exp(-Math.pow((m_OriginalImageGreen[i - blockRow][j - blockCol] - originalPixel[1]),2)/divisor);
							double rangeBlue = Math.exp(-Math.pow((m_OriginalImageBlue[i - blockRow][j - blockCol] - originalPixel[2]),2)/divisor);
							
							sumRed += gaussian * rangeRed * m_OriginalImageRed[i - blockRow][j - blockCol];
							sumGreen += gaussian * rangeGreen * m_OriginalImageGreen[i - blockRow][j - blockCol];
							sumBlue += gaussian * rangeBlue * m_OriginalImageBlue[i - blockRow][j - blockCol];
							
							noramlizerRed += gaussian * rangeRed;
							noramlizerGreen += gaussian * rangeGreen;
							noramlizerBlue += gaussian * rangeBlue;
						}
					}
					double finalRed = sumRed / noramlizerRed;
					double finalGreen = sumGreen / noramlizerGreen;
					double finalBlue = sumBlue / noramlizerBlue;
					
					image.setRGB(i, j, new Color((int)Math.round(finalRed), (int)Math.round(finalGreen), (int)Math.round(finalBlue)).getRGB());
				}
			}
			
			m_OriginalImage = image;
		}
		return image;
	}
	
	/**
	 * 
	 * @param edgeSize
	 * @param output
	 * @param mirrorCalc
	 * @param length
	 * @param height
	 */
	private static void SetCorrectEdges(int edgeSize, double[][] output,
			int mirrorCalc, int length, int height)
	{
		// Using a mirror algorithm to fill the edges:
		for (int i = 0; i < edgeSize; i++)
		{
			for (int j = edgeSize; j < output.length - edgeSize; j++)
			{
				output[j][i] = output[j][mirrorCalc - i];
				output[j][height - i] = output[j][height - mirrorCalc + i];
			}
		}
		
		for (int i = 0; i < edgeSize; i++)
		{
			for (int j = 0; j < output[0].length; j++)
			{
				output[i][j] = output[mirrorCalc - i][j];
				output[length - i][j] = output[length - mirrorCalc + i][j];
			}
		}
	}

	/**
	 * Sets every thing to the correct place
	 * @param img
	 * @return
	 */
	public static BufferedImage SmoothImage(BufferedImage img, int sigma, int iterations)
	{
		m_OriginalImage = img;
		int width = img.getWidth();
		int height = img.getHeight();
		
		double[][] originalImageRed = new double[width][height];
		double[][] originalImageGreen = new double[width][height];
		double[][] originalImageBlue = new double[width][height];
		
		// saves the image as red, green and blue images
		setColoredImages(img, width, height, originalImageRed,
				originalImageGreen, originalImageBlue);

		return runBiliteralSmooth(sigma, iterations);
	}

	/**
	 * set's stuff we need later - a type of initiation
	 * @param img
	 * @param width
	 * @param height
	 * @param originalImageRed
	 * @param originalImageGreen
	 * @param originalImageBlue
	 */
	private static void setColoredImages(BufferedImage img, int width,
			int height, double[][] originalImageRed,
			double[][] originalImageGreen, double[][] originalImageBlue)
	{
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				Color temp = new Color(img.getRGB(i, j));
				originalImageRed[i][j] = temp.getRed();
				originalImageGreen[i][j] = temp.getGreen();
				originalImageBlue[i][j] = temp.getBlue();
				
			}
		}
		m_OriginalImageRed = originalImageRed;
		m_OriginalImageGreen = originalImageGreen;
		m_OriginalImageBlue = originalImageBlue;
	}
	
	
	/**
	 * Converts an RGB image object to intensity matrix representation.
	 * 
	 * @param img RGB buffered image (values [0..255])
	 * @return 2D array with unbounded values
	 */
	public static double[][] rgb2gray(BufferedImage img)
	{
		int height = img.getHeight();
		int width = img.getWidth();
		double[][] result = new double[height][width];
		Color tempColor = null;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				tempColor = new Color(img.getRGB(j, i));
				int blue = tempColor.getBlue();
				int green = tempColor.getGreen();
				int red = tempColor.getRed();
				// as given in class
				result[i][j] = (0.2989 * red + 0.5870 * green + 0.1140 * blue)
								/ MAX_COLOR_SCALE;
			}
		}
		return result;
	}
	
	public static BufferedImage getImage(double[][] img)
	{
		BufferedImage result = new BufferedImage(img[0].length, img.length,BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < img.length; i++)
		{
			for (int j = 0; j < img[0].length; j++)
			{
				Color currentColor = new Color((int)img[i][j]);
				result.setRGB(j, i, currentColor.getRGB()); 
			}
		}
		return result;
	}

	/**
	 * Gets the image as a double array 
	 * 
	 * @remark this function is good for testing thing as you go along
	 * @param img
	 * @return
	 */
	public static double[][] getImageDoubleArray(BufferedImage img)
	{
		int height = img.getHeight();
		int width = img.getWidth();
		double[][] result = new double[height][width];

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				result[i][j] = img.getRGB(j, i); 
			}
		}
		return result;
	}
	
	/**
	 * Converts an intensity matrix to RGB image object. Values clipped between
	 * [0..1]
	 * 
	 * @param A
	 *            Intensity matrix with unbounded values
	 * @return BufferedImage containing the input monochromic image in each
	 *         channel
	 */
	public static BufferedImage gray2rgb(double[][] A)
	{
		int height = A.length;
		int width = A[0].length;

		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < width; ++x)
		{
			for (int y = 0; y < height; ++y)
			{
				int val = (int) Math.floor(A[y][x] * 255);
				val = Math.min(Math.max(val, 0), 255);
				img.setRGB(x, y, (new Color(val, val, val)).getRGB());
			}
		}
		return img;
	}

	/**
	 * Linearly scales values of a given matrix to the range [0,1]
	 * @param A A 2D array
	 */
	public static void normalize(double[][] A)
	{
		int rows = A.length;
		int cols = A[0].length;

		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;

		for (int i = 0; i < rows; ++i)
		{
			for (int j = 0; j < cols; ++j)
			{
				if (A[i][j] >= max)
				{
					max = A[i][j];
				}
				if (A[i][j] <= min)
				{
					min = A[i][j];
				}
			}
		}
		
		for (int i = 0; i < rows; ++i)
		{
			for (int j = 0; j < cols; ++j)
			{
				A[i][j] = (A[i][j] - min) / (max - min);
			}
		}
	}

	/**
	 * Given a grayscale image should perform the edge detection algorithm
	 * 
	 * @param I Input grayscale intensity image
	 * @return New image with marked edges. Should have positive values.
	 */
	public static double[][] sobelEdgeDetect(double[][] I)
	{
		double[][] tempImg = convolve(I, gaussianBlur);
		double[][] xGrad = convolve(tempImg, deriveKernel);
		double[][] yGrad = convolve(tempImg, deriveKernel2);
		
		for (int i = 0; i < tempImg.length - 1; i++)
		{
			for (int j = 0; j < tempImg[0].length - 1; j++)
			{
				tempImg[i][j] = Math.hypot(xGrad[i][j], yGrad[i][j]);
			}
		}

		return tempImg;
	}

	/**
	 * adds the edges to the image 
	 * 
	 * @remark you can use only one of the co0lors in the pixel to determine the color
	 * @param img
	 * @param edgeImg
	 */
	public static BufferedImage addEdges(BufferedImage img, BufferedImage edgeImg)
	{
		BufferedImage result = img;
		
		for(int i = 0; i < img.getWidth() ; i++)
		{
			for(int j = 0; j < img.getHeight() ; j++)
			{
				Color tempColor = new Color(edgeImg.getRGB(i, j));
				int blue = tempColor.getBlue();

				if(blue > 200)
				{
					result.setRGB(i, j, new Color(0, 0, 0).getRGB());
				}
			}
		}
		return result;
	}	
}