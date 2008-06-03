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
import soot.shimple.*;
import soot.toolkits.scalar.*;

import java.util.*;

/**
 * This class implements the LocalUses interface for Shimple.
 * ShimpleLocalUses can be used in conjunction with SimpleLocalDefs to
 * provide Definition/Use and Use/Definition chains in SSA.
 *
 * <p> In addition to the interface required by LocalUses,
 * ShimpleLocalUses also provides a method for obtaining the list of
 * uses given only the Local.  Furthermore, unlike SimpleLocalUses, a
 * LocalDefs object is not required when constructing
 * ShimpleLocalUses.
 *
 * @author Navindra Umanee
 * @see ShimpleLocalDefs
 * @see soot.toolkits.scalar.SimpleLocalDefs
 * @see soot.toolkits.scalar.SimpleLocalUses
 **/
public class ShimpleLocalUses implements LocalUses
{
    protected  Map<Local, ArrayList> localToUses;

    /**
     * Build a LocalUses interface from a ShimpleBody.  Proper SSA
     * form is required, otherwise correct behaviour is not
     * guaranteed.
     **/
    public ShimpleLocalUses(ShimpleBody sb)
    {
        // Instead of rebuilding the ShimpleBody without the
        // programmer's knowledge, throw a RuntimeException
        if(!sb.isSSA())
            throw new RuntimeException("ShimpleBody is not in proper SSA form as required by ShimpleLocalUses.  You may need to rebuild it or use SimpleLocalUses instead.");

        // initialise the map
        localToUses = new HashMap<Local, ArrayList>();
        Iterator localsIt = sb.getLocals().iterator();
        while(localsIt.hasNext()){
            Local local = (Local) localsIt.next();
            localToUses.put(local, new ArrayList());
        }

        // iterate through the units and save each Local use in the
        // appropriate list -- due to SSA form, each Local has a
        // unique def, and therefore one appropriate list.
        Iterator unitsIt = sb.getUnits().iterator();
        while(unitsIt.hasNext()){
            Unit unit = (Unit) unitsIt.next();
            Iterator boxIt = unit.getUseBoxes().iterator();

            while(boxIt.hasNext()){
                ValueBox box = (ValueBox)boxIt.next();
                Value value = box.getValue();

                if(!(value instanceof Local))
                    continue;

                List<UnitValueBoxPair> useList = localToUses.get(value);
                useList.add(new UnitValueBoxPair(unit, box));
            }
        }
    }

    /**
     * Returns all the uses of the given Local as a list of
     * UnitValueBoxPairs, each containing a Unit that uses the local
     * and the corresponding ValueBox containing the Local.  
     *
     * <p> This method is currently not required by the LocalUses
     * interface.
     **/
    public List getUsesOf(Local local)
    {
        List uses = localToUses.get(local);
        if(uses == null)
            return Collections.EMPTY_LIST;
        return uses;
    }

    /**
     * If a Local is defined in the Unit, returns all the uses of that
     * Local as a list of UnitValueBoxPairs, each containing a Unit
     * that uses the local and the corresponding ValueBox containing
     * the Local.
     **/
    public List getUsesOf(Unit unit)
    {
        List defBoxes = unit.getDefBoxes();
        
        switch(defBoxes.size()){
        case 0:
            return Collections.EMPTY_LIST;
        case 1:
            Value local = ((ValueBox)defBoxes.get(0)).getValue();
            if(!(local instanceof Local))
                return Collections.EMPTY_LIST;
            return getUsesOf((Local) local);
        default:
            G.v().out.println("Warning: Unit has multiple definition boxes?");
            List usesList = new ArrayList();
            Iterator defBoxesIt = defBoxes.iterator();
            while(defBoxesIt.hasNext()){
                Value def = ((ValueBox)defBoxesIt.next()).getValue();
                if(def instanceof Local)
                    usesList.addAll(getUsesOf((Local) def));
            }
            return usesList;
        }
    }
}
