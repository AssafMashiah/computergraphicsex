package ex3.gui;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A simple panel that displays an image from a buffer. To use it just add it 
 * to a container and then call "setImage" with a BufferedImage object. Afterwards 
 * Call the container's "pack" to recalculate sizes, and "repaint" in order to issue
 * a repaint event. Unless invalidated otherwise, only after calling "repaint" will 
 * the new image appear. 
 * 
 * @author chen
 * 
 */
public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage image;

	public ImagePanel() {
		super();
		this.setDoubleBuffered(false);
		this.setPreferredSize(new Dimension(320,240));
	}

	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Overrides the JPanel's paint method to draw the image
	 */
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	/**
	 * Sets the image to be displayed. Resizes panel to fit new image.
	 * @param image Any BufferedImage
	 */
	public void setImage(BufferedImage image) {
		if(image==null)
			return;
		this.image = image;
		setSize(new Dimension(image.getWidth(), image.getHeight()));
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
	}

	/**
	 * Paint without cleaning the buffer
	 */
	public void update(Graphics g) {
		paint(g);
	}
}
