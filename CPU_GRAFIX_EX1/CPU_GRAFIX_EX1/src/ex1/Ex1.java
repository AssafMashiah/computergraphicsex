package ex1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ex1.gui.MainFrame;

public class Ex1 {

	/**
	 * Creates Loads image from filename passed as an argument
	 * 
	 * @return A BufferedImage
	 */
	private static BufferedImage getTestImage(String[] args) {

		if (args.length > 1) {
			throw new IllegalArgumentException("Usage : Ex1 <image path>");
		}
		if (args.length == 1) {
			File file = new File(args[0]);
			if (file != null) {
				try {
					return ImageIO.read(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static String getAboutMessage() {
		return "Scaling GUI for exercise1\nAdd an image from your file system.\n " +
				"Use \"Ctrl + E\" to get the edge window.\n Use \"Ctrl + G\" to " +
				"get the gray scale image window.\n Drag the bar and watch the image " +
				"rescals using the seam carving. for real-time preformace pick " +
				"'real-time' from the 'Algorithem' menu.\n Enjoy\t\t Omer Amsel and Assaf Mashiah";
	}

	/**
	 * Main method. Instantiates the main form and initializes it with a test
	 * image.
	 * 
	 * @param args
	 *            First argument expected filename (optional)
	 */
	public static void main(String[] args) {
		MainFrame frm = new MainFrame();
		frm.initialize(getTestImage(args), getAboutMessage());
		frm.setVisible(true);
	}
}
