package ex1.gui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * Frame for displaying images
 * @author Assaf & Orr
 *
 */
public class ImageFrame extends JFrame 
{
	private static final long serialVersionUID = 1L;
	
	protected ImagePanel m_ImagePanel; 
	
	/**
	 * Creates new frame with given title 
	 * @param title
	 */
	public ImageFrame(String title) 
	{
		super(title);
		
		m_ImagePanel = new ImagePanel();		
		getContentPane().add(m_ImagePanel);
		
		// Resizes frame to fit image
		pack();
	}
	
	/**
	 * Displays given image and resizes frame to fit it
	 * @param img
	 */
	public void showImage(BufferedImage img) 
	{
		m_ImagePanel.setImage(img);		
		pack();
		m_ImagePanel.repaint();
	}	
	
	/**
	 * Saves space for the image
	 * @param size Size of placeholder
	 */
	public void setImageSize(Dimension size) 
	{
		m_ImagePanel.setPreferredSize(size);
		m_ImagePanel.setSize(size);
		pack();
	}
}