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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
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
	boolean circleFlag, squareFlag, diamondFlag, spiralFlag; // booleans are by deafault false
	// Boolean circleFlag = false; 
	// Boolean squareFlag =  false; 
	// Boolean diamondFlag =  false; 
	// Boolean spiralFlag =  false; 

	// ArrayList<Boolean> shapeFlags = new ArrayList<Boolean>(Arrays.asList(circleFlag, squareFlag, diamondFlag, spiralFlag));
	// LinkedHashMap<String, Boolean> shapeFlags2 = new LinkedHashMap<String, Boolean>() {{
	// 	put("Square", squareFlag);
	// 	put("Diamond", diamondFlag);
	// 	put("Circle", circleFlag);
	// 	put("Spiral", spiralFlag);
	// }};

	// public enum Flag {
	// 	CIRCLE( false ),
	// 	SQUARE( false ),
	// 	DIAMOND( false ); // default state is false for all
	  
	// 	private boolean state;
	// 	private Flag(boolean state) {
	// 	  this.state = state;
	// 	}
	  
	// 	public void flipState() {
	// 	  this.state = !this.state;
	// 	}
	  
	// 	public void setState(boolean state) {
	// 	  this.state = state;
	// 	}
	//   }
	SampleController controller;


	static boolean testing;
	ShapeManager shape;

	Timer fpsTimer = new Timer(timeTick, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			fpsTimerCounter++;
			// TODO make the flags into an array in the future

			if (circleFlag || squareFlag || diamondFlag || spiralFlag) {
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
			int xPos;
			int yPos;
			do {
				xPos = rand.nextInt((int) 530 - 100) + 50;
				yPos = rand.nextInt((int) 330 - 100) + 50;
				Particle p = new Particle(xPos, yPos, 0, 0, mass, charge); // mass charge at end
				particleList.add(p);
			} while (!particleAlreadyExists(xPos, yPos));

		}

	}

	public void paintComponent(Graphics g) {
		if (testing == true) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			for (int i = 0; i < shape.coordinates.size(); i++) {
				Point2D point = shape.coordinates.get(i);

				g2d.setColor(Color.WHITE);
				Shape circle = new Arc2D.Double(point.x - particleList.get(0).width / 2,
						point.y - particleList.get(0).height / 2, particleList.get(0).width, particleList.get(0).height,
						0, 360, Arc2D.CHORD);
				g2d.fill(circle);
			}
		} else {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;

			for (int i = 0; i < particleList.size(); i++) {
				Particle particle = particleList.get(i);

				g2d.setColor(Color.WHITE);
				Shape circle = new Arc2D.Double(particle.getX(), particle.getY(), particle.getWidth(),
						particle.getHeight(), 0, 360, Arc2D.CHORD);
				GradientPaint gp1 = new GradientPaint(5, 5, Color.red, 20, 20, Color.yellow, true);
				g2d.setPaint(gp1);
				g2d.fill(circle);

			}
		}

	}

	public double dragForce(double pos, double prevPos) {
		double diff = Math.abs(prevPos - pos);
		// System.out.println("diff :\t"+ diff);

		double a = 0;
		if (diff >= 150) {
			a = 3.0;
		} else if (diff >= 10 && diff < 150) {
			a = diff / 50;
		} else if (diff > 0 && diff < 10) {
			a = 0.2;
		} else {
			a = 0;
		}
		if (pos < prevPos) { // neg
			a = -a;
		} else if (pos > prevPos) {
			a = a;
		} else {
			a = 0;
		}
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
			// Boolean currFlag=false;
			// System.out.println("shape is  "  + shapeFlags2.get(shapeType));
			// if key == shapeType then set value true and set all that do not match the key to false ? 

			if (shapeType.equals("Circle")) {
				circlePressed();
				// currFlag = circleFlag;
			} else if (shapeType.equals("Square")) {
				squarePressed();
				// currFlag = squareFlag;
			} else if (shapeType.equals("Diamond")) {
				diamondPressed();
				// currFlag = diamondFlag;
			} else if (shapeType.equals("Spiral")) {
				spiralPressed();
				// currFlag = spiralFlag;
			}

			// if (!currFlag) {
			// 	//set all false except currFlag
			// 	for(Boolean flag : shapeFlags) flag = ( flag == currFlag ) ? true : false ;
			// 	physicsTimer.stop();
			// 	setInitialization((short) 0);
			// 	for(Boolean flag : shapeFlags) System.out.println(flag);
			// } else {
			// 	for(Boolean flag : shapeFlags) flag = false;
			// 	physicsTimer.start();
			// 	particleList.get(0).reinitializeVel(particleList);
			// 	shape.reinitializeCoordinates();
			// }

		});
	}

	public void circlePressed() {
		if (circleFlag == false) {
			circleFlag = true;

			physicsTimer.stop();

			setInitialization((short) 0);
			squareFlag = false;
			diamondFlag = false;
		} else {
			diamondFlag=false;
			circleFlag = false;
			diamondFlag = false;
			squareFlag = false;

			physicsTimer.start();
			particleList.get(0).reinitializeVel(particleList);
			shape.reinitializeCoordinates();

		}
	}

	public void squarePressed() {
		if (squareFlag == false) {
			squareFlag = true;

			physicsTimer.stop();
			setInitialization((short) 1);

			circleFlag = false;
			diamondFlag = false;

		} else {
			diamondFlag=false;
			circleFlag = false;
			diamondFlag = false;
			squareFlag = false;

			physicsTimer.start();
			particleList.get(0).reinitializeVel(particleList);
			shape.reinitializeCoordinates();

		}
	}

	public void diamondPressed() {
		if (diamondFlag == false) {
			diamondFlag = true;

			physicsTimer.stop();
			setInitialization((short) 2);

			circleFlag = false;
			squareFlag = false;

		} else {
			diamondFlag=false;
			circleFlag = false;
			diamondFlag = false;
			squareFlag = false;

			physicsTimer.start();
			particleList.get(0).reinitializeVel(particleList);
			shape.reinitializeCoordinates();

		}
	}
	public void spiralPressed(){
		if (spiralFlag == false) {
			spiralFlag = true;

			physicsTimer.stop();
			setInitialization((short) 3);

			circleFlag = false;
			diamondFlag = false;
			squareFlag = false;

		} else {
			diamondFlag=false;
			circleFlag = false;
			diamondFlag = false;
			squareFlag = false;

			physicsTimer.start();
			particleList.get(0).reinitializeVel(particleList);
			shape.reinitializeCoordinates();

		}
	}

	public void setInitialization(short shapeType) {
		// 0 = circle , 1 = square, 2 = diamond
		shape.reinitializeCoordinates();
		if (shapeType == 0) {
			System.out.println("HERE");
			shape.circleCoords(particleList);
		} else if (shapeType == 1) {
			SwingUtilities.invokeLater(() -> {
			});
			while (particleList.size() % 4 != 0) {
				initializeParticles(1, 100, 5);
			}
			shape.squareCoords(particleList);
		} else if (shapeType == 2) {
			shape.diamondCoords(particleList);
		} else if (shapeType == 3) {
			shape.spiralCoords(particleList);
		}

		shape.proximity(particleList);

		shape.setSpeed(particleList);

	}

	MainPanel() {

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {

				if (!particleAlreadyExists(e.getX(), e.getY()) && !removeFlag && !circleFlag) {
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