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
import javax.swing.JButton;
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
public class MainFrame extends JFrame 
{
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
	
	/**
	 * Create Frame GUI
	 *
	 */
	public MainFrame()
	{
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
	    this.addWindowListener(new WindowAdapter()
	    {		
	        public void windowClosing(WindowEvent we)
	        {
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
	}

	/**
	 * Create the File->open/File->save menu item.
	 * @return
	 */
	protected JMenu createFileMenu()
	{
	    JMenu fileMenu = new JMenu("File");
	    fileMenu.setMnemonic(KeyEvent.VK_F);
	    
	    JMenuItem fileOpen = new JMenuItem("Open...", KeyEvent.VK_O);
	    fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	    fileMenu.add(fileOpen);
	    	    	    
	    fileOpen.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{
				showFileOpenDialog();			
			}
		});
	    
	    JMenuItem menuItem = new JMenuItem("Save As...", KeyEvent.VK_A);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));	    
	    fileMenu.add(menuItem);
	    menuItem.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{
				showFileSaveDialog();			
			}
		});	    	    
	    return fileMenu;
	}
	
	/**
	 * Creates the menu which handles the scaling operations
	 * @return An initialized menu
	 */
	protected JMenu createActionMenu()
	{
		JMenu menu = new JMenu("Filters");
		menu.setMnemonic(KeyEvent.VK_F);		
		
		JMenuItem edgeItem = new JMenuItem("Edges");
	    edgeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));	    		    
	    menu.add(edgeItem);	    	    	    
	    edgeItem.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{
				m_EdgeFrame.setVisible(true);
				if(m_EdgeFrame.isVisible())
				{
					m_EdgeFrame.showImage(m_biliteralSmoother.getEdgeImage());
				}
			}
		});
		menu.add(edgeItem);
		
		JMenuItem grayscaleItem = new JMenuItem("Grayscale");
		grayscaleItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));	    		
		menu.add(grayscaleItem);
	    grayscaleItem.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{
				m_GrayscaleFrame.setVisible(true);
			}
		});
	    menu.add(grayscaleItem);
	    
	    JMenuItem Smooth = new JMenuItem("Gaussian Smooth");
	    Smooth.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));	    		
		menu.add(Smooth);
		Smooth.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{
				m_SmoothFrame.setVisible(true);
				if(m_SmoothFrame.isVisible())
				{
					m_SmoothFrame.showImage(ImageProcessor.convolve(m_RawImage, ImageProcessor.gaussianBlur));
				}
			}
		});
	    menu.add(grayscaleItem);

	    final JMenuItem chkSmooth = new JMenuItem();
		chkSmooth.setText("Biliteral Smooth");
		chkSmooth.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
		
		JMenuBar menuBar = new JMenuBar();	 
	    menuBar.add(createFileMenu());
	    
		chkSmooth.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				m_BiliteralSmoothFrame.setVisible(true);
				if(m_BiliteralSmoothFrame.isVisible())
				{
					 // button
				    JButton go = new JButton("Go!");
				    
				    // Sigma
				    JTextField sigmaLabel = new JTextField("Enter 'Sigma' here", 15);
				    sigmaLabel.setEditable(false);
				    
				    final JTextField sigmaText = new JTextField(10);
				    
				    JTextField iterationsLabel = new JTextField("Number of iterations", 15);
				    iterationsLabel.setEditable(false);
				    
				    final JTextField iterationsText = new JTextField(10);
				    
					if(firstTime)
					{
						m_Settings.setResizable(false);
					    m_Settings.pack();
					    
					    m_Settings.add(sigmaLabel);
					    m_Settings.add(sigmaText);
					    m_Settings.add(iterationsLabel);
					    m_Settings.add(iterationsText);
					    m_Settings.add(go);
					    m_Settings.setSize(300, 90);
					    
					    m_Settings.setVisible(true);
					    firstTime = false;
					}
					else
					{
						m_Settings.setVisible(true);
					}
				    
					// Post the event from button
				    go.addActionListener(new ActionListener()
				    {
						public void actionPerformed(ActionEvent e)
						{
							int sigma = 3;
							int iterations = 1;
							
							try
							{
								sigma = Integer.parseInt(sigmaText.getText());
								iterations = Integer.parseInt(iterationsText.getText());
							}
							catch (NumberFormatException nfe)
							{
								sigmaText.setText("");
								iterationsText.setText("");
							}
							
							if(sigma > 0 && sigma % 2 == 1 && sigma < 17)
							{	
								m_Settings.setVisible(false);
								m_ImageToSave = ImageProcessor.SmoothImage(m_RawImage, sigma, iterations);
								m_BiliteralSmoothFrame.showImage(m_ImageToSave);
							}
						}
					});
				}
				resetModel();				
			}
		});
		menu.add(chkSmooth);	
		
		final JMenuItem chkAddEdges = new JMenuItem();
		chkAddEdges.setText("Add Edges");
		chkAddEdges.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		chkAddEdges.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if(m_BiliteralSmoothFrame.isVisible())
				{
					m_BiliteralSmoothFrame.showImage(ImageProcessor.addEdges(m_biliteralSmoother.getImage(), m_biliteralSmoother.getEdgeImage()));
					resetModel();
				}
			}
		});
		menu.add(chkAddEdges);
		
		return menu;
	}
	
	/**
	 * Create a simple help menu to access the About dialog
	 * @return Menu
	 */
	protected JMenu createHelpMenu()
	{
		JMenu menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		
		JMenuItem menuItem = new JMenuItem();
		menuItem.setText("About");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, ActionEvent.CTRL_MASK));
		menuItem.setMnemonic(KeyEvent.VK_A);
		
		menu.add(menuItem);
		
		menuItem.addActionListener(new ActionListener()
		{				
			public void actionPerformed(ActionEvent e)
			{
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
		this.m_aboutMessage = aboutMessage;
		this.setRawImage(img);
		resetModel();		
	}

	/**
	 * Clears model and reinitializes it with model parameters. In addition 
	 * resets the slider.      
	 *
	 */	
	protected void resetModel()
	{
		if(m_RawImage != null)
		{
			m_biliteralSmoother.init(m_RawImage);
			
			m_GrayscaleFrame.showImage(m_biliteralSmoother.getGrayscaleImage());

			m_ImagePanel.setImage(m_RawImage);
			m_ImagePanel.repaint();
		}
		
		if(m_IsNewImage)
		{
			this.pack();			
			updateScreenPosition();
			m_IsNewImage = false;
		}
	}
			
	/**
	 * Sets raw image and update scaler about new width
	 * @param rawImage
	 */
	protected void setRawImage(BufferedImage rawImage)
	{
		this.m_RawImage = rawImage;
		m_IsNewImage = true;
	}
	
	/**
	 * Shows a "file open" dialog. Natively supports PNG file format and
	 * other formats as well (jpg, bmp,...). If image is valid then sets 
	 * it as new input image and displays it.
	 *
	 */	
	protected void showFileOpenDialog()
	{
		JFileChooser fd = new JFileChooser();
		
		fd.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "bmp"));
		fd.showOpenDialog(this);		
		
		File file = fd.getSelectedFile(); 
		
		if(file != null)
		{			
			try
			{
				// Create BufferedImage from file
				setRawImage(ImageIO.read(file));
				
				resetModel();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Shows a "file save" dialog. Natively supports PNG file format and
	 * other formats as well (jpg, bmp,...). Saves currently displayed image.
	 *
	 */
	protected void showFileSaveDialog()
	{
		JFileChooser fd = new JFileChooser();
				
		fd.setFileFilter(new FileNameExtensionFilter("png", "png"));
		fd.showSaveDialog(this);		
		
		File file = fd.getSelectedFile();
		
		if(file != null)
		{
			try
			{
				ImageIO.write(this.m_ImageToSave, "png", file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Shows the about dialog
	 */
	protected void showHelpAboutDialog()
	{
		JOptionPane.showMessageDialog(this, m_aboutMessage, "About", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Places the frame at the center of the screen
	 */
	protected void updateScreenPosition()
	{
		Dimension screenSize =
            Toolkit.getDefaultToolkit().getScreenSize();
		
          Dimension labelSize = this.getSize();          
          this.setLocation(screenSize.width / 2 - (labelSize.width/2),
                      screenSize.height / 2 - (labelSize.height/2));

          this.m_EdgeFrame.setImageSize(this.m_ImagePanel.getPreferredSize());
          this.m_GrayscaleFrame.setImageSize(this.m_ImagePanel.getSize());
          
          this.m_EdgeFrame.setLocation(this.getX() - this.getWidth(), this.getY() + this.getContentPane().getY());
          this.m_GrayscaleFrame.setLocation(this.getX() + this.getWidth(), this.getY() + this.getContentPane().getY());
	}

	/**
	 * Tells Swing to paint GUI as if it is native (i.e. uses OS GUI style) 
	 */
	public static void setNativeLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			System.out.println("Error setting native LAF: " + e);
		}
	}

	protected BufferedImage getRawImage()
	{
		return m_RawImage;
	}
}