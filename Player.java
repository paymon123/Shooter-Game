import javax.swing.JPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener{
	//FIELDS
	public static int WIDTH = 400;
	public static int HEIGHT = 400;
	private Thread thread;
	private boolean running;
	private BufferedImage image;
	private Graphics2D g;
	private int FPS = 30;
	private double averageFPS;
	public Player player;
	boolean collision;
	
	public static ArrayList<Bullet> bullets;
	public static ArrayList<Enemy> enemies;
	public static ArrayList<PowerUp> powerups;
	public static ArrayList<Explosion> explosions;
	public static ArrayList<Text> texts;
	
	private long waveStartTimer, waveStartTimerDiff, slowDownTimer, slowDownTimerDiff, fastTimer, fastTimerDiff, levelTimer, levelTimerDiff;
	private int waveNumber, waveDelay = 5000, slowDownLength = 6000, fastLength = 6000, levelLength = 60000, bonusTime;
	private boolean waveStart, bouncing;
	
	
	//CONSTRUCTOR
	public GamePanel(){
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
	}
	
	//METHODS
	public void addNotify()
	{
		super.addNotify();
		if (thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
		
	}
	public void run()
	{
		running = true;
		

		player = new Player();
		bullets = new ArrayList<Bullet>();
		enemies = new ArrayList<Enemy>();
		powerups = new ArrayList<PowerUp>();
		explosions = new ArrayList<Explosion>();
		texts = new ArrayList<Text>();
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	
		waveStartTimer = 0;
		waveStartTimerDiff = 0;
		waveStart = true;
		waveNumber = 0;
		

		
		
		long startTime;
		long URDTimeMillis;
		long waitTime;
		long totalTime = 0;
		
		int frameCount = 0;
		int maxFrameCount = 30;
		
		long targetTime = 1000/FPS;
		//GAME LOOP
		while(running)
		{
			startTime = System.nanoTime();
			gameUpdate();
			gameRender();
			gameDraw();
			
			URDTimeMillis = (System.nanoTime() - startTime)/1000000;
			
			waitTime = targetTime - URDTimeMillis;
			
			try {
				thread.sleep(waitTime);
			} catch (Exception e){}
			
			totalTime += System.nanoTime() - startTime;
			frameCount++;
			if (frameCount == maxFrameCount)
			{
				averageFPS = 1000.0/((totalTime/frameCount)/1000000);
				frameCount = 0;
				totalTime = 0;
			}
		}
		g.setColor(new Color(0,150, 255));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
		String s = "G A M E   O V E R";
		int length = (int) g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(s, (WIDTH - length)/2, HEIGHT/2-15);
		String sc = "Final Score: " + player.getScore();
		length = (int) g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(sc, (WIDTH - length)/2, HEIGHT/2 + 15);
		gameDraw();
	}
	public void gameUpdate()
	{
		

		//new wave //this code only runs once per wave
		if(waveStartTimer == 0 && enemies.size()== 0)
		{
			
			waveNumber++;
			waveStart = false;
			waveStartTimer = System.nanoTime();
			bouncing = false;
			bonusTime = 0;

		}
		else
		{

			waveStartTimerDiff = (System.nanoTime() - waveStartTimer)/1000000;
			if(waveStartTimerDiff > waveDelay)
			{

				
				if (enemies.size() == 0)
				{
					//reset the level
					levelTimer = System.nanoTime();
					levelTimerDiff = 0;
				}
				
				waveStart = true;
				waveStartTimer = 0;
				waveStartTimerDiff = 0;
				
				
				
			}
		}
		//create enemies
		if (waveStart && enemies.size()== 0)
		{
			createNewEnemies();
		}
		

		//player update
		player.update();
		
		//bullet update
		for (int i = 0; i<bullets.size();i++)
		{
			boolean remove = bullets.get(i).update();
			if(remove){
				bullets.remove(i);
				i--;
			}
		}
		
		//enemy update
		for (int i = 0; i<enemies.size();i++)
		{
			enemies.get(i).update();
		}
		//explosions update
		for (int i = 0; i < explosions.size(); i++)
		{
			boolean remove = explosions.get(i).update();
			if(remove)
			{
				explosions.remove(i);
				i--;
			}
		}
		//texts update
		for (int i = 0; i<texts.size(); i++)
		{
			boolean remove = texts.get(i).update();
			if(remove)
			{
				texts.remove(i);
			}
		}
		//check enemy to bullet collision
		for (int i = 0; i<bullets.size();i++)
		{
			Bullet b = bullets.get(i);
			double bx = b.getX();
			double by = b.getY();
			double br = b.getR();
			for (int j = 0; j<enemies.size();j++)
			{
				Enemy e = enemies.get(j);
				double ex = e.getX();
				double ey = e.getY();
				double er = e.getR();
				
				double dx = bx - ex;
				double dy = by - ey;
				double dist = Math.sqrt(dx*dx + dy*dy);
				if (dist < br + er + 1)
				{
					e.hit();
					bullets.remove(i);
					i--;
					collision = true;
	 				break;
				}
			}
		}
		//check dead player
		if (player.isDead())
		{
			running=false;
		}
		//check dead enemies
		for (int i = 0; i<enemies.size();i++)
		{
			if( enemies.get(i).isDead())
			{
				Enemy e = enemies.get(i);
				double rand = Math.random();
				if (rand < .02){
					powerups.add(new PowerUp(1, e.getX(), e.getY()));
				
				}
				else if (rand < 0.04){
					powerups.add(new PowerUp(2, e.getX(), e.getY()));
				
				}
				else if (rand < 0.06){
					powerups.add(new PowerUp(3, e.getX(), e.getY()));
				
				}
				else if (rand < 0.08){
					powerups.add(new PowerUp(4, e.getX(), e.getY()));
				
				}
				else if (rand < 0.1){
					powerups.add(new PowerUp(5, e.getX(), e.getY()));
				
				}
				else if (rand < 0.12){
					powerups.add(new PowerUp(6, e.getX(), e.getY()));
				
				}
				else if (rand < 0.14){
					powerups.add(new PowerUp(7, e.getX(), e.getY()));
				
				}
				else if (rand < .16){
					powerups.add(new PowerUp(8, e.getX(), e.getY()));
			
				}
				else if (rand < 0.18){
					powerups.add(new PowerUp(9, e.getX(), e.getY()));
			
				}
	
				
				
				
				player.addScore(e.getRank() + e.getType());
				enemies.remove(i);
				i--;
				if (e.getRank() > 1)
				{
					e.split();
					
					
				}
				explosions.add(new Explosion(e.getX(),e.getY(), e.getR(), e.getR()+20));
			}
		}
		for (int i = 0; i<powerups.size();i++)
		{
			powerups.get(i).update();
		}
		
		//player enemy collision
		if (!player.isRecovering())
		{
			int px = player.getX();
			int py = player.getY();
			int pr = player.getR();
			for (int i = 0; i<enemies.size();i++)
			{
				Enemy e = enemies.get(i);
				double ex = e.getX();
				double ey = e.getY();
				double er = e.getR();
				
				double dx = px - ex;
				double dy = py - ey;
				double dist = Math.sqrt(dx*dx + dy*dy);
				
				if (dist < pr + er)
				{
					player.loseLife();
				}
			}
		}
		//player power-up collision
		{
			int px = player.getX();
			int py = player.getY();
			int pr = player.getR();
			for (int i = 0; i<powerups.size();i++)
			{
				PowerUp p = powerups.get(i);
				double x = p.getX();
				double y = p.getY();
				double r = p.getR();
				
				double dx = px - x;
				double dy = py - y;
				double dist = Math.sqrt(dx*dx + dy*dy);
				
				//collected powerup
				if (dist < pr + r)
				{
					int type = p.getType();
					
					if (type == 1)
					{
						player.gainLife();
						texts.add(new Text(player.getX(),player.getY(), 1500, "Extra Life"));
					}
					if (type == 2)
					{
						player.increasePower(1);
						texts.add(new Text(player.getX(),player.getY(), 1500, "+1 Power"));
					}
					if (type == 3)
					{
						player.gainShootingSpeed();
						texts.add(new Text(player.getX(),player.getY(), 1500, "Faster Shooting"));
					}
					if (type==4)
					{
						player.increasePower(2);
						texts.add(new Text(player.getX(),player.getY(), 1500, "+2 Power"));
					}
					//slowdown timer
					if (type == 5)
					{
						slowDownTimer = System.nanoTime();
						for (int j = 0; j<enemies.size(); j++)
						{
							enemies.get(j).setSlow(true);
						}
						texts.add(new Text(player.getX(),player.getY(), 1500, "Slow Down"));
					//fast timer
					}
					if (type == 6)
					{
						fastTimer = System.nanoTime();
						for (int j = 0; j<enemies.size(); j++)
						{
							enemies.get(j).setFast(true);
						}
						texts.add(new Text(player.getX(),player.getY(), 1500, "Speed Up!"));
						
					}
					//bouncing
					if (type == 7)
					{
						bouncing = true;
						for (int j = 0; j<bullets.size(); j++)
						{
							//set existing bullets to bouncing true
							bullets.get(j).setBounce(true);
						}
						texts.add(new Text(player.getX(),player.getY(), 1500, "Bouncing Bullets!"));
						
					}
					//extra points
					if (type ==8)
					{
						player.addScore(100);
						texts.add(new Text(player.getX(),player.getY(), 1500, "+100 Score"));
					}
					//bonus 10 seconds
					if (type == 9)
					{
						if (levelTimerDiff < 10000)
							bonusTime += levelTimerDiff;
						else if (levelTimerDiff >= 10000 && enemies.size()!=0)
							bonusTime +=10000;
						texts.add(new Text(player.getX(),player.getY(), 1500, "Extra Time"));
						
						
					}
					powerups.remove(i);
				}
			}
		//update bouncing bullets
			//constantly check new bullets and set them to bounce if bouncing is true
			if (bouncing)
					for (int j = 0; j<bullets.size(); j++)
					{
						bullets.get(j).setBounce(true);
					}
			//set bullets to not bounce once bouncing becomes default false
			if (!bouncing)
					for (int j = 0; j<bullets.size(); j++)
					{
						bullets.get(j).setBounce(false);
					}
		//update slowdown timer
		if (slowDownTimer != 0)
		{
			slowDownTimerDiff = (System.nanoTime()- slowDownTimer)/1000000;
			if (slowDownTimerDiff>slowDownLength)
			{
				slowDownTimer = 0;
				for (int j = 0; j<enemies.size(); j++)
				{
					enemies.get(j).setSlow(false);
				}
			}
		}
		//update fast timer
		if (fastTimer != 0)
		{
			fastTimerDiff = (System.nanoTime()- fastTimer)/1000000;
			if (fastTimerDiff>fastLength)
			{
				fastTimer = 0;
				for (int j = 0; j<enemies.size(); j++)
				{
					enemies.get(j).setFast(false);
				}
			}
		}
		//cap timer

		//update levelTimer
		if (levelTimer != 0)
		{
			 
			
			if(levelTimerDiff<levelLength && enemies.size()!=0)
			{
			levelTimerDiff = ((System.nanoTime()- levelTimer)/1000000)-bonusTime;
			
			}
			
			else if(enemies.size()==0 &&levelTimerDiff < levelLength)
			{
				levelTimerDiff +=1000;
				player.addScore(1);
				
				
			}

			
			if (levelTimerDiff > levelLength && enemies.size()!= 0)
			{
				running = false;
			
			}
	
		}
		}
	
	
	}
	public void gameRender()
	{
		//draw background
		g.setColor(new Color(0, 150, 255));
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		
		//draw slowdown screen
		if (slowDownTimer !=0)
		{
		g.setColor(new Color(255, 255, 255, 128));
		g.fillRect(0,0, WIDTH, HEIGHT);
		}
		//draw fast screen
		if (fastTimer !=0)
		{
			g.setColor(new Color(0, 0, 0));
			g.fillRect(0,0, WIDTH, HEIGHT);
		}
		//draw texts
		for (int i = 0; i<texts.size(); i++)
			{
				texts.get(i).draw(g);
			}
		
		//draw player
		player.draw(g);
			
		//draw bullets
		for (int i =0; i<bullets.size(); i++)
		{
			bullets.get(i).draw(g);
		}
		
		//draw enemies
		for (int i = 0; i<enemies.size();i++)
		{
			enemies.get(i).draw(g);
			
		}
		//draw powerups
		for (int i =0; i<powerups.size(); i++)
		{
			powerups.get(i).draw(g);
		}
		//draw wave number
		if (waveStartTimer!= 0)
		{
			g.setFont((new Font ("Comic Sans MS", Font.PLAIN, 18)));
			String s = "-  W A V E   " + waveNumber + "  -";
			int length = (int) g.getFontMetrics().getStringBounds(s,g).getWidth();
			int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
			if(alpha > 255) alpha = 255;
			g.setColor(new Color(255, 255, 255, alpha));;
			g.drawString(s, WIDTH/2 - length/2, HEIGHT/2);
		}
		//draw explosions
		for (int i = 0; i < explosions.size(); i++)
		{
			explosions.get(i).draw(g);
		}
		//draw player lives
		for (int i = 0; i < player.getLives(); i++)
		{
			g.setColor(Color.WHITE);
			g.fillOval(20 + (20*i), 40, player.getR()*2, player.getR()*2);
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.WHITE.darker());
			g.drawOval(20 + (20*i), 40, player.getR()*2, player.getR()*2);
			g.setStroke(new BasicStroke(1));
		}
		
		//draw player score
		g.setColor(Color.WHITE);
		g.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
		g.drawString("Score: " + player.getScore(), WIDTH - 100, 50);
		
		
		//draw player power
		g.setColor(Color.YELLOW);
		g.fillRect(20, 60, player.getPower() * 8, 8);
		g.setColor(Color.YELLOW.darker());
		g.setStroke(new BasicStroke(2));
		
		
		
		
		//draw empty rectangles
		for (int i = 0; i<player.getRequiredPower(); i++)
		{
			g.drawRect(20 + 8*i, 60, 8, 8);
		}
		g.setStroke(new BasicStroke(1));
		//draw slowdown timer
		if (slowDownTimer !=0)
		{
		g.setColor(Color.WHITE);
		g.drawRect(20, 80, 100, 8);
		g.fillRect(20, 80, (int) (100 - 100.0 * slowDownTimerDiff/slowDownLength), 8);
		}
		//draw fast timer
		if (fastTimer !=0)
		{
		g.setColor(Color.WHITE);
		g.drawRect(20, 80, 100, 8);
		g.fillRect(20, 80, (int) (100 - 100.0 * fastTimerDiff/fastLength), 8);
		}
		//draw level timer
		if (levelTimer !=0)
		{
		g.setColor(Color.WHITE);
		g.drawRect(20, 20, 360, 8);
		g.fillRect(20, 20, (int) (360 - 360.0 * levelTimerDiff/levelLength), 8);
		}
		
		
	}
	public void gameDraw()
	{
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}
	private void createNewEnemies()
	{
		enemies.clear();
		Enemy e;
		
		if(waveNumber == 1)
		{
			for(int i = 0; i<8; i++)
			{
				enemies.add(new Enemy(1,1));
			}
		}
		if(waveNumber == 2)
		{
			for(int i = 0; i<8; i++)
			{
				enemies.add(new Enemy(2,1));
			}
		}
		if(waveNumber == 3)
		{
			for(int i = 0; i<8; i++)
			{
				enemies.add(new Enemy(3,1));
			}
		}
		if(waveNumber == 4)
		{
			for(int i = 0; i<12; i++)
			{
				enemies.add(new Enemy(1,2));				
			}
		}
		if(waveNumber == 5)
		{
			
			for(int i = 0; i<4; i++)
			{
				enemies.add(new Enemy(2,2));				
			}
		}
		if(waveNumber ==6)
		{
			for(int i = 0; i<4; i++)
			{
				enemies.add(new Enemy(3,2));
			}
		}
		if(waveNumber == 7)
		{
			for(int i = 0; i<4; i++)
			{
				enemies.add(new Enemy(1,5));
			}
		}
		if(waveNumber == 8)
		{
			for(int i = 0; i<150; i++)
			{
				enemies.add(new Enemy(1,1));
			}
		}
		if(waveNumber == 9)
		{
			for(int i = 0; i<20; i++)
			{
				enemies.add(new Enemy(3,2));
			}
		}
		if (waveNumber == 10)
		{
			for(int i = 0; i<1; i++)
			{
				enemies.add(new Enemy(3,4));
		
		}
		
		if (waveNumber == 11)
		{
			for(int i = 0; i<20; i++)
			{
				enemies.add(new Enemy(3,2));
			}
		
			for(int i = 0; i<4; i++)
			{
				enemies.add(new Enemy(1,5));
			}
			for(int i = 0; i<4; i++)
			{
				enemies.add(new Enemy(2,2));
			}
		}
		}
	}
	public void keyPressed(KeyEvent key) {
		int keyCode = key.getKeyCode();
		if (keyCode == KeyEvent.VK_UP){player.setUp(true);}
		if (keyCode == KeyEvent.VK_LEFT){player.setLeft(true);}
		if (keyCode == KeyEvent.VK_DOWN){player.setDown(true);}
		if (keyCode == KeyEvent.VK_RIGHT){player.setRight(true);}
		if (keyCode == KeyEvent.VK_SPACE){player.setFiring(true);}
	}
	public void keyReleased(KeyEvent key) {
		int keyCode = key.getKeyCode();
		if (keyCode == KeyEvent.VK_UP){player.setUp(false);  }
		if (keyCode == KeyEvent.VK_LEFT){player.setLeft(false); }
		if (keyCode == KeyEvent.VK_DOWN){player.setDown(false); }
		if (keyCode == KeyEvent.VK_RIGHT){player.setRight(false); }
		if (keyCode == KeyEvent.VK_SPACE){player.setFiring(false);}
	}
	public void keyTyped(KeyEvent key) {
	
	}

	public void changeTime(int Time)
	{
		levelTimerDiff += Time*1000;
	}
}
