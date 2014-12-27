import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

import org.lightcouch.CouchDbClient;
import com.google.gson.JsonObject;
/**
 * send() parse()
 * 
 * 
 * 
 * @author Hao
 * 
 */
public class NetGameClient {
	Random r;
	int bufferSize = 1024 * 64;
	Socket s = null;
	private int udpPort;
	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	private TankClient tc;
	DatagramSocket ds = null;
	CouchDbClient dbBackupServer = new CouchDbClient("couchdb2.properties");

	// List<TankWarBackupServer> BackupServerList = new
	// ArrayList<TankWarBackupServer>();

	public NetGameClient(TankClient tc) {
		this.tc = tc;
		
	}

	boolean exist = false;

	public void connectToServer(String IP, int TCPport) {// TCP connection
		try {// mainserver IP
			try {
				ds = new DatagramSocket(udpPort);// new DatagramSocket
			} catch (SocketException e) {
				e.printStackTrace();
			}
			s = new Socket(IP, TCPport);
			DataOutputStream output = new DataOutputStream(s.getOutputStream());
			DataInputStream input = new DataInputStream(s.getInputStream());
			output.writeInt(udpPort);
			int ID = input.readInt();
			System.out.println("READING CLIENTID " + ID);

			if (ID == 0) {
				List<JsonObject> allDocsServer = dbBackupServer.view(
						"_all_docs").includeDocs(true).query(JsonObject.class);

				int pickTCPPort = Integer.parseInt(allDocsServer.get(0).get(
						"TCPPORT").toString());
				int pickUDPPort = Integer.parseInt(allDocsServer.get(0).get(
						"UDP_PORT").toString());
				String pickIP = allDocsServer.get(0).get("ServerIP").toString()
						.replace("\"", "");
				int ServerID = Integer.parseInt(allDocsServer.get(0).get(
						"ServerID").toString());
				System.out.println("ServerID " + ServerID + "pickTCPPort "
						+ pickTCPPort + "pickUDPPort " + pickUDPPort
						+ "pickIP " + pickIP);

				System.out.println("netgameclient77 " + "ServerID " + ServerID
						+ "pickTCPPort " + pickTCPPort + "pickUDPPort "
						+ pickUDPPort + "pickIP " + pickIP);
				connectToServer(pickIP, pickTCPPort);// change to connect to
														// backupserver
				System.out.println("netGameClient new connectTo backup");
				exist = false;
			} else {
				tc.myTank.ID = ID;
				if(ID==100){tc.myTank.good = false;tc.myTank.isPlayerOne = true;System.out.println("100");}
				if(ID==101){tc.myTank.good = false;tc.myTank.isPlayerOne = false;System.out.println("101");}
				if(ID==102){tc.myTank.good = true;tc.myTank.isPlayerTwo = true;System.out.println("102");}
				if(ID==103){tc.myTank.good = true;tc.myTank.isPlayerTwo = false;System.out.println("103");}
				if(ID==104){tc.myTank.good = false;tc.myTank.isPlayerOne = true;System.out.println("104");}
				if(ID==105){tc.myTank.good = false;tc.myTank.isPlayerOne = false;System.out.println("105");}
				if(ID==106){tc.myTank.good = true;tc.myTank.isPlayerTwo = true;System.out.println("106");}
				if(ID==107){tc.myTank.good = true;tc.myTank.isPlayerTwo = false;System.out.println("107");}
				
				if(ID==500){tc.myTank.good = true;tc.myTank.isPlayerTwo = false;System.out.println("500");}
				if(ID==501){tc.myTank.good = false;tc.myTank.isPlayerTwo = true;System.out.println("500");}
				
				exist = true;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("unknown Host error.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("77NetGameClient rejection");
		} finally {
			if (s != null) {
				try {
					s.close();
					s = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (exist) {
			new Thread(new ClientReceiveUDPMessageThread()).start();
			System.out.println(tc.myTank.ID);
			TankNewMessage tm = new TankNewMessage(tc.myTank);
			System.out.println(tm.messageTypeID);
			System.out.println(tm.tank.ID);
			send(tm);// 把信息封装成类 UDP发送
			System.out.println("new tank send");

			exist = false;
		}
	}// end of connect()

	public void send(Message message) {// 使用多态 只要实现了message接口 不管message什么类型
		// 调用send方法发出去
		message.send(ds, TankWarMainServer.MainServerIP,
				TankWarMainServer.UDP_PORT);
		List<JsonObject> allDocsServer = dbBackupServer.view("_all_docs")
				.includeDocs(true).query(JsonObject.class);
		for (int i = 0; i < allDocsServer.size(); i++) {
			String pickIP = allDocsServer.get(0).get("ServerIP").toString()
					.replace("\"", "");
			int pickUDPPort = Integer.parseInt(allDocsServer.get(0).get(
					"UDP_PORT").toString());
			message.send(ds, pickIP, pickUDPPort);
			System.out.println("netgameclient135 send message to backup");
		}
		System.out.println("netgameclient137 send message to main");
	}

	private class ClientReceiveUDPMessageThread implements Runnable {
		// UDP的接收线程，接收send的包
		byte[] buffer = new byte[bufferSize];

		public void run() {
			while (ds != null) {
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				try {
					ds.receive(dp);
					parse(dp);
				} catch (IOException e) {
					e.printStackTrace();
				}// Receives a datagram packet from this socket

			}
		}

		private void parse(DatagramPacket dp) {
			ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(
					buffer, 0, dp.getLength());
			DataInputStream dataInput = new DataInputStream(byteArrayInput);

			try {
				int messageType = dataInput.readInt();// 根据message类型再分类处理。
				System.out.println("messageType: " + messageType);
				Message message = null;// 后面调用的是接口的parse方法，根据new
				// message的type调用不同的parse
				switch (messageType) {
				case Message.TANK_NEW_MESSAGE:
					message = new TankNewMessage(NetGameClient.this.tc);// parse（）是netClient内部类的方法，不能直接用this.tc.需要先NetClient.this
					message.parse(dataInput);// MESSAGE
					// 自己最了解message内容，留给他自己分析。面向对象理论。
					break;
				case Message.TANK_MOVE_MESSAGE:
					message = new TankMoveMessage(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.MISSILE_MESSAGE:
					message = new MissileMessage(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.TANK_DEAD_MESSAGE:
					message = new TankDeadMessage(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.MISSILE_DEAD_MESSAGE:
					message = new MissileDeadMessage(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.BLOOD_MESSAGE:
					message = new BloodMessage(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.BLOOD_RECOVERY_MESSAGE:
					message = new BloodRecoveryMessage(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.TILE_DEAD_MESSAGE:
					message = new TILE_DEAD_MESSAGE(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.TANK_SCORE_MESSAGE:
					message = new TankScoreMessage(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.MISSILE_RECOVERY_MESSAGE:
					message = new MissileRecoveryMessage(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.SECOND_MISSILE_RECOVERY_MESSAGE:
					message = new MissileRecovery2Message(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.SORE_ADDING_MESSAGE:
					message = new ScoringAddingMessage(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
				case Message.SPEED_UP_MESSAGE:
					message = new SpeedUpMessage(NetGameClient.this.tc);
					message.parse(dataInput);
					break;
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
