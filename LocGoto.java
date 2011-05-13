/*
* LocGoto.java
* 
* @author Karl Berger
* 
* Goto functions for localization.  Uses potential fields for path following and
* obstacle avoidance.
*/

import javaclient3.*;
import javaclient3.structures.*;
import java.awt.Point;
import java.util.ArrayList;

public class LocGoto {

	public static void executePath( PlayerClient pc, Position2DInterface pos, RangerInterface ranger, ArrayList<Point> wps, double xOff, double yOff ) {

		double turnrate = 0;
		double speed = 0;
		double omega = 20*Math.PI/180;
		int ptCount = 0;
		Point currTarget = wps.get( ptCount );
		double currX = currTarget.getX() * Localization.MAP_METERS_PER_PIXEL;
		double currY = currTarget.getY() * Localization.MAP_METERS_PER_PIXEL;
		System.out.println( "Current wp: " + currX + " " + currY );
		boolean reached = false;

		while( true ) {
			pc.readAll();
			if( !pos.isDataReady() ) {
                		continue;
            		}
			if( ranger == null || !ranger.isDataReady() ) {
                		continue;
            		}

			System.out.printf( "(%7f,%7f,%7f)\n",
			      pos.getX()+xOff,pos.getY()+yOff,pos.getYaw() );
			double[] ranges = ranger.getData().getRanges();

			if( ptCount == wps.size() ) { // If we have reached the last point, then stop
				speed = 0;
				turnrate = 0;
				// TODO: Rather exit or just allow the user to kill the program when they want?
			}else if( reached ) { 
				// If we have reached our current target, increase the 
				// point counter and see if we have any remaining points to hit
				ptCount++;
				// If there are remaining points, update current target
				if( ptCount != wps.size() ) {
					currTarget = wps.get( ptCount );
					currX = currTarget.getX() * Localization.MAP_METERS_PER_PIXEL;
					currY = currTarget.getY() * Localization.MAP_METERS_PER_PIXEL;
					System.out.println( "Current wp: " + currX + " " + currY );
					reached = false;
				}
				speed = 0;
				turnrate = 0;
			}else { 
				// We have not reached our target, so carry on!
				// Only working with every 5 lasers just to cut down on data being processed
				// Just looking for the closest obstacle
				double closest = Double.MAX_VALUE;
				int closestLaser = 0;
				for( int i = 0; i < ranges.length; i += 5 ) {
					if( ranges[i] < closest ) {
						closest = ranges[i];
						closestLaser = i;
					}
				}

				//Now for potential field stuff.
				//Alter the percieved distance of the obstacle to pretend we are in workspace
				double obsDistance = ranges[closestLaser] - 0.165;
				double obsTheta = closestLaser * Localization.RADIAN_PER_LASER - 
					Localization.LASER_ROBOT_OFFSET + pos.getYaw();
				//X and Y position relative to the robot;
				double obsX = Math.cos(obsTheta) * obsDistance;
				double obsY = Math.sin(obsTheta) * obsDistance;
				
				double obsForceX = ((-obsX) / Math.pow(obsDistance,3));
				double obsForceY = ((-obsY) / Math.pow(obsDistance,3));

				System.out.println( "ObsF: " + obsForceX + " " + obsForceY );

				double goalForceX = (currX - (pos.getX() + xOff)) / 
					Math.pow( Math.sqrt(Math.pow(currX - (pos.getX() + xOff),2) 
						+ Math.pow(currY - (pos.getY() + yOff),2)), 3);
				double goalForceY = (currY - (pos.getY() + yOff)) /
					Math.pow( Math.sqrt(Math.pow(currX - (pos.getX() + xOff),2) 
						+ Math.pow(currY - (pos.getY() + yOff),2)),3);

				System.out.println( "GoalF: " + goalForceX + " " + goalForceY );

				//Force exerted on the robot
				double robotFX = Localization.OBSTACLE_POTENTIAL_CONSTANT * obsForceX + 
					Localization.GOAL_POTENTIAL_CONSTANT * goalForceX;
				double robotFY = Localization.OBSTACLE_POTENTIAL_CONSTANT * obsForceY + 
					Localization.GOAL_POTENTIAL_CONSTANT * goalForceY;
				double robotForce = Math.sqrt( robotFX*robotFX + robotFY*robotFY );
				double robotForceAngle = Math.atan2(robotFY, robotFX);
				
				System.out.println( "Force: " + robotForce );
				System.out.println( "Angle: " + robotForceAngle );

				if( Math.sqrt(Math.pow(currX - (pos.getX() + xOff),2) 
						+ Math.pow(currY - (pos.getY() + yOff),2)) < .05 ) {
					reached = true;
					speed = 0;
					turnrate = 0;
				}else{
					//Now to translate to speed and turnrate for the robot.
					speed = 0.1;
					turnrate = (robotForceAngle - pos.getYaw())*(robotForce);
					turnrate = Math.min( turnrate, 1.0 );
				}
			}
			pos.setSpeed(speed, turnrate);
		}
	}//executePath

	public static void main( String[] args ) {
		PlayerClient pc = new PlayerClient("localhost",6665);

		Position2DInterface pos = pc.requestInterfacePosition2D(0,PlayerConstants.PLAYER_OPEN_MODE);
		RangerInterface ranger = pc.requestInterfaceRanger( 0, PlayerConstants.PLAYER_OPEN_MODE );
    
		int[][] map = Localization.getMap( args[0] );
		int[][] cMap = Localization.getWorkspaceMap( map );

		PathPlanner planner = new PathPlanner( 71, 71, 622, 136, cMap );
		ArrayList<Point> wps = planner.planPath();
		wps = planner.simplifyPath( wps, 20 );

		GridMap showMap = new GridMap( map.length, map[0].length, 1.0 );
		for( int i = 0; i < map.length; i++ ) {
			for( int j = 0; j < map[0].length; j++ ) {
				showMap.setVal( i, j, map[i][j] );
			}
		}

		for( Point wp: wps ) {
			showMap.setParticle( (int)wp.getX(), (int)wp.getY() );
		}
		showMap.pack();
		showMap.setVisible(true);

		LocGoto.executePath( pc, pos, ranger, wps, 71*0.02, 71*0.02 );
	}
} //LocGoto.java
