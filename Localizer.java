import java.util.ArrayList;
import java.util.Arrays;


/**
 ** Localizer: perform localization calculations
 **
 **
 **
 **
 */
public class Localizer extends Thread {
	public static final int NUM_PARTICLES = 10000;

	private boolean localized;
	private boolean updateReady;
	private double dx,dy,dYaw;
	// Doubles for means of all particles
	private double meanX,meanY,meanYaw;
	private int[][] map;
	private GridMap gmap;
	private double[] ranges;
	private ArrayList<Particle> particleList;
	private Particle expectedLocation;
	private static double particleTolerance = .95;

	/**
	 * Create a new Localizer object to be used in a map with the given
	 * dimensions. Fills the list of particles with NUM_PARTICLES particles or
	 * mapw*maph particles, whichever is smaller. All particles are initalized
	 * with equal weight, 1 divided by the number of particles.
	 *
	 * @param   map the map!
	 */
	public Localizer(int[][] map, GridMap gmap) {
		localized = false;
		this.map = map;
		this.gmap = gmap;
		int mapw = map.length;
		int maph = map[0].length;
		int numParticles = (NUM_PARTICLES > mapw*maph) ? 
				mapw*maph : NUM_PARTICLES;
		int ppp = Math.round(mapw*maph/numParticles);   // pixels per particle
		particleList = new ArrayList<Particle>(numParticles);
		int x = 0, y = 0;
		double weight = 1.0/numParticles;

		for (int i = 0; i < numParticles; i++) {
			if (map[x][y] != 0)   // Can't be in an obstacle, silly
				particleList.add(new Particle(x, y, 0, weight));
			x = (x+ppp) % mapw;
			if (x < ppp) y ++;
		}
		expectedLocation = null;
		updateReady = false;

		drawMap();
		gmap.repaint();

	}

	public void predict() {
		// Do we have enough particles?
		if (effectiveSampleSize() < particleTolerance * NUM_PARTICLES) {
			int[] indexCopyList = resample();
			for (int i = 0; i < indexCopyList.length; i++) {
				Particle temp = (Particle) particleList.get(i).clone();
				particleList.add(temp);
			}
		}
		//   for (j = 1 to M) do {Prediction after action A}
		int m = particleList.size();
		for (int j = 0; j < m; j++) {
			// Drifts, not sure exactly how we should do this, paper suggests using numbers selected randomly
			//  from a gaussian.
			double distDrift;
			double yawDrift;
			Particle temp = particleList.get(j);

			gmap.clearParticle(temp.getX(),temp.getY());

			double tx = temp.getX() + dx;
			double ty = temp.getY() + dy;
			double tp = temp.getPose() + dYaw;
			// If we are greater than pi, wrap around to negatives.
			if (tp > Math.PI)
				tp = tp - 2*Math.PI;
			temp.move(tx, ty, tp);

			gmap.setParticle(temp.getX(),temp.getY());

			//     X^k+1_j = F(X^k_j,A)
			particleList.set(j, temp);
			//   end for
		}
	}
	/*
	 * This will update and normalize the weights
	 */
	public void update() {
		//   for(j = 1 to M) do {Update the weights}
		//      W^k+1_j  = W^K_j * W(s,X^k+1_j)
		//   end for 
		//   for(j=1 to M) do {Normalize the weights}
		//     W^k+1_j = (W^k+1_j)/(Sumi=1 to M (W^k+1_i))
		//   end for
		double thesum = 0;
		for (Particle p : particleList) {
			double weight = p.getWeight() * prob(p,ranges);
			// accrue sum for normalizing
			thesum += weight;
			p.setWeight(weight);
		}
		// Normalize the weights
		for (Particle p : particleList) {
			double weight = p.getWeight() / thesum;
			p.setWeight(weight);
		}
	}
	public double effectiveSampleSize() {
		// Effective Sample Size = (M)/(1 + c*(v_t)^2)
		double ess = particleList.size() / (1 + coeffVariance());
		return ess;
	}
	public double coeffVariance() {
		double c = 0;
		for (int i = 0; i < particleList.size(); i++) {
			c += Math.pow((particleList.size() * particleList.get(i).getWeight())-1,2); 
		}
		c = c * (1.0/particleList.size());
		return c;
	}
	public int[] resample() {
		int[] index = new int[particleList.size()];
		// require sumofi=1 to N (Wi) = 1
		if (sumOf(particleList) != 1) {
			return null;
		}
		// Q = sumsum(W); 
		double[] q = cumsum(particleList);
		// t = rand(N+1);
		double[] t = randArray(particleList.size()+1); // t is an array of N+1 random numbers
		// T = sort(t);
		Arrays.sort(t);
		//T(N+1) = 1; i = 1; j = 1
		int i = 1;
		int j = 1;
		// while( i <= N) do
		while( i <= particleList.size()) {
			//  if T[i] < Q[j] then
			if (t[i] < q[j]) { 
				// Index[i] = j;
				index[i] = j;
				// i++
				i++;
			} else {
				//  else
				// j++;
				j++;
			}
			//  end if
			// end while
		}
		// Return(Index)
		return index;
	}

	private double prob(Particle p, double[] ranges) {
		double varX = getVariance(0);
		double varY = getVariance(1);
		double varYaw = getVariance(2);
		double sdevX = Math.sqrt(varX);
		double sdevY = Math.sqrt(varY);
		double sdevYaw = Math.sqrt(varYaw);
		double prob = (1 / Math.sqrt(2*Math.PI*sdevX))
			*Math.pow(Math.E,-1*Math.pow(p.getX()-meanX,2))/(2*varX);		
		prob = prob * (1 / Math.sqrt(2*Math.PI*sdevY))
		*Math.pow(Math.E,-1*Math.pow(p.getX()-meanY,2))/(2*varY);		
		prob = prob * (1 / Math.sqrt(2*Math.PI*sdevYaw))
		*Math.pow(Math.E,-1*Math.pow(p.getX()-meanYaw,2))/(2*varYaw);		
		return prob;
	}
	public double[] randArray(int size) {
		double[] array = new double[size];
		for (int i = 0; i < size;i ++) {
			array[i] = Math.random();
		}
		return array;
	}
	/*
	 * Calculate running totals :
	 * array[j] = sumof[l=0,j](list[l])
	 */
	public double[] cumsum(ArrayList<Particle> list) {
		double sum = 0;
		double[] array = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			sum += list.get(i).getWeight();
			array[i] = sum;
		}
		return array;
	}
	public double sumOf(ArrayList<Particle> list) {
		double sum = 0;
		for (int i = 0; i < list.size();i++) {
			sum += list.get(i).getWeight();
		}
		return sum;
	}
	public synchronized boolean isLocalized() {
		return localized;
	}

	public double[] getWeights() {
		double[] weights = new double[particleList.size()];
		for(int i = 0; i < weights.length; i++) {
			weights[i] = particleList.get(i).getWeight();
		}
		return weights;
	}
	// we might not need this, not sure yet BROH.
	private double getStandardDev(int v) {
		return Math.sqrt(getVariance(v));
	}
	/* 
	 * Calculate variance for x/y/yaw
	 * @param v - 0 for x, 1 for y, 2 for yaw
	 */
	private double getVariance(int v) {
		double variance = 0;
		for (Particle p : particleList) {
			if (v==0) 
				variance += Math.pow(meanX - p.getX(),2);
			if (v==1)
				variance += Math.pow(meanY - p.getY(), 2);
			if (v==2) 
				variance += Math.pow(meanYaw - p.getPose(),2);
		}
		return variance;
	}
	/*
	 * Calculate mean values for x/y/yaw
	 * @param int v - 0 for x, 1 for y, 2 for yaw 
	 */
	private double getMean(int v) {
		double mean = 0;
		for (Particle p : particleList) {
			if (v == 0)
				mean += p.getX();
			if (v == 1) 
				mean += p.getY();
			if (v == 2)
				mean += p.getPose();
		}
		mean = mean / particleList.size();
		return mean;
	}


	public synchronized void receiveUpdate(double dx, double dy, double dYaw,
			double[] ranges) {
		this.dx = dx;
		this.dy = dy;
		this.dYaw = dYaw;
		this.ranges = ranges;

		updateReady = true;
		notifyAll();
	}



	public void drawMap() {
		for (Particle p : particleList) {
			gmap.setParticle(p.getX(), p.getY());
		}
	}


	/**
	 * What this thread does when it runs, yo
	 */
	@Override
	public void run() {
		while (!localized) {
			if (!updateReady) {
				try {
					wait();
				} catch (InterruptedException e) {}
				continue;
			}
			updateReady = false;
			/* I think this is the right order...
	        @TODO Make sure this is right, then do it, son.
	        predict
	        update
	        if (effectiveSampleSize() < threshold) resample;
	        Nah G, we do EFF in predict yoh. Otherwise, lookin good holmes I'm gonna do it. -AK
			 */
			predict();
			update();

		}
	}

}