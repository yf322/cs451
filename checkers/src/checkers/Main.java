package checkers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

/**
 * This is a main class for the main  
 */

public class Main {

	private Server server = new Server();
	private Menu frame = new Menu();
	public static void main(String[] args) {
		Main main = new Main();
	}

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
					server.setPort(Integer.valueOf(frame.getGameID()));
					if (server.connectToServer()) {
						frame.getStartButton().setEnabled(false);
						startThread();
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
