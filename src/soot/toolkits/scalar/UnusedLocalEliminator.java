

package soot.toolkits.scalar;

import soot.*;
import java.util.*;


public class UnusedLocalEliminator extends BodyTransformer
{ 
    private static UnusedLocalEliminator instance = new UnusedLocalEliminator();
    private UnusedLocalEliminator() {}

    public static UnusedLocalEliminator v() { return instance; }

    protected void internalTransform(Body body, Map options)
    {
        Set usedLocals = new HashSet();

        // Traverse statements noting all the uses
        {
            Iterator unitIt = body.getUnits().iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                // Remove all locals in defBoxes from unusedLocals
                {
                    Iterator boxIt = s.getUseAndDefBoxes().iterator();

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
            Iterator it = body.getLocals().iterator();
            
            while(it.hasNext())
            {
                Local local = (Local) it.next();

                if(!usedLocals.contains(local))
                    it.remove();
            }
        }
    }
}
