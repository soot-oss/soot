

package ca.mcgill.sable.soot.toolkit.scalar;

import ca.mcgill.sable.soot.*;
import java.util.*;


public class UnusedLocalRemover { 
public static void removeUnusedLocals(Body listBody)
    {

        Set usedLocals = new HashSet();

        // Traverse statements noting all the uses
        {
            Iterator unitIt = listBody.getUnits().iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                // Remove all locals in defBoxes from unusedLocals
                {
                    Iterator boxIt = s.getDefBoxes().iterator();

                    while(boxIt.hasNext())
                    {
                        Value value = ((ValueBox) boxIt.next()).getValue();

                        if(value instanceof Local && !usedLocals.contains(value))
                            usedLocals.add(value);
                    }
                }

                // Remove all locals in useBoxes from unusedLocals
                {
                    Iterator boxIt = s.getUseBoxes().iterator();

                    while(boxIt.hasNext())
                    {
                        Value value = ((ValueBox) boxIt.next()).getValue();

                        if(value instanceof Local && !usedLocals.contains(value))
                            usedLocals.add(value);
                    }
                }
            }

        }

        // Remove all locals that are unused.
        {
            Iterator it = listBody.getLocals().iterator();
            
            while(it.hasNext())
            {
                Local local = (Local) it.next();

                if(!usedLocals.contains(local))
                    it.remove();
            }
        }
    }
}
