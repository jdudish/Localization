/**
 * PathPlanner.java
 * @author jmd
 *
 * This class does the path planning once the robot is localized.
 */
 
import java.awt.Point;
import java.util.ArrayList;

 public class PathPlanner {

	private Point start;
	private Point goal;
	private int[][] map;

	public PathPlanner( int startx, int starty, int goalx, int goaly, int[][] map ) {
		start = new Point( startx, starty );
		goal = new Point( goalx, goaly );
		this.map =  map;
	}

	public ArrayList<Point> planPath() {
		
	}

	private boolean isGoal( Point test ) {
		return ( test.getX() == goal.getX() ) && ( test.getY() == goal.getY() );
	}

	private int heuristic( Point state ) {
		return Math.abs( goal.getX() - state.getX() ) + Math.abs( goal.getY() - state.getY() );
	}

	private ArrayList<Point> getSuccessors( Point current ) {
		ArrayList<Point> successors = new ArrayList<Point>();
		if( current.getX() + 1 < map.length && map[current.getX() + 1][current.getY()] != 0 ) {
			successors.add( new Point( current.getX() + 1, current.getY() );
		}
		if( current.getX() - 1 >= 0 && map[current.getX() - 1][current.getY()] != 0 ) {
			successors.add( new Point( current.getX() - 1, current.getY() );
		}
		if( current.getY() + 1 < map[current.getX()].length && map[current.getX()][current.getY() + 1] != 0 ) {
			successors.add( new Point( current.getX(), current.getY() + 1 );
		}
		if( current.getY() - 1 >= 0 && map[current.getX()][current.getY() - 1] != 0 ) {
			successors.add( new Point( current.getX(), current.getY() - 1 );
		}
		return successors;
	}
 }
