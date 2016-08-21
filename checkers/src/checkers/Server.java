package checkers;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 *	This is a Server class that creates a ServerSocket for the server, Socket for the clients.
 *	This ServerSocket, and Socket enable the users to send/receive data simultaneously.
 */

public class Server {
	// The port is set to 22222 in the beginning. It is also used as an unique ID to connect two players.
	private int port = 22222;
	private final int portMin = 1024;
	private final int portMax = 65535;
	
	// The IP environment is set to localhost
	private final String ip = "localhost";
	
	// Server attributes to communicate between the two players.
	private ServerSocket serverSocket; 
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	
	// This connection and turn boolean variables are used in the beginning of game.
	private boolean connection = false;
	private boolean yourTurn = false;
	
	
	// Get/Set method for int port.
	public int getPort(){
		return this.port;
	}
	public void setPort(int port){
		this.port = port;
	}
	
	// Get method for String ip.
	public String getIp(){
		return this.ip;
	}
	
	// Get/Set method for DataOutputStream dos.
	public DataOutputStream getDos(){
		return this.dos;
	}
	public void setDos(DataOutputStream dos){
		this.dos = dos;
	}
	
	// Get/Set method for DataInputStream dis.
	public DataInputStream getDis(){
		return this.dis;
	}
	public void setDis(DataInputStream dis){
		this.dis = dis;
	}
	
	// Get method for ServerSocket serverSocket
	public ServerSocket getServerSocket(){
		return this.serverSocket;
	}
	
	// Get/Set method for boolean connection
	public boolean getConnection(){
		return this.connection;
	}
	public void setConnection(boolean connection){
		this.connection = connection;
	}
	
	// Get/Set method for boolean Turn
	public boolean getYourTurn(){
		return this.yourTurn;
	}
	public void setYourTurn(boolean yourTurn){
		this.yourTurn = yourTurn;
	}
	
	// Get/Set method for Socket socket
	public Socket getSocket(){
		return this.socket;
	}
	public void setSocket(Socket socket){
		this.socket = socket;
	}
	
	// Method to create ServerSocket. This method is used when a player hosts a new game.
	// If ServerSocket is successfully created, it returns true, otherwise false.
	public Boolean createServerSocket(){
		try {
			// Randomize the port value.
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
	
	// Method to connect to the server using the port variable.
	// If it is successfully connected to the server, it returns true, otherwise false.
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
	
	// Accept the socket request from the join player in the beginning.
	// If other player is successfully joined, it returns true otherwise false.
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
	
	// Method to create random number for port.
	// Port is used as an unique Id.
	public void createRandomPort(){
		Random randomNumber = new Random();
		this.port = randomNumber.nextInt(this.portMax - this.portMin + 1) + this.portMin;
	}
}
