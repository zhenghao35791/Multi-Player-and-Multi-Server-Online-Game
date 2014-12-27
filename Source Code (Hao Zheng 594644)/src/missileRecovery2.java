import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

/**
 * the blood of tank, initially is 100, once hit, decrease by 20.
 * 
 * @author Hao
 * 
 */
public class missileRecovery2 {
	TankClient tc;
	Image missileRecoveryImage = null;
	private int x, y, w, h;
	private boolean Live = true;

	public boolean isLive() {
		return Live;
	}

	public void setLive(boolean live) {
		Live = live;
	}

	/**
	 * initial missile and give value to x,y,w,h
	 */
	public missileRecovery2() {
		x = 870;
		y = 550;
		w = h = 50;
	}

	/**
	 * once draw, invoking move(), step++, to make the position change
	 * 
	 * @param g
	 */
	public void draw(Graphics g, int offsetX, int offsetY) {
		if (!Live)
			return;
		ImageIcon icon = new ImageIcon(getClass().getResource(
				"images/missileRecovery.png"));

		missileRecoveryImage = icon.getImage();
		g.drawImage(missileRecoveryImage, x + offsetX, y + offsetY, w, h, null);
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, w, h);
	}

	/**
	 * if tank eat missile, tank life becomes full, and the missile is gone
	 * 
	 * @param t
	 * @return
	 */
	public boolean hitMissile2Recovery(Tank t) {
		if (this.Live && this.getRect().intersects(t.getRect())) {
			t.setMissileAmount(10);
			setLive(false);
			Live = false;
			return true;
		}
		return false;
	}
}
