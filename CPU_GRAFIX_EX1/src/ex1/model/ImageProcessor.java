package ex1.model;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Contains a few handy image processing routines
 */
public class ImageProcessor {

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
	
	/**
	 * Applies a Convolution operator to a given matrix and kernel.
	 * 
	 * @param I
	 *            A 2d array
	 * @param kernel
	 *            The kernel/filter. Must be square and have odd size!
	 * @return A new 2D array not necessarily of the size as I
	 */
	public static double[][] convolve(double[][] I, double[][] kernel, boolean dynamicKernel) {
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
				if(dynamicKernel)
				{
					kernel = getBiliteralKernel(I, i, j);
				}
				
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

	private static double[][] getBiliteralKernel(double[][] img, int x, int y)
	{

		double[][] biliteralKernel = new double[ImageProcessor.gaussianBlur.length][ImageProcessor.gaussianBlur[0].length];
		
		int indexX = (x - 1) / 2;
		int indexY = (y - 1) / 2;
		
		for(int i = 0; i < ImageProcessor.gaussianBlur.length ; i++)
		{
			for(int j = 0 ; j < ImageProcessor.gaussianBlur[0].length ; j++)
			{
				//TODO:
				biliteralKernel[i][j] = img[indexX][indexY];
				
//				if(i == j)
//				{
//					System.out.println(biliteralKernel[i][j]);
//				}
			}
		}
		
		biliteralKernel = KernelMult(biliteralKernel, gaussianBlur);
		
		return biliteralKernel;
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

	public static BufferedImage SmoothImage(BufferedImage img)
	{
		int width = img.getWidth();
		int height = img.getHeight();
		
		double[][] imageRed = new double[width][height];
		double[][] imageGreen = new double[width][height];
		double[][] imageBlue = new double[width][height];
		
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				Color temp = new Color(img.getRGB(i, j));
				imageRed[i][j] = temp.getRed();
				imageGreen[i][j] = temp.getGreen();
				imageBlue[i][j] = temp.getBlue();
			}
		}
		
		imageRed = convolve(imageRed, gaussianBlur, true);
		imageGreen = convolve(imageGreen, gaussianBlur, true);
		imageBlue = convolve(imageBlue, gaussianBlur, true);
		
		BufferedImage returnImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for(int i = 0; i < width - 2; i++)
		{
			for(int j = 0; j < height - 2; j++)
			{
				returnImage.setRGB(i, j, new Color((int)Math.round(imageRed[i][j]), (int)Math.round(imageGreen[i][j]), (int)Math.round(imageBlue[i][j])).getRGB());
			}
		}
		
		return returnImage;
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
	
	public static BufferedImage getImage(double[][] img)
	{
		int height = img.length;
		int width = img[0].length;
		BufferedImage result = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < height; i++)
		{
			for (int j = 0; j < width; j++)
			{
				Color currentColor = new Color((int)img[i][j]);
				System.out.println(currentColor.getRGB());
				result.setRGB(j, i, currentColor.getRGB()); 
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
	public static double[][] sobelEdgeDetect(double[][] I) {
		double[][] tempImg = convolve(I, gaussianBlur, false);
		double[][] xGrad = convolve(tempImg, deriveKernel, false);
		double[][] yGrad = convolve(tempImg, deriveKernel2, false);
		
		for (int i = 0; i < tempImg.length - 1; i++)
		{
			for (int j = 0; j < tempImg[0].length - 1; j++)
			{
				tempImg[i][j] = Math.hypot(xGrad[i][j], yGrad[i][j]);
			}
		}

		return tempImg;
	}

	public static double[][] KernelMult(double[][] kernelA, double[][] kernelB)
	{
		double[][] res = new double[kernelA.length][kernelA[0].length];
		for(int i = 0; i < kernelA.length ; i++)
		{
			for(int j = 0; j < kernelA[0].length ; j++)
			{
				res[i][j] = kernelA[i][j] * kernelB[i][j];
				res[i][j] = res[i][j] / 1210;
			}
		}
		return res;
	}

	/**
	 * adds the edges to the image
	 * @param img
	 * @param edgeImg
	 */
	public static void addEdges(double[][] img, double[][] edgeImg)
	{
		for(int i = 0; i < img.length ; i++)
		{
			for(int j = 0; j < img[0].length ; j++)
			{
				// might need to make sure the numbers don't go to high...
				img[i][j] = img[i][j] + edgeImg[i][j];
			}
		}
	}
	
	public static BufferedImage BiliteralConvolve(double[][] img)
	{
		System.out.println("YEY");
		
		return null;
	}
}