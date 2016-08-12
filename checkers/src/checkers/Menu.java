package checkers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class Menu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField id;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Menu frame = new Menu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
		JButton start = new JButton("Start");
		JButton cancel = new JButton("Cancel");
		
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				setVisible(false);
				dispose();
			}
		});
		
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
	    JRadioButton host = new JRadioButton("Host", true);
	    JRadioButton join = new JRadioButton("Join", false);
	    
	    ButtonGroup group = new ButtonGroup();
	    group.add(host);
	    group.add(join);
	    
	    JPanel radioPanel = new JPanel(new GridLayout(0, 1));
	    radioPanel.setPreferredSize(new Dimension(400, 120));
	    radioPanel.add(host);
	    radioPanel.add(join);
	    
	    contentPane.add(radioPanel, BorderLayout.NORTH);
	}
	
	public void deployText() {
		TitledBorder titledBorder = new TitledBorder("Unique ID required");
		JPanel textPanel = new JPanel();
		id = new JTextField();
		id.setPreferredSize(new Dimension(100, 30));
		
		textPanel.add(id, BorderLayout.CENTER);
		textPanel.setBorder(titledBorder);
		contentPane.add(textPanel, BorderLayout.CENTER);
	}

}
