import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

/**
 * explosion on (x,y) when the tank is hitted by missile.
 * 
 * @author Hao
 * 
 */
public class Explosion {
	int x, y;
	private TankClient tc;
	/**
	 * 
	 * @param x
	 * @param y
	 * @param tc
	 *            tc is used to invoke variables and methods in tankClient.
	 */
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] images = {
			/******************************
			 * import all the images that need
			 **********************************/
			tk.getImage(Explosion.class.getClassLoader().getResource(
					"images/1.png")),
			tk.getImage(Explosion.class.getClassLoader().getResource(
					"images/2.png")),
			tk.getImage(Explosion.class.getClassLoader().getResource(
					"images/3.png")),
			tk.getImage(Explosion.class.getClassLoader().getResource(
					"images/4.png")),
			tk.getImage(Explosion.class.getClassLoader().getResource(
					"images/5.png")),
			tk.getImage(Explosion.class.getClassLoader().getResource(
					"images/6.png")),
			tk.getImage(Explosion.class.getClassLoader().getResource(
					"images/7.png")),
			tk.getImage(Explosion.class.getClassLoader().getResource(
					"images/8.png")),
			tk.getImage(Explosion.class.getClassLoader().getResource(
					"images/9.png")) };

	public Explosion(int x, int y, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}

	/**
	 * Explosion is composed with different cycles with different sizes. Like a
	 * wave from small diameter to big diameter
	 */
	int k = 0;
	boolean ExplosionLive = true;
	private static boolean init = false;

	public void draw(Graphics g, int offsetX, int offsetY) {
		if (!init) {//firstly draw the images in the background
			for (int i = 0; i < images.length; i++) {
				g.drawImage(images[i], -100 + offsetX, -100 + offsetY, null);
			}
			init = true;
		}
		if (!ExplosionLive) {
			/**
			 * explosion is false then remove from the list of explosions
			 */
			tc.explosions.remove(this);
			return;
		}
		if (k == images.length) {// cycle within the dimens[]
			ExplosionLive = false;
			k = 0;//set k to initial value
			return;
		}
		g.drawImage(images[k], x + offsetX, y + offsetY, null);//keep drawing image in the explsion position
		k++;
	}
}
