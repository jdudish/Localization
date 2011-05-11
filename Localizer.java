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
	private ArrayList<Particle> particleList;
	private Particle expectedLocation;
	
	
	/**
	 * Create a new Localizer object to be used in a map with the given
	 * dimensions.
	 *
	 * @param   mapw    the width of the map in pixels
	 * @param   maph    the height of the map in pixels
	 */
	public Localizer(int mapw, int maph) {
		localized = false;
		int numParticles = (NUM_PARTICLES > mapw*maph) ? 
		    mapw*maph : NUM_PARTICLES;
		particleList = new ArrayList<Particle>(numParticles);
		int x = 0, y = 0;
		for (Particle e : particleList) {
		    e = new Particle(x, y, 0, 1/numParticles);
		    x = (x+1) % mapw;
		    if (x == 0) y++;
		}
		expectedLocation = null;
	}
	
	public void predict(double[] scan) {
		//Require: A set of Particles for Robot i at time 0:
		// S^0_i = [X_j,W_j: j = 1...M].
		// W = wj : j = 1...M
		// while (Exploring) do
		//
		//
		//   k = k + 1;
		//   if (ESS(W) < B * M) then {Particle Population Depleted}
		//     Index Resample(W);
		//     S^k_i = S^k_i(Index);
		//   end if
		//   for (j = 1 to M) do {Prediction after action A}
		//     X^k+1_j = F(X^k_j,A)
		//   end for
		//   s = Sense()
		//   for(j = 1 to M) do {Update the weights}
		//      W^k+1_j  = W^K_j * W(s,X^k+1_j)
		//   end for 
		//   for(j=1 to M) do {Normalize the weights}
		//     W^k+1_j = (W^k+1_j)/(Sumi=1 to M (W^k+1_i))
		//   end for
		//
		//
		//
		// end while
		// {ESS is the Effective Sample Size}
	}
	public void update() {
        
	}
	public double effectiveSampleSize(double[] weight) {
		// Effective Sample Size = (M)/(1 + c*(v_t)^2)
		double ess = weight.length / (1 + coeffVariance(weight));
		return ess;
	}
	public double coeffVariance(double [] weight) {
		double c = 0;
		for (int i = 0; i < weight.length; i++) {
			c += Math.pow((weight.length * weight[i])-1,2); 
		}
		c = c * (1.0/weight.length);
		return c;
	}
	public int[] resample(double[] weight) {
		int[] index = new int[weight.length];
		// require sumofi=1 to N (Wi) = 1
		if (sumOf(weight) != 1) {
			return null;
		}
		// Q = sumsum(W); 
		double[] q = cumsum(weight);
		// t = rand(N+1);
		double[] t = randArray(weight.length+1); // t is an array of N+1 random numbers
		// T = sort(t);
		Arrays.sort(t);
		//T(N+1) = 1; i = 1; j = 1
		int i = 1;
		int j = 1;
		// while( i <= N) do
		while( i <= weight.length) {
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
	public double[] cumsum(double[] list) {
		double sum = 0;
		double[] array = new double[list.length];
		for (int i = 0; i < list.length; i++) {
			sum += list[i];
			array[i] = sum;
		}
		return array;
	}
	public double sumOf(double[] array) {
		double sum = 0;
		for (int i = 0; i < array.length;i++) {
			sum += array[i];
		}
		return sum;
	}
	/**
	 * What this thread does when it runs, yo
	 */
	@Override
	public void run() {
	    /*
	    
	    */
	}
	
}