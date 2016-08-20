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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class Menu extends JFrame implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField id;
	private JPanel textPanel;
	private ButtonGroup hostJoinGroup = new ButtonGroup();
	private JButton start = new JButton("Start");
	private JButton cancel = new JButton("Cancel");

	public String getGameID(){
		return this.id.getText();
	}
	
	public String getGameType(){
		for (Enumeration<AbstractButton> buttons = hostJoinGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }

        return null;
	}
	
	public JButton getStartButton(){
		return this.start;
	}
	
	public JButton getCancelButton(){
		return this.cancel;
	}

	/**
	 * Create the frame.
	 */
	public Menu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		deploy();
	}
	
	public void deploy() {
		deployButton();
		deployRadioButton();
		deployText();
	}
	
	public void deployButton() {
		JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
		buttonPanel.setPreferredSize(new Dimension(400, 50));
		start = new JButton("Start");
		cancel = new JButton("Cancel");
		
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
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
