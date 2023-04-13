package model;
import physics.particle.Particle;

public class Point2D {
	
	private double x;
	private double y;
	private Particle particle;
	public Point2D(double x , double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() { return x; }
	public double getY() { return y; }
	public Particle getParticle() { return particle; }

	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }
	public void setParticle(Particle particle) { this.particle = particle; }
}
