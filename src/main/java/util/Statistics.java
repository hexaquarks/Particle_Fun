package util;

import physics.particle.Particle;

import java.util.List;

import physics.force.ElectrostaticForce;
import physics.force.GravitationalForce;

public class Statistics {
    private int collisionsPerSecond = 0; 
    private static final ElectrostaticForce electrostaticForceManager = new ElectrostaticForce();
    private static final GravitationalForce gravitationalForceManager = new GravitationalForce();
    
    public void incrementCollisionsPerSecond() {
        ++collisionsPerSecond;
    }
    public int getCollisionsPerSecond() {
        return collisionsPerSecond;
    }

    public void resetCollisionsCounter() { 
        collisionsPerSecond = 0;
    }

	public double[] getCurrentStatistics(List<Particle> particleList) {
		double totalElectricEnergy = 0, totalPotential = 0;
	
		for (int i = 0; i < particleList.size(); i++) {
			Particle p1 = particleList.get(i);
	
			for (int j = i + 1; j < particleList.size(); j++) {
				Particle p2 = particleList.get(j);

				double[] electrostaticForce = electrostaticForceManager.calculateForce(p1, p2);
				double electricForce = Math.pow(electrostaticForce[0], 2) + Math.pow(electrostaticForce[1], 2);
				totalElectricEnergy += electricForce;
	
				double[] gravitationalForce = gravitationalForceManager.calculateForce(p1, p2);
				double potentialForce = Math.pow(gravitationalForce[0], 2) + Math.pow(gravitationalForce[1], 2);
				totalPotential += potentialForce;
			}
		}
		totalElectricEnergy *= 2;
		totalPotential *= 2;
	
		return new double[] { totalElectricEnergy, totalPotential, collisionsPerSecond };
	}
}
