import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Text {
private double x,y;
private long start, time, elapsed;
private String s;
	public Text(double x, double y, long time, String s)
	{
		this.s = s;
		this.x = x;
		this.y = y;
		this.time = time;
		start = System.nanoTime();
	}
	public boolean update()
	{
		y-= 2;
		elapsed = (System.nanoTime()- start) / 1000000;
		if (elapsed>time)
		{
			return true;
		}
		return false;
	}
	public void draw(Graphics2D g)
	{
		g.setFont((new Font ("Comic Sans MS", Font.PLAIN, 12)));
		int length = (int) g.getFontMetrics().getStringBounds(s,g).getWidth();
		int alpha = (int) (255 * Math.sin(3.14 * elapsed/time));
		if (alpha>255){alpha = 255;}
		g.setColor(new Color(255,255,255, alpha));
		g.drawString(s, (int)(x - length/2), (int)y);
		
	}
	
}
