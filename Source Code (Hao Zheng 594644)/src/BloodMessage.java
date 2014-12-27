import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
/**
 * if player's life change, then send a BloodMessage
 * @author hao
 *
 */
public class BloodMessage implements Message {
	int messageTypeID = Message.BLOOD_MESSAGE;
	TankClient tc;
	int life;
	int ID;

	public BloodMessage(int ID, int life) {
		this.ID = ID;
		this.life = life;
	}

	public BloodMessage(TankClient tc) {
		this.tc = tc;
	}

	public void parse(DataInputStream dataInput) {
		try {
			int ID = dataInput.readInt();//read playerID
			if (tc.myTank.ID == ID) {
				return;
			}// if ID is yourself tank, then leave this package
			int life = dataInput.readInt();//read life
			boolean exist = false;
			for (int i = 0; i < tc.tanks.size(); i++) {//looking for the player
				Tank t = tc.tanks.get(i);
				if (t.ID == ID) {
					exist = true;
					t.setLife(life);//set new life
					break;
				}
				if (!exist) {
					System.out.println("no found tank");//cannot find same player
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
			dataOutput.writeInt(messageTypeID);//output messageID, playerID and new life
			dataOutput.writeInt(ID);
			dataOutput.writeInt(life);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			DatagramPacket dp = new DatagramPacket(byteArrayOutput
					.toByteArray(), byteArrayOutput.toByteArray().length,
					new InetSocketAddress(IP, udpPort));
			ds.send(dp);//send packet
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
