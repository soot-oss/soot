import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.util.*;

import java.util.*;

/** Tracks which locals are definitely non-null.
 * Author: Patrick Lam (plam@sable.mcgill.ca)
 * Based on BranchedRefVarsAnalysis by Janus Godard (janus@place.org). */
class NullnessAnalysis extends ForwardBranchedFlowAnalysis
{
    protected void copy(Object src, 
                        Object dest) {
        FlowSet sourceSet = (FlowSet)src,
            destSet = (FlowSet) dest;
        
        sourceSet.copy(destSet);
    }


    protected void merge(Object src1, Object src2, Object dest)
    {
        FlowSet srcSet1 = (FlowSet) src1;
        FlowSet srcSet2 = (FlowSet) src2;
        FlowSet destSet = (FlowSet) dest;

        srcSet1.intersection(srcSet2, destSet);
    }

    FlowSet fullSet, emptySet;
    FlowUniverse allRefLocals;
    Map unitToGenerateSet;

    protected void flowThrough(Object srcValue, Unit unit,
                               List fallOut, List branchOuts)
    {
        FlowSet dest;
        FlowSet src  = (FlowSet) srcValue;
        Unit    s    = (Unit)    unit;

        // Create working set.
        dest = (FlowSet)src.clone();

        // Take out kill set.
        Iterator boxIt = s.getDefBoxes().iterator();
        while (boxIt.hasNext()) {
            ValueBox box = (ValueBox) boxIt.next();
            Value value = box.getValue();
            if (value instanceof Local && 
                    value.getType() instanceof RefLikeType)
                dest.remove(value);
        }

        // Perform gen.
        dest.union((FlowSet)unitToGenerateSet.get(unit), dest);

        // Handle copy statements: 
        //    x = y && 'y' in src => add 'x' to dest
        if (s instanceof DefinitionStmt)
        {
            DefinitionStmt as = (DefinitionStmt) s;

            Value ro = as.getRightOp();

            // extract cast argument
            if (ro instanceof CastExpr)
                ro = ((CastExpr) ro).getOp();
        
            if (src.contains(ro) &&
                  as.getLeftOp() instanceof Local)
                dest.add(as.getLeftOp());
        }

        // Copy the out value to the fallthrough box (don't need iterator)
        {
            Iterator it = fallOut.iterator();
            while (it.hasNext()) {
                FlowSet fs = (FlowSet) (it.next());
                copy(dest, fs);
            }
        }
        
        // Copy the out value to all branch boxes.
        {
            Iterator it = branchOuts.iterator();
            while (it.hasNext()) {
                FlowSet fs = (FlowSet) (it.next());
                copy(dest, fs);
            }
        }

        // Handle if statements by patching dest sets.
        if (unit instanceof IfStmt)
        {
            Value cond = ((IfStmt)unit).getCondition();
            Value op1 = ((BinopExpr) cond).getOp1();
            Value op2 = ((BinopExpr) cond).getOp2();
            boolean isNeg = cond instanceof NeExpr;
            Value toGen = null;

            // case 1: opN is a local and opM is NullConstant
            //          => opN nonnull on ne branch.
            if (op1 instanceof Local && op2 instanceof NullConstant)
                toGen = op1;

            if (op2 instanceof Local && op1 instanceof NullConstant)
                toGen = op2;

            if (toGen != null)
            {
                Iterator it = null;

                // if (toGen != null) goto l1: on branch, toGen nonnull.
                if (isNeg)
                    it = branchOuts.iterator();
                else
                    it = fallOut.iterator();

                while(it.hasNext()) {
                    FlowSet fs = (FlowSet) (it.next());
                    fs.add(toGen);
                }
            }

            // case 2: both ops are local and one op is non-null and testing equality
            if (op1 instanceof Local && op2 instanceof Local && 
                cond instanceof EqExpr)
            {
                toGen = null;

                if (src.contains(op1))
                    toGen = op2;
                if (src.contains(op2))
                    toGen = op1;

                if (toGen != null)
                {
                    Iterator branchIt = branchOuts.iterator();
                    while (branchIt.hasNext()) {
                        FlowSet fs = (FlowSet) (branchIt.next());
                        fs.add(toGen);
                    }
                }
            }    
        }
    }

    protected Object newInitialFlow()
    {
        return fullSet.clone();
    }

    protected Object entryInitialFlow()
    {
        // everything could be null
        return emptySet.clone();
    }

    private void addGen(Unit u, Value v)
    {
        ArraySparseSet l = (ArraySparseSet)unitToGenerateSet.get(u);
        l.add(v);
    }

    private void addGensFor(DefinitionStmt u)
    {
        Value lo = u.getLeftOp();
        Value ro = u.getRightOp();

        if (ro instanceof NewExpr ||
             ro instanceof NewArrayExpr ||
             ro instanceof NewMultiArrayExpr ||
             ro instanceof ThisRef ||
             ro instanceof CaughtExceptionRef)
            addGen(u, lo);
    }

    public NullnessAnalysis(UnitGraph g)
    {
        super(g);

        unitToGenerateSet = new HashMap();

        Body b = g.getBody();

        List refLocals = new LinkedList();

        // set up universe, empty, full sets.

        emptySet = new ArraySparseSet();
        fullSet = new ArraySparseSet();

        // Find all locals in body.
        Iterator localIt = b.getLocals().iterator();
        while (localIt.hasNext())
        {
            Local l = (Local)localIt.next();
            if (l.getType() instanceof RefLikeType)
                fullSet.add(l);
        }

        // Create gen sets.
        Iterator unitIt = b.getUnits().iterator();
        while (unitIt.hasNext())
        {
            Unit u = (Unit)unitIt.next();
            unitToGenerateSet.put(u, new ArraySparseSet());

            if (u instanceof DefinitionStmt)
            {
                Value lo = ((DefinitionStmt)u).getLeftOp();
                if (lo instanceof Local && 
                       lo.getType() instanceof RefLikeType)
                    addGensFor((DefinitionStmt)u);
            }

            Iterator boxIt = u.getUseAndDefBoxes().iterator();
            while (boxIt.hasNext())
            {
                Value boxValue = ((ValueBox) boxIt.next()).getValue();
                Value base = null;
                    
                if(boxValue instanceof InstanceFieldRef) {
                    base = ((InstanceFieldRef) (boxValue)).getBase();
                } else if (boxValue instanceof ArrayRef) {
                    base = ((ArrayRef) (boxValue)).getBase();
                } else if (boxValue instanceof InstanceInvokeExpr) {
                    base = ((InstanceInvokeExpr) boxValue).getBase();
                } else if (boxValue instanceof LengthExpr) {
                    base = ((LengthExpr) boxValue).getOp();
                } else if (u instanceof ThrowStmt) {
                    base = ((ThrowStmt)u).getOp();
                } else if (u instanceof MonitorStmt) {
                    base = ((MonitorStmt)u).getOp();
                }

                if (base != null && 
                      base instanceof Local && 
                      base.getType() instanceof RefLikeType)
                    addGen(u, base);
            }
        }

        // Call superclass method to do work.
        doAnalysis();
    }
}

