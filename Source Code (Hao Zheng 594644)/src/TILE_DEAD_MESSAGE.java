import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Tile is dead and remove it from the map
 * 
 * @author hao
 * 
 */
public class TILE_DEAD_MESSAGE implements Message {

	int TileID;
	int messageTypeID = Message.TILE_DEAD_MESSAGE;
	TankClient tc;

	public TILE_DEAD_MESSAGE(TankClient tc) {
		this.tc = tc;
	}

	public TILE_DEAD_MESSAGE(int TileID) {
		this.TileID = TileID;
	}

	public void parse(DataInputStream dataInput) {
		try {
			int TileID = dataInput.readInt();
			for (int i = 0; i < tc.tiles.size(); i++) {
				Tile tile = tc.tiles.get(i);
				if (tile.TileID == TileID) {
					tile.live = false;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(DatagramSocket ds, String IP, int udpPort) {

		ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();// Creates
		DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);
		try {
			dataOutput.writeInt(messageTypeID);
			dataOutput.writeInt(this.TileID);
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
