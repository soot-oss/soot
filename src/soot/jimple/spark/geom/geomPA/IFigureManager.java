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
public abstract class IFigureManager 
{
	// We implement an internal memory manager here
	private static SegmentNode segHeader = null;
	private static SegmentNode rectHeader = null;
	
	protected static SegmentNode getSegmentNode()
	{
		SegmentNode ret = null;
		
		if ( segHeader != null ) {
			ret = segHeader;
			segHeader = ret.next;
			ret.next = null;
			ret.is_new = true;
		}
		else
			ret = new SegmentNode();
		
		
		return ret;
	}
	
	protected static RectangleNode getRectangleNode()
	{
		RectangleNode ret = null;
		
		if ( rectHeader != null ) {
			ret = (RectangleNode)rectHeader;
			rectHeader = ret.next;
			ret.next = null;
			ret.is_new = true;
		}
		else
			ret = new RectangleNode();
		
		return ret;
	}
	
	protected static SegmentNode reclaimSegmentNode( SegmentNode p )
	{
		SegmentNode q = p.next;
		p.next = segHeader;
		segHeader = p;
		return q;
	}
	
	protected static SegmentNode reclaimRectangleNode( SegmentNode p )
	{
		SegmentNode q = p.next;
		p.next = rectHeader;
		rectHeader = p;
		return q;
	}
	
	/**
	 * We discard the allocated memories.
	 */
	public static void cleanCache()
	{
		segHeader = null;
		rectHeader = null;
	}
	
	
	// Get the information of the figures
	public abstract SegmentNode[] getFigures();
	public abstract int[] getSizes();
	public abstract boolean isThereUnprocessedFigures();
	public abstract void flush();
	
	// Deal with the figures
	public abstract SegmentNode addNewFigure(int code, RectangleNode pnew);
	public abstract void mergeFigures(int size);
	public abstract void removeUselessSegments();
	
}
