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





package soot.jimple;

import soot.*;
import soot.util.*;
import java.util.*;
import java.io.*;

public class PrintJimpleBodyOption
{
    public static final int USE_ABBREVIATIONS = 0x0001,
                            DEBUG_MODE        = 0x0002,
                            NUMBERED          = 0x0004,
			    XML_OUTPUT	      = 0x0008;

    protected PrintJimpleBodyOption()
    {
    }

    public static boolean useAbbreviations(int m)
    {
        return (m & USE_ABBREVIATIONS) != 0;
    }

    public static boolean numbered(int m)
    {
        return (m & NUMBERED) != 0;
    }
    
    public static boolean debugMode(int m)
    {
        return (m & DEBUG_MODE) != 0;
    }

    public static boolean xmlOutput(int m)
    {
	return (m & XML_OUTPUT) != 0;
    }
}


    





