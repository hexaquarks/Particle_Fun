import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ShapeManager {
	public String shapeType; // circle , square, pentagon , hexagon 
	Point2D center;

	public ShapeManager(Point2D panelSize) {
		this.center= panelSize;
	}
	static ArrayList<Point2D> coordinates = new ArrayList<Point2D>();


	public ArrayList<Point2D> getCopy() {
		return (ArrayList<Point2D>) coordinates.clone();
	}

	public void circleCoords(ArrayList<Particle> particles) {
		int n = particles.size();
		double alpha = Math.toRadians(360.0/n);    // angle of each triangle in the polygon
		float angle = 0;
		double pW = particles.get(0).width;
		double pH = particles.get(0).height;
		float side = (float) (particles.get(0).getWidth());
		float radius = (float) (side / (2*Math.sin(Math.PI / n)));
		if(!(2*radius >= center.x || 2*radius >= center.y)) {

			for(int i = 0 ; i < n ; i++) {
				this.coordinates.add(new Point2D((center.x + Math.sin(angle)*radius -pW/2),
						(center.y -Math.cos(angle)*radius) - pH/2));
				angle += alpha;

			}
		}

		//GamePanel.testing = true;

	}
	public void squareCoords(ArrayList<Particle> particles) {
		int n = particles.size();

		int layer = n/4;
		double width = particles.get(0).width;
		double rescale = 0;
		if(layer %2 == 0 ) {
			rescale = 0.5;
		}

		double k = -(layer/2+0.5);
		for(double i = k; i <= k+layer ;i+=1) {
			for(double j = k ; j <= k+layer ;j+=1) {
				if(!((i > k && i < (k+layer)) && (j > k && j < (k + layer)))) {
					this.coordinates.add(new Point2D(center.x - (j+rescale)*width,
							center.y - (i-rescale)*width));
							System.out.println("x coord :" + (center.x - this.coordinates.get(this.coordinates.size()-1).x));
				}

			}
		}

	}

	public void diamondCoords(ArrayList<Particle>particles) {

		squareCoords(particles);
		for(Point2D point : this.coordinates){
			//initial coords 
			double x = point.x;
			double y = point.y;
			//offset to center
			point.x = center.x - point.x;
			point.y = center.y - point.y;

			//rotation
			double newPosX = point.x*Math.cos(Math.toRadians(90)) - point.y*Math.sin(Math.toRadians(90));
			double newPosY = point.x*Math.sin(Math.toRadians(90)) + point.y*Math.cos(Math.toRadians(90));
			point.x = newPosX;
			point.y = newPosY;

			// stretch 
			point.x *= 0.2;
			point.y *= 1.2;

			//offset back to 0,0 origin
			point.x += x + (point.x / 0.2)*0.8;
			point.y += y - (point.y / 1.2)*0.2;

		}
	}

	public ArrayList<Double> distanceCalculator(ArrayList<Particle> pList, ArrayList<Point2D> second) {
		ArrayList<Double> distances = new ArrayList<Double>();
		Iterator<Particle> iterator1 = pList.iterator();
		while(iterator1.hasNext()) {
			Particle iterated = iterator1.next();

			for(int i = 0 ; i<second.size() ; i++){
				Point2D point = second.get(i);

				double d = Math.sqrt(Math.pow(iterated.x -point.x, 2)
						+ Math.pow(iterated.y - point.y, 2));
				distances.add(d);
			}
		}
		return distances;
	}
	public void proximity(ArrayList<Particle> particles){
		ArrayList<Particle> particlesCopy = (ArrayList<Particle>) particles.clone();
		ArrayList<Point2D> coordinatesCopy = this.getCopy();
		ArrayList<Double> distances;

		Iterator<Particle> iterator1 = particlesCopy.iterator();
		while(iterator1.hasNext()) {
			Particle particle = iterator1.next(); 			
			Iterator<Point2D> iterator2 = coordinatesCopy.iterator();
			distances = distanceCalculator(particlesCopy, coordinatesCopy);

			int i = 0;
			while(iterator2.hasNext()) {

				Point2D coordinate = iterator2.next();
				double d = Math.sqrt(Math.pow(particle.x -coordinate.x, 2)
						+ Math.pow(particle.y - coordinate.y, 2));

				if(d == distances.get(i)) {
					//int index = distances.indexOf((double) Collections.min(distances));
					coordinate.particle = particle;
					iterator1.remove();
					iterator2.remove();
					i++;
					break;
				}
			}

		}


	}

	public void jiggle(ArrayList<Particle> particles) {
		// if odd number of particles , then need to set last 2 in the list 
		// to get smaller but at a slower rate then the other before 

		int k = 0;
		for(int i = 0 ; i < particles.size() ; i++) {
			if(k % 2 ==0 ) {
				for (Particle p : particles) {

				}
				//enable particle bigger

				//particles are not in a linked list though 
			}
		}
	}

	public void setSpeed(ArrayList<Particle> particles) {

		for(int i = 0 ; i < coordinates.size() ; i++) {
			Point2D point = coordinates.get(i);

			if(point.particle.x - point.x <= 0) {
				point.particle.vx = (-point.particle.x + point.x)/(1000/16);
			} else if(point.particle.x - point.x > 0 ) {
				point.particle.vx = -(point.particle.x - point.x)/(1000/16);
			}

			if(point.particle.y - point.y <= 0) {
				point.particle.vy = (-point.particle.y + point.y)/(1000/16);
			} else if(point.particle.y - point.y > 0 ) {
				point.particle.vy = -(point.particle.y - point.y)/(1000/16);
			}
		}
	}


	public void checkArrival() {

		for(int i = 0 ; i < coordinates.size() ; i++){
			Point2D p = coordinates.get(i);

			if(p.particle.x >= p.x - p.particle.width/25 && p.particle.x <= p.x + p.particle.width/25) {
				p.particle.vx = 0;

			} 
			if(p.particle.y >= p.y - p.particle.height/25 && p.particle.y <= p.y + p.particle.height/25) {
				p.particle.vy = 0;
			} 
		}
	}


	public void reinitializeCoordinates() {
		this.coordinates = new ArrayList<Point2D>();
	}


}
