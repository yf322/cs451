package checkers;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

public class Main{


	private Server server = new Server();
	private Menu frame = new Menu();
	private Thread thread;
	
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
					if(server.createServerSocket()){
						frame.getStartButton().setEnabled(false);
					}
					JOptionPane.showMessageDialog(frame, "Your unique ID is "+ server.getPort());
					frame.getStartButton().setEnabled(true);
				} else if (frame.getGameType().equals("Join"))
				{
					server.setPort(Integer.valueOf(frame.getGameID()));
					if(server.connectToServer()){
						frame.getStartButton().setEnabled(false);
					};
				}
			}
		});
		while(true){
			try {
				if(server.getConnection()){
					initalSetup();
					break;
				}
				Thread.sleep(1000);
			} catch (InterruptedException e1) {}	
		}
	}
	
	
public void initalSetup(){
	Board board;
	if(!server.getYourTurn()){
		board = new Board(server, server.getYourTurn(), server.getPort());
	}
	while(true){
		try {
			if(server.getYourTurn()){
				if(server.listenServerBeginning()){
					board = new Board(server, server.getYourTurn(), server.getPort());
					break;
				}
			}
			Thread.sleep(1000);
		} catch (InterruptedException e) {}}
}
}


