import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class Server {

	private static int uniqueId;
	private ArrayList<ClientThread> al;
	private ServerGUI sg;
	private SimpleDateFormat sdf;
	private int port;
	private boolean keepGoing;
	
	public Server(int port) {
		this(port, null);
	}
	
	public Server (int port, ServerGUI sg) {
		this.sg = sg;
		this.port = port;
		sdf = new SimpleDateFormat("HH:mm:ss");
		al = new ArrayList<ClientThread>();
	}
	
	public void start() {
		keepGoing = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (keepGoing) {
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();
				
				if (!keepGoing) 
					break;
				ClientThread t = new ClientThread(socket);
				al.add(t);
				t.start();
			}
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					} catch(IOException ioE){
						
					}
				}
			}
			catch (Exception e) {
				display("Exception closing the server and clients: " + e);
				
			}
		}
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
		}
	}
	
	protected void stop () {
		keepGoing = false;
		try {
			new Socket("localhost", port);
		} catch(Exception e){
			
		}
	
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if (sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}
	
	private synchronized void broadcast(String message) {
		String time = sdf.format(new Date());
		String messageLF = time + " " + message + "\n";
		
		if (sg == null)
			System.out.print(messageLF);
		else
			sp.appenRoom(messageLF);
		
		for(int i al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			if (!ct.writeMsg(messageLF)){
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}
	
	synchronized void remove (int id) {
		for (int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			if (ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	
	public static void main (String[] args) {
		int portNumber = 1500;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				} catch (Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
		}
		Server server = new Server(portNumber);
		server.start();
	}
	
	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		
		int id;
		String username;
		ChatMessage cm;
		String date;
	}
		
	}
	
}
