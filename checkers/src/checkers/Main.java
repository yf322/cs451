package checkers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * This is a main class for the main  
 */

public class Main {

	// Attribute for Server class server
	private Server server = new Server();
	
	// Attribute for Jframe Menu
	private Menu frame = new Menu();
	
	// Main Method
	public static void main(String[] args) {
		Main main = new Main();
	}

	// Main constructor
	public Main() {
		frame.setVisible(true);
		System.out.println(frame.getGameType());
		frame.getStartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getGameType().equals("Host")) {
					if (server.createServerSocket()) {
						frame.getStartButton().setEnabled(false);
						startThread();
					}
					JOptionPane.showMessageDialog(frame, "Your unique ID is "
							+ server.getPort());
				} else if (frame.getGameType().equals("Join")) {
					if (frame.getGameIDInt() == null) {
						JOptionPane.showMessageDialog(frame,
								"Please check the unique ID");
					} else {
						server.setPort(frame.getGameIDInt());
						if (server.connectToServer()) {
							frame.getStartButton().setEnabled(false);
							startThread();
						}
					}
				}
			}
		});
	}

	public void startThread(){
		new Thread() {  
			public void run() {
			while (true) {
				try {
					if (server.getConnection()) {
						initalSetup();
						break;
					}
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
			}
			}
		}.start();
	}
	
	public void initalSetup() {
		Board board;
		if (!server.getYourTurn()) {
			board = new Board(server, server.getYourTurn(), server.getPort());
		}
		while (true) {
			try {
				if (server.getYourTurn()) {
					if (server.listenServerBeginning()) {
						board = new Board(server, server.getYourTurn(),
								server.getPort());
						break;
					}
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
