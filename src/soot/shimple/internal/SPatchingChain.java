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

package soot.shimple.internal;

import soot.*;
import soot.util.*;
import soot.shimple.*;
import java.util.*;

/**
 * Internal Shimple extension of PatchingChain.
 *
 * @author Navindra Umanee
 * @see soot.PatchingChain
 **/
public class SPatchingChain extends PatchingChain
{
    public SPatchingChain(Chain aChain)
    {
        super(aChain);
    }

    public boolean add(Object o)
    {
        processPhiNode(o);
        return super.add(o);
    }

    public void insertAfter(Object toInsert, Object point)
    {
        // important to do these before the patching, so that
        // computeNeedsPatching works properly
        processPhiNode(toInsert);
        super.insertAfter(toInsert, point);

        Unit unit = (Unit) point;

        // update any pointers from Phi nodes only if the unit
        // being inserted is in the same basic block as point, or if
        // control flows through to the Phi node
        patchpointers:
        {
            // no need to move the pointers
            if(!unit.fallsThrough())
                break patchpointers;
            
            /* handle each UnitBox individually */

            Object[] boxes = unit.getBoxesPointingToThis().toArray();

            for(int i = 0; i < boxes.length; i++){
                UnitBox ub = (UnitBox) boxes[i];

                if(ub.getUnit() != unit)
                    throw new RuntimeException("Assertion failed.");
                if(ub.isBranchTarget())
                    continue;

                SUnitBox box = getSBox(ub);
                Boolean needsPatching = (Boolean) boxToNeedsPatching.get(box);
                
                if(needsPatching == null || box.isUnitChanged()){
                    // if boxes were added or removed to the known Phi
                    if(!boxToPhiNode.containsKey(box)){
                        reprocessPhiNodes();
                        if(!boxToPhiNode.containsKey(box))
                            throw new RuntimeException("SPatchingChain has pointers from a Phi node that has never been seen.");
                    }
                    
                    computeNeedsPatching();
                    needsPatching = (Boolean) boxToNeedsPatching.get(box);

                    if(needsPatching == null){
                        // maybe the user forgot to clearUnitBoxes()
                        // when removing a Phi node, or the user removed
                        // a Phi node and hasn't put it back yet
                        G.v().out.println("WARNING: Orphaned UnitBox to " + unit + "?  SPatchingChain will not move the pointer.");
                        continue;
                    }
                }
                    
                if(needsPatching.booleanValue()){
                    box.setUnit((Unit)toInsert);
                    box.setUnitChanged(false);
                }
            }
        }
    }

    public void insertAfter(List toInsert, Object point)
    {
        processPhiNode(toInsert);
        super.insertAfter(toInsert, point);
    }
    
    public void insertBefore(List toInsert, Object point)
    {
        processPhiNode(toInsert);
        super.insertBefore(toInsert, point);
    }

    public void insertBefore(Object toInsert, Object point)
    {
        processPhiNode(toInsert);
        super.insertBefore(toInsert, point);
    }

    public void addFirst(Object u)
    {
        processPhiNode(u);
        super.addFirst(u);
    }
    
    public void addLast(Object u)
    {
        processPhiNode(u);
        super.addLast(u);
    }

    public boolean remove(Object obj)
    {
        if(contains(obj)){
            Shimple.redirectToPreds(this, (Unit)obj);
        }
        
        return super.remove(obj);
    }
    
    /**
     * Map from UnitBox to the Phi node owning it.
     **/
    protected Map boxToPhiNode = new HashMap();

    /**
     * Flag that indicates whether control flow falls through from the
     * box to the Phi node.  null indicates we probably need a call to
     * computeInternal().
     **/
    protected Map boxToNeedsPatching = new HashMap();

    
    protected void processPhiNode(Object o)
    {
        Unit phiNode = (Unit) o;
        PhiExpr phi = Shimple.getPhiExpr(phiNode);

        // not a Phi node
        if(phi == null)
            return;

        // already processed previously, unit chain manipulations?
        if(boxToPhiNode.values().contains(phiNode))
            return;

        Iterator boxesIt = phi.getUnitBoxes().iterator();
        while(boxesIt.hasNext()){
            UnitBox box = (UnitBox) boxesIt.next();
            boxToPhiNode.put(box, phiNode);
        }
    }

    protected void reprocessPhiNodes()
    {
        Set phiNodes = new HashSet(boxToPhiNode.values());
        boxToPhiNode = new HashMap();
        boxToNeedsPatching = new HashMap();

        Iterator phiNodesIt = phiNodes.iterator();
        while(phiNodesIt.hasNext())
            processPhiNode(phiNodesIt.next());
    }
    
    /**
     * NOTE: This will *miss* all the Phi nodes outside a chain.  So
     * make sure you know what you are doing if you remove a Phi node
     * from a chain and don't put it back or call clearUnitBoxes() on
     * it.
     **/
    protected void computeNeedsPatching()
    {
        {
            Set boxes = boxToPhiNode.keySet();

            if(boxes.isEmpty())
                return;
        }

        // we track the fallthrough control flow from boxes to the
        // corresponding Phi statements.  trackedPhi provides a
        // mapping from the Phi being tracked to its relevant boxes.
        MultiMap trackedPhiToBoxes = new HashMultiMap();

        // consider:
        //
        // if blah goto label1
        // label1:
        //
        // Here control flow both fallsthrough and branches to label1.
        // If such an if statement is encountered, we do not want to
        // move any UnitBox pointers beyond the if statement.
        Set trackedBranchTargets = new HashSet();
        
        Iterator unitsIt = iterator();
        while(unitsIt.hasNext()){
            Unit u = (Unit) unitsIt.next();

            // update trackedPhiToBoxes
            List boxesToTrack = u.getBoxesPointingToThis();
            if(boxesToTrack != null){
                Iterator boxesToTrackIt = boxesToTrack.iterator();
                while(boxesToTrackIt.hasNext()){
                    UnitBox boxToTrack = (UnitBox) boxesToTrackIt.next();

                    if(!boxToTrack.isBranchTarget())
                        trackedPhiToBoxes.put(boxToPhiNode.get(boxToTrack),
                                              boxToTrack);
                }
            }

            // update trackedBranchTargets
            if(u.fallsThrough() && u.branches())
                trackedBranchTargets.addAll(u.getUnitBoxes());
            
            // the tracked Phi nodes may be reached through branching.
            // (note: if u is a Phi node and not a trackedBranchTarget,
            // this is not triggered since u would fall through in that
            // case.)
            if(!u.fallsThrough() || trackedBranchTargets.contains(u)){
                Iterator boxesIt = trackedPhiToBoxes.values().iterator();
                while(boxesIt.hasNext()){
                    SUnitBox box = getSBox(boxesIt.next());
                    boxToNeedsPatching.put(box, Boolean.FALSE);
                    box.setUnitChanged(false);
                }

                trackedPhiToBoxes = new HashMultiMap();
                continue;
            }

            // we found one of the Phi nodes pointing to a Unit
            Set boxes = trackedPhiToBoxes.get(u);
            if(boxes != null){
                Iterator boxesIt = boxes.iterator();
                while(boxesIt.hasNext()){
                    SUnitBox box = getSBox(boxesIt.next());

                    // falls through
                    boxToNeedsPatching.put(box, Boolean.TRUE);
                    box.setUnitChanged(false);
                }

                trackedPhiToBoxes.remove(u);
            }
        }

        // after the iteration, the rest do not fall through
        Iterator boxesIt = trackedPhiToBoxes.values().iterator();
        while(boxesIt.hasNext()){
            SUnitBox box = getSBox(boxesIt.next());
            boxToNeedsPatching.put(box, Boolean.FALSE);
            box.setUnitChanged(false);
        }
    }

    protected SUnitBox getSBox(Object box)
    {
        if(!(box instanceof SUnitBox))
            throw new RuntimeException("Shimple box not an SUnitBox?");

        return (SUnitBox) box;
    }

    protected class SPatchingIterator extends PatchingIterator
    {
        SPatchingIterator()
        {
            super(innerChain);
        }

        SPatchingIterator(Object u)
        {
            super(innerChain, u);
        }

        SPatchingIterator(Object head, Object tail)
        {
            super(innerChain, head, tail);
        }
        
        public void remove()
        {
            if(!state)
                throw new IllegalStateException("remove called before first next() call");
            Shimple.redirectToPreds(SPatchingChain.this, (Unit) lastObject);
            super.remove();
        }
    }

    public Iterator iterator()
    {
        return new SPatchingIterator();
    }

    public Iterator iterator(Object u)
    {
        return new SPatchingIterator(u);
    }

    public Iterator iterator(Object head, Object tail)
    {
        return new SPatchingIterator(head, tail);
    }
}
