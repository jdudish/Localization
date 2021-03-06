/**
 * Particle.java
 * @author amk
 *
 * A single particle in a particle filter for localization
 */
 
public class Particle implements Comparable<Particle> {
	private double xCoord;
	private double yCoord;
	private double pose;
    private double weight;
    
	public Particle() {
		new Particle(0,0,0,1);
	}
	
	public Particle(double x, double y, double h, double w) {
		xCoord = x;
		yCoord = y;
		pose = h;
		weight = w;
	}
	
	public double getX() {
		return xCoord;
	}
	
	public double getY() {
		return yCoord;
	}
	
	public double getPose() {
		return pose;
	}
	
	public void setX(double x) {
		xCoord = x;
	}
	
	public void setY(double y) {
		yCoord = y;
	}
	
	public void setPose(double h) {
		pose = h;
	}
	public void setWeight(double w) {
		weight = w;
	}
	public double getWeight() {
		return weight;
	}
	public void move(double x, double y, double h) {
		xCoord = x;
		yCoord = y;
		pose = h;
		
		if (x < 0 || y < 0) weight = 0.0;
	}
	public Particle clone() {
		Particle returnMe = new Particle(xCoord,yCoord,pose,weight);
		return returnMe;
	}
	
	public boolean equals(Object o) {
	    if (! (o instanceof Particle)) return false;
	    Particle p = (Particle)o;
	    return this.weight == p.getWeight() && this.xCoord == p.getX() &&
	        this.yCoord == p.getY() && this.pose == p.getPose();
	}
	
	public int compareTo(Particle p) {
	    double diff = this.weight - p.getWeight();
	    return (diff < 0) ? -1 : 1;
	}
	
	@Override
	public String toString() {
	    String s = "Particle @ (" + xCoord + ", " + yCoord + ", " + pose + ")";
	    s += " weight= " + String.valueOf(weight);
	    return s;
	}
	
}
