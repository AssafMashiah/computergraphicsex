package ex3.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import ex3.parser.SceneDescriptor;
import ex3.render.IRenderer;
import ex3.render.RendererFactory;

/**
 * Frame of the main GUI of the application
 */
public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	protected String aboutMessage;
	protected File currentDir;
	protected File sceneFile;
	protected String sceneXMLDesc;
	protected IRenderer renderer;
	protected ImagePanel imagePanel;

	/**
	 * Create Frame GUI
	 */
	public MainFrame() {
		super("Exercise3");

		currentDir = new File(".");
		
		setNativeLookAndFeel();

		// Create UI components
		imagePanel = new ImagePanel();

		// Add UI components
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(imagePanel, BorderLayout.CENTER);

		// Handle window events
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(1);
			}
		});

		// Add top menu
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createAlgorithmMenu());
		menuBar.add(createHelpMenu());
		this.setJMenuBar(menuBar);

		imagePanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateTitle();
				MainFrame.this.imagePanel
						.setPreferredSize(MainFrame.this.imagePanel.getSize());
				super.componentResized(e);
			}
		});

		this.setResizable(true);
		this.pack();
	}

	/**
	 * Create the File->open/File->save menu item.
	 * 
	 * @return
	 */
	protected JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		JMenuItem fileOpen = new JMenuItem("Select Scene...", KeyEvent.VK_T);
		fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				ActionEvent.CTRL_MASK));
		fileMenu.add(fileOpen);

		fileOpen.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showSceneFileOpenDialog();
			}
		});

		JMenuItem menuItem = new JMenuItem("Save Image...", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		fileMenu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showImageSaveDialog();
			}
		});

		return fileMenu;
	}

	/**
	 * Creates the menu which handles the render operations
	 * 
	 * @return An initialized menu
	 */
	protected JMenu createAlgorithmMenu() {
		JMenu menu = new JMenu("Render");
		menu.setMnemonic(KeyEvent.VK_R);

		JMenuItem menuItem = new JMenuItem("Quick Render", KeyEvent.VK_Q);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				render();
			}
		});

		return menu;
	}

	/**
	 * Create a simple help menu to access the About dialog
	 * 
	 * @return Menu
	 */
	protected JMenu createHelpMenu() {
		JMenu menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);

		JMenuItem menuItem = new JMenuItem();
		menuItem.setText("About");
		menuItem.setMnemonic(KeyEvent.VK_A);

		menu.add(menuItem);

		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showHelpAboutDialog();
			}
		});
		return menu;
	}

	/**
	 * Should be called after construction. Sets default scene file, canvas size
	 * and about message
	 * 
	 * @param sceneFilename Filename of the scene to automatically load on startup
	 * @param canvasWidth Width of the rendered image
	 * @param canvasHeight Height of the rendered image
	 * @param aboutMessage Message to appear at the about dialog
	 */
	public void initialize(String sceneFilename, int canvasWidth,
			int canvasHeight, String aboutMessage) {
		this.aboutMessage = aboutMessage;
		
		setImageSize(new Dimension(canvasWidth, canvasHeight));
		pack();

		if (sceneFilename != null)
			sceneFile = new File(sceneFilename);

		updateScreenPosition();
	}

	/**
	 * Load the scene and render it to a canvas while display it in the image 
	 * panel. 
	 */
	public void render() {
				
		System.out.println("Begin Render");

		if(!loadSceneFromFile(sceneFile))
			return;
			
		pack();

		// Obtain canvas size from window size
		int canvasWidth = (int) getImageSize().getWidth();
		int canvasHeight = (int) getImageSize().getHeight();

		// Parse scene
		SceneDescriptor sd = new SceneDescriptor();
		try {
			sd.fromXML(sceneXMLDesc);
		} catch (ParseException e) {
			System.out.println("Syntactical error in scene description:");
			e.printStackTrace();
		}

		// Instantiate new renderer
		renderer = RendererFactory.newInstance();
		renderer.init(sd, (int) getImageSize().getWidth(), (int) getImageSize()
				.getHeight(), sceneFile);

		// Create canvas
		BufferedImage canvas = new BufferedImage(canvasWidth, canvasHeight,
				BufferedImage.TYPE_INT_RGB);
		
		// Render a line and draw to screen immediately
		long time = System.currentTimeMillis();
		for (int y = 0; y < canvas.getHeight(); y++) {
			renderer.renderLine(canvas, y);
			showImage(canvas);
		}
		System.out.println("It took " + (System.currentTimeMillis() - time)+ " MS to render");
	}

	/**
	 * Shows a "file open" dialog. Select XML files and set the current scene.
	 * 
	 */
	protected void showSceneFileOpenDialog() {
		JFileChooser fd = new JFileChooser();

		fd.setCurrentDirectory(currentDir);
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setDialogTitle("Select Scene file");
		fd.setAcceptAllFileFilterUsed(false);

		fd.setFileFilter(new FileNameExtensionFilter("XML Scene files", "xml"));
		if (fd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			currentDir = fd.getCurrentDirectory();
			sceneFile = fd.getSelectedFile();
			updateTitle();
		}
	}

	/**
	 * Read the XML text data to a string
	 * @param file File pointing to XML file
	 */
	public boolean loadSceneFromFile(File file) {
		if (file == null)
			return false;
		try {
			byte[] buffer = new byte[(int) file.length()];
			FileInputStream fin = new FileInputStream(file);

			fin.read(buffer);
			sceneXMLDesc = (new String(buffer));
			return true;

		} catch (IOException e) {
			e.printStackTrace();			
		}
		return false;		
	}

	/**
	 * Shows a "file save" dialog. Natively supports PNG file format and other
	 * formats as well (jpg, bmp,...). Saves currently displayed image.
	 * 
	 */
	protected void showImageSaveDialog() {
		JFileChooser fd = new JFileChooser();

		fd.setFileFilter(new FileNameExtensionFilter("png", "png"));
		fd.showSaveDialog(this);

		File file = fd.getSelectedFile();

		if (file != null) 
			saveRenderedImage(file);
	}

	/**
	 * Saves currently rendered image to given file
	 * @param file Image filename
	 */
	public void saveRenderedImage(File file) {
		if(imagePanel.getImage() == null)
			return;
		
		try {
			ImageIO.write(imagePanel.getImage(), "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the about dialog
	 */
	protected void showHelpAboutDialog() {
		JOptionPane.showMessageDialog(this, aboutMessage, "About",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Places the frame at the center of the screen
	 */
	protected void updateScreenPosition() {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int width = this.getWidth();
		int height = this.getHeight();

		this.setLocation(screenSize.width / 2 - (width / 2), screenSize.height
				/ 2 - (height / 2));
	}

	/**
	 * Tells Swing to paint GUI as if it is native (i.e. uses OS GUI style)
	 */
	public static void setNativeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}
	}

	/**
	 * Displays given image and resizes frame to fit it - updates immediately!
	 * 
	 * @param img
	 */
	public void showImage(BufferedImage img) {
		imagePanel.setImage(img);
		pack();

		// Using this method the panel can repaint immediately even without 
		// returning thread control to the EDT. 
		imagePanel.paintImmediately(0, 0, img.getWidth(), img.getHeight());
	}

	/**
	 * Saves space for the image
	 * 
	 * @param size
	 *            Size of placeholder
	 */
	public void setImageSize(Dimension size) {
		imagePanel.setPreferredSize(size);
		imagePanel.setSize(size);
		pack();
	}

	/**
	 * Update title according to canvas size and selected scene
	 */
	protected void updateTitle() {
		String sceneFilename = "<empty scene>";
		if (sceneFile != null)
			sceneFilename = sceneFile.getName();
		this.setTitle(sceneFilename + " (" + this.imagePanel.getWidth() + ","
				+ this.imagePanel.getHeight() + ")");
	}
	
	public Dimension getImageSize() {
		return imagePanel.getSize();
	}	
}
