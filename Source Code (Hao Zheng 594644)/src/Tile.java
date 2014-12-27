import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * basic information of Tile
 * 
 * @author hao
 * 
 */
public class Tile extends Wall {
	int TileID = 1;
	public static final int WALL_X = 0;
	private int WIDTH = 50;
	private int HEIGHT = 50;
	public static int WALL_Y = 0;
	private String style;
	private TankClient tc;
	public boolean live = true;
	private int x, y;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public Tile() {
		super();
	}

	public Tile(int x, int y, String style, TankClient tc, int TileID) {
		super(x, y, style, tc);
		this.x = x;
		this.y = y;
		this.style = style;
		this.tc = tc;
		this.TileID = TileID;
	}

	public boolean getLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	String wallStyle = null;
	int step = 0;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] wallImgs = null;
	private static Map<String, Image> imgs = new HashMap<String, Image>();
	static {//import images that need
		wallImgs = new Image[] {
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/tile1.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/tile2.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/tile3.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/tile4.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/tile5.png")), };
		imgs.put("tile1", wallImgs[0]);
		imgs.put("tile2", wallImgs[1]);
		imgs.put("tile3", wallImgs[2]);
		imgs.put("tile4", wallImgs[3]);
		imgs.put("tile5", wallImgs[4]);
	}
	boolean init = false;
	/*
	 * draw all the walls due to different styles
	 * @see Wall#draw(java.awt.Graphics, int, int)
	 */
	public void draw(Graphics g, int offsetX, int offsetY) {
		if (!this.live) {
			tc.tiles.remove(this);
			return;
		}
		if (!init) {
			for (int i = 0; i < wallImgs.length; i++)
				g.drawImage(wallImgs[i], -100, -100, null);
			init = true;
		}

		wallStyle = style;
		g.drawImage(imgs.get(wallStyle), this.x + offsetX, this.y + offsetY,
				null);
		if (("tile1".equals(style)) || ("tile2".equals(style))
				|| ("tile3".equals(style)) || ("tile4".equals(style))
				|| ("tile5".equals(style))) {
			this.coolidesWithMissiles(tc.missiles);
			this.collidesWithTank(tc.myTank);
			this.collidesWithTanks(tc.tanks);
		}
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	/**
	 * if missile hit a wall collision detection happens
	 * @param missile
	 * @return
	 */
	public boolean collidesWithMissile(Missile missile) {
		for (int i = 0; i < tc.tiles.size(); i++) {
			Tile tile = tc.tiles.get(i);
			if (tile.live && missile.isMissileLive()
					&& tile.getRect().intersects(missile.getRect())) {
				tile.live = false;
				Explosion e = new Explosion(x, y, tc);
				tc.explosions.add(e);
				missile.setMissileLive(false);
				MissileDeadMessage missileDeadMessage = new MissileDeadMessage(//send missile dead message
						missile.tankID, missile.missileID);
				tc.ngc.send(missileDeadMessage);
				TILE_DEAD_MESSAGE tileDeadMessage = new TILE_DEAD_MESSAGE(// send tile dead message
						tile.TileID);
				tc.ngc.send(tileDeadMessage);
				return true;
			}

		}
		return false;
	}

	/**
	 * whether missile hit the wall
	 * 
	 * @param missiles
	 *            list to pu the missiles
	 * @return boolaen hit or not
	 */

	public boolean coolidesWithMissiles(List<Missile> missiles) {
		for (int i = 0; i < missiles.size(); i++) {
			Missile missile = missiles.get(i);
			if (collidesWithMissile(missile))
				return true;
		}
		return false;
	}

	public boolean collidesWithTank(Tank tank) {
		if (this.live && tank.TankLive()
				&& this.getRect().intersects(tank.getRect())) {
			tank.stay();
			return true;
		}
		return false;
	}

	public boolean collidesWithTanks(List<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			Tank tank = tanks.get(i);
			if (collidesWithTank(tank))
				return true;
		}
		return false;
	}

	public String getStyle() {
		return style;
	}

	/**
	 * if missile hit this(Wall), missile live is false
	 * 
	 * @param m
	 *            because need to use variables in Missile
	 * @return
	 */
}
