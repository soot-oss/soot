package soot.dava.internal.javaRep;

import soot.*;
import soot.jimple.*;

public class DIntConstant extends IntConstant
{
    public Type type;

    private DIntConstant(int value, Type type)
    {
	super( value);
	this.type  = type;
    }

    public static DIntConstant v(int value, Type type)
    {
        return new DIntConstant(value, type);
    }

    public String toString()
    {
	if (type != null)
	    if (type instanceof BooleanType) {
		if (value == 0)
		    return "false";
		else 
		    return "true";
	    }
	    else if (type instanceof CharType) {
		String ch = new Character( (char) value).toString();
		if (ch.equals( "\'") || ch.equals( "\\"))
		    ch = "\\" + ch;
		
		return "'" + ch + "'";
	    }

	return new Integer(value).toString();
    }
}
