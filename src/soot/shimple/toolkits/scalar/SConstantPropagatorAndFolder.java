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
import java.util.*;

/**
 * An example of constant propagation using Shimple.
 *
 * <p> This analysis is already more powerful than the simplistic
 * soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder and
 * demonstrates some of the benefits of SSA -- particularly the fact
 * that Phi nodes represent natural merge points in the control flow.
 * This implementation also shows how to access U/D and D/U chains in
 * Shimple.
 *
 * <p> To use this analysis from the command line in Soot, try
 * something like: <code>soot.Main -f shimple -p sop on
 * &lt;classname&gt;</code> or <code>soot.Main -f jimple --via-shimple
 * -p sop on -p shimple naive-phi-elimination
 * &lt;classname&gt;</code>.
 *
 * <p> To compare the results with the non-SSA propagator, you can use
 * (this disables all optimizations but constant propagation and
 * folding): <code>soot.Main -f jimple -p jop on -p jop.cpf on -p
 * jop.cse off -p jop.bcm off -p off jop.lcm -p off jop.cp -p jop.cbf
 * off -p jop.dae off -p jop.uce1 off -p jop.ubf1 off -p jop.uce2 off
 * -p jop.ubf2 off -p jop.ubf2 off * &lt;classname&gt;</code>
 * 
 * <p> The analysis is based on the efficient linear algorithm
 * described in section 1.1, P5 of the Cytron paper with the exception
 * that conditional control flow is not considered (conservatively
 * estimated).  This is not necessarily the best implementation (in
 * fact, it's a somewhat brute force approach to programming!) --
 * improvements and suggestions are welcome.
 *
 * @author Navindra Umanee
 * @see soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder
 * @see soot.shimple.toolkits.scalar.ShimpleLocalDefs
 * @see soot.toolkits.scalar.SimpleLocalUses
 * @see <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a> 
 **/
public class SConstantPropagatorAndFolder extends BodyTransformer
{
    public SConstantPropagatorAndFolder(Singletons.Global g) {}

    public static SConstantPropagatorAndFolder v()
    { return G.v().SConstantPropagatorAndFolder(); }

    /**
     * Map that keeps track of our assumptions on whether a local is a
     * constant or not.  Initially we assume all Locals are
     * TopConstant.
     **/
    protected Map localToConstant;

    protected ShimpleLocalDefs localDefs;
    protected LocalUses localUses;
    
    protected void internalTransform(Body b, String phaseName, Map options)
    {
        if(!(b instanceof ShimpleBody))
            throw new RuntimeException("SConstantPropagatorAndFolder requires a ShimpleBody.");
           
        ShimpleBody sBody = (ShimpleBody) b;

        if (Options.v().verbose())
            G.v().out.println("[" + sBody.getMethod().getName() +
                               "] Propagating and folding constants (SSA)...");

        Chain units = sBody.getUnits();
        Chain locals = sBody.getLocals();

        // initialise localToConstant map -- assume all scalars are
        // constant (Top)
        {
            localToConstant = new HashMap(units.size() * 2 + 1, 0.7f);

            Iterator localsIt = locals.iterator();
            while(localsIt.hasNext()){
                Local local = (Local) localsIt.next();
                Type localType = local.getType();

                // only concerned with scalars
                if(localType instanceof PrimType)
                    localToConstant.put(local, TopConstant.v());
            }
        }

        localDefs = sBody.getLocalDefs();
        localUses = sBody.getLocalUses();

        // flow analysis
        {
            Iterator unitsIt = units.iterator();
            while(unitsIt.hasNext()){
                propagate((Unit) unitsIt.next());
            }
        }

        // finally, propagate the results
        {
            Iterator localsIt = locals.iterator();
            while(localsIt.hasNext()){
                Local local = (Local) localsIt.next();
                Constant constant = (Constant) localToConstant.get(local);

                if(constant instanceof NumericConstant){
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

    /**
     * Recursive flow analysis function.  Our assumptions are
     * maintained in the localToConstant table.  Initially all Locals
     * are assumed to be the TopConstant and assumptions are corrected
     * until there are no more changes.
     *
     * <p> This function observes a Unit and recursively updates
     * assumptions if new defining information is found.
     **/
    protected void propagate(Unit unit)
    {
        if(!(unit instanceof DefinitionStmt))
            return;

        DefinitionStmt dStmt = (DefinitionStmt) unit;
        
        Local local;
        
        {
            Value value = dStmt.getLeftOp();

            // not concerned
            if(!(value instanceof Local))
                return;
        
            // non-scalar
            if(localToConstant.get(value) == null) return;

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
                    if((assumedConstant == null) ||
                       (assumedConstant instanceof BottomConstant)){
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

        // see if anything changed and if so, recursively propagate
        if(changed){
            Iterator usesIt = localUses.getUsesOf(unit).iterator();
            while(usesIt.hasNext()){
                Unit client = ((UnitValueBoxPair) usesIt.next()).getUnit();
                propagate(client);
            }
        }
    }
    
    /**
     * Returns true if the merge changed anything in the corrected
     * assumptions about local, and false otherwise.
     **/
    protected boolean merge(Local local, Constant constant)
    {
        Constant current = (Constant) localToConstant.get(local);

        if(current == null || current instanceof BottomConstant) 
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
class TopConstant extends Constant
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
class BottomConstant extends Constant
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
