package collision;

import particle.Particle;

public class EdgeCollision implements Collision {
    private static final double WALL_DAMPING_COEFFICIENT = 0.6;
	private static final int RIGHT_WALL_X_POS = 691;
	private static final int LEFT_WALL_X_POS = 0;
	private static final int BOTTOM_WALL_Y_POS = 452;
	private static final int TOP_WALL_Y_POS = 0;

    @Override
    public void updateVelocity(Particle p) { 
        if (isParticleCollidingHorizontally(p)) {
            p.setVX(-p.getVX() * WALL_DAMPING_COEFFICIENT);
		}
	
		if (isParticleCollidingVertically(p)) {
            p.setVY(-p.getVY() * WALL_DAMPING_COEFFICIENT);
		}
    }

    @Override
    public void updateVelocity(Particle p1, Particle p2) {
        throw new UnsupportedOperationException("This method is not supported in ParticleCollision");
    }

    @Override
    public boolean isColliding(Particle p) {
        return isParticleCollidingHorizontally(p) || 
               isParticleCollidingVertically(p);
    }

    @Override
    public boolean isColliding(Particle p1, Particle p2) {
        throw new UnsupportedOperationException("This method is not supported in ParticleCollision");
    }

    public void edgeCollision(Particle p2) {
		
	}
	
	private boolean isParticleCollidingHorizontally(Particle p) {
		return p.getX() + p.getVX() + p.getRadius() > RIGHT_WALL_X_POS || 
		       p.getX() + p.getVX() < LEFT_WALL_X_POS;
	}
	
	private boolean isParticleCollidingVertically(Particle p) {
		return p.getY() + p.getVY() + p.getRadius() > BOTTOM_WALL_Y_POS || 
			   p.getY() + p.getVY() < TOP_WALL_Y_POS;
	}	
}
