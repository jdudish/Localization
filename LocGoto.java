/*
* LocGoto.java
* 
* @author Karl Berger
* 
* Goto functions for localization.  Uses potential fields for path following and
* obstacle avoidance.
*/

import javaclient3.*;
import javaclient3.structures.PlayerConstants;
import java.awt.Point;
import java.util.ArrayList;

public class LocGoto {

	public static void executePath( PlayerClient pc, ArrayList<Point> wps, int[][] map ) {

		Position2DInterface pos = pc.requestInterfacePosition2D(0,PlayerConstants.PLAYER_OPEN_MODE);
		RangerInterface ranger = pc.requestInterfaceRanger( 0, PlayerConstants.PLAYER_OPEN_MODE );

		double turnrate = 0, speed = 0;
		double omega = 20*Math.PI/180;
		int ptCount = 0;
		double[] currTarget = wps.get( ptCount );
		boolean reached = false;


		while( true ) {
			pc.readAll();

			if( !ranger.isDataReady() ) {
                		continue;
            		}
			if( !pos.isDataReady() ) {
                		continue;
            		}

			double[] ranges = ranger.getData().getRanges();

			if( ptCount == wps.size() ) { // If we have reached the last point, then stop
				speed = 0;
				turnrate = 0;
				// TODO: Rather exit or just allow the user to kill the program when they want?
			}else if( reached ) { // If we have reached our current target, increase the 
				ptCount++;    // point counter and see if we have any remaining points to hit
				if( ptCount != wps.size() ) { // If there are remaining points, update current target
					currTarget = pts.get( ptCount );
					reached = false;
					speed = 0;
					turnrate = 0;
				}else { // Else stop
					speed = 0;
					turnrate = 0;
				}
			}else { // We have not reached our target, so carry on!
				double xToGo = currTarget[0] - pos.getX();
				double yToGo = currTarget[1] - pos.getY();
				double hypToGo = Math.sqrt( Math.pow(xToGo,2) + Math.pow(yToGo,2) );
				double angle = Math.atan2( yToGo, xToGo );
				System.out.println( "HypToGo: " + hypToGo );
				System.out.println( "AngleDif: " + Math.abs( angle - pos.getYaw() ) );

				double closest = Double.MAX_VALUE;
				int closestLaser = 0;

				// Only working with every 5 lasers just to cut down on data being processed
				// Just looking for the closest obstacle
				for( int i = 0; i < ranges.length; i += 5 ) {
					if( ranges[i] < closest ) {
						closest = ranges[i];
						closestLaser = i;
					}
				}

				// Angle of the laser beam relative to the world (in the same coordinates as the robot),
            			// not with respect to the robot.
		            	double sampleTheta = i * RADIAN_PER_LASER - LASER_ROBOT_OFFSET + position[2];
            			// Components of the current sample
            			double xComponent = Math.cos(sampleTheta);
            			double yComponent = Math.sin(sampleTheta);

				if( hypToGo < .05 ) {
					reached = true;
					speed = 0;
					turnrate = 0;
				}else {
					if( Math.abs( angle - pos.getYaw() ) > .2 ) {
						speed = 0;
                                                double tmpturn = Math.abs( angle - pos.getYaw() )*omega;
                                                turnrate = Math.max( tmpturn, .1 );
					}else {
                                                double tmpspeed = hypToGo*.25;
						speed = Math.min( tmpspeed, .2 );
                                                speed = Math.max( tmpspeed, .025 );
						turnrate = 0;
					}
				}
			}
	    		System.out.printf( "(%7f,%7f,%7f)\n",
			      pos.getX(),pos.getY(),pos.getYaw() );
			pos.setSpeed(speed, turnrate);
		}
	}
}
