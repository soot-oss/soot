/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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

package soot.toolkits.graph;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.scalar.*;
import soot.options.*;

// STEP 1: What are we computing?
// SETS OF Units that are post-dominators => Use ArraySparseSet.
//
// STEP 2: Precisely define what we are computing.
// For each statement compute the set of stmts that post-dominate it 
// 
// STEP 3: Decide whether it is a backwards or forwards analysis.
// FORWARDS
//
//
public class PostDominatorAnalysis extends BackwardFlowAnalysis {

    private UnitGraph g;
    private FlowSet allNodes;
    
    public PostDominatorAnalysis(UnitGraph g)
    {
        super(g);
        this.g = g;

        initAllNodes();

        doAnalysis();
        
    }

    private void initAllNodes(){
        allNodes = new ArraySparseSet();
        Iterator it = g.iterator();
        while (it.hasNext()){
            allNodes.add(it.next());
        } 
    }


// STEP 4: Is the merge operator union or intersection?
// INTERSECTION

    protected void merge(Object in1, Object in2, Object out)
    {
	    FlowSet inSet1 = (FlowSet) in1;
	    FlowSet inSet2 = (FlowSet) in2;
	    FlowSet outSet = (FlowSet) out;

        inSet1.intersection(inSet2, outSet);
	
    }

    protected void copy(Object source, Object dest) {

        FlowSet sourceIn = (FlowSet)source;
        FlowSet destOut = (FlowSet)dest;
        
        sourceIn.copy(destOut);

    }
   
// STEP 5: Define flow equations.
// dom(s) = s U ( ForAll Y in pred(s): Intersection (dom(y)))
// ie: dom(s) = s and whoever dominates all the predeccessors of s
// 
    
    protected void flowThrough(Object inValue, Object unit,
            Object outValue)
    {
        FlowSet in  = (FlowSet) inValue;
        FlowSet out = (FlowSet) outValue;
        Unit    s   = (Unit)    unit;

        if (isUnitEndNode(s)){
//            System.out.println("s: "+s+" is end node");
            out.clear();
            out.add(s);
//            System.out.println("in: "+in+" out: "+out);
        }
        else {
        
//            System.out.println("s: "+s+" is not start node");
            FlowSet domsOfSuccs = (FlowSet) allNodes.clone();
        
            // for each pred of s
            Iterator succsIt = g.getSuccsOf(s).iterator();
            while (succsIt.hasNext()){
                Unit succ = (Unit)succsIt.next();
                // get the unitToBeforeFlow and find the intersection
//                System.out.println("succ: "+succ);
                FlowSet next = (FlowSet) unitToBeforeFlow.get(succ);
//                System.out.println("next: "+next);
//                System.out.println("in before intersect: "+in);
                in.intersection(next, in);
//                System.out.println("in after intersect: "+in);
            }
        
            // intersected with in
       
//            System.out.println("out init: "+out);
            out.intersection(in, out);
            out.add(s);
//            System.out.println("out after: "+out);
        }
    }
    
    private boolean isUnitEndNode(Unit s){
        //System.out.println("head: "+g.getHeads().get(0));
        if( g.getTails().contains(s) )
        	return true;
        return false;
    }

// STEP 6: Determine value for start/end node, and
// initial approximation.
// dom(startNode) = startNode
// dom(node) = allNodes
//
    protected Object entryInitialFlow()
    {

        FlowSet fs = new ArraySparseSet();
        List tails = g.getTails();
//        if (tails.size() != 1) {
//            throw new RuntimeException("Expect one end node only.");
//        }
        fs.add(tails.get(0));
        return fs;
    }


    protected Object newInitialFlow()
    {
        return allNodes.clone();
    }
        

}
