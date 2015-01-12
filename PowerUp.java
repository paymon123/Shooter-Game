import java.awt.*;
import java.util.Random;
public class PowerUp {

	private double x, y;
	private int r, type;
	private Color color1;
	Random rand = new Random();
	
	//CONSTRUCTOR
	public PowerUp(int type, double x, double y)
	{
		this.type = type;
		this.x = x;
		this.y = y;

		if (type == 1){color1 = Color.GREEN; r = 3;}
		if (type == 2){color1 = Color.YELLOW; r = 2;}
		if (type == 3){color1 = Color.RED; r = 5;}
		if (type == 4){color1 = Color.YELLOW; r = 4;}
		if (type == 5){color1 = Color.WHITE; r=3;}
		if (type == 6){color1 = new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)); r=3;}
		if (type == 7){color1 = Color.BLUE; r=4;}
		if (type == 8){color1 = Color.GRAY; r=3;}
		if (type == 9){color1 = Color.PINK; r=3;}
	}
	
	
	public double getX(){return x;}
	public double getY(){return y;}
	public double getR(){return r;}
	public int getType(){return type;}
	
	public boolean update()
	{
		y+= 2;
		if (type == 6)
		{
			color1 = new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
		}
		if(y > GamePanel.HEIGHT + r) {return true;}
		return false;
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(color1);
		g.fillRect((int)(x-r), (int)(y-r), 2*r, 2*r);
		g.setStroke(new BasicStroke(3));
		g.setColor(color1.darker());
		g.drawRect((int)(x-r), (int)(y-r), 2*r, 2*r);
		g.setStroke(new BasicStroke(1));
	}
	
}
