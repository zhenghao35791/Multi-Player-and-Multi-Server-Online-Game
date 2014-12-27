import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * if the player eat the second missile revocery icon
 * 
 * @author hao
 * 
 */
public class MissileRecovery2Message implements Message {
	int messageTypeID = Message.SECOND_MISSILE_RECOVERY_MESSAGE;
	int ID;
	TankClient tc;

	public MissileRecovery2Message(TankClient tc) {
		this.tc = tc;
	}

	public MissileRecovery2Message(int ID) {
		this.ID = ID;
	}

	public void parse(DataInputStream dataInput) {
		try {
			int ID = dataInput.readInt();
			if (tc.myTank.ID == ID) {
				return;
			}
			for (int i = 0; i < tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if (t.ID == ID) {
					tc.missileRecovery2.setLive(false);
					break;
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
			dataOutput.writeInt(ID);
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