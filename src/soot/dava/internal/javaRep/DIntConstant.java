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
		String ch = "";

		switch (value) {

		case 0x08: ch = "\\b";  break;
		case 0x09: ch = "\\t";  break;
		case 0x0a: ch = "\\n";  break;
		case 0x0c: ch = "\\f";  break;
		case 0x0d: ch = "\\r";  break;
		case 0x22: ch = "\\\""; break;
		case 0x27: ch = "\\'";  break;
		case 0x5c: ch = "\\\\"; break;

		default:
		    if ((value > 31) && (value < 127))
			ch = new Character( (char) value).toString();

		    else {
			ch = Integer.toHexString( value);

			while (ch.length() < 4)
			    ch = "0" + ch;

			if (ch.length() > 4)
			    ch = ch.substring( ch.length() - 4);
			
			ch = "\\u" + ch;
		    }
		}
		
		return "'" + ch + "'";
	    }

	    else if (type instanceof ByteType) 
		return "(byte) " + new Integer(value).toString();

	return new Integer(value).toString();
    }
}
