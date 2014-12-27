import java.awt.*;
import java.util.List;

/**
 * Tank can shot missile. my tank can control by"CTRL" enemy tank can shot
 * randomly.
 * 
 * @author hao
 * 
 */
public class Missile {
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] missileImgs = null;
	int tankID;
	int missileID = 1;
	int x;
	int y;
	boolean good = true;
	int scoreTankID = 0;
	/**
	 * static final can be easily invoked by Class.variable, also can change in
	 * this final general method
	 */
	public static final int hitLoseBlood = 33;//once being hit, lose 33 blood.
	public static final int MISSILE_WIDTH = 10;
	public static final int MISSILE_HEIGHT = 10;
	public static final int XSPEED = 10;//speed of x axis
	public static final int YSPEED = 10;//speed of y axis
	Direction dir;
	private TankClient tc;
	private boolean MissileLive = true;

	public boolean isMissileLive() {
		return MissileLive;
	}

	public void setMissileLive(boolean missileLive) {
		MissileLive = missileLive;
	}

	public Missile(int x, int y, Direction dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}

	static {
		// import missile images
		missileImgs = new Image[] {
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/1.png")),
				tk.getImage(Wall.class.getClassLoader().getResource(
						"images/missile2.png")),

		};
	}

	public Missile(int TankID, int x, int y, Direction dir, TankClient tc,
			boolean good) {

		this(x, y, dir);
		this.tankID = TankID;
		this.tc = tc;
		this.good = good;// the missile is from good player or bad player
		this.missileID++; 
	}

	public void draw(Graphics g, int offsetX, int offsetY) {
		if (!MissileLive) {
			tc.missiles.remove(this);//if already dead, remove from the game
			return;
		}
/****************************************************
 * if good missile, use image 0
 * if bad missile, use image 1
 ***************************************************/
		if (good)
			g.drawImage(missileImgs[0], x + offsetX + 5, y + offsetY
							+ 10, null);
		else
			g.drawImage(missileImgs[1], x + offsetX + 10, y + offsetY + 20,
					null);

		move();
	}

	/**
	 * Missile has 8 directions like tank, the missile will come out followed
	 * the direction of missile
	 */
	
	private void move() {
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
		 * if missile has went out of game frame, then set the missile live
		 * false
		 */
		if (x > TankClient.FRAME_SIZE_X || y > TankClient.FRAME_SIZE_Y || x < 0
				|| y < 0) {
			MissileLive = false;
		}
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, MISSILE_WIDTH, MISSILE_HEIGHT);//get physic body of missile
	}

	public boolean hitTank(Tank t) {
		if (this.getRect().intersects(t.getRect()) && t.TankLive()
				&& this.good != t.good) {

			int newLife = (t.getLife() - Missile.hitLoseBlood);//new life equals to old life - losed life
			t.setLife(newLife);
			BloodMessage bloodMessage = new BloodMessage(t.ID, newLife); //send blood chance message
			tc.ngc.send(bloodMessage);

			if (t.getLife() <= 0) {// if life <0, tank die
				t.setTankLive(false);
				for (int i = 0; i < tc.tanks.size(); i++) {//locate the player ID
					Tank tank = tc.tanks.get(i);
					if (this.tankID == tank.ID) {
						TankScoreMessage tankScoreMessage = new TankScoreMessage(
								tank.ID, tank.getScore() + 10);
						tc.ngc.send(tankScoreMessage);//send score change message
					}
				}
			}
			this.MissileLive = false;//icon dead remove
			Explosion e = new Explosion(x, y, tc);
			tc.explosions.add(e);
			return true;
		} else
			return false;
	}

	/**
	 * missile hit the tanks in the enemy tank list. for each tank in the list,
	 * invoke hitTank().
	 * 
	 * @param tanks
	 */
	public void hitTanks(List<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			hitTank(tanks.get(i));
		}
	}
}
