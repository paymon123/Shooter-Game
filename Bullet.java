import java.awt.*;
import java.util.Random;

public class Bullet {
//FIELDS
	private double x, y, speed, dy, dx, rad;
	int r;
	private boolean bouncing = false;
	Random random = new Random();
	
	private Color color1;
	
	public Bullet (double angle, int x, int y, int r)
	{
		this.x = x;
		this.y = y;
		this.r = r;
		speed = 10;
		rad = Math.toRadians(angle);
		dx = Math.cos(rad) *speed;
		dy = Math.sin(rad) * speed;
		color1 = Color.YELLOW;
	}
	
	public boolean update()
	{
		x+=dx;
		y+=dy;
		if (bouncing)
		{
			if (x<-r || x >GamePanel.WIDTH + r) dx = -dx;
			if (y<-r || y >GamePanel.HEIGHT + r) {dy = -dy;dx = (random.nextInt(20)-10);}
		}
		else if (!bouncing)
		{
			if (x<-r || x >GamePanel.WIDTH + r || y<-r || y >GamePanel.HEIGHT + r) 
				return true;
			
		}
			
		return false;
		
	}
	public void draw(Graphics2D g)
	{
		g.setColor(color1);
		g.fillOval((int)(x-r), (int)(y-r), 2*r, 2*r);
	}
	public double getX(){return x;}
	public double getY(){return y;}
	public double getR(){return r;}
	public void setRadius(int r){this.r = r;}
	public void setBounce(boolean bouncing){this.bouncing = bouncing;}
}
