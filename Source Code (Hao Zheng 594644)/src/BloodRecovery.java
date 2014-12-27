import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

/**
 * the blood of tank, initially is 100, once hit, decrease by 33.
 * 
 * @author Hao
 * 
 */
public class BloodRecovery {
	/**
	 * int[][] means the positions of blood
	 */
	TankClient tc;
	Image bloodRecoveryImage = null;
	private int[][] positions = { { 500, 700 }, { 520, 705 }, { 530, 710 },
			{ 550, 720 }, { 530, 710 }, { 520, 705 }, { 500, 700 } };//dynamic icon, moving through the path
	private int x, y, w, h;
	private int step = 0;
	private boolean Bloodlive = true;

	/**
	 * initial Blood and give value to x,y,w,h
	 */
	public BloodRecovery() {
		x = positions[0][0];
		y = positions[0][1];
		w = h = 50;
	}

	public boolean isBloodlive() {
		return Bloodlive;
	}

	public void setBloodlive(boolean bloodlive) {
		Bloodlive = bloodlive;
	}

	/**
	 * once draw, invoking move(), step++, to make the position change
	 * 
	 * @param g
	 */
	public void draw(Graphics g, int offsetX, int offsetY) {
		if (!Bloodlive)
			return;
		ImageIcon icon = new ImageIcon(getClass().getResource(
				"images/bloodRecovery.png"));
		bloodRecoveryImage = icon.getImage();
		g.drawImage(bloodRecoveryImage, x + offsetX, y + offsetY, w, h, null);
		move();
	}

	/**
	 * the position of blood is changing within a circle.
	 */
	private void move() {
		step++;
		if (step == positions.length) {
			step = 0;
		}
		x = positions[step][0];
		y = positions[step][1];

	}

	public Rectangle getRect() {
		return new Rectangle(x, y, w, h);
	}

	/**
	 * if tank eat blood, tank life becomes full, and the blood is gone
	 * 
	 * @param t
	 * @return
	 */
	public boolean hitBlood(Tank t) {
		if (this.isBloodlive() && this.getRect().intersects(t.getRect())//collision detection
				&& t.TankLive()) {
			t.setLife(100);// if hit the recovery icon, get a life recovery
			setBloodlive(false);
			Bloodlive = false;//remove icon from screen
			return true;
		}
		return false;
	}
}
