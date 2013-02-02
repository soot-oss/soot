package soot.jimple.spark.geom.dataRep;

import soot.jimple.spark.pag.Node;
import soot.util.Numberable;

/**
 * The root class for representing context sensitive pointer/object in explicit form.
 * @author xiao
 *
 */
public class ContextVar implements Numberable 
{
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
}
