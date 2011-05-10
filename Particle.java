
public class Particle {
	private double xCoord;
	private double yCoord;
	private double pose;
	
	public Particle() {
		new Particle(0,0,0);
	}
	public Particle(double x, double y, double h) {
		xCoord = x;
		yCoord = y;
		pose = h;
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
	public void move(double x, double y, double h) {
		xCoord = x;
		yCoord = y;
		pose = h;
	}
	
}
