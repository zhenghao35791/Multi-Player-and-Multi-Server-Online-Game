import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lightcouch.CouchDbClient;
import com.google.gson.JsonObject;

/**
 * main class. draw frame.paint will reload all the time.
 * 
 * @author Hao
 * 
 */
public class TankClient extends Frame {
	CouchDbClient dbNameScore = new CouchDbClient("couchdb3.properties");
	String myUDPPortFromDialog;
	InetAddress ia;
	String ipAddress;
	public static final int BACKGROUD_WIDTH = 1085 * 2;
	public static final int BACKGROUD_HEIGHT = 595 * 2;
	private static Image[] FrontImages = null;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] background = null;

	static {
		//import the images
		background = new Image[] { tk.getImage(Wall.class.getClassLoader()
				.getResource("images/test3.png")) };
		background[0].getScaledInstance(BACKGROUD_WIDTH, BACKGROUD_HEIGHT,
				Image.SCALE_DEFAULT);
		FrontImages = new Image[] {

				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/id.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/missileAmount.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/score.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/heart.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/missileRecovery.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/scoring.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/speedup.png")) };
	}

	private static final long serialVersionUID = 1L;
	CouchDbClient dbBackupServer = new CouchDbClient("couchdb2.properties");
	int TileID = 1;
	// public static final String IP = "127.0.0.1";
	public static final int FRAME_LOCATION_X = 150;
	public static final int FRAME_LOCATION_Y = 0;
	public static final int FRAME_SIZE_X = 1024;
	public static final int FRAME_SIZE_Y = 800;
	private Random r = new Random();
	ConnDialog dialog = new ConnDialog();
	int randomInt = r.nextInt(450);
	NetGameClient ngc = new NetGameClient(this);
	BloodRecovery bloodRecovery = new BloodRecovery();
	SpeedUp speedUp = new SpeedUp();
	missileRecovery missileRecovery = new missileRecovery();
	missileRecovery2 missileRecovery2 = new missileRecovery2();
	ScoreAdding scoreAdding = new ScoreAdding();
	Image offScreen = null;
	Missile m = null;
	Tank myTank = new Tank(20 + randomInt, 0, this, true, Direction.STOP);

	/**
	 * X
	 */
	public static final int WALL_X = 0;

	/**
	 * Y
	 */
	public static int WALL_Y = 0;
	/*
	 *List of players, walls, missiles, everything that may be on the screen
	 */
	List<Tank> tanks = new ArrayList<Tank>();
	List<Wall> walls = new ArrayList<Wall>();
	List<Tile> tiles = new ArrayList<Tile>();
	List<Missile> missiles = new ArrayList<Missile>();
	List<Explosion> explosions = new ArrayList<Explosion>();
	String sytle = null;//wall style

	public TankClient() {
		drawWall();
	}

	public void paint(Graphics g) {
		/**
		 * if tank is dead,will send its information to the database before remove itself from the screen
		 */
		if (!myTank.TankLive()) {
			int score = myTank.getScore();
			try {
				ia = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			myTank.setLife(0);
			ipAddress = ia.getHostAddress();
			JsonObject json = new JsonObject();
			json.addProperty("SCORE", score + "");
			json.addProperty("ipAddress", ipAddress);
			json.addProperty("myUDPPortFromDialog", myUDPPortFromDialog);
			System.out.println("ip: " + ipAddress + "myUDPPortFromDialog: "
					+ myUDPPortFromDialog);
			dbNameScore.save(json);
			g.setFont(new Font("Times", Font.CENTER_BASELINE, 500));
			g.drawString("GAMEOVER", 100, 100);
		}
		/*
		 * set the offset to make the map moving with the player
		 */
		int offsetX = FRAME_SIZE_X / 2 - myTank.x;
		offsetX = Math.min(offsetX, 0);
		offsetX = Math.max(offsetX, FRAME_SIZE_X - BACKGROUD_WIDTH);

		int offsetY = FRAME_SIZE_Y / 2 - myTank.y;
		offsetY = Math.min(offsetY, 0);
		offsetY = Math.max(offsetY, FRAME_SIZE_Y - BACKGROUD_HEIGHT);
		myTank.draw(g, offsetX, offsetY);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Times", Font.CENTER_BASELINE, 35));
		/*
		 *drawing icons at the top of the screen 
		 */
		g.drawImage(FrontImages[0], 70, 50, null);
		g.drawString("" + myTank.ID, 150, 85);

		g.drawImage(FrontImages[3], 320, 50, null);
		g.drawString("" + myTank.getLife(), 400, 85);

		g.drawImage(FrontImages[2], 570, 50, null);
		g.drawString("" + myTank.getScore(), 650, 85);

		g.drawImage(FrontImages[1], 820, 50, null);
		g.drawString("" + myTank.getMissileAmount(), 900, 85);

		speedUp.draw(g, offsetX, offsetY);
		/*
		 * estimate if the player hit or eat the extra icons
		 */
		if (speedUp.hitSpeedUp(myTank)) {
			SpeedUpMessage speedUpMessage = new SpeedUpMessage(myTank.ID);
			ngc.send(speedUpMessage);
		}

		bloodRecovery.draw(g, offsetX, offsetY);
		if (bloodRecovery.hitBlood(myTank)) {
			BloodRecoveryMessage bloodRecoveryMessage = new BloodRecoveryMessage(
					myTank.ID);
			ngc.send(bloodRecoveryMessage);

			BloodMessage bloodMessage = new BloodMessage(myTank.ID, 100);
			ngc.send(bloodMessage);
		}

		missileRecovery.draw(g, offsetX, offsetY);
		if (missileRecovery.hitMissileRecovery(myTank)) {
			MissileRecoveryMessage missileRecoveryMessage = new MissileRecoveryMessage(
					myTank.ID);
			ngc.send(missileRecoveryMessage);
		}

		missileRecovery2.draw(g, offsetX, offsetY);
		if (missileRecovery2.hitMissile2Recovery(myTank)) {
			MissileRecovery2Message missileRecoveryMessage2 = new MissileRecovery2Message(
					myTank.ID);
			ngc.send(missileRecoveryMessage2);
		}

		scoreAdding.draw(g, offsetX, offsetY);
		if (scoreAdding.hitScoreAdding(myTank)) {
			ScoringAddingMessage scoringAddingMessage = new ScoringAddingMessage(
					myTank.ID);
			ngc.send(scoringAddingMessage);
		}

		for (int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			if (missiles.get(i).isMissileLive()) {
				if (m.hitTank(myTank)) {//if missile hit the player send the message
					MissileDeadMessage missileDeadMessage = new MissileDeadMessage(
							m.tankID, m.missileID);
					ngc.send(missileDeadMessage);
					if (!myTank.TankLive()) {
						TankDeadMessage tankDeadMessage = new TankDeadMessage(//if player dead send the dead message
								myTank.ID);
						ngc.send(tankDeadMessage);
					}
				}
				m.draw(g, offsetX, offsetY);
			} else {
				missiles.remove(i);
				m.setMissileLive(false);
				MissileDeadMessage missileDeadMessage = new MissileDeadMessage(
						m.tankID, m.missileID);
				ngc.send(missileDeadMessage);
			}

		}
		/*
		 * collision detection between tanks
		 */
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			t.draw(g, offsetX, offsetY);
			t.ColidesWithTank(tanks);
		}

		for (int i = 0; i < explosions.size(); i++) {
			Explosion e = explosions.get(i);
			e.draw(g, offsetX - 20, offsetY - 20);
		}

		for (int i = 0; i < walls.size(); i++) {
			Wall wall = walls.get(i);
			wall.draw(g, offsetX, offsetY);
		}

		for (int i = 0; i < tiles.size(); i++) {
			Tile tile = tiles.get(i);
			tile.draw(g, offsetX, offsetY);
		}

	}
	/*
	 * draw offset screen before drawing the real screen
	 * @see java.awt.Container#update(java.awt.Graphics)
	 */
	public void update(Graphics g) {
		if (offScreen == null) {
			offScreen = this.createImage(BACKGROUD_WIDTH, BACKGROUD_HEIGHT);
		}
		Graphics gOffScreen = offScreen.getGraphics();
		gOffScreen.drawImage(background[0], 0, 0, BACKGROUD_WIDTH,
				BACKGROUD_HEIGHT, null);
		print(gOffScreen);
		g.drawImage(offScreen, 0, 0, null);
	}

	// i is y
	public void drawWall() {

		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 30; j++) {

				if (i == 3 && (j == 0)) {// J=0
					Wall wall = new Wall(WALL_X + j * 50, WALL_Y + i * 50,
							"palace2", this);
					walls.add(wall);
				}
				if (i == 3 && (j == 20)) {// J=0
					Wall wall = new Wall(WALL_X + j * 50, WALL_Y + i * 50,
							"palace1", this);
					walls.add(wall);
				}
				if (i == 12 && (j == 0)) {
					Wall wall = new Wall(WALL_X + j * 50, WALL_Y + i * 50,
							"palace2", this);
					walls.add(wall);
				}
				if (i == 12 && (j == 20)) {
					Wall wall = new Wall(WALL_X + j * 50, WALL_Y + i * 50,
							"palace1", this);
					walls.add(wall);
				}

				if (i == 10 || i == 11 || i == 9) {
					if (j > 7 && j < 12) {
						Wall wall = new Wall(WALL_X + j * 50, WALL_Y + i * 50,
								"building1", this);
						walls.add(wall);
					}
				}

				if (j == 6 || j == 14) {
					if ((i > 1 && i < 6) || i > 15) {
						Wall wall = new Wall(WALL_X + j * 50, WALL_Y + i * 50,
								"column", this);
						walls.add(wall);
					}
				}

				if (i == 2 || i == 3 || i == 4 || i == 12 || i == 13 || i == 14) {
					if (j == 3 || j == 4 || j == 5 || j == 15 || j == 16
							|| j == 17) {
						Wall wall = new Wall(WALL_X + j * 50, WALL_Y + i * 50,
								"tile", this);

						walls.add(wall);
					}
				}

				if (i == 6 || i == 7 || i == 8 || i == 9) {
					if (j < 6) {

						Tile tile = new Tile(WALL_X + j * 50, WALL_Y + i * 50,
								"tile1", this, TileID++);
						tiles.add(tile);
					}

					if (j > 15) {

						Tile tile = new Tile(WALL_X + j * 50, WALL_Y + i * 50,
								"tile4", this, TileID++);
						tiles.add(tile);
					}
				}
				if (i == 18 || i == 19 || i == 20) {
					if (j < 6) {
						Tile tile = new Tile(WALL_X + j * 50, WALL_Y + i * 50,
								"tile3", this, TileID++);
						tiles.add(tile);
					}
					if (j > 15) {
						Tile tile = new Tile(WALL_X + j * 50, WALL_Y + i * 50,
								"tile2", this, TileID++);
						tiles.add(tile);
					}
				}
			}
		}
	}

	public void launchFrame() {
		this.setLocation(FRAME_LOCATION_X, FRAME_LOCATION_Y);
		this.setSize(FRAME_SIZE_X, FRAME_SIZE_Y);
		setVisible(true);
		setTitle("TankWar");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.addKeyListener(new KeyMonitor());// add key monitor

		setResizable(false);

		/*
		 * TankThread h = new TankThread(); Thread d = new Thread(h);
		 * d.start();----another way to relize
		 */
		new Thread(new TankThread()).start();
	}

	public static void main(String[] aregs) {

		TankClient tc = new TankClient();
		tc.launchFrame();
		tc.setVisible(true);
	}

	private class TankThread implements Runnable {

		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class KeyMonitor extends KeyAdapter {

		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_C) {
				dialog.setVisible(true);
			} else {
				myTank.keyPressed(e);
			}
		}

		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}
	}

	class ConnDialog extends Dialog {

		/**
		 * berofe play the game, need to input in the dialog
		 */
		private static final long serialVersionUID = 1L;
		Button b = new Button("YES");
		TextField tfIP = new TextField(TankWarMainServer.MainServerIP, 12);
		TextField tfPort = new TextField(""
				+ TankWarMainServer.TCP_PORT_FOR_CLIENTS, 4);
		TextField tfMyUDPPort = new TextField("2000", 4);

		public ConnDialog() {
			super(TankClient.this, true);
			this.setLayout(new FlowLayout());
			this.add(new Label("ServerIP:"));
			this.add(tfIP);
			this.add(new Label("ServerTCPPort:"));
			this.add(tfPort);
			this.add(new Label("MyUDPPort:"));
			this.add(tfMyUDPPort);
			this.add(b);
			this.setLocation(300, 300);
			this.pack();
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			});
			b.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					// get IP and port from user input
					String IP = tfIP.getText().trim();
					int port = Integer.parseInt(tfPort.getText().trim());
					int myUDPPort = Integer.parseInt(tfMyUDPPort.getText()
							.trim());
					ngc.setUdpPort(myUDPPort);
					myUDPPortFromDialog = myUDPPort + "";
					try {
						ngc.connectToServer(IP, port);
					} catch (Exception e2) {
						System.out
								.println("tankclient 326mainserver is full, reqect the connection, please connect to backuoserver");
						List<JsonObject> allDocsServer = dbBackupServer.view(
								"_all_docs").includeDocs(true).query(
								JsonObject.class);

						int pickTCPPort = Integer.parseInt(allDocsServer.get(0)
								.get("TCPPORT").toString());
						int pickUDPPort = Integer.parseInt(allDocsServer.get(0)
								.get("UDP_PORT").toString());
						String pickIP = allDocsServer.get(0).get("ServerIP")
								.toString().replace("\"", "");
						int ServerID = Integer.parseInt(allDocsServer.get(0)
								.get("ServerID").toString());
						System.out.println("tankclient227 " + "ServerID "
								+ ServerID + "pickTCPPort " + pickTCPPort
								+ "pickUDPPort " + pickUDPPort + "pickIP "
								+ pickIP);
						ngc.connectToServer(pickIP, pickTCPPort);
					}

					setVisible(false);
				}

			});
		}
	}
}
