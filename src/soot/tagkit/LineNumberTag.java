/* Soot - a J*va Optimization Framework
 * Copyright (C) 2001 Feng Qian
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.tagkit;

public class LineNumberTag implements Tag
{
    /* it is a u2 value representing line number. */
    int line_number;
    public LineNumberTag(int ln)
    {
	line_number = ln;
    }

    public String getName()
    {
	return "LineNumberTag";
    }

    public byte[] getValue()
    {
	byte[] v = new byte[2];
	v[0] = (byte)(line_number/256);
	v[1] = (byte)(line_number%256);
	return v;
    }

    public int getLineNumber()
    {
	return line_number;
    }

    public String toString()
    {
   	return ""+line_number;
    }

}
