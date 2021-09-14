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

	ArrayList<Particle> particleList = new ArrayList<Particle>();
	Random rand = new Random();
	double newDirY = 0; // y-direction of a new particle
	double newDirX = 0; // x-direction of a new particle
	double prevY; // tracker for y-value of the mouse
	double prevX; // tracker for x-value of the mouse
	boolean collisionFlag, electricFlag, gravityFlag; // flags for the forces
	boolean removeFlag; // flag for removing particles
	int timeTick = 1000 / 60; // 60 FPS
	int collisionsPerSecond; // collisions per second for statistics
	int fpsTimerCounter = 0; // counter to have the statistics at a fixed frequency

	double spiralAngle = Math.PI * (1 + Math.sqrt(5) / 4); // base angle for sunflower shape
	double[] information; // array holding information for the statistics
	double currAnchorX; // x-value for the mouse anchor whence rotating a fixed shape
	double currAnchorY; // y-value for the mouse anchor whence rotating a fixed shape
	String lastShape;
	boolean tempFlag = true;

	/**
	 * Instances
	 */
	SampleController controller;
	ShapeManager shape;

	/**
	 * Information associated with shapes
	 */
	ArrayList<String> shapeNames = new ArrayList<String>(
			Arrays.asList("Circle", "Square", "Diamond", "Spiral", "Loose Spiral", "Sunflower"));

	public enum Flag {
		CIRCLE(false), SQUARE(false), DIAMOND(false), SPIRAL(false), LOOSESPIRAL(false), SUNFLOWER(false);

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

	/**
	 * Constructor with mouse events embeed on it.
	 * 
	 * @param controller main instance of the JavaFX controller where the components
	 *                   events are handled.
	 */
	public MainPanel(SampleController controller) {
		this.controller = controller;

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				doMouseReleased(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				doMousePressed(e);
			}
		});
		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				doMouseDragged(e);
			}
		});

	}

	/**
	 * Seter that changes the angle to be used in the computation of the coordinates
	 * associated with the spiral shape. Each angle outputs a different shape on the
	 * canvas.
	 * 
	 * @param value the angle value in radian, taken from the spiralAngle button
	 *              inside SampleController.java
	 */
	public void setSpiralAngle(double value) {
		this.spiralAngle = value;

		if (Flag.SUNFLOWER.state) {
			changeSunflower();
		}
	}

	/**
	 * Method that sets one of the shape activated, that is the particles begin to
	 * form a shape or are already in the form of a shape
	 * 
	 * @return boolean true if the particles are in the process of forming a shape
	 *         or already in a shape, false otherwise
	 */
	public static boolean shapeActivated() {
		for (Flag flag : Flag.values()) {
			if (flag.state)
				return true;
		}
		return false;
	}

	public void setAllFlagsFalse() {
		for (Flag flag : Flag.values()) {
			flag.setState(false);
		}
	}

	/**
	 * FPS timer that performs the necessary graphical updates every 1000/16
	 * millisecond.
	 */
	Timer fpsTimer = new Timer(timeTick, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			fpsTimerCounter++;

			if (shapeActivated()) {
				if (shape.checkArrival())
					shape.setShapeIsDraggable(true);

				for (int i = 0; i < particleList.size(); i++) {
					Particle p = particleList.get(i);
					p.x += p.vx;
					p.y += p.vy;
				}
			}
			if (fpsTimerCounter % 60 == 0) {
				information = statistics();
				controller.setLabels(particleList.size(), information);
				collisionsPerSecond = 0;
			}
			// collisionsPerSecond = (fpsTimerCounter % 60 == 0) ? 0 : collisionsPerSecond;
			repaint();
		}
	});

	/**
	 * Physics timer that performs the necessary calculators through a 2 millisecond
	 * thread
	 */
	Timer physicsTimer = new Timer(2, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			for (int i = 0; i < particleList.size(); i++) {
				Particle p1 = particleList.get(i);

				for (int j = 0; j < particleList.size(); j++) {
					Particle p2 = particleList.get(j);

					if (p1 == p2)
						continue;

					p1.edgeCollision(p2);

					if (!electricFlag || !gravityFlag)
						applyForces(p1, p2);

					applyCollision(p1, p2);
				}
				p1.x += p1.vx;
				p1.y += p1.vy;
			}
		}
	});

	/**
	 * Method that applies the forces on the particles if the forces are activated
	 * 
	 * @param p1 a particle present on the canvas
	 * @param p2 another particle present on the canvas
	 */
	public void applyForces(Particle p1, Particle p2) {
		if (!electricFlag) {
			p1.applyForce(p1.electrostaticForce(p2));
			if (collisionFlag) {
				if (Math.abs(p1.x - p2.x) <= p1.width / 50000 || Math.abs(p1.y - p2.y) <= p1.width / 50000) {
				}
			}
		}
		if (!gravityFlag) {
			p1.applyForce(p1.gravitationalForce(p2));
		}
	}

	/**
	 * Method that performs the logistics behind a collision between 2 particles
	 * TODO n-particles collision maybe ?
	 * 
	 * @param p1 a particle that is present on the canvas
	 * @param p2 another particle that is present on the canvas
	 */
	public void applyCollision(Particle p1, Particle p2) {
		if (p1.collide(p2) && !collisionFlag) {
			p1.velCollision(p2);
			p2.velCollision(p1);
			collisionsPerSecond++;
		}
	}

	/**
	 * Method that performs the calculations for the statistical information display
	 * 
	 * @return double[] 3 elements arry representing the total electric energy
	 *         computed, the total potential energy computed and the number of
	 *         collisions per second.
	 */
	public double[] statistics() {
		// TODO reduce this thing, it's waayy to bad for performance.
		double totalElectricEnergy = 0, totalPotential = 0;

		for (int i = 0; i < particleList.size(); i++) {
			Particle p1 = particleList.get(i);

			for (int j = 0; j < particleList.size(); j++) {
				Particle p2 = particleList.get(j);

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

	/**
	 * Method that initializes a number of particles on the canvas
	 * 
	 * @param numberOfParticles number of particles to be spawned
	 * @param mass              the mass to be given to each new particle
	 * @param charge            the charge to be given to each new particle
	 */
	public void initializeParticles(int numberOfParticles, int mass, int charge) {
		for (int i = 0; i < numberOfParticles; i++) {
			int xPos, yPos;

			do {
				xPos = rand.nextInt((int) 530 - 100) + 50;
				yPos = rand.nextInt((int) 330 - 100) + 50;
				Particle p = new Particle(xPos, yPos, rand.nextInt(2) - 1, rand.nextInt(2) - 1, mass, charge); // mass
																												// charge
																												// at
																												// end
				particleList.add(p);
			} while (!particleAlreadyExists(xPos, yPos));
		}
	}

	/**
	 * Method that paints the particles on the canvas
	 * 
	 * @param g paint componet instance associated with this JPanel
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		for (int i = 0; i < particleList.size(); i++) {
			Particle particle = particleList.get(i);

			g2d.setColor(Color.WHITE);
			Shape circle = new Arc2D.Double(particle.x, particle.y, particle.width, particle.height, 0, 360,
					Arc2D.CHORD);
			GradientPaint gp1 = new GradientPaint(5, 5, Color.red, 20, 20, Color.yellow, true);
			g2d.setPaint(gp1);
			g2d.fill(circle);
		}
	}

	/**
	 * Method that sets a speed in (x,y) direction for a particle ,given the
	 * magnitude and the direction described by a mouse dragging.
	 * 
	 * @param pos     current position of the mouse on this canvas
	 * @param prevPos previous position of the mouse on this canvas (the anchor)
	 * @return double a value for the speed that follows a logarithmic function with
	 *         a stretching factor
	 */
	public double dragForce(double pos, double prevPos) {
		double diff = Math.abs(prevPos - pos);
		double a = Math.log(diff / 100 + 1);

		a = (pos < prevPos) ? -a : 0;

		return a;
	}

	/**
	 * Method that verifies wether a particle already exists for a given point.
	 * 
	 * @param x x-value on the canvas
	 * @param y y-value on the canvas
	 * @return boolean true if a particle exists at this (x,y), false otherwise
	 */
	public boolean particleAlreadyExists(double x, double y) {
		boolean particleExistsAlready = false;

		for (int i = 0; i < particleList.size(); i++) {
			Particle particle = particleList.get(i);

			if (x >= particle.x - particle.width && x <= particle.x + 2 * particle.width
					&& y >= particle.y - particle.height && y <= particle.y + 2 * particle.height) {
				particleExistsAlready = true;
				break;
			}
		}
		return particleExistsAlready;
	}

	/**
	 * Method that generates a list of particles to remvoe given the area defined by
	 * a given point.
	 * 
	 * @param x x-value on the canvas
	 * @param y y-value on the canvas
	 * @return ArrayList<Particle> a list of the particles to remove defined within
	 *         the area of twice the width and height of a particle
	 */
	public ArrayList<Particle> particleToRemove(double x, double y) {
		ArrayList<Particle> particlesToRemove = new ArrayList<Particle>();
		for (int i = 0; i < particleList.size(); i++) {
			Particle particle = particleList.get(i);

			if (x >= particle.x - particle.width && x <= particle.x + 2 * particle.width
					&& y >= particle.y - particle.height && y <= particle.y + 2 * particle.height) {
				particlesToRemove.add(particle);
			}
		}
		return particlesToRemove;
	}

	/**
	 * Method that inverts the flags associated with the forces
	 * 
	 * @param force string representing a force selected from the controller
	 */
	public void forcesButtonsPressed(String force) {
		if (force.equals("Collision"))
			collisionFlag = !collisionFlag;
		else if (force.equals("Electrostatics"))
			electricFlag = !electricFlag;
		else
			gravityFlag = !gravityFlag;
	}

	/**
	 * Method that initializes a number of particles whenever Add Particle button is
	 * pressed from controller
	 * 
	 * @param mass     the mass selected from the slider
	 * @param charge   the charge selected from the slider
	 * @param quantity the quantity selected with the Add Particle button
	 */
	public void addParticleButtonPressed(int mass, int charge, int quantity) {
		initializeParticles(quantity, mass, charge);
	}

	/**
	 * Method that allows the removal of a particle (Remove Particle button) or
	 * removes all the particles from this canvas (Remove All Button)
	 * 
	 * @param number button selected from the controller: 1 - remove one particle ,
	 *               !1 - remove all particles from this canvas
	 */
	public void removeParticleButtonPressed(String number) {
		if (number.equals("one"))
			removeFlag = !removeFlag;
		else
			this.particleList = new ArrayList<Particle>();
	}

	/**
	 * Method that, given the `shapeType` input, sets a shape flag to true and calls
	 * the initialization process associated with the particles arranging into the
	 * shape.
	 * 
	 * @param shapeType string with the name of the shape e.g. "Circle" , "Spiral" ,
	 *                  ...
	 */
	public void shapeButtonPressed(String shapeType) {
		SwingUtilities.invokeLater(() -> {
			shape.setShapeIsDraggable(false);
			spiralAngle = Math.PI * (1 + Math.sqrt(5) / 4);

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

			lastShape = shapeType;
		});
	}

	/**
	 * Method that sets the coordinates of the shape (from the `shapeType` input)
	 * and sets the speed of the particles towards those coordinates.
	 * 
	 * @param shapeType number associated with the shape. The number follows the
	 *                  order of the elements of ArrayList<String> shapeName global.
	 */
	public void setInitialization(short shapeType) {
		// 0 = circle , 1 = square, 2 = diamond, ...
		shape.reinitializeCoordinates();

		if (shapeType == 0) {
			shape.getCircleCoords(particleList);
		} else if (shapeType == 1 || shapeType == 2) {
			while (particleList.size() % 4 != 0) {
				initializeParticles(1, 100, 5);
			}
			if (shapeType == 1)
				shape.getSquareCoords(particleList);
			else
				shape.getDiamondCoords(particleList);
		} else if (shapeType == 3) {
			shape.getSpiralCoords(particleList);
		} else if (shapeType == 4) {
			shape.getLooseSpiralCoords(particleList);
		} else if (shapeType == 5) {
			shape.getSunflowerCoords(particleList, spiralAngle);
		}

		if (tempFlag) {
			shape.setProximity(particleList);
			shape.setSpeed(particleList);
		}
	}

	/**
	 * Method that resets the sunflower shape arragnement of the particles, vien the
	 * spiralAngle global.
	 */
	public void changeSunflower() {
		SwingUtilities.invokeLater(() -> {
			shape.reinitializeCoordinates();
			shape.getSunflowerCoords(particleList, spiralAngle);
			shape.setProximity(particleList);
			shape.setSpeed(particleList);
		});
	}

	public void divideShape() {
		if (shape.shapeIsDraggable) {
			shape.divide(particleList);

			// recompute the coordiantes given the new particle size
			tempFlag = false;

			// Flag currFlag = Flag.values()[shapeNames.indexOf(lastShape)];
			// for (Flag flag : Flag.values()) {
			// flag.setState((flag == currFlag) ? true : false);
			// }
			// physicsTimer.stop();
			// setInitialization((short) currFlag.ordinal());
			shapeButtonPressed(lastShape);

			SwingUtilities.invokeLater(() -> {
				shape.setDividedShapeCoodinates(particleList);
				shape.setProximity(particleList);
				shape.setSpeed(particleList);
			});
			setAllFlagsFalse();
			tempFlag = true;

			// shape.setShapeIsDraggable(false);
		}
	}

	/**
	 * Method that adds a new particle whenever the mouse is released
	 * 
	 * @param e the mouse position on the canvas
	 */
	public void doMouseReleased(MouseEvent e) {
		if (shape.shapeIsDraggable)
			return;

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

	/**
	 * Method that updates the mouse position tracker, and sets the anchor if we can
	 * rotate the shape
	 * 
	 * @param e the mouse position on the canvas
	 */
	public void doMousePressed(MouseEvent e) {
		prevY = e.getY();
		prevX = e.getX();

		if (shape.shapeIsDraggable) {
			shape.setAnchor(e.getX(), e.getY());
			shape.startAngle = shape.getAngle(shape.center.x, shape.center.y, shape.anchorX, shape.anchorY);
		}
	}

	/**
	 * Method calculates the new velocity of a particle before initialization, and
	 * performs the shape rotation if allowed.
	 * 
	 * @param e the mouse position on the canvas
	 */
	public void doMouseDragged(MouseEvent e) {
		int y = e.getY(), x = e.getX();

		newDirY = dragForce(y, prevY);
		newDirX = dragForce(x, prevX);

		if (shape.shapeIsDraggable) {
			double temp = Math.sqrt(Math.pow((e.getX() - shape.anchorX), 2) + Math.pow((e.getY() - shape.anchorY), 2));

			if (temp >= 20)
				shape.rotateShape(e.getX(), e.getY(), particleList);
		}
	}

}