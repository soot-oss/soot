/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai
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


package soot.toolkits.graph;

import java.util.*;

import soot.*;
import soot.baf.*;
import soot.util.*;

import soot.jimple.*;

/**
 *  Implements a CFG for a Body instance where the nodes are Block
 *  instances. It is a generic implementation used by more specific
 *   classes such as BriefBlockGraph.
 *   @see BriefBlockGraph
 *   @see CompleteBlockGraph
 *   @see ZonedBlockGraph
 *   @see ArrayRefBlockGraph
 */

public class BlockGraph implements DirectedGraph 
{
    Body mBody;
    Chain mUnits;
    List mBlocks;
    List mHeads = new ArrayList();
    List mTails = new ArrayList();

    public static final int COMPLETE = 0;
    public static final int BRIEF = 1;
    public static final int ZONED = 2;

    public static final int ARRAYREF = 99;

   
    /**
     *  Constructs a BriefBlockGraph from a given Body instance.
     *
     *   @param aBody  The Body instance we want a graph for.
     *   @param type   Specifies the type of graph to build. 
     *                 This can be  BRIEF, ZONED or COMPLETE.
     *   @see CompleteBlockGraph
     *   @see BriefBlockGraph
     *   @see ZonedBlockGraph
     *   @see ArrayRefBlockGraph 
     */
    public BlockGraph(Body aBody, int type) 
    {
        /* Algorithm used to compute basic blocks:
         * 1) Identify basic block leaders: these are the units that mark
         *    the start of a basic block (ex: target of branches)
         * 2) Use the leaders found in step 1 to slice the linear chain of 
         *    units into basic blocks, with the leaders as delimiters.
         */ 

        mBody = aBody;
        mUnits = aBody.getUnits();
        

        Map trapBeginUnits = new HashMap(); // Maps a unit to a list of traps whose scopes start at that unit.
        List trapsInScope = null;           //  List of Traps that can catch an exception at a given unit.
        Chain traps = null;                 // All traps for this Body
        List handlerList = new ArrayList(); // list of handler units

        /*
         *  Compute basic block leaders.
         */
        Map leaders = new HashMap();
        
                    
        // Get the list of all traps for this Body
        traps = mBody.getTraps();
        trapsInScope = new ArrayList(traps.size());   // no more than traps.size() traps can be in scope for any given unit.
            
        // Populate the trapBeginUnits Map and handler list
        Iterator trapIt = traps.iterator();
        while(trapIt.hasNext()) {
            Trap someTrap = (Trap) trapIt.next();
            Unit firstUnit = someTrap.getBeginUnit();

            handlerList.add(someTrap.getHandlerUnit());

            List trapList = (List) trapBeginUnits.get(firstUnit);
            if(trapList == null) {
                trapList = new ArrayList(4);
                trapList.add(someTrap);
                trapBeginUnits.put(firstUnit, trapList);
            } else {
                trapList.add(someTrap);
            }
            

            // Get the leaders that bound exception contexts.
            if(type == ZONED) {              
              List predList = new ArrayList();
              predList.add(mUnits.getPredOf(someTrap.getBeginUnit()));
              leaders.put(someTrap.getBeginUnit(), predList);

              predList = new ArrayList();
              predList.add(mUnits.getPredOf(someTrap.getEndUnit()));
              leaders.put(someTrap.getEndUnit(), predList);                    
            }            
        }
        

        /*            
        List trapHeads = new ArrayList();
        trapIt = trapBeginUnits.values().iterator();
        while(trapIt.hasNext() ) {
            List l = (List) trapIt.next();
            // if(l.size() != 1) {throw new RuntimeException("size != 1");}
            Trap someTrap = (Trap) l.get(0);
            trapHeads.add(someTrap.getHandlerUnit());
            trapHeads.add(someTrap.getBeginUnit());
        }        
        */      



        {
            // Iterate over the Body searching for leaders.
            Iterator it = mUnits.iterator();
            Unit currentUnit, nextUnit; 
            nextUnit = it.hasNext() ? (Unit) it.next(): null;

            while(nextUnit != null) {  //
                currentUnit = nextUnit;
                nextUnit = it.hasNext() ? (Unit) it.next(): null;
                

                // for a COMPLETE graph only, get the traps beginning at the currentUnit
                // and add them to the trapsInScope list
                if(type == COMPLETE) {
                    List trapsBeginningHere = (List) trapBeginUnits.get(currentUnit);
                    
                    if(trapsBeginningHere != null) {
                        Iterator iter = trapsBeginningHere.iterator();
                        if(iter != null) {
                            while(iter.hasNext()) {
                                trapsInScope.add(iter.next());
                            }
                        }
                    } 
                }
                
               
                if(currentUnit.branches()) {

                    // iterate over the targets of the branch for this Unit (should only be one right?)
                    Iterator targetIt = currentUnit.getUnitBoxes().iterator();
                    if(!targetIt.hasNext())
                        throw new RuntimeException("error: branching instruction has no target.");
                    
                    while(targetIt.hasNext()) {

                        Unit target = ((UnitBox) targetIt.next()).getUnit();
                        List predecessors;
                      
                        // the target is a leader; add it to the leaders list if it isn't already there;
                        // add the current unit to it's predecessors list.
                        if(!leaders.containsKey(target)) {
                            predecessors= new LinkedList();
                            predecessors.add(currentUnit);
                            Unit targetPred = (Unit) mUnits.getPredOf(target);
                            if(targetPred.fallsThrough())
                                predecessors.add(targetPred);
                                
                            leaders.put(target, predecessors);
                        } else {
                            predecessors = (List) leaders.get(target);
                            predecessors.add(currentUnit);
                        }            
                    }
                

                    // current unit is a branch; this should not happen
                    //  if(nextUnit == null)
                    //throw new RuntimeException("error:  this can only happen if we are a return statement.");

                    
                    // A unit following a branch is a leader.
                    List predecessors;
                    if(!leaders.containsKey(nextUnit)) {
                        predecessors= new LinkedList();
                        leaders.put(nextUnit, predecessors);
                    }
                    // if this branch falls through, add currentUnit to nextUnit's predecessors list
                    if(currentUnit.fallsThrough()) {
                        predecessors = (List) leaders.get(nextUnit);
                        predecessors.add(currentUnit);
                    }
                    

                    
                }

                // A unit following a return unit is a leader. (note !branches && !fallsThrough => return)
                else if(!currentUnit.branches() && !currentUnit.fallsThrough()) {
                    if(nextUnit != null) {
                        if(!leaders.containsKey(nextUnit)) {
                            leaders.put(nextUnit, new LinkedList());
                        } 
                    }

                }

   		// For array reference graph only:
		if (type == ARRAYREF)
		{
		    Stmt stmt = (Stmt)nextUnit;

		    if ((nextUnit != null) &&(stmt.containsArrayRef()))
		    {
			if (!leaders.containsKey(nextUnit))
		        {
			    List predicessors = new LinkedList();
			    predicessors.add(currentUnit);

			    leaders.put(nextUnit, predicessors);
			}
		    }
		}
                
             
                // For complete graphs only:
                if(type ==COMPLETE) {
                    if(trapsInScope.size() != 0 ){
                                       
                        List predecessors;
                        Iterator trapScopeIt = trapsInScope.iterator();
                        while(trapScopeIt.hasNext()) {
                            Trap someTrap = (Trap) trapScopeIt.next();

                                // 1) The first unit of the trap handler is a leader.                            
                            Unit target = someTrap.getHandlerUnit();
                            if(!leaders.containsKey(target)) {
                                predecessors= new LinkedList();
                                predecessors.add(currentUnit);
                                Unit targetPred = (Unit) mUnits.getPredOf(target);
                                if(targetPred.fallsThrough())
                                    predecessors.add(targetPred);
                            
                                leaders.put(target, predecessors);
                            } else {
                                predecessors = (List) leaders.get(target);
                                predecessors.add(currentUnit);
                            }                    
                            
                                // 2) A unit following a unit having exception handlers in it's scope is a leader.
                            if(nextUnit != null) {
                                if(!leaders.containsKey(nextUnit)) {
                                    predecessors= new LinkedList();
                                    leaders.put(nextUnit, predecessors);
                                }
                                if(currentUnit.fallsThrough()) {
                                    predecessors = (List) leaders.get(nextUnit);
                                    
                                    if(!predecessors.contains(currentUnit))
                                        predecessors.add(currentUnit);
                                }
                            }
                            
                                // 3) A unit having exception handlers in it's scope is a leader.
                            if(!leaders.containsKey(currentUnit)) {
                                predecessors= new LinkedList();
                                leaders.put(currentUnit, predecessors);
                            }
                            
                            predecessors =(List) leaders.get(currentUnit);
                            Unit targetPred = (Unit) mUnits.getPredOf(currentUnit);
                            if(targetPred != null && targetPred.fallsThrough())
                                if(!predecessors.contains(targetPred))
                                    predecessors.add(targetPred);
                        }
                    }
                    
 
                    // Remove traps that go out of scope after the current unit
                    Iterator trapScopeIt = trapsInScope.iterator();
                    while(trapScopeIt.hasNext()) {
                        Trap trap = (Trap) trapScopeIt.next();
                        if(trap.getEndUnit() == nextUnit)
                            trapScopeIt.remove();
                    }
                }  
                
                
            }
        }
    
        
        
        

        /* 
         * Build basic block list
         */
        {
            List basicBlockList = new ArrayList(16);
            Unit blockHead, blockTail;    // points to first unit in block
            Block block;
            int indexInMethod = 0;

            Iterator it = mUnits.iterator();
            blockHead = (Unit) mUnits.getFirst();
            int blockLength = 1;
            boolean isHandler = false;

            if(it.hasNext())
                it.next();
            
            while(it.hasNext()) {
                Unit  unit = (Unit) it.next();
                blockLength++;

                if(leaders.containsKey(unit)){
                    blockTail = (Unit) mUnits.getPredOf(unit);
                    block = new Block(blockHead, blockTail, mBody, indexInMethod++, blockLength -1, this);
                    block.setPreds((List) leaders.get(blockHead));
                    basicBlockList.add(block);
                    blockHead = unit;
                    blockLength = 1;
                }                
            }
            
            block = new Block(blockHead, (Unit) mUnits.getLast(),mBody, indexInMethod++, blockLength, this);
            block.setPreds((List) leaders.get(blockHead));
            basicBlockList.add(block);
            
            // Important: the predecessor list built previously for
            // each bb, contains the tail unit of each predecessor
            // block for a given bb. We need to replace each of these
            // unit references by a reference to the unit's enclosing
            // bb.
            it = basicBlockList.iterator();
            while(it.hasNext() ) {
                List newl = new ArrayList();
                Block bb = (Block) it.next();
                List l = bb.getPreds();
                if(l != null) {
                    Iterator predIt = l.iterator();
                    while(predIt.hasNext()) {
                        Unit u = (Unit) predIt.next();
                        Iterator it2 = basicBlockList.iterator();
                        while(it2.hasNext() ) {
                            Block b2 = (Block)it2.next();

                            if(u == b2.getTail()){
                                if(!newl.contains(b2))
                                    newl.add(b2);
                            }
                        }
                    }
                    bb.setPreds(newl);
                }
                else
                {
                    // Hmm.  This BB has no preds.
                    // It must be a head!
                    // Clearly we don't want BB's with no preds around.

                    bb.setPreds(new ArrayList());
                }
            }
                            



            // now compute successors from the predecessor data
            it = basicBlockList.iterator();
            while(it.hasNext()) {
                Block b = (Block) it.next();
                List successorList = new ArrayList(4);
                Iterator it2 = basicBlockList.iterator();
                
                while(it2.hasNext() ) {
                    Block maybeASuccessor = (Block) it2.next();

                        List predecessors = maybeASuccessor.getPreds();
                        if(predecessors!= null && predecessors.contains(b)) {
                            successorList.add(maybeASuccessor);

                    }
                }
                b.setSuccs(successorList);
            }
            
            

            // temporary check on the structure of the code
            // (ie catch goto's pointing to goto's) 
            // check if block A  has a unique sucessor B, which itself 
            // has, as a unique predecessor, block A 
            // Can be deleted in time.


	    /*
	    it = basicBlockList.iterator();
	    while(it.hasNext()) {
		Block currentBlock = (Block) it.next();
		G.v().out.println(currentBlock);
		List blockSuccsList = currentBlock.getSuccs();
		if(blockSuccsList.size() == 1) {
                    Block succBlock = (Block)  blockSuccsList.get(0);
                    if(succBlock.getPreds().size() == 1) {
			if(succBlock.getSuccs().size() == 1 || succBlock.getSuccs().size()==0) { 

			    if(succBlock.getSuccs().size() == 1) {
				Block succBlock2 = (Block)  blockSuccsList.get(0);
				if(mUnits.getSuccOf(currentBlock.getTail()) == succBlock2.getHead() ) {
                                    
				    
				    G.v().out.println("Code structure error dump:");
				    G.v().out.println("Current block: " + currentBlock.toString());
				    G.v().out.println("Succ block: " + succBlock.toString());
				    G.v().out.println("pred: " + succBlock.getPreds().toString() + "succ  :" + succBlock.getSuccs().toString());
                                
				    mBlocks = basicBlockList;
				    G.v().out.println("Printing basic blocks ...");
				    it = basicBlockList.iterator();
				    while(it.hasNext()) {
					G.v().out.println(((Block)it.next()).toString());
				    }
				    throw new RuntimeException("Code structure error");
				}
			    }
			    G.v().out.println("Code structure error detected .");
                            
			}

                                
			else { 

			    G.v().out.println("Code structure error dump:");
			    G.v().out.println("Current block: " + currentBlock.toString());
			    G.v().out.println("Succ block: " + succBlock.toString());
			    G.v().out.println("pred: " + succBlock.getPreds().toString() + "succ  :" + succBlock.getSuccs().toString());
                            
			    mBlocks = basicBlockList;
			    G.v().out.println("Printing basic blocks ...");
			    it = basicBlockList.iterator();
			    while(it.hasNext()) {
				G.v().out.println(((Block)it.next()).toString());
			    }
			    throw new RuntimeException("Code structure error (interesting case)");      
			}


                    }
                    }
                    }
	    */

            mBlocks = basicBlockList;
            
            // build head list
            {
                Iterator blockIt =  mBlocks.iterator();
                while(blockIt.hasNext()) {
                    Block b = (Block) blockIt.next();
                    if(b.getHead() == mUnits.getFirst() ||
                       (type != COMPLETE && handlerList.contains(b.getHead())))
                    {
                        mHeads.add(b);
                    }
                }
                /*
                Iterator ittt = mHeads.iterator();
                G.v().out.println("Heads are:");
                while(ittt.hasNext()) {
                    G.v().out.println("next head: ");
                    G.v().out.println(ittt.next());
                }
                G.v().out.println("done heads");
                */
            }
        }

        // Add edges from the predecessors of trapped blocks directly
        // to the handlers. This is necessary because sometimes the
        // first statement of a trapped block is not even fully
        // executed before an exception is thrown.  Directly
        // inspired/copied from code in UnitGraph. (applicable to
        // complete graph only)
        if(type == COMPLETE){
            Iterator blockIt = mBlocks.iterator();
            while(blockIt.hasNext()){
                Block block = (Block) blockIt.next();
                Unit head = block.getHead();
                List preds;
                
                // are we in a catch block? 
                if(!handlerList.contains(head))
                    continue;

                // no preds to add
                preds = block.getPreds();
                if(preds == null || preds.isEmpty())
                    continue;
                
                // need to clone preds since we are potentially
                // modifying it
                List predsClone = new ArrayList();
                predsClone.addAll(preds);

                // iterate through the trapped blocks
                Iterator predsIt = predsClone.iterator();
                while(predsIt.hasNext()){
                    Block trapped = (Block) predsIt.next();

                    List newPreds = trapped.getPreds();
                    if(newPreds == null)
                        continue;

                    Iterator newPredsIt = newPreds.iterator();

                    // Add an edge from each predecessor of the try
                    // block to the catch block and vice-versa
                    while(newPredsIt.hasNext()){
                        Block newPred = (Block) newPredsIt.next();
                        if(!preds.contains(newPred)){
                            // from block to newPred
                            preds.add(0, newPred);

                            // from newPred to block
                            List newPredSuccs = newPred.getSuccs();
                            newPredSuccs.add(newPredSuccs.size(), block);
                        }
                    }
                }
            }
        }
    }   
    

     
    /**
     *  Returns the underlying Body instance this BlockGraph is derived from.
     *  @return The underlying Body instance this BlockGraph is derived from.
     *  @see BlockGraph
     *  @see Body
     */
    public Body getBody()
    {
        return mBody;
    }

    /**
     *   Returns a list of the Blocks composing this graph.
     *   @return A list of the blocks composing this graph
     *           in the same order as they partition underlying Body instance's
     *           unitchain.
     *  @see Block
     */
    public List getBlocks()
    {
        return mBlocks;
    }

               
    public String toString() {
       
        Iterator it = mBlocks.iterator();
        StringBuffer buf = new StringBuffer();
        while(it.hasNext()) {
            Block someBlock = (Block) it.next();
            
            buf.append(someBlock.toString() + '\n');
        }
        
        return buf.toString();
    }

    /* DirectedGraph implementation   */
    public List getHeads()
    {
        return mHeads;
    }

    public List getTails()
    {
      return new LinkedList();
      //        throw new RuntimeException("not yet implemented");
    }

    public List getPredsOf(Object s)
    {
        Block b = (Block) s;
        return b.getPreds();
    }

    public List getSuccsOf(Object s)
    {
        Block b = (Block) s;
        return b.getSuccs();
    }

    public int size()
    {
        return mBlocks.size();
    }

    public Iterator iterator()
    {
        return mBlocks.iterator();
    }
}

    

    




