package soot.jimple.toolkits.annotation.arraycheck;

import java.util.*;

class Array2ndDimensionSymbol
{
    private Object var;

    private static HashMap pool = new HashMap();
   
    public static Array2ndDimensionSymbol v(Object which)
    {
   	Array2ndDimensionSymbol tdal = (Array2ndDimensionSymbol)pool.get(which);
	if (tdal == null)
	{
	    tdal = new Array2ndDimensionSymbol(which);
	    pool.put(which, tdal);
	}

	return tdal;
    }
    
    private Array2ndDimensionSymbol(Object which)
    {
	this.var = which;
    }
    
    public Object getVar()
    {
	return this.var;
    }

    public int hashCode()
    {
	return var.hashCode()+1;
    }

    public boolean equals(Object other)
    {
	if (other instanceof Array2ndDimensionSymbol)
	{
	    Array2ndDimensionSymbol another = (Array2ndDimensionSymbol)other;

	    return (this.var == another.var);
	}
	else
	    return false;
    }

    public String toString()
    {
	return var+"[";
    }
}
