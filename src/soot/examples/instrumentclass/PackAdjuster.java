/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrick Lam
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


/* To enable this file:
 *
 * 1. Change the package name to Soot.
 * 2. Create a directory, say ~/soot-extensions/soot
 * 3. Move this file to ~/soot-extensions/soot
 * 4. Add ~/soot-extensions to your classpath before Soot.
 * 5. From ~/soot-extensions, run javac PackAdjuster.java.
 */

package soot.examples.instrumentclass;
import soot.examples.instrumentclass.*;
import soot.*;

/** An example PackAdjuster enabling goto instrumentation. */
public class PackAdjuster
{ 
    public static void adjustPacks(Scene s)
    {
        Pack p = s.getPack("jtp");

        /* You can override this class and provide your 
         * own implementation of adjustPacks.  It would add the necessary
         * Transformer classes annotating something.  */
        p.add(new Transform("jtp.gi", GotoInstrumenter.v()));
    }
}
