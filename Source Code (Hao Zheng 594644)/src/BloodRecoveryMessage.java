import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * if player eat the BloodRecovery icon, then send this message
 * 
 * @author hao
 * 
 */
public class BloodRecoveryMessage implements Message {
	int messageTypeID = Message.BLOOD_RECOVERY_MESSAGE;
	int ID;
	TankClient tc;

	public BloodRecoveryMessage(TankClient tc) {
		this.tc = tc;
	}

	public BloodRecoveryMessage(int ID) {
		this.ID = ID;
	}

	public void parse(DataInputStream dataInput) {
		try {
			int ID = dataInput.readInt();
			if (tc.myTank.ID == ID) {
				return;
			}// if this player is myself, then return
			for (int i = 0; i < tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if (t.ID == ID) {
					tc.bloodRecovery.setBloodlive(false);// remove the
															// bloodRecovery
															// icon
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
			dataOutput.writeInt(messageTypeID);//send mesageID and playerID
			dataOutput.writeInt(ID);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			DatagramPacket dp = new DatagramPacket(byteArrayOutput
					.toByteArray(), byteArrayOutput.toByteArray().length,
					new InetSocketAddress(IP, udpPort));
			ds.send(dp);//send packet to the exactly address
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
