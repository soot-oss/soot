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

public class SConstantPropagatorAndFolder extends BodyTransformer
{
    public SConstantPropagatorAndFolder(Singletons.Global g) {}

    public static SConstantPropagatorAndFolder v()
    { return G.v().SConstantPropagatorAndFolder(); }

    protected ShimpleBody sb;
    protected boolean debug;
    
    protected void internalTransform(Body b, String phaseName, Map options)
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

        SCPFAnalysis scpf = new SCPFAnalysis(new CompleteUnitGraph(sb));
        propagateResults(scpf.getResults());

        if(pruneCFG){
            removeStmts(scpf.getDeadStmts());
            replaceStmts(scpf.getStmtsToReplace());
        }
    }

    protected void propagateResults(Map localToConstant)
    {
        Chain units = sb.getUnits();
        Chain locals = sb.getLocals();
        ShimpleLocalDefs localDefs = new ShimpleLocalDefs(sb);
        ShimpleLocalUses localUses = new ShimpleLocalUses(sb);
        
        Iterator localsIt = locals.iterator();
        while(localsIt.hasNext()){
            Local local = (Local) localsIt.next();
            Constant constant = (Constant) localToConstant.get(local);
            
            if(constant instanceof MetaConstant)
                continue;

            // update definition
            {
                DefinitionStmt stmt =
                    (DefinitionStmt) localDefs.getDefsOf(local).get(0);

                ValueBox defSrcBox = stmt.getRightOpBox();
                Value defSrc = defSrcBox.getValue();
                
                if(defSrcBox.canContainValue(constant)){
                    if(Shimple.isPhiNode(stmt))
                        stmt.clearUnitBoxes();

                    defSrcBox.setValue(constant);
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

    protected void removeStmts(List deadStmts)
    {
        Chain units = sb.getUnits();
        Iterator deadIt = deadStmts.iterator();
        while(deadIt.hasNext()){
            Unit dead = (Unit) deadIt.next();
            units.remove(dead);
            dead.clearUnitBoxes();
        }
    }

    protected void replaceStmts(Map stmtsToReplace)
    {
        Chain units = sb.getUnits();
        Iterator stmtsIt = stmtsToReplace.keySet().iterator();
        while(stmtsIt.hasNext()){
            // important not to call clearUnitBoxes() on booted since
            // replacement uses the same UnitBox
            Unit booted = (Unit) stmtsIt.next();
            Unit replacement = (Unit) stmtsToReplace.get(booted);
            units.swapWith(booted, replacement);
        }
    }
}

class SCPFAnalysis extends ForwardBranchedFlowAnalysis
{
    FlowSet emptySet;
    Map localToConstant;
    Map stmtToReplacement;
    List deadStmts;
    
    public Map getResults()
    {
        return localToConstant;
    }

    public List getDeadStmts()
    {
        return deadStmts;
    }

    public Map getStmtsToReplace()
    {
        return stmtToReplacement;
    }
    
    public SCPFAnalysis(UnitGraph graph)
    {
        super(graph);
        emptySet = new ArraySparseSet();
        stmtToReplacement = new HashMap();
        deadStmts = new ArrayList();
        
        // initialise localToConstant map -- assume all scalars are
        // constant (Top)
        {
            Chain locals = graph.getBody().getLocals();
            Iterator localsIt = locals.iterator();
            localToConstant = new HashMap(graph.size() * 2 + 1, 0.7f);

            while(localsIt.hasNext()){
                Local local = (Local) localsIt.next();
                localToConstant.put(local, TopConstant.v());
            }
        }

        doAnalysis();
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

    protected Object newInitialFlow()
    {
        return emptySet.emptySet();
    }

    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet fin1 = (FlowSet) in1;
        FlowSet fin2 = (FlowSet) in2;
        FlowSet fout = (FlowSet) out;

        fin1.union(fin2, fout);
    }

    protected void copy(Object source, Object dest)
    {
        FlowSet fource = (FlowSet) source;
        FlowSet fest = (FlowSet) dest;

        fource.copy(fest);
    }

    protected void flowThrough(Object in, Unit s, List fallOut, List branchOuts)
    {
        FlowSet fin = (FlowSet) ((FlowSet)in).clone();

        // not reachable
        if(fin.isEmpty())
            return;
        
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
            if(constant instanceof BottomConstant)
                break IFSTMT;

            // no flow
            if(constant instanceof TopConstant)
                return;

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
            if(keyC instanceof BottomConstant)
                break TABLESWITCHSTMT;

            // no flow
            if(keyC instanceof TopConstant)
                return;

            // flow all branches
            if(!(keyC instanceof IntConstant))
                break TABLESWITCHSTMT;

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
            if(keyC instanceof BottomConstant)
                break LOOKUPSWITCHSTMT;

            // no flow
            if(keyC instanceof TopConstant)
                return;

            // flow all branches
            if(!(keyC instanceof IntConstant))
                break LOOKUPSWITCHSTMT;

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
     * Returns null or (Unit, Constant) pair if something has changed.
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
     * Returns true if the merge changed anything in the corrected
     * assumptions about local, and false otherwise.
     **/
    protected boolean merge(Local local, Constant constant)
    {
        Constant current = (Constant) localToConstant.get(local);

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
