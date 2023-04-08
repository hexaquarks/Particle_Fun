import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	double shapeWidth=0;
	double shapeHeight=0;

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
							   .map(point -> new Point2D(point.x, point.y))
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
		double pW = particles.get(0).width;
		double pH = particles.get(0).height;
		float side = (float) particles.get(0).width;
		float radius = (float) (side / (2 * Math.sin(Math.PI / n)));
	
		this.coordinates = IntStream.range(0, n)
			.mapToObj(i -> new Point2D(
				center.x + Math.sin(i * alpha) * radius - pW / 2,
				center.y - Math.cos(i * alpha) * radius - pH / 2
			))
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
		double particleWidth = particles.get(0).width;
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
		double x = center.x - (column + rescale) * particleWidth;
		double y = center.y - (row - rescale) * particleWidth;
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

			// initial coords
			double x = point.x;
			double y = point.y;

			// offset to center
			double rescaleX = center.x - point.x;
			double rescaleY = center.y - point.y;
			point.x = rescaleX;
			point.y = rescaleY;

			// rotation
			double newPosX = point.x * Math.cos(Math.toRadians(45)) - point.y * Math.sin(Math.toRadians(45));
			double newPosY = point.x * Math.sin(Math.toRadians(45)) + point.y * Math.cos(Math.toRadians(45));

			// stretch
			newPosX *= 0.6;
			newPosY *= 1.3;

			double diffX = newPosX - rescaleX;
			double diffY = newPosY - rescaleY;

			point.x = x - diffX;
			point.y = y - diffY;

		}
	}

	/**
	 * Method that computes the coordinates on the canvas that form a spiral shape.
	 * 
	 * @param particles the list of particles elements present on the canvas
	 */
	public void getSpiralCoords(List<Particle> particles) {
		double rotation = -Math.PI / 2;
		int awayStep = (int) particles.get(0).width / 2;
		int chord = awayStep * 3; // distance between points
		double theta = chord / awayStep;

		this.coordinates.add(new Point2D(center.x, center.y));

		for (int i = 1; i < particles.size(); i++) {
			double away = awayStep * theta; // how far away from center
			double around = theta + rotation; // how far around the center
			double x = center.x + Math.cos(around) * away;
			double y = center.y + Math.sin(around) * away;

			this.coordinates.add(new Point2D(x, y));
			theta += chord / away;
		}
	}

	/**
	 * Method that computes the coordinates on the canvas that form a loose spiral
	 * shape.
	 * 
	 * @param particles the list of particles elements present on the canvas
	 */
	public void getLooseSpiralCoords(List<Particle> particles) {
		int awayStep = (int) particles.get(0).width * 2;
		double rotation = -Math.PI / 2;
		int chord = awayStep / 2; // distance between points
		double delta;
		double theta = chord / awayStep;

		this.coordinates.add(new Point2D(center.x, center.y));

		for (int i = 1; i < particles.size(); i++) {
			double away = awayStep * theta; // how far away from center
			double around = theta + rotation; // how far around the center
			double x = center.x + Math.cos(around) * away;
			double y = center.y + Math.sin(around) * away;

			this.coordinates.add(new Point2D(x, y));

			delta = (-2 * away + Math.sqrt(4 * away * away + 8 * awayStep * chord)) / (2 * awayStep);
			theta += delta;
		}
	}

	/**
	 * Method that computes the coordinates on the canvas that form a sunflower
	 * spiral shape.
	 * 
	 * @param particles the list of particles elements present on the canvas
	 * @param angle     the base angle in the coordinates computation
	 */
	public void getSunflowerCoords(List<Particle> particles, double angle) {
		double localMultiplier = 1.2 * particles.get(0).width; // guess
		double baseAngle = angle;

		for (int i = 0; i < particles.size(); i++) {
			double angle2 = baseAngle * i;
			double x = Math.sqrt(i) * Math.cos(angle2) * localMultiplier;
			double y = Math.sqrt(i) * Math.sin(angle2) * localMultiplier;

			this.coordinates.add(new Point2D(x + center.x, y + center.y));
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
		currentAngle = getAngle(center.x, center.y, x, y);
		double angle = (currentAngle - startAngle) / 10;

		if (Math.abs(angle) >= 0.4)
			angle /= -10;

		for (int i = 0; i < particles.size(); i++) {
			Particle particle = particles.get(i);

			// initial coords
			double initialX = particle.x;
			double initialY = particle.y;

			// rescale origin to center
			double rescaleX = center.x - particle.x;
			double rescaleY = center.y - particle.y;
			particle.x = rescaleX;
			particle.y = rescaleY;

			// rotation
			double newPosX = particle.x * Math.cos(angle) - particle.y * Math.sin(angle);
			double newPosY = particle.x * Math.sin(angle) + particle.y * Math.cos(angle);

			double diffX = newPosX - rescaleX;
			double diffY = newPosY - rescaleY;

			particle.x = initialX - diffX;
			particle.y = initialY - diffY;
		}
	}

	public void calculateShapeSize(List<Particle> particles) {
		double widthMax = 0, widthMin = particles.get(0).x;
		double heightMax = 0, heightMin = particles.get(0).y;

		// set coordinates to the current particle on the canvas if null.
		if (this.coordinates.size() == 0) {
			for (int i = 0; i < particles.size(); i++) {
				Particle particle = particles.get(i);
				this.coordinates.get(i).x = particle.x;
				this.coordinates.get(i).y = particle.y;
			}
		}

		// find diameter and height
		for (int i = 0; i < particles.size(); i++) {
			Particle particle = particles.get(i);
			widthMax = (particle.x > widthMax) ? particle.x : widthMax;
			heightMax = (particle.y > heightMax) ? particle.y : heightMax;

			widthMin = (particle.x < widthMin) ? particle.x : widthMin;
			heightMin = (particle.y < heightMin) ? particle.y : heightMin;
		}

		this.shapeWidth = widthMax - widthMin;
		this.shapeHeight = heightMax - heightMin;

	}

	/**
	 * @param particles
	 */
	public void divide(List<Particle> particles) {
		calculateShapeSize(particles);

		// compute new coordinates?
		if (this.shapeWidth >= center.x) {
			rescaleShape(particles);
		}
	}

	/**
	 * @param currWidth
	 * @param currHeight
	 */
	public void rescaleShape(List<Particle> particles) {
		System.out.println("in rescale");
		// impose 1/9 to the left, 1/9 to the middle and 1/9 to the right
		// then left child is 1/3 of canvas width and right child is 1/3 too.

		double prefferedWidth = 2 * center.x / 3; // a third of canvas width
		double prefferedHeight = 2 * center.y / 3; // a third of canvas height

		// perform rescaling of this.coordinates given the width height of a aprticle
		// it should be linear ?

		// currParticleWidth => currWidth , then assuming lixnearity
		// newParticleWidth => prefferedWidth , with NPW < CPW
		double newParticleWidth = prefferedWidth * particles.get(0).width / this.shapeWidth;

		double newParticleHeight = prefferedHeight * particles.get(0).height / this.shapeHeight;
		particles.get(0).width = newParticleWidth;
		particles.get(0).height = newParticleHeight;
	}

	public void setDividedShapeCoodinates(List<Particle> particles) {
		// 2 child shapes for now
		// clone the coordinates

		List<Point2D> coordinatesClone = getCopy();
		for (int i = 0; i < coordinatesClone.size(); i++) {
			Point2D coordinate = coordinatesClone.get(i);
			// coordinate.x++;
		}
		dividedShapeCoords.add(coordinatesClone);

		// clone the coordinates, separate them ,merge both lists

		for (int i = 0; i < this.coordinates.size(); i++) {
			Point2D point = this.coordinates.get(i);
			point.x = center.x - point.x;
			point.x += 2 * center.x / 3;
		}

		for (int i = 0; i < coordinatesClone.size(); i++) {
			Point2D point = coordinatesClone.get(i);
			point.x = center.x - point.x;
			point.x += 2 * center.x * 0.6666;
		}

		this.coordinates.addAll(coordinatesClone);
		// clone the particles, merge both lists

		List<Particle> particlesClone = new ArrayList<>();
		for (int i = 0; i < particles.size(); i++) {
			Particle p = new Particle(particles.get(i).x + 1, particles.get(i).y + 1, particles.get(i).vx,
					particles.get(i).vy, particles.get(i).mass, particles.get(i).charge);
			particlesClone.add(p);
		}

		particles.addAll(particlesClone);
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
				double d = Math.sqrt(Math.pow(iterated.x - point.x, 2) + Math.pow(iterated.y - point.y, 2));

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
		List<Particle> particlesCopy = new ArrayList<>( particles);
		List<Point2D> coordinatesCopy = new ArrayList<>(this.coordinates);
		List<Double> distances;
		Iterator<Particle> iterator1 = particlesCopy.iterator();

		while (iterator1.hasNext()) {
			Particle particle = iterator1.next();
			Iterator<Point2D> iterator2 = coordinatesCopy.iterator();
			distances = calculateDistance(particlesCopy, coordinatesCopy);

			int i = 0;
			while (iterator2.hasNext()) {

				Point2D coordinate = iterator2.next();
				double d = Math.sqrt(Math.pow(particle.x - coordinate.x, 2) + Math.pow(particle.y - coordinate.y, 2));

				if (d == distances.get(i)) {
					// int index = distances.indexOf((double) Collections.min(distances));
					coordinate.particle = particle;
					iterator1.remove();
					iterator2.remove();
					i++;
					break;
				}
			}

		}
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

			if (point.particle.x - point.x <= 0) {
				point.particle.vx = (-point.particle.x + point.x) / (1000 / 16);
			} else if (point.particle.x - point.x > 0) {
				point.particle.vx = -(point.particle.x - point.x) / (1000 / 16);
			}

			if (point.particle.y - point.y <= 0) {
				point.particle.vy = (-point.particle.y + point.y) / (1000 / 16);
			} else if (point.particle.y - point.y > 0) {
				point.particle.vy = -(point.particle.y - point.y) / (1000 / 16);
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

			if (p.particle.x >= p.x - p.particle.width / 25 && p.particle.x <= p.x + p.particle.width / 25) {
				p.particle.vx = 0;
			} else {
				allParticlesArrived = false;
			}

			if (p.particle.y >= p.y - p.particle.height / 25 && p.particle.y <= p.y + p.particle.height / 25) {
				p.particle.vy = 0;
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
