import java.awt.*;
import java.util.Random;

public class Enemy {
	//FIELDS
	private double x, y, dx, dy, rad, speed, angle;
	private int r;
	
	private int health, type, rank;
	
	private Color color1;
	
	private boolean ready, dead, slow, fast;

	Random rand = new Random();
	
	private boolean hit;
	private long hitTimer;
	
	//CONSTRUCTOR
	public Enemy(int type, int rank)
	{
		this.type=type;
		this.rank = rank;
		this.x = Math.random() * GamePanel.WIDTH/2;
		this.y = -r;
		setSlow(slow);
		setFast(fast);
		//default enemy
		if(type == 1)
		{
			color1 = new Color(0,0,255, 150);
		}
		if (type == 2)
		{
			color1 = new Color(255,0,0, 150);
		}
		if (type == 3)
		{
			color1 = new Color(0,0,0, 150);
		}

		if (type<3)speed = 2*type;
		else
		{
			speed =1.5;
		}
		r = 5*rank;
		health = 1*type;

		angle = Math.random() * 140 + 20;
		rad = Math.toRadians(angle);
		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		
		ready = false;
		dead = false;
		hit = false;
		hitTimer = 0;
		}
	//METHODS
	public void setX(int x)
	{
		this.x = x;
	}
	public void setY(int y)
	{
		this.y = y;
	}
	public void setSlow(boolean b)
	{
		slow = b;
	}
	public void setFast(boolean b)
	{
		fast = b;
	}
	public double getX(){return x;}
	public double getY(){return y;}
	public int getR(){return r;}
	public int getType(){return type;}
	public int getRank(){return rank;}
	public boolean isDead(){return dead;}
	public void split(){
		
		for(int j = 0; j<getType()+1; j++)
		{
				
			Enemy e = new Enemy(getType(), getRank()-1);
			e.setSlow(slow);
			e.setFast(fast);
			e.x = this.x;
			e.y = this.y;
			
			if (!ready)
			{
				angle = Math.random() *140 + 20;
			}
			else
			{
				angle = Math.random() * 360;
			}
			GamePanel.enemies.add(e);

		}	
	}
	public void hit()
	{
		health--;
		if (health <= 0)
		{
			dead = true;
		}
		hit = true;
		hitTimer = System.nanoTime();
	}
	public void update()
	{
		if (slow)
		{
			x+=dx*0.25;
			y+=dy*0.25;
		}
		else if (fast)
		{
			x+= dx*3;
			y+= dy*3;
		}
		else if (!slow && !fast)
		{	
			x+=dx;
			y+=dy;
		}
		if (!ready)
			
		{
			if (x>r && x< GamePanel.WIDTH - r &&y > r && y< GamePanel.HEIGHT - r )
			{
				ready = true;
			}
		}
		if(x<r && dx <0) dx = -dx;
		if(y<r && dy <0) dy = -dy;
		if(x>GamePanel.WIDTH - r && dx > 0) dx = -dx;
		if(y>GamePanel.HEIGHT - r && dy > 0) dy = -dy;
		
		if (hit){
			long elapsed = (System.nanoTime() - hitTimer) /1000000;
			if (elapsed > 50)
				hit = false;
				hitTimer = 0;
		}
		
	}
	public void draw(Graphics2D g)
	{
		if (hit)
		{
			g.setColor(Color.WHITE);
			g.fillOval((int)(x-r), (int)(y-r), 2*r, 2*r);
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.WHITE.darker());
			g.drawOval((int)(x-r), (int)(y-r), 2*r, 2*r);
			g.setStroke(new BasicStroke(1));
		}
		else{
			if(fast)
			{
				g.setColor(new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
				g.fillOval((int)(x-r), (int)(y-r), 2*r, 2*r);
				g.setStroke(new BasicStroke(3));
				g.setColor(new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
				g.drawOval((int)(x-r), (int)(y-r), 2*r, 2*r);
				g.setStroke(new BasicStroke(1));
			}
			else
			{
				g.setColor(color1);
				g.fillOval((int)(x-r), (int)(y-r), 2*r, 2*r);
				g.setStroke(new BasicStroke(3));
				g.setColor(color1.darker());
				g.drawOval((int)(x-r), (int)(y-r), 2*r, 2*r);
				g.setStroke(new BasicStroke(1));
			}
		}
	}
	
}
