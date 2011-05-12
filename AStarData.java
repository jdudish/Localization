/*
 * AStarData.java
 * 
 * @author Karl Berger
 *
 * Data wrapper for Priority Queue elements of a*
 */ 
public class AStarData<T> implements Comparable<AStarData> {

	/**
	 * Data object being held
	 */
	private T data;
	/**
	 * Cost of the object being held
	 */
	private double cost;

	/**
	 * Constructor
	 *
	 * @param cost Cost of the data being held.
	 * @param data Data object being held.
	 */
	public AStarData( double cost, T data ) {
		this.cost = cost;
		this.data = data;
	}

	/**
	 * Getter for cost.
	 * 
	 * @returns the cost of this object.
	 */
	public double getCost() { return cost; }

	/**
	 * Getter for data.
	 * 
	 * @returns the data wrapped by this object.
	 */
	public T getData() { return data; }

	/**
	 * compareTo method for AStarData.  Provides comparison based
	 * on stored cost.
	 *
	 * @param other The other AStarData to be compared with this object.
	 * @return -1, 0, or 1 if this object is less than, equal to, or greater than other.
	 */
	public int compareTo( AStarData other ) {
		if( this.cost < other.cost ) return -1;
		if( this.cost == other.cost ) return 0;
		else return 1;
	}
}// AStarData.java
