/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


package soot.jimple.toolkits.scalar;
import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.jimple.Jimple;
import soot.jimple.LookupSwitchStmt;

/**
 * Removes empty switch statements which always take the default action from a
 * method body, i.e. blocks of the form switch(x) { default: ... }. Such blocks
 * are replaced by the code of the default block.
 * @author Steven Arzt
 *
 */
public class EmptySwitchEliminator extends BodyTransformer
{
    public EmptySwitchEliminator( Singletons.Global g ) {}
    public static EmptySwitchEliminator v() { return G.v().soot_jimple_toolkits_scalar_EmptySwitchEliminator(); }

    protected void internalTransform(Body b, String phaseName, Map<String,String> options)
    {
    	Iterator<Unit> it = b.getUnits().snapshotIterator();
        while (it.hasNext()) {
        	Unit u = it.next();
        	if (u instanceof LookupSwitchStmt) {
        		LookupSwitchStmt sw = (LookupSwitchStmt) u;
        		if (sw.getTargetCount() == 0 && sw.getDefaultTarget() != null)
        			b.getUnits().swapWith(sw, Jimple.v().newGotoStmt(sw.getDefaultTarget()));
        	}
        }
        
    }
}
