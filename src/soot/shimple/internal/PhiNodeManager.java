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
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

/**
 * @author Navindra Umanee
 * @see soot.shimple.ShimpleBody
 * @see <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a>
 **/
public class PhiNodeManager
{
    protected ShimpleBody body;
    protected ShimpleFactory sf;
    protected DominatorTree<Block> dt;
    protected DominanceFrontier<Block> df;
    protected BlockGraph cfg;
    protected GuaranteedDefs gd;
    
    public PhiNodeManager(ShimpleBody body, ShimpleFactory sf)
    {
        this.body = body;
        this.sf = sf;
    }

    public void update()
    {
        gd = new GuaranteedDefs(sf.getUnitGraph());
        cfg = sf.getBlockGraph();
        dt = sf.getDominatorTree();
        df = sf.getDominanceFrontier();
    }

    protected MultiMap<Local, Block> varToBlocks;
    
    /**
     * Phi node Insertion Algorithm from Cytron et al 91, P24-5,
     *
     * <p>Special Java case: If a variable is not defined along all
     * paths of entry to a node, a Phi node is not needed.</p>
     **/
    public boolean insertTrivialPhiNodes()
    {
		update();
		boolean change = false;
		varToBlocks = new HashMultiMap<Local, Block>();
		Map<Local, List<Block>> localsToDefPoints = new HashMap<Local, List<Block>>();

		// compute localsToDefPoints and varToBlocks
		for (Block block : cfg) {
			for (Unit unit : block) {
				List<ValueBox> defBoxes = unit.getDefBoxes();
				for (ValueBox vb : defBoxes) {
					Value def = vb.getValue();
					if (def instanceof Local) {
						Local local = (Local) def;
						List<Block> def_points = null;
						if (localsToDefPoints.containsKey(local)) {
							def_points = localsToDefPoints.get(local);
						} else {
							def_points = new ArrayList<Block>();
							localsToDefPoints.put(local, def_points);
						}
						def_points.add(block);
					}
				}

				if (Shimple.isPhiNode(unit))
					varToBlocks.put(Shimple.getLhsLocal(unit), block);
			}
		}

        /* Routine initialisations. */
        
        int[] workFlags = new int[cfg.size()];
        int iterCount = 0;
        Stack<Block> workList = new Stack<Block>();

        Map<Integer, Integer> has_already = new HashMap<Integer, Integer>();
        for(Iterator<Block> blocksIt = cfg.iterator(); blocksIt.hasNext(); ){
          Block block = blocksIt.next();
          has_already.put(block.getIndexInMethod(), 0);
        }

        /* Main Cytron algorithm. */
        
        {
        	for (Local local : localsToDefPoints.keySet()) {
                iterCount++;

                // initialise worklist
                {
                    List<Block> def_points = localsToDefPoints.get(local);
                    //if the local is only defined once, no need for phi nodes
                    if(def_points.size() == 1){
                      continue;
                    }
                    for(Block block : def_points){
                        workFlags[block.getIndexInMethod()] = iterCount;
                        workList.push(block);
                    }
                }

                while(!workList.empty()){
                    Block block = workList.pop();
                    DominatorNode<Block> node = dt.getDode(block);
                    Iterator<DominatorNode<Block>> frontierNodes = df.getDominanceFrontierOf(node).iterator();

                    while(frontierNodes.hasNext()){
                        Block frontierBlock = frontierNodes.next().getGode();
                        int fBIndex = frontierBlock.getIndexInMethod();

                        Iterator<Unit> unitsIt = frontierBlock.iterator();
                        if(!unitsIt.hasNext()){
                          continue;
                        }
                        
                        if(has_already.get(frontierBlock.getIndexInMethod()) < iterCount)
                        {
                            has_already.put(frontierBlock.getIndexInMethod(), iterCount);
                            prependTrivialPhiNode(local, frontierBlock);
                            change = true;

                            if(workFlags[fBIndex] < iterCount){
                                workFlags[fBIndex] = iterCount;
                                workList.push(frontierBlock);
                            }
                        } 
                    }
                }
            }
        }

        return change;
    }

    /**
     * Inserts a trivial Phi node with the appropriate number of
     * arguments.
     **/
    public void prependTrivialPhiNode(Local local, Block frontierBlock)
    {
        List<Block> preds = frontierBlock.getPreds();
        PhiExpr pe = Shimple.v().newPhiExpr(local, preds);
        pe.setBlockId(frontierBlock.getIndexInMethod());
        Unit trivialPhi = Jimple.v().newAssignStmt(local, pe);
        Unit blockHead = frontierBlock.getHead();

        // is it a catch block?
        if(blockHead instanceof IdentityUnit)
            frontierBlock.insertAfter(trivialPhi, frontierBlock.getHead());
        else
            frontierBlock.insertBefore(trivialPhi, frontierBlock.getHead());

        varToBlocks.put(local, frontierBlock);
    }

    /**
     * Exceptional Phi nodes have a huge number of arguments and control
     * flow predecessors by default.  Since it is useless trying to keep
     * the number of arguments and control flow predecessors in synch,
     * we might as well trim out all redundant arguments and eliminate
     * a huge number of copy statements when we get out of SSA form in
     * the process.
     **/
    public void trimExceptionalPhiNodes()
    {
        Set<Unit> handlerUnits = new HashSet<Unit>();
        Iterator<Trap> trapsIt = body.getTraps().iterator();

        while(trapsIt.hasNext()) {
            Trap trap = trapsIt.next();
            handlerUnits.add(trap.getHandlerUnit());
        }

        for (Block block : cfg) {
            // trim relevant Phi expressions
            if(handlerUnits.contains(block.getHead())){
            	for (Unit unit : block) {
                    //if(!(newPhiNodes.contains(unit)))
                    PhiExpr phi = Shimple.getPhiExpr(unit);

                    if(phi == null)
                        continue;

                    trimPhiNode(phi);
                }
            }
        }
    }

    /**
     * @see #trimExceptionalPhiNodes()
     **/
    public void trimPhiNode(PhiExpr phiExpr)
    {
        /* A value may appear many times in an exceptional Phi. Hence,
           the same value may be associated with many UnitBoxes. We
           build the MultiMap valueToPairs for convenience.  */

        MultiMap<Value, ValueUnitPair> valueToPairs = new HashMultiMap<Value, ValueUnitPair>();
        for (ValueUnitPair argPair : phiExpr.getArgs()) {
            Value value = argPair.getValue();
            valueToPairs.put(value, argPair);
        }

        /* Consider each value and see if we can find the dominating
           UnitBoxes.  Once we have found all the dominating
           UnitBoxes, the rest of the redundant arguments can be
           trimmed.  */
        
        Iterator<Value> valuesIt = valueToPairs.keySet().iterator();
        while(valuesIt.hasNext()){
            Value value = valuesIt.next();

            // although the champs list constantly shrinks, guaranteeing
            // termination, the challengers list never does.  This could
            // be optimised.
            Set<ValueUnitPair> pairsSet = valueToPairs.get(value);
            List<ValueUnitPair> champs = new LinkedList<ValueUnitPair>(pairsSet);
            List<ValueUnitPair> challengers = new LinkedList<ValueUnitPair>(pairsSet);
            
            // champ is the currently assumed dominator
            ValueUnitPair champ = champs.remove(0);
            Unit champU = champ.getUnit();

            // hopefully everything will work out the first time, but
            // if not, we will have to try a new champion just in case
            // there is more that can be trimmed.
            boolean retry = true;
            while(retry){
                retry = false;

                // go through each challenger and see if we dominate them
                // if not, the challenger becomes the new champ
                for (Iterator<ValueUnitPair> iterator = challengers.iterator(); iterator.hasNext(); ) {
                    ValueUnitPair challenger = iterator.next();
                    if (challenger.equals(champ))
                        continue;
                    Unit challengerU = challenger.getUnit();

                    // kill the challenger
                    if (dominates(champU, challengerU)) {
                        phiExpr.removeArg(challenger);
                        iterator.remove();
                    }
                    // we die, find a new champ
                    else if (dominates(challengerU, champU)) {
                        phiExpr.removeArg(champ);
                        champ = challenger;
                        champU = champ.getUnit();
                    }

                    // neither wins, oops!  we'll have to try the next
                    // available champ at the next pass.  It may very
                    // well be inevitable that we will have two
                    // identical value args in an exceptional PhiExpr,
                    // but the more we can trim the better.
                    else
                        retry = true;
                }

                if(retry) {
                    if(champs.isEmpty())
                        break;
                    champ = champs.remove(0);
                    champU = champ.getUnit();
                }
            }
        }

        /*
        {
            List preds = phiExpr.getPreds();

            for(int i = 0; i < phiExpr.getArgCount(); i++){
                ValueUnitPair vup = phiExpr.getArgBox(i);
                Value value = vup.getValue();
                Unit unit = vup.getUnit();

                PhiExpr innerPhi = Shimple.getPhiExpr(unit);
                if(innerPhi == null)
                    continue;
                
                Value innerValue = Shimple.getLhsLocal(unit);
                if(!innerValue.equals(value))
                    continue;

                boolean canRemove = true;
                for(int j = 0; j < innerPhi.getArgCount(); j++){
                    Unit innerPred = innerPhi.getPred(j);
                    if(!preds.contains(innerPred)){
                        canRemove = false;
                        break;
                    }
                }

                if(canRemove)
                    phiExpr.removeArg(vup);
            }
        }
        */
    }
    
    protected Map<Unit, Block> unitToBlock;

    /**
     * Returns true if champ dominates challenger.  Note that false
     * doesn't necessarily mean that challenger dominates champ.
     **/
    public boolean dominates(Unit champ, Unit challenger)
    {
        if(champ == null || challenger == null)
            throw new RuntimeException("Assertion failed.");
        
        // self-domination
        if(champ.equals(challenger))
            return true;
        
        if(unitToBlock == null)
            unitToBlock = getUnitToBlockMap(cfg);

        Block champBlock = unitToBlock.get(champ);
        Block challengerBlock = unitToBlock.get(challenger);

        if(champBlock.equals(challengerBlock)){
            Iterator<Unit> unitsIt = champBlock.iterator();

            while(unitsIt.hasNext()){
                Unit unit = unitsIt.next();
                if(unit.equals(champ))
                    return true;
                if(unit.equals(challenger))
                    return false;
            }

            throw new RuntimeException("Assertion failed.");
        }

        DominatorNode<Block> champNode = dt.getDode(champBlock);
        DominatorNode<Block> challengerNode = dt.getDode(challengerBlock);

        // *** FIXME: System.out.println("champ: " + champNode);
        // System.out.println("chall: " + challengerNode);
        
        return(dt.isDominatorOf(champNode, challengerNode));
    }
    
    /**
     * Eliminate Phi nodes in block by naively replacing them with
     * shimple assignment statements in the control flow predecessors.
     * Returns true if new locals were added to the body during the
     * process, false otherwise.
     **/
    public boolean doEliminatePhiNodes()
    {
        // flag that indicates whether we created new locals during the
        // elimination process
        boolean addedNewLocals = false;
        
        // List of Phi nodes to be deleted.
        List<Unit> phiNodes = new ArrayList<Unit>();

        // This stores the assignment statements equivalent to each
        // (and every) Phi.  We use lists instead of a Map of
        // non-determinate order since we prefer to preserve the order
        // of the assignment statements, i.e. if a block has more than
        // one Phi expression, we prefer that the equivalent
        // assignments be placed in the same order as the Phi expressions.
        List<AssignStmt> equivStmts = new ArrayList<AssignStmt>();

        // Similarly, to preserve order, instead of directly storing
        // the pred, we store the pred box so that we follow the
        // pointers when SPatchingChain moves them.
        List<ValueUnitPair> predBoxes = new ArrayList<ValueUnitPair>();
        
        Chain<Unit> units = body.getUnits();
        for (Unit unit : units) {
            PhiExpr phi = Shimple.getPhiExpr(unit);

            if(phi == null)
                continue;

            Local lhsLocal = Shimple.getLhsLocal(unit);

            for(int i = 0; i < phi.getArgCount(); i++){
                Value phiValue = phi.getValue(i);
                AssignStmt convertedPhi =
                    Jimple.v().newAssignStmt(lhsLocal, phiValue);

                equivStmts.add(convertedPhi);
                predBoxes.add(phi.getArgBox(i));
            }

            phiNodes.add(unit);
        }

        if(equivStmts.size() != predBoxes.size())
            throw new RuntimeException("Assertion failed.");
        
        /* Avoid Concurrent Modification exceptions. */

        for(int i = 0; i < equivStmts.size(); i++){
            AssignStmt stmt = equivStmts.get(i);
            Unit pred = predBoxes.get(i).getUnit();

            if(pred == null)
                throw new RuntimeException("Assertion failed.");

            // if we need to insert the copy statement *before* an
            // instruction that happens to be *using* the Local being
            // defined, we need to do some extra work to make sure we
            // don't overwrite the old value of the local
            if(pred.branches()){
                boolean needPriming = false;
                Local lhsLocal = (Local) stmt.getLeftOp();
                Local savedLocal = Jimple.v().newLocal(lhsLocal.getName()+"_",
                                                       lhsLocal.getType());
                Iterator<ValueBox> useBoxesIt = pred.getUseBoxes().iterator();

                while(useBoxesIt.hasNext()){
                    ValueBox useBox = useBoxesIt.next();
                    if(lhsLocal.equals(useBox.getValue())){
                        needPriming = true;
                        addedNewLocals = true;
                        useBox.setValue(savedLocal);
                    }
                }

                if(needPriming){
                    body.getLocals().add(savedLocal);
                    AssignStmt copyStmt = Jimple.v().newAssignStmt(savedLocal, lhsLocal);
                    units.insertBefore(copyStmt, pred);
                }

                // this is all we really wanted to do!
                units.insertBefore(stmt, pred);
            }
            else
                units.insertAfter(stmt, pred);
        }
        
        Iterator<Unit> phiNodesIt = phiNodes.iterator();

        while(phiNodesIt.hasNext()){
            Unit removeMe = phiNodesIt.next();
            units.remove(removeMe);
            removeMe.clearUnitBoxes();
        }

        return addedNewLocals;
    }

    /**
     * Convenience function that maps units to blocks.  Should
     * probably be in BlockGraph.
     **/
    public Map<Unit, Block> getUnitToBlockMap(BlockGraph blocks)
    {
        Map<Unit, Block> unitToBlock = new HashMap<Unit, Block>();

        Iterator<Block> blocksIt = blocks.iterator();
        while(blocksIt.hasNext()){
            Block block = blocksIt.next();
            Iterator<Unit> unitsIt = block.iterator();

            while(unitsIt.hasNext()){
                Unit unit = unitsIt.next();
                unitToBlock.put(unit, block);
            }
        }

        return unitToBlock;
    }
}
