package soot.jimple.toolkits.annotation.arraycheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Body;
import soot.IntType;
import soot.Local;
import soot.SootField;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AddExpr;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.LengthExpr;
import soot.jimple.MulExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JSubExpr;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;

class ArrayIndexLivenessAnalysis extends BackwardFlowAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(ArrayIndexLivenessAnalysis.class);
  HashSet<Local> fullSet = new HashSet<Local>();
  ExceptionalUnitGraph eug;

  /*
   * for each unit, kill set has variables to be killed. gen set was considered with conditionOfGenSet, for example, gen set
   * of unit s are valid only when the condition object. was in the living input set.
   */
  HashMap<Stmt, HashSet<Object>> genOfUnit;
  HashMap<Stmt, HashSet<Value>> absGenOfUnit;
  HashMap<Stmt, HashSet<Value>> killOfUnit;
  HashMap<Stmt, HashSet<Value>> conditionOfGen;

  // s --> a kill all a[?].
  HashMap<DefinitionStmt, Value> killArrayRelated;
  // s --> true
  HashMap<DefinitionStmt, Boolean> killAllArrayRef;

  IntContainer zero = new IntContainer(0);

  private final boolean fieldin;
  HashMap<Object, HashSet<Value>> localToFieldRef, fieldToFieldRef;
  HashSet<Value> allFieldRefs;

  private final boolean arrayin;
  HashMap localToArrayRef;
  HashSet allArrayRefs;

  private final boolean csin;
  HashMap<Value, HashSet<Value>> localToExpr;

  private final boolean rectarray;
  HashSet<Local> multiarraylocals;

  public ArrayIndexLivenessAnalysis(DirectedGraph dg, boolean takeFieldRef, boolean takeArrayRef, boolean takeCSE,
      boolean takeRectArray) {
    super(dg);

    fieldin = takeFieldRef;
    arrayin = takeArrayRef;
    csin = takeCSE;
    rectarray = takeRectArray;

    if (Options.v().debug()) {
      logger.debug("Enter ArrayIndexLivenessAnalysis");
    }

    eug = (ExceptionalUnitGraph) dg;
    retrieveAllArrayLocals(eug.getBody(), fullSet);

    /* compute gen set, kill set, and condition set */
    genOfUnit = new HashMap<Stmt, HashSet<Object>>(eug.size() * 2 + 1);
    absGenOfUnit = new HashMap<Stmt, HashSet<Value>>(eug.size() * 2 + 1);
    killOfUnit = new HashMap<Stmt, HashSet<Value>>(eug.size() * 2 + 1);
    conditionOfGen = new HashMap<Stmt, HashSet<Value>>(eug.size() * 2 + 1);

    if (fieldin) {
      localToFieldRef = new HashMap<Object, HashSet<Value>>();
      fieldToFieldRef = new HashMap<Object, HashSet<Value>>();
      allFieldRefs = new HashSet<Value>();
    }

    if (arrayin) {
      localToArrayRef = new HashMap();
      allArrayRefs = new HashSet();

      killArrayRelated = new HashMap<DefinitionStmt, Value>();
      killAllArrayRef = new HashMap<DefinitionStmt, Boolean>();

      if (rectarray) {
        multiarraylocals = new HashSet<Local>();
        retrieveMultiArrayLocals(eug.getBody(), multiarraylocals);
      }
    }

    if (csin) {
      localToExpr = new HashMap<Value, HashSet<Value>>();
    }

    getAllRelatedMaps(eug.getBody());

    getGenAndKillSet(eug.getBody(), absGenOfUnit, genOfUnit, killOfUnit, conditionOfGen);

    doAnalysis();

    if (Options.v().debug()) {
      logger.debug("Leave ArrayIndexLivenessAnalysis");
    }
  }

  public HashMap<Object, HashSet<Value>> getLocalToFieldRef() {
    return localToFieldRef;
  }

  public HashMap<Object, HashSet<Value>> getFieldToFieldRef() {
    return fieldToFieldRef;
  }

  public HashSet<Value> getAllFieldRefs() {
    return this.allFieldRefs;
  }

  public HashMap getLocalToArrayRef() {
    return localToArrayRef;
  }

  public HashSet getAllArrayRefs() {
    return allArrayRefs;
  }

  public HashMap<Value, HashSet<Value>> getLocalToExpr() {
    return localToExpr;
  }

  public HashSet<Local> getMultiArrayLocals() {
    return multiarraylocals;
  }

  private void getAllRelatedMaps(Body body) {
    Iterator unitIt = body.getUnits().iterator();
    while (unitIt.hasNext()) {
      Stmt stmt = (Stmt) unitIt.next();

      if (csin) {
        if (stmt instanceof DefinitionStmt) {
          Value rhs = ((DefinitionStmt) stmt).getRightOp();

          if (rhs instanceof BinopExpr) {
            Value op1 = ((BinopExpr) rhs).getOp1();
            Value op2 = ((BinopExpr) rhs).getOp2();

            if (rhs instanceof AddExpr) {
              // op1 + op2 --> a + b
              if ((op1 instanceof Local) && (op2 instanceof Local)) {
                HashSet<Value> refs = localToExpr.get(op1);
                if (refs == null) {
                  refs = new HashSet<Value>();
                  localToExpr.put(op1, refs);
                }
                refs.add(rhs);

                refs = localToExpr.get(op2);
                if (refs == null) {
                  refs = new HashSet<Value>();
                  localToExpr.put(op2, refs);
                }
                refs.add(rhs);
              }
            }
            // a * b, a * c, c * a
            else if (rhs instanceof MulExpr) {
              HashSet<Value> refs = localToExpr.get(op1);
              if (refs == null) {
                refs = new HashSet<Value>();
                localToExpr.put(op1, refs);
              }
              refs.add(rhs);

              refs = localToExpr.get(op2);
              if (refs == null) {
                refs = new HashSet<Value>();
                localToExpr.put(op2, refs);
              }
              refs.add(rhs);
            } else if (rhs instanceof SubExpr) {
              if (op2 instanceof Local) {
                HashSet<Value> refs = localToExpr.get(op2);
                if (refs == null) {
                  refs = new HashSet<Value>();
                  localToExpr.put(op2, refs);
                }
                refs.add(rhs);

                if (op1 instanceof Local) {
                  refs = localToExpr.get(op1);
                  if (refs == null) {
                    refs = new HashSet<Value>();
                    localToExpr.put(op1, refs);
                  }
                  refs.add(rhs);
                }
              }
            }
          }
        }
      }

      for (ValueBox vbox : stmt.getUseAndDefBoxes()) {
        Value v = vbox.getValue();

        if (fieldin) {
          if (v instanceof InstanceFieldRef) {
            Value base = ((InstanceFieldRef) v).getBase();
            SootField field = ((InstanceFieldRef) v).getField();

            HashSet<Value> baseset = localToFieldRef.get(base);
            if (baseset == null) {
              baseset = new HashSet<Value>();
              localToFieldRef.put(base, baseset);
            }

            baseset.add(v);

            HashSet<Value> fieldset = fieldToFieldRef.get(field);
            if (fieldset == null) {
              fieldset = new HashSet<Value>();
              fieldToFieldRef.put(field, fieldset);
            }

            fieldset.add(v);
          }

          if (v instanceof FieldRef) {
            allFieldRefs.add(v);
          }
        }

        if (arrayin) {
          // a = ... --> kill all a[x] nodes.
          // a[i] = .. --> kill all array references.
          // m(a) --> kill all array references
          // i = ... --> kill all array reference with index as i
          /*
           * if (v instanceof ArrayRef) { Value base = ((ArrayRef)v).getBase(); Value index = ((ArrayRef)v).getIndex();
           *
           * HashSet refset = (HashSet)localToArrayRef.get(base); if (refset == null) { refset = new HashSet();
           * localToArrayRef.put(base, refset); } refset.add(v);
           *
           * if (index instanceof Local) { refset = (HashSet)localToArrayRef.get(index); if (refset == null) { refset = new
           * HashSet(); localToArrayRef.put(index, refset); }
           *
           * refset.add(v); } allArrayRefs.add(v); }
           */
        }
      }
    }
  }

  private void retrieveAllArrayLocals(Body body, Set<Local> container) {
    for (Local local : body.getLocals()) {
      Type type = local.getType();

      if (type instanceof IntType || type instanceof ArrayType) {
        container.add(local);
      }
    }
  }

  private void retrieveMultiArrayLocals(Body body, Set<Local> container) {
    for (Local local : body.getLocals()) {
      Type type = local.getType();

      if (type instanceof ArrayType) {
        if (((ArrayType) type).numDimensions > 1) {
          this.multiarraylocals.add(local);
        }
      }
    }
  }

  private void getGenAndKillSetForDefnStmt(DefinitionStmt asstmt, HashMap<Stmt, HashSet<Value>> absgen,
      HashSet<Object> genset, HashSet<Value> absgenset, HashSet<Value> killset, HashSet<Value> condset) {
    /* kill left hand side */
    Value lhs = asstmt.getLeftOp();
    Value rhs = asstmt.getRightOp();

    boolean killarrayrelated = false;
    boolean killallarrayref = false;

    if (fieldin) {
      if (lhs instanceof Local) {
        HashSet<Value> related = localToFieldRef.get(lhs);
        if (related != null) {
          killset.addAll(related);
        }
      } else if (lhs instanceof StaticFieldRef) {
        killset.add(lhs);
        condset.add(lhs);
      } else if (lhs instanceof InstanceFieldRef) {
        SootField field = ((InstanceFieldRef) lhs).getField();
        HashSet<Value> related = fieldToFieldRef.get(field);
        if (related != null) {
          killset.addAll(related);
        }
        condset.add(lhs);
      }

      if (asstmt.containsInvokeExpr()) {
        /*
         * Value expr = asstmt.getInvokeExpr(); List parameters = ((InvokeExpr)expr).getArgs();
         *
         * // add the method invocation boolean killall = false; if (expr instanceof InstanceInvokeExpr) killall = true; else
         * { for (int i=0; i<parameters.size(); i++) { Value para = (Value)parameters.get(i); if (para.getType() instanceof
         * RefType) { killall = true; break; } } }
         *
         * if (killall) { killset.addAll(allInstFieldRefs); }
         */

        killset.addAll(allFieldRefs);
      }
    }

    if (arrayin) {
      // a = ... or i = ...
      if (lhs instanceof Local) {
        killarrayrelated = true;
      } else if (lhs instanceof ArrayRef) {
        // a[i] = ...
        killallarrayref = true;
        condset.add(lhs);
      }

      // invokeexpr kills all array references.
      if (asstmt.containsInvokeExpr()) {
        killallarrayref = true;
      }
    }

    if (csin) {
      HashSet<Value> exprs = localToExpr.get(lhs);
      if (exprs != null) {
        killset.addAll(exprs);
      }

      if (rhs instanceof BinopExpr) {
        Value op1 = ((BinopExpr) rhs).getOp1();
        Value op2 = ((BinopExpr) rhs).getOp2();

        if (rhs instanceof AddExpr) {
          if ((op1 instanceof Local) && (op2 instanceof Local)) {
            genset.add(rhs);
          }
        } else if (rhs instanceof MulExpr) {
          if ((op1 instanceof Local) || (op2 instanceof Local)) {
            genset.add(rhs);
          }
        } else if (rhs instanceof SubExpr) {
          if (op2 instanceof Local) {
            genset.add(rhs);
          }
        }
      }
    }

    if ((lhs instanceof Local) && (fullSet.contains(lhs))) {
      killset.add(lhs);
      /* speculatively add lhs as live condition. */
      condset.add(lhs);
    } else if (lhs instanceof ArrayRef) {
      /* a[i] generate a and i. */

      Value base = ((ArrayRef) lhs).getBase();
      Value index = ((ArrayRef) lhs).getIndex();

      absgenset.add(base);

      if (index instanceof Local) {
        absgenset.add(index);
      }
    }

    if (rhs instanceof Local) {
      /* only lhs=rhs is valid. */
      /*
       * if (lhs instanceof Local && fullSet.contains(rhs)) genset.add(rhs);
       */
      if (fullSet.contains(rhs)) {
        genset.add(rhs);
      }

      /*
       * if (fieldin && (lhs instanceof FieldRef)) genset.add(rhs);
       */
    } else if (rhs instanceof FieldRef) {
      if (fieldin) {
        genset.add(rhs);
      }
    } else if (rhs instanceof ArrayRef) {
      /* lhs=a[i]. */

      Value base = ((ArrayRef) rhs).getBase();
      Value index = ((ArrayRef) rhs).getIndex();

      absgenset.add(base);
      if (index instanceof Local) {
        absgenset.add(index);
      }

      if (arrayin) {
        genset.add(rhs);

        if (rectarray) {
          genset.add(Array2ndDimensionSymbol.v(base));
        }
      }
    } else if (rhs instanceof NewArrayExpr) {
      /* a = new A[i]; */
      Value size = ((NewArrayExpr) rhs).getSize();
      if (size instanceof Local) {
        genset.add(size);
      }
    } else if (rhs instanceof NewMultiArrayExpr) {
      /* a = new A[i][]...; */
      /* More precisely, we should track other dimensions. */

      List sizes = ((NewMultiArrayExpr) rhs).getSizes();
      Iterator sizeIt = sizes.iterator();
      while (sizeIt.hasNext()) {
        Value size = (Value) sizeIt.next();

        if (size instanceof Local) {
          genset.add(size);
        }
      }
    } else if (rhs instanceof LengthExpr) {
      /* lhs = lengthof rhs */
      Value op = ((LengthExpr) rhs).getOp();
      genset.add(op);
    } else if (rhs instanceof JAddExpr) {
      /* lhs = rhs+c, lhs=c+rhs */
      Value op1 = ((JAddExpr) rhs).getOp1();
      Value op2 = ((JAddExpr) rhs).getOp2();

      if ((op1 instanceof IntConstant) && (op2 instanceof Local)) {
        genset.add(op2);
      } else if ((op2 instanceof IntConstant) && (op1 instanceof Local)) {
        genset.add(op1);
      }
    } else if (rhs instanceof JSubExpr) {
      Value op1 = ((JSubExpr) rhs).getOp1();
      Value op2 = ((JSubExpr) rhs).getOp2();

      if ((op1 instanceof Local) && (op2 instanceof IntConstant)) {
        genset.add(op1);
      }
    }

    if (arrayin) {
      if (killarrayrelated) {
        killArrayRelated.put(asstmt, lhs);
      }

      if (killallarrayref) {
        killAllArrayRef.put(asstmt, new Boolean(true));
      }
    }
  }

  private void getGenAndKillSet(Body body, HashMap<Stmt, HashSet<Value>> absgen, HashMap<Stmt, HashSet<Object>> gen,
      HashMap<Stmt, HashSet<Value>> kill, HashMap<Stmt, HashSet<Value>> condition) {
    for (Unit u : body.getUnits()) {
      Stmt stmt = (Stmt) u;

      HashSet<Object> genset = new HashSet<Object>();
      HashSet<Value> absgenset = new HashSet<Value>();
      HashSet<Value> killset = new HashSet<Value>();
      HashSet<Value> condset = new HashSet<Value>();

      if (stmt instanceof DefinitionStmt) {
        getGenAndKillSetForDefnStmt((DefinitionStmt) stmt, absgen, genset, absgenset, killset, condset);

      } else if (stmt instanceof IfStmt) {
        /* if one of condition is living, than other one is live. */
        Value cmpcond = ((IfStmt) stmt).getCondition();

        if (cmpcond instanceof ConditionExpr) {
          Value op1 = ((ConditionExpr) cmpcond).getOp1();
          Value op2 = ((ConditionExpr) cmpcond).getOp2();

          if (fullSet.contains(op1) && fullSet.contains(op2)) {
            condset.add(op1);
            condset.add(op2);

            genset.add(op1);
            genset.add(op2);
          }
        }
      }

      if (genset.size() != 0) {
        gen.put(stmt, genset);
      }
      if (absgenset.size() != 0) {
        absgen.put(stmt, absgenset);
      }
      if (killset.size() != 0) {
        kill.put(stmt, killset);
      }
      if (condset.size() != 0) {
        condition.put(stmt, condset);
      }
    }
  }

  /* It is unsafe for normal units. */
  /*
   * Since the initial value is safe, empty set. we do not need to do it again.
   */
  protected Object newInitialFlow() {
    return new HashSet();
  }

  /* It is safe for end units. */
  protected Object entryInitialFlow() {
    return new HashSet();
  }

  protected void flowThrough(Object inValue, Object unit, Object outValue) {
    HashSet inset = (HashSet) inValue;
    HashSet outset = (HashSet) outValue;
    Stmt stmt = (Stmt) unit;

    /* copy in set to out set. */
    outset.clear();
    outset.addAll(inset);

    HashSet<Object> genset = genOfUnit.get(unit);
    HashSet<Value> absgenset = absGenOfUnit.get(unit);
    HashSet<Value> killset = killOfUnit.get(unit);
    HashSet<Value> condset = conditionOfGen.get(unit);

    if (killset != null) {
      outset.removeAll(killset);
    }

    if (arrayin) {
      Boolean killall = killAllArrayRef.get(stmt);

      if ((killall != null) && killall.booleanValue()) {
        List keylist = new ArrayList(outset);
        Iterator keyIt = keylist.iterator();
        while (keyIt.hasNext()) {
          Object key = keyIt.next();
          if (key instanceof ArrayRef) {
            outset.remove(key);
          }
        }
      } else {
        Object local = killArrayRelated.get(stmt);
        if (local != null) {
          List keylist = new ArrayList(outset);
          Iterator keyIt = keylist.iterator();
          while (keyIt.hasNext()) {
            Object key = keyIt.next();
            if (key instanceof ArrayRef) {
              Value base = ((ArrayRef) key).getBase();
              Value index = ((ArrayRef) key).getIndex();

              if (base.equals(local) || index.equals(local)) {
                outset.remove(key);
              }
            }

            if (rectarray) {
              if (key instanceof Array2ndDimensionSymbol) {
                Object base = ((Array2ndDimensionSymbol) key).getVar();
                if (base.equals(local)) {
                  outset.remove(key);
                }
              }
            }
          }
        }
      }
    }

    if (genset != null) {
      if (condset == null || (condset.size() == 0)) {
        outset.addAll(genset);
      } else {
        Iterator condIt = condset.iterator();
        while (condIt.hasNext()) {
          if (inset.contains(condIt.next())) {
            outset.addAll(genset);
            break;
          }
        }
      }
    }

    if (absgenset != null) {
      outset.addAll(absgenset);
    }
  }

  protected void merge(Object in1, Object in2, Object out) {
    HashSet inset1 = (HashSet) in1;
    HashSet inset2 = (HashSet) in2;
    HashSet outset = (HashSet) out;

    HashSet src = inset1;

    if (outset == inset1) {
      src = inset2;
    } else if (outset == inset2) {
      src = inset1;
    } else {
      outset.clear();
      outset.addAll(inset2);
    }

    outset.addAll(src);
  }

  protected void copy(Object source, Object dest) {
    if (source == dest) {
      return;
    }

    HashSet sourceSet = (HashSet) source;
    HashSet destSet = (HashSet) dest;

    destSet.clear();
    destSet.addAll(sourceSet);
  }
}
