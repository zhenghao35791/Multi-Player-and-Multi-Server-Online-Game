import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * if a new missile is shoot by the player
 * 
 * @author hao
 * 
 */
public class MissileMessage implements Message {
	Missile m;
	int messageTypeID = Message.MISSILE_MESSAGE;
	TankClient tc;

	public MissileMessage(Missile m) {// used for send
		this.m = m;
	}

	public MissileMessage(TankClient tc) {// used for parse
		this.tc = tc;

	}

	public void parse(DataInputStream dataInput) {

		int ID;// this ID is MissileID, equals to tankIID.
		int MissileID;
		try {
			ID = dataInput.readInt();
			if (tc.myTank.ID == ID) {// if this missile comes from mytank, then
				// ignore it.
				return;
			}// if ID is yourself tank, then foget this package
			MissileID = dataInput.readInt();
			int x = dataInput.readInt();
			int y = dataInput.readInt();
			Direction dir = Direction.values()[dataInput.readInt()];
			boolean good = dataInput.readBoolean();

			Missile m = new Missile(ID, x, y, dir, tc, good);
			m.missileID = MissileID;
			tc.missiles.add(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
		try {
			dataOutput.writeInt(messageTypeID);
			dataOutput.writeInt(m.tankID);
			dataOutput.writeInt(m.missileID);
			dataOutput.writeInt(m.x);
			dataOutput.writeInt(m.y);
			dataOutput.writeInt(m.dir.ordinal());// return Enum int
			dataOutput.writeBoolean(m.good);
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
