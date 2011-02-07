package ex1.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import ex1.model.GrayscaleMethod;
import ex1.model.SeamCarver;


/**
 * Frame of the main GUI of the application
 *
 */
public class MainFrame extends JFrame {
		
	private static final long serialVersionUID = 1L;

	// Custom SWING component to display the image
	private ImagePanel imagePanel;
	
	// Slider SWING component for selecting a number from a given range. 
	// Used for controlling scaling size
	private JSlider slider;	
	
	protected ImageFrame grayscaleFrame;
	
	protected ImageFrame edgeFrame; 
	
	// Image received as input
	private BufferedImage rawImage;
	
	// The main algorithm object
	private SeamCarver seamCarver;
	
	// Message to appear in About dialog. 
	private String aboutMessage;

	private boolean isNewImage;
	
	// Model parameters
	private GrayscaleMethod grayscaleMethod;
	private boolean isRealtime;
	
	/**
	 * Create Frame GUI
	 *
	 */
	public MainFrame() {
		super("Exercise1");

		setNativeLookAndFeel();
		
		// Create UI components
		imagePanel = new ImagePanel();		
		slider = new JSlider(SwingConstants.HORIZONTAL);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(50);
		slider.setMaximum(100);
		slider.setValue(100);
		slider.addChangeListener(new ChangeListener() {			
			public void stateChanged(ChangeEvent e) {		
				if(!slider.isEnabled())
					return;
				
				if(
						(slider.getValueIsAdjusting() && isRealtime) || 
						(!slider.getValueIsAdjusting())
						)
					resizeImage((slider.getValue()));
			}
		});	
		
		// Add UI components
		this.getContentPane().setLayout(new BorderLayout());	
		this.getContentPane().add(imagePanel,BorderLayout.CENTER);
		this.getContentPane().add(slider,BorderLayout.SOUTH);
		
		// Handle window events 
	    this.addWindowListener(new WindowAdapter() {		
	        public void windowClosing(WindowEvent we){
	        	System.exit(1);
	        }
	      });
					    
	    // Add top menu
	    JMenuBar menuBar = new JMenuBar();	 
	    menuBar.add(createFileMenu());
	    menuBar.add(createAlgorithmMenu());
	    menuBar.add(createWindowMenu());	    	    
	    menuBar.add(createHelpMenu());
	    this.setJMenuBar(menuBar);

	    this.setResizable(false);
		this.pack();
	
		// Create display frames
		grayscaleFrame = new ImageFrame("Grayscale");
		edgeFrame = new ImageFrame("Edges");

		// Init model
		this.grayscaleMethod = GrayscaleMethod.average;
		this.seamCarver = new SeamCarver();
	}

	/**
	 * Create the File->open/File->save menu item.
	 * @return
	 */
	protected JMenu createFileMenu() {
	    JMenu fileMenu = new JMenu("File");
	    fileMenu.setMnemonic(KeyEvent.VK_F);
	    
	    JMenuItem fileOpen = new JMenuItem("Open...", KeyEvent.VK_O);
	    fileOpen.setAccelerator(
	    		KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	    fileMenu.add(fileOpen);
	    	    	    
	    fileOpen.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				showFileOpenDialog();			
			}
		});
	    
	    JMenuItem menuItem = new JMenuItem("Save As...", KeyEvent.VK_A);
	    menuItem.setAccelerator(
	    		KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));	    
	    fileMenu.add(menuItem);
	    menuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				showFileSaveDialog();			
			}
		});	    	    
	    return fileMenu;
	}

	protected JMenu createWindowMenu() {
	    JMenu viewMenu = new JMenu("Window");
	    viewMenu.setMnemonic(KeyEvent.VK_W);
		
		JCheckBoxMenuItem grayscaleItem = new JCheckBoxMenuItem("Grayscale");
		grayscaleItem.setAccelerator(
	    		KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));	    		
	    viewMenu.add(grayscaleItem);
	    grayscaleItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem chk = (JCheckBoxMenuItem) e.getSource();
				chk.setSelected(chk.isSelected());
				grayscaleFrame.setVisible(chk.isSelected());
			}
		});
	    
	    JCheckBoxMenuItem edgeItem = new JCheckBoxMenuItem("Edges");
	    edgeItem.setAccelerator(
	    		KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));	    		    
	    viewMenu.add(edgeItem);	    	    	    
	    edgeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem chk = (JCheckBoxMenuItem) e.getSource();
				chk.setSelected(chk.isSelected());
				edgeFrame.setVisible(chk.isSelected());
				if(chk.isSelected())
					edgeFrame.showImage(seamCarver.getEdgeImage());
			}
		});
	    
	    return viewMenu;
	}
	
	/**
	 * Creates the menu which handles the scaling operations
	 * @return An initialized menu
	 */
	protected JMenu createAlgorithmMenu() {
		JMenu menu = new JMenu("Algorithm");
		menu.setMnemonic(KeyEvent.VK_A);
		
		ButtonGroup group = new ButtonGroup();
		
		ActionListener grayscaleChanged = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				grayscaleMethod = GrayscaleMethod.valueOf(e.getActionCommand());
				resetModel();
			}
		}; 	

		JMenu grayscaleMenu = new JMenu("rgb2gray");
		grayscaleMenu.setMnemonic(KeyEvent.VK_G);
		menu.add(grayscaleMenu);		
		
		boolean isFirst = true;
		for(GrayscaleMethod method:GrayscaleMethod.values()) {

			JRadioButtonMenuItem grayscaleItem = new JRadioButtonMenuItem();
			grayscaleItem.setText(method.toString());
			grayscaleItem.setActionCommand(method.toString());							
			grayscaleItem.setSelected(isFirst);
			grayscaleItem.addActionListener(grayscaleChanged);
			group.add(grayscaleItem);
			grayscaleMenu.add(grayscaleItem);			
			
			isFirst = false;			
		}
		
		final JCheckBoxMenuItem chkRealtime = new JCheckBoxMenuItem();
		chkRealtime.setText("Real-time");
		chkRealtime.addActionListener(new ActionListener() {
					
			public void actionPerformed(ActionEvent arg0) {
				isRealtime = chkRealtime.isSelected();
				resetModel();				
			}
		});
		menu.add(chkRealtime);		
		
		return menu;
	}
	
	/**
	 * Create a simple help menu to access the About dialog
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
	 * Should be called after construction. Sets default input image and about message
	 * @param img An image to load when just starting the app 
	 * @param aboutMessage A message to appear in the about dialog
	 */
	public void initialize(BufferedImage img, String aboutMessage) {		
		this.aboutMessage = aboutMessage;
		this.setRawImage(img);
		resetModel();		
	}

	/**
	 * Clears model and reinitializes it with model parameters. In addition 
	 * resets the slider.      
	 *
	 */	
	protected void resetModel() {
		
		if(rawImage != null) {
			// This is a heavy computation that may take a while (when isRealtime is true)
			seamCarver.init(rawImage,isRealtime,grayscaleMethod);
			
			slider.setEnabled(false);
			slider.setMaximum(rawImage.getWidth());
			slider.setValue(rawImage.getWidth()); 		
			slider.setEnabled(true);
			
			grayscaleFrame.showImage(seamCarver.getGrayscaleImage());

			imagePanel.setImage(rawImage);
			imagePanel.repaint();		
			
			// Should display original images
			//resizeImage(rawImage.getWidth());
		}
		
		if(isNewImage) {
			this.pack();			
			updateScreenPosition();
			isNewImage = false;
		}
	}
			
	/**
	 * Calls the re-scaling algorithm to resize the image and display it
	 * @param targetWidth
	 */
	protected void resizeImage(int targetWidth) {
		if(rawImage == null || seamCarver == null)
			return;
		
		seamCarver.resize(targetWidth);
		
		if(edgeFrame.isVisible())
			edgeFrame.showImage(seamCarver.getEdgeImage());
		
		imagePanel.setImage(seamCarver.getResizedImage());
		imagePanel.repaint();		
		System.out.println("Rescale: " + targetWidth);
	}

	/**
	 * Sets raw image and update scaler about new width
	 * @param rawImage
	 */
	protected void setRawImage(BufferedImage rawImage) {
		this.rawImage = rawImage;
		isNewImage = true;
	}
	
	/**
	 * Shows a "file open" dialog. Natively supports PNG file format and
	 * other formats as well (jpg, bmp,...). If image is valid then sets 
	 * it as new input image and displays it.
	 *
	 */	
	protected void showFileOpenDialog() {
		JFileChooser fd = new JFileChooser();
		
		fd.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "bmp"));
		fd.showOpenDialog(this);		
		
		File file = fd.getSelectedFile(); 
		
		if(file != null) {			
			try {
				// Create BufferedImage from file
				setRawImage(ImageIO.read(file));
				
				resetModel();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Shows a "file save" dialog. Natively supports PNG file format and
	 * other formats as well (jpg, bmp,...). Saves currently displayed image.
	 *
	 */
	protected void showFileSaveDialog() {
		JFileChooser fd = new JFileChooser();
				
		fd.setFileFilter(new FileNameExtensionFilter("png", "png"));
		fd.showSaveDialog(this);		
		
		File file = fd.getSelectedFile(); 
		
		if(file != null) {			
			try {
				ImageIO.write(this.imagePanel.getImage(), "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
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

		Dimension screenSize =
            Toolkit.getDefaultToolkit().getScreenSize();
		
          Dimension labelSize = this.getSize();          
          this.setLocation(screenSize.width/2 - (labelSize.width/2),
                      screenSize.height/2 - (labelSize.height/2));

          this.edgeFrame.setImageSize(this.imagePanel.getPreferredSize());
          this.grayscaleFrame.setImageSize(this.imagePanel.getSize());
          
          this.edgeFrame.setLocation(this.getX()-this.getWidth(), this.getY()+this.getContentPane().getY());
          this.grayscaleFrame.setLocation(this.getX()+this.getWidth(), this.getY()+this.getContentPane().getY());
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
		return rawImage;
	}
}
