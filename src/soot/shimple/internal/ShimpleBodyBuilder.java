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
import soot.shimple.toolkits.graph.*;
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
public class ShimpleBodyBuilder
{
    protected ShimpleBody body;
    protected ShimpleFactory sf;
    protected DominatorTree dt;
    protected BlockGraph cfg;
    
    /**
     * A fixed list of all original Locals.
     **/
    protected List origLocals;

    public PhiNodeManager phi;
    public PiNodeManager pi;

    ShimpleOptions options;
    
    /**
     * Transforms the provided body to pure SSA form.
     **/
    public ShimpleBodyBuilder(ShimpleBody body)
    {
        this.body = body;
        sf = G.v().shimpleFactory;
        sf.setBody(body);
        sf.clearCache();
        phi = new PhiNodeManager(body);
        pi = new PiNodeManager(body, false);
        options = body.getOptions();
        makeUniqueLocalNames();
    }
    
    public void update()
    {
        cfg = sf.getBlockGraph();
        dt = sf.getDominatorTree();
        origLocals = new ArrayList(body.getLocals());
    }

    public void transform()
    {
        phi.insertTrivialPhiNodes();

        boolean change = false;
        if(options.extended()){
            change = pi.insertTrivialPiNodes();
        
            while(change){
                if(phi.insertTrivialPhiNodes()){
                    change = pi.insertTrivialPiNodes();
                }
                else{
                    break;
                }
            }
        }

        renameLocals();
        phi.trimExceptionalPhiNodes();
        makeUniqueLocalNames();
    }

    public void preElimOpt()
    {
        boolean optElim = options.node_elim_opt();

        // *** FIXME: 89e9a0470601091906j26489960j65290849dbe0481f@mail.gmail.com
        //if(optElim)
        //DeadAssignmentEliminator.v().transform(body);
    }

    public void postElimOpt()
    {
        boolean optElim = options.node_elim_opt();
        
        if(optElim){
            DeadAssignmentEliminator.v().transform(body);
            UnreachableCodeEliminator.v().transform(body);
            UnconditionalBranchFolder.v().transform(body);
            Aggregator.v().transform(body);
            UnusedLocalEliminator.v().transform(body);
        }
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
    public void eliminatePhiNodes()
    {
        if(phi.doEliminatePhiNodes())
            makeUniqueLocalNames();

    }

    public void eliminatePiNodes()
    {
        boolean optElim = options.node_elim_opt();
        pi.eliminatePiNodes(optElim);
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
     * Must be called after trivial nodes have been added.
     **/
    public void renameLocals()
    {
        update();
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
            throw new RuntimeException("Assertion failed:  Only one head expected.");
        
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

                    if(!Shimple.isPhiNode(unit))
                        useBoxes.addAll(unit.getUseBoxes());

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
                    if(!(unit instanceof DefinitionStmt))
                        continue;
                
                    DefinitionStmt defStmt = (DefinitionStmt) unit;
                    
                    Value lhsValue = defStmt.getLeftOp();
                    
                    // not something we're interested in
                    if(!origLocals.contains(lhsValue))
                        continue;

                    ValueBox lhsLocalBox = defStmt.getLeftOpBox();
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
            Iterator succsIt = cfg.getSuccsOf(block).iterator();

            while(succsIt.hasNext()){
                Block succ = (Block) succsIt.next();

                Iterator unitsIt = succ.iterator();

                while(unitsIt.hasNext()){
                    Unit unit = (Unit) unitsIt.next();

                    PhiExpr phiExpr = Shimple.getPhiExpr(unit);

                    if(phiExpr == null)
                        continue;

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
            DominatorNode node = dt.getDode(block);

            // now we recurse over children

            Iterator childrenIt = dt.getChildrenOf(node).iterator();

            while(childrenIt.hasNext()){
                DominatorNode childNode = (DominatorNode) childrenIt.next();

                renameLocalsSearch((Block) childNode.getGode());
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
     * Make sure the locals in the given body all have unique String
     * names.  Renaming is done if necessary.
     **/
    public void makeUniqueLocalNames()
    {
        if(options.standard_local_names()){
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
    public String makeUniqueLocalName(String dupName, Set localNames)
    {
        int counter = 1;
        String newName = dupName;

        while(localNames.contains(newName))
            newName = dupName + "_" + counter++;

        return newName;
    }
}
