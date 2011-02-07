package ex1.gui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * Frame for displaying images
 * @author chen
 *
 */
public class ImageFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	protected ImagePanel imagePanel; 
	
	/**
	 * Creates new frame with given title 
	 * @param title
	 */
	public ImageFrame(String title) {
		super(title);
		
		imagePanel = new ImagePanel();		
		getContentPane().add(imagePanel);
		
		// Resizes frame to fit image
		pack();
	}
	
	/**
	 * Displays given image and resizes frame to fit it
	 * @param img
	 */
	public void showImage(BufferedImage img) {
		imagePanel.setImage(img);		
		pack();
		imagePanel.repaint();
	}	
	
	/**
	 * Saves space for the image
	 * @param size Size of placeholder
	 */
	public void setImageSize(Dimension size) {
		imagePanel.setPreferredSize(size);
		imagePanel.setSize(size);
		pack();
	}
}
