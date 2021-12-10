package soot.baf;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.Modifier;
import soot.NullType;
import soot.PackManager;
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
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IdentityRef;
import soot.jimple.IntConstant;
import soot.jimple.JimpleBody;
import soot.jimple.LongConstant;
import soot.jimple.MethodHandle;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;
import soot.options.Options;
import soot.tagkit.JasminAttribute;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefBlockGraph;
import soot.util.ArraySet;
import soot.util.Chain;

public class JasminClass extends AbstractJasminClass {
  private static final Logger logger = LoggerFactory.getLogger(JasminClass.class);

  public JasminClass(SootClass sootClass) {
    super(sootClass);
  }

  @Override
  protected void assignColorsToLocals(Body body) {
    super.assignColorsToLocals(body);

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
    if (!(activeBody instanceof BafBody)) {
      if (activeBody instanceof JimpleBody) {
        if (Options.v().verbose()) {
          logger.debug(
              "Was expecting Baf body for " + method + " but found a Jimple body. Will convert body to Baf on the fly.");
        }
        activeBody = PackManager.v().convertJimpleBodyToBaf(method);
      } else {
        throw new RuntimeException("method: " + method.getName() + " has an invalid active body!");
      }
    }

    BafBody body = (BafBody) activeBody;

    if (body == null) {
      throw new RuntimeException("method: " + method.getName() + " has no active body!");
    }

    if (Options.v().time()) {
      Timers.v().buildJasminTimer.start();
    }

    Chain<Unit> instList = body.getUnits();

    int stackLimitIndex = -1;

    subroutineToReturnAddressSlot = new HashMap<Unit, Integer>(10, 0.7f);

    // Determine the unitToLabel map
    {
      unitToLabel = new HashMap<Unit, String>(instList.size() * 2 + 1, 0.7f);
      labelCount = 0;

      for (UnitBox uBox : body.getUnitBoxes(true)) {
        // Assign a label for each statement reference
        {
          InstBox box = (InstBox) uBox;

          if (!unitToLabel.containsKey(box.getUnit())) {
            unitToLabel.put(box.getUnit(), "label" + labelCount++);
          }
        }
      }
    }

    // Emit the exceptions, recording the Units at the beginning
    // of handlers so that later on we can recognize blocks that
    // begin with an exception on the stack.
    Set<Unit> handlerUnits = new ArraySet<Unit>(body.getTraps().size());
    for (Trap trap : body.getTraps()) {
      handlerUnits.add(trap.getHandlerUnit());
      if (trap.getBeginUnit() != trap.getEndUnit()) {
        emit(".catch " + slashify(trap.getException().getName()) + " from " + unitToLabel.get(trap.getBeginUnit()) + " to "
            + unitToLabel.get(trap.getEndUnit()) + " using " + unitToLabel.get(trap.getHandlerUnit()));
      }
    }

    // Determine where the locals go
    {
      int localCount = 0;
      int[] paramSlots = new int[method.getParameterCount()];
      int thisSlot = 0;
      Set<Local> assignedLocals = new HashSet<Local>();

      localToSlot = new HashMap<Local, Integer>(body.getLocalCount() * 2 + 1, 0.7f);

      // assignColorsToLocals(body);

      // Determine slots for 'this' and parameters
      {
        if (!method.isStatic()) {
          thisSlot = 0;
          localCount++;
        }

        for (int i = 0; i < method.getParameterCount(); i++) {
          paramSlots[i] = localCount;
          localCount += sizeOfType(method.getParameterType(i));
        }
      }

      // Handle identity statements
      for (Unit u : instList) {
        Inst s = (Inst) u;
        if (s instanceof IdentityInst) {
          IdentityInst is = (IdentityInst) s;
          Value lhs = is.getLeftOp();
          if (lhs instanceof Local) {
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

            Local l = (Local) lhs;
            localToSlot.put(l, slot);
            assignedLocals.add(l);
          }
        }
      }

      // Assign the rest of the locals
      {
        for (Local local : body.getLocals()) {
          if (assignedLocals.add(local)) {
            localToSlot.put(local, localCount);
            localCount += sizeOfType(local.getType());
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

    // Emit code in one pass
    {
      isEmittingMethodCode = true;
      maxStackHeight = 0;
      isNextGotoAJsr = false;

      for (Unit u : instList) {
        Inst s = (Inst) u;

        if (unitToLabel.containsKey(s)) {
          emit(unitToLabel.get(s) + ":");
        }

        // emit this statement
        emitInst(s);
      }

      isEmittingMethodCode = false;

      // calculate max stack height
      {
        maxStackHeight = 0;
        if (!activeBody.getUnits().isEmpty()) {
          BlockGraph blockGraph = new BriefBlockGraph(activeBody);
          if (!blockGraph.getBlocks().isEmpty()) {
            // set the stack height of the entry points
            List<Block> entryPoints = blockGraph.getHeads();
            for (Block entryBlock : entryPoints) {
              Integer initialHeight;
              if (handlerUnits.contains(entryBlock.getHead())) {
                initialHeight = 1;
              } else {
                initialHeight = 0;
              }
              if (blockToStackHeight == null) {
                blockToStackHeight = new HashMap<Block, Integer>();
              }
              blockToStackHeight.put(entryBlock, initialHeight);
              if (blockToLogicalStackHeight == null) {
                blockToLogicalStackHeight = new HashMap<Block, Integer>();
              }
              blockToLogicalStackHeight.put(entryBlock, initialHeight);
            }

            // dfs the block graph using the blocks in the
            // entryPoints list as roots
            for (Block nextBlock : entryPoints) {
              calculateStackHeight(nextBlock);
              calculateLogicalStackHeightCheck(nextBlock);
            }
          }
        }
      }
      int modifiers = method.getModifiers();
      if (!Modifier.isNative(modifiers) && !Modifier.isAbstract(modifiers)) {
        code.set(stackLimitIndex, "    .limit stack " + maxStackHeight);
      }
    }

    // emit code attributes
    for (Tag t : body.getTags()) {
      if (t instanceof JasminAttribute) {
        emit(".code_attribute " + t.getName() + " \"" + ((JasminAttribute) t).getJasminValue(unitToLabel) + "\"");
      }
    }
  }

  void emitInst(Inst inst) {
    LineNumberTag lnTag = (LineNumberTag) inst.getTag(LineNumberTag.NAME);
    if (lnTag != null) {
      emit(".line " + lnTag.getLineNumber());
    }
    inst.apply(new InstSwitch() {
      @Override
      public void caseReturnVoidInst(ReturnVoidInst i) {
        emit("return");
      }

      @Override
      public void caseReturnInst(ReturnInst i) {
        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid return type " + t.toString());
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dreturn");
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("freturn");
          }

          @Override
          public void caseIntType(IntType t) {
            emit("ireturn");
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("ireturn");
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("ireturn");
          }

          @Override
          public void caseCharType(CharType t) {
            emit("ireturn");
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("ireturn");
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lreturn");
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emit("areturn");
          }

          @Override
          public void caseRefType(RefType t) {
            emit("areturn");
          }

          @Override
          public void caseNullType(NullType t) {
            emit("areturn");
          }
        });
      }

      @Override
      public void caseNopInst(NopInst i) {
        emit("nop");
      }

      @Override
      public void caseEnterMonitorInst(EnterMonitorInst i) {
        emit("monitorenter");
      }

      @Override
      public void casePopInst(PopInst i) {
        if (i.getWordCount() == 2) {
          emit("pop2");
        } else {
          emit("pop");
        }
      }

      @Override
      public void caseExitMonitorInst(ExitMonitorInst i) {
        emit("monitorexit");
      }

      @Override
      public void caseGotoInst(GotoInst i) {
        emit("goto " + unitToLabel.get(i.getTarget()));
      }

      @Override
      public void caseJSRInst(JSRInst i) {
        emit("jsr " + unitToLabel.get(i.getTarget()));
      }

      @Override
      public void casePushInst(PushInst i) {
        final Constant constant = i.getConstant();
        if (constant instanceof IntConstant) {
          IntConstant v = (IntConstant) constant;
          int val = v.value;
          if (val == -1) {
            emit("iconst_m1");
          } else if (val >= 0 && val <= 5) {
            emit("iconst_" + val);
          } else if (val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE) {
            emit("bipush " + val);
          } else if (val >= Short.MIN_VALUE && val <= Short.MAX_VALUE) {
            emit("sipush " + val);
          } else {
            emit("ldc " + v.toString());
          }
        } else if (constant instanceof StringConstant) {
          emit("ldc " + constant.toString());
        } else if (constant instanceof ClassConstant) {
          emit("ldc " + ((ClassConstant) constant).toInternalString());
        } else if (constant instanceof DoubleConstant) {
          DoubleConstant v = (DoubleConstant) constant;
          double val = v.value;
          if ((val == 0) && ((1.0 / val) > 0.0)) {
            emit("dconst_0");
          } else if (val == 1) {
            emit("dconst_1");
          } else {
            emit("ldc2_w " + doubleToString(v));
          }
        } else if (constant instanceof FloatConstant) {
          FloatConstant v = (FloatConstant) constant;
          float val = v.value;
          if ((val == 0) && ((1.0f / val) > 1.0f)) {
            emit("fconst_0");
          } else if (val == 1) {
            emit("fconst_1");
          } else if (val == 2) {
            emit("fconst_2");
          } else {
            emit("ldc " + floatToString(v));
          }
        } else if (constant instanceof LongConstant) {
          LongConstant v = (LongConstant) constant;
          long val = v.value;
          if (val == 0) {
            emit("lconst_0");
          } else if (val == 1) {
            emit("lconst_1");
          } else {
            emit("ldc2_w " + v.toString());
          }
        } else if (constant instanceof NullConstant) {
          emit("aconst_null");
        } else if (constant instanceof MethodHandle) {
          throw new RuntimeException("MethodHandle constants not supported by Jasmin. Please use -asm-backend.");
        } else {
          throw new RuntimeException("unsupported opcode");
        }
      }

      @Override
      public void caseIdentityInst(IdentityInst i) {
        if (i.getRightOp() instanceof CaughtExceptionRef) {
          Value leftOp = i.getLeftOp();
          if (leftOp instanceof Local) {
            int slot = localToSlot.get((Local) leftOp);
            if (slot >= 0 && slot <= 3) {
              emit("astore_" + slot);
            } else {
              emit("astore " + slot);
            }
          }
        }
      }

      @Override
      public void caseStoreInst(StoreInst i) {
        final int slot = localToSlot.get(i.getLocal());

        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseArrayType(ArrayType t) {
            if (slot >= 0 && slot <= 3) {
              emit("astore_" + slot);
            } else {
              emit("astore " + slot);
            }
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            if (slot >= 0 && slot <= 3) {
              emit("dstore_" + slot);
            } else {
              emit("dstore " + slot);
            }
          }

          @Override
          public void caseFloatType(FloatType t) {
            if (slot >= 0 && slot <= 3) {
              emit("fstore_" + slot);
            } else {
              emit("fstore " + slot);
            }
          }

          @Override
          public void caseIntType(IntType t) {
            if (slot >= 0 && slot <= 3) {
              emit("istore_" + slot);
            } else {
              emit("istore " + slot);
            }
          }

          @Override
          public void caseByteType(ByteType t) {
            if (slot >= 0 && slot <= 3) {
              emit("istore_" + slot);
            } else {
              emit("istore " + slot);
            }
          }

          @Override
          public void caseShortType(ShortType t) {
            if (slot >= 0 && slot <= 3) {
              emit("istore_" + slot);
            } else {
              emit("istore " + slot);
            }
          }

          @Override
          public void caseCharType(CharType t) {
            if (slot >= 0 && slot <= 3) {
              emit("istore_" + slot);
            } else {
              emit("istore " + slot);
            }
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            if (slot >= 0 && slot <= 3) {
              emit("istore_" + slot);
            } else {
              emit("istore " + slot);
            }
          }

          @Override
          public void caseLongType(LongType t) {
            if (slot >= 0 && slot <= 3) {
              emit("lstore_" + slot);
            } else {
              emit("lstore " + slot);
            }
          }

          @Override
          public void caseRefType(RefType t) {
            if (slot >= 0 && slot <= 3) {
              emit("astore_" + slot);
            } else {
              emit("astore " + slot);
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
            if (slot >= 0 && slot <= 3) {
              emit("astore_" + slot);
            } else {
              emit("astore " + slot);
            }
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid local type:" + t);
          }
        });
      }

      @Override
      public void caseLoadInst(LoadInst i) {
        final int slot = localToSlot.get(i.getLocal());

        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseArrayType(ArrayType t) {
            if (slot >= 0 && slot <= 3) {
              emit("aload_" + slot);
            } else {
              emit("aload " + slot);
            }
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid local type to load" + t);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            if (slot >= 0 && slot <= 3) {
              emit("dload_" + slot);
            } else {
              emit("dload " + slot);
            }
          }

          @Override
          public void caseFloatType(FloatType t) {
            if (slot >= 0 && slot <= 3) {
              emit("fload_" + slot);
            } else {
              emit("fload " + slot);
            }
          }

          @Override
          public void caseIntType(IntType t) {
            if (slot >= 0 && slot <= 3) {
              emit("iload_" + slot);
            } else {
              emit("iload " + slot);
            }
          }

          @Override
          public void caseByteType(ByteType t) {
            if (slot >= 0 && slot <= 3) {
              emit("iload_" + slot);
            } else {
              emit("iload " + slot);
            }
          }

          @Override
          public void caseShortType(ShortType t) {
            if (slot >= 0 && slot <= 3) {
              emit("iload_" + slot);
            } else {
              emit("iload " + slot);
            }
          }

          @Override
          public void caseCharType(CharType t) {
            if (slot >= 0 && slot <= 3) {
              emit("iload_" + slot);
            } else {
              emit("iload " + slot);
            }
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            if (slot >= 0 && slot <= 3) {
              emit("iload_" + slot);
            } else {
              emit("iload " + slot);
            }
          }

          @Override
          public void caseLongType(LongType t) {
            if (slot >= 0 && slot <= 3) {
              emit("lload_" + slot);
            } else {
              emit("lload " + slot);
            }
          }

          @Override
          public void caseRefType(RefType t) {
            if (slot >= 0 && slot <= 3) {
              emit("aload_" + slot);
            } else {
              emit("aload " + slot);
            }
          }

          @Override
          public void caseNullType(NullType t) {
            if (slot >= 0 && slot <= 3) {
              emit("aload_" + slot);
            } else {
              emit("aload " + slot);
            }
          }
        });
      }

      @Override
      public void caseArrayWriteInst(ArrayWriteInst i) {
        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseArrayType(ArrayType t) {
            emit("aastore");
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dastore");
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fastore");
          }

          @Override
          public void caseIntType(IntType t) {
            emit("iastore");
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lastore");
          }

          @Override
          public void caseRefType(RefType t) {
            emit("aastore");
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("bastore");
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("bastore");
          }

          @Override
          public void caseCharType(CharType t) {
            emit("castore");
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("sastore");
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid type: " + t);
          }
        });
      }

      @Override
      public void caseArrayReadInst(ArrayReadInst i) {
        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseArrayType(ArrayType ty) {
            emit("aaload");
          }

          @Override
          public void caseBooleanType(BooleanType ty) {
            emit("baload");
          }

          @Override
          public void caseByteType(ByteType ty) {
            emit("baload");
          }

          @Override
          public void caseCharType(CharType ty) {
            emit("caload");
          }

          @Override
          public void defaultCase(Type ty) {
            throw new RuntimeException("invalid base type");
          }

          @Override
          public void caseDoubleType(DoubleType ty) {
            emit("daload");
          }

          @Override
          public void caseFloatType(FloatType ty) {
            emit("faload");
          }

          @Override
          public void caseIntType(IntType ty) {
            emit("iaload");
          }

          @Override
          public void caseLongType(LongType ty) {
            emit("laload");
          }

          @Override
          public void caseNullType(NullType ty) {
            emit("aaload");
          }

          @Override
          public void caseRefType(RefType ty) {
            emit("aaload");
          }

          @Override
          public void caseShortType(ShortType ty) {
            emit("saload");
          }
        });
      }

      @Override
      public void caseIfNullInst(IfNullInst i) {
        emit("ifnull " + unitToLabel.get(i.getTarget()));
      }

      @Override
      public void caseIfNonNullInst(IfNonNullInst i) {
        emit("ifnonnull " + unitToLabel.get(i.getTarget()));
      }

      @Override
      public void caseIfEqInst(IfEqInst i) {
        emit("ifeq " + unitToLabel.get(i.getTarget()));
      }

      @Override
      public void caseIfNeInst(IfNeInst i) {
        emit("ifne " + unitToLabel.get(i.getTarget()));
      }

      @Override
      public void caseIfGtInst(IfGtInst i) {
        emit("ifgt " + unitToLabel.get(i.getTarget()));
      }

      @Override
      public void caseIfGeInst(IfGeInst i) {
        emit("ifge " + unitToLabel.get(i.getTarget()));
      }

      @Override
      public void caseIfLtInst(IfLtInst i) {
        emit("iflt " + unitToLabel.get(i.getTarget()));
      }

      @Override
      public void caseIfLeInst(IfLeInst i) {
        emit("ifle " + unitToLabel.get(i.getTarget()));
      }

      @Override
      public void caseIfCmpEqInst(final IfCmpEqInst i) {
        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmpeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmpeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmpeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmpeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmpeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg");
            emit("ifeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp");
            emit("ifeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg");
            emit("ifeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emit("if_acmpeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseRefType(RefType t) {
            emit("if_acmpeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseNullType(NullType t) {
            emit("if_acmpeq " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseIfCmpNeInst(final IfCmpNeInst i) {
        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmpne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmpne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmpne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmpne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmpne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg");
            emit("ifne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp");
            emit("ifne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg");
            emit("ifne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emit("if_acmpne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseRefType(RefType t) {
            emit("if_acmpne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseNullType(NullType t) {
            emit("if_acmpne " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseIfCmpGtInst(final IfCmpGtInst i) {
        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmpgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmpgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmpgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmpgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmpgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg");
            emit("ifgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp");
            emit("ifgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg");
            emit("ifgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emit("if_acmpgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseRefType(RefType t) {
            emit("if_acmpgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseNullType(NullType t) {
            emit("if_acmpgt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseIfCmpGeInst(final IfCmpGeInst i) {
        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmpge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmpge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmpge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmpge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmpge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg");
            emit("ifge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp");
            emit("ifge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg");
            emit("ifge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emit("if_acmpge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseRefType(RefType t) {
            emit("if_acmpge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseNullType(NullType t) {
            emit("if_acmpge " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseIfCmpLtInst(final IfCmpLtInst i) {
        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmplt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmplt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmplt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmplt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmplt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg");
            emit("iflt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp");
            emit("iflt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg");
            emit("iflt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emit("if_acmplt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseRefType(RefType t) {
            emit("if_acmplt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseNullType(NullType t) {
            emit("if_acmplt " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseIfCmpLeInst(final IfCmpLeInst i) {
        i.getOpType().apply(new TypeSwitch<Object>() {
          @Override
          public void caseIntType(IntType t) {
            emit("if_icmple " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseBooleanType(BooleanType t) {
            emit("if_icmple " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseShortType(ShortType t) {
            emit("if_icmple " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseCharType(CharType t) {
            emit("if_icmple " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseByteType(ByteType t) {
            emit("if_icmple " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("dcmpg");
            emit("ifle " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseLongType(LongType t) {
            emit("lcmp");
            emit("ifle " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("fcmpg");
            emit("ifle " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseArrayType(ArrayType t) {
            emit("if_acmple " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseRefType(RefType t) {
            emit("if_acmple " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void caseNullType(NullType t) {
            emit("if_acmple " + unitToLabel.get(i.getTarget()));
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("invalid type");
          }
        });
      }

      @Override
      public void caseStaticGetInst(StaticGetInst i) {
        SootFieldRef field = i.getFieldRef();
        emit("getstatic " + slashify(field.declaringClass().getName()) + "/" + field.name() + " "
            + jasminDescriptorOf(field.type()));
      }

      @Override
      public void caseStaticPutInst(StaticPutInst i) {
        SootFieldRef field = i.getFieldRef();
        emit("putstatic " + slashify(field.declaringClass().getName()) + "/" + field.name() + " "
            + jasminDescriptorOf(field.type()));
      }

      @Override
      public void caseFieldGetInst(FieldGetInst i) {
        SootFieldRef field = i.getFieldRef();
        emit("getfield " + slashify(field.declaringClass().getName()) + "/" + field.name() + " "
            + jasminDescriptorOf(field.type()));
      }

      @Override
      public void caseFieldPutInst(FieldPutInst i) {
        SootFieldRef field = i.getFieldRef();
        emit("putfield " + slashify(field.declaringClass().getName()) + "/" + field.name() + " "
            + jasminDescriptorOf(field.type()));
      }

      @Override
      public void caseInstanceCastInst(InstanceCastInst i) {
        Type castType = i.getCastType();
        if (castType instanceof RefType) {
          emit("checkcast " + slashify(((RefType) castType).getClassName()));
        } else if (castType instanceof ArrayType) {
          emit("checkcast " + jasminDescriptorOf(castType));
        }
      }

      @Override
      public void caseInstanceOfInst(InstanceOfInst i) {
        Type checkType = i.getCheckType();
        if (checkType instanceof RefType) {
          emit("instanceof " + slashify(checkType.toString()));
        } else if (checkType instanceof ArrayType) {
          emit("instanceof " + jasminDescriptorOf(checkType));
        }
      }

      @Override
      public void caseNewInst(NewInst i) {
        emit("new " + slashify(i.getBaseType().getClassName()));
      }

      @Override
      public void casePrimitiveCastInst(PrimitiveCastInst i) {
        emit(i.toString());
      }

      @Override
      public void caseDynamicInvokeInst(DynamicInvokeInst i) {
        StringBuilder str = new StringBuilder();

        SootMethodRef m = i.getMethodRef();
        str.append("invokedynamic \"").append(m.name()).append("\" ").append(jasminDescriptorOf(m)).append(' ');

        SootMethodRef bsm = i.getBootstrapMethodRef();
        str.append(slashify(bsm.declaringClass().getName())).append('/').append(bsm.name()).append(jasminDescriptorOf(bsm));

        str.append('(');
        for (Iterator<Value> iterator = i.getBootstrapArgs().iterator(); iterator.hasNext();) {
          Value val = iterator.next();
          str.append('(').append(jasminDescriptorOf(val.getType())).append(')');
          str.append(escape(val.toString()));

          if (iterator.hasNext()) {
            str.append(',');
          }
        }
        str.append(')');

        emit(str.toString());
      }

      private String escape(String bsmArgString) {
        return bsmArgString.replace(",", "\\comma").replace(" ", "\\blank").replace("\t", "\\tab").replace("\n",
            "\\newline");
      }

      @Override
      public void caseStaticInvokeInst(StaticInvokeInst i) {
        SootMethodRef m = i.getMethodRef();
        emit("invokestatic " + slashify(m.declaringClass().getName()) + "/" + m.name() + jasminDescriptorOf(m));
      }

      @Override
      public void caseVirtualInvokeInst(VirtualInvokeInst i) {
        SootMethodRef m = i.getMethodRef();
        emit("invokevirtual " + slashify(m.declaringClass().getName()) + "/" + m.name() + jasminDescriptorOf(m));
      }

      @Override
      public void caseInterfaceInvokeInst(InterfaceInvokeInst i) {
        SootMethodRef m = i.getMethodRef();
        emit("invokeinterface " + slashify(m.declaringClass().getName()) + "/" + m.name() + jasminDescriptorOf(m) + " "
            + (argCountOf(m) + 1));
      }

      @Override
      public void caseSpecialInvokeInst(SpecialInvokeInst i) {
        SootMethodRef m = i.getMethodRef();
        emit("invokespecial " + slashify(m.declaringClass().getName()) + "/" + m.name() + jasminDescriptorOf(m));
      }

      @Override
      public void caseThrowInst(ThrowInst i) {
        emit("athrow");
      }

      @Override
      public void caseCmpInst(CmpInst i) {
        emit("lcmp");
      }

      @Override
      public void caseCmplInst(CmplInst i) {
        if (i.getOpType().equals(FloatType.v())) {
          emit("fcmpl");
        } else {
          emit("dcmpl");
        }
      }

      @Override
      public void caseCmpgInst(CmpgInst i) {
        if (i.getOpType().equals(FloatType.v())) {
          emit("fcmpg");
        } else {
          emit("dcmpg");
        }
      }

      private void emitOpTypeInst(final String s, final OpTypeArgInst i) {
        i.getOpType().apply(new TypeSwitch<Object>() {
          private void handleIntCase() {
            emit("i" + s);
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
            emit("l" + s);
          }

          @Override
          public void caseDoubleType(DoubleType t) {
            emit("d" + s);
          }

          @Override
          public void caseFloatType(FloatType t) {
            emit("f" + s);
          }

          @Override
          public void defaultCase(Type t) {
            throw new RuntimeException("Invalid argument type for div");
          }
        });
      }

      @Override
      public void caseAddInst(AddInst i) {
        emitOpTypeInst("add", i);
      }

      @Override
      public void caseDivInst(DivInst i) {
        emitOpTypeInst("div", i);
      }

      @Override
      public void caseSubInst(SubInst i) {
        emitOpTypeInst("sub", i);
      }

      @Override
      public void caseMulInst(MulInst i) {
        emitOpTypeInst("mul", i);
      }

      @Override
      public void caseRemInst(RemInst i) {
        emitOpTypeInst("rem", i);
      }

      @Override
      public void caseShlInst(ShlInst i) {
        emitOpTypeInst("shl", i);
      }

      @Override
      public void caseAndInst(AndInst i) {
        emitOpTypeInst("and", i);
      }

      @Override
      public void caseOrInst(OrInst i) {
        emitOpTypeInst("or", i);
      }

      @Override
      public void caseXorInst(XorInst i) {
        emitOpTypeInst("xor", i);
      }

      @Override
      public void caseShrInst(ShrInst i) {
        emitOpTypeInst("shr", i);
      }

      @Override
      public void caseUshrInst(UshrInst i) {
        emitOpTypeInst("ushr", i);
      }

      @Override
      public void caseIncInst(IncInst i) {
        if (i.getUseBoxes().get(0).getValue() != i.getDefBoxes().get(0).getValue()) {
          throw new RuntimeException("iinc def and use boxes don't match");
        }

        emit("iinc " + localToSlot.get(i.getLocal()) + " " + i.getConstant());
      }

      @Override
      public void caseArrayLengthInst(ArrayLengthInst i) {
        emit("arraylength");
      }

      @Override
      public void caseNegInst(NegInst i) {
        emitOpTypeInst("neg", i);
      }

      @Override
      public void caseNewArrayInst(NewArrayInst i) {
        if (i.getBaseType() instanceof RefType) {
          emit("anewarray " + slashify(((RefType) i.getBaseType()).getClassName()));
        } else if (i.getBaseType() instanceof ArrayType) {
          emit("anewarray " + jasminDescriptorOf(i.getBaseType()));
        } else {
          emit("newarray " + i.getBaseType().toString());
        }
      }

      @Override
      public void caseNewMultiArrayInst(NewMultiArrayInst i) {
        emit("multianewarray " + jasminDescriptorOf(i.getBaseType()) + " " + i.getDimensionCount());
      }

      @Override
      public void caseLookupSwitchInst(LookupSwitchInst i) {
        emit("lookupswitch");

        List<Unit> targets = i.getTargets();
        List<IntConstant> lookupValues = i.getLookupValues();
        for (int j = 0; j < lookupValues.size(); j++) {
          emit("  " + lookupValues.get(j) + " : " + unitToLabel.get(targets.get(j)));
        }

        emit("  default : " + unitToLabel.get(i.getDefaultTarget()));
      }

      @Override
      public void caseTableSwitchInst(TableSwitchInst i) {
        emit("tableswitch " + i.getLowIndex() + " ; high = " + i.getHighIndex());

        for (Unit t : i.getTargets()) {
          emit("  " + unitToLabel.get(t));
        }

        emit("default : " + unitToLabel.get(i.getDefaultTarget()));
      }

      private boolean isDwordType(Type t) {
        return t instanceof LongType || t instanceof DoubleType || t instanceof DoubleWordType;
      }

      @Override
      public void caseDup1Inst(Dup1Inst i) {
        Type firstOpType = i.getOp1Type();
        if (isDwordType(firstOpType)) {
          emit("dup2"); // (form 2)
        } else {
          emit("dup");
        }
      }

      @Override
      public void caseDup2Inst(Dup2Inst i) {
        Type firstOpType = i.getOp1Type();
        Type secondOpType = i.getOp2Type();
        // The first two cases have no real bytecode equivalents.
        // Use a pair of insts to simulate them.
        if (isDwordType(firstOpType)) {
          emit("dup2"); // (form 2)
          if (isDwordType(secondOpType)) {
            emit("dup2"); // (form 2 -- by simulation)
          } else {
            emit("dup"); // also a simulation
          }
        } else if (isDwordType(secondOpType)) {
          if (isDwordType(firstOpType)) {
            emit("dup2"); // (form 2)
          } else {
            emit("dup");
          }
          emit("dup2"); // (form 2 -- complete the simulation)
        } else {
          emit("dup2"); // form 1
        }
      }

      @Override
      public void caseDup1_x1Inst(Dup1_x1Inst i) {
        Type opType = i.getOp1Type();
        Type underType = i.getUnder1Type();

        if (isDwordType(opType)) {
          if (isDwordType(underType)) {
            emit("dup2_x2"); // (form 4)
          } else {
            emit("dup2_x1"); // (form 2)
          }
        } else {
          if (isDwordType(underType)) {
            emit("dup_x2"); // (form 2)
          } else {
            emit("dup_x1"); // (only one form)
          }
        }
      }

      @Override
      public void caseDup1_x2Inst(Dup1_x2Inst i) {
        Type opType = i.getOp1Type();
        Type under1Type = i.getUnder1Type();
        Type under2Type = i.getUnder2Type();

        // 07-20-2006 Michael Batchelder
        // NOW handling all types of dup1_x2
        /*
         * From VM Spec: cat1 = category 1 (word type) cat2 = category 2 (doubleword)
         *
         * Form 1: [..., cat1_value3, cat1_value2, cat1_value1]->[..., cat1_value2, cat1_value1, cat1_value3, cat1_value2,
         * cat1_value1] Form 2: [..., cat1_value2, cat2_value1]->[..., cat2_value1, cat1_value2, cat2_value1]
         */

        if (isDwordType(opType)) {
          if (!isDwordType(under1Type) && !isDwordType(under2Type)) {
            emit("dup2_x2"); // (form 2)
          } else {
            throw new RuntimeException("magic not implemented yet");
          }
        } else {
          if (isDwordType(under1Type) || isDwordType(under2Type)) {
            throw new RuntimeException("magic not implemented yet");
          }
        }

        emit("dup_x2"); // (form 1)
      }

      @Override
      public void caseDup2_x1Inst(Dup2_x1Inst i) {
        Type op1Type = i.getOp1Type();
        Type op2Type = i.getOp2Type();
        Type under1Type = i.getUnder1Type();

        // 07-20-2006 Michael Batchelder
        // NOW handling all types of dup2_x1
        /*
         * From VM Spec: cat1 = category 1 (word type) cat2 = category 2 (doubleword)
         *
         * Form 1: [..., cat1_value3, cat1_value2, cat1_value1]->[..., cat1_value2, cat1_value1, cat1_value3, cat1_value2,
         * cat1_value1] Form 2: [..., cat1_value2, cat2_value1]->[..., cat2_value1, cat1_value2, cat2_value1]
         */
        if (isDwordType(under1Type)) {
          if (!isDwordType(op1Type) && !isDwordType(op2Type)) {
            throw new RuntimeException("magic not implemented yet");
          } else {
            emit("dup2_x2"); // (form 3)
          }
        } else {
          if ((isDwordType(op1Type) && op2Type != null) || isDwordType(op2Type)) {
            throw new RuntimeException("magic not implemented yet");
          }
        }

        emit("dup2_x1"); // (form 1)
      }

      @Override
      public void caseDup2_x2Inst(Dup2_x2Inst i) {
        Type op1Type = i.getOp1Type();
        Type op2Type = i.getOp2Type();
        Type under1Type = i.getUnder1Type();
        Type under2Type = i.getUnder2Type();

        // 07-20-2006 Michael Batchelder
        // NOW handling all types of dup2_x2

        /*
         * From VM Spec: cat1 = category 1 (word type) cat2 = category 2 (doubleword) Form 1: [..., cat1_value4, cat1_value3,
         * cat1_value2, cat1_value1]->[..., cat1_value2, cat1_value1, cat1_value4, cat1_value3, cat1_value2, cat1_value1]
         * Form 2: [..., cat1_value3, cat1_value2, cat2_value1]->[ ..., cat2_value1, cat1_value3, cat1_value2, cat2_value1]
         * Form 3: [..., cat2_value3, cat1_value2, cat1_value1]->[..., cat1_value2, cat1_value1, cat2_value3, cat1_value2,
         * cat1_value1] Form 4: [..., cat2_value2, cat2_value1]->[..., cat2_value1, cat2_value2, cat2_value1]
         */
        boolean malformed = true;
        if (isDwordType(op1Type)) {
          if (op2Type == null && under1Type != null) {
            if ((under2Type == null && isDwordType(under1Type))
                || (!isDwordType(under1Type) && under2Type != null && !isDwordType(under2Type))) {
              malformed = false;
            }
          }
        } else if (op1Type != null && op2Type != null && !isDwordType(op2Type)) {
          if ((under2Type == null && isDwordType(under1Type))
              || (under1Type != null && !isDwordType(under1Type) && under2Type != null && !isDwordType(under2Type))) {
            malformed = false;
          }
        }
        if (malformed) {
          throw new RuntimeException("magic not implemented yet");
        }

        emit("dup2_x2"); // (form 1)
      }

      @Override
      public void caseSwapInst(SwapInst i) {
        emit("swap");
      }
    });
  }

  private void calculateStackHeight(Block aBlock) {
    int blockHeight = blockToStackHeight.get(aBlock);
    if (blockHeight > maxStackHeight) {
      maxStackHeight = blockHeight;
    }

    for (Unit u : aBlock) {
      Inst nInst = (Inst) u;

      blockHeight -= nInst.getInMachineCount();

      if (blockHeight < 0) {
        throw new RuntimeException(
            "Negative Stack height has been attained in :" + aBlock.getBody().getMethod().getSignature() + " \n"
                + "StackHeight: " + blockHeight + "\n" + "At instruction:" + nInst + "\n" + "Block:\n" + aBlock
                + "\n\nMethod: " + aBlock.getBody().getMethod().getName() + "\n" + aBlock.getBody().getMethod());
      }

      blockHeight += nInst.getOutMachineCount();
      if (blockHeight > maxStackHeight) {
        maxStackHeight = blockHeight;
      }
      // logger.debug(">>> " + nInst + " " + blockHeight);
    }

    for (Block b : aBlock.getSuccs()) {
      Integer i = blockToStackHeight.get(b);
      if (i != null) {
        if (i != blockHeight) {
          throw new RuntimeException(
              aBlock.getBody().getMethod().getSignature() + ": incoherent stack height at block merge point " + b + aBlock
                  + "\ncomputed blockHeight == " + blockHeight + " recorded blockHeight = " + i);
        }
      } else {
        blockToStackHeight.put(b, blockHeight);
        calculateStackHeight(b);
      }
    }
  }

  private void calculateLogicalStackHeightCheck(Block aBlock) {
    int blockHeight = blockToLogicalStackHeight.get(aBlock);
    for (Unit u : aBlock) {
      Inst nInst = (Inst) u;

      blockHeight -= nInst.getInCount();

      if (blockHeight < 0) {
        throw new RuntimeException("Negative Stack Logical height has been attained: \n" + "StackHeight: " + blockHeight
            + "\nAt instruction:" + nInst + "\nBlock:\n" + aBlock + "\n\nMethod: " + aBlock.getBody().getMethod().getName()
            + "\n" + aBlock.getBody().getMethod());
      }

      blockHeight += nInst.getOutCount();
      // logger.debug(">>> " + nInst + " " + blockHeight);
    }

    for (Block b : aBlock.getSuccs()) {
      Integer i = blockToLogicalStackHeight.get(b);
      if (i != null) {
        if (i != blockHeight) {
          throw new RuntimeException("incoherent logical stack height at block merge point " + b + aBlock);
        }
      } else {
        blockToLogicalStackHeight.put(b, blockHeight);
        calculateLogicalStackHeightCheck(b);
      }
    }
  }
}
