import java.util.List;
import java.util.Random;


public class Particle{
	double 	x;
	double 	y;
	double 	vx;
	double 	vy;
	double 	mass;
	int 	charge;
	double 	width;
	double 	height;
	double 	radius = 0;
	Random 	rand = new Random();
	
	private static final double coefficientWall = 0.6;
	private static final double k = 0.025;     // coulomb's constant
    private static final double g = 0.00025;   // universal gravitational constant

	public Particle(double x, double y, double vx, double vy,double mass, int charge) {
		this.x = x;
		this.y = y;
		this.width = mass/10;
		this.height = mass/10;
		this.vx = vx;
		this.vy = vy;
		this.mass = mass;
		this.charge = charge;
		this.radius = this.width/2;
	}
	
	
	/** 
	 * @param force
	 */
	public void applyForce(double[] force) {
		this.vx += force[0]/this.mass;
		this.vy += force[1]/this.mass;
	}

	
	/** 
	 * @param p2
	 * @return double[]
	 */
	public double[] electrostaticForce(Particle p2) {

		double d = Math.sqrt(
				Math.pow(this.x - p2.x, 2) +
				Math.pow(this.y - p2.y, 2));

		double fy = k * (this.y - p2.y) * (this.charge * p2.charge)
				/ Math.pow(d, 2);
		double fx = k * (this.x - p2.x) * (this.charge * p2.charge)
				/ Math.pow(d, 2);

		return new double[] {fx,fy};
	}
	
	/** 
	 * @param p2
	 * @return double[]
	 */
	public double[] gravitationalForce(Particle p2) {
		double d = Math.sqrt(
				Math.pow(this.x - p2.x, 2) +
				Math.pow(this.y-p2.y, 2));

		double fy = g * (this.y-p2.y) * (-(this.mass * p2.mass)
				/ Math.pow(d, 2));
		double fx = g * (this.x-p2.x) * (-(this.mass * p2.mass)
				/ Math.pow(d, 2));

		return new double[] {fx,fy};
	}

	
	/** 
	 * @param p2
	 */
	public void velCollision(Particle p2) {
		double m1 = this.mass , m2 = p2.mass;

		this.vx = -(this.mass*this.vx + p2.mass*p2.vx
                        + p2.mass * 0.001 * (p2.vx - this.vx))
                        /(m1 + m2);
         this.vy = -(this.mass*this.vy + p2.mass*p2.vy
                        + p2.mass * 0.001 * (p2.vy - this.vy))
                        / (m1 + m2);

		double d = Math.sqrt(
				Math.pow(this.x - p2.x, 2) +
				Math.pow(this.y-p2.y, 2));

		double fy = 0.001 * (this.y-p2.y) * ((this.mass * p2.mass)
				/ Math.pow(d, 0.8));
		double fx = 0.001 * (this.x-p2.x) * ((this.mass * p2.mass)
				/ Math.pow(d, 0.8));
	

        this.vx += fx/this.mass;
        this.vy += fy/this.mass;
	}
	
	/** 
	 * @param p2
	 */
	public void edgeCollision(Particle p2) {
		if(this.x + this.vx+this.radius > 691 || this.x + this.vx < 0) {
			this.vx = -this.vx * coefficientWall;
		}

		if(this.y + this.vy+this.radius > 452 || this.y + this.vy < 0){
			this.vy = -this.vy * coefficientWall;
		}
	}

	
	/** 
	 * @return double
	 */
	public double velInit() {
		double val;
		if (rand.nextBoolean()) {
			val = rand.nextDouble()*0.5;
		} else {
			val = -rand.nextDouble()*0.5;
		}
		return val;
	}

	
	/** 
	 * @param particles
	 */
	public void reinitializeVel(List<Particle> particles) {
		for(Particle p : particles) {
			p.vx = velInit();
			p.vy = velInit();
		}
	}
	
	/** 
	 * @return String
	 */
	public String toString() {
		return "x :\t" + this.x + "  y :\t" + this.y + "  velX  : \t" + this.vx + "  velY  : \t" + this.vy;
	}
    
	/** 
	 * @param other
	 * @return boolean
	 */
	public boolean collide(Particle other){
    	double d = Math.sqrt(
				Math.pow(this.x - other.x, 2) +
				Math.pow(this.y-other.y, 2));
        return (d <= (this.width + other.width)/2);
    } 
	
	/** 
	 * @param other
	 * @param flag
	 * @return boolean
	 */
	public boolean collide(Particle other, boolean flag) {
		return Math.abs((this.x+this.vx)-(other.x+other.vx)) < width 
				&& Math.abs((this.y+this.vy)-(other.y+other.vy)) < height;
	}
}