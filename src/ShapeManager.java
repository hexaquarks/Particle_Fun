import java.util.ArrayList;
import java.util.Iterator;

import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

public class ShapeManager {
	public String shapeType; // circle , square, pentagon , hexagon 
	Point2D center;
	double anchorX, anchorY;
	boolean shapeIsDraggable;
	double startAngle; 
	double currentAngle;

	public double getAngle(double xc, double yc, double x, double y){
		double angle;
		double dy = y - yc;
		double dx = x - xc;
		if (dx == 0 ) {
			angle = dy >= 0 ? Math.PI/2 : -Math.PI/2;
		} else {
			angle = Math.atan(dy/dx);
			if(dx < 0 ) angle += Math.PI;
		}

		if (angle < 0 ) angle += 2* Math.PI;

		return angle;
	}

	public ShapeManager(Point2D panelSize) {
		this.center= panelSize;
		System.out.println(this.center.x+ " , " +  this.center.y);
	}
	static ArrayList<Point2D> coordinates = new ArrayList<Point2D>();

	public ArrayList<Point2D> getCopy() {
		return (ArrayList<Point2D>) coordinates.clone();
	}

	public void setAnchor(double x, double y){
		this.anchorX = x;
		this.anchorY = y;
	}

	public void setShapeIsDraggable(boolean shapeIsDraggable) { this.shapeIsDraggable = shapeIsDraggable; }

	public void circleCoords(ArrayList<Particle> particles) {
		int n = particles.size();
		double alpha = Math.toRadians(360.0/n);    // angle of each triangle in the polygon
		float angle = 0;
		double pW = particles.get(0).width;
		double pH = particles.get(0).height;
		float side = (float) (particles.get(0).getWidth());
		float radius = (float) (side / (2*Math.sin(Math.PI / n)));
		if(!(radius >= center.x || radius >= center.y)) {

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
			point.x = center.x - point.x ;
			point.y = center.y - point.y ;

			//rotation
			double newPosX = point.x*Math.cos(Math.toRadians(45)) - point.y*Math.sin(Math.toRadians(45));
			double newPosY = point.x*Math.sin(Math.toRadians(45)) + point.y*Math.cos(Math.toRadians(45));

			System.out.println("torad(90) : " + Math.cos(Math.toRadians(90)) + " , " + Math.cos(Math.PI/ 2));
			point.x = newPosX;
			point.y = newPosY;

			// stretch 
			// point.x *= 0.2;
			// point.y *= 1.2;

			//offset back to 0,0 origin
			
			 point.x += center.x;
			 point.y += center.y ;

		}
	}

	public void spiralCoords(ArrayList<Particle> particles){
		//find thetaMax which is NbCoils * 2 * Pi
		//actually no one cares about theta max, just iterate while
		//   there are still particles lol.

		int awayStep = (int) particles.get(0).width/2;
		double rotation = -Math.PI / 2;
		int chord = awayStep*3; //distance between points

		double theta = chord / awayStep;
		this.coordinates.add(new Point2D(center.x, center.y));
		for(int i = 1 ; i < particles.size() ; i++) {
			double away = awayStep*theta;  //how far away from center
			double around = theta + rotation; //how far around the center
			double x = center.x + Math.cos(around) * away;
			double y = center.y + Math.sin(around) * away;

			this.coordinates.add(new Point2D(x, y));
			theta += chord / away;
		}
	}

	public void looseSpiralCoords(ArrayList<Particle> particles) { 
		int awayStep = (int) particles.get(0).width*2;
		double rotation = -Math.PI / 2;
		int chord = awayStep/2; //distance between points
		double delta; 
		double theta = chord / awayStep;
		this.coordinates.add(new Point2D(center.x, center.y));
		for(int i = 1 ; i < particles.size() ; i++) {
			double away = awayStep*theta;  //how far away from center
			double around = theta + rotation; //how far around the center
			double x = center.x + Math.cos(around) * away;
			double y = center.y + Math.sin(around) * away;

			this.coordinates.add(new Point2D(x, y));

			delta = ( -2 * away + Math.sqrt( 4 * away * away + 8 * awayStep * chord ) ) / ( 2 * awayStep );
			theta += delta;
		}
	}

	public void sunflowerCoords(ArrayList<Particle> particles ,double angle) {
		double localMultiplier = 1.2*particles.get(0).width; //guess
		double baseAngle = angle;

		for(int i = 0 ; i < particles.size() ; i++){
			double angle2 = baseAngle * i;
			
			double x = Math.sqrt(i) * Math.cos(angle2) * localMultiplier;
			double y = Math.sqrt(i) * Math.sin(angle2) * localMultiplier;

			this.coordinates.add(new Point2D(x + center.x, y + center.y));
		}
	}

	//called only if shapeIsDraggable = true 
	public void rotateShape(double x, double y, ArrayList<Particle> particles) {
		//A = arccos ((b^2 + c^2 -a ^2 ) / 2bc)
		// x = center.x - x;
		// y = center.y - y;
		// anchorX = center.x - anchorX;
		// anchorY = center.y - anchorY;
		// double a = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		// double b = Math.sqrt(Math.pow(anchorX , 2) + Math.pow(anchorY , 2));
		// double c = Math.sqrt(Math.pow((anchorX-x), 2) + Math.pow((anchorY - y), 2));

		// double angle = Math.acos((Math.pow(b, 2) + Math.pow(a, 2) - Math.pow(c, 2)) / (2 * b * a));
		// // if( y >= anchorY) angle = - angle;
		// if(y > 0) {
		// 	System.out.println("UP");
		// 	if(y > anchorY ){
		// 		if(x < 0) {
		// 			angle *= -1;
		// 		} else if (x > 0) {
		// 			if(x > anchorX) angle *= -1;
					
		// 		} else { 
		// 			angle = 0;
		// 		}
		// 	} else  {
		// 		if(x < 0) {
					
		// 		} else if (x > 0) {
		// 			angle *= -1;
		// 		} else { 
		// 			angle =0 ;
		// 		}
		// 	}
		// } 

		// if(y <= 0) {
		// 	if(y > anchorY ){
		// 		if(x < 0) {
		// 			angle *= -1;
		// 		} else if (x > 0) {
					
		// 		} else {
		// 			angle = 0;
		// 		}
		// 	} else  {
		// 		if(x < 0) {
					
		// 		} else if (x > 0) {
		// 			System.out.println("IN");
		// 			angle *= -1;
		// 		} else {
		// 			angle = 0;
		// 		}
		// 	}
		// }
		// angle *= 25;

		// System.out.println("anchor (x,y) : " + anchorX + "," + anchorY + "  and (x,y) : " + x + "," + y);

		currentAngle = getAngle(center.x, center.y, x, y);
		double angle = currentAngle - startAngle;
		
		for (int i = 0 ; i < particles.size() ; i++) {
			Particle particle = particles.get(i);

			//initial coords 
			double initialX = particle.x;
			double initialY = particle.y;

			//offset to center
			double rescaleX  = center.x - particle.x;
			double rescaleY  = center.y - particle.y;
			particle.x = rescaleX;
			particle.y = rescaleY;

			//rotation
			double newPosX = particle.x*Math.cos(Math.toRadians(angle)) - particle.y*Math.sin(Math.toRadians(angle));
			double newPosY = particle.x*Math.sin(Math.toRadians(angle)) + particle.y*Math.cos(Math.toRadians(angle));

			double diffX = newPosX - rescaleX; 
			double diffY = newPosY - rescaleY; 

			particle.x = initialX - diffX;
			particle.y = initialY - diffY;
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


	public boolean checkArrival() {
		boolean allParticlesArrived = true;
		for(int i = 0 ; i < coordinates.size() ; i++){
			Point2D p = coordinates.get(i);

			if(p.particle.x >= p.x - p.particle.width/25 && p.particle.x <= p.x + p.particle.width/25) {
				p.particle.vx = 0;
			} else {
				allParticlesArrived = false;
			}
			if(p.particle.y >= p.y - p.particle.height/25 && p.particle.y <= p.y + p.particle.height/25) {
				p.particle.vy = 0;
			} else {
				allParticlesArrived = false;
			}
		}
		// Transform trans = new Rotate(65, new Point3D(0, 1, 0));
		// Box box = new Box(100, 20 ,20);
		// box.getTransforms().add(trans);
		return allParticlesArrived;
	}


	public void reinitializeCoordinates() {
		this.coordinates = new ArrayList<Point2D>();
	}


}
