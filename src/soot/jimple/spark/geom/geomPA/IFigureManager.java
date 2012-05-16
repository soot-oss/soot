/*
 * Please attach the following author information if you would like to redistribute the source code:
 * Developer: Xiao Xiao
 * Address: Room 4208, Hong Kong University of Science and Technology
 * Contact: frogxx@gmail.com
 */
package soot.jimple.spark.geom.geomPA;

/**
 * An interface to standardize the functionality of a figure manager.
 * 
 * @author xiao
 *
 */
public interface IFigureManager 
{
	public SegmentNode[] getFigures();
	public int[] getSizes();
	public boolean isThereUnprocessedFigures();
	public void flush();
	
	public SegmentNode addNewFigure(int code, RectangleNode pnew);
	public void mergeFigures(int size);
	public void removeUselessSegments();
}
