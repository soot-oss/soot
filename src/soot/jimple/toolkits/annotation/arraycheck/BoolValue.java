package soot.jimple.toolkits.annotation.arraycheck;

import soot.toolkits.graph.*;

class BoolValue 
{
    private boolean isRectangular;

    private static BoolValue trueValue = new BoolValue(true);
    private static BoolValue falseValue = new BoolValue(false);

    public BoolValue(boolean v)
    {
	isRectangular = v;
    }

    public static BoolValue v(boolean v)
    {
	if (v)
	    return trueValue;
	else
	    return falseValue;
    }

    public boolean getValue()
    {
	return isRectangular;
    }

    public boolean or(BoolValue other)
    {
	if (other.getValue())
	    isRectangular = true;

	return isRectangular;
    }

    public boolean or(boolean other)
    {
	if (other)
	    isRectangular = true;
	return isRectangular;
    }

    public boolean and(BoolValue other)
    {
        if (!other.getValue())
	    isRectangular = false;

	return isRectangular;
    }

    public boolean and(boolean other)
    {
	if (!other)
	    isRectangular = false;

	return isRectangular;
    }

    public int hashCode()
    {
	if (isRectangular)
	    return 1;
	else
	    return 0;
    }

    public boolean equals(Object other)
    {
	if (other instanceof BoolValue)
	{
	    return isRectangular == ((BoolValue)other).getValue();
	}

	return false;
    }

    public String toString()
    {
    	return "["+isRectangular+"]";
    }
}


