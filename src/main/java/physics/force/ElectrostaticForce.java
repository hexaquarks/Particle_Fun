package physics.force;

import physics.particle.Particle;

public class ElectrostaticForce implements Force{

    private static final double COULOMBS_CONSTANT = 0.025;     

    @Override
    public double[] calculateForce(Particle p1, Particle p2) {
        return electrostaticForce(p1, p2);
    }

    @Override 
	public void applyForce(Particle p, double[] force) {
        p.setVX(p.getVX() + (force[0] / p.getMass()));
        p.setVY(p.getVY() + (force[1] / p.getMass()));
	}

    private double[] electrostaticForce(Particle p1, Particle p2) {
		double d = calculateDistance(p1, p2);
		double forceMultiplier = COULOMBS_CONSTANT * p1.getCharge() * p2.getCharge() / (d * d);
		return calculateForceComponents(p1, p2, forceMultiplier, d);
	}

    private double calculateDistance(Particle p1, Particle p2) {
		double dx = p1.getX() - p2.getX();
		double dy = p1.getY() - p2.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	private double[] calculateForceComponents(Particle p1, Particle p2, double forceMultiplier, double d) {
		double fx = forceMultiplier * (p1.getX() - p2.getX());
		double fy = forceMultiplier * (p1.getY() - p2.getY());
		return new double[] {fx, fy};
	}
}
