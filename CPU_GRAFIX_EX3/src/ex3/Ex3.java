package ex3;

import java.io.File;

import ex3.gui.MainFrame;

public class Ex3 {

	private static String getAboutMessage() {
		return "This is a render application that render a picture according to" +
				" given fields the convention of the data is in the EX3 paper."
				+ "there are some XML files in the librery scene you can use to" +
						" start render now";
	}

	/**
	 * Main method. Command line usage is: <input scene filename> <canvas width>
	 * <canvas height> <target image filename>
	 */
	public static void main(String[] args) {

		String sceneFilename = null;
		String imageFilename = null;
		int canvasWidth = 480;
		int canvasHeight = 360;

		if (args.length > 0)
			sceneFilename = args[0];
		if (args.length > 2)
			canvasWidth = Integer.valueOf(args[1]);
		if (args.length > 2)
			canvasHeight = Integer.valueOf(args[2]);
		if (args.length > 3)
			imageFilename = args[3];

		// Init GUI
		MainFrame mainFrame = new MainFrame();
		
		mainFrame.initialize(sceneFilename, canvasWidth, canvasHeight,
				getAboutMessage());

		if (imageFilename == null) {
			mainFrame.setVisible(true);
		} else {
			// Render to file and quit
			mainFrame.render();
			mainFrame.saveRenderedImage(new File(imageFilename));
			System.exit(1);
		}
	}
}
