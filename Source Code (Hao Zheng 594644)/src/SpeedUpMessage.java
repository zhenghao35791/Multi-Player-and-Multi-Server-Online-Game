import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * send message let others know my speed has changed.
 * 
 * @author hao
 * 
 */
public class SpeedUpMessage implements Message {

	int messageTypeID = Message.SPEED_UP_MESSAGE;
	int ID;
	TankClient tc;

	public SpeedUpMessage(TankClient tc) {
		this.tc = tc;
	}

	public SpeedUpMessage(int ID) {
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
					tc.speedUp.setLive(false);
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
