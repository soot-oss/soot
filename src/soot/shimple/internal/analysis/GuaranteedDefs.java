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

package soot.shimple.internal.analysis;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.options.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

/**
 * Wrapper class for a flow analysis to find all the definitions
 * guaranteed to exist at any program point.
 *
 * @author Navindra Umanee
 **/
public class GuaranteedDefs 
{
    Map unitToGuaranteedDefs;

    public GuaranteedDefs(UnitGraph graph)
    {
        if(Options.v().verbose())
            G.v().out.println("[" + graph.getBody().getMethod().getName() +
                               "]     Constructing GuaranteedDefs...");

        GuaranteedDefsAnalysis analysis = new GuaranteedDefsAnalysis(graph);

        // build unit to guaranteed definitions map
        {
            unitToGuaranteedDefs = new HashMap(graph.size() * 2 + 1, 0.7f);
            Iterator unitIt = graph.iterator();

            while(unitIt.hasNext()){
                Unit s = (Unit) unitIt.next();
                FlowSet set = (FlowSet) analysis.getFlowBefore(s);
                unitToGuaranteedDefs.put
                    (s, Collections.unmodifiableList(set.toList()));
            }
        }
    }

    public List getGuaranteedDefs(Unit s)
    {
        return (List) unitToGuaranteedDefs.get(s);
    }
}

/**
 * Flow analysis to find guaranteed to be available definitions at
 * every program point.
 *
 * <p> Fairly self-documenting.
 **/
class GuaranteedDefsAnalysis extends ForwardFlowAnalysis
{
    FlowSet emptySet;
    Map unitToGenerateSet;
    Map unitToPreserveSet;  // complement of killSet 

    GuaranteedDefsAnalysis(UnitGraph graph)
    {
        super(graph);

        // define empty set, with proper universe for complementation
        {
            Chain locals = graph.getBody().getLocals();
            FlowUniverse localUniverse = new CollectionFlowUniverse(locals);
            emptySet = new ArrayPackedSet(localUniverse);
        }

        // pre-compute preserve sets
        {
            unitToPreserveSet = new HashMap(graph.size() * 2 + 1, 0.7f);
            Iterator unitIt = graph.iterator();

            while(unitIt.hasNext()){
                Unit s = (Unit) unitIt.next();
                BoundedFlowSet killSet = (BoundedFlowSet) emptySet.clone();
                Iterator boxIt = s.getDefBoxes().iterator();

                // calculate kill set
                while(boxIt.hasNext()){
                    ValueBox box = (ValueBox) boxIt.next();
                    
                    if(box.getValue() instanceof Local)
                        killSet.add(box.getValue(), killSet);
                }

                // get preserve set from complement
                killSet.complement(killSet);
                unitToPreserveSet.put(s, killSet);
            }
            
        }

        // pre-compute generate sets
        {
            unitToGenerateSet = new HashMap(graph.size() * 2 + 1, 0.7f);
            Iterator unitIt = graph.iterator();

            while(unitIt.hasNext()){
                Unit s = (Unit) unitIt.next();
                FlowSet genSet = (FlowSet) emptySet.clone();
                Iterator boxIt = s.getDefBoxes().iterator();

                while(boxIt.hasNext()){
                    ValueBox box = (ValueBox) boxIt.next();

                    if(box.getValue() instanceof Local)
                        genSet.add(box.getValue(), genSet);
                }

                unitToGenerateSet.put(s, genSet);
            }
        }

        doAnalysis();
    }

    /**
     * all OUTs are initialized to the full set of definitions
     * OUT(Start) is tweaked in customizeInitialFlowGraph
     **/
    protected Object newInitialFlow()
    {
        BoundedFlowSet initSet = (BoundedFlowSet) emptySet.clone();
        initSet.complement(initSet);
        return initSet;
    }

    /**
     * OUT(Start) is the empty set
     **/
    protected Object entryInitialFlow()
    {
        return emptySet.clone();
    }

    /**
     * We compute OUT straightforwardly.
     **/
    protected void flowThrough(Object inValue, Object unit, Object outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        // Perform kill
        in.intersection((FlowSet) unitToPreserveSet.get(unit), out);

        // Perform generation
        out.union((FlowSet) unitToGenerateSet.get(unit), out);
    }

    /**
     * All paths == Intersection.
     **/
    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;

        FlowSet outSet = (FlowSet) out;

        inSet1.intersection(inSet2, outSet);
    }

    protected void copy(Object source, Object dest)
    {
        FlowSet sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;

        sourceSet.copy(destSet);
    }
}
