/**
 * PathPlanner.java
 * @author jmd
 *
 * This class does the path planning once the robot is localized.
 */
 
import java.awt.Point;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.HashMap;

 public class PathPlanner {

	private Point start;
	private Point goal;
	private int[][] map;
	private HashMap<Point,Point> dictionary = new HashMap<Point,Point>();
	private HashMap<Point,Double> costLookup = new HashMap<Point,Double>();

	public PathPlanner( int startx, int starty, int goalx, int goaly, int[][] map ) {
		start = new Point( startx, starty );
		goal = new Point( goalx, goaly );
		this.map =  map;
		dictionary.put( start, null );
	}

	public ArrayList<Point> planPath() {
		PriorityQueue<AStarData<Point>> pq = new PriorityQueue<AStarData<Point>>();
		pq.add( new AStarData<Point>( 0, start ) );
		costLookup.put( start, 0.0 );
		while( !pq.isEmpty() ) {
			Point curr = pq.poll().getData();
			if( isGoal( curr ) ) {
				return buildPath( curr );
			}
			ArrayList<Point> succs = getSuccessors( curr );
			for( Point succ : succs ) {
				if( !visited( succ ) ) {
					dictionary.put( succ, curr );
					costLookup.put( succ, costLookup.get(curr) + 1 );
					System.out.println( "PathCost: " + costLookup.get( succ ) );
					pq.add( new AStarData<Point>( heuristic( succ ) + costLookup.get( succ ), succ ) );
				}
			}
		}
		return null;
	}

	private boolean isGoal( Point test ) {
		return ( test.getX() == goal.getX() ) && ( test.getY() == goal.getY() );
	}

	private int heuristic( Point state ) {
		//return (int)(Math.abs( goal.getX() - state.getX() ) + Math.abs( goal.getY() - state.getY() ));
		return (int)(Math.sqrt( Math.pow( goal.getX() - state.getX(), 2 ) + Math.pow( goal.getY() - state.getY(), 2 ) ));
	}

	private ArrayList<Point> getSuccessors( Point current ) {
		ArrayList<Point> successors = new ArrayList<Point>();
		if( map[(int)current.getX() + 1][(int)current.getY()] != 0 ) {
			successors.add( new Point( (int)current.getX() + 1, (int)current.getY() ) );
		}
		if( map[(int)current.getX() - 1][(int)current.getY()] != 0 ) {
			successors.add( new Point( (int)current.getX() - 1, (int)current.getY() ) );
		}
		if( map[(int)current.getX()][(int)current.getY() + 1] != 0 ) {
			successors.add( new Point( (int)current.getX(), (int)current.getY() + 1 ) );
		}
		if( map[(int)current.getX()][(int)current.getY() - 1] != 0 ) {
			successors.add( new Point( (int)current.getX(), (int)current.getY() - 1 ) );
		}
		return successors;
	}

	private boolean visited( Point current ) {
		return dictionary.containsKey( current );
	}

	private ArrayList<Point> buildPath( Point state ) {
		ArrayList<Point> path = new ArrayList<Point>();
		path.add( state );
		Point current = state;
		Point parent = dictionary.get( current );
		while( parent != null ) {
			path.add( parent );
			current = parent;
			parent = dictionary.get( parent );
		}
		//  Must reverse path to be usable by the robot.
		ArrayList<Point> properPath = new ArrayList<Point>();
		for( int i = path.size() - 1; i >= 0; i-- ) {
			properPath.add( path.get(i) );
		}
		return properPath;
	}
	
	public static void main( String[] args ) {
		int[][] map = Localization.getMap( args[0] );
		int[][] cMap = Localization.getWorkspaceMap( map );
		Point testStart = new Point( 43, 102 );
		Point testGoal = new Point( 622, 136 );
		GridMap showMap = new GridMap( map.length, map[0].length, 1.0 );
		GridMap showCMap = new GridMap( cMap.length, cMap[0].length, 1.0 );
		for( int i = 0; i < map.length; i++ ) {
			for( int j = 0; j < map[0].length; j++ ) {
				showMap.setVal( i, j, map[i][j] );
			}
		}
		PathPlanner planner = new PathPlanner( 43, 102, 622, 136, cMap );
		ArrayList<Point> wps = planner.planPath();
		for( Point wp: wps ) {
			showMap.setParticle( (int)wp.getX(), (int)wp.getY() );
		}
		showMap.pack();
		showMap.setVisible(true);

		for( int i = 0; i < map.length; i++ ) {
			for( int j = 0; j < map[0].length; j++ ) {
				showCMap.setVal( i, j, cMap[i][j] );
			}
		}
		for( Point wp: wps ) {
			showCMap.setParticle( (int)wp.getX(), (int)wp.getY() );
		}
		showCMap.pack();
		showCMap.setVisible(true);
	}
 }
