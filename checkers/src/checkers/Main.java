package checkers;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Main implements Runnable{

	private Thread thread;
	private Server server = new Server();
	private Menu frame = new Menu();
	private boolean connection = false;
	private boolean yourTurn = false;
	
	public static void main(String[] args) {
		Main main = new Main();
	}

	public Main() {
		frame.setVisible(true);
		System.out.println(frame.getGameType());
		frame.getBtnStartButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (frame.getGameType().equals("Host")) {
					if (server.createServerSocket())
					{
						System.out.println("Waiting for other opponent to join");
						yourTurn = true;
						connection = true;
					}
					else{
						System.out.println("The server is currently busy");
					}
				} else if (frame.getGameType().equals("Join"))
				{
					if(server.connectToServer()){
						connection = true;
					}
					else{
						System.out.println("failed");
					}
				}
			}
		});
		while(true){
			try {
				if(connection){
					break;
				}
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
			}
			
		}
		thread = new Thread(this, "Main");
		thread.start();
	}

	@Override
public void run() {
		if(!yourTurn){
			Board board = new Board();
		}
		while(true){
			try {
				if(yourTurn){
					if(server.listenForServerRequest()){
						Board board = new Board();
						break;
					}
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
		}
	}
	
public void initalSetup(){
	
}
}

