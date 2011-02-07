package ex1.model;

import java.awt.image.BufferedImage;

/**
 * Class for performing content aware image resizing using the Seam Carving
 * method
 * 
 */
public class SeamCarver {
	private static final int BLACK_COLOR = 0;
	private BufferedImage origImage;
	private BufferedImage curImage;
	private BufferedImage origSobel;
	private BufferedImage curSobel;

	private GrayscaleMethod grayScaleType;

	private double[][] grayImage;
	private double[][] edgeMatrix;
	private int[][] indexMatrix;
	private int[][] seamIndex;

	/**
	 * Initializes the Seam Carving algorithm with the given parameters. Any old
	 * data should be disposed.
	 * 
	 * @param img
	 *            The original RGB image the user selected
	 * @param isRealtime
	 *            To use the realtime algorithm or not?
	 * @param grayscaleMethod
	 *            Which method to use for rgb2gray conversion
	 */
	public void init(BufferedImage img, boolean isRealtime,
			GrayscaleMethod grayscaleMethod) {
		/*
		 * We needed to use copy here , when opening another file after changing
		 * a different image. it created an aliasing bug, which cause a
		 * NullPointerException
		 */

		curImage = copyImage(img);
		origImage = copyImage(img);

		grayScaleType = grayscaleMethod;

		// We use this private member to save redundant calculations
		grayImage = ImageProcessor.rgb2gray(curImage, grayScaleType);

		// This private member helps us with calculation in the dynamic
		// programming
		edgeMatrix = ImageProcessor.sobelEdgeDetect(grayImage);

		// As with curImage and origImage we want to change the Sobel matrix
		// accordingly without having to call the Sobel edge detection too much.
		origSobel = ImageProcessor.gray2rgb(ImageProcessor
				.sobelEdgeDetect(grayImage));
		curSobel = ImageProcessor.gray2rgb(ImageProcessor
				.sobelEdgeDetect(grayImage));

		// The index matrix contains the indexes in proportion to the original
		// image , it helps us draw the resized image.
		indexMatrix = new int[img.getHeight()][img.getWidth()];
		initIndexMatrix(indexMatrix);

		// The seamIndex matrix helps us find the corresponding seams to
		// 'delete' at each iteration
		seamIndex = new int[img.getHeight()][img.getWidth()];

		// Because of the pre-processing we need to simulate every seam removal
		// Until no more can be done
		for (int i = 0; i < img.getWidth(); i++) {
			dynamicProgramming(i);
		}
	}

	/**
	 * We can think of this method like a copy constructor for BufferedImage
	 * 
	 * @param img
	 *            - the BufferedImage to copy
	 * @return - a copied BufferedImage
	 */
	private BufferedImage copyImage(BufferedImage img) {
		BufferedImage temp = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		for (int col = 0; col < temp.getWidth(); col++) {
			for (int row = 0; row < temp.getHeight(); row++) {
				temp.setRGB(col, row, img.getRGB(col, row));
			}
		}
		return temp;
	}

	private void dynamicProgramming(int currentSeamNum) {
		int rows = edgeMatrix.length;
		int columns = edgeMatrix[0].length;
		double[][] DPcalc = new double[rows][columns + 2];
		int[] result = new int[rows];

		// Copying the first line of the sobelEdge Matrix
		for (int i = 1; i < columns; i++) {
			DPcalc[0][i] = edgeMatrix[0][i - 1];
		}

		// Padding the matrix with 2 MAX_VALUE columns at both edges
		for (int i = 0; i < rows; i++) {
			DPcalc[i][0] = Double.MAX_VALUE;
			DPcalc[i][columns + 1] = Double.MAX_VALUE;
		}

		// Calculating the DP
		for (int i = 1; i < rows; i++) {
			for (int j = 1; j < columns + 1; j++) {
				DPcalc[i][j] = edgeMatrix[i][j - 1]
						+ Math.min(Math.min(DPcalc[i - 1][j - 1],
								DPcalc[i - 1][j]), DPcalc[i - 1][j + 1]);
			}
		}

		// Back Tracking and finding the seam
		int xIndex = findMin(DPcalc[rows - 1]);
		result[rows - 1] = xIndex - 1;
		
		// this will pick the right place to go to in the x axis
		for (int i = rows - 2; i >= 0; i--) {
			if ((DPcalc[i][xIndex - 1] < DPcalc[i][xIndex])
					&& (DPcalc[i][xIndex - 1] < DPcalc[i][xIndex + 1])) {
				xIndex -= 1;
			} else {
				if ((DPcalc[i][xIndex + 1] < DPcalc[i][xIndex])
						&& (DPcalc[i][xIndex + 1] < DPcalc[i][xIndex - 1])) {
					xIndex += 1;
				}
			}
			result[i] = xIndex - 1;
		}

		/*
		 * We update in the seamIndex matrix , the coordinates of the pixels
		 * which we need to remove, in respect to the original image
		 * coordinates.
		 */
		for (int i = 0; i < rows; i++) {
			seamIndex[i][indexMatrix[i][result[i]]] = currentSeamNum;
		}

		/*
		 * Now we delete 1 seam both from the SobelEdge matrix and from the
		 * index matrix So next time we calculate the dynamic programming we do
		 * it on a smaller picture.
		 */

		// new edge matrix
		double[][] tempEdgeMatrix = new double[rows][columns - 1];

		// new index Matrix
		int[][] tempIndexMatrix = new int[rows][columns - 1];

		for (int row = 0; row < rows; row++) {
			// copying the values up to the seam itself (not including)
			for (int col = 0; col < result[row]; col++) {
				tempEdgeMatrix[row][col] = edgeMatrix[row][col];
				tempIndexMatrix[row][col] = indexMatrix[row][col];
			}

			// copying the values from the seam to the end (not including)
			for (int col = result[row] + 1; col < columns; col++) {
				tempEdgeMatrix[row][col - 1] = edgeMatrix[row][col];
				tempIndexMatrix[row][col - 1] = indexMatrix[row][col];
			}
		}

		// copying the pointers
		indexMatrix = tempIndexMatrix;
		edgeMatrix = tempEdgeMatrix;

	}

	/***
	 * Finding minimum value in an array , used to find the x coordinate of the
	 * minimum value in the last line of the DP table
	 * 
	 * @param mat
	 *            - list of numbers corresponding with the values of the DP of
	 *            the image
	 * @return
	 */

	private int findMin(double[] mat) {
		int tempIndex = 0;
		double tempMin = Double.MAX_VALUE;

		// finds the minimum value
		for (int i = 0; i < mat.length; i++) {
			if (mat[i] < tempMin) {
				tempMin = mat[i];
				tempIndex = i;
			}
		}
		return tempIndex;
	}

	/***
	 * initializing the index values of the matrix
	 * 
	 * @param mat
	 *            - index values 0 to image width
	 */
	private void initIndexMatrix(int[][] mat) {
		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat[0].length; j++) {
				mat[i][j] = j;
			}
		}

	}

	/**
	 * Resizes the image to the given value. The result is later retrieved by
	 * the GUI using getResizedImage.
	 * 
	 * @param targetWidth
	 *            A target width to resize to. Not larger than original image's
	 *            width.
	 */
	public void resize(int targetWidth) {
		int rows = origImage.getHeight();
		int columns = origImage.getWidth();
		int pixCount;
		int validPix = columns - targetWidth;
		for (int row = 0; row < rows; row++) {
			pixCount = 0;

			// saving relevant data from the original image to the current used image
			for (int col = 0; col < columns; col++) {
				if (seamIndex[row][col] >= validPix) {
					curImage.setRGB(pixCount, row, origImage.getRGB(col, row));
					curSobel.setRGB(pixCount, row, origSobel.getRGB(col, row));
					pixCount++;
				}
			}

			// paints the rest of the unused pixels in black
			for (int col = pixCount; col < columns; col++) {
				curImage.setRGB(col, row, BLACK_COLOR);
				curSobel.setRGB(col, row, BLACK_COLOR);
			}
		}
	}

	/**
	 * Retrieves an image of the first step of the algorithm: The grayscale
	 * image of the original RGB image.
	 * 
	 * @return Grayscale image
	 */
	public BufferedImage getGrayscaleImage() {
		return ImageProcessor.gray2rgb(grayImage);
	}

	/**
	 * Retrieves an image of the second step of the algorithm: The edge image/
	 * energy image for the resized image. This method is called by the GUI
	 * framework after each "resize".
	 * 
	 * @return Edge image
	 */
	public BufferedImage getEdgeImage() {
		return curSobel;
	}

	/**
	 * Retrieves an image of the third and final step of the algorithm: The
	 * actual resized image.
	 * 
	 * @return Resized image
	 */
	public BufferedImage getResizedImage() {
		return curImage;
	}

}