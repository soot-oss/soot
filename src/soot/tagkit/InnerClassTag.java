/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Archie L. Cobbs
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

import java.io.UnsupportedEncodingException;
import soot.*;

public class InnerClassTag implements Tag
{
    String innerClass;
    String outerClass;
    String name;
    int accessFlags;

    public InnerClassTag(String innerClass, String outerClass, String name, int accessFlags)
    {
	this.innerClass = innerClass;
	this.outerClass = outerClass;
	this.name = name;
	this.accessFlags = accessFlags;
    }

    public String getName()
    {
	return "InnerClassTag";
    }

    /**
     * Returns the inner class name (only) encoded in UTF8.
     * There is no obvious standalone byte[] encoding for this
     * attribute because it contains embedded constant pool indicies.
     */
    public byte[] getValue()
    {
	try {
		return innerClass.getBytes("UTF8");
	} catch (UnsupportedEncodingException e) {
		return new byte[0];
	}
    }

    public String getInnerClass()
    {
	return innerClass;
    }

    public String getOuterClass()
    {
	return outerClass;
    }

    public String getShortName()
    {
	return name;
    }

    public int getAccessFlags()
    {
	return accessFlags;
    }

    public String toString()
    {
	return "[inner="+innerClass+", outer="+outerClass
	    +", name="+name+",flags="+accessFlags+"]";
    }
}

