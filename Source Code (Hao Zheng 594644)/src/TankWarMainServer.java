import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lightcouch.CouchDbClient;

import com.google.gson.JsonObject;

/**
 * the controller of this game
 * 
 * @author hao
 * 
 */
public class TankWarMainServer {
	
	/***********************************************************
	 * client ID start from 100, easy to find this client is connecting to the main server
	 ************************************************************/
	private int ClientID = 100;
	
	/***********************************************************
	 * max amount clients that the main server can handle
	 ************************************************************/
	public static final int MAX_MAINSERVER_CLIENTS = 20;
	public static final int TCP_PORT_FOR_CLIENTS = 9000;
	public static final int UDP_PORT = 3000;
	
	/***********************************************************
	 * main server IP, initial use local host
	 ************************************************************/
	public static final String MainServerIP = "127.0.0.1";
	Random r;
	int id = 1;
	List<TankWarBackupServer> MainServerBackupList = new ArrayList<TankWarBackupServer>();
	CouchDbClient dbClient = new CouchDbClient("couchdb.properties");

	public TankWarMainServer() {
	}

	List<String> AllClientsCopy = new ArrayList<String>();
	List<Client> MainServerClients = new ArrayList<Client>();
	List<Client> AllClients = new ArrayList<Client>();
	int bufferSize = 1024 * 64;

	public void start() {
		new Thread(new UDPServerReceiveThread()).start();
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(TCP_PORT_FOR_CLIENTS);

			//keep receiving the packets
			while (true) {
				Socket s = serverSocket.accept();
				DataOutputStream output = new DataOutputStream(s
						.getOutputStream());
				DataInputStream input = new DataInputStream(s.getInputStream());
				int udpPort = input.readInt();
				String IP = s.getInetAddress().getHostAddress();
				System.out.println("serverIP: " + IP + " serverUDPPORT: "
						+ udpPort);
				//if main server is overloaded, wont accpet the connection and send ID 0 to the client, so the client will know
				if (MainServerClients.size() >= MAX_MAINSERVER_CLIENTS) {
					System.out.println("mianserver clients size >max");
					output.writeInt(0);
				} else {
					//save the client information to the database
					Client c = new Client(IP, udpPort);// udpPort is get from
					output.writeInt(ClientID++);
					MainServerClients.add(c);
					JsonObject json = new JsonObject();
					json.addProperty("clientID", ClientID);
					json.addProperty("IP", IP);
					json.addProperty("udpPort", udpPort);
					dbClient.save(json);
				}
				s.close();
				System.out.println("Main server start");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TankWarMainServer mainServer = new TankWarMainServer();
		mainServer.start();
	}

	private class Client {
		String IP;
		int udpPort;

		public Client(String IP, int udpPort) {
			this.IP = IP;
			this.udpPort = udpPort;
		}
	}

	private class UDPServerReceiveThread implements Runnable {
		byte[] buffer = new byte[bufferSize];
		DatagramSocket ds = null;

		public void run() {
			try {
				ds = new DatagramSocket(UDP_PORT);

				while (ds != null) {
					DatagramPacket dp = new DatagramPacket(buffer,
							buffer.length);
					ds.receive(dp);
					List<JsonObject> allDocs = dbClient.view("_all_docs")
							.includeDocs(true).query(JsonObject.class);
					// view all docs in couchDb
					for (int i = 0; i < allDocs.size(); i++) {
						String ip = allDocs.get(i).get("IP").toString()
								.replace("\"", "");
						int udpPort = Integer.parseInt(allDocs.get(i).get(
								"udpPort").toString());
						Client c = new Client(ip, udpPort);
						AllClients.add(c);
						dp.setSocketAddress(new InetSocketAddress(c.IP,
								c.udpPort));
						ds.send(dp);
						ds.setReceiveBufferSize(bufferSize);
						System.out.println("mainserver send");
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
