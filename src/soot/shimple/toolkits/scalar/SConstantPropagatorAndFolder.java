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

public class SConstantPropagatorAndFolder extends BodyTransformer
{
    public SConstantPropagatorAndFolder(Singletons.Global g) {}


    public static SConstantPropagatorAndFolder v()
    { return G.v().SConstantPropagatorAndFolder(); }

    protected void internalTransform(Body b, String phaseName, Map options)
    {
        if(!(b instanceof ShimpleBody))
            throw new RuntimeException("SConstantPropagatorAndFolder requires a ShimpleBody.");

        ShimpleBody sb = (ShimpleBody) b;

        if(!sb.isSSA())
            throw new RuntimeException("ShimpleBody is not in proper SSA form as required by SConstantPropagatorAndFolder.  You may need to rebuild it or use ConstantPropagatorAndFolder instead.");

        if (Options.v().verbose())
            G.v().out.println("[" + sb.getMethod().getName() +
                              "] Propagating and folding constants (SSA)...");

        // perform the main analysis
        SCPFAnalysis scpf = new SCPFAnalysis(new CompleteUnitGraph(sb));
        Map localToConstant = scpf.getResults();

        // propagate the results
        {
            Chain units = sb.getUnits();
            Chain locals = sb.getLocals();
            ShimpleLocalDefs localDefs = new ShimpleLocalDefs(sb);
            LocalUses localUses = new SimpleLocalUses(sb, localDefs);
        
            Iterator localsIt = locals.iterator();
            while(localsIt.hasNext()){
                Local local = (Local) localsIt.next();
                Constant constant = (Constant) localToConstant.get(local);

                if(!(constant instanceof BogusConstant)){
                    DefinitionStmt stmt =(DefinitionStmt) localDefs.getDefsOf(local).get(0);

                    // update the definition
                    {
                        ValueBox defSrcBox = stmt.getRightOpBox();

                        if(defSrcBox.canContainValue(constant))
                            defSrcBox.setValue(constant);
                        else
                            G.v().out.println("Warning: Couldn't propagate a constant.");
                    }
                    
                    // update the uses
                    {
                        Iterator usesIt = localUses.getUsesOf(stmt).iterator();

                        while(usesIt.hasNext()){
                            ValueBox clientUseBox =
                                ((UnitValueBoxPair) usesIt.next()).getValueBox();

                            if(clientUseBox.canContainValue(constant))
                                clientUseBox.setValue(constant);
                            else
                                G.v().out.println("Warning: Couldn't propagate a constant.");
                        }
                    }
                }
            }
        }
    }
}

class SCPFAnalysis extends ForwardBranchedFlowAnalysis
{
    FlowSet emptySet;
    Map localToConstant;

    public Map getResults()
    {
        return localToConstant;
    }
    
    public SCPFAnalysis(UnitGraph graph)
    {
        super(graph);
        emptySet = new ArraySparseSet();

        // initialise localToConstant map -- assume all scalars are
        // constant (Top)
        {
            Chain locals = graph.getBody().getLocals();
            Iterator localsIt = locals.iterator();
            localToConstant = new HashMap(graph.size() * 2 + 1, 0.7f);

            while(localsIt.hasNext()){
                Local local = (Local) localsIt.next();
                Type localType = local.getType();
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

    public void copy(Object source, Object dest)
    {
        FlowSet fource = (FlowSet) source;
        FlowSet fest = (FlowSet) dest;

        fource.copy(fest);
    }

    public void flowThrough(Object in, Unit s, List fallOut, List branchOuts)
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

        boolean fall = false;
        boolean branch = false;
        
        IFSTMT:
        {
        if(s instanceof IfStmt){
            Value condValue = ((IfStmt) s).getCondition();
            if(!(condValue instanceof ConditionExpr))
                break IFSTMT;
            ConditionExpr ce = (ConditionExpr) condValue;
            
            Constant op1, op2;
            
            {
                Value op1Value = ce.getOp1();
                if(op1Value instanceof Constant)
                    op1 = (Constant) op1Value;
                else
                    op1 = (Constant) localToConstant.get(op1Value);

                Value op2Value = ce.getOp2();
                if(op2Value instanceof Constant)
                    op2= (Constant) op2Value;
                else
                    op2 = (Constant) localToConstant.get(op2Value);
            }

            if(op1 instanceof BottomConstant || op2 instanceof BottomConstant)
                break IFSTMT;
            
            if(op1 instanceof TopConstant || op2 instanceof TopConstant)
                return;

            if(ce instanceof EqExpr || ce instanceof GeExpr || ce instanceof LeExpr){
                if(op1.equals(op2))
                    branch = true;
                else
                    fall = true;
            }

            if(ce instanceof NeExpr){
                if(op1.equals(op2))
                    fall = true;
                else
                    branch = true;
            }

            Constant trueC = IntConstant.v(1);
            Constant falseC = IntConstant.v(0);
            
            if(ce instanceof GeExpr || ce instanceof GtExpr){
                NumericConstant nop1 = (NumericConstant) op1;
                NumericConstant nop2 = (NumericConstant) op2;
                
                Constant retC = nop1.greaterThan(nop2);
                if(retC.equals(trueC))
                    branch = true;
                else
                    fall = true;
            }

            if(ce instanceof LeExpr || ce instanceof LtExpr){
                NumericConstant nop1 = (NumericConstant) op1;
                NumericConstant nop2 = (NumericConstant) op2;
                
                Constant retC = nop1.lessThan(nop2);
                if(retC.equals(trueC))
                    branch = true;
                else
                    fall = true;
            }
            
            if(fall == branch)
                throw new RuntimeException("Assertion failed.");
        }
        }

        // conservative control flow estimates
        if(fall == branch){
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
    }

    /**
     * Returns null or (Unit, Constant) pair if something has changed.
     **/
    public Pair processDefinitionStmt(Unit u)
    {
        if(!(u instanceof DefinitionStmt))
            return null;

        DefinitionStmt dStmt = (DefinitionStmt) u;
        
        Local local;
        
        {
            Value value = dStmt.getLeftOp();

            // not concerned
            if(!(value instanceof Local))
                return null;
        
            local = (Local) value;
        }

        // keeps track of any changes
        boolean changed = false;

        Value rightOp = dStmt.getRightOp();

        // update our assumptions
        if(dStmt instanceof IdentityStmt)
            changed = merge(local, BottomConstant.v());
        else if(rightOp instanceof FieldRef)
            changed = merge(local, BottomConstant.v());
        else if(rightOp instanceof Constant)
            changed = merge(local, (Constant) rightOp);
        else if(rightOp instanceof Local){
            Constant rightValue = (Constant) localToConstant.get(rightOp);
            changed = merge(local, rightValue);
        }
        else if(!(rightOp instanceof UnopExpr ||
                  rightOp instanceof BinopExpr ||
                  rightOp instanceof PhiExpr)){
            changed = merge(local, BottomConstant.v());
        }
        // now handle PhiExpr, UnopExpr and BinopExpr
        else{
            // clonedRightOp is a copy of rightOp updated to use
            // our assumptions.  We create it so that it can be passed
            // to Evaluator in order to determine the Constant value of
            // the expression
            Value clonedRightOp = (Value) rightOp.clone();

            Iterator useBoxIt = clonedRightOp.getUseBoxes().iterator();
            boolean cannotfold = false;

            // update clonedRightOp with our assumptions
            while(useBoxIt.hasNext()){
                ValueBox useBox = (ValueBox) useBoxIt.next();
                Value use = useBox.getValue();

                if(use instanceof Local){
                    Constant assumedConstant = (Constant) localToConstant.get(use);
                    if(assumedConstant instanceof BottomConstant){
                        changed = merge(local, BottomConstant.v());
                        break;
                    }
                    else if(assumedConstant instanceof TopConstant){
                        cannotfold = true;
                    }
                    else{
                        if(useBox.canContainValue(assumedConstant))
                           useBox.setValue(assumedConstant);
                    }
                }
            }

            // We cannot fold normal expressions because Top is
            // present in the expression, we simply assume the
            // expression resolves to Top.  We can (and should)
            // however fold Phi expressions simply by ignoring Top.
            if(cannotfold && clonedRightOp instanceof PhiExpr){
                PhiExpr pe = (PhiExpr) clonedRightOp;

                if(SEvaluator.isPhiFuzzyConstantValued(pe)){
                    Constant rightValue = SEvaluator.getFirstConstantInPhi(pe);

                    if(rightValue != null)
                        changed = merge(local, rightValue);
                    // else TopConstant
                }
                else
                    changed = merge(local, BottomConstant.v());
            }
            else{
                Constant newConstant = (Constant) SEvaluator.getConstantValueOf(clonedRightOp);
                if(newConstant != null)
                    changed = merge(local, newConstant);
                else
                    changed = merge(local, BottomConstant.v());
            }
        } // end of merging phase

         if(!changed)
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

/**
 * Top.  Denotes that a local is conservatively assumed to be a constant.
 **/
class TopConstant extends BogusConstant
{
    private static final TopConstant constant = new TopConstant();
    
    private TopConstant() {}

    public static Constant v()
    {
        return constant;
    }
    
    public Type getType()
    {
        return UnknownType.v();
    }

    public void apply(Switch sw)
    {
        throw new RuntimeException("Not implemented.");
    }
}

/**
 * Denotes that a local is (conservatively) not a constant.
 **/
class BottomConstant extends BogusConstant
{
    private static final BottomConstant constant = new BottomConstant();
        
    private BottomConstant() {}

    public static Constant v()
    {
        return constant;
    }
    
    public Type getType()
    {
        return UnknownType.v();
    }
    
    public void apply(Switch sw)
    {
        throw new RuntimeException("Not implemented.");
    }
}

abstract class BogusConstant extends Constant
{
}
