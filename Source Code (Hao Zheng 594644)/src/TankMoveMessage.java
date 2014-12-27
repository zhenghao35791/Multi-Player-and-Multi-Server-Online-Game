import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * send message if players' direction has changed.
 * 
 * @author hao
 * 
 */

public class TankMoveMessage implements Message {
	int messageTypeID = Message.TANK_MOVE_MESSAGE;
	int ID;
	Direction dir;
	Direction barrelDir;
	TankClient tc;
	int x;
	int y;

	public TankMoveMessage(int ID, Direction dir, int x, int y,
			Direction barrelDir) {
		this.ID = ID;
		this.dir = dir;
		this.x = x;
		this.y = y;
		this.barrelDir = barrelDir;
	}

	public TankMoveMessage(TankClient tc) {
		this.tc = tc;
	}

	public void parse(DataInputStream dataInput) {
		try {

			int ID = dataInput.readInt();
			if (tc.myTank.ID == ID) {
				return;
			}// if ID is yourself tank, then foget this package
			int x = dataInput.readInt();
			int y = dataInput.readInt();
			Direction dir = Direction.values()[dataInput.readInt()];
			Direction barrelDir = Direction.values()[dataInput.readInt()];

			boolean exist = false;
			for (int i = 0; i < tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if (t.ID == ID) {
					exist = true;
					t.x = x;
					t.y = y;
					t.dir = dir;
					t.barrelDir = barrelDir;
					break;
				}
				if (!exist) {
					System.out.println("new tank");
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
			dataOutput.writeInt(x);
			dataOutput.writeInt(y);
			dataOutput.writeInt(dir.ordinal());// return Enum int
			dataOutput.writeInt(barrelDir.ordinal());// return Enum int

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
