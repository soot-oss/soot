package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.AbstractJasminClass;
import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.G;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
import soot.LongType;
import soot.Modifier;
import soot.NullType;
import soot.RefType;
import soot.ShortType;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.StmtAddressType;
import soot.Timers;
import soot.Trap;
import soot.Type;
import soot.TypeSwitch;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.grimp.AbstractGrimpValueSwitch;
import soot.grimp.NewInvokeExpr;
import soot.jimple.internal.StmtBox;
import soot.options.Options;
import soot.tagkit.LineNumberTag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.scalar.FastColorer;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.util.Chain;

/*
 * TODO This is the right JasminClass
 */

/** Methods for producing Jasmin code from Jimple. */
public class JasminClass extends AbstractJasminClass {
  private static final Logger logger = LoggerFactory.getLogger(JasminClass.class);

  void emit(String s, int stackChange) {
    modifyStackHeight(stackChange);
    okayEmit(s);
  }

  void modifyStackHeight(int stackChange) {
    if (currentStackHeight > maxStackHeight) {
      maxStackHeight = currentStackHeight;
    }

    currentStackHeight += stackChange;

    if (currentStackHeight < 0) {
      throw new RuntimeException("Stack height is negative!");
    }

    if (currentStackHeight > maxStackHeight) {
      maxStackHeight = currentStackHeight;
    }
  }

  public JasminClass(SootClass sootClass) {
    super(sootClass);
  }

  @Override
  protected void assignColorsToLocals(Body body) {
    super.assignColorsToLocals(body);

    // Call the graph colorer.
    FastColorer.assignColorsToLocals(body, localToGroup, localToColor, groupToColorCount);

    if (Options.v().time()) {
      Timers.v().packTimer.end();
    }
  }

  @Override
  protected void emitMethodBody(SootMethod method) {
    if (Options.v().time()) {
      Timers.v().buildJasminTimer.end();
    }

    Body activeBody = method.getActiveBody();
    if (!(activeBody instanceof StmtBody)) {
      throw new RuntimeException("method: " + method.getName() + " has an invalid active body!");
    }

    StmtBody body = (StmtBody) activeBody;
    body.validate();

    // if(body == null)

    if (Options.v().time()) {
      Timers.v().buildJasminTimer.start();
    }

    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Performing peephole optimizations...");
    }

    subroutineToReturnAddressSlot = new HashMap<Unit, Integer>(10, 0.7f);
    Chain<Unit> units = body.getUnits();

    // Determine the unitToLabel map
    {
      unitToLabel = new HashMap<Unit, String>(units.size() * 2 + 1, 0.7f);
      labelCount = 0;

      for (UnitBox ubox : body.getUnitBoxes(true)) {
        // Assign a label for each statement reference
        StmtBox box = (StmtBox) ubox;
        if (!unitToLabel.containsKey(box.getUnit())) {
          unitToLabel.put(box.getUnit(), "label" + labelCount++);
        }
      }
    }

    // Emit the exceptions
    for (Trap trap : body.getTraps()) {
      if (trap.getBeginUnit() != trap.getEndUnit()) {
        emit(".catch " + slashify(trap.getException().getName()) + " from " + unitToLabel.get(trap.getBeginUnit()) + " to "
            + unitToLabel.get(trap.getEndUnit()) + " using " + unitToLabel.get(trap.getHandlerUnit()));
      }
    }

    int stackLimitIndex = -1;

    // Determine where the locals go
    {
      int localCount = 0;
      int[] paramSlots = new int[method.getParameterCount()];
      int thisSlot = 0;
      Set<Local> assignedLocals = new HashSet<Local>();
      Map<GroupIntPair, Integer> groupColorPairToSlot =
          new HashMap<GroupIntPair, Integer>(body.getLocalCount() * 2 + 1, 0.7f);

      localToSlot = new HashMap<Local, Integer>(body.getLocalCount() * 2 + 1, 0.7f);

      assignColorsToLocals(body);

      // Determine slots for 'this' and parameters
      {
        if (!method.isStatic()) {
          thisSlot = 0;
          localCount++;
        }

        List<Type> paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.size(); i++) {
          paramSlots[i] = localCount;
          localCount += sizeOfType((Type) paramTypes.get(i));
        }
      }

      // Handle identity statements
      for (Unit u : units) {
        if (u instanceof IdentityStmt) {
          IdentityStmt is = (IdentityStmt) u;
          Value leftOp = is.getLeftOp();
          if (leftOp instanceof Local) {
            int slot = 0;

            IdentityRef identity = (IdentityRef) is.getRightOp();
            if (identity instanceof ThisRef) {
              if (method.isStatic()) {
                throw new RuntimeException("Attempting to use 'this' in static method");
              }
              slot = thisSlot;
            } else if (identity instanceof ParameterRef) {
              slot = paramSlots[((ParameterRef) identity).getIndex()];
            } else {
              // Exception ref. Skip over this
              continue;
            }

            Local l = (Local) leftOp;
            // Make this (group, color) point to the given slot,
            // so that all locals of the same color can be pointed here too
            groupColorPairToSlot.put(new GroupIntPair(localToGroup.get(l), localToColor.get(l)), slot);

            localToSlot.put(l, slot);
            assignedLocals.add(l);
          }
        }
      }

      // Assign the rest of the locals
      {
        for (Local local : body.getLocals()) {
          if (!assignedLocals.contains(local)) {
            int slot;
            GroupIntPair pair = new GroupIntPair(localToGroup.get(local), localToColor.get(local));
            if (groupColorPairToSlot.containsKey(pair)) {
              // This local should share the same slot as the previous local with
              // the same (group, color);
              slot = groupColorPairToSlot.get(pair);
            } else {
              slot = localCount;
              localCount += sizeOfType(local.getType());
              groupColorPairToSlot.put(pair, slot);
            }

            localToSlot.put(local, slot);
            assignedLocals.add(local);
          }
        }
        int modifiers = method.getModifiers();
        if (!Modifier.isNative(modifiers) && !Modifier.isAbstract(modifiers)) {
          emit("    .limit stack ?");
          stackLimitIndex = code.size() - 1;
          emit("    .limit locals " + localCount);
        }
      }
    }

    // let's create a u-d web for the ++ peephole optimization.
    ExceptionalUnitGraph stmtGraph = null;
    LocalDefs ld = null;
    LocalUses lu = null;

    // final boolean enablePeephole = !PhaseOptions.getBoolean(options, "no-peephole");
    final boolean enablePeephole = false;
    if (enablePeephole) {
      stmtGraph = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body);
      ld = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(stmtGraph);
      lu = LocalUses.Factory.newLocalUses(body, ld);
    }

    // Emit code in one pass
    {
      isEmittingMethodCode = true;
      maxStackHeight = 0;
      isNextGotoAJsr = false;

      LOOP: for (Iterator<Unit> codeIt = units.iterator(); codeIt.hasNext();) {
        Stmt s = (Stmt) codeIt.next();

        if (unitToLabel.containsKey(s)) {
          emit(unitToLabel.get(s) + ":");
        }

        if (subroutineToReturnAddressSlot.containsKey(s)) {
          AssignStmt assignStmt = (AssignStmt) s;

          modifyStackHeight(1); // simulate the pushing of address onto the stack by the jsr

          int slot = localToSlot.get(assignStmt.getLeftOp());
          if (slot >= 0 && slot <= 3) {
            emit("astore_" + slot, -1);
          } else {
            emit("astore " + slot, -1);
          }

          // emit("astore " + ( ( Integer ) subroutineToReturnAddressSlot.get( s ) ).intValue() );
        }

        PEEP: if (enablePeephole) {
          // Test for postincrement operators ++ and --
          // We can optimize them further.
          if (!(s instanceof AssignStmt)) {
            break PEEP;
          }

          // sanityCheck: see that we have another statement after s.
          if (!codeIt.hasNext()) {
            break PEEP;
          }

          AssignStmt nextStmt;
          Stmt nextNextStmt;
          {
            Stmt ns = (Stmt) stmtGraph.getSuccsOf(s).get(0);
            if (!(ns instanceof AssignStmt)) {
              break PEEP;
            }
            List<Unit> l = stmtGraph.getSuccsOf(ns);
            if (l.size() != 1) {
              break PEEP;
            }
            nextStmt = (AssignStmt) ns;
            nextNextStmt = (Stmt) l.get(0);
          }

          AssignStmt stmt = (AssignStmt) s;
          final Value lvalue = stmt.getLeftOp();
          final Value rvalue = stmt.getRightOp();

          // we're looking for this pattern:
          // a: <lvalue> = <rvalue>; <rvalue> = <rvalue> +/- 1; use(<lvalue>);
          // b: <lvalue> = <rvalue>; <rvalue> = <lvalue> +/- 1; use(<lvalue>);
          // case a is emitted when rvalue is a local;
          // case b when rvalue is, eg. a field ref.
          //
          // we use structural equality
          // for rvalue & nextStmt.getLeftOp().
          if (!(lvalue instanceof Local) || !nextStmt.getLeftOp().equivTo(rvalue)
              || !(nextStmt.getRightOp() instanceof AddExpr)) {
            break PEEP;
          }

          // make sure that nextNextStmt uses the local exactly once
          {
            boolean foundExactlyOnce = false;
            for (ValueBox box : nextNextStmt.getUseBoxes()) {
              if (box.getValue() == lvalue) {
                if (!foundExactlyOnce) {
                  foundExactlyOnce = true;
                } else {
                  foundExactlyOnce = false;
                  break;
                }
              }
            }
            if (!foundExactlyOnce) {
              break PEEP;
            }
          }

          // Specifically exclude the case where rvalue is on the lhs
          // of nextNextStmt (what a mess!)
          // this takes care of the extremely pathological case where
          // the thing being incremented is also on the lhs of nns (!)
          for (ValueBox box : nextNextStmt.getDefBoxes()) {
            if (box.getValue().equivTo(rvalue)) {
              break PEEP;
            }
          }

          AddExpr addexp = (AddExpr) nextStmt.getRightOp();
          {
            Value op1 = addexp.getOp1();
            if (!op1.equivTo(lvalue) && !op1.equivTo(rvalue)) {
              break PEEP;
            }
            Value op2 = addexp.getOp2();
            if (!(op2 instanceof IntConstant) || ((IntConstant) (op2)).value != 1) {
              break PEEP;
            }
          }

          /* check that we have two uses and that these */
          /* uses live precisely in nextStmt and nextNextStmt */
          /* LocalDefs tells us this: if there was no use, */
          /* there would be no corresponding def. */
          if (addexp.getOp1().equivTo(lvalue)) {
            if (lu.getUsesOf(s).size() != 2 || ld.getDefsOfAt((Local) lvalue, nextStmt).size() != 1
                || ld.getDefsOfAt((Local) lvalue, nextNextStmt).size() != 1) {
              break PEEP;
            }
            plusPlusState = 0;
          } else {
            if (lu.getUsesOf(s).size() != 1 || ld.getDefsOfAt((Local) lvalue, nextNextStmt).size() != 1) {
              break PEEP;
            }
            plusPlusState = 10;
          }

          /* emit dup slot */

          // logger.debug("found ++ instance:");
          // logger.debug(""+s); logger.debug(""+nextStmt);
          // logger.debug(""+nextNextStmt);

          /* this should be redundant, but we do it */
          /* just in case. */
          if (!IntType.v().equals(lvalue.getType())) {
            break PEEP;
          }

          /* our strategy is as follows: eat the */
          /* two incrementing statements, push the lvalue to */
          /* be incremented & its holding local on a */
          /* plusPlusStack and deal with it in */
          /* emitLocal. */

          currentStackHeight = 0;

          /* emit statements as before */
          plusPlusValue = rvalue;
          plusPlusHolder = (Local) lvalue;
          plusPlusIncrementer = nextStmt;

          /* emit new statement with quickness */
          emitStmt(nextNextStmt);

          /* hm. we didn't use local. emit incrementage */
          if (plusPlusHolder != null) {
            emitStmt(s);
            emitStmt(nextStmt);
          }

          if (currentStackHeight != 0) {
            throw new RuntimeException("Stack has height " + currentStackHeight + " after execution of stmt: " + s);
          }
          codeIt.next();
          codeIt.next();
          continue LOOP;
        } // end of peephole opts.

        // emit this statement
        {
          currentStackHeight = 0;
          emitStmt(s);

          if (currentStackHeight != 0) {
            throw new RuntimeException("Stack has height " + currentStackHeight + " after execution of stmt: " + s);
          }
        }
      }

      isEmittingMethodCode = false;

      int modifiers = method.getModifiers();
      if (!Modifier.isNative(modifiers) && !Modifier.isAbstract(modifiers)) {
        code.set(stackLimitIndex, "    .limit stack " + maxStackHeight);
      }
    }
  }

  void emitAssignStmt(AssignStmt stmt) {
    final Value lvalue = stmt.getLeftOp();
    final Value rvalue = stmt.getRightOp();

    // Handle simple subcase where you can use the efficient iinc bytecode
    if (lvalue instanceof Local && (rvalue instanceof AddExpr || rvalue instanceof SubExpr)) {
      Local l = (Local) lvalue;
      BinopExpr expr = (BinopExpr) rvalue;
      Value op1 = expr.getOp1();
      Value op2 = expr.getOp2();

      // more peephole stuff.
      if (lvalue == plusPlusHolder) {
        emitValue(lvalue);
        plusPlusHolder = null;
        plusPlusState = 0;
      }
      // end of peephole

      if (IntType.v().equals(l.getType())) {
        boolean isValidCase = false;
        int x = 0;

        if (op1 == l && op2 instanceof IntConstant) {
          x = ((IntConstant) op2).value;
          isValidCase = true;
        } else if (expr instanceof AddExpr && op2 == l && op1 instanceof IntConstant) {
          // Note expr can't be a SubExpr because that would be x = 3 - x

          x = ((IntConstant) op1).value;
          isValidCase = true;
        }

        if (isValidCase && x >= Short.MIN_VALUE && x <= Short.MAX_VALUE) {
          emit("iinc " + localToSlot.get(l) + " " + ((expr instanceof AddExpr) ? x : -x), 0);
          return;
        }
      }
    }

    lvalue.apply(new AbstractJimpleValueSwitch<Object>() {
      @Override
      public void caseArrayRef(ArrayRef v) {
        emitValue(v.getBase());
        emitValue(v.getIndex());
        emitValue(rvalue);

        v.getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseArrayType(ArrayType t) {
            emit("aastore", -3);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dastore", -4);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fastore", -3);
          }

          @Override
          public void caseIntType(IntType t) {
            emit("iastore", -3);
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lastore", -4);
          }

          @Override
          public void caseRefType(RefType t) {
            emit("aastore", -3);
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("bastore", -3);
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("bastore", -3);
          }

          @Override
          public void caseCharType(CharType t) {
            emit("castore", -3);
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("sastore", -3);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid type: " + t);
          }
        });
      }

      @Override
      public void caseInstanceFieldRef(InstanceFieldRef v) {
        emitValue(v.getBase());
        emitValue(rvalue);

        emit("putfield " + slashify(v.getFieldRef().declaringClass().getName()) + '/' + v.getFieldRef().name() + ' '
            + jasminDescriptorOf(v.getFieldRef().type()), -1 + -sizeOfType(v.getFieldRef().type()));
      }

      @Override
      public void caseLocal(final Local v) {
        final int slot = localToSlot.get(v);

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntegerType(IntegerType t) {
            emitValue(rvalue);
            if (slot >= 0 && slot <= 3) {
              emit("istore_" + slot, -1);
            } else {
              emit("istore " + slot, -1);
            }
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntegerType(t);
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntegerType(t);
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntegerType(t);
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntegerType(t);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntegerType(t);
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emitValue(rvalue);
            if (slot >= 0 && slot <= 3) {
              emit("astore_" + slot, -1);
            } else {
              emit("astore " + slot, -1);
            }
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emitValue(rvalue);
            if (slot >= 0 && slot <= 3) {
              emit("dstore_" + slot, -2);
            } else {
              emit("dstore " + slot, -2);
            }
          }

          @Override
          public void caseFloatType(FloatType t) {
            emitValue(rvalue);
            if (slot >= 0 && slot <= 3) {
              emit("fstore_" + slot, -1);
            } else {
              emit("fstore " + slot, -1);
            }
          }

          @Override
          public void caseLongType(LongType t) {
            emitValue(rvalue);
            if (slot >= 0 && slot <= 3) {
              emit("lstore_" + slot, -2);
            } else {
              emit("lstore " + slot, -2);
            }
          }

          @Override
          public void caseRefType(RefType t) {
            emitValue(rvalue);
            if (slot >= 0 && slot <= 3) {
              emit("astore_" + slot, -1);
            } else {
              emit("astore " + slot, -1);
            }
          }

          @Override
          public void caseStmtAddressType(StmtAddressType t) {
            isNextGotoAJsr = true;
            returnAddressSlot = slot;
            /*
             * if ( slot >= 0 && slot <= 3) emit("astore_" + slot, ); else emit("astore " + slot, );
             */
          }

          @Override
          public void caseNullType(NullType t) {
            emitValue(rvalue);
            if (slot >= 0 && slot <= 3) {
              emit("astore_" + slot, -1);
            } else {
              emit("astore " + slot, -1);
            }
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid local type: " + t);
          }
        });
      }

      @Override
      public void caseStaticFieldRef(StaticFieldRef v) {
        SootFieldRef field = v.getFieldRef();

        emitValue(rvalue);
        emit("putstatic " + slashify(field.declaringClass().getName()) + '/' + field.name() + ' '
            + jasminDescriptorOf(field.type()), -sizeOfType(v.getFieldRef().type()));
      }
    });
  }

  void emitIfStmt(IfStmt stmt) {
    Value cond = stmt.getCondition();

    final Value op1 = ((BinopExpr) cond).getOp1();
    final Value op2 = ((BinopExpr) cond).getOp2();
    final String label = unitToLabel.get(stmt.getTarget());

    // Handle simple subcase where op1 is null
    if (op2 instanceof NullConstant || op1 instanceof NullConstant) {
      if (op2 instanceof NullConstant) {
        emitValue(op1);
      } else {
        emitValue(op2);
      }

      if (cond instanceof EqExpr) {
        emit("ifnull " + label, -1);
      } else if (cond instanceof NeExpr) {
        emit("ifnonnull " + label, -1);
      } else {
        throw new RuntimeException("invalid condition");
      }
      return;
    }

    // Handle simple subcase where op2 is 0
    if (op2 instanceof IntConstant && ((IntConstant) op2).value == 0) {
      emitValue(op1);

      cond.apply(new AbstractJimpleValueSwitch<Object>() {
        @Override
        public void caseEqExpr(EqExpr expr) {
          emit("ifeq " + label, -1);
        }

        @Override
        public void caseNeExpr(NeExpr expr) {
          emit("ifne " + label, -1);
        }

        @Override
        public void caseLtExpr(LtExpr expr) {
          emit("iflt " + label, -1);
        }

        @Override
        public void caseLeExpr(LeExpr expr) {
          emit("ifle " + label, -1);
        }

        @Override
        public void caseGtExpr(GtExpr expr) {
          emit("ifgt " + label, -1);
        }

        @Override
        public void caseGeExpr(GeExpr expr) {
          emit("ifge " + label, -1);
        }
      });

      return;
    }

    // Handle simple subcase where op1 is 0 (flip directions)
    if (op1 instanceof IntConstant && ((IntConstant) op1).value == 0) {
      emitValue(op2);

      cond.apply(new AbstractJimpleValueSwitch<Object>() {
        @Override
        public void caseEqExpr(EqExpr expr) {
          emit("ifeq " + label, -1);
        }

        @Override
        public void caseNeExpr(NeExpr expr) {
          emit("ifne " + label, -1);
        }

        @Override
        public void caseLtExpr(LtExpr expr) {
          emit("ifgt " + label, -1);
        }

        @Override
        public void caseLeExpr(LeExpr expr) {
          emit("ifge " + label, -1);
        }

        @Override
        public void caseGtExpr(GtExpr expr) {
          emit("iflt " + label, -1);
        }

        @Override
        public void caseGeExpr(GeExpr expr) {
          emit("ifle " + label, -1);
        }
      });

      return;
    }

    emitValue(op1);
    emitValue(op2);

    cond.apply(new AbstractJimpleValueSwitch<Object>() {
      @Override
      public void caseEqExpr(EqExpr expr) {
        op1.getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmpeq " + label, -2);
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmpeq " + label, -2);
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmpeq " + label, -2);
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmpeq " + label, -2);
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmpeq " + label, -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emit("ifeq " + label, -1);
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emit("ifeq " + label, -1);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emit("ifeq " + label, -1);
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emit("if_acmpeq " + label, -2);
          }

          @Override
          public void caseRefType(RefType t) {
            emit("if_acmpeq " + label, -2);
          }

          @Override
          public void caseNullType(NullType t) {
            emit("if_acmpeq " + label, -2);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseNeExpr(NeExpr expr) {
        op1.getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmpne " + label, -2);
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmpne " + label, -2);
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmpne " + label, -2);
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmpne " + label, -2);
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmpne " + label, -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emit("ifne " + label, -1);
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emit("ifne " + label, -1);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emit("ifne " + label, -1);
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emit("if_acmpne " + label, -2);
          }

          @Override
          public void caseRefType(RefType t) {
            emit("if_acmpne " + label, -2);
          }

          @Override
          public void caseNullType(NullType t) {
            emit("if_acmpne " + label, -2);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type for NeExpr: " + t);
          }
        });
      }

      @Override
      public void caseLtExpr(LtExpr expr) {
        op1.getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmplt " + label, -2);
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmplt " + label, -2);
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmplt " + label, -2);
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmplt " + label, -2);
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmplt " + label, -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emit("iflt " + label, -1);
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emit("iflt " + label, -1);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emit("iflt " + label, -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseLeExpr(LeExpr expr) {
        op1.getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmple " + label, -2);
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmple " + label, -2);
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmple " + label, -2);
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmple " + label, -2);
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmple " + label, -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emit("ifle " + label, -1);
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emit("ifle " + label, -1);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emit("ifle " + label, -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseGtExpr(GtExpr expr) {
        op1.getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmpgt " + label, -2);
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmpgt " + label, -2);
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmpgt " + label, -2);
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmpgt " + label, -2);
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmpgt " + label, -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emit("ifgt " + label, -1);
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emit("ifgt " + label, -1);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emit("ifgt " + label, -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseGeExpr(GeExpr expr) {
        op1.getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmpge " + label, -2);
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmpge " + label, -2);
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmpge " + label, -2);
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmpge " + label, -2);
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmpge " + label, -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emit("ifge " + label, -1);
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emit("ifge " + label, -1);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emit("ifge " + label, -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }
    });
  }

  void emitStmt(Stmt stmt) {
    LineNumberTag lnTag = (LineNumberTag) stmt.getTag(LineNumberTag.NAME);
    if (lnTag != null) {
      emit(".line " + lnTag.getLineNumber());
    }
    stmt.apply(new AbstractStmtSwitch<Object>() {
      @Override
      public void caseAssignStmt(AssignStmt s) {
        emitAssignStmt(s);
      }

      @Override
      public void caseIdentityStmt(IdentityStmt s) {
        if (s.getRightOp() instanceof CaughtExceptionRef) {
          Value leftOp = s.getLeftOp();
          if (leftOp instanceof Local) {
            // simulate the pushing of the exception onto the stack by the jvm
            modifyStackHeight(1);

            int slot = localToSlot.get((Local) leftOp);
            if (slot >= 0 && slot <= 3) {
              emit("astore_" + slot, -1);
            } else {
              emit("astore " + slot, -1);
            }
          }
        }
      }

      @Override
      public void caseBreakpointStmt(BreakpointStmt s) {
        emit("breakpoint", 0);
      }

      @Override
      public void caseInvokeStmt(InvokeStmt s) {
        emitValue(s.getInvokeExpr());

        Type returnType = ((InvokeExpr) s.getInvokeExpr()).getMethodRef().returnType();
        if (!VoidType.v().equals(returnType)) {
          // Need to do some cleanup because this value is not used.
          if (sizeOfType(returnType) == 1) {
            emit("pop", -1);
          } else {
            emit("pop2", -2);
          }
        }
      }

      @Override
      public void caseEnterMonitorStmt(EnterMonitorStmt s) {
        emitValue(s.getOp());
        emit("monitorenter", -1);
      }

      @Override
      public void caseExitMonitorStmt(ExitMonitorStmt s) {
        emitValue(s.getOp());
        emit("monitorexit", -1);
      }

      @Override
      public void caseGotoStmt(GotoStmt s) {
        if (isNextGotoAJsr) {
          emit("jsr " + unitToLabel.get(s.getTarget()));
          isNextGotoAJsr = false;

          subroutineToReturnAddressSlot.put(s.getTarget(), returnAddressSlot);
        } else {
          emit("goto " + unitToLabel.get(s.getTarget()));
        }
      }

      @Override
      public void caseIfStmt(IfStmt s) {
        emitIfStmt(s);
      }

      @Override
      public void caseLookupSwitchStmt(LookupSwitchStmt s) {
        emitValue(s.getKey());
        emit("lookupswitch", -1);

        List<Unit> targets = s.getTargets();
        List<IntConstant> lookupValues = s.getLookupValues();
        for (int i = 0; i < lookupValues.size(); i++) {
          emit("  " + lookupValues.get(i) + " : " + unitToLabel.get(targets.get(i)));
        }

        emit("  default : " + unitToLabel.get(s.getDefaultTarget()));
      }

      @Override
      public void caseNopStmt(NopStmt s) {
        emit("nop", 0);
      }

      @Override
      public void caseRetStmt(RetStmt s) {
        emit("ret " + localToSlot.get(s.getStmtAddress()), 0);
      }

      @Override
      public void caseReturnStmt(ReturnStmt s) {
        Value returnValue = s.getOp();
        emitValue(returnValue);

        returnValue.getType().apply(new TypeSwitch<Object>() {
          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid return type " + t.toString());
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dreturn", -2);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("freturn", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            emit("ireturn", -1);
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("ireturn", -1);
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("ireturn", -1);
          }

          @Override
          public void caseCharType(CharType t) {
            emit("ireturn", -1);
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("ireturn", -1);
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lreturn", -2);
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emit("areturn", -1);
          }

          @Override
          public void caseRefType(RefType t) {
            emit("areturn", -1);
          }

          @Override
          public void caseNullType(NullType t) {
            emit("areturn", -1);
          }
        });
      }

      @Override
      public void caseReturnVoidStmt(ReturnVoidStmt s) {
        emit("return", 0);
      }

      @Override
      public void caseTableSwitchStmt(TableSwitchStmt s) {
        emitValue(s.getKey());
        emit("tableswitch " + s.getLowIndex() + " ; high = " + s.getHighIndex(), -1);

        for (Unit t : s.getTargets()) {
          emit("  " + unitToLabel.get(t));
        }

        emit("default : " + unitToLabel.get(s.getDefaultTarget()));
      }

      @Override
      public void caseThrowStmt(ThrowStmt s) {
        emitValue(s.getOp());
        emit("athrow", -1);
      }
    });
  }

  /* try to pre-duplicate a local and fix-up its dup_xn parameter. */
  /* if we find that we're unable to proceed, we swap the dup_xn */
  /* for a store pl, load pl combination */
  Value plusPlusValue;
  Local plusPlusHolder;
  int plusPlusState;
  int plusPlusPlace;
  int plusPlusHeight;
  Stmt plusPlusIncrementer;
  /* end of plusplus stuff. */

  void emitLocal(final Local v) {
    final int slot = localToSlot.get(v);

    v.getType().apply(new TypeSwitch<Object>() {
      @Override
      public void caseArrayType(ArrayType t) {
        if (slot >= 0 && slot <= 3) {
          emit("aload_" + slot, 1);
        } else {
          emit("aload " + slot, 1);
        }
      }

      @Override
      public void defaultCase(Type t) {
        throw new RuntimeException("invalid local type to load" + t);
      }

      @Override
      public void caseDoubleType(DoubleType t) {
        if (slot >= 0 && slot <= 3) {
          emit("dload_" + slot, 2);
        } else {
          emit("dload " + slot, 2);
        }
      }

      @Override
      public void caseFloatType(FloatType t) {
        if (slot >= 0 && slot <= 3) {
          emit("fload_" + slot, 1);
        } else {
          emit("fload " + slot, 1);
        }
      }

      // add boolean, byte, short, and char type
      @Override
      public void caseBooleanType(BooleanType t) {
        handleIntegerType(t);
      }

      @Override
      public void caseByteType(ByteType t) {
        handleIntegerType(t);
      }

      @Override
      public void caseShortType(ShortType t) {
        handleIntegerType(t);
      }

      @Override
      public void caseCharType(CharType t) {
        handleIntegerType(t);
      }

      @Override
      public void caseIntType(IntType t) {
        handleIntegerType(t);
      }

      // peephole stuff appears here.
      public void handleIntegerType(IntegerType t) {
        if (v.equals(plusPlusHolder)) {
          switch (plusPlusState) {
            case 0: {
              // ok, we're called upon to emit the
              // ++ target, whatever it was.

              // now we need to emit a statement incrementing
              // the correct value.
              // actually, just remember the local to be incremented.

              // here ppi is of the form ppv = pph + 1

              plusPlusState = 1;

              emitStmt(plusPlusIncrementer);
              int diff = plusPlusHeight - currentStackHeight + 1;
              if (diff > 0) {
                code.set(plusPlusPlace, "    dup_x" + diff);
              }
              plusPlusHolder = null;

              // afterwards we have the value on the stack.
              return;
            }
            case 1:
              plusPlusHeight = currentStackHeight;
              plusPlusHolder = null;

              emitValue(plusPlusValue);

              plusPlusPlace = code.size();
              emit("dup", 1);

              return;
            case 10: {
              // this time we have ppi of the form ppv = ppv + 1
              plusPlusState = 11;

              // logger.debug("ppV "+plusPlusValue);
              // logger.debug("ppH "+plusPlusHolder);
              // logger.debug("ppI "+plusPlusIncrementer);

              plusPlusHolder = (Local) plusPlusValue;
              emitStmt(plusPlusIncrementer);
              int diff = plusPlusHeight - currentStackHeight + 1;
              if (diff > 0 && plusPlusState == 11) {
                code.set(plusPlusPlace, "    dup_x" + diff);
              }
              plusPlusHolder = null;

              // afterwards we have the value on the stack.
              return;
            }
            case 11:
              plusPlusHeight = currentStackHeight;
              plusPlusHolder = null;

              emitValue(plusPlusValue);
              if (plusPlusState != 11) {
                emit("dup", 1);
              }

              plusPlusPlace = code.size();

              return;
          }
        }
        if (slot >= 0 && slot <= 3) {
          emit("iload_" + slot, 1);
        } else {
          emit("iload " + slot, 1);
        }
      }
      // end of peephole stuff.

      @Override
      public void caseLongType(LongType t) {
        if (slot >= 0 && slot <= 3) {
          emit("lload_" + slot, 2);
        } else {
          emit("lload " + slot, 2);
        }
      }

      @Override
      public void caseRefType(RefType t) {
        if (slot >= 0 && slot <= 3) {
          emit("aload_" + slot, 1);
        } else {
          emit("aload " + slot, 1);
        }
      }

      @Override
      public void caseNullType(NullType t) {
        if (slot >= 0 && slot <= 3) {
          emit("aload_" + slot, 1);
        } else {
          emit("aload " + slot, 1);
        }
      }
    });
  }

  void emitValue(Value value) {
    value.apply(new AbstractGrimpValueSwitch<Object>() {
      @Override
      public void caseAddExpr(AddExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("iadd", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("ladd", -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dadd", -2);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fadd", -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for add");
          }
        });
      }

      @Override
      public void caseAndExpr(AndExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("iand", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("land", -2);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for and");
          }
        });
      }

      @Override
      public void caseArrayRef(ArrayRef v) {
        emitValue(v.getBase());
        emitValue(v.getIndex());

        v.getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseArrayType(ArrayType ty) {
            emit("aaload", -1);
          }

          @Override
          public void caseBooleanType(BooleanType ty) {
            emit("baload", -1);
          }

          @Override
          public void caseByteType(ByteType ty) {
            emit("baload", -1);
          }

          @Override
          public void caseCharType(CharType ty) {
            emit("caload", -1);
          }

          @Override
          public void defaultCase(Type ty) {
            throw new RuntimeException("invalid base type");
          }

          @Override
          public void caseDoubleType(DoubleType ty) {
            emit("daload", 0);
          }

          @Override
          public void caseFloatType(FloatType ty) {
            emit("faload", -1);
          }

          @Override
          public void caseIntType(IntType ty) {
            emit("iaload", -1);
          }

          @Override
          public void caseLongType(LongType ty) {
            emit("laload", 0);
          }

          @Override
          public void caseNullType(NullType ty) {
            emit("aaload", -1);
          }

          @Override
          public void caseRefType(RefType ty) {
            emit("aaload", -1);
          }

          @Override
          public void caseShortType(ShortType ty) {
            emit("saload", -1);
          }
        });
      }

      @Override
      public void caseCastExpr(final CastExpr v) {
        final Value op = v.getOp();
        emitValue(op);

        final Type toType = v.getCastType();
        if (toType instanceof RefType) {
          emit("checkcast " + slashify(toType.toString()), 0);
        } else if (toType instanceof ArrayType) {
          emit("checkcast " + jasminDescriptorOf(toType), 0);
        } else {
          op.getType().apply(new TypeSwitch<Object>() {
            @Override
            public void defaultCase(Type ty) {
              throw new RuntimeException("invalid fromType " + op.getType());
            }

            @Override
            public void caseDoubleType(DoubleType ty) {
              if (IntType.v().equals(toType)) {
                emit("d2i", -1);
              } else if (LongType.v().equals(toType)) {
                emit("d2l", 0);
              } else if (FloatType.v().equals(toType)) {
                emit("d2f", -1);
              } else {
                throw new RuntimeException("invalid toType from double: " + toType);
              }
            }

            @Override
            public void caseFloatType(FloatType ty) {
              if (IntType.v().equals(toType)) {
                emit("f2i", 0);
              } else if (LongType.v().equals(toType)) {
                emit("f2l", 1);
              } else if (DoubleType.v().equals(toType)) {
                emit("f2d", 1);
              } else {
                throw new RuntimeException("invalid toType from float: " + toType);
              }
            }

            @Override
            public void caseIntType(IntType ty) {
              emitIntToTypeCast();
            }

            @Override
            public void caseBooleanType(BooleanType ty) {
              emitIntToTypeCast();
            }

            @Override
            public void caseByteType(ByteType ty) {
              emitIntToTypeCast();
            }

            @Override
            public void caseCharType(CharType ty) {
              emitIntToTypeCast();
            }

            @Override
            public void caseShortType(ShortType ty) {
              emitIntToTypeCast();
            }

            private void emitIntToTypeCast() {
              if (ByteType.v().equals(toType)) {
                emit("i2b", 0);
              } else if (CharType.v().equals(toType)) {
                emit("i2c", 0);
              } else if (ShortType.v().equals(toType)) {
                emit("i2s", 0);
              } else if (FloatType.v().equals(toType)) {
                emit("i2f", 0);
              } else if (LongType.v().equals(toType)) {
                emit("i2l", 1);
              } else if (DoubleType.v().equals(toType)) {
                emit("i2d", 1);
              } else if (IntType.v().equals(toType)) {
                // this shouldn't happen?
              } else if (BooleanType.v().equals(toType)) {
                // intentionally empty
              } else {
                throw new RuntimeException("invalid toType from int: " + toType + " " + v.toString());
              }
            }

            @Override
            public void caseLongType(LongType ty) {
              if (IntType.v().equals(toType)) {
                emit("l2i", -1);
              } else if (FloatType.v().equals(toType)) {
                emit("l2f", -1);
              } else if (DoubleType.v().equals(toType)) {
                emit("l2d", 0);
              } else if (ByteType.v().equals(toType)) {
                emit("l2i", -1);
                emitIntToTypeCast();
              } else if (ShortType.v().equals(toType)) {
                emit("l2i", -1);
                emitIntToTypeCast();
              } else if (CharType.v().equals(toType)) {
                emit("l2i", -1);
                emitIntToTypeCast();
              } else if (BooleanType.v().equals(toType)) {
                emit("l2i", -1);
                emitIntToTypeCast();
              } else {
                throw new RuntimeException("invalid toType from long: " + toType);
              }
            }
          });
        }
      }

      @Override
      public void caseCmpExpr(CmpExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());
        emit("lcmp", -3);
      }

      @Override
      public void caseCmpgExpr(CmpgExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        if (FloatType.v().equals(v.getOp1().getType())) {
          emit("fcmpg", -1);
        } else {
          emit("dcmpg", -3);
        }
      }

      @Override
      public void caseCmplExpr(CmplExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        if (FloatType.v().equals(v.getOp1().getType())) {
          emit("fcmpl", -1);
        } else {
          emit("dcmpl", -3);
        }
      }

      @Override
      public void caseDivExpr(DivExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("idiv", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("ldiv", -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("ddiv", -2);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fdiv", -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for div");
          }
        });
      }

      @Override
      public void caseDoubleConstant(DoubleConstant v) {
        double val = v.value;
        if ((val == 0) && ((1.0 / val) > 0.0)) {
          emit("dconst_0", 2);
        } else if (val == 1) {
          emit("dconst_1", 2);
        } else {
          emit("ldc2_w " + doubleToString(v), 2);
        }
      }

      @Override
      public void caseFloatConstant(FloatConstant v) {
        float val = v.value;
        if ((val == 0) && ((1.0f / val) > 0.0f)) {
          emit("fconst_0", 1);
        } else if (val == 1) {
          emit("fconst_1", 1);
        } else if (val == 2) {
          emit("fconst_2", 1);
        } else {
          emit("ldc " + floatToString(v), 1);
        }
      }

      @Override
      public void caseInstanceFieldRef(InstanceFieldRef v) {
        emitValue(v.getBase());
        SootFieldRef field = v.getFieldRef();
        emit("getfield " + slashify(field.declaringClass().getName()) + '/' + field.name() + ' '
            + jasminDescriptorOf(field.type()), -1 + sizeOfType(field.type()));
      }

      @Override
      public void caseInstanceOfExpr(InstanceOfExpr v) {
        emitValue(v.getOp());

        Type checkType = v.getCheckType();
        if (checkType instanceof RefType) {
          emit("instanceof " + slashify(checkType.toString()), 0);
        } else if (checkType instanceof ArrayType) {
          emit("instanceof " + jasminDescriptorOf(checkType), 0);
        }
      }

      @Override
      public void caseIntConstant(IntConstant v) {
        int val = v.value;
        if (val == -1) {
          emit("iconst_m1", 1);
        } else if (val >= 0 && val <= 5) {
          emit("iconst_" + val, 1);
        } else if (val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE) {
          emit("bipush " + val, 1);
        } else if (val >= Short.MIN_VALUE && val <= Short.MAX_VALUE) {
          emit("sipush " + val, 1);
        } else {
          emit("ldc " + v.toString(), 1);
        }
      }

      @Override
      public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
        emitValue(v.getBase());

        SootMethodRef m = v.getMethodRef();
        for (int i = 0; i < m.parameterTypes().size(); i++) {
          emitValue(v.getArg(i));
        }

        emit("invokeinterface " + slashify(m.declaringClass().getName()) + "/" + m.name() + jasminDescriptorOf(m) + " "
            + (argCountOf(m) + 1), -(argCountOf(m) + 1) + sizeOfType(m.returnType()));
      }

      @Override
      public void caseLengthExpr(LengthExpr v) {
        emitValue(v.getOp());
        emit("arraylength", 0);
      }

      @Override
      public void caseLocal(Local v) {
        emitLocal(v);
      }

      @Override
      public void caseLongConstant(LongConstant v) {
        long val = v.value;
        if (val == 0) {
          emit("lconst_0", 2);
        } else if (val == 1) {
          emit("lconst_1", 2);
        } else {
          emit("ldc2_w " + v.toString(), 2);
        }
      }

      @Override
      public void caseMulExpr(MulExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("imul", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lmul", -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dmul", -2);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fmul", -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for mul");
          }
        });
      }

      @Override
      public void caseLtExpr(LtExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getOp1().getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emitBooleanBranch("iflt");
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emitBooleanBranch("iflt");
          }

          private void handleIntCase() {
            emit("if_icmplt", -2);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emitBooleanBranch("iflt");
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseLeExpr(LeExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getOp1().getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emitBooleanBranch("ifle");
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emitBooleanBranch("ifle");
          }

          private void handleIntCase() {
            emit("if_icmple", -2);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emitBooleanBranch("ifle");
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseGtExpr(GtExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getOp1().getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emitBooleanBranch("ifgt");
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emitBooleanBranch("ifgt");
          }

          private void handleIntCase() {
            emit("if_icmpgt", -2);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emitBooleanBranch("ifgt");
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseGeExpr(GeExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getOp1().getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emitBooleanBranch("ifge");
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emitBooleanBranch("ifge");
          }

          private void handleIntCase() {
            emit("if_icmpge", -2);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emitBooleanBranch("ifge");
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseNeExpr(NeExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getOp1().getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emit("iconst_0", 1);
            emitBooleanBranch("if_icmpne");
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -1);
            emit("iconst_0", 1);
            emitBooleanBranch("if_icmpne");
          }

          private void handleIntCase() {
            emit("if_icmpne", -2);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emit("iconst_0", 1);
            emitBooleanBranch("if_icmpne");
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emitBooleanBranch("if_acmpne");
          }

          @Override
          public void caseRefType(RefType t) {
            emitBooleanBranch("if_acmpne");
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseEqExpr(EqExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getOp1().getType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg", -3);
            emit("iconst_0", 1);
            emitBooleanBranch("if_icmpeq");
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg", -3);
            emit("iconst_0", 1);
            emitBooleanBranch("if_icmpeq");
          }

          private void handleIntCase() {
            emit("if_icmpeq", -2);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp", -3);
            emit("iconst_0", 1);
            emitBooleanBranch("if_icmpeq");
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emitBooleanBranch("if_acmpeq");
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseNegExpr(final NegExpr v) {
        emitValue(v.getOp());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("ineg", 0);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lneg", 0);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dneg", 0);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fneg", 0);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for neg: " + t + ": " + v);
          }
        });
      }

      @Override
      public void caseNewArrayExpr(NewArrayExpr v) {
        emitValue(v.getSize());

        Type baseType = v.getBaseType();
        if (baseType instanceof RefType) {
          emit("anewarray " + slashify(baseType.toString()), 0);
        } else if (baseType instanceof ArrayType) {
          emit("anewarray " + jasminDescriptorOf(baseType), 0);
        } else {
          emit("newarray " + baseType.toString(), 0);
        }
      }

      @Override
      public void caseNewMultiArrayExpr(NewMultiArrayExpr v) {
        for (Value val : v.getSizes()) {
          emitValue(val);
        }

        int size = v.getSizeCount();
        emit("multianewarray " + jasminDescriptorOf(v.getBaseType()) + " " + size, -size + 1);
      }

      @Override
      public void caseNewExpr(NewExpr v) {
        emit("new " + slashify(v.getBaseType().toString()), 1);
      }

      @Override
      public void caseNewInvokeExpr(NewInvokeExpr v) {
        emit("new " + slashify(v.getBaseType().toString()), 1);
        emit("dup", 1);

        // emitValue(v.getBase());
        // already on the stack

        SootMethodRef m = v.getMethodRef();
        for (int i = 0; i < m.parameterTypes().size(); i++) {
          emitValue(v.getArg(i));
        }

        emit("invokespecial " + slashify(m.declaringClass().getName()) + "/" + m.name() + jasminDescriptorOf(m),
            -(argCountOf(m) + 1) + sizeOfType(m.returnType()));
      }

      @Override
      public void caseNullConstant(NullConstant v) {
        emit("aconst_null", 1);
      }

      @Override
      public void caseOrExpr(OrExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("ior", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lor", -2);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for or");
          }
        });
      }

      @Override
      public void caseRemExpr(RemExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("irem", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lrem", -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("drem", -2);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("frem", -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for rem");
          }
        });
      }

      @Override
      public void caseShlExpr(ShlExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("ishl", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lshl", -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for shl");
          }
        });
      }

      @Override
      public void caseShrExpr(ShrExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("ishr", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lshr", -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for shr");
          }
        });
      }

      @Override
      public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
        emitValue(v.getBase());

        SootMethodRef m = v.getMethodRef();
        for (int i = 0; i < m.parameterTypes().size(); i++) {
          emitValue(v.getArg(i));
        }

        emit("invokespecial " + slashify(m.declaringClass().getName()) + "/" + m.name() + jasminDescriptorOf(m),
            -(argCountOf(m) + 1) + sizeOfType(m.returnType()));
      }

      @Override
      public void caseStaticInvokeExpr(StaticInvokeExpr v) {
        SootMethodRef m = v.getMethodRef();
        for (int i = 0; i < m.parameterTypes().size(); i++) {
          emitValue(v.getArg(i));
        }

        emit("invokestatic " + slashify(m.declaringClass().getName()) + "/" + m.name() + jasminDescriptorOf(m),
            -(argCountOf(m)) + sizeOfType(m.returnType()));
      }

      @Override
      public void caseStaticFieldRef(StaticFieldRef v) {
        SootFieldRef field = v.getFieldRef();
        emit("getstatic " + slashify(field.declaringClass().getName()) + "/" + field.name() + " "
            + jasminDescriptorOf(field.type()), sizeOfType(field.type()));
      }

      @Override
      public void caseStringConstant(StringConstant v) {
        emit("ldc " + v.toString(), 1);
      }

      @Override
      public void caseClassConstant(ClassConstant v) {
        emit("ldc " + v.toInternalString(), 1);
      }

      @Override
      public void caseSubExpr(SubExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("isub", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lsub", -2);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dsub", -2);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fsub", -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for sub");
          }
        });
      }

      @Override
      public void caseUshrExpr(UshrExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("iushr", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lushr", -1);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for ushr");
          }
        });
      }

      @Override
      public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
        emitValue(v.getBase());

        SootMethodRef m = v.getMethodRef();
        for (int i = 0; i < m.parameterTypes().size(); i++) {
          emitValue(v.getArg(i));
        }

        emit("invokevirtual " + slashify(m.declaringClass().getName()) + "/" + m.name() + jasminDescriptorOf(m),
            -(argCountOf(m) + 1) + sizeOfType(m.returnType()));
      }

      @Override
      public void caseXorExpr(XorExpr v) {
        emitValue(v.getOp1());
        emitValue(v.getOp2());

        v.getType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("ixor", -1);
          }

          @Override
          public void caseIntType(IntType t) {
            handleIntCase();
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            handleIntCase();
          }

          @Override
          public void caseShortType(ShortType t) {
            handleIntCase();
          }

          @Override
          public void caseCharType(CharType t) {
            handleIntCase();
          }

          @Override
          public void caseByteType(ByteType t) {
            handleIntCase();
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lxor", -2);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for xor");
          }
        });
      }
    });
  }

  public void emitBooleanBranch(String s) {
    int count;

    if (s.contains("icmp") || s.contains("acmp")) {
      count = -2;
    } else {
      count = -1;
    }

    emit(s + " label" + labelCount, count);
    emit("iconst_0", 1);
    emit("goto label" + labelCount + 1, 0);
    emit("label" + labelCount++ + ":");
    emit("iconst_1", 1);
    emit("label" + labelCount++ + ":");
  }
}
