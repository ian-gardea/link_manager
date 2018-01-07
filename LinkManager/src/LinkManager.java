
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This program allows the dynamic creation, and execution
 * of links, and DOS commands. See the README documentation 
 * for a full description of features.
 * 
 * @author Ian Gardea
 * @version 1.0.1
 * 
 */
public class LinkManager {
	private static final String VERSION;    
	private static final String JRE;
	
	private static IniFile INI; // Configuration file that holds customizable settings.
	private static File FILE;
	private static File README;
	
	private static final JFrame FRAME;
	private static final int SLEEPTIME;
	private static final int GUIWIDTH;
	private static final int GUIHEIGHT;
	private static String CUSTOM_VAR;
	
	private static CustomTabList tabbedList = null;
	private static boolean       isLocked;
	
	static {
		// These variables are only allowed to be changed during development.
	    VERSION = "1.0.1"; // TODO: Please update on every subsequent code change.
        JRE = "1.7.0_45";  // TODO: Please update if tested on a later JRE.
        
        // Load other global variables from a configuration file to allow users to change them as needed.
		try {
	        
			INI = new IniFile("./config.ini");
			
			FILE       = new File(INI.getString("global","sessionFilePath", "./session.xml"));
			README     = new File(INI.getString("global","readmeFilePath", "./README.txt"));
		}
		catch (IOException ex) {
			JOptionPane.showMessageDialog(LinkManager.FRAME, "An error occurred reading the INI file." + ex.getLocalizedMessage(),
					"I/O Exception", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch (NullPointerException ex) {
			String iniErrMessage = "An error occurred when trying to load INI file key \"" + INI.getLastKey() + "\".";
			JOptionPane.showMessageDialog(LinkManager.FRAME, iniErrMessage,
					"I/O Exception", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(LinkManager.FRAME, ex.getLocalizedMessage(),
					"Unknown Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		FRAME = new JFrame("Link Manager v" + VERSION);
			
		CUSTOM_VAR = INI.getString("global","customVarValue", "CUSTOM_VAR");
		SLEEPTIME = INI.getInt("gui","sleepTime", 2000);
		GUIWIDTH = INI.getInt("gui","guiWidth", 480);
		GUIHEIGHT = INI.getInt("gui","guiHeight", 600);
	}
	
	/**
	 * Schedules a job for the event-dispatching thread to
	 * create, and show the main GUI.
	 * 
	 */
	public LinkManager() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
	/**
	 * Creates and displays the main GUI.
	 * 
	 */
	private static void createAndShowGUI() {
		LinkManager.isLocked = false;

		// Create Windows look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		// The list of tabs available in the GUI.
		tabbedList = new CustomTabList();

		// Load the existing configuration, or start a blank session if not defined.
		if(FILE.exists()){
			tabbedList.revertDocument(FILE);
		}
		else{
			showInstructions();
		}

		// Main menu
		JMenuBar jmbMain = new JMenuBar();
		FRAME.setJMenuBar( jmbMain );

		// File menu
		final JMenu jmnFile = new JMenu("File");
		jmnFile.setMnemonic(KeyEvent.VK_F);

		final JMenuItem jmiNew = jmnFile.add("New");
		jmiNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));

		final JMenuItem jmiSave = jmnFile.add("Save");
		jmiSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));

		final JMenuItem jmiRevert = jmnFile.add("Reload Session File");
		jmiRevert.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
		
		jmnFile.addSeparator();
		
		final JMenuItem jmiSetVariable = jmnFile.add("Set Custom Variable");
		jmiSetVariable.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, 0));
		
		jmnFile.addSeparator();

		final JMenuItem jmiExit = jmnFile.add("Exit");

		// Edit Menu
		final JMenu jmnEdit = new JMenu("Edit");
		jmnEdit.setMnemonic(KeyEvent.VK_E);

		final JMenuItem jmiLock = jmnEdit.add("Lock");
		jmiLock.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));

		jmnEdit.addSeparator();
		
		final JMenuItem jmiAddTab = jmnEdit.add("Add Tab");
		jmiAddTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK));

		final JMenuItem jmiDeleteTab = jmnEdit.add("Delete Tab");
		jmiDeleteTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
		
		jmnEdit.addSeparator();
		
		final JMenuItem jmiAddLink = jmnEdit.add("Add Link");
		jmiAddLink.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0));

		final JMenuItem jmiAddSeparator = jmnEdit.add("Add Separator");
		jmiAddSeparator.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
		
		jmnEdit.addSeparator();
		
		final JMenuItem jmiRunLinks = jmnEdit.add("Run Selected Links");
		jmiRunLinks.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
		
		final JMenuItem jmiDeleteLinks = jmnEdit.add("Delete Selected Links");
		jmiDeleteLinks.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0));
		
		// Help menu
		final JMenu jmnHelp = new JMenu("Help");
		jmnHelp.setMnemonic(KeyEvent.VK_H);

		final JMenuItem jmiInstructions = jmnHelp.add( "Instructions");
		jmiInstructions.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		jmnHelp.addSeparator();
		final JMenuItem jmiAbout = jmnHelp.add( "About");

		// Add menu bar items
		jmbMain.add(jmnFile);
		jmbMain.add(jmnEdit);
		jmbMain.add(Box.createHorizontalGlue());
		jmbMain.add(jmnHelp);

		// Menu listeners
		jmiNew.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if(!LinkManager.isLocked) {
							tabbedList.newDocument(FILE);
							LinkManager.isLocked = false;
							jmiLock.setText("Lock");
						}
						else {
							LinkManager.showLockedMessage();
						}
					}
				});
		jmiSave.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						tabbedList.saveDocument(FILE);
					}
				});
		jmiRevert.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if(!LinkManager.isLocked) {
							tabbedList.revertDocument(FILE);
						}
						else {
							LinkManager.showLockedMessage();
						}
					}
				});
		
		jmiLock.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if(tabbedList != null) {							
							if(LinkManager.isLocked) {
								TabInputHandler.enableInputListeners(tabbedList);

								for(int i=0; i<tabbedList.getTabList().size(); i++) {
									CustomLinkPane currentTab = tabbedList.getTabList().get(i);

									DropTargetHandler.enableDragListeners(currentTab);
									if(currentTab.getLinkTable() != null) {
										TableTransferHandler.enableTransferListeners(currentTab.getLinkTable(), currentTab);
									}
								}

								jmiLock.setText("Lock");
							}
							else {
								TabInputHandler.disableInputListeners(tabbedList);

								for(int i=0; i<tabbedList.getTabList().size(); i++) {
									CustomLinkPane currentTab = tabbedList.getTabList().get(i);

									DropTargetHandler.disableDragListeners(currentTab);
									if(currentTab.getLinkTable() != null) {
										TableTransferHandler.disableTransferListeners(currentTab.getLinkTable(), currentTab);
									}
								}

								jmiLock.setText("Unlock");
							}
							LinkManager.isLocked = !LinkManager.isLocked;
							for(int i=0; i<tabbedList.getTabList().size(); i++) {
								tabbedList.getTabList().get(i).refresh();
							}
						}
					}
				});
		jmiSetVariable.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if(!LinkManager.isLocked) {
							String input = null;
							
							while(input == null || input.equals("")) {
								input = (String) JOptionPane.showInputDialog(LinkManager.getFrame(), "Please enter a new value for the custom variable."
										, "Enter Variable Name",
										JOptionPane.PLAIN_MESSAGE, null, null, LinkManager.CUSTOM_VAR);

								// If the user hits Cancel
								if (input == null) {
									return;
								} 
							}
							
							LinkManager.CUSTOM_VAR = input;
						}
						else {
							LinkManager.showLockedMessage();
						}
					}
				});	
		jmiExit.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						System.exit(0);
					}
				});
		jmiAddTab.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if(!LinkManager.isLocked) {
							tabbedList.promptAddTab();
						}
						else {
							LinkManager.showLockedMessage();
						}
					}
				});
		jmiDeleteTab.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if(!LinkManager.isLocked) {
							tabbedList.deleteCurrentTab();
						}
						else {
							LinkManager.showLockedMessage();
						}
					}
				});
		jmiAddLink.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if(!LinkManager.isLocked) {
							CustomLinkPane currentTab = tabbedList.getTabList().get(tabbedList.getSelectedIndex());

							currentTab.getLinkList().promptAddLink();	
							currentTab.refresh();
						}
						else {
							LinkManager.showLockedMessage();
						}
					}
				});
		jmiAddSeparator.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if(!LinkManager.isLocked) {
							CustomLinkPane currentTab = tabbedList.getTabList().get(tabbedList.getSelectedIndex());

							currentTab.getLinkList().addSeparator();	
							currentTab.refresh();
						}
						else {
							LinkManager.showLockedMessage();
						}
					}
				});
		jmiDeleteLinks.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if(!LinkManager.isLocked) {
							CustomLinkPane currentTab = tabbedList.getTabList().get(tabbedList.getSelectedIndex());
							currentTab.doDelete();
						}
						else {
							LinkManager.showLockedMessage();
						}
					}
				});
		
		jmiRunLinks.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						if(!LinkManager.isLocked) {
							CustomLinkPane currentTab = tabbedList.getTabList().get(tabbedList.getSelectedIndex());
							currentTab.doRun();
						}
						else {
							LinkManager.showLockedMessage();
						}
					}
				});
		
		jmiInstructions.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						LinkManager.showInstructions();
					}
				});
		jmiAbout.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						LinkManager.showAbout();
					}
				});

		// Add content to the JFrame.
		FRAME.add(tabbedList);

		// Set JFRAME preferences.
		FRAME.setPreferredSize(new Dimension(GUIWIDTH, GUIHEIGHT));
		FRAME.setResizable(false);
		FRAME.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		FRAME.setVisible(true);
		
		// Position the GUI in the middle of the screen when started.
		FRAME.pack();
		FRAME.setLocationRelativeTo(null); 
	}

	/**
	 * Displays the about screen.
	 * 
	 */
	public static void showAbout(){
		JOptionPane.showMessageDialog( 
				FRAME,
				new JLabel(
						"<html><hr><pre style='font-family: consolas, courier new, courier, monospace; font-size: 8.9px;'>"
								+ "<br>Link Manager" 
								+ "<br>"
								+ "<br>Written by: Ian A. Gardea"
								+ "<br>Version: " + LinkManager.VERSION
								+ "<br>"
								+ "<br>Current JRE:     " + System.getProperty("java.version")
								+ "<br>Recommended JRE: " + LinkManager.JRE
								+ "<br>"
								+ "<br></pre><hr></html>"
						),"About Link Manager", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays the instructions screen.
	 * 
	 */
	public static void showInstructions(){		
		JLabel instructions = new JLabel(
				"<html><hr><pre style='font-family: consolas, courier new, courier, monospace; font-size: 8.9px;'>"
						+ "<br>To begin, simply drag and drop links to the main window to build your own, customized"
						+ "<br>list of links. Once created, you can save your configuration to an XML file for"
						+ "<br>future use on subsequent sessions."
						+ "<br><br>See the <a href=\"\">README</a> document for more detailed instructions, and available commands."
						+ "<br><br></pre><hr></html>"
				);

		instructions.setCursor(new Cursor(Cursor.HAND_CURSOR));
		instructions.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						URI uri = Paths.get(README.getCanonicalPath()).toUri();
						uri.normalize();
						Desktop.getDesktop().browse(uri);
					} 
					catch (IOException ex) {
						JOptionPane.showMessageDialog(LinkManager.FRAME, ex.getLocalizedMessage(),
								"I/O Exception", JOptionPane.ERROR_MESSAGE);
					}
					catch (Exception ex) {
						JOptionPane.showMessageDialog(LinkManager.FRAME, ex.getLocalizedMessage(),
								"Unknown Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});


		JOptionPane.showMessageDialog( 
				FRAME,
				instructions,"User Instructions", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays the locked message.
	 * 
	 */
	public static void showLockedMessage() {
		JOptionPane.showMessageDialog(LinkManager.FRAME, "The operation could not be performed because editing is disabled.\n"
				+ "To re-enable editing, select File/Unlock, or press CTRL + L.",
				"Notification", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * The customized variable is a feature that allows the user to
	 * set a single value, and set up a series of dynamic commands that
	 * would use that value.
	 * 
	 * For instance, if the user sets up a series of DOS commands for a 
	 * PING, NSLOOKUP, etc, and those commands share the same value, rather than
	 * changing that value x amount of times, the user can instead use the customer variable
	 * so that only one value needs to be modified.
	 * 
	 * @return - the value of the customized variable.
	 */
	public static String getCustomVariable() {
		return CUSTOM_VAR;
	}
	
	/**
	 * The configuration file is and XML file that is primarily used to track the changes 
	 * that the user makes during their session. When saves, the configuration file is 
	 * generated/updated so that those changes can be recalled on the next session.
	 * 
	 * @return - the configuration file object.
	 */
	public static File getConfigFile() {
		return FILE;
	}

	/**
	 * This program allows the user to "lock" the GUI to prevent accidental changes.
	 * 
	 * @return - the locked state of the GUI.
	 */
	public static boolean isLocked() {
		return isLocked;
	}

	/**
	 * @return the time (in seconds) that the program will wait between link executions.
	 */
	public static int getSleepTime() {
		return LinkManager.SLEEPTIME;
	}
	
	/**
	 * @return the GUI JFRAME.
	 */
	public static JFrame getFrame() {
		return LinkManager.FRAME;
	}

	/**
	 * Defines the main entry point for the program.
	 * 
	 * @param args - unused.
	 */
	public static void main(String[] args){
		new LinkManager();
	}
}
