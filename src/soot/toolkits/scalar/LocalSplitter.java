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





package soot.toolkits.scalar;
import soot.options.*;

import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

/**
 *    A BodyTransformer that attemps to indentify and separate uses of a local
 *    varible that are independent of each other. Conceptually the inverse transform
 *    with respect to the LocalPacker transform.
 *
 *    For example the code:
 *
 *    for(int i; i < k; i++);
 *    for(int i; i < k; i++);
 *
 *    would be transformed into:
 *    for(int i; i < k; i++);
 *    for(int j; j < k; j++);
 *
 *
 *    @see BodyTransformer
 *    @see LocalPacker
 *    @see Body 
 */
public class LocalSplitter extends BodyTransformer
{
    public LocalSplitter( Singletons.Global g ) {}
    public static LocalSplitter v() { return G.v().soot_toolkits_scalar_LocalSplitter(); }

    protected void internalTransform(Body body, String phaseName, Map options)
    {
        Chain units = body.getUnits();
        List webs = new ArrayList();

        if(Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() + "] Splitting locals...");

        Map boxToSet = new HashMap(units.size() * 2 + 1, 0.7f);

        if(Options.v().time())
                Timers.v().splitPhase1Timer.start();

        // Go through the definitions, building the webs
        {
            ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body);

            LocalDefs localDefs;
            
            localDefs = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));

            LocalUses localUses = new SimpleLocalUses(graph, localDefs);
            
            if(Options.v().time())
                Timers.v().splitPhase1Timer.end();
    
            if(Options.v().time())
                Timers.v().splitPhase2Timer.start();

            Set markedBoxes = new HashSet();
            Map boxToUnit = new HashMap(units.size() * 2 + 1, 0.7f);
            
            Iterator codeIt = units.iterator();

            while(codeIt.hasNext())
            {
                Unit s = (Unit) codeIt.next();

                if (s.getDefBoxes().size() > 1)
                    throw new RuntimeException("stmt with more than 1 defbox!");

                if (s.getDefBoxes().size() < 1)
                    continue;

                ValueBox loBox = (ValueBox)s.getDefBoxes().get(0);
                Value lo = loBox.getValue();

                if(lo instanceof Local && !markedBoxes.contains(loBox))
                {
                    LinkedList defsToVisit = new LinkedList();
                    LinkedList boxesToVisit = new LinkedList();

                    List web = new ArrayList();
                    webs.add(web);
                                        
                    defsToVisit.add(s);
                    markedBoxes.add(loBox);
                    
                    while(!boxesToVisit.isEmpty() || !defsToVisit.isEmpty())
                    {
                        if(!defsToVisit.isEmpty())
                        {
                            Unit d = (Unit) defsToVisit.removeFirst();

                            web.add(d.getDefBoxes().get(0));

                            // Add all the uses of this definition to the queue
                            {
                                List uses = localUses.getUsesOf(d);
                                Iterator useIt = uses.iterator();
    
                                while(useIt.hasNext())
                                {
                                    UnitValueBoxPair use = (UnitValueBoxPair) useIt.next();
    
                                    if(!markedBoxes.contains(use.valueBox))
                                    {
                                        markedBoxes.add(use.valueBox);
                                        boxesToVisit.addLast(use.valueBox);
                                        boxToUnit.put(use.valueBox, use.unit);
                                    }
                                }
                            }
                        }
                        else {
                            ValueBox box = (ValueBox) boxesToVisit.removeFirst();

                            web.add(box);

                            // Add all the definitions of this use to the queue.
                            {               
                                List defs = localDefs.getDefsOfAt((Local) box.getValue(),
                                    (Unit) boxToUnit.get(box));
                                Iterator defIt = defs.iterator();
    
                                while(defIt.hasNext())
                                {
                                    Unit u = (Unit) defIt.next();

                                    Iterator defBoxesIter = u.getDefBoxes().iterator();
                                    ValueBox b;

                                    for (; defBoxesIter.hasNext(); )
                                    {
                                        b = (ValueBox)defBoxesIter.next();
                                        if(!markedBoxes.contains(b))
                                        {
                                            markedBoxes.add(b);
                                            defsToVisit.addLast(u);
                                        }
                                    }    
                                }
                            }
                        }
                    }
                }
            }
        }

        // Assign locals appropriately.
        {
            Map localToUseCount = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
            Iterator webIt = webs.iterator();

            while(webIt.hasNext())
            {
                List web = (List) webIt.next();

                ValueBox rep = (ValueBox) web.get(0);
                Local desiredLocal = (Local) rep.getValue();

                if(!localToUseCount.containsKey(desiredLocal))
                {
                    // claim this local for this set

                    localToUseCount.put(desiredLocal, new Integer(1));
                }
                else {
                    // generate a new local

                    int useCount = ((Integer) localToUseCount.get(desiredLocal)).intValue() + 1;
                    localToUseCount.put(desiredLocal, new Integer(useCount));
        
                    Local local = (Local) desiredLocal.clone();
                    local.setName(desiredLocal.getName() + "#" + useCount);
                    
                    body.getLocals().add(local);

                    // Change all boxes to point to this new local
                    {
                        Iterator j = web.iterator();

                        while(j.hasNext())
                        {
                            ValueBox box = (ValueBox) j.next();

                            box.setValue(local);
                        }
                    }
                }
            }
        }
        
        if(Options.v().time())
            Timers.v().splitPhase2Timer.end();

    }   
}
