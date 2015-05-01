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
import java.util.*;

import soot.jimple.Stmt;
import soot.toolkits.scalar.*;

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
/**
 * @deprecated use {@link MHGPostDominatorsFinder} instead
 */
@Deprecated
public class PostDominatorAnalysis extends BackwardFlowAnalysis<Unit,FlowSet<Unit>> {

    private UnitGraph g;
    private FlowSet<Unit> allNodes;
    
    public PostDominatorAnalysis(UnitGraph g)
    {
        super(g);
        this.g = g;

        initAllNodes();

        doAnalysis();
        
    }

    private void initAllNodes(){
        allNodes = new ArraySparseSet<Unit>();
        Iterator<Unit> it = g.iterator();
        while (it.hasNext()){
            allNodes.add(it.next());
        } 
    }


// STEP 4: Is the merge operator union or intersection?
// INTERSECTION

    @Override
    protected void merge(FlowSet<Unit> in1, FlowSet<Unit> in2, FlowSet<Unit> out)
    {
        in1.intersection(in2, out);	
    }
    
    @Override
    protected void copy(FlowSet<Unit> source, FlowSet<Unit> dest) {        
        source.copy(dest);

    }
   
// STEP 5: Define flow equations.
// dom(s) = s U ( ForAll Y in pred(s): Intersection (dom(y)))
// ie: dom(s) = s and whoever dominates all the predeccessors of s
// 
    @Override
    protected void flowThrough(FlowSet<Unit> in, Unit s, FlowSet<Unit> out)
    {
        if (isUnitEndNode(s)){
//            System.out.println("s: "+s+" is end node");
            out.clear();
            out.add(s);
//            System.out.println("in: "+in+" out: "+out);
        }
        else {
        
//            System.out.println("s: "+s+" is not start node");
            //FlowSet domsOfSuccs = (FlowSet) allNodes.clone();
        
            // for each pred of s
            Iterator<Unit> succsIt = g.getSuccsOf(s).iterator();
            while (succsIt.hasNext()){
                Unit succ = succsIt.next();
                // get the unitToBeforeFlow and find the intersection
//                System.out.println("succ: "+succ);
                FlowSet<Unit> next = getFlowBefore(succ);
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
    @Override
    protected FlowSet<Unit> entryInitialFlow()
    {

    	FlowSet<Unit> fs = new ArraySparseSet<Unit>();
        List<Unit> tails = g.getTails();
//        if (tails.size() != 1) {
//            throw new RuntimeException("Expect one end node only.");
//        }
        fs.add(tails.get(0));
        return fs;
    }

    @Override
    protected FlowSet<Unit> newInitialFlow()
    {
        return allNodes.clone();
    }
    
	/**
	 * Returns true if s post-dominates t.
	 */
	public boolean postDominates(Stmt s, Stmt t) {
		return getFlowBefore(t).contains(s);
	}

        

}
