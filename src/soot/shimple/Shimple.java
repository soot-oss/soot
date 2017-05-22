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

package soot.shimple;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.G;
import soot.Local;
import soot.PhaseOptions;
import soot.Singletons;
import soot.SootMethod;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.JimpleBody;
import soot.options.Options;
import soot.shimple.internal.SPhiExpr;
import soot.shimple.internal.SPiExpr;
import soot.toolkits.graph.Block;
import soot.toolkits.scalar.ValueUnitPair;
import soot.util.Chain;

/**
 * Contains the constructors for the components of the SSA Shimple
 * grammar.  Methods are available to construct Shimple from
 * Jimple/Shimple, create Phi nodes, and converting back from
 * Shimple to Jimple.
 *
 * <p> This should normally be used in conjunction with the
 * constructor methods from soot.jimple.Jimple.
 *
 * <p> Miscellaneous utility functions are also available in this
 * class.
 *
 * @author Navindra Umanee
 * @see soot.jimple.Jimple
 * @see <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a>
**/
public class Shimple
{
    public static final String IFALIAS = "IfAlias";
    public static final String MAYMODIFY = "MayModify";
    public static final String PHI = "Phi";
    public static final String PI = "Pi";
    public static final String PHASE = "shimple";
    
    public Shimple(Singletons.Global g) {}

    public static Shimple v()
    { return G.v().soot_shimple_Shimple(); }

    /**
     * Returns an empty ShimpleBody associated with method m, using
     * default phase options.
     **/
    public ShimpleBody newBody(SootMethod m)
    {
        Map<String, String> options = PhaseOptions.v().getPhaseOptions(PHASE);
        return new ShimpleBody(m, options);
    }

    /**
     * Returns an empty ShimpleBody associated with method m, using
     * provided option map.
     **/
    public ShimpleBody newBody(SootMethod m, Map<String, String> options)
    {
        return new ShimpleBody(m, options);
    }

    /**
     * Returns a ShimpleBody constructed from b, using default phase
     * options.
     **/
    public ShimpleBody newBody(Body b)
    {
        Map<String, String> options = PhaseOptions.v().getPhaseOptions(PHASE);
        return new ShimpleBody(b, options);
    }

    /**
     * Returns a ShimpleBody constructed from b, using provided option
     * Map.
     **/
    public ShimpleBody newBody(Body b, Map<String, String> options)
    {
        return new ShimpleBody(b, options);
    }

    /**
     * Create a trivial PhiExpr, where preds are an ordered list of
     * the control predecessor Blocks of the Phi expression.  Instead
     * of a list of blocks, you may provide a list of the tail Units
     * from the corresponding blocks.
     **/
    public PhiExpr newPhiExpr(Local leftLocal, List<Block> preds)
    {
        return new SPhiExpr(leftLocal, preds);
    }

    public PiExpr newPiExpr(Local local, Unit predicate, Object targetKey)
    {
        return new SPiExpr(local, predicate, targetKey);
    }
    
    /**
     * Create a PhiExpr with the provided list of Values (Locals or
     * Constants) and the corresponding control flow predecessor
     * Blocks.  Instead of a list of predecessor blocks, you may
     * provide a list of the tail Units from the corresponding blocks.
     **/
    public PhiExpr newPhiExpr(List<Value> args, List<Unit> preds)
    {
        return new SPhiExpr(args, preds);
    }

    /**
     * Constructs a JimpleBody from a ShimpleBody.
     *
     * @see soot.options.ShimpleOptions
     **/
    public JimpleBody newJimpleBody(ShimpleBody body)
    {
        return body.toJimpleBody();
    }

    /**
     * Returns true if the value is a Phi expression, false otherwise.
     **/
    public static boolean isPhiExpr(Value value)
    {
        return (value instanceof PhiExpr);
    }
    
    /**
     * Returns true if the unit is a Phi node, false otherwise.
     **/
    public static boolean isPhiNode(Unit unit)
    {
        return
            getPhiExpr(unit) == null ? false : true;
    }

    /**
     * Returns the corresponding PhiExpr if the unit is a Phi node,
     * null otherwise.
     **/
    public static PhiExpr getPhiExpr(Unit unit)
    {
        if(!(unit instanceof AssignStmt))
            return null;

        Value right = ((AssignStmt)unit).getRightOp();
        
        if(isPhiExpr(right))
            return (PhiExpr) right;

        return null;
    }

    public static boolean isPiExpr(Value value)
    {
        return (value instanceof PiExpr);
    }

    public static boolean isPiNode(Unit unit)
    {
        return getPiExpr(unit) == null ? false : true;
    }

    public static PiExpr getPiExpr(Unit unit)
    {
        if(!(unit instanceof AssignStmt))
            return null;

        Value right = ((AssignStmt)unit).getRightOp();

        if(isPiExpr(right))
            return (PiExpr) right;

        return null;
    }

    /**
     * Returns the corresponding left Local if the unit is a Shimple node,
     * null otherwise.
     **/
    public static Local getLhsLocal(Unit unit)
    {
        if(!(unit instanceof AssignStmt))
            return null;

        Value right = ((AssignStmt)unit).getRightOp();
        
        if(right instanceof ShimpleExpr){
            Value left = ((AssignStmt)unit).getLeftOp();
            return (Local) left;
        }

        return null;
    }

    /**
     * If you are removing a Unit from a Unit chain which contains
     * PhiExpr's, you might want to call this utility function in
     * order to update any PhiExpr pointers to the Unit to point to
     * the Unit's predecessor(s). This function will not modify
     * "branch target" UnitBoxes.
     *
     * <p> Normally you should not have to call this function
     * directly, since patching is taken care of Shimple's internal
     * implementation of PatchingChain.
     **/
    public static void redirectToPreds(Body body, Unit remove)
    {
        boolean debug = Options.v().debug();
        if(body instanceof ShimpleBody)
            debug |= ((ShimpleBody)body).getOptions().debug();
            
        Chain<Unit> units = body.getUnits();

        /* Determine whether we should continue processing or not. */
        List<UnitBox> boxesPointingToThis = remove.getBoxesPointingToThis();
        if(boxesPointingToThis.isEmpty())
            return;

        for (UnitBox pointer : boxesPointingToThis) {
            // a PhiExpr may be involved, hence continue processing.
            // note that we will use the value of "pointer" and
            // continue iteration from where we left off.
            if(!pointer.isBranchTarget())
                break;
        }

        /* Ok, continuing... */
            
        Set<Unit> preds = new HashSet<Unit>();
        Set<PhiExpr> phis  = new HashSet<PhiExpr>();
        
        // find fall-through pred
        if(!remove.equals(units.getFirst())){
            Unit possiblePred = (Unit) units.getPredOf(remove);
            if(possiblePred.fallsThrough())
                preds.add(possiblePred);
        }

        // find the rest of the preds and all Phi's that point to remove
        for (Unit unit : units) {
        	for (UnitBox targetBox : unit.getUnitBoxes()) {
                if(remove.equals(targetBox.getUnit())){
                    if(targetBox.isBranchTarget())
                        preds.add(unit);
                    else{
                        PhiExpr phiExpr = Shimple.getPhiExpr(unit);
                        if(phiExpr != null)
                            phis.add(phiExpr);
                    }
                }
            }
        }

        /* sanity check */
        
        if(phis.size() == 0){
            if(debug)
                G.v().out.println("Warning: Orphaned UnitBoxes to " + remove + "? Shimple.redirectToPreds is giving up.");
            return;
        }

        if(preds.size() == 0){
            if(debug)
                G.v().out.println("Warning: Shimple.redirectToPreds couldn't find any predecessors for " + remove + " in " + body.getMethod() + ".");

            if(!remove.equals(units.getFirst())){
                Unit pred = (Unit) units.getPredOf(remove);
                if(debug)
                    G.v().out.println("Warning: Falling back to immediate chain predecessor: " + pred + ".");
                preds.add(pred);
            }
            else if(!remove.equals(units.getLast())){
                Unit succ = (Unit) units.getSuccOf(remove);
                if(debug)
                    G.v().out.println("Warning: Falling back to immediate chain successor: " + succ + ".");
                preds.add(succ);
            }
            else
                throw new RuntimeException("Assertion failed.");
        }

        /* At this point we have found all the preds and relevant Phi's */

        /* Each Phi needs an argument for each pred. */
        Iterator<PhiExpr> phiIt = phis.iterator();
        while(phiIt.hasNext()){
            PhiExpr phiExpr = phiIt.next();
            ValueUnitPair argBox = phiExpr.getArgBox(remove);

            if(argBox == null)
                throw new RuntimeException("Assertion failed.");
            
            // now we've got the value!
            Value arg = argBox.getValue();
            phiExpr.removeArg(argBox);

            // add new arguments to Phi
            Iterator<Unit> predsIt = preds.iterator();
            while(predsIt.hasNext()){
                Unit pred = predsIt.next();
                phiExpr.addArg(arg, pred);
            }
        }
    }

    /**
     * Redirects PhiExpr pointers to the given Unit to the new Unit.
     *
     * <p> Normally you should not have to call this function
     * directly, since patching is taken care of Shimple's internal
     * implementation of PatchingChain.
     **/
    public static void redirectPointers(Unit oldLocation, Unit newLocation)
    {
        List<UnitBox> boxesPointing = oldLocation.getBoxesPointingToThis();

        // important to change this to an array to have a static copy
        UnitBox[] boxes = boxesPointing.toArray(new UnitBox[boxesPointing.size()]);
        for (UnitBox box : boxes) {
            if(box.getUnit() != oldLocation)
                throw new RuntimeException("Something weird's happening");
            
            if(!box.isBranchTarget())
                box.setUnit(newLocation);
        }
    }
}
