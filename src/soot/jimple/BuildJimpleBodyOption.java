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

public class BuildJimpleBodyOption
{
    public static final int NO_TYPING               = 0x0001,
                            NO_RENAMING             = 0x0002,
                            NO_SPLITTING            = 0x0004,
                            USE_PACKING             = 0x0010,
                            NO_AGGREGATING          = 0x0020,
                            USE_ORIGINAL_NAMES      = 0x0040,
                            AGGRESSIVE_AGGREGATING  = 0x0008;

    public static boolean noTyping(int m)
    {
        return (m & NO_TYPING) != 0;
    }

    public static boolean noRenaming(int m)
    {
        return (m & NO_RENAMING) != 0;
    }

    public static boolean noSplitting(int m)
    {
        return (m & NO_SPLITTING) != 0;
    }

    public static boolean usePacking(int m)
    {
        return (m & USE_PACKING) != 0;
    }

    public static boolean noAggregating(int m)
    {
        return (m & NO_AGGREGATING) != 0;
    }

    public static boolean aggressiveAggregating(int m)
    {
        return (m & AGGRESSIVE_AGGREGATING) != 0;
    }
    
    public static boolean useOriginalNames(int m)
    {
        return (m & USE_ORIGINAL_NAMES) != 0;
    }
}

