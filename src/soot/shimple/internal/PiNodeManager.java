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
import java.util.*;
import soot.shimple.*;
import soot.shimple.toolkits.scalar.*;
import soot.options.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.jimple.toolkits.base.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

/**
 * This class does the real high-level work.  It takes a Jimple body
 * or Jimple/Shimple hybrid body and produces pure Shimple.
 *
 * <p> The work is done in two main steps:
 *
 * <ol>
 * <li> Trivial Phi nodes are added.
 * <li> A renaming algorithm is executed.
 * </ol>
 *
 * <p> This class can also translate out of Shimple by producing an
 * equivalent Jimple body with all Phi nodes removed.
 *
 * <p> Note that this is an internal class, understanding it should
 * not be necessary from a user point-of-view and relying on it
 * directly is not recommended.
 *
 * @author Navindra Umanee
 * @see soot.shimple.ShimpleBody
 * @see <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a>
 **/
public class PiNodeManager
{
    protected ShimpleBody body;
    protected ShimpleFactory sf;
    protected DominatorTree dt;
    protected DominanceFrontier df;
    protected ReversibleGraph cfg;
    protected boolean trimmed;
    
    /**
     * Transforms the provided body to pure SSA form.
     **/
    public PiNodeManager(ShimpleBody body, boolean trimmed)
    {
        this.body = body;
        this.trimmed = trimmed;
        sf = G.v().shimpleFactory;
    }

    public void update()
    {
        cfg = sf.getReverseBlockGraph();
        dt = sf.getReverseDominatorTree();
        df = sf.getReverseDominanceFrontier();
    }
    protected MultiMap varToBlocks;
    
    public boolean insertTrivialPiNodes()
    {
        update();
        boolean change = false;
        MultiMap localsToUsePoints = new SHashMultiMap();
        varToBlocks = new HashMultiMap();    
        
        // compute localsToUsePoints and varToBlocks
        for(Iterator blocksIt = cfg.iterator(); blocksIt.hasNext();){
            Block block = (Block)blocksIt.next();

            for(Iterator unitsIt = block.iterator(); unitsIt.hasNext();){
                Unit unit = (Unit) unitsIt.next();

                List useBoxes = unit.getUseBoxes();
                for(Iterator useBoxesIt = useBoxes.iterator(); useBoxesIt.hasNext();){
                    Value use = ((ValueBox)useBoxesIt.next()).getValue();
                    if(use instanceof Local)
                        localsToUsePoints.put(use, block);
                }

                if(Shimple.isPiNode(unit))
                    varToBlocks.put(Shimple.getLhsLocal(unit), block);
            }
        }

        /* Routine initialisations. */
        
        int[] workFlags = new int[cfg.size()];
        int[] hasAlreadyFlags = new int[cfg.size()];
        
        int iterCount = 0;
        Stack workList = new Stack();

        /* Main Cytron algorithm. */
        
        {
            Iterator localsIt = localsToUsePoints.keySet().iterator();

            while(localsIt.hasNext()){
                Local local = (Local) localsIt.next();

                iterCount++;

                // initialise worklist
                {
                    Iterator useNodesIt = localsToUsePoints.get(local).iterator();
                    while(useNodesIt.hasNext()){
                        Block block = (Block) useNodesIt.next();
                        workFlags[block.getIndexInMethod()] = iterCount;
                        workList.push(block);
                    }
                }

                while(!workList.empty()){
                    Block block = (Block) workList.pop();
                    DominatorNode node = dt.getDode(block);
                    Iterator frontierNodes = df.getDominanceFrontierOf(node).iterator();

                    while(frontierNodes.hasNext()){
                        Block frontierBlock = (Block) ((DominatorNode) frontierNodes.next()).getGode();
                        int fBIndex = frontierBlock.getIndexInMethod();
                        
                        if(hasAlreadyFlags[fBIndex] < iterCount){
                            insertPiNodes(local, frontierBlock);
                            change = true;
                            
                            hasAlreadyFlags[fBIndex] = iterCount;

                            if(workFlags[fBIndex] < iterCount){
                                workFlags[fBIndex] = iterCount;
                                workList.push(frontierBlock);
                            }
                        }
                    }
                }
            }
        }

        if(change)
            sf.clearCache();
        return change;
    }

    public void insertPiNodes(Local local, Block frontierBlock)
    {
        if(varToBlocks.get(local).contains(frontierBlock.getSuccs().get(0)))
            return;

        Unit u = frontierBlock.getTail();

        TRIMMED:
        {
            if(trimmed){
                for(Iterator i = u.getUseBoxes().iterator(); i.hasNext();){
                    Value use = ((ValueBox)i.next()).getValue();
                    if(use.equals(local))
                        break TRIMMED;
                }
                return;
            }
        }
            
        if(u instanceof IfStmt)
            piHandleIfStmt(local, (IfStmt) u);
        else if((u instanceof LookupSwitchStmt) || (u instanceof TableSwitchStmt))
            piHandleSwitchStmt(local, u);
        else
            throw new RuntimeException("Assertion failed: Unhandled stmt: " + u);
    }

    public void piHandleIfStmt(Local local, IfStmt u)
    {
        Unit target = u.getTarget();
        
        PiExpr pi1 = Shimple.v().newPiExpr(local, u);
        PiExpr pi2 = Shimple.v().newPiExpr(local, u);
        Unit add1 = Jimple.v().newAssignStmt(local, pi1);
        Unit add2 = Jimple.v().newAssignStmt(local, pi2);
            
        PatchingChain units = body.getUnits();
            
        // insert after should be safe; a new block should result if
        // the Unit originally after the IfStmt had another predecessor.
        // what about SPatchingChain?  seems sane.
        units.insertAfter(add1, u);

        /* we need to be careful with insertBefore, if target
           already had some other predecessors. */

        // handle immediate predecessor if it falls through
        // *** FIXME: Does SPatchingChain do the right thing?
        PREDFALLSTHROUGH:
        {
            Unit predOfTarget = null;
            try{
                predOfTarget = (Unit) units.getPredOf(target);
            }
            catch(NoSuchElementException e){
                predOfTarget = null;
            }
            
            if(predOfTarget == null)
                break PREDFALLSTHROUGH;
            
            if(predOfTarget.fallsThrough()){
                GotoStmt gotoStmt = Jimple.v().newGotoStmt(target);
                units.insertAfter(gotoStmt, predOfTarget);
            }
        }

        // we do not want to move the pointers for other branching statements
        units.getNonPatchingChain().insertBefore(add2, target);
        u.setTarget(add2);
    }

    public void piHandleSwitchStmt(Local local, Unit u)
    {
        for(Iterator targetIt = u.getUnitBoxes().iterator(); targetIt.hasNext();){
            UnitBox targetBox = (UnitBox) targetIt.next();
            Unit target = targetBox.getUnit();
            
            PiExpr pi1 = Shimple.v().newPiExpr(local, u);
            Unit add1 = Jimple.v().newAssignStmt(local, pi1);
            
            PatchingChain units = body.getUnits();

            /* we need to be careful with insertBefore, if target
               already had some other predecessors. */

            // handle immediate predecessor if it falls through
            // *** FIXME: Does SPatchingChain do the right thing?
            PREDFALLSTHROUGH:
            {
                Unit predOfTarget = null;
                try{
                    predOfTarget = (Unit) units.getPredOf(target);
                }
                catch(NoSuchElementException e){
                    predOfTarget = null;
                }
                
                if(predOfTarget == null)
                    break PREDFALLSTHROUGH;

                if(predOfTarget.fallsThrough()){
                    GotoStmt gotoStmt = Jimple.v().newGotoStmt(target);
                    units.insertAfter(gotoStmt, predOfTarget);
                }
            }

            // we do not want to move the pointers for other branching statements
            units.getNonPatchingChain().insertBefore(add1, target);
            targetBox.setUnit(add1);
        }
    }

    public void eliminatePiNodes(boolean smart)
    {
        if(smart){
            Map newToOld = new HashMap();
            List boxes = new ArrayList();
            
            for(Iterator unitsIt = body.getUnits().iterator(); unitsIt.hasNext();){
                Unit u = (Unit) unitsIt.next();
                PiExpr pe = Shimple.getPiExpr(u);
                if(pe != null){
                    newToOld.put(Shimple.getLhsLocal(u), pe.getValue());
                    unitsIt.remove();
                }
                else{
                    boxes.addAll(u.getUseBoxes());
                }
            }

            for(Iterator boxesIt = boxes.iterator(); boxesIt.hasNext();){
                ValueBox box = (ValueBox) boxesIt.next();
                Value value = box.getValue();
                Value old = (Value) newToOld.get(value);
                if(old != null)
                    box.setValue(old);
            }

            DeadAssignmentEliminator.v().transform(body);
            CopyPropagator.v().transform(body);
            DeadAssignmentEliminator.v().transform(body);            
        }
        else{
            for(Iterator unitsIt = body.getUnits().iterator(); unitsIt.hasNext();){
                Unit u = (Unit) unitsIt.next();
                PiExpr pe = Shimple.getPiExpr(u);
                if(pe != null)
                    ((AssignStmt)u).setRightOp(pe.getValue());
            }
        }
    }
    
    public static List getUseBoxesFromBlock(Block block)
    {
        Iterator unitsIt = block.iterator();
        
        List useBoxesList = new ArrayList();
    
        while(unitsIt.hasNext())
            useBoxesList.addAll(((Unit)unitsIt.next()).getUseBoxes());
        
        return useBoxesList;
    }
}
