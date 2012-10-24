package soot.jimple.spark.geom.geomPA;

import java.io.PrintStream;

/**
 * It is the the abstract super type of geometric figures, also the manager of all the generated sub-type figures.
 * We implement a simple compression by eliminating identical figures.
 * 
 * @author xiao
 *
 */
public abstract class ShapeNode 
{
	// Common Instance Fields
	// I1 : the starting x coordinate
	// I2 : the starting y coordinate
	// E1: the end coordinate of the X or Y axis depending on the value of I1 and I2 (I1 != 0, then E1 is associated with I1)
	public long I1;
	public long I2;
	public long E1;
	public boolean is_new;
	public ShapeNode next;
	
	public ShapeNode()
	{
		is_new = true;
		next = null;
	}
	
	/**
	 * Clone itself and make a new instance.
	 * @return
	 */
	public abstract ShapeNode makeDuplicate();
	
	/**
	 * Test if the invoked figure contains the passed in figure
	 * @param other
	 * @return
	 */
	public abstract boolean inclusionTest(ShapeNode other);
	
	/**
	 * Test if the input x parameter falls in the range of the X coordinates of this figure
	 * @param x
	 * @return
	 */
	public abstract boolean coverThisXValue(long x);
	
	public abstract void printSelf(PrintStream outPrintStream);
	
	public abstract void copy(ShapeNode other);
}
