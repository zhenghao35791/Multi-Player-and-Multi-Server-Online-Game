import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * send this message when a new player join
 * 
 * @author hao
 * 
 */

public class TankNewMessage implements Message {
	Tank tank;
	TankClient tc;
	int messageTypeID = Message.TANK_NEW_MESSAGE;

	public TankNewMessage(Tank tank) {
		this.tank = tank;
	}

	public TankNewMessage(TankClient tc) {
		this.tc = tc;
		System.out.println("tc init");
	}

	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
		try {
			dataOutput.writeInt(messageTypeID);
			dataOutput.writeInt(tank.ID);
			dataOutput.writeInt(tank.x);
			dataOutput.writeInt(tank.y);
			dataOutput.writeInt(tank.dir.ordinal());// return Enum int
			dataOutput.writeBoolean(tank.good);
			System.out.println("tank new message send" + tank.ID + tank.x
					+ messageTypeID);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			DatagramPacket dp = new DatagramPacket(byteArrayOutput
					.toByteArray(), byteArrayOutput.toByteArray().length,
					new InetSocketAddress(IP, udpPort));
			ds.send(dp);
			System.out.println("backup send new");
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Constructs a datagram packet for sending packets of length length to
		// the specified port number on the specified host
		// Socket address. destinated address
	}

	public void parse(DataInputStream dataInput) {
		try {
			int ID = dataInput.readInt();
			if (tc.myTank.ID == ID) {
				return;
			}// if ID is yourself tank, then foget this package

			int x = dataInput.readInt();
			int y = dataInput.readInt();
			int dirID = dataInput.readInt();
			Direction dir = Direction.values()[dirID];
			Boolean good = dataInput.readBoolean();
			boolean tankExist = false;
			for (int i = 0; i < tc.tanks.size(); i++) {
				Tank t = tc.tanks.get(i);
				if (t.ID == ID) {
					tankExist = true;
					break;
				}
			}
			System.out.println("tanknewmessage " + tc.tanks.size());
			if (!tankExist) {
				TankNewMessage tanknewmessage = new TankNewMessage(tc.myTank);
				tc.ngc.send(tanknewmessage);
				Tank t = new Tank(x, y, tc, good, dir);// a new tank based on a
														// datagrampacket
														// message
				t.ID = ID;// set ID to the tank
				tc.tanks.add(t);
			}// add tank to the list, so that draw() can draw all
			// tanks in the list
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
