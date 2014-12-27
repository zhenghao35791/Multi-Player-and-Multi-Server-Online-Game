import java.io.DataInputStream;
import java.net.DatagramSocket;

/**
 * types of message, message represents the action happens in the client side
 * 
 * @author hao
 * 
 */
public interface Message {
	//message name and message ID
	public static final int TANK_MOVE_MESSAGE = 1;
	public static final int TANK_NEW_MESSAGE = 2;
	public static final int MISSILE_MESSAGE = 3;
	public static final int TANK_DEAD_MESSAGE = 4;
	public static final int MISSILE_DEAD_MESSAGE = 5;
	public static final int BLOOD_MESSAGE = 6;
	public static final int BLOOD_RECOVERY_MESSAGE = 7;
	public static final int TILE_DEAD_MESSAGE = 8;
	public static final int TANK_SCORE_MESSAGE = 9;
	public static final int MISSILE_RECOVERY_MESSAGE = 10;
	public static final int SECOND_MISSILE_RECOVERY_MESSAGE = 11;
	public static final int SORE_ADDING_MESSAGE = 12;
	public static final int SPEED_UP_MESSAGE = 13;

	// need to realize send and parse methods
	void parse(DataInputStream dataInput);

	void send(DatagramSocket ds, String IP, int udpPort);
}
