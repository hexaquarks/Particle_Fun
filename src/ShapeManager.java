import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import model.Point2D;
import physics.particle.Particle;

/**
 * ShapeManager.java
 * 
 */

public class ShapeManager {
	String shapeType; // circle , square, pentagon , hexagon
	Point2D center;
	List<Point2D> coordinates = new ArrayList<>();
	List<List<Point2D>> dividedShapeCoords = new ArrayList<>(Arrays.asList(coordinates));

	boolean shapeIsDraggable;
	double currentAngle;
	double startAngle;
	double anchorX;
	double anchorY;

	double shapeWidth = 0;
	double shapeHeight = 0;

	/**
	 * Constructor
	 * 
	 * @param panelSize
	 */
	public ShapeManager(Point2D panelSize) {
		this.center = panelSize;
	}

	public ShapeManager() {
	}

	/**
	 * Method to calculate the angle formed between the mouse point and the origin
	 * point.
	 * 
	 * @param xc x-value of the origin (center in this case)
	 * @param yc y-value of the origin (center in this case)
	 * @param x  x-value of the current point (pointed with the mouse)
	 * @param y  y-value of the current point (pointed with the mouse)
	 * @return double the angle
	 */
	public double getAngle(double xc, double yc, double x, double y) {
		double dy = y - yc;
		double dx = x - xc;
		return dx == 0 ? dy >= 0 ? Math.PI / 2 : -Math.PI / 2 : Math.atan(dy / dx) + (dx < 0 ? Math.PI : 0);
	}

	public List<Point2D> getCopy() {
		return this.coordinates.stream()
				.map(point -> new Point2D(point.getX(), point.getY()))
				.collect(Collectors.toList());
	}

	/**
	 * Method that sets the value for the anchor, to be used for shape rotation
	 * through the mouse.
	 * 
	 * @param x the x-value for the anchor
	 * @param y the y-value for the anchor
	 */
	public void setAnchor(double x, double y) {
		this.anchorX = x;
		this.anchorY = y;
	}

	/**
	 * Sets the flag for the shapes to be rotatable throught the mouse.
	 * 
	 * @param particles the list of particles on the canvas
	 */
	public void setShapeIsDraggable(boolean shapeIsDraggable) {
		this.shapeIsDraggable = shapeIsDraggable;
	}

	/**
	 * Method that computes the coordinates on the canvas that form a circular
	 * shape.
	 * 
	 * @param particles the list of particles elements present on the canvas
	 */
	public void getCircleCoords(List<Particle> particles) {
		int n = particles.size();
		double alpha = Math.toRadians(360.0 / n);
		double pW = particles.get(0).getWidth();
		double pH = particles.get(0).getHeight();
		float side = (float) particles.get(0).getWidth();
		float radius = (float) (side / (2 * Math.sin(Math.PI / n)));

		this.coordinates = IntStream.range(0, n)
				.mapToObj(i -> new Point2D(
						center.getX() + Math.sin(i * alpha) * radius - pW / 2,
						center.getY() - Math.cos(i * alpha) * radius - pH / 2))
				.collect(Collectors.toList());
	}

	/**
	 * Method that computes the coordinates on the canvas that form a square shape.
	 * 
	 * @param particles the list of particles elements present on the canvas
	 */
	public void getSquareCoords(List<Particle> particles) {
		int particleCount = particles.size();
		int layers = particleCount / 4;
		double particleWidth = particles.get(0).getWidth();
		double rescale = calculateRescale(layers);
		double startPosition = calculateStartPosition(layers);

		// Generate top and bottom rows
		for (double column = startPosition; column <= startPosition + layers; column++) {
			addCoordinate(column, startPosition, rescale, particleWidth);
			addCoordinate(column, startPosition + layers, rescale, particleWidth);
		}

		// Generate left and right columns (excluding corners, which are already added)
		for (double row = startPosition + 1; row < startPosition + layers; row++) {
			addCoordinate(startPosition, row, rescale, particleWidth);
			addCoordinate(startPosition + layers, row, rescale, particleWidth);
		}
	}

	private double calculateRescale(int layers) {
		return (layers % 2 == 0) ? 0.5 : 0;
	}

	private double calculateStartPosition(int layers) {
		return -(layers / 2 + 0.5);
	}

	private void addCoordinate(double column, double row, double rescale, double particleWidth) {
		double x = center.getX() - (column + rescale) * particleWidth;
		double y = center.getY() - (row - rescale) * particleWidth;
		this.coordinates.add(new Point2D(x, y));
	}

	/**
	 * Method that computes the coordinates on the canvas that form a diamond shape.
	 * 
	 * @param particles the list of particles elements present on the canvas
	 */
	public void getDiamondCoords(List<Particle> particles) {
		getSquareCoords(particles);

		for (Point2D point : this.coordinates) {
			// Offset to the center
			Point2D rescaledPoint = rescaleToCenter(point);

			// Rotate by 45 degrees
			Point2D rotatedPoint = rotatePoint(rescaledPoint, 45);

			// Stretch in x and y directions
			Point2D stretchedPoint = stretchPoint(rotatedPoint, 0.6, 1.3);

			// Revert the offset to the original position
			applyRevertedOffset(point, rescaledPoint, stretchedPoint);
		}
	}

	private Point2D rescaleToCenter(Point2D point) {
		double offsetX = center.getX() - point.getX();
		double offsetY = center.getY() - point.getY();
		return new Point2D(offsetX, offsetY);
	}

	private Point2D rotatePoint(Point2D point, double angle) {
		double radians = Math.toRadians(angle);
		double rotatedX = point.getX() * Math.cos(radians) - point.getY() * Math.sin(radians);
		double rotatedY = point.getX() * Math.sin(radians) + point.getY() * Math.cos(radians);
		return new Point2D(rotatedX, rotatedY);
	}

	private Point2D stretchPoint(Point2D point, double scaleX, double scaleY) {
		double stretchedX = point.getX() * scaleX;
		double stretchedY = point.getY() * scaleY;
		return new Point2D(stretchedX, stretchedY);
	}

	private void applyRevertedOffset(Point2D originalPoint, Point2D rescaledPoint, Point2D transformedPoint) {
		double diffX = transformedPoint.getX() - rescaledPoint.getX();
		double diffY = transformedPoint.getY() - rescaledPoint.getY();
		originalPoint.setX(originalPoint.getX() - diffX);
		originalPoint.setY(originalPoint.getY() - diffY);
	}

	/**
	 * Method that computes the coordinates on the canvas that form a spiral shape.
	 * 
	 * @param particles the list of particles elements present on the canvas
	 */
	public void getSpiralCoords(List<Particle> particles) {
		double rotation = -Math.PI / 2;
		int awayStep = (int) particles.get(0).getWidth() / 2;
		int chord = awayStep * 3; // distance between points
		double theta = chord / awayStep;

		this.coordinates.add(new Point2D(center.getX(), center.getY()));

		for (int i = 1; i < particles.size(); ++i) {
			Point2D coordinate = calculateSpiralCoordinate(awayStep, theta, rotation);
			this.coordinates.add(coordinate);
			theta += chord / (awayStep * theta);
		}
	}

	private Point2D calculateSpiralCoordinate(int awayStep, double theta, double rotation) {
		double away = awayStep * theta; // how far away from the center
		double around = theta + rotation; // how far around the center
		double x = center.getX() + Math.cos(around) * away;
		double y = center.getY() + Math.sin(around) * away;
		return new Point2D(x, y);
	}

	/**
	 * Method that computes the coordinates on the canvas that form a loose spiral
	 * shape.
	 * 
	 * @param particles the list of particles elements present on the canvas
	 */
	public void getLooseSpiralCoords(List<Particle> particles) {
		int awayStep = (int) particles.get(0).getWidth() * 2;
		double rotation = -Math.PI / 2;
		int chord = awayStep / 2; // distance between points
		double theta = chord / awayStep;

		this.coordinates.add(new Point2D(center.getX(), center.getY()));

		for (int i = 1; i < particles.size(); ++i) {
			Point2D coordinate = calculateSpiralCoordinate(awayStep, theta, rotation);
			this.coordinates.add(coordinate);

			double delta = calculateDeltaTheta(awayStep, chord, theta);
			theta += delta;
		}
	}

	private double calculateDeltaTheta(int awayStep, int chord, double theta) {
		double away = awayStep * theta;
		return (-2 * away + Math.sqrt(4 * away * away + 8 * awayStep * chord)) / (2 * awayStep);
	}

	/**
	 * Method that computes the coordinates on the canvas that form a sunflower
	 * spiral shape.
	 * 
	 * @param particles the list of particles elements present on the canvas
	 * @param angle     the base angle in the coordinates computation
	 */
	public void getSunflowerCoords(List<Particle> particles, double angle) {
		double localMultiplier = 1.2 * particles.get(0).getWidth(); // guess
		double baseAngle = angle;

		for (int i = 0; i < particles.size(); i++) {
			double angle2 = baseAngle * i;
			double x = Math.sqrt(i) * Math.cos(angle2) * localMultiplier;
			double y = Math.sqrt(i) * Math.sin(angle2) * localMultiplier;

			this.coordinates.add(new Point2D(x + center.getX(), y + center.getY()));
		}
	}

	/**
	 * Method that performs a rotation on a shape present on the canvas.
	 * 
	 * @param x         x-value on the screen pointed by the mouse
	 * @param y         y-value on the screen pointed by the mouse
	 * @param particles the list of particles present on the canvas
	 */
	public void rotateShape(double x, double y, List<Particle> particles) {
		currentAngle = getAngle(center.getX(), center.getY(), x, y);
		double angle = (currentAngle - startAngle) / 10;

		if (Math.abs(angle) >= 0.4) {
			angle /= -10;
		}

		for (Particle particle : particles) {
			// Save the initial coordinates
			double initialX = particle.getX();
			double initialY = particle.getY();

			// Offset to the center
			Point2D rescaledPoint = rescaleToCenter(new Point2D(initialX, initialY));

			// Rotate by the calculated angle
			Point2D rotatedPoint = rotatePoint(rescaledPoint, Math.toDegrees(angle));

			// Revert the offset to the original position and update particle coordinates
			Point2D updatedPoint = new Point2D(initialX, initialY);
			applyRevertedOffset(updatedPoint, rescaledPoint, rotatedPoint);
			particle.setX(updatedPoint.getX());
			particle.setY(updatedPoint.getY());
		}
	}

	public void calculateShapeSize(List<Particle> particles) {
		double widthMax = 0, widthMin = particles.get(0).getX();
		double heightMax = 0, heightMin = particles.get(0).getY();

		// set coordinates to the current particle on the canvas if null.
		if (this.coordinates.size() == 0) {
			for (int i = 0; i < particles.size(); i++) {
				Particle particle = particles.get(i);
				this.coordinates.get(i).setX(particle.getX());
				this.coordinates.get(i).setY(particle.getY());
			}
		}

		// find diameter and height
		for (int i = 0; i < particles.size(); i++) {
			Particle particle = particles.get(i);
			widthMax = (particle.getX() > widthMax) ? particle.getX() : widthMax;
			heightMax = (particle.getY() > heightMax) ? particle.getY() : heightMax;

			widthMin = (particle.getX() < widthMin) ? particle.getX() : widthMin;
			heightMin = (particle.getY() < heightMin) ? particle.getY() : heightMin;
		}

		this.shapeWidth = widthMax - widthMin;
		this.shapeHeight = heightMax - heightMin;

	}

	public void divide(List<Particle> particles) {
		calculateShapeSize(particles);
	
		// compute new coordinates?
		if (this.shapeWidth >= center.getX()) {
			rescaleShape(particles);
		}
	
		divideShapeIntoSubshapes(particles);
	}
	
	public void divideShapeIntoSubshapes(List<Particle> particles) {
		int particlesSize = particles.size();
	
		// If there are not enough particles to divide, return
		if (particlesSize < 2) {
			return;
		}
	
		// Calculate the index to divide the particles list
		int middleIndex = particlesSize / 2;
	
		List<Particle> leftParticles = new ArrayList<>(particles.subList(0, middleIndex));
		List<Particle> rightParticles = new ArrayList<>(particles.subList(middleIndex, particlesSize));
	
		// Assign new coordinates for left and right subshapes
		List<Point2D> leftCoordinates = calculateNewCoordinates(leftParticles, -1);
		List<Point2D> rightCoordinates = calculateNewCoordinates(rightParticles, 1);
	
		// Clear the original coordinates and add the new divided coordinates
		this.coordinates.clear();
		this.coordinates.addAll(leftCoordinates);
		this.coordinates.addAll(rightCoordinates);
	}
	
	
	private List<Point2D> calculateNewCoordinates(List<Particle> particles, int direction) {
		double offsetX = (direction * center.getX()) / 3;
	
		List<Point2D> newCoordinates = new ArrayList<>(particles.size());
	
		for (Particle particle : particles) {
			double x = particle.getX() + offsetX;
			double y = particle.getY();
			newCoordinates.add(new Point2D(x, y));
		}
	
		return newCoordinates;
	}
	
	

	/**
	 * @param currWidth
	 * @param currHeight
	 */
	public void rescaleShape(List<Particle> particles) {
		System.out.println("in rescale");
		// impose 1/9 to the left, 1/9 to the middle and 1/9 to the right
		// then left child is 1/3 of canvas width and right child is 1/3 too.

		double prefferedWidth = 2 * center.getX() / 3; // a third of canvas width
		double prefferedHeight = 2 * center.getY() / 3; // a third of canvas height

		// perform rescaling of this.coordinates given the width height of a aprticle
		// it should be linear ?

		// currParticleWidth => currWidth , then assuming lixnearity
		// newParticleWidth => prefferedWidth , with NPW < CPW
		double newParticleWidth = prefferedWidth * particles.get(0).getWidth() / this.shapeWidth;

		double newParticleHeight = prefferedHeight * particles.get(0).getHeight() / this.shapeHeight;
		particles.get(0).setWidth(newParticleWidth);
		particles.get(0).setHeight(newParticleHeight);
	}

	public void setDividedShapeCoodinates(List<Particle> particles) {
		int particlesSize = particles.size();
	
		// If there are not enough particles to divide, return
		if (particlesSize < 2) {
			return;
		}
	
		// Calculate the index to divide the particles list
		int middleIndex = particlesSize / 2;
	
		List<Particle> leftParticles = new ArrayList<>(particles.subList(0, middleIndex));
		List<Particle> rightParticles = new ArrayList<>(particles.subList(middleIndex, particlesSize));
	
		// Assign new coordinates for left and right subshapes
		List<Point2D> leftCoordinates = calculateNewCoordinates(leftParticles, -1);
		List<Point2D> rightCoordinates = calculateNewCoordinates(rightParticles, 1);
	
		// Clear the original coordinates and add the new divided coordinates
		this.coordinates.clear();
		this.coordinates.addAll(leftCoordinates);
		this.coordinates.addAll(rightCoordinates);

	}
	
	/**
	 * TODO
	 * 
	 * @param particles list of particles on the canvas
	 */
	public void jiggle(List<Particle> particles) {
		// if odd number of particles , then need to set last 2 in the list
		// to get smaller but at a slower rate then the other before

		int k = 0;
		for (int i = 0; i < particles.size(); i++) {
			if (k % 2 == 0) {
				for (Particle p : particles) {

				}
				// enable particle bigger

				// particles are not in a linked list though
			}
		}
	}

	/**
	 * Method that computes the distances between all particles and all the
	 * coordinates.
	 * 
	 * @param pList  list of particles on the canvas
	 * @param second list of Point2D coordinates representing the shape's
	 *               coordinates.
	 * @return List<Double> all the distances between all points and all
	 *         coordinates
	 */
	public List<Double> calculateDistance(List<Particle> pList, List<Point2D> second) {
		List<Double> distances = new ArrayList<>();
		Iterator<Particle> iterator1 = pList.iterator();
		while (iterator1.hasNext()) {
			Particle iterated = iterator1.next();

			for (int i = 0; i < second.size(); i++) {
				Point2D point = second.get(i);
				double d = Math.sqrt(Math.pow(iterated.getX() - point.getX(), 2) + Math.pow(iterated.getY() - point.getY(), 2));

				distances.add(d);
			}
		}
		return distances;
	}

	/**
	 * Method that sets for each point an associated closest coordinate.
	 * 
	 * @param particles list of particles on the canvas
	 */
	public void setProximity(List<Particle> particles) {
		List<Particle> particlesCopy = new ArrayList<>(particles);
		List<Point2D> coordinatesCopy = new ArrayList<>(this.coordinates);
	
		for (Particle particle : particlesCopy) {
			Point2D closestCoordinate = findClosestCoordinate(particle, coordinatesCopy);
	
			if (closestCoordinate != null) {
				closestCoordinate.setParticle(particle);
				coordinatesCopy.remove(closestCoordinate);
			}
		}
	}
	
	private Point2D findClosestCoordinate(Particle particle, List<Point2D> coordinates) {
		Point2D closestCoordinate = null;
		double minDistance = Double.MAX_VALUE;
	
		for (Point2D coordinate : coordinates) {
			double distance = calculateDistance(particle, coordinate);
	
			if (distance < minDistance) {
				minDistance = distance;
				closestCoordinate = coordinate;
			}
		}
	
		return closestCoordinate;
	}
	
	private double calculateDistance(Particle particle, Point2D coordinate) {
		return Math.sqrt(Math.pow(particle.getX() - coordinate.getX(), 2) + Math.pow(particle.getY() - coordinate.getY(), 2));
	}
	

	/**
	 * Method that sets the speed of particles in direction of it's respective
	 * coordinate
	 * 
	 * @param particles list of particles on the canvas
	 */
	public void setSpeed(List<Particle> particles) {

		for (int i = 0; i < this.coordinates.size(); i++) {

			Point2D point = this.coordinates.get(i);

			if (point.getParticle().getX() - point.getX() <= 0) {
				point.getParticle().setVX((-point.getParticle().getX() + point.getX()) / (1000 / 16));
			} else if (point.getParticle().getX() - point.getX() > 0) {
				point.getParticle().setVX(-(point.getParticle().getX() - point.getX()) / (1000 / 16));
			}

			if (point.getParticle().getY() - point.getY() <= 0) {
				point.getParticle().setVY((-point.getParticle().getY() + point.getY()) / (1000 / 16));
			} else if (point.getParticle().getY() - point.getY() > 0) {
				point.getParticle().setVY(-(point.getParticle().getY() - point.getY()) / (1000 / 16));
			}
		}
	}

	/**
	 * Method that sets the speed of particles to 0 if they arrived at its
	 * associated coordinate.
	 * 
	 * @return boolean true if all the particles have arrived, false otherwise
	 */
	public boolean checkArrival() {
		boolean allParticlesArrived = true;

		for (int i = 0; i < this.coordinates.size(); i++) {
			Point2D p = this.coordinates.get(i);

			if (p.getParticle().getX() >= p.getX() - p.getParticle().getWidth() / 25 && p.getParticle().getX() <= p.getX() + p.getParticle().getWidth() / 25) {
				p.getParticle().setVX(0);
			} else {
				allParticlesArrived = false;
			}

			if (p.getParticle().getY() >= p.getY() - p.getParticle().getHeight() / 25 && p.getParticle().getY() <= p.getY() + p.getParticle().getHeight() / 25) {
				p.getParticle().setVY(0);
			} else {
				allParticlesArrived = false;
			}
		}

		return allParticlesArrived;
	}

	/**
	 * Method that reinitializes the coordinates.
	 */
	public void reinitializeCoordinates() {
		this.coordinates = new ArrayList<Point2D>();
	}

}
