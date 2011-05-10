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
	private boolean localized;
	private ArrayList<Particle> particleList;
	private Particle expectedLocation;
	
	public Localizer() {
		localized = false;
		particleList = new ArrayList<Particle>();
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
	public double effectiveSampleSize(double weight,int m) {
		// Effective Sample Size = (M)/(1 + c*(v_t)^2)
		double ess = m / (1 + coeffVariance(t))
		return 0;
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
	
	}
	
}