/*
 * AStarData.java
 * 
 * @author Karl Berger
 *
 * Data wrapper for Priority Queue elements of a*
 */ 
public class AStarData<T> implements Comparable<AStarData> {

	private T data;
	private double cost;

	public AStarData( double cost, T data ) {
		this.cost = cost;
		this.data = data;
	}

	public double getCost() { return cost; }

	public T getData() { return data; }

	public int compareTo( AStarData other ) {
		if( this.cost < other.cost ) return -1;
		if( this.cost == other.cost ) return 0;
		else return 1;
	}
}
