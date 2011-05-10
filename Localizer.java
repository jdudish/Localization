import java.util.ArrayList;


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
	public void resample(double weight) {
		
	}
	
	/**
	 * What this thread does when it runs, yo
	 */
	@Override
	public void run() {
	
	}
	
}