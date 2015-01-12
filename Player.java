import java.awt.*;

public class Player {

	private int x, y, r, lives, speed, dx, dy, score, powerLevel, power;
	private boolean left, right, up, down, firing, recovering;
	private Color color1, color2;
	private long firingTimer, firingDelay, recoveryTimer;
	private int[] requiredPower = {1,2,3,4,5,6,7,8,9,10};
	
	
	public Player()
	{
		x = GamePanel.WIDTH/2;
		y = GamePanel.HEIGHT/2;
		r = 5;
		
		dx = 0;
		dy = 0;
		speed = 5;
		lives = 3;
		color1 = Color.WHITE;
		color2 = Color.RED;
		
		firing = false;
		firingTimer = System.nanoTime();
		firingDelay = 300;
		recovering = false;
		recoveryTimer = 0;
		score = 0;
		
	}
	public void setLeft(boolean b){left = b;}
	public void setRight(boolean b){right = b;}
	public void setUp(boolean b){up = b;}
	public void setDown(boolean b){down = b;}
	public void setFiring(boolean b){firing = b;}
	
	public void addScore(int i)
	{
		score+=i;
	}
	public void loseLife()
	{
		lives--;
		recovering = true;
		recoveryTimer = System.nanoTime();
	}
	public void gainLife()
	{
		lives++;
		
	}
	public void gainShootingSpeed()
	{
		firingDelay /= 1.1;
	}
	public void increasePower(int i)
	{
		power += i;
		if (powerLevel == 9)
		{
			if (power >requiredPower[powerLevel])
			{
				power = requiredPower[powerLevel];
			}
			return;
		
		}
	
		if(power >= requiredPower[powerLevel])
		{
			power-= requiredPower[powerLevel];
			powerLevel++;
		}
	}
	public int getPower(){return power;}
	public int getPowerLevel(){return powerLevel;}
	public int getRequiredPower(){return requiredPower[powerLevel];}
	
	public void update()
	{
		if (left) {dx = -speed;}
		if (right) {dx = speed;}
		if (up) {dy = -speed;}
		if (down){ dy = speed;}
	
		x += dx;
		y += dy;
		
		if (x<r) x = r;
		if (y<r) y = r;
		if (x>GamePanel.WIDTH - r) x = GamePanel.WIDTH - r;
		if (y>GamePanel.HEIGHT - r) y = GamePanel.HEIGHT - r;
		
		dx =0;
		dy = 0;
		
		
		if(firing)
		{
			
			long elapsed = (System.nanoTime() - firingTimer)/1000000;
			if (elapsed > firingDelay){

				if (powerLevel < 1 )
					GamePanel.bullets.add(new Bullet(270, x, y, 2));
				else if (powerLevel < 2)
				{
					GamePanel.bullets.add(new Bullet(270, x, y, 3));
				}
				else if (powerLevel <3)
				{
					GamePanel.bullets.add(new Bullet(270, x - 3, y, 2));
					GamePanel.bullets.add(new Bullet(270, x+3, y, 2));
				}
				else if (powerLevel <4)
				{
					GamePanel.bullets.add(new Bullet(270, x - 3, y, 3));
					GamePanel.bullets.add(new Bullet(270, x+3, y, 3));
				}
				else if (powerLevel <5)
				{
					GamePanel.bullets.add(new Bullet(265, x - 3, y,2));
					GamePanel.bullets.add(new Bullet(275, x+3, y,2));
					GamePanel.bullets.add(new Bullet(270, x, y,2));
					
				}
				else if (powerLevel <6)
				{
					GamePanel.bullets.add(new Bullet(265, x - 3, y,3));
					GamePanel.bullets.add(new Bullet(275, x+3, y,3));
					GamePanel.bullets.add(new Bullet(270, x, y,3));
					
				}
				else if (powerLevel < 7)
				{
					GamePanel.bullets.add(new Bullet(260, x -3, y,2));
					GamePanel.bullets.add(new Bullet(267, x-2, y,2));
					GamePanel.bullets.add(new Bullet(273, x+2, y,2));
					GamePanel.bullets.add(new Bullet(280, x+3, y,2));
					
				}
				else if (powerLevel < 8)
				{
					GamePanel.bullets.add(new Bullet(260, x -3, y,3));
					GamePanel.bullets.add(new Bullet(267, x-2, y,3));
					GamePanel.bullets.add(new Bullet(273, x+2, y,3));
					GamePanel.bullets.add(new Bullet(280, x+3, y,3));
				}
				else if (powerLevel < 9)
				{
					GamePanel.bullets.add(new Bullet(265, x - 4, y,2));
					GamePanel.bullets.add(new Bullet(275, x+4, y,2));
					GamePanel.bullets.add(new Bullet(270, x, y,2));
					GamePanel.bullets.add(new Bullet(273, x+2, y,2));
					GamePanel.bullets.add(new Bullet(267, x-2, y,2));
				}
				else if (powerLevel < 10)
				{
					GamePanel.bullets.add(new Bullet(265, x - 4, y,3));
					GamePanel.bullets.add(new Bullet(275, x+4, y,3));
					GamePanel.bullets.add(new Bullet(270, x, y,3));
					GamePanel.bullets.add(new Bullet(273, x+2, y,3));
					GamePanel.bullets.add(new Bullet(267, x-2, y,3));
				}
				firingTimer = System.nanoTime();
			
			}
		}
		//recovery timer logic
	if (recovering)
	{
		long elapsed = (System.nanoTime() - recoveryTimer)/1000000;
		if ( elapsed > 2000)
		{
			recovering = false;
			recoveryTimer = 0;
		}
	}
		
	}
	public void draw(Graphics2D g)
	{
		if (recovering)
		{
			g.setColor(color2);
			g.fillOval(x-r, y-r, 2*r, 2*r);
			
			g.setStroke(new BasicStroke(3));
			g.setColor(color2.darker());
			g.drawOval(x-r, y-r, 2*r, 2*r);
			g.setStroke(new BasicStroke(1));
		}
		else
		{
			g.setColor(color1);
			g.fillOval(x-r, y-r, 2*r, 2*r);
		
			g.setStroke(new BasicStroke(3));
			g.setColor(color1.darker());
			g.drawOval(x-r, y-r, 2*r, 2*r);
			g.setStroke(new BasicStroke(1));
		}
		
	}
	public int getLives(){return lives;}
	public int getX(){return x;}
	public int getY(){return y;}
	public int getR(){return r;}
	public int getScore(){return score;}
	public boolean isRecovering(){return recovering;}
	public boolean isDead(){return lives<=0;}
	
	
}
