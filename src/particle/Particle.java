package particle;
import java.util.List;
import java.util.Random;


public class Particle{
	private double x;
    private double y;
    private double vx;
    private double vy;
    private double mass;
    private int charge;
    private double width;
    private double height;
    private double radius;

	public Particle(double x, double y, double vx, double vy,double mass, int charge) {
		this.x = x;
		this.y = y;
		this.width = mass / 10;
		this.height = mass / 10;		
		this.vx = vx;
		this.vy = vy;
		this.mass = mass;
		this.charge = charge;
		this.radius = this.width / 2;
	}
	
	private double velInit() {
		Random rand = new Random();
		double val = rand.nextDouble() * 0.5;
		return rand.nextBoolean() ? val : -val;
	}
	
	public void reinitializeVel(List<Particle> particles) {
		for (Particle p : particles) {
			p.vx = velInit();
			p.vy = velInit();
		}
	}
	
	public String toString() {
		return "x :\t" + this.x + "  y :\t" + this.y + "  velX  : \t" + this.vx + "  velY  : \t" + this.vy;
	}
	
	public void moveParticleOneTimeTick() {
		this.x += this.vx;
		this.y += this.vy;
	}

	public double getX() { return this.x; }
	public double getY() { return this.y; }
	public double getWidth() { return this.width; }
	public double getHeight() { return this.height; }
	public double getVX() { return this.vx; }
	public double getVY() { return this.vy; }
	public double getRadius() { return this.radius; }
	public double getMass() { return this.mass; }
	public double getCharge() { return this.charge; }

	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }
	public void setVX(double vx) { this.vx = vx; }
	public void setVY(double vy) { this.vy = vy; }
	public void setWidth(double width) { this.width = width; }
	public void setHeight(double height) { this.height = height; }
}