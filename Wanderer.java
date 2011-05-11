/**
 * Wanderer.java
 *
 * Executes a safe wander for a given PlayerClient robot interface.
 *
 * @author Karl Berger
 */

import javaclient3.*;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.ranger.*;

public class Wanderer extends Thread {

	private PlayerClient pc;
	private Position2DInterface pos;
	private RangerInterface ranger;
	private Localizer loc;

	public Wanderer( PlayerClient pc, Position2DInterface pos, RangerInterface ranger, Localizer loc ) {
		this.pc = pc;
		this.pos = pos;
		this.ranger = ranger;
		this.loc = loc;
	}

	public void run() {
		while( loc.isAlive() && !loc.isLocalized() ) {

            		double turnrate = 0, fwd = 0;
            		double omega = 20 * Math.PI / 180;

            		pc.readAll();

            		if (!ranger.isDataReady()) {
            	    		continue;
            		}

            		if (!pos.isDataReady()) {
                		continue;
            		}

            		double[] ranges = ranger.getData().getRanges();

	            	// do simple collision avoidance
            		double rightval = (ranges[113] + ranges[118]) / 2.0;
            		double leftval = (ranges[569] + ranges[574]) / 2.0;
            		double frontval = (ranges[340] + ranges[345]) / 2.0;

            		if (frontval < 0.5) {
                		fwd = 0;
                		if (Math.abs(leftval - rightval) < .05) {
                    			turnrate = omega;
                		} else if (leftval > rightval) {
                    			turnrate = omega;
                		} else {
                    		turnrate = -1 * omega;
                		}
            		} else {
                		fwd = 0.2;
                		if (leftval < 0.5) {
                    			fwd = 0.0;
                    			turnrate = -1 * omega;
                		} else if (rightval < 0.5) {
                    			fwd = 0.0;
                    			turnrate = omega;
                		}
            		}
            		pos.setSpeed(fwd, turnrate);
		}
	}// run()
}// Wanderer.java
