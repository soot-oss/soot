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
import soot.shimple.internal.analysis.*;
import soot.shimple.toolkits.scalar.*;
import soot.options.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.jimple.toolkits.base.*;
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
public class ShimpleBodyBuilder
{
    protected ShimpleBody body;
    protected DominatorTree dt;
    protected BlockGraph cfg;

    /**
     * A fixed list of all original Locals.
     **/
    protected List origLocals;

    /**
     * An analysis class that allows us to verify that a variable is
     * guaranteed to be defined at program point P.  Used as an
     * accessory to tweaking our Phi node insertion algorithm.
     **/
    protected GuaranteedDefs gd;

    /**
     * Transforms the provided body to pure SSA form.
     **/
    public ShimpleBodyBuilder(ShimpleBody body, boolean hasPhiNodes)
    {
        this.body = body;

        // our SSA building algorithm currently assumes there are no
        // foreign Phi nodes in the given body, therefore, any such
	// Phi nodes must be eliminated first. The results may not 
	// be as pretty as expected and it's possible that as part of 
	// the process of eliminating and subsequent recomputation of 
	// SSA form, minimality (to the original source) is not 
	// maintained.
        if(hasPhiNodes)
            eliminatePhiNodes(body);
        
        cfg = new CompleteBlockGraph(body);
        dt = new DominatorTree(cfg, true);
        gd = new GuaranteedDefs(new CompleteUnitGraph(body));
        origLocals = new ArrayList(body.getLocals());
        
        /* Carry out the transformations. */
        
        insertTrivialPhiNodes();
        renameLocals();
        trimExceptionalPhiNodes();
        makeUniqueLocalNames(body);
    }

    /**
     * Phi node Insertion Algorithm from Cytron et al 91, P24-5,
     * implemented in various bits and pieces by the next functions.
     *
     * <p> For each definition of variable V, find the iterated
     * dominance frontier.  Each block in the iterated dominance
     * frontier is prepended with a trivial Phi node.
     *
     * <p> We found out the hard way that this isn't the ideal
     * solution for Jimple because a lot of redundant Phi nodes
     * get inserted probably due to the fact that the algorithm
     * assumes all variables have an initial definition on entry.
     *
     * <p> While this assumption does not produce incorrect results, it
     * produces hopelessly complicated and ineffectual code.
     *
     * <p> Our quick solution was to ensure that a variable was
     * defined along all paths to the block where we were considering
     * insertion of a Phi node.  If the variable was not defined
     * along at least one path (isLocalDefinedOnEntry()), then
     * certainly a Phi node was superfluous and meaningless.  Our
     * GuaranteedDefs flow analysis provided us with the necessary
     * information.
     *
     * <p> Better and more efficient alternatives suggest themselves.
     * We later found this formulation from IBM's Jikes RVM:
     *
     * <p><i> Special Java case: if node N dominates all defs of V,
     * then N does not need a Phi node for V. </i>
     **/
    public void insertTrivialPhiNodes()
    {
        Map localsToDefPoints = new HashMap();

        // compute localsToDefPoints
        // ** can we use LocalDefs instead?  don't think so.
        {
            Iterator localsIt = origLocals.iterator();

            while(localsIt.hasNext()){
                Local local = (Local)localsIt.next();

                // all blocks containing definitions of our Local
                List blockList = new ArrayList();

                Iterator blocksIt = cfg.iterator();

                while(blocksIt.hasNext()){
                    Block block = (Block)blocksIt.next();
                    Iterator defBoxesIt = getDefBoxesFromBlock(block).iterator();
                    while(defBoxesIt.hasNext()){
                        Value def = ((ValueBox)defBoxesIt.next()).getValue();

                        if(def.equals(local)){
                            blockList.add(block);
                            break;
                        }
                    }
                }
                
                localsToDefPoints.put(local, blockList);
            }
        }

        /* Routine initialisations. */
        
        int[] workFlags = new int[cfg.size()];
        int[] hasAlreadyFlags = new int[cfg.size()];
        
        int iterCount = 0;
        Stack workList = new Stack();

        /* Main Cytron algorithm. */
        
        {
            Iterator localsIt = localsToDefPoints.keySet().iterator();

            while(localsIt.hasNext()){
                Local local = (Local) localsIt.next();

                iterCount++;

                // initialise worklist
                {
                    Iterator defNodesIt = ((List) localsToDefPoints.get(local)).iterator();
                    while(defNodesIt.hasNext()){
                        Block block = (Block) defNodesIt.next();
                        workFlags[block.getIndexInMethod()] = iterCount;
                        workList.push(block);
                    }
                }

                while(!workList.empty()){
                    Block block = (Block) workList.pop();
                    DominatorNode node = dt.fetchNode(block);
                    Iterator frontierNodes = node.getDominanceFrontier().iterator();

                    while(frontierNodes.hasNext()){
                        Block frontierBlock = ((DominatorNode) frontierNodes.next()).getBlock();
                        int fBIndex = frontierBlock.getIndexInMethod();
                        
                        if(hasAlreadyFlags[fBIndex] < iterCount){
                            // Make sure we don't add useless Phi nodes
                            if(isLocalDefinedOnEntry(local, frontierBlock))
                                prependTrivialPhiNode(local, frontierBlock);

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
    }

    /**
     * Inserts a trivial Phi node with the appropriate number of
     * arguments.
     **/
    public void prependTrivialPhiNode(Local local, Block frontierBlock)
    {
        List preds = frontierBlock.getPreds();
        Unit trivialPhi = Jimple.v().newAssignStmt(local, Shimple.v().newPhiExpr(local, preds));
        Unit blockHead = frontierBlock.getHead();

        // is it a catch block?
        if(blockHead instanceof IdentityUnit)
            frontierBlock.insertAfter(trivialPhi, frontierBlock.getHead());
        else
            frontierBlock.insertBefore(trivialPhi, frontierBlock.getHead());
    }

    /**
     * Function that allows us to weed out special cases where
     * we do not require Phi nodes.
     *
     * <p> Temporary implementation, with much room for improvement.
     **/
    protected boolean isLocalDefinedOnEntry(Local local, Block block)
    {
        Iterator unitsIt = block.iterator();

        if(!unitsIt.hasNext())
            throw new RuntimeException("Empty block in CFG?");

        Unit unit = (Unit) unitsIt.next();
        
        // this will return null if the head unit is an inserted Phi statement
        List definedLocals = gd.getGuaranteedDefs(unit);

        // this should not fail
        while(definedLocals == null){
            if(!unitsIt.hasNext())
                throw new RuntimeException("Empty block in CFG?");
            unit = (Unit) unitsIt.next();
            definedLocals = gd.getGuaranteedDefs(unit);
        }
        
        return definedLocals.contains(local);
    }
    
    /**
     * Maps new name Strings to Locals.
     **/
    protected Map newLocals;

    /**
     * Maps renamed Locals to original Locals.
     **/
    protected Map newLocalsToOldLocal;

    protected int[] assignmentCounters;
    protected Stack[] namingStacks;
    
    /**
     * Variable Renaming Algorithm from Cytron et al 91, P26-8,
     * implemented in various bits and pieces by the next functions.
     * Must be called after trivial Phi nodes have been added.
     *
     * <pre>
     *  call search(entry)
     *
     *  search(X):
     *  for each statement A in X do
     *     if A is an ordinary assignment
     *       for each variable V used do
     *            replace use of V by use of V_i where i = Top(S(V))
     *       done
     *     fi
     *    for each V in LHS(A) do
     *       i <- C(V)
     *       replace V by new V_i in LHS(A)
     *       push i onto S(V)
     *       C(V) <- i+1
     *    done
     *  done (end of first loop)
     *  for each Y in succ(X) do
     *      j <- whichPred(Y, X)
     *      for each Phi-function F in Y do
     *       replace the j-th operand V in RHS(F) by V_i with i = TOP(S(V))
     *     done
     *  done (end of second loop)
     *  for each Y in Children(X) do
     *    call search(Y)
     *  done (end of third loop)
     *  for each assignment A in X do
     *     for each V in oldLHS(A) do
     *       pop(S(V))
     *    done
     *  done (end of fourth loop)
     *  end
     * </pre>
     **/
    public void renameLocals()
    {
        newLocals = new HashMap();
        newLocalsToOldLocal = new HashMap();

        assignmentCounters = new int[origLocals.size()];
        namingStacks = new Stack[origLocals.size()];

        for(int i = 0; i < namingStacks.length; i++)
            namingStacks[i] = new Stack();

        List heads = cfg.getHeads();

        if(heads.size() == 0)
            return;

        if(heads.size() != 1)
            throw new RuntimeException("This version of Shimple was built against versions of CompleteBlockGraph and CompleteUnitGraph where only one head is possible.  If this has changed, then Shimple requires an update.");
        
        Block entry = (Block) heads.get(0);
        renameLocalsSearch(entry);
    }

    /**
     * Driven by renameLocals().
     **/
    public void renameLocalsSearch(Block block)
    {
        // accumulated in Step 1 to be re-processed in Step 4
        List lhsLocals = new ArrayList();
        
        // Step 1 of 4 -- Rename block's uses (ordinary) and defs
        {
            // accumulated and re-processed in a later loop
            Iterator unitsIt = block.iterator();

            while(unitsIt.hasNext()){
                Unit unit = (Unit) unitsIt.next();

                // Step 1/2 of 1
                {
                    List useBoxes = new ArrayList();

                    // process all Ordinary Uses
                    if(unit instanceof AssignStmt){
                        Value rValue = ((AssignStmt) unit).getRightOp();
                        
                        if(!(rValue instanceof PhiExpr))
                            useBoxes.addAll(unit.getUseBoxes());
                    }
                    else{
                        useBoxes.addAll(unit.getUseBoxes());
                    }

                    Iterator useBoxesIt = useBoxes.iterator();
                
                    while(useBoxesIt.hasNext()){
                        ValueBox useBox = (ValueBox) useBoxesIt.next();
                        Value use = useBox.getValue();

                        int localIndex = indexOfLocal(use);

                        // not one of our locals
                        if(localIndex == -1)
                            continue;

                        Local localUse = (Local) use;

                        if(namingStacks[localIndex].empty())
                            continue;

                        Integer subscript = (Integer) namingStacks[localIndex].peek();

                        Local renamedLocal = fetchNewLocal(localUse, subscript);
                        useBox.setValue(renamedLocal);
                    }
                }

                // Step 1 of 1
                {
                    if(!(unit instanceof AssignStmt))
                        continue;
                
                    AssignStmt assignStmt = (AssignStmt) unit;
                    
                    Value lhsValue = assignStmt.getLeftOp();
                    
                    // not something we're interested in
                    if(!origLocals.contains(lhsValue))
                        continue;

                    ValueBox lhsLocalBox = assignStmt.getLeftOpBox();
                    Local lhsLocal = (Local) lhsValue;

                    // re-processed in Step 4
                    lhsLocals.add(lhsLocal);

                    int localIndex = indexOfLocal(lhsLocal);
                    if(localIndex == -1)
                        throw new RuntimeException("Assertion failed.");
                
                    Integer subscript = new Integer(assignmentCounters[localIndex]);

                    Local newLhsLocal = fetchNewLocal(lhsLocal, subscript);
                    lhsLocalBox.setValue(newLhsLocal);

                    namingStacks[localIndex].push(subscript);
                    assignmentCounters[localIndex]++;                    
                    
                }
            }
        }

        // Step 2 of 4 -- Rename Phi node uses in Successors
        {
            Iterator succsIt = block.getSuccs().iterator();

            while(succsIt.hasNext()){
                Block succ = (Block) succsIt.next();

                Iterator unitsIt = succ.iterator();

                while(unitsIt.hasNext()){
                    Unit unit = (Unit) unitsIt.next();

                    if(!(unit instanceof AssignStmt))
                        continue;

                    AssignStmt assignStmt = (AssignStmt) unit;

                    Value rhsRValue = assignStmt.getRightOp();

                    // only interested in Phi expressions
                    if(!(rhsRValue instanceof PhiExpr))
                        continue;

                    PhiExpr phiExpr = (PhiExpr) rhsRValue;

                    // simulate whichPred
                    int argIndex = phiExpr.getArgIndex(block);
                    if(argIndex == -1)
                        throw new RuntimeException("Assertion failed.");
                        
                    ValueBox phiArgBox = phiExpr.getArgBox(argIndex);

                    Local phiArg = (Local) phiArgBox.getValue();
                    
                    int localIndex = indexOfLocal(phiArg);
                    if(localIndex == -1)
                        throw new RuntimeException("Assertion failed.");
                    
                    if(namingStacks[localIndex].empty())
                        continue;

                    Integer subscript = (Integer) namingStacks[localIndex].peek();
                    
                    Local newPhiArg = fetchNewLocal(phiArg, subscript);
                    phiArgBox.setValue(newPhiArg);
                }
            }
        }

        // Step 3 of 4 -- Recurse over children.
        {
            DominatorNode node = dt.fetchNode(block);

            // now we recurse over children

            Iterator childrenIt = node.getChildren().iterator();

            while(childrenIt.hasNext()){
                DominatorNode childNode = (DominatorNode) childrenIt.next();

                renameLocalsSearch(childNode.getBlock());
            }
        }

        // Step 4 of 4 -- Tricky name stack updates.
        {
            Iterator lhsLocalsIt = lhsLocals.iterator();

            while(lhsLocalsIt.hasNext()){
                Local lhsLocal = (Local) lhsLocalsIt.next();

                int lhsLocalIndex = indexOfLocal(lhsLocal);
                if(lhsLocalIndex == -1)
                    throw new RuntimeException("Assertion failed.");
                
                namingStacks[lhsLocalIndex].pop();
            }
        }

        /* And we're done.  The renaming process is complete. */
    }

    /**
     * Clever convenience function to fetch or create new Local's
     * given a Local and the desired subscript.
     **/
    protected Local fetchNewLocal(Local local, Integer subscript)
    {
        Local oldLocal = local;
        
        if(!origLocals.contains(local))
            oldLocal = (Local) newLocalsToOldLocal.get(local);
        
        if(subscript.intValue() == 0)
            return oldLocal;

        // If the name already exists, makeUniqueLocalNames() will
        // take care of it.
        String name = oldLocal.getName() + "_" + subscript;

        Local newLocal = (Local) newLocals.get(name);

        if(newLocal == null){
            newLocal = new JimpleLocal(name, oldLocal.getType());
            newLocals.put(name, newLocal);
            newLocalsToOldLocal.put(newLocal, oldLocal);

            // add proper Local declation
            body.getLocals().add(newLocal);
        }

        return newLocal;
    }

    /**
     * Convenient function that maps new Locals to the originating
     * Local, and finds the appropriate array index into the naming
     * structures.
     **/
    protected int indexOfLocal(Value local)
    {
        int localIndex = origLocals.indexOf(local);

        if(localIndex == -1){
            // might be null
            Local oldLocal = (Local) newLocalsToOldLocal.get(local);

            localIndex = origLocals.indexOf(oldLocal);
        }
        
        return localIndex;
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
        Set handlerUnits = new HashSet();
        Iterator trapsIt = body.getTraps().iterator();

        while(trapsIt.hasNext()) {
            Trap trap = (Trap) trapsIt.next();
            handlerUnits.add(trap.getHandlerUnit());
        }

        Iterator blocksIt = cfg.iterator();
        while(blocksIt.hasNext()){
            Block block = (Block) blocksIt.next();

            // trim relevant Phi expressions
            if(handlerUnits.contains(block.getHead())){
                Iterator unitsIt = block.iterator();
                while(unitsIt.hasNext()){
                    Unit unit = (Unit) unitsIt.next();
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

        MultiMap valueToPairs = new HashMultiMap();

        Iterator argsIt = phiExpr.getArgs().iterator();
        while(argsIt.hasNext()){
            ValueUnitPair argPair = (ValueUnitPair) argsIt.next();
            Value value = argPair.getValue();
            valueToPairs.put(value, argPair);
        }

        /* Consider each value and see if we can find the dominating
           UnitBoxes.  Once we have found all the dominating
           UnitBoxes, the rest of the redundant arguments can be
           trimmed.  */
        
        Iterator valuesIt = valueToPairs.keySet().iterator();
        while(valuesIt.hasNext()){
            Value value = (Value) valuesIt.next();

            // although the champs list constantly shrinks, guaranteeing
            // termination, the challengers list never does.  This could
            // be optimised.
            Set pairsSet = valueToPairs.get(value);
            List champs = new ArrayList(pairsSet);
            List challengers = new ArrayList(pairsSet);
            
            // champ is the currently assumed dominator
            ValueUnitPair champ = (ValueUnitPair) champs.remove(0);
            Unit champU = champ.getUnit();

            // hopefully everything will work out the first time, but
            // if not, we will have to try a new champion just in case
            // there is more that can be trimmed.
            boolean retry = true;
            while(retry){
                retry = false;

                // go through each challenger and see if we dominate them
                // if not, the challenger becomes the new champ
                for(int i = 0; i < challengers.size(); i++){
                    ValueUnitPair challenger = (ValueUnitPair)challengers.get(i);

                    if(challenger.equals(champ))
                        continue;
                    Unit challengerU = challenger.getUnit();
                
                    // kill the challenger
                    if(dominates(champU, challengerU))
                        phiExpr.removeArg(challenger);

                    // we die, find a new champ
                    else if(dominates(challengerU, champU)){
                        phiExpr.removeArg(champ);
                        champ = challenger;
                        champU = champ.getUnit();
                        champs.remove(champ);
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
                    if(champs.size() == 0)
                        break;
                    champ = (ValueUnitPair)champs.remove(0);
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
    
    protected Map unitToBlock;

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

        Block champBlock = (Block) unitToBlock.get(champ);
        Block challengerBlock = (Block) unitToBlock.get(challenger);

        if(champBlock.equals(challengerBlock)){
            Iterator unitsIt = champBlock.iterator();

            while(unitsIt.hasNext()){
                Unit unit = (Unit) unitsIt.next();
                if(unit.equals(champ))
                    return true;
                if(unit.equals(challenger))
                    return false;
            }

            throw new RuntimeException("Assertion failed.");
        }

        DominatorNode champNode = dt.fetchNode(champBlock);
        DominatorNode challengerNode = dt.fetchNode(challengerBlock);

        return(champNode.dominates(challengerNode));
    }
    
    /**
     * Remove Phi nodes from current body, high probablity this
     * destroys SSA form.
     *
     * <p> Dead code elimination + register aggregation are performed
     * as recommended by Cytron.  The Aggregator looks like it could
     * use some improvements.
     *
     * @see soot.options.ShimpleOptions
     **/
    public static void eliminatePhiNodes(ShimpleBody body)
    {
        ShimpleOptions options = body.getOptions();
        int phiElimOpt = options.phi_elim_opt();
        
        // off by default
        if((phiElimOpt == options.phi_elim_opt_pre) ||
           (phiElimOpt == options.phi_elim_opt_pre_and_post))
        {
            DeadAssignmentEliminator.v().transform(body);
            Aggregator.v().transform(body);
        }
        
        // offloaded in a separate function for historical reasons
        if(doEliminatePhiNodes(body))
            makeUniqueLocalNames(body);
        
        // on by default
        if((phiElimOpt == options.phi_elim_opt_post) ||
           (phiElimOpt == options.phi_elim_opt_pre_and_post))
        {
            DeadAssignmentEliminator.v().transform(body);
            Aggregator.v().transform(body);
        }
    }
    
    /**
     * Eliminate Phi nodes in block by naively replacing them with
     * shimple assignment statements in the control flow predecessors.
     * Returns true if new locals were added to the body during the
     * process, false otherwise.
     **/
    public static boolean doEliminatePhiNodes(ShimpleBody body)
    {
        // flag that indicates whether we created new locals during the
        // elimination process
        boolean addedNewLocals = false;
        
        // List of Phi nodes to be deleted.
        List phiNodes = new ArrayList();

        // This stores the assignment statements equivalent to each
        // (and every) Phi.  We use lists instead of a Map of
        // non-determinate order since we prefer to preserve the order
        // of the assignment statements, i.e. if a block has more than
        // one Phi expression, we prefer that the equivalent
        // assignments be placed in the same order as the Phi expressions.
        List equivStmts = new ArrayList();

        // Similarly, to preserve order, instead of directly storing
        // the pred, we store the pred box so that we follow the
        // pointers when SPatchingChain moves them.
        List predBoxes = new ArrayList();
        
        Chain units = body.getUnits();
        Iterator unitsIt = units.iterator();

        while(unitsIt.hasNext()){
            Unit unit = (Unit) unitsIt.next();
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
            AssignStmt stmt = (AssignStmt) equivStmts.get(i);
            Unit pred = ((UnitBox) predBoxes.get(i)).getUnit();

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
                Iterator useBoxesIt = pred.getUseBoxes().iterator();

                while(useBoxesIt.hasNext()){
                    ValueBox useBox = (ValueBox) useBoxesIt.next();
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
        
        Iterator phiNodesIt = phiNodes.iterator();

        while(phiNodesIt.hasNext()){
            Unit removeMe = (Unit) phiNodesIt.next();
            units.remove(removeMe);
            removeMe.clearUnitBoxes();
        }

        return addedNewLocals;
    }

    /**
     * Make sure the locals in the given body all have unique String
     * names.  Renaming is done if necessary.
     **/
    public static void makeUniqueLocalNames(ShimpleBody body)
    {
        if(body.getOptions().standard_local_names()){
            LocalNameStandardizer.v().transform(body);
            return;
        }

        Set localNames = new HashSet();
        Iterator localsIt = body.getLocals().iterator();

        while(localsIt.hasNext()){
            Local local = (Local) localsIt.next();
            String localName = local.getName();
            
            if(localNames.contains(localName)){
                String uniqueName = makeUniqueLocalName(localName, localNames);
                local.setName(uniqueName);
                localNames.add(uniqueName);
            }
            else
                localNames.add(localName);
        }
    }

    /**
     * Given a set of Strings, return a new name for dupName that is
     * not currently in the set.
     **/
    public static String makeUniqueLocalName(String dupName, Set localNames)
    {
        int counter = 1;
        String newName = dupName;

        while(localNames.contains(newName))
            newName = dupName + "_" + counter++;

        return newName;
    }
    
    /**
     * Convenience function that really ought to be implemented in
     * soot.toolkits.graph.Block.
     **/
    public static List getDefBoxesFromBlock(Block block)
    {
        Iterator unitsIt = block.iterator();
        
        List defBoxesList = new ArrayList();
    
        while(unitsIt.hasNext())
            defBoxesList.addAll(((Unit)unitsIt.next()).getDefBoxes());
        
        return defBoxesList;
    }

    /**
     * Convenience function that really ought to be implemented in
     * soot.toolkits.graph.Block
     **/
    public static List getUseBoxesFromBlock(Block block)
    {
        Iterator unitsIt = block.iterator();
        
        List useBoxesList = new ArrayList();
    
        while(unitsIt.hasNext())
            useBoxesList.addAll(((Unit)unitsIt.next()).getUseBoxes());
        
        return useBoxesList;
    }

    /**
     * Convenience function that maps units to blocks.  Should
     * probably be in BlockGraph.
     **/
    public static Map getUnitToBlockMap(BlockGraph blocks)
    {
        Map unitToBlock = new HashMap();

        Iterator blocksIt = blocks.iterator();
        while(blocksIt.hasNext()){
            Block block = (Block) blocksIt.next();
            Iterator unitsIt = block.iterator();

            while(unitsIt.hasNext()){
                Unit unit = (Unit) unitsIt.next();
                unitToBlock.put(unit, block);
            }
        }

        return unitToBlock;
    }
}
