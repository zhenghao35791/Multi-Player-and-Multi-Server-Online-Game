import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * in the frame can have walls that if missile hit wall will die, if tanks hit
 * walls will turn back.
 * 
 * @author Hao
 * 
 */

public class Wall {

	public final int WIDTH = 50;
	public final int HEIGHT = 50;
	private String style;
	private TankClient tc;
	private boolean live = true;

	public boolean getLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	private String wallStyle = null;
	int step = 0;
	private int x, y;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] wallImgs = null;
	private static Map<String, Image> imgs = new HashMap<String, Image>();
	static {
		wallImgs = new Image[] {
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/stone.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/tile.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/column.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/building1.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/building2.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/palace1.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/palace2.png")) };

		imgs.put("stone", wallImgs[0]);
		imgs.put("tile", wallImgs[1]);
		imgs.put("column", wallImgs[2]);
		imgs.put("building1", wallImgs[3]);
		imgs.put("building2", wallImgs[4]);
		imgs.put("palace1", wallImgs[5]);
		imgs.put("palace2", wallImgs[6]);
	}
	boolean init = false;

	/**
	 * 
	 * 
	 * @param x
	 *            left wallx
	 * @param y
	 *            left wall y
	 * @param style
	 *            the style of the wall
	 * @param tc
	 *            current tc
	 */

	public Wall() {
	}

	public Wall(int x, int y, String style, TankClient tc) {
		this.x = x;
		this.y = y;
		this.style = style;
		this.tc = tc;
	}

	public void draw(Graphics g, int offsetX, int offsetY) {
		if (!this.live) {
			tc.walls.remove(this);
			return;
		}
		if (!init) {
			for (int i = 0; i < wallImgs.length; i++)
				g.drawImage(wallImgs[i], -100, -100, null);
			init = true;
		}

		wallStyle = style;
		g.drawImage(imgs.get(wallStyle), x + offsetX, y + offsetY, null);
		if ("stone".equals(style)) {
			this.collidesWithTank(tc.myTank);
			this.collidesWithTanks(tc.tanks);
		} else if ("tile".equals(style)) {
			this.collidesWithTank(tc.myTank);
			this.collidesWithTanks(tc.tanks);
		} else if ("column".equals(style)) {
			this.collidesWithTank(tc.myTank);
			this.collidesWithTanks(tc.tanks);
		} else if (("palace1".equals(style)) || ("palace2".equals(style))) {
			this.collidesWithTank(tc.myTank);
			this.collidesWithTanks(tc.tanks);
		} else if (("building1".equals(style)) || ("building2".equals(style))
				|| ("building3".equals(style)) || ("building4".equals(style))
				|| ("building5".equals(style))) {
			this.collidesWithTank(tc.myTank);
			this.collidesWithTanks(tc.tanks);
		}
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, this.WIDTH, this.HEIGHT);
	}

	/**
	 * whether hit by a missile
	 * 
	 * @param missile
	 * 
	 * @return boolean hit or not
	 */

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
