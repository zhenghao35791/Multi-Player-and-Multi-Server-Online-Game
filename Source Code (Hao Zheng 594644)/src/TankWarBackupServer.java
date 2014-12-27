import java.io.BufferedWriter;
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
import org.lightcouch.CouchDbClient;
import com.google.gson.JsonObject;

/**
 * bakup server, the same founction of main server, but clients firstly try
 * connect to the main server
 * 
 * @author hao
 * 
 */

public class TankWarBackupServer {
	public int backupUDPPort = 3001;
	public int INDIVIDLE_BACKUP_TCP_PORT_FOR_CLIENTS = 9001;
	/**********************************************************************************
	 * backup server max amount of connecting clients and IP
	 ************************************************************************************/
	public static final int MAX_CLIENTS = 20;
	public String BACKUP_SERVER_IP = "127.0.0.1";
	List<TankWarBackupServer> BackupServerList = new ArrayList<TankWarBackupServer>();
	BufferedWriter writer = null;
	public int backupServerID = 0;
	private static int BACKUP_CLIENTID = 500; // initial ID of clients who connect to the backup
												// server.Easy to find that this  client is connecting to backup
	public List<Client> backupClients = new ArrayList<Client>();
	public List<Client> BackupAllClients = new ArrayList<Client>();
	TankWarMainServer ms;
	int bufferSize = 1024 * 64;
	Socket s = null;
	CouchDbClient dbClient = new CouchDbClient("couchdb.properties"); //import two databases
	CouchDbClient dbBackupServer = new CouchDbClient("couchdb2.properties");

	public TankWarBackupServer() {
	}

	public TankWarBackupServer(int serverID, int TCPport, int UDPport, String IP) {
		this.backupServerID = serverID;
		this.INDIVIDLE_BACKUP_TCP_PORT_FOR_CLIENTS = TCPport;
		this.backupUDPPort = UDPport;
		this.BACKUP_SERVER_IP = IP;
	}

	public void start() {
		new Thread(new backupServerUDPReceiveThread()).start();
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(
					INDIVIDLE_BACKUP_TCP_PORT_FOR_CLIENTS);
			while (true) {
				Socket s = serverSocket.accept();
				DataOutputStream output = new DataOutputStream(
						s.getOutputStream());
				DataInputStream input = new DataInputStream(s.getInputStream());
				int udpPort = input.readInt();
				String IP = s.getInetAddress().getHostAddress();
				System.out.println("IP: " + IP + " UDPPORT: " + udpPort);
				Client c = new Client(IP, udpPort);
				output.writeInt(BACKUP_CLIENTID++);
				backupClients.add(c);
				
				// send data to the database
				JsonObject json = new JsonObject();
				json.addProperty("clientID", BACKUP_CLIENTID);
				json.addProperty("IP", IP);
				json.addProperty("udpPort", udpPort);
				dbClient.save(json);
				
				//if bakup server is overloaded
				if (backupClients.size() >= MAX_CLIENTS) {
					System.out.println("backup server clients > max");

					List<JsonObject> allDocsServer = dbBackupServer
							.view("_all_docs").includeDocs(true)
							.query(JsonObject.class);
					// view all docs in couchDb
					for (int i = 0; i < allDocsServer.size(); i++) {
						String TCPPORT = allDocsServer.get(i).get("TCPPORT")
								.toString().replace("\"", "");
						if (TCPPORT
								.equals(INDIVIDLE_BACKUP_TCP_PORT_FOR_CLIENTS)) {
							dbBackupServer.remove(allDocsServer.get(i));
							System.out
									.println("remove overload backup server ");
						} else
							System.out
									.println("didnt find the overload backupserver");
					}
				}
				s.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	};

	public static void main(String[] aregs) {
		TankWarBackupServer backupServer = new TankWarBackupServer();
		backupServer.start();
	}

	class Client {
		String IP;
		int udpPort;
		Socket s;

		public Client(String IP, int udpPort) {
			this.IP = IP;
			this.udpPort = udpPort;
		}
	}

	private class backupServerUDPReceiveThread implements Runnable {
		byte[] buffer = new byte[bufferSize];
		DatagramSocket ds = null;

		public void run() {
			try {
				ds = new DatagramSocket(backupUDPPort);
				System.out.println("backupserver start");
				
				//send bakup server information to the database
				JsonObject json = new JsonObject();
				json.addProperty("ServerID", 1);
				json.addProperty("UDP_PORT", backupUDPPort);
				json.addProperty("TCPPORT",
						INDIVIDLE_BACKUP_TCP_PORT_FOR_CLIENTS);
				json.addProperty("ServerIP", BACKUP_SERVER_IP);
				json.addProperty("MAXCLIENTS", MAX_CLIENTS);
				dbBackupServer.save(json);
				
				//keep receive the packets
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
						int udpPort = Integer.parseInt(allDocs.get(i)
								.get("udpPort").toString());
						Client c = new Client(ip, udpPort);
						BackupAllClients.add(c);
						dp.setSocketAddress(new InetSocketAddress(c.IP,
								c.udpPort));
						ds.send(dp);
						ds.setReceiveBufferSize(bufferSize);
						System.out.println("backup  send ");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
