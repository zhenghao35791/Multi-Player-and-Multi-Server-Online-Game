import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tank; list of tank
 * 
 * @author Hao
 * 
 */

public class Tank {
	private int missileAmount = 10;//maximum has 10 missiles

	public int getMissileAmount() {
		return missileAmount;
	}

	public void setMissileAmount(int missileAmount) {
		this.missileAmount = missileAmount;
	}

	int x = 50;
	int y = 50;
	private int oldX, oldY;// the x,y before move
	private int life = 100;// blood life
	int ID;
	boolean isPlayerOne;
	boolean isPlayerTwo;
	private int Score = 0;//players' score, if kill a enemy get 10 score

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
		// return life;
	}

	public int XSPEED = 5;
	public int YSPEED = 5;
	public static final int TANK_WIDTH = 40;
	public static final int TANK_HEIGHT = 40;
	public Direction dir = Direction.STOP;
	private boolean bl = false;
	private boolean br = false;
	private boolean bu = false;
	private boolean bd = false;
	TankClient tc;
	public Direction barrelDir = Direction.U;
	boolean good = true;
	private boolean tankLive = true;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] goodTankImages = null;
	private static Image[] badTankImages = null;
	private static Image[] goodTankImages2 = null;
	private static Image[] badTankImages2 = null;
	private static Map<String, Image> imgs = new HashMap<String, Image>();
	//import all the images need
	static {
		goodTankImages = new Image[] {
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue1L.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue1LU.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue1U.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue1RU.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue1R.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue1RD.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue1D.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue1LD.png"))

		};

		goodTankImages2 = new Image[] {
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue2L.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue2LU.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue2U.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue2RU.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue2R.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue2RD.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue2D.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/blue2LD.png"))

		};

		badTankImages = new Image[] {
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white1L.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white1LU.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white1U.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white1RU.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white1R.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white1RD.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white1D.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white1LD.png"))

		};

		badTankImages2 = new Image[] {
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white2L.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white2LU.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white2U.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white2RU.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white2R.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white2RD.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white2D.png")),
				tk.getImage(Tank.class.getClassLoader().getResource(
						"images/white2LD.png"))

		};

		imgs.put("goodL", goodTankImages[0]);
		imgs.put("goodLU", goodTankImages[1]);
		imgs.put("goodU", goodTankImages[2]);
		imgs.put("goodRU", goodTankImages[3]);
		imgs.put("goodR", goodTankImages[4]);
		imgs.put("goodRD", goodTankImages[5]);
		imgs.put("goodD", goodTankImages[6]);
		imgs.put("goodLD", goodTankImages[7]);

		imgs.put("badL", badTankImages[0]);
		imgs.put("badLU", badTankImages[1]);
		imgs.put("badU", badTankImages[2]);
		imgs.put("badRU", badTankImages[3]);
		imgs.put("badR", badTankImages[4]);
		imgs.put("badRD", badTankImages[5]);
		imgs.put("badD", badTankImages[6]);
		imgs.put("badLD", badTankImages[7]);

		imgs.put("goodL2", goodTankImages2[0]);
		imgs.put("goodLU2", goodTankImages2[1]);
		imgs.put("goodU2", goodTankImages2[2]);
		imgs.put("goodRU2", goodTankImages2[3]);
		imgs.put("goodR2", goodTankImages2[4]);
		imgs.put("goodRD2", goodTankImages2[5]);
		imgs.put("goodD2", goodTankImages2[6]);
		imgs.put("goodLD2", goodTankImages2[7]);

		imgs.put("badL2", badTankImages2[0]);
		imgs.put("badLU2", badTankImages2[1]);
		imgs.put("badU2", badTankImages2[2]);
		imgs.put("badRU2", badTankImages2[3]);
		imgs.put("badR2", badTankImages2[4]);
		imgs.put("badRD2", badTankImages2[5]);
		imgs.put("badD2", badTankImages2[6]);
		imgs.put("badLD2", badTankImages2[7]);
	}

	public boolean TankLive() {
		return tankLive;
	}

	public void setTankLive(boolean tankLive) {
		this.tankLive = tankLive;
	}

	public Tank(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Tank(int x, int y, TankClient tc, boolean good, Direction dir) {
		this(x, y);
		this.oldX = x;
		this.oldY = y;
		this.tc = tc;
		this.good = good;
		this.dir = dir;
	}

	public void draw(Graphics g, int offsetX, int offsetY) { // draw graphics

		if (!TankLive()) {
			if (!good) {
				tc.tanks.remove(this);
			}
			return;
		}

		if (tankLive) {// draw blood bar
			new BloodBar().draw(g, offsetX, offsetY);
		}
		Color cc = g.getColor();//drawing current attitute icons at the top of screen
		g.setColor(Color.WHITE);
		g.setFont(new Font("Times", Font.CENTER_BASELINE, 15));
		g.drawString("ID: " + ID, x + offsetX + 25, y + offsetY - 15);
		g.setColor(cc);

		/**
		 * swith the directions of gun barrel; the directions of gun barrel are
		 * the same as the directions of tank
		 */
		switch (barrelDir) {

		case L:
			if (good && isPlayerOne)
				g.drawImage(imgs.get("goodL"), x + offsetX, y + offsetY, null);
			if (good && !isPlayerOne)
				g.drawImage(imgs.get("goodL2"), x + offsetX, y + offsetY, null);
			if (!good && isPlayerTwo)
				g.drawImage(imgs.get("badL"), x + offsetX, y + offsetY, null);
			if (!good && !isPlayerTwo)
				g.drawImage(imgs.get("badL2"), x + offsetX, y + offsetY, null);
			break;
		case LU:
			if (good && isPlayerOne)
				g.drawImage(imgs.get("goodLU"), x + offsetX, y + offsetY, null);
			if (good && !isPlayerOne)
				g.drawImage(imgs.get("goodLU2"), x + offsetX, y + offsetY, null);
			if (!good && isPlayerTwo)
				g.drawImage(imgs.get("badLU"), x + offsetX, y + offsetY, null);
			if (!good && !isPlayerTwo)
				g.drawImage(imgs.get("badLU2"), x + offsetX, y + offsetY, null);
			break;
		case U:
			if (good && isPlayerOne)
				g.drawImage(imgs.get("goodU"), x + offsetX, y + offsetY, null);
			if (good && !isPlayerOne)
				g.drawImage(imgs.get("goodU2"), x + offsetX, y + offsetY, null);
			if (!good && isPlayerTwo)
				g.drawImage(imgs.get("badU"), x + offsetX, y + offsetY, null);
			if (!good && !isPlayerTwo)
				g.drawImage(imgs.get("badU2"), x + offsetX, y + offsetY, null);
			break;
		case RU:
			if (good && isPlayerOne)
				g.drawImage(imgs.get("goodRU"), x + offsetX, y + offsetY, null);
			if (good && !isPlayerOne)
				g.drawImage(imgs.get("goodRU2"), x + offsetX, y + offsetY, null);
			if (!good && isPlayerTwo)
				g.drawImage(imgs.get("badRU"), x + offsetX, y + offsetY, null);
			if (!good && !isPlayerTwo)
				g.drawImage(imgs.get("badRU2"), x + offsetX, y + offsetY, null);
			break;
		case R:
			if (good && isPlayerOne)
				g.drawImage(imgs.get("goodR"), x + offsetX, y + offsetY, null);
			if (good && !isPlayerOne)
				g.drawImage(imgs.get("goodR2"), x + offsetX, y + offsetY, null);
			if (!good && isPlayerTwo)
				g.drawImage(imgs.get("badR"), x + offsetX, y + offsetY, null);
			if (!good && !isPlayerTwo)
				g.drawImage(imgs.get("badR2"), x + offsetX, y + offsetY, null);
			break;
		case RD:
			if (good && isPlayerOne)
				g.drawImage(imgs.get("goodRD"), x + offsetX, y + offsetY, null);
			if (good && !isPlayerOne)
				g.drawImage(imgs.get("goodRD2"), x + offsetX, y + offsetY, null);
			if (!good && isPlayerTwo)
				g.drawImage(imgs.get("badRD"), x + offsetX, y + offsetY, null);
			if (!good && !isPlayerTwo)
				g.drawImage(imgs.get("badRD2"), x + offsetX, y + offsetY, null);
			break;
		case D:
			if (good && isPlayerOne)
				g.drawImage(imgs.get("goodD"), x + offsetX, y + offsetY, null);
			if (good && !isPlayerOne)
				g.drawImage(imgs.get("goodD2"), x + offsetX, y + offsetY, null);
			if (!good && isPlayerTwo)
				g.drawImage(imgs.get("badD"), x + offsetX, y + offsetY, null);
			if (!good && !isPlayerTwo)
				g.drawImage(imgs.get("badD2"), x + offsetX, y + offsetY, null);
			break;
		case LD:
			if (good && isPlayerOne)
				g.drawImage(imgs.get("goodLD"), x + offsetX, y + offsetY, null);
			if (good && !isPlayerOne)
				g.drawImage(imgs.get("goodLD2"), x + offsetX, y + offsetY, null);
			if (!good && isPlayerTwo)
				g.drawImage(imgs.get("badLD"), x + offsetX, y + offsetY, null);
			if (!good && !isPlayerTwo)
				g.drawImage(imgs.get("badLD2"), x + offsetX, y + offsetY, null);
			break;
		}
		move();
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		/**
		 * if mytank is dead , press F5 to renew a mytank with 100 life.
		 */
		switch (key) {
		//get direction
		case KeyEvent.VK_LEFT:
			bl = true;
			break;
		case KeyEvent.VK_RIGHT:
			br = true;
			break;
		case KeyEvent.VK_UP:
			bu = true;
			break;
		case KeyEvent.VK_DOWN:
			bd = true;
			break;
		}
		currentLocation();
	}

	/**
	 * dicide the current location by judging pressed button.
	 */
	private void currentLocation() {
		Direction oldDir = this.dir;
		if (bl && !br && !bu && !bd) {
			dir = Direction.L;
		} else if (bl && !br && bu && !bd) {
			dir = Direction.LU;
		} else if (!bl && !br && bu && !bd) {
			dir = Direction.U;
		} else if (!bl && br && bu && !bd) {
			dir = Direction.RU;
		} else if (!bl && br && !bu && !bd) {
			dir = Direction.R;
		} else if (!bl && br && !bu && bd) {
			dir = Direction.RD;
		} else if (!bl && !br && !bu && bd) {
			dir = Direction.D;
		} else if (bl && !br && !bu && bd) {
			dir = Direction.LD;
		} else if (!bl && !br && !bu && !bd) {
			dir = Direction.STOP;
		}
		if (dir != oldDir) {
			TankMoveMessage tankMoveMessage = new TankMoveMessage(ID, dir, x,
					y, barrelDir);
			tc.ngc.send(tankMoveMessage);//if direction has changed, send the moving message
		}
	}

	/**
	 * x, y will change follow the directions
	 */
	private void move() {
		this.oldX = x;
		this.oldY = y;
		switch (dir) {

		case L:
			x = x - XSPEED;
			break;
		case LU:
			x = x - XSPEED;
			y = y - YSPEED;
			break;
		case U:
			y = y - YSPEED;
			break;
		case RU:
			x = x + XSPEED;
			y = y - YSPEED;
			break;
		case R:
			x = x + XSPEED;
			break;
		case RD:
			x = x + XSPEED;
			y = y + YSPEED;
			break;
		case D:
			y = y + YSPEED;
			break;
		case LD:
			y = y + YSPEED;
			x = x - XSPEED;
			break;
		case STOP:
			break;

		}

		/**
		 * make sure the tank wont run out of the game frame
		 */
		if (x < 0)
			x = 0;
		if (y < 30)
			y = 30;
		if (x + Tank.TANK_WIDTH > TankClient.FRAME_SIZE_X)
			x = TankClient.FRAME_SIZE_X - Tank.TANK_WIDTH;
		if (y + Tank.TANK_HEIGHT > TankClient.FRAME_SIZE_Y)
			y = TankClient.FRAME_SIZE_Y - Tank.TANK_HEIGHT;

		if (this.dir != Direction.STOP) {
			this.barrelDir = this.dir;
		}
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {

		/*
		 * case KeyEvent.VK_A: superFire(); break;
		 */
		case KeyEvent.VK_CONTROL:
			// tc.m = fire();
			this.fire();
			break;
		case KeyEvent.VK_LEFT:// if released, wont left anymore
			bl = false;
			break;
		case KeyEvent.VK_RIGHT:
			br = false;
			break;
		case KeyEvent.VK_UP:
			bu = false;
			break;
		case KeyEvent.VK_DOWN:
			bd = false;
			break;
		}
		currentLocation();

	}

	private void fire() {
		if (!TankLive()) {
		}

		if (tc.myTank.getMissileAmount() > 0) {
			Missile m = new Missile(ID, x + TANK_WIDTH / 2,
					y + TANK_HEIGHT / 2, barrelDir, this.tc, good);// make sure
																	// the
																	// missile
																	// is fired
																	// from
			tc.missiles.add(m);
			MissileMessage missileMessage = new MissileMessage(m);
			if (tc.myTank.getMissileAmount() > 0) {
				System.out.println(tc.myTank.getMissileAmount());

				tc.myTank.setMissileAmount(tc.myTank.getMissileAmount() - 1);
			}
			System.out.println(tc.myTank.getMissileAmount());
			tc.ngc.send(missileMessage);
		} else {
			System.out.println("no more missile");
		}
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, TANK_WIDTH, TANK_HEIGHT);
	}

	public void stay() {// tank goes back to the past x,y. go back to last step
		// and will get new random direction
		this.x = oldX;
		this.y = oldY;
	}

	public boolean ColidesWithTank(List<Tank> tanks) {// if mytank(goodtank)
		// colides with tanks,
		// stay
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			if (this != t) {
				if (this.getRect().intersects(t.getRect()) && this.tankLive
						&& t.tankLive) {
					this.stay();
					return true;
				}
			}
		}
		return false;
	}

	public void setScore(int score) {
		Score = score;
	}

	public int getScore() {
		return Score;
	}

	private class BloodBar {
		public void draw(Graphics g, int offsetX, int offsetY) {
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(x + offsetX + 25, y + offsetY - 10, TANK_WIDTH, 5);
			int w = (TANK_WIDTH) * life / 100;// draw blood bar based on the
			// current life. if 80, then 80/100
			// is the proportion of life that tank has
			g.fillRect(x + offsetX + 25, y + offsetY - 10, w, 5);
			g.setColor(c);
		}
	}
}
