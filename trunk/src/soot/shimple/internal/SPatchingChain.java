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
import soot.options.Options;
import soot.util.*;
import soot.shimple.*;
import java.util.*;

/**
 * Internal Shimple extension of PatchingChain.
 *
 * @author Navindra Umanee
 * @see soot.PatchingChain
 **/
public class SPatchingChain extends PatchingChain<Unit>
{
    /**
     * Needed to find non-trapped Units of the body.
     **/
    Body body = null;
    boolean debug;
    
    public SPatchingChain(Body aBody, Chain aChain)
    {
        super(aChain);
        this.body = aBody;
        this.debug = Options.v().debug();
        if(aBody instanceof ShimpleBody)
            debug |= ((ShimpleBody)aBody).getOptions().debug();
    }

    public boolean add(Unit o)
    {
        processPhiNode(o);
        return super.add(o);
    }

    public void swapWith(Unit out, Unit in)
    {
        // Ensure that branching statements are swapped correctly.
        // The normal swapWith implementation would still work
        // correctly but redirectToPreds performed during the remove
        // would be more expensive and might print warnings if no
        // actual CFG predecessors for out was found due to the
        // insertion of branching statement in.
        processPhiNode(in);
        Shimple.redirectPointers((Unit) out, (Unit) in);
        super.insertBefore(in, out);
        super.remove(out);
    }
    
    public void insertAfter(Unit toInsert, Unit point)
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

            // move pointers unconditionally, needed as a special case
            if(!unit.branches()){
                Set trappedUnits = Collections.EMPTY_SET;
                if(body != null)
                    trappedUnits = TrapManager.getTrappedUnitsOf(body);
                if(!trappedUnits.contains(unit)){
                    Shimple.redirectPointers(unit, (Unit) toInsert);
                    break patchpointers;
                }
            }
            
            /* handle each UnitBox individually */

            UnitBox[] boxes = (UnitBox[]) unit.getBoxesPointingToThis().toArray(new UnitBox[0]);

            for (UnitBox ub : boxes) {

                if(ub.getUnit() != unit)
                    throw new RuntimeException("Assertion failed.");
                if(ub.isBranchTarget())
                    continue;

                SUnitBox box = getSBox(ub);
                Boolean needsPatching = boxToNeedsPatching.get(box);
                
                if(needsPatching == null || box.isUnitChanged()){
                    // if boxes were added or removed to the known Phi
                    if(!boxToPhiNode.containsKey(box)){
                        reprocessPhiNodes();

                        // *** FIXME: Disabling this allows us to have
                        // PiExpr that have UnitBox pointers.
                        // I think this means that any changes 
                        // to the relevant Unit will be ignored by
                        //  SPatchingChain.
                        //
                        // Hopefully this also means that any
                        // transformation that moves/removes/modifies
                        // a Unit pointed at by a PiExpr knows what
                        // it's doing.
                        if(!boxToPhiNode.containsKey(box) && debug)
                            throw new RuntimeException("SPatchingChain has pointers from a Phi node that has never been seen.");
                    }
                    
                    computeNeedsPatching();
                    needsPatching = boxToNeedsPatching.get(box);

                    if(needsPatching == null){
                        // maybe the user forgot to clearUnitBoxes()
                        // when removing a Phi node, or the user removed
                        // a Phi node and hasn't put it back yet
                        if(debug)
                            G.v().out.println("Warning: Orphaned UnitBox to " + unit + "?  SPatchingChain will not move the pointer.");
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

    public void insertAfter(List<Unit> toInsert, Unit point)
    {
        for (Unit unit : toInsert) {
            processPhiNode(unit);
        }
        super.insertAfter(toInsert, point);
    }
    
    public void insertBefore(List<Unit> toInsert, Unit point)
    {
        for (Unit unit : toInsert) {
            processPhiNode(unit);
        }
        super.insertBefore(toInsert, point);
    }

    public void insertBefore(Unit toInsert, Unit point)
    {
        processPhiNode(toInsert);
        super.insertBefore(toInsert, point);
    }

    public void addFirst(Unit u)
    {
        processPhiNode(u);
        super.addFirst(u);
    }
    
    public void addLast(Unit u)
    {
        processPhiNode(u);
        super.addLast(u);
    }

    public boolean remove(Unit obj)
    {
        if(contains(obj)){
            Shimple.redirectToPreds(body, (Unit)obj);
        }
        
        return super.remove(obj);
    }
    
    /**
     * Map from UnitBox to the Phi node owning it.
     **/
    protected Map<UnitBox, Unit> boxToPhiNode = new HashMap<UnitBox, Unit>();

    /**
     * Flag that indicates whether control flow falls through from the
     * box to the Phi node.  null indicates we probably need a call to
     * computeInternal().
     **/
    protected Map<SUnitBox, Boolean> boxToNeedsPatching = new HashMap<SUnitBox, Boolean>();

    
    protected void processPhiNode(Unit o)
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
        Set<Unit> phiNodes = new HashSet<Unit>(boxToPhiNode.values());
        boxToPhiNode = new HashMap<UnitBox, Unit>();
        boxToNeedsPatching = new HashMap<SUnitBox, Boolean>();

        Iterator<Unit> phiNodesIt = phiNodes.iterator();
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
            Set<UnitBox> boxes = boxToPhiNode.keySet();

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
                Iterator<UnitBox> boxesIt = trackedPhiToBoxes.values().iterator();
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
                Iterator<UnitBox> boxesIt = boxes.iterator();
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
        Iterator<UnitBox> boxesIt = trackedPhiToBoxes.values().iterator();
        while(boxesIt.hasNext()){
            SUnitBox box = getSBox(boxesIt.next());
            boxToNeedsPatching.put(box, Boolean.FALSE);
            box.setUnitChanged(false);
        }
    }

    protected SUnitBox getSBox(UnitBox box)
    {
        if(!(box instanceof SUnitBox))
            throw new RuntimeException("Shimple box not an SUnitBox?");

        return (SUnitBox) box;
    }

    protected class SPatchingIterator extends PatchingIterator
    {
        SPatchingIterator(Chain innerChain)
        {
            super(innerChain);
        }

        SPatchingIterator(Chain innerChain, Unit u)
        {
            super(innerChain, u);
        }

        SPatchingIterator(Chain innerChain, Unit head, Unit tail)
        {
            super(innerChain, head, tail);
        }
        
        public void remove()
        {
            Unit victim = (Unit) lastObject;
            
            if(!state)
                throw new IllegalStateException("remove called before first next() call");
            Shimple.redirectToPreds(SPatchingChain.this.body, victim);

            // work around for inadequate inner class support in javac 1.2
            // super.remove();
            Unit successor;

            if((successor = (Unit)getSuccOf(victim)) == null)
                successor = (Unit)getPredOf(victim);

            innerIterator.remove();
            victim.redirectJumpsToThisTo(successor);
        }
    }

    public Iterator iterator()
    {
        return new SPatchingIterator(innerChain);
    }

    public Iterator iterator(Unit u)
    {
        return new SPatchingIterator(innerChain, u);
    }

    public Iterator iterator(Unit head, Unit tail)
    {
        return new SPatchingIterator(innerChain, head, tail);
    }
}
