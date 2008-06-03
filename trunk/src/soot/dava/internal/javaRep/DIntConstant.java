/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

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
