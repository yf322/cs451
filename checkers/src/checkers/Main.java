package checkers;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Main implements Runnable{

	private Thread thread;
	private Server server = new Server();
	private Menu frame = new Menu();

	
	public static void main(String[] args) {
		Main main = new Main();
	}

	public Main() {
		frame.setVisible(true);
		System.out.println(frame.getGameType());
		frame.getStartButton().addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getGameType().equals("Host")) {
					server.createServerSocket();
					frame.getStartButton().setEnabled(false);
				} else if (frame.getGameType().equals("Join"))
				{
					if(server.connectToServer()){
						frame.getStartButton().setEnabled(false);
					};
				}
			}
		});
		while(true){
			try {
				if(server.getConnection()){
					thread = new Thread(this, "Main");
					thread.start();
					break;
				}
				Thread.sleep(1000);
			} catch (InterruptedException e1) {}	
		}
	}

	@Override
public void run() {
		initalSetup();
	}
	
public void initalSetup(){
	if(!server.getYourTurn()){
		Board board = new Board();
	}
	while(true){
		try {
			if(server.getYourTurn()){
				if(server.listenServerBeginning()){
					Board board = new Board();
					break;
				}
			}
			Thread.sleep(1000);
		} catch (InterruptedException e) {}}
}
}

