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

package soot.shimple.toolkits.scalar;

import soot.*;
import soot.util.*;
import soot.options.*;
import soot.jimple.*;
import soot.shimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;

import java.util.*;

import soot.shimple.toolkits.scalar.SEvaluator.MetaConstant;
import soot.shimple.toolkits.scalar.SEvaluator.TopConstant;
import soot.shimple.toolkits.scalar.SEvaluator.BottomConstant;

/**
 * A powerful constant propagator and folder based on an algorithm
 * sketched by Cytron et al that takes conditional control flow into
 * account.  This optimization demonstrates some of the benefits of
 * SSA -- particularly the fact that Phi nodes represent natural merge
 * points in the control flow.
 *
 * @author Navindra Umanee
 * @see <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a>
 **/
public class SConstantPropagatorAndFolder extends BodyTransformer
{
    public SConstantPropagatorAndFolder(Singletons.Global g) {}

    public static SConstantPropagatorAndFolder v()
    { return G.v().soot_shimple_toolkits_scalar_SConstantPropagatorAndFolder(); }

    protected ShimpleBody sb;
    protected boolean debug;
    
    protected void internalTransform(Body b, String phaseName, Map<String,String> options)
    {
        if(!(b instanceof ShimpleBody))
            throw new RuntimeException("SConstantPropagatorAndFolder requires a ShimpleBody.");
        
        this.sb = (ShimpleBody) b;

        if(!sb.isSSA())
            throw new RuntimeException("ShimpleBody is not in proper SSA form as required by SConstantPropagatorAndFolder.  You may need to rebuild it or use ConstantPropagatorAndFolder instead.");

        boolean pruneCFG = PhaseOptions.getBoolean(options, "prune-cfg");
        debug = Options.v().debug();
        debug |= sb.getOptions().debug();
        
        if (Options.v().verbose())
            G.v().out.println("[" + sb.getMethod().getName() +
                              "] Propagating and folding constants (SSA)...");

        // *** FIXME: What happens when Shimple is built with another UnitGraph?
        SCPFAnalysis scpf = new SCPFAnalysis(new ExceptionalUnitGraph(sb));

        propagateResults(scpf.getResults());
        if(pruneCFG){
            removeStmts(scpf.getDeadStmts());
            replaceStmts(scpf.getStmtsToReplace());
        }
    }

    /**
     * Propagates constants to the definition and uses of the relevant
     * locals given a mapping.  Notice that we use the Shimple
     * implementation of LocalDefs and LocalUses.
     **/
    protected void propagateResults(Map<Local, Constant> localToConstant)
    {
        Chain<Unit> units = sb.getUnits();
        Collection<Local> locals = sb.getLocals();
        ShimpleLocalDefs localDefs = new ShimpleLocalDefs(sb);
        ShimpleLocalUses localUses = new ShimpleLocalUses(sb);
        
        Iterator<Local> localsIt = locals.iterator();
        while(localsIt.hasNext()){
            Local local = localsIt.next();
            Constant constant = localToConstant.get(local);
            
            if(constant instanceof MetaConstant)
                continue;

            // update definition
            {
                DefinitionStmt stmt =
                    (DefinitionStmt) localDefs.getDefsOf(local).get(0);

                ValueBox defSrcBox = stmt.getRightOpBox();
                Value defSrc = defSrcBox.getValue();
                
                if(defSrcBox.canContainValue(constant)){
                    defSrcBox.setValue(constant);

                    // remove dangling pointers
                    if(defSrc instanceof UnitBoxOwner)
                        ((UnitBoxOwner)defSrc).clearUnitBoxes();
                }
                else if(debug)
                    G.v().out.println("Warning: Couldn't propagate constant " + constant + " to box " + defSrcBox.getValue() + " in unit " + stmt);
            }
            
            // update uses
            {
                Iterator usesIt = localUses.getUsesOf(local).iterator();
                
                while(usesIt.hasNext()){
                    UnitValueBoxPair pair = (UnitValueBoxPair) usesIt.next();
                    ValueBox useBox = pair.getValueBox();

                    if(useBox.canContainValue(constant))
                       useBox.setValue(constant);
                    else if(debug)
                        G.v().out.println("Warning: Couldn't propagate constant " + constant + " to box " + useBox.getValue() + " in unit " + pair.getUnit());
                }
            }
        }
    }

    /**
     * Removes the given list of fall through IfStmts from the body.
     **/
    protected void removeStmts(List<IfStmt> deadStmts)
    {
        Chain units = sb.getUnits();
        Iterator<IfStmt> deadIt = deadStmts.iterator();
        while(deadIt.hasNext()){
            Unit dead = deadIt.next();
            units.remove(dead);
            dead.clearUnitBoxes();
        }
    }

    /**
     * Replaces conditional branches by unconditional branches as
     * given by the mapping.
     **/
    protected void replaceStmts(Map<Stmt, GotoStmt> stmtsToReplace)
    {
        Chain units = sb.getUnits();
        Iterator<Stmt> stmtsIt = stmtsToReplace.keySet().iterator();
        while(stmtsIt.hasNext()){
            // important not to call clearUnitBoxes() on booted since
            // replacement uses the same UnitBox
            Unit booted = stmtsIt.next();
            Unit replacement = stmtsToReplace.get(booted);
            units.swapWith(booted, replacement);
        }
    }
}

/**
 * The actual branching flow analysis implementation.  Briefly, a
 * sketch of the sketch from the Cytron et al paper:
 *
 * <p> Initially the algorithm assumes that each edge is unexecutable
 * (the entry nodes are reachable) and that each variable is constant
 * with an unknown value, Top.  Assumptions are corrected until they
 * stabilise.
 *
 * <p> For example, if <tt>q</tt> is found to be not a constant (Bottom)
 * in <tt>if(q == 0) goto label1</tt> then both edges leaving the the
 * statement are considered executable, if <tt>q</tt> is found to be a
 * constant then only one of the edges are executable.
 *
 * <p> Whenever a reachable definition statement such as "x = 3" is
 * found, the information is propagated to all uses of x (this works
 * due to the SSA property).
 *
 * <p> Perhaps the crucial point is that if a node such as <tt>x =
 * Phi(x_1, x_2)</tt> is ever found, information on <tt>x</tt> is
 * assumed as follows:
 *
 * <ul>
 *  <li>If <tt>x_1</tt> and <tt>x_2</tt> are the same assumed
 *  constant, <tt>x</tt> is assumed to be that constant.  If they are
 *  not the same constant, <tt>x</tt> is Bottom.</li>
 *
 *  <li>If either one is Top and the other is a constant, <tt>x</tt>
 *  is assumed to be the same as the known constant.</li>
 *
 *  <li>If either is Bottom, <tt>x</tt> is assumed to be Bottom.</li>
 * </ul>
 *
 * <p> The crucial point about the crucial point is that if
 * definitions of <tt>x_1</tt> or <tt>x_2</tt> are never reached, the
 * Phi node will still assume them to be Top and hence they will not
 * influence the decision as to whether <tt>x</tt> is a constant or not.
 **/
class SCPFAnalysis extends ForwardBranchedFlowAnalysis
{
    protected FlowSet emptySet;

    /**
     * A mapping of the locals to their current assumed constant value
     * (which may be Top or Bottom).
     **/
    protected Map<Local, Constant> localToConstant;

    /**
     * A map from conditional branches to their possible replacement 
     * unit, an unconditional branch.
     **/
    protected Map<Stmt, GotoStmt> stmtToReplacement;

    /**
     * A list of IfStmts that always fall through.
     **/
    protected List<IfStmt> deadStmts;


    /**
     * Returns the localToConstant map.
     **/
    public Map<Local, Constant> getResults()
    {
        return localToConstant;
    }

    /**
     * Returns the list of fall through IfStmts.
     **/
    public List<IfStmt> getDeadStmts()
    {
        return deadStmts;
    }

    /**
     * Returns a Map from conditional branches to the unconditional branches
     * that can replace them.
     **/
    public Map<Stmt, GotoStmt> getStmtsToReplace()
    {
        return stmtToReplacement;
    }
    
    public SCPFAnalysis(UnitGraph graph)
    {
        super(graph);
        emptySet = new ArraySparseSet();
        stmtToReplacement = new HashMap<Stmt, GotoStmt>();
        deadStmts = new ArrayList<IfStmt>();
        
        // initialise localToConstant map -- assume all scalars are
        // constant (Top)
        {
        	Collection<Local> locals = graph.getBody().getLocals();
            Iterator<Local> localsIt = locals.iterator();
            localToConstant = new HashMap<Local, Constant>(graph.size() * 2 + 1, 0.7f);

            while(localsIt.hasNext()){
                Local local = (Local) localsIt.next();
                localToConstant.put(local, TopConstant.v());
            }
        }

        doAnalysis();
    }

    // *** NOTE: this is here because ForwardBranchedFlowAnalysis does
    // *** not handle exceptional control flow properly in the
    // *** dataflow analysis.  this should be removed when
    // *** ForwardBranchedFlowAnalysis is fixed.
    protected boolean treatTrapHandlersAsEntries()
    {
        return true;
    }

    /**
     * If a node has empty IN sets we assume that it is not reachable.
     * Hence, we initialise the entry sets to be non-empty to indicate
     * that they are reachable.
     **/
    protected Object entryInitialFlow()
    {
        FlowSet entrySet = (FlowSet) emptySet.emptySet();
        entrySet.add(TopConstant.v());
        return entrySet;
    }

    /**
     * All other nodes are assumed to be unreachable by default.
     **/
    protected Object newInitialFlow()
    {
        return emptySet.emptySet();
    }

    /**
     * Since we are interested in control flow from all branches,
     * take the union.
     **/
    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet fin1 = (FlowSet) in1;
        FlowSet fin2 = (FlowSet) in2;
        FlowSet fout = (FlowSet) out;

        fin1.union(fin2, fout);
    }

    /**
     * Defer copy to FlowSet.
     **/
    protected void copy(Object source, Object dest)
    {
        FlowSet fource = (FlowSet) source;
        FlowSet fest = (FlowSet) dest;

        fource.copy(fest);
    }

    /**
     * If a node has an empty in set, it is considered unreachable.
     * Otherwise the node is examined and if any assumptions have to
     * be corrected, a Pair containing the corrected assumptions is
     * flowed to the reachable nodes.  If no assumptions have to be
     * corrected then no information other than the in set is
     * propagated to the reachable nodes.
     *
     * <p> Pair serves no other purpose than to keep the analysis
     * flowing for as long as needed.  The final results are
     * accumulated in the localToConstant map.
     **/
    protected void flowThrough(Object in, Unit s, List fallOut, List branchOuts)
    {
        FlowSet fin = ((FlowSet)in).clone();

        // not reachable
        if(fin.isEmpty())
            return;
        
        // If s is a definition, check if any assumptions have to be
        // corrected.
        Pair pair = processDefinitionStmt(s);

        if(pair != null)
            fin.add(pair);
        
        // normal, non-branching statement
        if(!s.branches() && s.fallsThrough()){
            Iterator fallOutIt = fallOut.iterator();
            while(fallOutIt.hasNext()){
                FlowSet fallSet = (FlowSet) fallOutIt.next();
                fallSet.union(fin);
            }

            return;
        }

        /* determine which nodes are reachable. */
        
        boolean conservative = true;
        boolean fall = false;
        boolean branch = false;
        FlowSet oneBranch = null;
        
        IFSTMT:
        {
        if(s instanceof IfStmt){
            IfStmt ifStmt = (IfStmt) s;
            Value cond = ifStmt.getCondition();
            Constant constant =
                SEvaluator.getFuzzyConstantValueOf(cond, localToConstant);
            
            // flow both ways
            if(constant instanceof BottomConstant){
                deadStmts.remove(ifStmt);
                stmtToReplacement.remove(ifStmt);
                break IFSTMT;
            }

            // no flow
            if(constant instanceof TopConstant)
                return;

            /* determine whether to flow through or branch */
            
            conservative = false;

            Constant trueC = IntConstant.v(1);
            Constant falseC = IntConstant.v(0);

            if(constant.equals(trueC)){
                branch = true;
                GotoStmt gotoStmt =
                    Jimple.v().newGotoStmt(ifStmt.getTargetBox());
                stmtToReplacement.put(ifStmt, gotoStmt);
            }

            if(constant.equals(falseC)){
                fall = true;
                deadStmts.add(ifStmt);
            }
        }
        } // end IFSTMT

        TABLESWITCHSTMT:
        {
        if(s instanceof TableSwitchStmt){
            TableSwitchStmt table = (TableSwitchStmt) s;
            Value keyV = table.getKey();
            Constant keyC =
                SEvaluator.getFuzzyConstantValueOf(keyV, localToConstant);

            // flow all branches
            if(keyC instanceof BottomConstant){
                stmtToReplacement.remove(table);
                break TABLESWITCHSTMT;
            }

            // no flow
            if(keyC instanceof TopConstant)
                return;

            // flow all branches
            if(!(keyC instanceof IntConstant))
                break TABLESWITCHSTMT;

            /* find the one branch we need to flow to */

            conservative = false;
            
            int key = ((IntConstant)keyC).value;
            int low = table.getLowIndex();
            int high = table.getHighIndex();
            int index = key - low;

            UnitBox branchBox = null;
            if(index < 0 || index > high)
                branchBox = table.getDefaultTargetBox();
            else
                branchBox = table.getTargetBox(index);

            GotoStmt gotoStmt = Jimple.v().newGotoStmt(branchBox);
            stmtToReplacement.put(table, gotoStmt);
            
            List unitBoxes = table.getUnitBoxes();
            int setIndex = unitBoxes.indexOf(branchBox);
            oneBranch = (FlowSet) branchOuts.get(setIndex);
        }
        } // end TABLESWITCHSTMT

        LOOKUPSWITCHSTMT:
        {
        if(s instanceof LookupSwitchStmt){
            LookupSwitchStmt lookup = (LookupSwitchStmt) s;
            Value keyV = lookup.getKey();
            Constant keyC =
                SEvaluator.getFuzzyConstantValueOf(keyV, localToConstant);

            // flow all branches
            if(keyC instanceof BottomConstant){
                stmtToReplacement.remove(lookup);
                break LOOKUPSWITCHSTMT;
            }

            // no flow
            if(keyC instanceof TopConstant)
                return;

            // flow all branches
            if(!(keyC instanceof IntConstant))
                break LOOKUPSWITCHSTMT;

            /* find the one branch we need to flow to */

            conservative = false;
            
            int index = lookup.getLookupValues().indexOf(keyC);

            UnitBox branchBox = null;
            if(index == -1)
                branchBox = lookup.getDefaultTargetBox();
            else
                branchBox = lookup.getTargetBox(index);

            GotoStmt gotoStmt = Jimple.v().newGotoStmt(branchBox);
            stmtToReplacement.put(lookup, gotoStmt);
            
            List unitBoxes = lookup.getUnitBoxes();
            int setIndex = unitBoxes.indexOf(branchBox);
            oneBranch = (FlowSet) branchOuts.get(setIndex);
        }
        } // end LOOKUPSWITCHSTMT
        
        // conservative control flow estimates
        if(conservative){
            fall = s.fallsThrough();
            branch = s.branches();
        }

        if(fall){
            Iterator fallOutIt = fallOut.iterator();
            while(fallOutIt.hasNext()){
                FlowSet fallSet = (FlowSet) fallOutIt.next();
                fallSet.union(fin);
            }
        }
        
        if(branch){
            Iterator branchOutsIt = branchOuts.iterator();
            while(branchOutsIt.hasNext()){
                FlowSet branchSet = (FlowSet) branchOutsIt.next();
                branchSet.union(fin);
            }
        }

        if(oneBranch != null){
            oneBranch.union(fin);
        }
    }

    /**
     * Returns (Unit, Constant) pair if an assumption has  changed 
     * due to the fact that u is reachable.  Else returns null.
     **/
    protected Pair processDefinitionStmt(Unit u)
    {
        if(!(u instanceof DefinitionStmt))
            return null;

        DefinitionStmt dStmt = (DefinitionStmt) u;
        
        Local local;

        {
            Value value = dStmt.getLeftOp();
            if(!(value instanceof Local))
                return null;
            local = (Local) value;
        }

        /* update assumptions */

        Value rightOp = dStmt.getRightOp();
        Constant constant =
            SEvaluator.getFuzzyConstantValueOf(rightOp, localToConstant);
        
        if(!merge(local, constant))
            return null;

        return new Pair(u, localToConstant.get(local));
    }
    
    /**
     * Verifies if the given assumption "constant" changes the
     * previous assumption about "local" and merges the information
     * into the localToConstant map.  Returns true if something
     * changed.
     **/
    protected boolean merge(Local local, Constant constant)
    {
        Constant current = localToConstant.get(local);

        if(current instanceof BottomConstant) 
            return false;

        if(current instanceof TopConstant){
            localToConstant.put(local, constant);
            return true;
        }

        if(current.equals(constant))
            return false;

        // not equal
        localToConstant.put(local, BottomConstant.v());
        return true;
    }
}
