/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Feng Qian
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

package soot.jimple.toolkits.annotation.tags;

import soot.*;

import java.util.*;
import java.io.*;

/** Implementation of the Tag interface for array bounds checks.
 */
public class ArrayCheckTag implements OneByteCodeTag
{
    private final static String NAME = "ArrayCheckTag";

    private boolean lowerCheck = true;
    private boolean upperCheck = true;

    /** 
     * A tag represents two bounds checks of an array reference.
     * The value 'true' indicates check needed.
     */
    public ArrayCheckTag(boolean lower, boolean upper)
    {
	lowerCheck = lower;
	upperCheck = upper;
    }

    /** 
     * Returns back the check information in binary form, which
     * will be written into the class file.
     */    
    public byte[] getValue()
    {
        byte[] value = new byte[1];

	value[0] = 0;
	
	if (lowerCheck)
	    value[0] |= 0x01;
	
	if (upperCheck)
	    value[0] |= 0x02;

	return value;
    }
   
  /** Needs upper bound check?
   */ 
    public boolean isCheckUpper()
    {
	return upperCheck;
    }
   
  /** Needs lower bound check?
   */ 
    public boolean isCheckLower()
    {
	return lowerCheck;
    }

    public String getName()
    {
	return NAME;
    }

    public String toString()
    {
	return   (lowerCheck ? "[potentially unsafe lower bound]": "[safe lower bound]") +"" +  (upperCheck ? "[potentially unsafe upper bound]":"[safe upper bound]");
    }
}


