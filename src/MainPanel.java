import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MainPanel extends JPanel {

	private static final int TIME_TICK = 1000 / 60;
	private static final double DEFAULT_SPIRAL_ANGLE = Math.PI * (1 + Math.sqrt(5) / 4);

	private List<Particle> particleList = new ArrayList<>();
	private double newDirY = 0; // y-direction of a new particle
	private double newDirX = 0; // x-direction of a new particle
	private double prevY; // tracker for y-value of the mouse
	private double prevX; // tracker for x-value of the mouse
	private boolean collisionFlag, electricFlag, gravityFlag; // flags for the forces
	private boolean isInParticleRemovalMode; // flag for removing particles

	private int collisionsPerSecond; // collisions per second for statistics
	private int fpsTimerCounter = 0; // counter to have the statistics at a fixed frequency

	private double spiralAngle = DEFAULT_SPIRAL_ANGLE;
	private double[] information; // array holding information for the statistics
	private ShapeType lastShape;
	private boolean tempFlag = true;

	private Timer fpsTimer = null;
	private Timer physicsTimer = null;

	/**
	 * Instances
	 */
	SampleController controller;
	private ShapeManager shape = new ShapeManager();

	/**
	 * Information associated with shapes
	 */
	List<String> shapeNames = new ArrayList<String>(
			Arrays.asList("Circle", "Square", "Diamond", "Spiral", "Loose Spiral", "Sunflower"));

	public enum Flag {
		CIRCLE(false), SQUARE(false), DIAMOND(false), SPIRAL(false),
		LOOSESPIRAL(false), SUNFLOWER(false);

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
		setupMouseListener();
		startTimers();
	}

	private void setupMouseListener() {
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				doMouseReleased(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				doMousePressed(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				doMouseDragged(e);
			}
		};
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}

	private void startTimers() {
		fpsTimer = new Timer(TIME_TICK, e -> {
			++fpsTimerCounter;

			if (ShapeType.AtLeastOneShapeIsActivated()) {
				if (shape.checkArrival())
					shape.setShapeIsDraggable(true);
				for (Particle p : particleList) {
					p.x += p.vx;
					p.y += p.vy;
				}
			}
			if (fpsTimerCounter % 60 == 0) {
				information = statistics();
				controller.setLabels(particleList.size(), information);
				collisionsPerSecond = 0;
			}
			repaint();
		});
		fpsTimer.start();

		physicsTimer = new Timer(2, e -> {
			for (Particle p1 : particleList) {
				for (Particle p2 : particleList) {
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
		});
		physicsTimer.start();
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
		if (ShapeType.SUNFLOWER.isSelected()) {
			changeSunflower();
		}
	}

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
		if (p1.isCollidingOtherParticle(p2) && !collisionFlag) {
			p1.doCollisionWithOtherParticle(p2);
			p2.doCollisionWithOtherParticle(p1);
			++collisionsPerSecond;
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
		double totalElectricEnergy = 0, totalPotential = 0;
	
		for (int i = 0; i < particleList.size(); i++) {
			Particle p1 = particleList.get(i);
	
			for (int j = i + 1; j < particleList.size(); j++) {
				Particle p2 = particleList.get(j);
	
				double[] electrostaticForce = p1.electrostaticForce(p2);
				double electricForce = Math.pow(electrostaticForce[0], 2) + Math.pow(electrostaticForce[1], 2);
				totalElectricEnergy += electricForce;
	
				double[] gravitationalForce = p1.gravitationalForce(p2);
				double potentialForce = Math.pow(gravitationalForce[0], 2) + Math.pow(gravitationalForce[1], 2);
				totalPotential += potentialForce;
			}
		}
		totalElectricEnergy *= 2;
		totalPotential *= 2;
	
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
		Random rand = new Random();

		for (int i = 0; i < numberOfParticles; ++i) {
			int xPos;
			int yPos;
			Particle p;

			do {
				xPos = rand.nextInt(530 - 100) + 50;
				yPos = rand.nextInt(330 - 100) + 50;
				double vx = rand.nextInt(2) - 1;
				double vy = rand.nextInt(2) - 1;

				p = new Particle(xPos, yPos, vx, vy, mass, charge);
			} while (particleAlreadyExists(xPos, yPos));

			particleList.add(p);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		for (Particle particle : particleList) {
			drawParticle(g2d, particle);
		}
	}

	private void drawParticle(Graphics2D g2d, Particle particle) {
		g2d.setColor(Color.WHITE);
		Shape circle = new Arc2D.Double(particle.x, particle.y, particle.width, particle.height, 0, 360, Arc2D.CHORD);
		GradientPaint gradientPaint = new GradientPaint(5, 5, Color.red, 20, 20, Color.yellow, true);
		g2d.setPaint(gradientPaint);
		g2d.fill(circle);
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
		return particleList.stream().anyMatch(
			particle -> isCoordinateWithingParticleBounds(x, y, particle)
		);
	}

	private void addParticle(double x, double y, double dirX, double dirY, double mass, int charge) {
		Particle p = new Particle(x, y, dirX, dirY, mass, charge);
		particleList.add(p);
	}

	private void removeParticlesAt(int x, int y) {
		List<Particle> particlesToRemove = particlesToRemove(x, y);
		for (Particle particle : particlesToRemove) {
			particleList.remove(particle);
		}
	}

	/**
	 * Method that generates a list of particles to remvoe given the area defined by
	 * a given point.
	 * 
	 * @param x x-value on the canvas
	 * @param y y-value on the canvas
	 * @return List<Particle> a list of the particles to remove defined within
	 *         the area of twice the width and height of a particle
	 */
	public List<Particle> particlesToRemove(double x, double y) {
		return particleList.stream()
				.filter(particle -> isCoordinateWithingParticleBounds(x, y, particle))
				.collect(Collectors.toList());
	}

	private boolean isCoordinateWithingParticleBounds(double x, double y, Particle p) {
		boolean xWithinBounds = x >= p.x - p.width && x <= p.x + 2 * p.width;
		boolean yWithinBounds = y >= p.y - p.height && y <= p.y + 2 * p.height;
		return xWithinBounds && yWithinBounds;
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
			isInParticleRemovalMode = !isInParticleRemovalMode;
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
	public void shapeButtonPressed(ShapeType selectedShapeRadioButton) {
		SwingUtilities.invokeLater(() -> {
			shape.setShapeIsDraggable(false);
			spiralAngle = DEFAULT_SPIRAL_ANGLE;
	
			if (selectedShapeRadioButton.isSelected()) {
				selectedShapeRadioButton.setIsSelected(false);
				physicsTimer.start();
				particleList.get(0).reinitializeVel(particleList);
				shape.reinitializeCoordinates();
			} else {
				Arrays.stream(ShapeType.values()).forEach(st -> st.setIsSelected(st == selectedShapeRadioButton));
				physicsTimer.stop();
				setInitialization(selectedShapeRadioButton);
			}
			lastShape = selectedShapeRadioButton;
		});
	}
	
	public void setInitialization(ShapeType shapeType) {
		shape.reinitializeCoordinates();
	
		switch (shapeType) {
			case CIRCLE:
				shape.getCircleCoords(particleList);
				break;
			case SQUARE:
			case DIAMOND:
				while (particleList.size() % 4 != 0) {
					initializeParticles(1, 100, 5);
				}
				if (shapeType == ShapeType.SQUARE) {
					shape.getSquareCoords(particleList);
				} else {
					shape.getDiamondCoords(particleList);
				}
				break;
			case SPIRAL:
				shape.getSpiralCoords(particleList);
				break;
			case LOOSE_SPIRAL:
				shape.getLooseSpiralCoords(particleList);
				break;
			case SUNFLOWER:
				shape.getSunflowerCoords(particleList, spiralAngle);
				break;
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
			SwingUtilities.invokeLater(() -> {
				shape.divide(particleList);
				shape.setDividedShapeCoodinates(particleList);
				shape.setProximity(particleList);
				shape.setSpeed(particleList);
			});
		}
	}

	/**
	 * Method that adds a new particle whenever the mouse is released
	 * 
	 * @param e the mouse position on the canvas
	 */
	private void doMouseReleased(MouseEvent e) {
		if (shape.shapeIsDraggable) {
			return;
		}

		int x = e.getX();
		int y = e.getY();

		if (isInParticleRemovalMode) {
			removeParticlesAt(x, y);
		} else if (!particleAlreadyExists(x, y)) {
			addParticle(x, y, newDirX, newDirY, 100, 0);
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
		int x = e.getX();
		int y = e.getY();
	
		newDirX = dragForce(x, prevX);
		newDirY = dragForce(y, prevY);
	
		if (shape.shapeIsDraggable) {
			double distance = computeDistance(x, y, shape.anchorX, shape.anchorY);
	
			if (distance >= 20) {
				shape.rotateShape(x, y, particleList);
			}
		}
	}
	
	private double computeDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}	

	public Timer getPhysicsTimer() {
		return physicsTimer;
	}

	public Timer getFpsTimer() {
		return fpsTimer;
	}

	public void setShape(ShapeManager newShape) {
		shape = newShape;
	}
}