package soot.jimple.toolkits.scalar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.UnitBox;
import soot.UnitBoxOwner;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.options.Options;
import soot.shimple.toolkits.scalar.SEvaluator;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

/**
 * Implementation of Conditional Constant Propagation from the paper of Wegman and Zadeck
 * "Constant Propagation with Conditional Branches".
 * https://www.cs.utexas.edu/users/lin/cs380c/wegman.pdf.
 */
public class ConditionalConstantPropagatorAndFolder extends BodyTransformer {
    private static final Logger logger = LoggerFactory.getLogger(ConditionalConstantPropagatorAndFolder.class);

    public ConditionalConstantPropagatorAndFolder(Singletons.Global g) {
    }

    public static ConditionalConstantPropagatorAndFolder v() {
        return G.v().soot_jimple_toolkits_scalar_ConditionalConstantPropagatorAndFolder();
    }

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        if (!(b instanceof JimpleBody))
            throw new RuntimeException("ConditionalConstantPropagatorAndFolder requires a JimpleBody.");

        if (Options.v().verbose()) {
            logger.debug("[" + b.getMethod().getName() + "] Propagating and folding constants...");
        }

        CCPFAnalysis ccpf = new CCPFAnalysis(new ExceptionalUnitGraph(b));

        propagateResults(b, ccpf.getResults());

        removeStmts(b, ccpf.getDeadStmts());
        replaceStmts(b, ccpf.getStmtsToReplace());
    }

    /**
     * Propagates constants to the definition and uses of the relevant locals given a mapping.
     **/
    protected void propagateResults(Body body, Map<ValueBox, Constant> valueBoxToConstant) {
        LocalDefs localDefs = LocalDefs.Factory.newLocalDefs(body);
        LocalUses localUses = LocalUses.Factory.newLocalUses(body);

        for (Local local : body.getLocals()) {
            // update definition
            for (Unit stmt : localDefs.getDefsOf(local)) {
                if (stmt instanceof JIdentityStmt)
                    continue;
                JAssignStmt aStmt = (JAssignStmt) stmt;

                Value constant = valueBoxToConstant.get(aStmt.getLeftOpBox());
                if (constant == null)
                    continue;
                if (constant instanceof SEvaluator.MetaConstant)
                    continue;

                Value old = aStmt.getRightOp();

                if (aStmt.getRightOpBox().canContainValue(constant)) {
                    aStmt.getRightOpBox().setValue(constant);

                    if (old instanceof UnitBoxOwner)
                        ((UnitBoxOwner) old).clearUnitBoxes();
                }
            }

            // update uses
            for (Unit stmt : localDefs.getDefsOf(local)) {
                for (UnitValueBoxPair pair : localUses.getUsesOf(stmt)) {
                    ValueBox useBox = pair.getValueBox();

                    Value constant = valueBoxToConstant.get(useBox);
                    if (constant instanceof SEvaluator.MetaConstant)
                        continue;

                    if (useBox.canContainValue(constant))
                        useBox.setValue(constant);
                }
            }
        }
    }

    /**
     * Removes the given list of fall through IfStmts from the body.
     **/
    protected void removeStmts(Body body, Set<IfStmt> deadStmts) {
        for (IfStmt dead : deadStmts) {
            body.getUnits().remove(dead);
            dead.clearUnitBoxes();
        }
    }

    /**
     * Replaces conditional branches by unconditional branches as given by the mapping.
     **/
    protected void replaceStmts(Body body, Map<Stmt, GotoStmt> stmtsToReplace) {
        // important not to call clearUnitBoxes() on booted since
        // replacement uses the same UnitBox
        for (Stmt booted : stmtsToReplace.keySet())
            body.getUnits().swapWith(booted, stmtsToReplace.get(booted));
    }
}

class CCPFAnalysis extends ForwardBranchedFlowAnalysis<HashMap<Local, Constant>> {
    protected HashMap<Local, Constant> initMap;
    protected HashMap<Local, Constant> handlerMap;
    protected Map<Stmt, GotoStmt> stmtToReplacement;
    protected Map<Unit, Boolean> unitToExecutable;
    protected Map<ValueBox, Constant> valueBoxToConstant;
    protected Set<IfStmt> deadStmts;
    protected boolean firstRun = true;

    public CCPFAnalysis(UnitGraph graph) {
        super(graph);
        stmtToReplacement = new HashMap<>();
        deadStmts = new HashSet<>();

        initMap = new HashMap<>();
        for (Local local : graph.getBody().getLocals())
            initMap.put(local, SEvaluator.TopConstant.v());

        handlerMap = new HashMap<>();
        for (Local local : graph.getBody().getLocals())
            handlerMap.put(local, SEvaluator.BottomConstant.v());

        unitToExecutable = new HashMap<>();
        for (Unit u : graph.getBody().getUnits())
            unitToExecutable.put(u, false);
        for (Unit u : graph.getHeads())
            unitToExecutable.put(u, true);
        for (Trap trap : graph.getBody().getTraps())
            unitToExecutable.put(trap.getHandlerUnit(), true);

        valueBoxToConstant = new HashMap<>();
        for (ValueBox box : graph.getBody().getUseAndDefBoxes())
            if (box.getValue() instanceof Local)
                valueBoxToConstant.put(box, null);

        doAnalysis();
    }

    public Map<ValueBox, Constant> getResults() {
        return valueBoxToConstant;
    }

    public Set<IfStmt> getDeadStmts() {
        return deadStmts;
    }

    public Map<Stmt, GotoStmt> getStmtsToReplace() {
        return stmtToReplacement;
    }

    protected boolean treatTrapHandlersAsEntries() {
        return false;
    }

    @Override
    protected void merge(HashMap<Local, Constant> in1, HashMap<Local, Constant> in2, HashMap<Local, Constant> out) {
        if (!in1.keySet().equals(in2.keySet()))
            throw new RuntimeException("Try to merge two flowSet with inconsistent keySet!");

        for (Local u : in1.keySet())
            out.put(u, meet(in1.get(u), in2.get(u)));
    }

    @Override
    protected void copy(HashMap<Local, Constant> source, HashMap<Local, Constant> dest) {
        dest.clear();
        dest.putAll(source);
    }

    Constant meet(Constant c1, Constant c2) {
        /**
         * Meet rules :
         * ∀x,
         * x Π Τ = x
         * x Π ⊥ = ⊥
         * c_1 Π c_1 = c_1 , where c_1 is a constant
         * c_2 Π c_1 = ⊥, where c_1 and c_2 are constant
         */
        if (c1 instanceof SEvaluator.BottomConstant || c2 instanceof SEvaluator.BottomConstant)
            return SEvaluator.BottomConstant.v();

        if (c1 instanceof SEvaluator.TopConstant)
            return c2;
        if (c2 instanceof SEvaluator.TopConstant)
            return c1;

        if (c1.equals(c2))
            return c1;
        else
            return SEvaluator.BottomConstant.v();
    }

    @Override
    protected HashMap<Local, Constant> newInitialFlow() {
        HashMap<Local, Constant> res = new HashMap<>();
        copy(initMap, res);
        return res;
    }

    @Override
    protected void flowThrough(HashMap<Local, Constant> in, Unit s, List<HashMap<Local, Constant>> fallOut, List<HashMap<Local, Constant>> branchOuts) {
        // not reachable
        if (!unitToExecutable.get(s))
            return;

        final HashMap<Local, Constant> localToConstant = in;

        processDefinitionStmt(s, localToConstant);

        for (ValueBox box : s.getUseAndDefBoxes())
            if (box.getValue() instanceof Local)
                valueBoxToConstant.put(box, localToConstant.get(box.getValue()));

        /* determine which nodes are reachable. */
        boolean conservative = true;
        boolean fall = false;
        boolean branch = false;
        HashMap<Local, Constant> oneBranch = null;
        Unit unitBranch = null;

        IFSTMT: {
            if (s instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt) s;
                Value cond = ifStmt.getCondition();
                Constant constant = SEvaluator.getFuzzyConstantValueOf(cond, localToConstant);

                // flow both ways
                if (constant instanceof SEvaluator.BottomConstant) {
                    deadStmts.remove(ifStmt);
                    stmtToReplacement.remove(ifStmt);
                    break IFSTMT;
                }

                // no flow
                if (constant instanceof SEvaluator.TopConstant)
                    return;

                /* determine whether to flow through or branch */
                conservative = false;

                Constant trueC = IntConstant.v(1);
                Constant falseC = IntConstant.v(0);

                if (constant.equals(trueC)) {
                    branch = true;
                    GotoStmt gotoStmt = Jimple.v().newGotoStmt(ifStmt.getTargetBox());
                    stmtToReplacement.put(ifStmt, gotoStmt);
                }

                if (constant.equals(falseC)) {
                    fall = true;
                    deadStmts.add(ifStmt);
                }
            }
        } // end IFSTMT

        TABLESWITCHSTMT: {
            if (s instanceof TableSwitchStmt) {
                TableSwitchStmt table = (TableSwitchStmt) s;
                Value keyV = table.getKey();
                Constant keyC = SEvaluator.getFuzzyConstantValueOf(keyV, localToConstant);

                // flow all branches
                if (keyC instanceof SEvaluator.BottomConstant) {
                    stmtToReplacement.remove(table);
                    break TABLESWITCHSTMT;
                }

                // no flow
                if (keyC instanceof SEvaluator.TopConstant)
                    return;

                // flow all branches
                if (!(keyC instanceof IntConstant))
                    break TABLESWITCHSTMT;

                /* find the one branch we need to flow to */
                conservative = false;

                int key = ((IntConstant) keyC).value;
                int low = table.getLowIndex();
                int high = table.getHighIndex();
                int index = key - low;

                UnitBox branchBox;
                if (index < 0 || index > high)
                    branchBox = table.getDefaultTargetBox();
                else
                    branchBox = table.getTargetBox(index);

                GotoStmt gotoStmt = Jimple.v().newGotoStmt(branchBox);
                stmtToReplacement.put(table, gotoStmt);

                List unitBoxes = table.getUnitBoxes();
                int setIndex = unitBoxes.indexOf(branchBox);
                oneBranch = branchOuts.get(setIndex);
                unitBranch = branchBox.getUnit();
            }
        } // end TABLESWITCHSTMT

        LOOKUPSWITCHSTMT: {
            if (s instanceof LookupSwitchStmt) {
                LookupSwitchStmt lookup = (LookupSwitchStmt) s;
                Value keyV = lookup.getKey();
                Constant keyC = SEvaluator.getFuzzyConstantValueOf(keyV, localToConstant);

                // flow all branches
                if (keyC instanceof SEvaluator.BottomConstant) {
                    stmtToReplacement.remove(lookup);
                    break LOOKUPSWITCHSTMT;
                }

                // no flow
                if (keyC instanceof SEvaluator.TopConstant)
                    return;

                // flow all branches
                if (!(keyC instanceof IntConstant))
                    break LOOKUPSWITCHSTMT;

                /* find the one branch we need to flow to */
                conservative = false;

                int index = lookup.getLookupValues().indexOf(keyC);

                UnitBox branchBox;
                if (index == -1)
                    branchBox = lookup.getDefaultTargetBox();
                else
                    branchBox = lookup.getTargetBox(index);

                GotoStmt gotoStmt = Jimple.v().newGotoStmt(branchBox);
                stmtToReplacement.put(lookup, gotoStmt);

                List unitBoxes = lookup.getUnitBoxes();
                int setIndex = unitBoxes.indexOf(branchBox);
                oneBranch = branchOuts.get(setIndex);
                unitBranch = branchBox.getUnit();
            }
        } // end LOOKUPSWITCHSTMT

        // conservative control flow estimates
        if (conservative) {
            fall = s.fallsThrough();
            branch = s.branches();
        }

        if (fall) {
            if (fallOut.size() != 1)
                throw new RuntimeException("Unexpected fallOut occur on pure fall-through statement!");

            final HashMap<Local, Constant> fallSet = fallOut.get(0);
            merge(in, fallSet, fallSet);

            Unit succ = ((UnitGraph) graph).getBody().getUnits().getSuccOf(s);
            if (succ != null)
                unitToExecutable.put(succ, true);
        }

        if (branch) {
            for (HashMap<Local, Constant> branchSet : branchOuts)
                merge(in, branchSet, branchSet);

            for (UnitBox u : s.getUnitBoxes()) {
                if (u.getUnit() != null)
                    unitToExecutable.put(u.getUnit(), true);
            }
        }

        if (oneBranch != null) {
            merge(in, oneBranch, oneBranch);

            unitToExecutable.put(unitBranch, true);
        }
    }

    @Override
    public HashMap<Local, Constant> getFlowBefore(Unit s) {
        if (firstRun) {
            // Do the true init for throw-handlers.
            for (Trap trap : ((UnitGraph) graph).getBody().getTraps()) {
                HashMap<Local, Constant> res = new HashMap() {{ copy(handlerMap, this); }};
                unitToBeforeFlow.put(trap.getHandlerUnit(), res);
            }
            firstRun = false;
        }
        return super.getFlowBefore(s);
    }

    protected void processDefinitionStmt(Unit u, Map<Local, Constant> localToConstant) {
        if (!(u instanceof DefinitionStmt))
            return;

        DefinitionStmt dStmt = (DefinitionStmt) u;
        Local local;

        {
            Value value = dStmt.getLeftOp();
            if (!(value instanceof Local))
                return;
            local = (Local) value;
        }

        Value rightOp = dStmt.getRightOp();
        // This evaluation process is correct!
        Constant constant = (u instanceof JIdentityStmt) ? SEvaluator.BottomConstant.v()
                : SEvaluator.getFuzzyConstantValueOf(rightOp, localToConstant);

        // This is verified, the only case won't show up is new constant is TOP when old one is constant.
        if (localToConstant.get(local) instanceof SEvaluator.BottomConstant)
            return;

        localToConstant.put(local, constant);
    }
}