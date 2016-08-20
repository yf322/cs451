package checkers;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
				System.out.println(frame.getGameType());
				if (frame.getGameType().equals("Host")) {
					if (server.createServerSocket())
					{
						System.out.println("Waiting for other opponent to join");
						yourTurn = true;
					}
					else{
						System.out.println("The server is currently busy");
					}
				} else if (frame.getGameType().equals("Join"))
				{
					server.connectToServer();
				}
			}
		});
		if(connection){
			thread = new Thread(this, "Main");
			thread.start();
		}
	}

	@Override
public void run() {
		System.out.println("test");
		server.listenForServerRequest();
	}
}

