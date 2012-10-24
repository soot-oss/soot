/*
 * Please attach the following author information if you would like to redistribute the source code:
 * Developer: Xiao Xiao
 * Address: Room 4208, Hong Kong University of Science and Technology
 * Contact: frogxx@gmail.com
 */
package soot.jimple.spark.geom.geomPA;

import soot.jimple.spark.geom.geomPA.SegmentNode;

/**
 * The rectangle figure for encoding the points-to/flows-to relations.
 * 
 * @author xiao
 *
 */
public class RectangleNode extends SegmentNode {

	// I1 : the starting x coordinate
	// I2 : the starting y coordinate
	// L  : the length of the x-axis side
	// L_prime : the length of the y-axis side
	public long L_prime;
	
	public RectangleNode() {}

	public RectangleNode( RectangleNode other )
	{
		copyRectangle(other);
	}
	
	public void copyRectangle( RectangleNode other )
	{
		I1 = other.I1;
		I2 = other.I2;
		L = other.L;
		L_prime = other.L_prime;
	}
	
	public RectangleNode( long I1, long I2, long L, long LL )
	{
		super( I1, I2, L );
		L_prime = LL;
	}
	
	public boolean equals( RectangleNode other )
	{
		if ( I1 == other.I1 &&
				I2 == other.I2 &&
				L == other.L &&
				L_prime == other.L_prime )
			return true;
		
		return false;
	}
}
