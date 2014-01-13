package soot.jimple.spark.geom.dataRep;

import soot.jimple.spark.pag.Node;
import soot.util.Numberable;

/**
 * The root class for representing context sensitive pointer/object in explicit form.
 * 
 * @author xiao
 *
 */
public abstract class ContextVar implements Numberable 
{
	// We use spark Node since it can be easily used by clients
	public Node var = null;
	public int id = -1;
	
	// This class cannot be instantiated directly
	// Use its derived classes
	protected ContextVar()
	{
		
	}
	
	@Override
	public void setNumber(int number) 
	{
		id = number;
	}

	@Override
	public int getNumber() 
	{
		return id;
	}
	
	/** 
	 * Test if current context variable contains the information for passed in variable
	 * 
	 * @param cv
	 * @return
	 */
	public abstract boolean contains(ContextVar cv);
	
	/** 
	 * Merge two context variables if possible
	 * Merged information is written into current variable.
	 *  
	 * @param cv
	 * @return true if mergable. 
	 */
	public abstract boolean merge(ContextVar cv);
	
	/**
	 * Two context sensitive variables have intersected contexts.
	 * @param cv
	 * @return
	 */
	public abstract boolean intersect(ContextVar cv);
}
