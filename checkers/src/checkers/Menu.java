package checkers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * This is a Menu class for JFrame Menu Screen
 */
public class Menu extends JFrame implements Serializable{

	// Attribute for serialVersionUID
	private static final long serialVersionUID = 1L;
	
	// Attribute for JFrame
	private JPanel contentPane;
	private JTextField id;
	private JPanel textPanel;
	private ButtonGroup hostJoinGroup = new ButtonGroup();
	private JButton start = new JButton("Start");
	private JButton cancel = new JButton("Cancel");

	// Method to get the GameID from the textBox
	public String getGameID(){
		return this.id.getText();
	}
	
	// Method to get the GameID from the textBox
	public Integer getGameIDInt() {
		Integer val = null;
		try {
			val = Integer.parseInt(getGameID());
		} catch (NumberFormatException nfe) {
		}
		return val;
	}
	
	// Method to get the game type from the radio button
	public String getGameType(){
		for (Enumeration<AbstractButton> buttons = hostJoinGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
	}
	
	// Get Method for JButton start
	public JButton getStartButton(){
		return this.start;
	}
	
	// Get Method for JButton cancel
	public JButton getCancelButton(){
		return this.cancel;
	}

	/*
	 * Create the frame.
	 */
	public Menu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 320);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		deploy();
	}
	
	public void deployMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String releaseNote = "Release Note : \n"
						+ "6/25/2016   version 1.0\n"
						+ "Initial Version\n"
						+ "7/29/2016   version 1.1\n"
						+ "Revisions from TA's feedback. Fixed minor issues.\nAdded interface design figures, and use case diagram.\n"
						+ "8/20/2016 version 1.2\n"
						+ "First player start the game by selecting host and \nstart the game, then tell the opponent the unique ID.\n"
						+ "Then the oponent use the unique ID to join.\n"
						;
				JOptionPane.showOptionDialog(null, releaseNote,"Release Note", JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
			}
		});
		help.add(about);
		menuBar.add(help);
		this.setJMenuBar(menuBar);
	}
	
	public void deploy() {
		deployButton();
		deployRadioButton();
		deployText();
		deployMenu();
	}
	
	public void deployButton() {
		JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
		buttonPanel.setPreferredSize(new Dimension(400, 50));
		start = new JButton("Start");
		cancel = new JButton("Cancel");
		
		// Adding acctionListener for Cancel
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(start.isEnabled()){
					System.exit(0);
				}
				else{
					start.setEnabled(true);
				}
			}
		});
		buttonPanel.add(start);
		buttonPanel.add(cancel);
		
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public void deployRadioButton() {
	    JRadioButton rdbtnHostButton = new JRadioButton("Host", true);
	    JRadioButton rdbtnJoinButton = new JRadioButton("Join", false);
	    
	    hostJoinGroup.add(rdbtnHostButton);
	    hostJoinGroup.add(rdbtnJoinButton);
	    
	    JPanel radioPanel = new JPanel(new GridLayout(0, 1));
	    radioPanel.setPreferredSize(new Dimension(400, 120));
	    radioPanel.add(rdbtnHostButton);
	    radioPanel.add(rdbtnJoinButton);
	    
	    contentPane.add(radioPanel, BorderLayout.NORTH);
	    
	    // ActionListener when host/join is clicked whether to display uniqueId text field.
	    rdbtnHostButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me){
				textPanel.setVisible(false);
			}
		});
		rdbtnJoinButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me){
				textPanel.setVisible(true);
			}
		});
	}
	
	public void deployText() {
		TitledBorder titledBorder = new TitledBorder("Unique ID required");
		id = new JTextField();
		id.setPreferredSize(new Dimension(100, 30));
		textPanel = new JPanel();
		textPanel.add(id, BorderLayout.CENTER);
		textPanel.setBorder(titledBorder);
		contentPane.add(textPanel, BorderLayout.CENTER);
		textPanel.setVisible(false);
	}

}
