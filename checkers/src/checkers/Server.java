package checkers;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
  * A fairly generic server class that listens on port 9879; waits for clients to connect, then
  * passes the client's socket off to a ClientHandler object in its own thread
  */
public class Server {
	private int port = 22222;
	private String ip = "localhost";
	private ServerSocket serverSocket; 
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	
	public int getPort(){
		return this.port;
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
	
	public Boolean createServerSocket(){
		try {
			this.serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
		}
		catch (IOException e) {
			System.out.println("Couldn't create socket on port " + this.port);
			return false;
		}
		return true;
	}
	
	public Boolean connectToServer(){
		try {
			socket = new Socket(this.ip, this.port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Unable to connect to the address: " + this.ip + ":" + this.port + " | Starting a server");
			return false;
		}
		System.out.println("Successfully connected to the server.");
		return true;
	}
	
	public boolean listenForServerRequest() {
		socket = null;
		try {
			socket = serverSocket.accept();
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			System.out.println("CLIENT HAS REQUESTED TO JOIN, AND WE HAVE ACCEPTED");
			return true;
		} catch (IOException e) {
			System.out.println("error occured");
			return false;
		}
	}
}
