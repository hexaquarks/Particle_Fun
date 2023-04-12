package physics.collision;

import physics.particle.Particle; 

public interface Collision {
    void updateVelocity(Particle p1, Particle p2);
    void updateVelocity(Particle p1);
    boolean isColliding(Particle p1, Particle p2);
    boolean isColliding(Particle p1);
}