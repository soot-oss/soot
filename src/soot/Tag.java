/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot;

import java.util.*;

/** Represents a tag; these get attached to implementations of Host.
 *
 * The type of the tag can be deduced from its suffix; .l denotes long,
 * .d denotes double, and .s denotes string. */
public class Tag 
{
    String mName;
    Object mValue;
 
    /** Constructs a tag with the given name and initial value. */
    Tag(String aName, Object aValue)
    {
	mName = aName;
	mValue = aValue;
	validateType();
    }
    
    /** Returns the name of the current tag. */
    public String getName()
    {
	return mName;
    }

    /** Returns a textual representation of the current tag. */
    public String toString()
    {
	return mName + ": " + mValue;
    }

    /** Returns the value of the current tag. */
    public Object getValue()
    {
	return mValue;
    }

    /** Sets the value of the current tag. */
    public void setValue(Object o)
    {	
	mValue = o;
	validateType();
    }
    
    /** Checks that the suffix of the tag matches its type. */
    private void validateType()
    {
	if(mName.endsWith(".l") && !(mValue instanceof Long) ||
	   mName.endsWith(".d") && !(mValue instanceof Double) ||
	   mName.endsWith(".s") && !(mValue instanceof String) )
	    throw new RuntimeException("invalid type for tag: " + mName);	    
    }
     
}
