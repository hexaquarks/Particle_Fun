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
	private static final int RIGHT_WALL_X_POS = 691;
	private static final int LEFT_WALL_X_POS = 0;
	private static final int BOTTOM_WALL_Y_POS = 452;
	private static final int TOP_WALL_Y_POS = 0;
	private static final double COLLISION_DAMPING_FACTOR = 0.001;

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

	
	public double[] electrostaticForce(Particle p2) {
		double d = calculateDistance(p2);
		double forceMultiplier = k * this.charge * p2.charge / (d * d);
		return calculateForceComponents(p2, forceMultiplier, d);
	}
	
	public double[] gravitationalForce(Particle p2) {
		double d = calculateDistance(p2);
		double forceMultiplier = -g * this.mass * p2.mass / (d * d);
		return calculateForceComponents(p2, forceMultiplier, d);
	}
	
	private double calculateDistance(Particle p2) {
		double dx = this.x - p2.x;
		double dy = this.y - p2.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	private double[] calculateForceComponents(Particle p2, double forceMultiplier, double d) {
		double fx = forceMultiplier * (this.x - p2.x) / d;
		double fy = forceMultiplier * (this.y - p2.y) / d;
		return new double[] {fx, fy};
	}

	public void doCollisionWithOtherParticle(Particle p2) {
		double m1 = this.mass;
		double m2 = p2.mass;
	
		updateVelocityAfterCollision(p2, m1, m2);
		applyElasticForce(p2);
	}
	
	private void updateVelocityAfterCollision(Particle p2, double m1, double m2) {
		this.vx = -(m1 * this.vx + m2 * p2.vx + m2 * COLLISION_DAMPING_FACTOR * (p2.vx - this.vx)) / (m1 + m2);
		this.vy = -(m1 * this.vy + m2 * p2.vy + m2 * COLLISION_DAMPING_FACTOR * (p2.vy - this.vy)) / (m1 + m2);
	}
	
	private void applyElasticForce(Particle p2) {
		double d = calculateDistanceBetweenParticles(p2);
		double fx = COLLISION_DAMPING_FACTOR * (this.x - p2.x) * ((this.mass * p2.mass) / Math.pow(d, 0.8));
		double fy = COLLISION_DAMPING_FACTOR * (this.y - p2.y) * ((this.mass * p2.mass) / Math.pow(d, 0.8));
	
		this.vx += fx / this.mass;
		this.vy += fy / this.mass;
	}
	
	private double calculateDistanceBetweenParticles(Particle p2) {
		return Math.sqrt(Math.pow(this.x - p2.x, 2) + Math.pow(this.y - p2.y, 2));
	}
	
	public void edgeCollision(Particle p2) {
		updateVelocityAfterEdgeCollision(p2);
	}
	
	private void updateVelocityAfterEdgeCollision(Particle p2) {
		if (isParticleCollidingHorizontally()) {
			this.vx = -this.vx * coefficientWall;
		}
	
		if (isParticleCollidingVertically()) {
			this.vy = -this.vy * coefficientWall;
		}
	}
	
	private boolean isParticleCollidingHorizontally() {
		return this.x + this.vx + this.radius > RIGHT_WALL_X_POS || 
		       this.x + this.vx < LEFT_WALL_X_POS;
	}
	
	private boolean isParticleCollidingVertically() {
		return this.y + this.vy + this.radius > BOTTOM_WALL_Y_POS || 
			   this.y + this.vy < TOP_WALL_Y_POS;
	}	
	
	public double velInit() {
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
	
	public boolean isCollidingOtherParticle(Particle other) {
		double d = calculateDistanceBetweenParticles(other);
		return (d <= (this.width + other.width) / 2);
	}
	
	public boolean isCollidingOtherParticle(Particle other, boolean flag) {
		return Math.abs((this.x + this.vx) - (other.x + other.vx)) < width
				&& Math.abs((this.y + this.vy) - (other.y + other.vy)) < height;
	}	
}