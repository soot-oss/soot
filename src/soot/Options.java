/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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

public class Options
{
    /** This method returns true iff key "name" is in options 
        and maps to "true". */
    public static boolean getBoolean(Map options, String name)
    {
        return options.containsKey(name) &&
            options.get(name).equals("true");
    }

    /** This method returns the value of "name" in options 
        or "" if "name" is not found. */
    public static String getString(Map options, String name)
    {
        return options.containsKey(name) ?
            (String)options.get(name) : "";
    }

    /** This method returns the float value of "name" in options 
        or 1.0 if "name" is not found. */
    public static float getFloat(Map options, String name)
    {
        return options.containsKey(name) ?
            new Float((String)options.get(name)).floatValue() : 1.0f;
    }

    /** This method returns the integer value of "name" in options 
        or 0 if "name" is not found. */
    public static int getInt(Map options, String name)
    {
        return options.containsKey(name) ?
            new Integer((String)options.get(name)).intValue() : 0;
    }

    /** Prints a warning if some key in options is not in declaredOptions;
      * throws an exception in debug mode. */
    public static void checkOptions(Map options, String phase, String declaredOptions)
    {
        HashSet declaredSet = new HashSet();

        StringTokenizer tokenizer = new StringTokenizer(declaredOptions, " ");
        while(tokenizer.hasMoreElements()) 
        {
            String option = tokenizer.nextToken();
            declaredSet.add(option);
        }

        Iterator keysIt = options.keySet().iterator();
        while (keysIt.hasNext())
        {
            String usedOption = (String)keysIt.next();
            if (!(declaredSet.contains(usedOption)))
            {
                /* aha! */
                if (soot.Main.isInDebugMode)
                    throw new RuntimeException("use of undeclared phase option "+usedOption);
                else
                    System.out.println("WARNING: use of undeclared phase option "+usedOption+" in phase "+phase);
            }
        }
    }
}
