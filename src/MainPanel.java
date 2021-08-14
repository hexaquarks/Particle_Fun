import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MainPanel extends JPanel {

	static Random rand = new Random();
	static ArrayList<Particle> particleList = new ArrayList<Particle>();
	static ArrayList<Particle> particleListCollided = new ArrayList<Particle>();
	static double newDirY = 0, newDirX = 0;
	static double prevY, prevX;
	static int timeTick = 1000 / 60; // fps normal
	static int collisionsPerSecond; // still unused
	static int fpsTimerCounter = 0;
	boolean collisionFlag, electricFlag, gravityFlag;
	boolean removeFlag;
	// boolean circleFlag, squareFlag, diamondFlag, spiralFlag; // booleans are by
	// deafault false

	ArrayList<String> shapeNames = new ArrayList<String>(
			Arrays.asList("Circle", "Square", "Diamond", "Spiral", "Loose Spiral"));

	public enum Flag {
		CIRCLE(false), SQUARE(false), DIAMOND(false), SPIRAL(false), LOOSESPIRAL(false);

		private boolean state;

		private Flag(boolean state) {
			this.state = state;
		}

		public void flipState() {
			this.state = !this.state;
		}

		public void setState(boolean state) {
			this.state = state;
		}

	}

	SampleController controller;

	static boolean testing;
	ShapeManager shape;

	public static boolean shapeActivated() {
		for (Flag flag : Flag.values()) {
			if (flag.state)
				return true;
		}
		return false;
	}

	Timer fpsTimer = new Timer(timeTick, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			fpsTimerCounter++;
			// TODO make the flags into an array in the future

			if (shapeActivated()) {
				shape.checkArrival();
				for (int i = 0; i < particleList.size(); i++) {
					Particle p = particleList.get(i);
					p.x += p.vx;
					p.y += p.vy;
				}
			}
			// double[] information = statistics();
			// controller.setLabels(particleList.size(), information);
			collisionsPerSecond = (fpsTimerCounter % 60 == 0) ? 0 : collisionsPerSecond;
			repaint();
		}
	});

	Timer physicsTimer = new Timer(1, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			for (int i = 0; i < particleList.size(); i++) {
				Particle p1 = particleList.get(i);
				for (int j = 0; j < particleList.size(); j++) {
					Particle p2 = particleList.get(j);

					if (p1 == p2)
						continue;

					p1.edgeCollision(p2);
					applyForces(p1, p2);
					applyCollision(p1, p2);
				}
				p1.x += p1.vx;
				p1.y += p1.vy;
			}
		}
	});

	public void applyForces(Particle p1, Particle p2) {
		if (!electricFlag) {
			p1.applyForce(p1.electrostaticForce(p2));
			if (collisionFlag) {
				if (Math.abs(p1.getX() - p2.getX()) <= p1.getWidth() / 50000
						|| Math.abs(p1.getY() - p2.getY()) <= p1.getWidth() / 50000) {
				}
			}
		}
		if (!gravityFlag) {
			p1.applyForce(p1.gravitationalForce(p2));
		}
	}

	public void applyCollision(Particle p1, Particle p2) {

		if (p1.collide(p2) && !collisionFlag) {

			p1.velCollision(p2);
			p2.velCollision(p1);
			// TODO
			collisionsPerSecond++;

		}
	}

	public double[] statistics() {
		double totalElectricEnergy = 0, totalPotential = 0, totalCollisions = 0;

		for (Particle p1 : particleList) {
			for (Particle p2 : particleList) {
				if (p1 == p2)
					continue;

				double electricForce = Math.pow(p1.electrostaticForce(p2)[0], 2)
						+ Math.pow(p1.electrostaticForce(p2)[1], 2);
				totalElectricEnergy += electricForce;

				double potentialForce = Math.pow(p1.gravitationalForce(p2)[0], 2)
						+ Math.pow(p1.gravitationalForce(p2)[1], 2);
				totalPotential += potentialForce;
			}
		}
		return new double[] { totalElectricEnergy, totalPotential, collisionsPerSecond };

	}

	public void initializeParticles(int numberOfParticles, int mass, int charge) {

		for (int i = 0; i < numberOfParticles; i++) {
			int xPos, yPos;
			do {
				xPos = rand.nextInt((int) 530 - 100) + 50;
				yPos = rand.nextInt((int) 330 - 100) + 50;
				Particle p = new Particle(xPos, yPos, 0, 0, mass, charge); // mass charge at end
				particleList.add(p);
			} while (!particleAlreadyExists(xPos, yPos));

		}

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		for (int i = 0; i < particleList.size(); i++) {
			Particle particle = particleList.get(i);

			g2d.setColor(Color.WHITE);
			Shape circle = new Arc2D.Double(particle.getX(), particle.getY(), particle.getWidth(), particle.getHeight(),
					0, 360, Arc2D.CHORD);
			GradientPaint gp1 = new GradientPaint(5, 5, Color.red, 20, 20, Color.yellow, true);
			g2d.setPaint(gp1);
			g2d.fill(circle);

		}
	}

	public double dragForce(double pos, double prevPos) {
		double diff = Math.abs(prevPos - pos);
		/*
		 * The speed of the particle with respect to the drag distance of the mouse
		 * should follows approximatively a logarithmic curve.
		 */
		double a = Math.log(diff / 100 + 1);

		// check if drag is negative or null.
		a = (pos < prevPos) ? -a : 0;

		return a;
	}

	public boolean particleAlreadyExists(double x, double y) {

		boolean particleExistsAlready = false;
		for (int i = 0; i < particleList.size(); i++) {
			Particle particle = particleList.get(i);

			if (x >= particle.getX() - particle.getWidth() && x <= particle.getX() + 2 * particle.getWidth()
					&& y >= particle.getY() - particle.getHeight() && y <= particle.getY() + 2 * particle.getHeight()) {
				particleExistsAlready = true;
				break;
			}
		}
		return particleExistsAlready;

	}

	public ArrayList<Particle> particleToRemove(double x, double y) {
		ArrayList<Particle> particlesToRemove = new ArrayList<Particle>();
		for (int i = 0; i < particleList.size(); i++) {
			Particle particle = particleList.get(i);

			if (x >= particle.getX() - particle.getWidth() && x <= particle.getX() + 2 * particle.getWidth()
					&& y >= particle.getY() - particle.getHeight() && y <= particle.getY() + 2 * particle.getHeight()) {
				particlesToRemove.add(particle);
			}
		}
		return particlesToRemove;
	}

	public void b1Pressed(int mass, int charge) {
		initializeParticles(1, mass, charge);
	}

	public void b2Pressed() {
		removeFlag = !removeFlag;
	}

	public void b3Pressed() {
		collisionFlag = !collisionFlag;
	}

	public void b4Pressed() {
		electricFlag = !electricFlag;
	}

	public void b5Pressed() {
		gravityFlag = !gravityFlag;
	}

	public void shapeButtonPressed(String shapeType) {
		SwingUtilities.invokeLater(() -> {

			Flag currFlag = Flag.values()[shapeNames.indexOf(shapeType)];

			if (!currFlag.state) {
				for (Flag flag : Flag.values()) {
					flag.setState((flag == currFlag) ? true : false);
				}
				physicsTimer.stop();
				setInitialization((short) currFlag.ordinal());
			} else {
				currFlag.setState(false);
				physicsTimer.start();
				particleList.get(0).reinitializeVel(particleList);
				shape.reinitializeCoordinates();
			}
		});
	}

	public void setInitialization(short shapeType) {
		// 0 = circle , 1 = square, 2 = diamond
		shape.reinitializeCoordinates();
		if (shapeType == 0) {
			shape.circleCoords(particleList);
		} else if (shapeType == 1) {
			while (particleList.size() % 4 != 0) {
				initializeParticles(1, 100, 5);
			}
			shape.squareCoords(particleList);
		} else if (shapeType == 2) {
			shape.diamondCoords(particleList);
		} else if (shapeType == 3) {
			shape.spiralCoords(particleList);
		} else if (shapeType == 4) {
			shape.looseSpiralCoords(particleList);
		}

		shape.proximity(particleList);

		shape.setSpeed(particleList);

	}

	MainPanel() {

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {

				if (!particleAlreadyExists(e.getX(), e.getY()) && !removeFlag) {
					Particle p = new Particle(e.getX(), e.getY(), newDirX, newDirY, 100, 0);
					particleList.add(p);
				} else if (removeFlag) {
					ArrayList<Particle> particlesToRemove = particleToRemove(e.getX(), e.getY());
					for (int i = 0; i < particlesToRemove.size(); i++) {
						Particle particle = particlesToRemove.get(i);
						particleList.remove(particle);
					}
				}
				newDirX = 0;
				newDirY = 0;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				prevY = e.getY();
				prevX = e.getX();
			}
		});
		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {

				int y = e.getY(), x = e.getX();

				newDirY = dragForce(y, prevY);
				newDirX = dragForce(x, prevX);

			}
		});

	}

}