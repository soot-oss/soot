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

import soot.util.*;
import java.util.*;
import soot.*;
import soot.baf.*;

public class BlockGraph implements DirectedGraph
{
    Body mBody;
    Chain mUnits;
    List mBlocks;
    List mHeads = new ArrayList();
    List mTails = new ArrayList();

    static final int COMPLETE = 0;
    static final int BRIEF = 1;
    static final int ZONED = 2;

    private Map blockToSuccs;
    private Map blockToPreds;
    
    public Body getBody()
    {
        return mBody;
    }
    public List getBlocks()
    {
        return mBlocks;
    }


    /* Technique used to compute basic blocks:
     * 1) Identify basic block leaders: these are the units that mark
     *    the start of a basic block (ex: target of branches)
     * 2) Use the leaders found in step 1 to slice the linear chain of 
     *    units into basic blocks, with the leaders as delimiters.
     */ 
    BlockGraph(Body aBody, int type) 
    {
        mBody = aBody;
        mUnits = aBody.getUnits();
        

        Map trapBeginUnits = new HashMap();  // Map a unit to all 
        List trapsInScope = null;    // List of Trap that can be invoked at a given unit
        Chain traps = null;          // Traps for this Body


        /*
         *  Compute basic block leaders.
         */
        Map leaders = new HashMap();
        
        
            
        // if(type == COMPLETE) {
        // Get the list of all traps for this Body
        traps = mBody.getTraps();
        trapsInScope = new ArrayList(traps.size());
            
        // Populate the trapBeginUnits Map
        Iterator trapIt = traps.iterator();
        while(trapIt.hasNext()) {
            Trap someTrap = (Trap) trapIt.next();
            Unit firstUnit = someTrap.getBeginUnit();
            

                    
            List trapList = (List) trapBeginUnits.get(firstUnit);
            if(trapList == null) {
                trapList = new ArrayList(4);
                trapList.add(someTrap);
                trapBeginUnits.put(firstUnit, trapList);
            } else {
                trapList.add(someTrap);
            }
        }
        


            
        List trapHeads = new ArrayList();
        trapIt = trapBeginUnits.values().iterator();
        while(trapIt.hasNext() ) {
            List l = (List) trapIt.next();
            // if(l.size() != 1) {throw new RuntimeException("size != 1");}
            Trap someTrap = (Trap) l.get(0);
            trapHeads.add(someTrap.getHandlerUnit());
            trapHeads.add(someTrap.getBeginUnit());
        }
        
                



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
            int blockDeltaHeight = 0;
            boolean isHandler = false;

            while(it.hasNext()) {
                Unit  unit = (Unit) it.next();
                blockLength++;
                if(leaders.containsKey(unit)){

                    // this happens if the first unit of the method is under an exception context.(ie first unit is a block and has no pred unit)
                    if((blockTail =(Unit) mUnits.getPredOf(unit)) == null) {
                        blockTail = blockHead;
                    }
                    
                   
                    block = new Block(blockHead, blockTail, mBody, indexInMethod++, blockLength -1, this);
                    block.setPreds((List) leaders.get(blockHead));
                    basicBlockList.add(block);
                    blockHead = unit;
                    blockLength = 1;
                    blockDeltaHeight = 0;
                }                
                
            }
            
            
            block = new Block(blockHead, (Unit) mUnits.getLast(),mBody, indexInMethod++, blockLength, this);
            block.setPreds((List) leaders.get(blockHead));
            basicBlockList.add(block);
            

            
            // Important: the predecessor list built previously for each bb, contains
            // the tail unit of each predecessor block for a given bb. We need to replace each of these
            // unit references by a reference to the unit's enclosing bb.
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
            }
                            



            // now compute successors from the predecessor data
            it = basicBlockList.iterator();
            while(it.hasNext()) {
                Block b = (Block) it.next();
                List successorList = new ArrayList(4);
                Iterator it2 = basicBlockList.iterator();
                
                while(it2.hasNext() ) {
                    Block maybeASuccessor = (Block) it2.next();
                    if(maybeASuccessor != b) {
                        List predecessors = maybeASuccessor.getPreds();
                        if(predecessors!= null && predecessors.contains(b)) {
                            successorList.add(maybeASuccessor);
                        }
                    }
                }
                b.setSuccessors(successorList);
            }
            
            

            // temporary check on the structure of the code
            // (ie catch goto's pointing to goto's) 
            // check if block A  has a unique sucessor B, which itself 
            // has, as a unique predecessor, block A 
            // Can be deleted in time.
            /*            it = basicBlockList.iterator();
                    while(it.hasNext()) {
                    Block currentBlock = (Block) it.next();
                    List blockSuccsList = currentBlock.getSuccessors();
                    if(blockSuccsList.size() == 1) {
                    Block succBlock = (Block)  blockSuccsList.get(0);
                    if(succBlock.getPreds().size() == 1) {
                    if(succBlock.getSuccessors().size() == 1 || succBlock.getSuccessors().size()==0) { 

                    if(succBlock.getSuccessors().size() == 1) {
                    Block succBlock2 = (Block)  blockSuccsList.get(0);
                    if(mUnits.getSuccOf(currentBlock.getTail()) == succBlock2.getHead() ) {
                                    

                    System.out.println("Code structure error dump:");
                    System.out.println("Current block: " + currentBlock.toString());
                    System.out.println("Succ block: " + succBlock.toString());
                    System.out.println("pred: " + succBlock.getPreds().toString() + "succ  :" + succBlock.getSuccessors().toString());
                                
                    mBlocks = basicBlockList;
                    System.out.println("Printing basic blocks ...");
                    it = basicBlockList.iterator();
                    while(it.hasNext()) {
                    System.out.println(((Block)it.next()).toBriefString());
                    }
                    throw new RuntimeException("Code structure error");
                    }
                    }
                    System.out.println("Code structure error detected.");
                            
                    }

                                
                    else { 

                    System.out.println("Code structure error dump:");
                    System.out.println("Current block: " + currentBlock.toString());
                    System.out.println("Succ block: " + succBlock.toString());
                    System.out.println("pred: " + succBlock.getPreds().toString() + "succ  :" + succBlock.getSuccessors().toString());
                            
                    mBlocks = basicBlockList;
                    System.out.println("Printing basic blocks ...");
                    it = basicBlockList.iterator();
                    while(it.hasNext()) {
                    System.out.println(((Block)it.next()).toBriefString());
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
                List handlerList = new ArrayList();
                Iterator trapIt2 = mBody.getTraps().iterator();
                while(trapIt2.hasNext()) {
                    Trap trap = (Trap) trapIt2.next();
                    handlerList.add(trap.getHandlerUnit());    
                }
                
                //                System.out.println("unit first " + mUnits.getFirst());
                Iterator blockIt =  mBlocks.iterator();
                while(blockIt.hasNext()) {
                    Block b = (Block) blockIt.next();
                    if(b.getHead() == mUnits.getFirst() || handlerList.contains(b.getHead())) {
                        mHeads.add(b);
                    }
                }
                /*
                Iterator ittt = mHeads.iterator();
                System.out.println("Heads are:");
                while(ittt.hasNext()) {
                    System.out.println("next head: ");
                    System.out.println(ittt.next());
                }
                System.out.println("done heads");
                */
            }
        }
    }   
    
        
        
    public String toString() {
       
        Iterator it = mBlocks.iterator();
        StringBuffer buf = new StringBuffer();
        while(it.hasNext()) {
            Block someBlock = (Block) it.next();
            
            buf.append(someBlock.toBriefString() + '\n');
        }
        
        return buf.toString();
    }


    // Directed graph implementation
    public List getHeads()
    {
        return mHeads;
    }
    public List getTails()
    {
        throw new RuntimeException("not yet implemented");
    }
    public List getPredsOf(Unit s)
    {
        throw new RuntimeException("not yet implemented");
    }
    public List getSuccsOf(Unit s)
    {
        throw new RuntimeException("not yet implemented");
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

    

    




