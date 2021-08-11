import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;


public class Particle{
	double x, y;
	double width, height;
	double mass;
	double vx, vy;
	int charge;
	public double radius = 0;
	public double centerX = 0;
	public double centerY = 0;
	static Random rand = new Random();
	final double coefficientWall = 0.6;
	//test
	
	private final double k = 0.025;     // coulomb's constant
    private final double g = 0.00025;    // universal gravitational constant


	public Particle(double x, double y, double width
			, double height, double velX, double velY,double mass, int charge) {
		this.x = x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.vx=velX;
		this.vy = velY;
		this.mass=mass;
		this.charge = charge;
		this.radius = this.width/2;
		this.centerX = this.x + (this.width/2);
		this.centerY = this.y + (this.height/2);
	}
	
	public void applyForce(double[] force) {
		this.vx += force[0]/this.mass;
		this.vy += force[1]/this.mass;
	}

	public double[] electrostaticForce(Particle p2) {

		double d = Math.sqrt(
				Math.pow(this.x - p2.getX(), 2) +
				Math.pow(this.y - p2.getY(), 2));

		double fy = this.k * (this.y - p2.getY()) * (this.charge * p2.charge)
				/ Math.pow(d, 2);
		double fx = this.k * (this.x - p2.getX()) * (this.charge * p2.charge)
				/ Math.pow(d, 2);

		return new double[] {fx,fy};
	}
	

	public double[] gravitationalForce(Particle p2) {
		double d = Math.sqrt(
				Math.pow(this.x - p2.getX(), 2) +
				Math.pow(this.y-p2.getY(), 2));

		double fy = this.g * (this.y-p2.getY()) * (-(this.mass * p2.mass)
				/ Math.pow(d, 2));
		double fx = this.g * (this.x-p2.getX()) * (-(this.mass * p2.mass)
				/ Math.pow(d, 2));

		return new double[] {fx,fy};
	}

	public void velCollision(Particle p2) {
		double v1x=0, v1y =0, v2x=0, v2y=0;
		double m1 = this.mass , m2 = p2.getMass();

		v1x = this.getVelX();
		v2x = p2.getVelX();
		v1y = this.getVelY();
		v2y = p2.getVelY();


		this.vx = -(this.mass*this.vx + p2.mass*p2.vx
                        + p2.getMass() * 0.001 * (p2.vx - this.vx))
                        /(m1 + m2);
         this.vy = -(this.mass*this.vy + p2.mass*p2.vy
                        + p2.getMass() * 0.001 * (p2.vy - this.vy))
                        / (m1 + m2);

		double d = Math.sqrt(
				Math.pow(this.x - p2.getX(), 2) +
				Math.pow(this.y-p2.getY(), 2));

		double fy = 0.001 * (this.y-p2.y) * ((this.mass * p2.getMass())
				/ Math.pow(d, 0.8));
		double fx = 0.001 * (this.x-p2.x) * ((this.mass * p2.getMass())
				/ Math.pow(d, 0.8));
	

        this.vx += fx/this.mass;
        this.vy += fy/this.mass;
	}

	public double timeToHit(Particle p2) {

		double drx = Math.pow(p2.centerX - this.centerX , 2);
		double dry = Math.pow(p2.centerY - this.centerY , 2);
		double sig = this.radius + p2.radius;

		//maybe abs
		double dvx = Math.pow(p2.getVelX() - this.getVelX() , 2);
		double dvy = Math.pow(p2.getVelY() - this.getVelY() , 2);
		double drdr = Math.pow(drx, 2) + Math.pow(dry, 2);
		double dvdv = Math.pow(dvx, 2) + Math.pow(dvy, 2);
		double dvdr = dvx*drx + dvy*dry;
		double d = Math.pow(dvdv, 2) - dvdv*(drdr - Math.pow(sig, 2)); 

		return -((dvdr + Math.sqrt(d))/dvdv);

	}
	
	// public void edgeCollision(Particle p2) {
	// 	if(this.x + this.vx+this.radius > GameFrame.frame_width - this.width-GameFrame.sidePanel.getWidth()-6 || this.x + this.vx < 0) {
	// 		this.vx = -this.vx * this.coefficientWall;
	// 	}
	// 	if(this.y + this.vy+this.radius > GameFrame.frame_height - GameFrame.panelSouth.getHeight()-40-this.radius || this.y + this.vy < 0) {
	// 		this.vy = -this.vy * this.coefficientWall;
	// 	}
	// }
	public void edgeCollision(Particle p2) {
		// System.out.println("here L " + GameFrame.gamePanel.getSize().getWidth() );
		if(this.x + this.vx+this.radius > 691 || this.x + this.vx < 0) {
			this.vx = -this.vx * this.coefficientWall;
		}
		if(this.y + this.vy+this.radius > 452 || this.y + this.vy < 0){
			this.vy = -this.vy * this.coefficientWall;
		}
	}

	public static double velInit() {
		double val;
		if(rand.nextBoolean()) {
			val = rand.nextDouble()*0.5;
		} else {
			val = -rand.nextDouble()*0.5;
		}
		return val;
	}

	public void reinitializeVel(ArrayList<Particle> particles) {
		for(Particle p : particles) {
			p.vx = velInit();
			p.vy = velInit();
			
		}
		
	}
	public String toString() {
		return "x :\t" + this.x + "  y :\t" + this.y + "  velX  : \t" + this.vx + "  velY  : \t" + this.vy;
	}
	public double getMass() {
		return mass;
	}
	public void setMass(double mass) {
		this.mass=mass;
	}

	public double getVelX() {
		return vx;
	}

	public void setVelX(double velX) {
		this.vx = velX;
	}
	public double getVelY() {
		return vy;
	}

	public void setVelY(double velY) {
		this.vy = velY;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

    public boolean collide(Particle other){
    	double d = Math.sqrt(
				Math.pow(this.x - other.getX(), 2) +
				Math.pow(this.y-other.getY(), 2));
        return (d <= (this.width + other.width)/2);
    } 
	public boolean collide(Particle other, boolean flag) {
		return Math.abs((this.x+this.vx)-(other.x+other.vx)) < width 
				&& Math.abs((this.y+this.vy)-(other.y+other.vy)) < height;
	}
}