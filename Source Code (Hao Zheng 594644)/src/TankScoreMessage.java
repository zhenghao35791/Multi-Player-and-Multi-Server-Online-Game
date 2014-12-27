import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * send message if score has changed.
 * 
 * @author hao
 * 
 */

public class TankScoreMessage implements Message {
	int messageTypeID = Message.TANK_SCORE_MESSAGE;
	TankClient tc;
	int tankScore;
	int tankID;

	public TankScoreMessage(int tankID, int tankScore) {
		this.tankID = tankID;
		this.tankScore = tankScore;

	}

	public TankScoreMessage(TankClient tc) {
		this.tc = tc;
	}

	public void parse(DataInputStream dataInput) {

		try {
			int tankID = dataInput.readInt();
			int tankScore = dataInput.readInt();
			if (tc.myTank.ID == tankID) {
				tc.myTank.setScore(tankScore);
				System.out.println("mytankscoreupdate " + tankScore);
				System.out.println("+10 " + tc.myTank.getScore() + 10);
			}

			boolean exist = false;
			for (int i = 0; i < tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if (t.ID == tankID) {
					exist = true;
					t.setScore(tankScore);
					System.out.println("mytankscoreupdate2 " + tankScore);
					System.out.println("+10 2 " + t.getScore() + 10);
					break;
				}
				if (!exist) {
					System.out.println("no found tank");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
		try {
			dataOutput.writeInt(messageTypeID);
			dataOutput.writeInt(tankID);
			dataOutput.writeInt(tankScore);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			DatagramPacket dp = new DatagramPacket(byteArrayOutput
					.toByteArray(), byteArrayOutput.toByteArray().length,
					new InetSocketAddress(IP, udpPort));
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
