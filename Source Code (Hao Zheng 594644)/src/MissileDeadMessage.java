import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * if a missile hit other objects, then it will dead
 * 
 * @author hao
 * 
 */
public class MissileDeadMessage implements Message {
	int messageTypeID = Message.MISSILE_DEAD_MESSAGE;
	int tankID;
	int missileID;
	TankClient tc;

	public MissileDeadMessage(TankClient tc) {
		this.tc = tc;
	}

	public MissileDeadMessage(int tankID, int missileID) {
		this.tankID = tankID;
		this.missileID = missileID;
	}

	public void send(DatagramSocket ds, String IP, int udpPort) {
		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
		try {
			dataOutput.writeInt(messageTypeID);//send playerID to get who shot this missile
			dataOutput.writeInt(tankID);
			dataOutput.writeInt(missileID);
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

	public void parse(DataInputStream dataInput) {
		try {
			int tankID = dataInput.readInt();
			/*
			 * if(tc.myTank.ID == tankID){ return; }
			 */

			int missileID = dataInput.readInt();
			for (int i = 0; i < tc.missiles.size(); i++) {
				Missile m = tc.missiles.get(i);
				if (m.tankID == tankID && m.missileID == missileID) {//if locate this missile, set missile dead
					m.setMissileLive(false);
					tc.explosions.add(new Explosion(m.x, m.y, tc));
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
