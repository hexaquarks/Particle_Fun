package collision;

import particle.Particle; 

public class ParticleCollision implements Collision {

    private static final double COLLISION_DAMPING_FACTOR = 0.001;

    @Override
    public void updateVelocity(Particle p1) { 
        throw new UnsupportedOperationException("This method is not supported in ParticleCollision");
    }

    @Override
    public void updateVelocity(Particle p1, Particle p2) {
        double m1 = p1.getMass();
		double m2 = p2.getMass();
	
		updateVelocitiesAfterCollision(p1, p2, m1, m2);

        double d = calculateDistanceBetweenParticles(p1, p2);
		applyElasticForce(p1, p2, d);
        applyElasticForce(p2, p1, d);
    }

    @Override
    public boolean isColliding(Particle p) {
        throw new UnsupportedOperationException("This method is not supported in ParticleCollision");
    }

    @Override
    public boolean isColliding(Particle p1, Particle p2) {
        double d = calculateDistanceBetweenParticles(p1, p2);
		return (d <= (p1.getWidth() + p2.getWidth()) / 2);
    }
	
	private void updateVelocitiesAfterCollision(Particle p1, Particle p2, double m1, double m2) {
        double cacheVx = p1.getVX();
        double cacheVy = p2.getVY();
		p1.setVX(-(m1 * p1.getVX() + m2 * p2.getVX() + m2 * COLLISION_DAMPING_FACTOR * (p2.getVX() - p1.getVX())) / (m1 + m2));
        p1.setVY(-(m1 * p1.getVY() + m2 * p2.getVY() + m2 * COLLISION_DAMPING_FACTOR * (p2.getVY() - p1.getVY())) / (m1 + m2));

        p2.setVX(-(m2 * p2.getVX() + m1 * cacheVx + m1 * COLLISION_DAMPING_FACTOR * (cacheVx - p2.getVX())) / (m1 + m2));
        p2.setVY(-(m2 * p2.getVY() + m1 * cacheVy + m1 * COLLISION_DAMPING_FACTOR * (cacheVy - p2.getVY())) / (m1 + m2));
	}
	
	private void applyElasticForce(Particle p1, Particle p2, double d) {
		double fx = COLLISION_DAMPING_FACTOR * (p1.getX() - p2.getX()) * ((p1.getMass() * p2.getMass()) / Math.pow(d, 0.8));
		double fy = COLLISION_DAMPING_FACTOR * (p1.getY() - p2.getY()) * ((p1.getMass() * p2.getMass()) / Math.pow(d, 0.8));
        
        p1.setVX(p1.getVX() + (fx / p1.getMass()));
        p1.setVY(p1.getVY() + (fy / p1.getMass()));
	}

	private double calculateDistanceBetweenParticles(Particle p1, Particle p2) {
		return Math.sqrt(
            Math.pow(
                p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2
            )
        );
	}
    
}
