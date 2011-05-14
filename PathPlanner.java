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
				return simplifyPath(buildPath( curr ), 20);
			}
			ArrayList<Point> succs = getSuccessors( curr );
			for( Point succ : succs ) {
				if( !visited( succ ) ) {
					dictionary.put( succ, curr );
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
		Point temp = new Point( 0, 0 );
		// +X
		if( map[(int)current.getX() + 1][(int)current.getY()] != 0 ) {
			temp = new Point( (int)current.getX() + 1, (int)current.getY() );
			if( !visited( temp ) ) {
				successors.add( temp );
				costLookup.put( temp, costLookup.get(current) + 1 );
			}
		}
		// -X
		if( map[(int)current.getX() - 1][(int)current.getY()] != 0 ) {
			temp = new Point( (int)current.getX() - 1, (int)current.getY() );
			if( !visited( temp ) ) {
				successors.add( temp );
				costLookup.put( temp, costLookup.get(current) + 1 );
			}
		}
		// +Y
		if( map[(int)current.getX()][(int)current.getY() + 1] != 0 ) {
			temp = new Point( (int)current.getX(), (int)current.getY() + 1 );
			if( !visited( temp ) ) {
				successors.add( temp );
				costLookup.put( temp, costLookup.get(current) + 1 );
			}
		}
		// -Y
		if( map[(int)current.getX()][(int)current.getY() - 1] != 0 ) {
			temp = new Point( (int)current.getX(), (int)current.getY() - 1 );
			if( !visited( temp ) ) {
				successors.add( temp );
				costLookup.put( temp, costLookup.get(current) + 1 );
			}
		}
/*
		// +X +Y
		if( map[(int)current.getX() + 1][(int)current.getY() + 1] != 0 ) {
			temp = new Point( (int)current.getX() + 1, (int)current.getY() + 1 );
			if( !visited( temp ) ) {
				successors.add( temp );
				costLookup.put( temp, costLookup.get(current) + Math.sqrt(2) );
			}
		}
		// +X -Y
		if( map[(int)current.getX() + 1][(int)current.getY() - 1] != 0 ) {
			temp = new Point( (int)current.getX() + 1, (int)current.getY() - 1 );
			if( !visited( temp ) ) {
				successors.add( temp );
				costLookup.put( temp, costLookup.get(current) + Math.sqrt(2) );
			}
		}
		// -X +Y
		if( map[(int)current.getX() - 1][(int)current.getY() + 1] != 0 ) {
			temp = new Point( (int)current.getX() - 1, (int)current.getY() + 1 );
			if( !visited( temp ) ) {
				successors.add( temp );
				costLookup.put( temp, costLookup.get(current) + Math.sqrt(2) );
			}
		}
		// -X -Y
		if( map[(int)current.getX() - 1][(int)current.getY() - 1] != 0 ) {
			temp = new Point( (int)current.getX() - 1, (int)current.getY() - 1 );
			if( !visited( temp ) ) {
				successors.add( temp );
				costLookup.put( temp, costLookup.get(current) + Math.sqrt(2) );
			}
		}
*/
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

	private ArrayList<Point> simplifyPath( ArrayList<Point> path, int maxLen ) {
		ArrayList<Point> newPath = new ArrayList<Point>();
		Point current = path.remove(0);
		while( path.size() > 1 ) {
			if( Math.abs( path.get(0).getX() - current.getX() ) < maxLen &&
			    Math.abs( path.get(0).getY() - current.getY() ) < maxLen ) {
				path.remove(0);
			}else {
				newPath.add( current );
				current = path.remove(0);
			}
		}
		newPath.add( path.remove(0) );
		return newPath;

		/*if( path.size() < 3 ) {
			return path;
		}
		int start = 0;
		int end = 1;
		while( end < path.size() ) {
			if( (end - start) == (maxLen + 1) ) {
				//path.removeRange(start + 1, end - 1);
				for( int i = start + 1; i < end - 1; i++ ) {
					path.remove( start + 1 );
				}
				start++;
				end = start + 1;
			}else {
				if( path.get(start).getX() == path.get(end).getX() ||
						path.get(start).getY() == path.get(end).getY() ) {
					end++;
				}else {
					if( end - start == 1) {
						start++;
						end++;
					}else if( end - start == 2 ) {
						path.remove( start + 1 );
						start++;
						end = start + 1;
					}else {
						//path.removeRange( start + 1, end - 1 );
						for( int i = start + 1; i < end - 1; i++ ) {
							path.remove( start + 1 );
						}
						start++;
						end = start + 1;
					}
				}
			}
		}
		return path;*/
	}
	
	// Main for testing porpoises.
	public static void main( String[] args ) {
		int[][] map = Localization.getMap( args[0] );
		int[][] cMap = Localization.getWorkspaceMap( map );
		map = Localization.getWorkspaceMap( map );
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
	}
 }
