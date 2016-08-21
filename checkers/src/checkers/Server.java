package checkers;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
  * A fairly generic server class that listens on port 9879; waits for clients to connect, then
  * passes the client's socket off to a ClientHandler object in its own thread
  */
public class Server {
	private int port = 22222;
	private final int portMin = 1024;
	private final int portMax = 65535;
	private String ip = "localhost";
	private ServerSocket serverSocket; 
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	private boolean connection = false;
	private boolean yourTurn = false;
	
	public int getPort(){
		return this.port;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public String getIp(){
		return this.ip;
	}
	
	public DataOutputStream getDos(){
		return this.dos;
	}
	
	public void setDos(DataOutputStream dos){
		this.dos = dos;
	}
	
	public DataInputStream getDis(){
		return this.dis;
	}
	
	public void setDis(DataInputStream dis){
		this.dis = dis;
	}
	
	public ServerSocket getServerSocket(){
		return this.serverSocket;
	}
	
	public boolean getConnection(){
		return this.connection;
	}
	
	public void setConnection(boolean connection){
		this.connection = connection;
	}
	
	public boolean getYourTurn(){
		return this.yourTurn;
	}
	
	public void setYourTurn(boolean yourTurn){
		this.yourTurn = yourTurn;
	}
	
	public Boolean createServerSocket(){
		try {
			createRandomPort();
			this.serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
			connection = true;
			yourTurn = true;
			System.out.println("Waiting for other opponent to join");
		}
		catch (IOException e) {
			System.out.println("The server is currently busy");
			return false;
		}
		return true;
	}
	
	public Boolean connectToServer(){
		try {
			socket = new Socket(this.ip, this.port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			connection = true;
		} catch (IOException e) {
			System.out.println("Please check your ID or host the game.");
			return false;
		}
		System.out.println("Successfully connected to the server.");
		return true;
	}
	
	public boolean listenServerBeginning() {
		socket = null;
		try {
			socket = serverSocket.accept();
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			System.out.println("Other opponent joined the room.");
			serverSocket.close();
			return true;
		} catch (IOException e) {
			System.out.println("error occured");
			return false;
		}
	}
	
	public void createRandomPort(){
		Random randomNumber = new Random();
		this.port = randomNumber.nextInt(this.portMax - this.portMin + 1) + this.portMin;
	}
}
