package ex1.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import ex1.model.BiliteralSmoother;
import ex1.model.ImageProcessor;

/**
 * Frame of the main GUI of the application
 * 
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	// Custom SWING component to display the image
	private ImagePanel m_ImagePanel;

	private ImageFrame m_GrayscaleFrame;

	private ImageFrame m_EdgeFrame;

	// Image received as input
	private BufferedImage m_RawImage;

	// The main algorithm object
	private BiliteralSmoother m_biliteralSmoother;

	// Message to appear in About dialog.
	private String m_aboutMessage;

	// New window
	private boolean m_IsNewImage;

	// New window for Biliteral result
	private ImageFrame m_BiliteralSmoothFrame;

	// New window for smooth result
	private ImageFrame m_SmoothFrame;

	// Settings window
	private JFrame m_Settings;

	// The Image that will be saved
	private BufferedImage m_ImageToSave;

	protected boolean firstTime = true;

	protected BufferedImage m_GaussFilteredImage;

	// Settings data
	private JTextField m_SigmaLabel;
	private JTextField m_SigmaText;
	private JTextField m_IterationsLabel;
	private JTextField m_IterationsText;
	private JTextField m_ThresholdLabel;
	private JTextField m_ThresholdText;

	// Constants defauls
	private final int SIGMA = 3;
	private final int ITERATIONS = 1;
	private final int THRESHOLD = 225;

	protected boolean m_CanWork = false;

	/**
	 * Create Frame GUI
	 * 
	 */
	public MainFrame() {
		super("Exercise1");

		setNativeLookAndFeel();

		// Create UI components
		m_ImagePanel = new ImagePanel();
		m_Settings = new JFrame();
		m_Settings.setLayout(new FlowLayout());

		// Add UI components
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(m_ImagePanel, BorderLayout.CENTER);

		// Handle window events
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(1);
			}
		});

		// Add top menu
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createActionMenu());
		menuBar.add(createHelpMenu());
		this.setJMenuBar(menuBar);

		this.setResizable(false);
		this.pack();

		// Create display frames
		m_GrayscaleFrame = new ImageFrame("Grayscale");
		m_EdgeFrame = new ImageFrame("Edges");
		m_BiliteralSmoothFrame = new ImageFrame("Biliteral Smooth");
		m_SmoothFrame = new ImageFrame("Gaussian Smooth");

		// Init model
		this.m_biliteralSmoother = new BiliteralSmoother();
		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		
		// Init Setting
		// Sigma
		m_SigmaLabel = new JTextField("Enter 'Sigma' here:", 15);
		m_SigmaLabel.setEditable(false);

		m_SigmaText = new JTextField(10);

		m_IterationsLabel = new JTextField("Number of iterations:", 15);
		m_IterationsLabel.setEditable(false);

		m_IterationsText = new JTextField(10);

		m_ThresholdLabel = new JTextField("Enter edge threshold:", 15);
		m_ThresholdLabel.setEditable(false);

		m_ThresholdText = new JTextField(10);

	}

	/**
	 * Create the File->open/File->save menu item.
	 * 
	 * @return
	 */
	protected JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		JMenuItem settingsItem = new JMenuItem("Settings", KeyEvent.VK_E);
		settingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				ActionEvent.CTRL_MASK));
		fileMenu.add(settingsItem);
		settingsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_Settings.setResizable(false);
				m_Settings.pack();

				m_Settings.add(m_SigmaLabel);
				m_Settings.add(m_SigmaText);
				m_Settings.add(m_IterationsLabel);
				m_Settings.add(m_IterationsText);
				m_Settings.add(m_ThresholdLabel);
				m_Settings.add(m_ThresholdText);

				m_Settings.setSize(300, 115);

				m_Settings.setVisible(true);
				firstTime = false;
			}
		});

		JMenuItem fileOpen = new JMenuItem("Open...", KeyEvent.VK_O);
		fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.CTRL_MASK));
		fileMenu.add(fileOpen);

		fileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showFileOpenDialog();
				m_CanWork = true;
			}
		});

		JMenuItem saveItem = new JMenuItem("Save Abstraction...", KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		fileMenu.add(saveItem);
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showFileSaveDialog();
			}
		});
		
		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});

		return fileMenu;
	}

	/**
	 * Creates the menu which handles the scaling operations
	 * 
	 * @return An initialized menu
	 */
	protected JMenu createActionMenu() {
		JMenu menu = new JMenu("Filters");
		menu.setMnemonic(KeyEvent.VK_F);

		JMenuItem edgeItem = new JMenuItem("Edges");
		edgeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
				ActionEvent.CTRL_MASK));
		menu.add(edgeItem);
		edgeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_EdgeFrame.setVisible(true);
				if (m_EdgeFrame.isVisible() && m_CanWork) {
					m_EdgeFrame.showImage(m_biliteralSmoother.getEdgeImage());
				}
			}
		});
		menu.add(edgeItem);

		JMenuItem grayscaleItem = new JMenuItem("Grayscale");
		grayscaleItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		menu.add(grayscaleItem);
		grayscaleItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(m_CanWork)
				{
					m_GrayscaleFrame.setVisible(true);
				}
			}
		});
		menu.add(grayscaleItem);

		JMenuItem Smooth = new JMenuItem("Gaussian Smooth");
		Smooth.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				ActionEvent.CTRL_MASK));
		menu.add(Smooth);
		Smooth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(m_CanWork)
				{
					if(!m_SmoothFrame.isVisible())
					{
						m_Settings.setVisible(true);
					}
					else
					{
						m_SmoothFrame.setVisible(true);
					}
					
					int sigma = SIGMA;
					int iterations = ITERATIONS;
					
					if(m_Settings.isVisible())
					{
						try {
							sigma = Integer.parseInt(m_SigmaText.getText());
							iterations = Integer.parseInt(m_IterationsText.getText());
		
							if (sigma > 0 && sigma % 2 == 1 && iterations >= 1) {
								m_Settings.setVisible(false);
								m_SigmaText.setText("");
								m_IterationsText.setText("");
								
						
							m_GaussFilteredImage = ImageProcessor.convolve(m_RawImage,
									ImageProcessor.gaussianBlur);
							m_SmoothFrame.showImage(m_GaussFilteredImage);
							} else {
								m_SigmaText.setText("");
								m_IterationsText.setText("");
							}
						} catch (NumberFormatException nfe) {
							m_SigmaText.setText("");
							m_IterationsText.setText("");
						}
				}
				}
			}
		});
		menu.add(grayscaleItem);

		final JMenuItem chkSmooth = new JMenuItem();
		chkSmooth.setText("Biliteral Smooth");
		chkSmooth.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
				ActionEvent.CTRL_MASK));

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());

		chkSmooth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				m_BiliteralSmoothFrame.setVisible(true);

				int sigma = SIGMA;
				int iterations = ITERATIONS;
				
				if(m_Settings.isVisible())
				{
					try {
						sigma = Integer.parseInt(m_SigmaText.getText());
						iterations = Integer.parseInt(m_IterationsText.getText());
	
						if (sigma > 0 && sigma % 2 == 1 && iterations >= 1) {
							m_Settings.setVisible(false);
							m_SigmaText.setText("");
							m_IterationsText.setText("");
							m_ImageToSave = ImageProcessor.SmoothImage(m_RawImage,
									sigma, iterations);
							m_BiliteralSmoothFrame.showImage(m_ImageToSave);
						} else {
							m_SigmaText.setText("");
							m_IterationsText.setText("");
						}
					} catch (NumberFormatException nfe) {
						m_SigmaText.setText("");
						m_IterationsText.setText("");
					}
				}
				resetModel();
			}
		});
		menu.add(chkSmooth);

		final JMenuItem chkAddEdges = new JMenuItem();
		chkAddEdges.setText("Abstract image");
		chkAddEdges.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));
		chkAddEdges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int threshold = THRESHOLD;
				if (m_BiliteralSmoothFrame.isVisible() && m_Settings.isVisible()) {
					try {
						threshold = Integer.parseInt(m_ThresholdText.getText());
						if(threshold > 0 && threshold < 255)
						{
							m_BiliteralSmoothFrame.showImage(ImageProcessor.addEdges(m_ImageToSave, m_biliteralSmoother.getEdgeImage(), threshold));
						}
							
					} catch (NumberFormatException nfe) {
						m_ThresholdText.setText("");
					}
					
					resetModel();
				}
			}
		});
		menu.add(chkAddEdges);

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
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,
				ActionEvent.CTRL_MASK));
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
	 * Should be called after construction. Sets default input image and about
	 * message
	 * 
	 * @param img
	 *            An image to load when just starting the app
	 * @param aboutMessage
	 *            A message to appear in the about dialog
	 */
	public void initialize(BufferedImage img, String aboutMessage) {
		this.m_aboutMessage = aboutMessage;
		this.setRawImage(img);
		resetModel();
	}

	/**
	 * Clears model and reinitializes it with model parameters. In addition
	 * resets the slider.
	 * 
	 */
	protected void resetModel() {
		if (m_RawImage != null) {
			m_biliteralSmoother.init(m_RawImage);

			m_GrayscaleFrame.showImage(m_biliteralSmoother.getGrayscaleImage());

			m_ImagePanel.setImage(m_RawImage);
			m_ImagePanel.repaint();
		}

		if (m_IsNewImage) {
			this.pack();
			updateScreenPosition();
			m_IsNewImage = false;
		}
	}

	/**
	 * Sets raw image and update scaler about new width
	 * 
	 * @param rawImage
	 */
	protected void setRawImage(BufferedImage rawImage) {
		this.m_RawImage = rawImage;
		m_IsNewImage = true;
	}

	/**
	 * Shows a "file open" dialog. Natively supports PNG file format and other
	 * formats as well (jpg, bmp,...). If image is valid then sets it as new
	 * input image and displays it.
	 * 
	 */
	protected void showFileOpenDialog() {
		JFileChooser fd = new JFileChooser();

		fd.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg",
				"bmp"));
		fd.showOpenDialog(this);

		File file = fd.getSelectedFile();

		if (file != null) {
			try {
				if (m_EdgeFrame.isVisible()) {
					m_EdgeFrame.setVisible(false);
				}
				m_EdgeFrame.dispose();

				if (m_GrayscaleFrame.isVisible()) {
					m_GrayscaleFrame.setVisible(false);
				}
				m_GrayscaleFrame.dispose();

				if (m_BiliteralSmoothFrame.isVisible()) {
					m_BiliteralSmoothFrame.setVisible(false);
				}
				m_BiliteralSmoothFrame.dispose();

				if (m_SmoothFrame.isVisible()) {
					m_SmoothFrame.setVisible(false);
				}
				m_SmoothFrame.dispose();

				m_EdgeFrame = new ImageFrame("Edges");
				m_BiliteralSmoothFrame = new ImageFrame("Biliteral Smooth");
				m_SmoothFrame = new ImageFrame("Gaussian Smooth");

				// Create BufferedImage from file
				setRawImage(ImageIO.read(file));
				resetModel();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Shows a "file save" dialog. Natively supports PNG file format and other
	 * formats as well (jpg, bmp,...). Saves currently displayed image.
	 * 
	 */
	protected void showFileSaveDialog() {
		JFileChooser fd = new JFileChooser();

		fd.setFileFilter(new FileNameExtensionFilter("png", "png"));
		fd.showSaveDialog(this);

		File file = fd.getSelectedFile();

		if (file != null) {
			try {
				ImageIO.write(this.m_ImageToSave, "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Shows the about dialog
	 */
	protected void showHelpAboutDialog() {
		JOptionPane.showMessageDialog(this, m_aboutMessage, "About",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Places the frame at the center of the screen
	 */
	protected void updateScreenPosition() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension labelSize = this.getSize();
		this.setLocation(screenSize.width / 2 - (labelSize.width / 2),
				screenSize.height / 2 - (labelSize.height / 2));
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

	protected BufferedImage getRawImage() {
		return m_RawImage;
	}
}