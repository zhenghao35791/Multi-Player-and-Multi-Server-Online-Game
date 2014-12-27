import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

/**
 * set the speed
 * 
 * @author Hao
 * 
 */
public class SpeedUp {

	TankClient tc;
	Image speedup = null;
	private int x, y, w, h;
	private boolean Live = true;

	public boolean isLive() {
		return Live;
	}

	public void setLive(boolean live) {
		Live = live;
	}

	public SpeedUp() {
		x = 570;
		y = 150;
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
				"images/speedup.png"));
		speedup = icon.getImage();
		g.drawImage(speedup, x + offsetX, y + offsetY, w, h, null);
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, w, h);
	}

	public boolean hitSpeedUp(Tank t) {
		if (this.Live && this.getRect().intersects(t.getRect())) {
			t.XSPEED = 10;
			t.YSPEED = 10;
			setLive(false);
			Live = false;
			return true;
		}
		return false;
	}
}
