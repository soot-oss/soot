/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

package soot.shimple.toolkits.scalar;

import soot.*;
import soot.util.*;
import soot.shimple.*;
import soot.toolkits.scalar.*;
import java.util.*;

/**
 * This class implements the LocalDefs interface for Shimple.
 * ShimpleLocalDefs can be used in conjunction with SimpleLocalUses to
 * provide Definition/Use and Use/Definition chains in SSA.
 *
 * <p> This implementation can be considered a small demo for how SSA
 * can be put to good use since it is much simpler than
 * soot.toolkits.scalar.SimpleLocalDefs. Shimple can often be treated
 * as Jimple with the added benefits of SSA assumptions.
 *
 * <p> In addition to the interface required by LocalDefs,
 * ShimpleLocalDefs also provides a method for obtaining the
 * definition Unit given only the Local.
 *
 * @author Navindra Umanee
 * @see ShimpleLocalUses
 * @see soot.toolkits.scalar.SimpleLocalDefs
 * @see soot.toolkits.scalar.SimpleLocalUses
 **/
public class ShimpleLocalDefs implements LocalDefs
{
    protected  Map localToDefs;

    /**
     * Build a LocalDefs interface from a ShimpleBody.  Proper SSA
     * form is required, otherwise correct behaviour is not
     * guaranteed.
     **/
    public ShimpleLocalDefs(ShimpleBody sb)
    {
        // Instead of rebuilding the ShimpleBody without the
        // programmer's knowledge, throw a RuntimeException
        if(!sb.isSSA())
            throw new RuntimeException("ShimpleBody is not in proper SSA form as required by ShimpleLocalDefs.  You may need to rebuild it or use SimpleLocalDefs instead.");

        // build localToDefs map simply by iterating through all the
        // units in the body and saving the unique definition site for
        // each local -- no need for fancy analysis 
        {
            Chain unitsChain = sb.getUnits();
            Iterator unitsIt = unitsChain.iterator();
            localToDefs = new HashMap(unitsChain.size() * 2 + 1, 0.7f);
        
            while(unitsIt.hasNext()){
                Unit unit = (Unit) unitsIt.next();
                Iterator defBoxesIt = unit.getDefBoxes().iterator();
                while(defBoxesIt.hasNext()){
                    Value value = ((ValueBox)defBoxesIt.next()).getValue();

                    // only map locals
                    if(!(value instanceof Local))
                        continue;
                        
                    localToDefs.put(value, new SingletonList(unit));
                }
            }
        }
    }

    /**
     * Unconditionally returns the definition site of a local (as a
     * singleton list).
     *
     * <p> This method is currently not required by the LocalDefs
     * interface.
     **/
    public List getDefsOf(Local l)
    {
        List defs = (List) localToDefs.get(l);

        if(defs == null)
            throw new RuntimeException("Local not found in Body.");

        return defs;
    }

    /**
     * Returns the definition site for a Local at a certain point
     * (Unit) in a method as a singleton list.
     *
     * @param l the Local in question.
     * @param s a unit that specifies the method context (location) to
     * query for the definitions of the Local.
     * @return a singleton list containing the definition site.
     **/
    public List getDefsOfAt(Local l, Unit s)
    {
        // For consistency with SimpleLocalDefs, check that the local
        // is indeed used in the given Unit.  This neatly sidesteps
        // the problem of checking whether the local is actually
        // defined at the given point in the program.
        {
            Iterator boxIt = s.getUseBoxes().iterator();
            boolean defined = false;

            while(boxIt.hasNext()){
                Value value = ((ValueBox) boxIt.next()).getValue();
                if(value.equals(l)){
                    defined = true;
                    break;
                }
            }

            if(!defined)
                throw new RuntimeException("Illegal LocalDefs query; local " + l + " is not being used at " + s);
        }

        return getDefsOf(l);
    }
}
