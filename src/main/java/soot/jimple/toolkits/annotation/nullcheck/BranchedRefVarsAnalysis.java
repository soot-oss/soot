package soot.jimple.toolkits.annotation.nullcheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrick Lam, Janus
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.EquivalentValue;
import soot.Local;
import soot.NullType;
import soot.RefType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.EqExpr;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.MonitorStmt;
import soot.jimple.NeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArrayFlowUniverse;
import soot.toolkits.scalar.ArrayPackedSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;

/*
     README FIRST - IMPORTANT IMPLEMENTATION NOTE

     As per the analysis presented in the report, there are four possible 
     pairs for given reference r:
     (r, kBottom)
     (r, kNonNull)
     (r, kNull)
     (r, kTop)

     To save space an simplify operations, we implemented those 4 values 
     with two bits rather than four, namely:

     (r, kTop) in a set is represented by having (r, kNull) and (r, kNonNull) 
     in the set. Ditto, (r, kBottom) is represented by having neither in the set.
    
     Keep this in mind as you read the code, it helps. Honnest :)

     -- Janus

 */

/*
      BranchedRefVarsAnalysis class

      Perform the analysis presented in the report.

      
      KNOWN LIMITATION: there is a problem in the ForwardBranchedFlowAnalysis
      or maybe the CompleteUnitGraph that prevent the analysis
      (at the ForwardBranchedFlowAnalysis level) to handle properly traps.
      We make the analysis conservative in case of exceptions by setting
      exceptions handler statements In to TOP.
      

 */

/**
 * @deprecated THIS IS KNOWN TO BE BUGGY. USE {@link NullnessAnalysis} INSTEAD!
 */
@Deprecated
public class BranchedRefVarsAnalysis extends ForwardBranchedFlowAnalysis<FlowSet<RefIntPair>> {
  private static final Logger logger = LoggerFactory.getLogger(BranchedRefVarsAnalysis.class);
  /*
   * COMPILATION OPTIONS
   */

  // we don't want the analysis to be conservative?
  // i.e. we don't want it to only care for locals
  private static final boolean isNotConservative = false;

  // do we want the analysis to handle if statements?
  private static final boolean isBranched = true;

  // do we want the analysis to care that f and g
  // could be the same reference?
  private static final boolean careForAliases = false;

  // do we want the analysis to care that a method
  // call could have side effects?
  private static final boolean careForMethodCalls = true;

  // **** END OF COMPILATION OPTIONS *****

  // constants for the analysis
  public final static int kBottom = 0;
  public final static int kNull = 1;
  public final static int kNonNull = 2;
  public final static int kTop = 99;

  // bottom and top sets
  protected final FlowSet<RefIntPair> emptySet;
  protected final FlowSet<RefIntPair> fullSet;

  // gen and preserve sets (for each statement)
  protected final Map<Unit, FlowSet<RefIntPair>> unitToGenerateSet;
  protected final Map<Unit, FlowSet<RefIntPair>> unitToPreserveSet;

  // sets of variables that need a null pointer check (for each statement)
  protected final Map<Unit, HashSet<Value>> unitToAnalyzedChecksSet;
  protected final Map<Unit, HashSet<Value>> unitToArrayRefChecksSet;
  protected final Map<Unit, HashSet<Value>> unitToInstanceFieldRefChecksSet;
  protected final Map<Unit, HashSet<Value>> unitToInstanceInvokeExprChecksSet;
  protected final Map<Unit, HashSet<Value>> unitToLengthExprChecksSet;

  // keep track of the different kinds of reference types this analysis is working on
  protected final List<EquivalentValue> refTypeLocals;
  protected final List<EquivalentValue> refTypeInstFields;
  protected final List<EquivalentValue> refTypeInstFieldBases;
  protected final List<EquivalentValue> refTypeStaticFields;
  protected final List<EquivalentValue> refTypeValues; // sum of all the above

  // fast conversion from Value -> EquivalentValue
  // because used in methods
  private final HashMap<Value, EquivalentValue> valueToEquivValue = new HashMap<Value, EquivalentValue>(2293, 0.7f);

  // constant (r, v) pairs because used in methods
  private final HashMap<EquivalentValue, RefIntPair> kRefBotttomPairs = new HashMap<EquivalentValue, RefIntPair>(2293, 0.7f);
  private final HashMap<EquivalentValue, RefIntPair> kRefNonNullPairs = new HashMap<EquivalentValue, RefIntPair>(2293, 0.7f);
  private final HashMap<EquivalentValue, RefIntPair> kRefNullPairs = new HashMap<EquivalentValue, RefIntPair>(2293, 0.7f);
  private final HashMap<EquivalentValue, RefIntPair> kRefTopPairs = new HashMap<EquivalentValue, RefIntPair>(2293, 0.7f);

  // used in flowThrough.
  protected FlowSet<RefIntPair> tempFlowSet = null;

  /**
   * @deprecated THIS IS KNOWN TO BE BUGGY. USE {@link NullnessAnalysis} INSTEAD!
   */
  @Deprecated
  public BranchedRefVarsAnalysis(UnitGraph g) {
    super(g);

    // initialize all the refType lists
    this.refTypeLocals = new ArrayList<EquivalentValue>();
    this.refTypeInstFields = new ArrayList<EquivalentValue>();
    this.refTypeInstFieldBases = new ArrayList<EquivalentValue>();
    this.refTypeStaticFields = new ArrayList<EquivalentValue>();
    this.refTypeValues = new ArrayList<EquivalentValue>();
    initRefTypeLists();

    // initialize emptySet, fullSet and tempFlowSet
    {
      final int len = refTypeValues.size();
      RefIntPair[] universeArray = new RefIntPair[2 * len];

      for (int i = 0; i < len; i++) {
        int j = i * 2;
        EquivalentValue r = refTypeValues.get(i);
        universeArray[j] = getKRefIntPair(r, kNull);
        universeArray[j + 1] = getKRefIntPair(r, kNonNull);
      }
      ArrayPackedSet<RefIntPair> temp = new ArrayPackedSet<RefIntPair>(new ArrayFlowUniverse<RefIntPair>(universeArray));
      this.emptySet = temp;
      this.fullSet = temp.clone();
      temp.complement(fullSet);

      this.tempFlowSet = newInitialFlow();
    }

    // initialize unitTo...Sets
    // perform preservation and generation
    {
      final int cap = graph.size() * 2 + 1;
      this.unitToGenerateSet = new HashMap<Unit, FlowSet<RefIntPair>>(cap, 0.7f);
      this.unitToPreserveSet = new HashMap<Unit, FlowSet<RefIntPair>>(cap, 0.7f);

      this.unitToAnalyzedChecksSet = new HashMap<Unit, HashSet<Value>>(cap, 0.7f);
      this.unitToArrayRefChecksSet = new HashMap<Unit, HashSet<Value>>(cap, 0.7f);
      this.unitToInstanceFieldRefChecksSet = new HashMap<Unit, HashSet<Value>>(cap, 0.7f);
      this.unitToInstanceInvokeExprChecksSet = new HashMap<Unit, HashSet<Value>>(cap, 0.7f);
      this.unitToLengthExprChecksSet = new HashMap<Unit, HashSet<Value>>(cap, 0.7f);
    }
    initUnitSets();

    doAnalysis();
  } // end constructor

  public EquivalentValue getEquivalentValue(Value v) {
    if (valueToEquivValue.containsKey(v)) {
      return valueToEquivValue.get(v);
    } else {
      EquivalentValue ev = new EquivalentValue(v);
      valueToEquivValue.put(v, ev);
      return ev;
    }
  } // end getEquivalentValue

  // make that (r, v) pairs are constants
  // i.e. the same r and v values always generate the same (r, v) object
  public RefIntPair getKRefIntPair(EquivalentValue r, int v) {
    HashMap<EquivalentValue, RefIntPair> pairsMap;
    switch (v) {
      case kNonNull:
        pairsMap = kRefNonNullPairs;
        break;
      case kNull:
        pairsMap = kRefNullPairs;
        break;
      case kTop:
        pairsMap = kRefTopPairs;
        break;
      case kBottom:
        pairsMap = kRefBotttomPairs;
        break;
      default:
        throw new RuntimeException("invalid constant (" + v + ")");
    }

    if (pairsMap.containsKey(r)) {
      return pairsMap.get(r);
    } else {
      RefIntPair pair = new RefIntPair(r, v, this);
      pairsMap.put(r, pair);
      return pair;
    }
  } // end getKRefIntPair

  /*
   * Utility methods.
   * 
   * They are used all over the place. Most of them are declared "private static" so they can be inlined with javac -O.
   */

  // isAlwaysNull returns true if the reference r is known to be always null
  private static boolean isAlwaysNull(Value r) {
    return (r instanceof NullConstant) || (r.getType() instanceof NullType);
  } // end isAlwaysNull

  // isAlwaysTop returns true if the reference r is known to be always top for this analysis
  // i.e. its value is undecidable by this analysis
  private static boolean isAlwaysTop(Value r) {
    if (isNotConservative) {
      return false;
    } else {
      return r instanceof InstanceFieldRef || r instanceof StaticFieldRef;
    }
  } // end isAlwaysTop

  private static boolean isAlwaysNonNull(Value ro) {
    return (ro instanceof NewExpr) || (ro instanceof NewArrayExpr) || (ro instanceof NewMultiArrayExpr)
        || (ro instanceof ThisRef) || (ro instanceof CaughtExceptionRef) || (ro instanceof StringConstant);
  }

  // isAnalyzedRef returns true if the reference r is to be analyzed by this analysis
  // i.e. its value is not always known (or undecidable)
  private static boolean isAnalyzedRef(Value r) {
    if (isAlwaysNull(r) || isAlwaysTop(r)) {
      return false;
    } else if (r instanceof Local || r instanceof InstanceFieldRef || r instanceof StaticFieldRef) {
      Type rType = r.getType();
      return (rType instanceof RefType || rType instanceof ArrayType);
    } else {
      return false;
    }
  } // end isAnalyzedRef

  // refInfo is a helper method to tranform our two bit representation back to the four constants
  // For a given reference and a flow set, tell us if r is bottom, top, null or non-null
  // Note: this method will fail if r is not in the flow set
  protected final int refInfo(EquivalentValue r, FlowSet<RefIntPair> fs) {
    boolean isNull = fs.contains(getKRefIntPair(r, kNull));
    boolean isNonNull = fs.contains(getKRefIntPair(r, kNonNull));

    if (isNull && isNonNull) {
      return kTop;
    } else if (isNull) {
      return kNull;
    } else if (isNonNull) {
      return kNonNull;
    } else {
      return kBottom;
    }
  } // end refInfo

  private int refInfo(Value r, FlowSet<RefIntPair> fs) {
    return refInfo(getEquivalentValue(r), fs);
  } // end refInfo

  // Like refInfo, but the reference doesn't have to be in the flow set
  // note: it still need to be a reference, i.e. ArrayType or RefType
  public final int anyRefInfo(Value r, FlowSet<RefIntPair> f) {
    if (isAlwaysNull(r)) {
      return kNull;
    } else if (isAlwaysTop(r)) {
      return kTop;
    } else if (isAlwaysNonNull(r)) {
      return kNonNull;
    } else {
      return refInfo(r, f);
    }
  } // end anyRefInfo

  /*
   * methods: uAddTopToFlowSet uAddInfoToFlowSet uListAddTopToFlowSet
   * 
   * Adding a pair (r, v) to a set is always a two steps process: a) remove all pairs (r, *) b) add the pair (r, v)
   * 
   * The methods above handle that.
   * 
   * Most of them come in two flavors: to act on one set or two act on separate generate and preserve sets.
   * 
   */

  // method to add (r, kTop) to the gen set (and remove it from the pre set)
  private void uAddTopToFlowSet(EquivalentValue r, FlowSet<RefIntPair> genFS, FlowSet<RefIntPair> preFS) {
    RefIntPair nullPair = getKRefIntPair(r, kNull);
    RefIntPair nullNonPair = getKRefIntPair(r, kNonNull);

    if (genFS != preFS) {
      preFS.remove(nullPair, preFS);
      preFS.remove(nullNonPair, preFS);
    }

    genFS.add(nullPair, genFS);
    genFS.add(nullNonPair, genFS);
  } // end uAddTopToFlowSet

  private void uAddTopToFlowSet(Value r, FlowSet<RefIntPair> genFS, FlowSet<RefIntPair> preFS) {
    uAddTopToFlowSet(getEquivalentValue(r), genFS, preFS);
  } // end uAddTopToFlowSet

  // method to add (r, kTop) to a set
  private void uAddTopToFlowSet(Value r, FlowSet<RefIntPair> fs) {
    uAddTopToFlowSet(getEquivalentValue(r), fs, fs);
  } // end uAddTopToFlowSet

  // method to add (r, kTop) to a set
  private void uAddTopToFlowSet(EquivalentValue r, FlowSet<RefIntPair> fs) {
    uAddTopToFlowSet(r, fs, fs);
  } // end uAddTopToFlowSet

  // method to add (r, kNonNull) or (r, kNull) to the gen set (and remove it from the pre set)
  private void uAddInfoToFlowSet(EquivalentValue r, int v, FlowSet<RefIntPair> genFS, FlowSet<RefIntPair> preFS) {
    int kill;
    switch (v) {
      case kNull:
        kill = kNonNull;
        break;
      case kNonNull:
        kill = kNull;
        break;
      default:
        throw new RuntimeException("invalid info");
    }

    if (genFS != preFS) {
      preFS.remove(getKRefIntPair(r, kill), preFS);
    }

    genFS.remove(getKRefIntPair(r, kill), genFS);
    genFS.add(getKRefIntPair(r, v), genFS);
  } // end uAddInfoToFlowSet

  private void uAddInfoToFlowSet(Value r, int v, FlowSet<RefIntPair> genF, FlowSet<RefIntPair> preF) {
    uAddInfoToFlowSet(getEquivalentValue(r), v, genF, preF);
  } // end uAddInfoToFlowSet

  // method to add (r, kNonNull) or (r, kNull) to a set
  private void uAddInfoToFlowSet(Value r, int v, FlowSet<RefIntPair> fs) {
    uAddInfoToFlowSet(getEquivalentValue(r), v, fs, fs);
  } // end uAddInfoToFlowSet

  // method to add (r, kNonNull) or (r, kNull) to a set
  private void uAddInfoToFlowSet(EquivalentValue r, int v, FlowSet<RefIntPair> fs) {
    uAddInfoToFlowSet(r, v, fs, fs);
  } // end uAddInfoToFlowSet

  // method to apply uAddTopToFlowSet to a whole list of references
  private void uListAddTopToFlowSet(List<EquivalentValue> refs, FlowSet<RefIntPair> genFS, FlowSet<RefIntPair> preFS) {
    for (EquivalentValue ev : refs) {
      uAddTopToFlowSet(ev, genFS, preFS);
    }
  } // end uListAddTopToFlowSet

  /********** end of utility methods *********/

  // method to initialize refTypeLocals, refTypeInstFields, refTypeInstFieldBases
  // refTypeStaticFields, and refTypeValues
  // those lists contains fields that can/need to be analyzed
  private void initRefTypeLists() {
    // build list of locals
    for (Local l : ((UnitGraph) graph).getBody().getLocals()) {
      Type type = l.getType();
      if (type instanceof RefType || type instanceof ArrayType) {
        refTypeLocals.add(getEquivalentValue(l));
      }
    }

    if (isNotConservative) {
      // build list of fields
      // if the analysis is not conservative (if it is then we will only work on locals)
      for (Unit s : graph) {
        for (ValueBox next : s.getUseBoxes()) {
          initRefTypeLists(next);
        }
        for (ValueBox next : s.getDefBoxes()) {
          initRefTypeLists(next);
        }
      }
    } // end build list of fields

    refTypeValues.addAll(refTypeLocals);
    refTypeValues.addAll(refTypeInstFields);
    refTypeValues.addAll(refTypeStaticFields);

    // logger.debug("Analyzed references: " + refTypeValues);
  } // end initRefTypeLists

  private void initRefTypeLists(ValueBox box) {
    Type opType;
    Value val = box.getValue();
    if (val instanceof InstanceFieldRef) {
      InstanceFieldRef ir = (InstanceFieldRef) val;

      opType = ir.getType();
      if (opType instanceof RefType || opType instanceof ArrayType) {

        EquivalentValue eir = getEquivalentValue(ir);

        if (!refTypeInstFields.contains(eir)) {
          refTypeInstFields.add(eir);

          EquivalentValue eirbase = getEquivalentValue(ir.getBase());
          if (!refTypeInstFieldBases.contains(eirbase)) {
            refTypeInstFieldBases.add(eirbase);
          }
        }
      }
    } else if (val instanceof StaticFieldRef) {
      StaticFieldRef sr = (StaticFieldRef) val;
      opType = sr.getType();

      if (opType instanceof RefType || opType instanceof ArrayType) {

        EquivalentValue esr = getEquivalentValue(sr);

        if (!refTypeStaticFields.contains(esr)) {
          refTypeStaticFields.add(esr);
        }
      }
    }
  }

  private void initUnitSets() {
    for (Unit s : graph) {
      FlowSet<RefIntPair> genSet = emptySet.clone();
      FlowSet<RefIntPair> preSet = fullSet.clone();
      // *** KILL PHASE ***

      // naivity here. kill all the fields after an invoke, i.e. promote them to top
      if (careForMethodCalls && ((Stmt) s).containsInvokeExpr()) {
        uListAddTopToFlowSet(refTypeInstFields, genSet, preSet);
        uListAddTopToFlowSet(refTypeStaticFields, genSet, preSet);
      }
      if (careForAliases && (s instanceof AssignStmt)) {
        Value lhs = ((AssignStmt) s).getLeftOp();
        if (refTypeInstFieldBases.contains(getEquivalentValue(lhs))) {
          // we have a write to a local 'f' and there is a reference to f.x.
          // The LHS will certainly be a local. here, we kill "f.*".
          for (EquivalentValue eifr : refTypeInstFields) {
            InstanceFieldRef ifr = (InstanceFieldRef) eifr.getValue();
            if (ifr.getBase() == lhs) {
              uAddTopToFlowSet(eifr, genSet, preSet);
            }
          }
        }
        if (lhs instanceof InstanceFieldRef) {
          // we have a write to 'f.x', so we'd better kill 'g.x' for all g.
          final String lhsName = ((InstanceFieldRef) lhs).getField().getName();
          for (EquivalentValue eifr : refTypeInstFields) {
            InstanceFieldRef ifr = (InstanceFieldRef) eifr.getValue();
            String name = ifr.getField().getName();
            if (lhsName.equals(name)) {
              uAddTopToFlowSet(eifr, genSet, preSet);
            }
          }
        }
      } // end if (s instanceof AssignStmt)
      // kill rhs of defs
      for (ValueBox box : s.getDefBoxes()) {
        Value val = box.getValue();
        if (isAnalyzedRef(val)) {
          uAddTopToFlowSet(val, genSet, preSet);
        }
      }

      // GENERATION PHASE
      if (s instanceof DefinitionStmt) {
        DefinitionStmt as = (DefinitionStmt) s;
        Value ro = as.getRightOp();
        // take out the cast from "x = (type) y;"
        if (ro instanceof CastExpr) {
          ro = ((CastExpr) ro).getOp();
        }

        Value lo = as.getLeftOp();
        if (isAnalyzedRef(lo)) {
          if (isAlwaysNonNull(ro)) {
            uAddInfoToFlowSet(lo, kNonNull, genSet, preSet);
          } else if (isAlwaysNull(ro)) {
            uAddInfoToFlowSet(lo, kNull, genSet, preSet);
          } else if (isAlwaysTop(ro)) {
            uAddTopToFlowSet(lo, genSet, preSet);
          }
        }
      } // end DefinitionStmt gen case

      HashSet<Value> analyzedChecksSet = new HashSet<Value>(5, 0.7f);
      HashSet<Value> arrayRefChecksSet = new HashSet<Value>(5, 0.7f);
      HashSet<Value> instanceFieldRefChecksSet = new HashSet<Value>(5, 0.7f);
      HashSet<Value> instanceInvokeExprChecksSet = new HashSet<Value>(5, 0.7f);
      HashSet<Value> lengthExprChecksSet = new HashSet<Value>(5, 0.7f);

      // check use and def boxes for dereferencing operations
      // since those operations cause a null pointer check
      // after the statement we know the involved references are non-null
      {
        for (ValueBox next : s.getUseBoxes()) {
          Value base = null;
          Value boxValue = next.getValue();
          if (boxValue instanceof InstanceFieldRef) {
            base = ((InstanceFieldRef) boxValue).getBase();
            instanceFieldRefChecksSet.add(base);
          } else if (boxValue instanceof ArrayRef) {
            base = ((ArrayRef) boxValue).getBase();
            arrayRefChecksSet.add(base);
          } else if (boxValue instanceof InstanceInvokeExpr) {
            base = ((InstanceInvokeExpr) boxValue).getBase();
            instanceInvokeExprChecksSet.add(base);
          } else if (boxValue instanceof LengthExpr) {
            base = ((LengthExpr) boxValue).getOp();
            lengthExprChecksSet.add(base);
          } else if (s instanceof ThrowStmt) {
            base = ((ThrowStmt) s).getOp();
          } else if (s instanceof MonitorStmt) {
            base = ((MonitorStmt) s).getOp();
          }

          if (base != null && isAnalyzedRef(base)) {
            uAddInfoToFlowSet(base, kNonNull, genSet, preSet);
            analyzedChecksSet.add(base);
          }
        }
        for (ValueBox name : s.getDefBoxes()) {
          Value base = null;
          Value boxValue = name.getValue();
          if (boxValue instanceof InstanceFieldRef) {
            base = ((InstanceFieldRef) boxValue).getBase();
            instanceFieldRefChecksSet.add(base);
          } else if (boxValue instanceof ArrayRef) {
            base = ((ArrayRef) boxValue).getBase();
            arrayRefChecksSet.add(base);
          } else if (boxValue instanceof InstanceInvokeExpr) {
            base = ((InstanceInvokeExpr) boxValue).getBase();
            instanceInvokeExprChecksSet.add(base);
          } else if (boxValue instanceof LengthExpr) {
            base = ((LengthExpr) boxValue).getOp();
            lengthExprChecksSet.add(base);
          } else if (s instanceof ThrowStmt) {
            base = ((ThrowStmt) s).getOp();
          } else if (s instanceof MonitorStmt) {
            base = ((MonitorStmt) s).getOp();
          }

          if (base != null && isAnalyzedRef(base)) {
            uAddInfoToFlowSet(base, kNonNull, genSet, preSet);
            analyzedChecksSet.add(base);
          }
        }
      } // done check use and def boxes
      unitToGenerateSet.put(s, genSet);
      unitToPreserveSet.put(s, preSet);
      unitToAnalyzedChecksSet.put(s, analyzedChecksSet);
      unitToArrayRefChecksSet.put(s, arrayRefChecksSet);
      unitToInstanceFieldRefChecksSet.put(s, instanceFieldRefChecksSet);
      unitToInstanceInvokeExprChecksSet.put(s, instanceInvokeExprChecksSet);
      unitToLengthExprChecksSet.put(s, lengthExprChecksSet);
    }
  } // initUnitSets

  @Override
  protected void flowThrough(FlowSet<RefIntPair> in, Unit stmt, List<FlowSet<RefIntPair>> outFall,
      List<FlowSet<RefIntPair>> outBranch) {
    FlowSet<RefIntPair> out = tempFlowSet;
    FlowSet<RefIntPair> pre = unitToPreserveSet.get(stmt);
    FlowSet<RefIntPair> gen = unitToGenerateSet.get(stmt);

    // Perform perservation
    in.intersection(pre, out);

    // Perform generation
    out.union(gen, out);

    // Manually add any x = y; when x and y are both analyzed references
    // these are not sets.
    if (stmt instanceof AssignStmt) {
      AssignStmt as = (AssignStmt) stmt;
      Value rightOp = as.getRightOp();
      // take out the cast from "x = (type) y;"
      if (rightOp instanceof CastExpr) {
        rightOp = ((CastExpr) rightOp).getOp();
      }

      Value leftOp = as.getLeftOp();
      if (isAnalyzedRef(leftOp) && isAnalyzedRef(rightOp)) {
        int roInfo = refInfo(rightOp, in);
        if (roInfo == kTop) {
          uAddTopToFlowSet(leftOp, out);
        } else if (roInfo != kBottom) {
          uAddInfoToFlowSet(leftOp, roInfo, out);
        }
      }
    }

    // Copy the out value to all branch boxes.
    for (FlowSet<RefIntPair> fs : outBranch) {
      copy(out, fs);
    }
    // Copy the out value to the fallthrough box (don't need iterator)
    for (FlowSet<RefIntPair> fs : outFall) {
      copy(out, fs);
    }

    if (isBranched && (stmt instanceof IfStmt)) {
      Value cond = ((IfStmt) stmt).getCondition();
      Value op1 = ((BinopExpr) cond).getOp1();
      Value op2 = ((BinopExpr) cond).getOp2();

      // make sure at least one of the op is a reference being analyzed
      // and that none is a reference that is always Top
      if ((!(isAlwaysTop(op1) || isAlwaysTop(op2))) && (isAnalyzedRef(op1) || isAnalyzedRef(op2))) {
        Value toGen = null;
        int toGenInfo = kBottom;
        {
          final int op1Info = anyRefInfo(op1, in);
          final int op2Info = anyRefInfo(op2, in);
          final boolean op2isKnown = (op2Info == kNull || op2Info == kNonNull);
          if (op1Info == kNull || op1Info == kNonNull) {
            if (!op2isKnown) {
              toGen = op2;
              toGenInfo = op1Info;
            }
          } else if (op2isKnown) {
            toGen = op1;
            toGenInfo = op2Info;
          }
        }

        // only generate info for analyzed references that are top or bottom
        if ((toGen != null) && isAnalyzedRef(toGen)) {
          int fInfo = kBottom;
          int bInfo = kBottom;

          if (cond instanceof EqExpr) {
            // branching mean op1 == op2
            bInfo = toGenInfo;
            if (toGenInfo == kNull) {
              // falling through mean toGen != null
              fInfo = kNonNull;
            }
          } else if (cond instanceof NeExpr) {
            // if we don't branch that mean op1 == op2
            fInfo = toGenInfo;
            if (toGenInfo == kNull) {
              // branching through mean toGen != null
              bInfo = kNonNull;
            }
          } else {
            throw new RuntimeException("invalid condition");
          }

          if (fInfo != kBottom) {
            for (FlowSet<RefIntPair> fs : outFall) {
              copy(out, fs);
              uAddInfoToFlowSet(toGen, fInfo, fs);
            }
          }
          if (bInfo != kBottom) {
            for (FlowSet<RefIntPair> fs : outBranch) {
              copy(out, fs);
              uAddInfoToFlowSet(toGen, bInfo, fs);
            }
          }
        }
      }
    }
  } // end flowThrough

  @Override
  protected void merge(FlowSet<RefIntPair> in1, FlowSet<RefIntPair> in2, FlowSet<RefIntPair> out) {
    // we do that in case out is in1 or in2
    FlowSet<RefIntPair> inSet1Copy = in1.clone();
    FlowSet<RefIntPair> inSet2Copy = in2.clone();

    // first step, set out to the intersection of in1 & in2
    in1.intersection(in2, out);
    // but we are not over, the intersection doesn't handle the top & bottom cases
    for (EquivalentValue r : refTypeValues) {
      int refInfoIn1 = refInfo(r, inSet1Copy);
      int refInfoIn2 = refInfo(r, inSet2Copy);
      if (refInfoIn1 != refInfoIn2) {
        // only process if they are not equal, otherwise the intersection has done its job
        if ((refInfoIn1 == kTop) || (refInfoIn2 == kTop)) {
          // ok, r is top in one of the sets but not the other, make it top in the outSet
          uAddTopToFlowSet(r, out);
        } else if (refInfoIn1 == kBottom) {
          // r is bottom in set1 but not set2, promote to the value in set2
          uAddInfoToFlowSet(r, refInfoIn2, out);
        } else if (refInfoIn2 == kBottom) {
          // r is bottom in set2 but not set1, promote to the value in set1
          uAddInfoToFlowSet(r, refInfoIn1, out);
        } else {
          // r is known in both set, but it's a different value in each set, make it top
          uAddTopToFlowSet(r, out);
        }
      }
    }
  } // end merge

  @Override
  protected void copy(FlowSet<RefIntPair> source, FlowSet<RefIntPair> dest) {
    source.copy(dest);
  } // end copy

  @Override
  protected FlowSet<RefIntPair> newInitialFlow() {
    return emptySet.clone();
  } // end newInitialFlow

  @Override
  protected FlowSet<RefIntPair> entryInitialFlow() {
    return fullSet.clone();
  }

  // try to workaround exception limitation of ForwardBranchedFlowAnalysis
  // this will make for a very conservative analysys when exception handling
  // statements are in the code :-(
  @Override
  public boolean treatTrapHandlersAsEntries() {
    return true;
  }
} // end class BranchedRefVarsAnalysis
