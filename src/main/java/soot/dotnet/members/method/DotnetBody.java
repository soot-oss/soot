package soot.dotnet.members.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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

import soot.Body;
import soot.BooleanConstant;
import soot.ByteConstant;
import soot.Immediate;
import soot.Local;
import soot.LocalGenerator;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.dotnet.instructions.CilBlockContainer;
import soot.dotnet.members.ByReferenceWrapperGenerator;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.members.InitialFieldTagValue;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.proto.ProtoIlInstructions.IlFunctionMsg;
import soot.dotnet.types.DotnetTypeFactory;
import soot.dotnet.types.StructTag;
import soot.dotnet.values.FunctionPointerConstant;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.base.Aggregator;
import soot.jimple.toolkits.scalar.ConditionalBranchFolder;
import soot.jimple.toolkits.scalar.ConstantCastEliminator;
import soot.jimple.toolkits.scalar.CopyPropagator;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.jimple.toolkits.scalar.IdentityCastEliminator;
import soot.jimple.toolkits.scalar.IdentityOperationEliminator;
import soot.jimple.toolkits.scalar.NopEliminator;
import soot.jimple.toolkits.scalar.UnconditionalBranchFolder;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.toolkits.exceptions.TrapTightener;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.toolkits.scalar.UnusedLocalEliminator;

/**
 * Represents a .NET Method Body A method body starts with a BlockContainer, which contains Blocks, which have IL
 * Instructions .NET Method Body (with ILSpy AST) -> BlockContainer -> Block -> IL Instruction
 */
public class DotnetBody {
  //we need to split thanks to the style imposed line length limit. Great.
  private static final String INIT_ARRAY
      = "<System.Runtime.CompilerServices.RuntimeHelpers:" + 
        "void InitializeArray(System.Array,System.RuntimeFieldHandle)>";
  private final ProtoIlInstructions.IlFunctionMsg ilFunctionMsg;
  private JimpleBody jb;

  public BlockEntryPointsManager blockEntryPointsManager;
  public DotnetBodyVariableManager variableManager;

  /**
   * Get method signature of this method body
   *
   * @return method signature
   */
  public DotnetMethod getDotnetMethodSig() {
    return dotnetMethodSig;
  }

  public IlFunctionMsg getFunctionMsg() {
    return ilFunctionMsg;
  }

  private final DotnetMethod dotnetMethodSig;

  public DotnetBody(DotnetMethod methodSignature, ProtoIlInstructions.IlFunctionMsg ilFunctionMsg) {
    this.dotnetMethodSig = methodSignature;
    this.ilFunctionMsg = ilFunctionMsg;
    blockEntryPointsManager = new BlockEntryPointsManager();
  }

  public void jimplify(JimpleBody jb) {
    this.jb = jb;
    variableManager = new DotnetBodyVariableManager(this, this.jb);
    // resolve initial variable assignments
    addThisStmt();

    List<Unit> unwrapCalls = new ArrayList<>();
    Map<Local, Local> unwrappedToWrapped = new HashMap<>();
    variableManager.fillMethodParameter(unwrapCalls, unwrappedToWrapped);
    jb.getUnits().addAll(unwrapCalls);
    variableManager.addInitLocalVariables(ilFunctionMsg.getVariablesList());

    // Resolve .NET Method Body -> BlockContainer -> Block -> IL Instruction
    CilBlockContainer blockContainer = new CilBlockContainer(ilFunctionMsg.getBody(), this);
    Body b = blockContainer.jimplify();
    Set<Local> allLocals = Collections.newSetFromMap(new java.util.IdentityHashMap<Local, Boolean>());
    allLocals.addAll(jb.getLocals());
    for (Local l : b.getLocals()) {
      if (allLocals.add(l)) {
        jb.getLocals().add(l);
      }
    }
    jb.getUnits().addAll(b.getUnits());
    jb.getTraps().addAll(b.getTraps());
    blockEntryPointsManager.swapGotoEntriesInJBody(jb);

    replaceWrappedRefWrites(jb, unwrapCalls, unwrappedToWrapped);

    // We now do similar kind of optimizations than for dex code, since
    // the code we generate is not really efficient...

    rewriteConditionsToIfs(jb);
    DelegateHandler.replaceDelegates(jb);

    UnconditionalBranchFolder.v().transform(jb);

    //LocalPacker.v().transform(jb);
    // UnusedLocalEliminator.v().transform(jb);

    TrapTightener.v().transform(jb);
    Aggregator.v().transform(jb);

    ConditionalBranchFolder.v().transform(jb);

    // Remove unnecessary typecasts
    ConstantCastEliminator.v().transform(jb);
    IdentityCastEliminator.v().transform(jb);

    // Remove unnecessary logic operations
    IdentityOperationEliminator.v().transform(jb);

    // We need to run this transformer since the conditional branch folder
    // might have rendered some code unreachable (well, it was unreachable
    // before as well, but we didn't know).
    UnreachableCodeEliminator.v().transform(jb);

    TransformIntsToBooleans.v().transform(jb);
    CopyPropagator.v().transform(jb);

    TransformIntsToBooleans.v().transform(jb);
    CopyPropagator.v().transform(jb);
    rewriteConditionsToIfs(jb);

    removeDeadNewExpr(jb);
    DeadAssignmentEliminator.v().transform(jb);
    UnusedLocalEliminator.v().transform(jb);

    ConditionalBranchFolder.v().transform(jb);

    UnconditionalBranchFolder.v().transform(jb);
    introduceStructConstructorCalls(jb);

    NopEliminator.v().transform(jb);
    replaceInitArray(jb);

    renameLocals(jb);

    for (Unit u : jb.getUnits()) {
      for (ValueBox d : u.getUseBoxes()) {
        if (d.getValue() instanceof FunctionPointerConstant) {
          if (dotnetMethodSig.getProtoMessage().getIsUnsafe()) {
            // this is somewhat expected. We resolve unsafe methods on a best effort basis
            throw new RuntimeException("Function pointer left in unsafe method; this is expected.");
          } else {
            // this is more concerning...
            throw new RuntimeException("Function pointer left in normal method.");
          }
        }
      }
    }
  }

  protected void renameLocals(JimpleBody jb) {
    // Sadly, the original contributer made everything relying on the names, so
    // we rename that mess now...
    Set<String> names = new HashSet<>();
    int id = 0;
    for (Local l : jb.getLocals()) {
      if (!names.add(l.getName())) {
        l.setName(l.getName() + "_" + id++);
      }
    }
  }

  private void replaceInitArray(JimpleBody jb2) {

    UnitPatchingChain uchain = jb.getUnits();
    Unit crt = uchain.getFirst();
    Jimple j = Jimple.v();
    while (crt != null) {
      Unit next = uchain.getSuccOf(crt);
      Stmt c = (Stmt) crt;
      if (c.containsInvokeExpr()) {
        InvokeExpr invExpr = c.getInvokeExpr();
        if (invExpr.getMethodRef().getName().equals("InitializeArray")) {
          if (invExpr.getMethodRef().getSignature().equals(INIT_ARRAY)) {
            Value ref = invExpr.getArg(1);
            if (ref instanceof soot.jimple.MethodHandle) {
              soot.jimple.MethodHandle mh = (soot.jimple.MethodHandle) ref;
              SootField f = mh.getFieldRef().resolve();
              InitialFieldTagValue t = (InitialFieldTagValue) f.getTag(InitialFieldTagValue.NAME);
              Value arg = invExpr.getArg(0);
              if (t != null) {
                byte[] val = t.getValue();
                List<Unit> toInsert = new ArrayList<>(val.length);
                for (int i = 0; i < val.length; i++) {
                  toInsert.add(j.newAssignStmt(j.newArrayRef(arg, IntConstant.v(i)), ByteConstant.v(val[i])));
                }
                uchain.insertAfter(toInsert, c);
                uchain.remove(c);
              }
            }
          }
        }

      }
      crt = next;
    }
  }

  private void introduceStructConstructorCalls(JimpleBody jb) {
    //in some cases, we do not generate <init> calls for structs (when default values are used)

    SimpleLocalUses uses = null;
    UnitPatchingChain uchain = jb.getUnits();
    Unit crt = uchain.getFirst();
    Jimple j = Jimple.v();
    nextUnit: while (crt != null) {
      Unit next = uchain.getSuccOf(crt);
      if (crt instanceof AssignStmt) {
        AssignStmt assign = (AssignStmt) crt;
        Value rop = assign.getRightOp();
        if (rop instanceof NewExpr) {
          RefType rt = (RefType) rop.getType();
          if (rt.hasSootClass() && rt.getSootClass().hasTag(StructTag.NAME)) {
            if (uses == null) {
              uses = new SimpleLocalUses(jb, new SimpleLocalDefs(new ExceptionalUnitGraph(jb)));
            }
            Local base = (Local) assign.getLeftOp();
            for (UnitValueBoxPair u : uses.getUsesOf(assign)) {
              Stmt c = (Stmt) u.getUnit();
              if (c.containsInvokeExpr()) {
                InvokeExpr expr = c.getInvokeExpr();
                if (expr instanceof SpecialInvokeExpr && expr.getMethodRef().getName().equals("<init>")) {
                  SpecialInvokeExpr si = (SpecialInvokeExpr) expr;
                  if (si.getBase() == base) {
                    crt = next;
                    continue nextUnit;
                  }
                }
              }
            }

            SootMethod ctor = rt.getSootClass().getMethod("<init>", Collections.emptyList());
            InvokeStmt ctorcall = j.newInvokeStmt(j.newSpecialInvokeExpr(base, ctor.makeRef()));
            uchain.insertAfter(ctorcall, crt);
          }
        }
      }
      crt = next;
    }
  }

  /**
   * This pass checks for writes to all unwrapped by-reference variable, and updates the reference.
   * 
   * @param jb
   *          the body
   * @param unwrappedToWrapped
   *          map from unwrapped to wrapped variable
   */
  private void replaceWrappedRefWrites(JimpleBody jb, List<Unit> unwrapCalls, Map<Local, Local> unwrappedToWrapped) {
    UnitPatchingChain unitchain = jb.getUnits();
    Unit u = unitchain.getFirst();
    while (u != null) {
      Unit next = unitchain.getSuccOf(u);
      if (!unwrapCalls.contains(u) && u instanceof AssignStmt) {
        AssignStmt assign = (AssignStmt) u;
        Value lop = assign.getLeftOp();
        if (lop instanceof Local) {
          Local unwrapped = (Local) lop;
          Local wrapped = unwrappedToWrapped.get(unwrapped);
          if (wrapped != null) {
            unitchain.insertAfter(ByReferenceWrapperGenerator.getUpdateWrappedValueCall(wrapped, unwrapped), u);
          }
        }

      }
      u = next;
    }
  }

  protected void removeDeadNewExpr(JimpleBody jb) {
    UnitPatchingChain up = jb.getUnits();
    ExceptionalUnitGraph g = new ExceptionalUnitGraph(jb);
    SimpleLocalUses ld = new SimpleLocalUses(g, new SimpleLocalDefs(g));
    Unit u = up.getFirst();
    while (u != null) {
      Unit next = up.getSuccOf(u);
      if (u instanceof AssignStmt) {
        AssignStmt assign = (AssignStmt) u;
        if (assign.getRightOp() instanceof NewExpr) {
          if (ld.getUsesOf(assign).isEmpty()) {
            up.remove(assign);
          }

        }
      }
      u = next;
    }
  }

  protected void rewriteConditionsToIfs(JimpleBody jb) {
    UnitPatchingChain up = jb.getUnits();
    Unit u = up.getFirst();
    Jimple j = Jimple.v();
    while (u != null) {
      Unit next = up.getSuccOf(u);
      if (u instanceof AssignStmt) {
        AssignStmt assign = (AssignStmt) u;
        if (assign.getRightOp() instanceof ConditionExpr) {
          // e.g. foo = a == b;
          // this is not valid in Jimple...
          AssignStmt assignTrue = j.newAssignStmt(assign.getLeftOp(), BooleanConstant.v(true));
          AssignStmt assignFalse = j.newAssignStmt(assign.getLeftOp(), BooleanConstant.v(false));
          IfStmt ifs = j.newIfStmt(assign.getRightOp(), assignTrue);
          up.insertBefore(Arrays.asList(ifs, assignFalse, j.newGotoStmt(next), assignTrue), assign);
          up.remove(assign);

        }
      }
      u = next;
    }
  }

  protected Value createTempVar(Body jb, final Jimple jimple, Value inv) {
    Local interimLocal = variableManager.localGenerator.generateLocal(inv.getType());
    jb.getLocals().add(interimLocal);
    jb.getUnits().add(jimple.newAssignStmt(interimLocal, inv));
    return interimLocal;
  }

  private void addThisStmt() {
    if (dotnetMethodSig.isStatic()) {
      return;
    }
    RefType thisType = dotnetMethodSig.getDeclaringClass().getType();
    Local l = Jimple.v().newLocal("this", thisType);
    IdentityStmt identityStmt = Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef(thisType));
    this.jb.getLocals().add(l);
    this.jb.getUnits().add(identityStmt);
  }

  /**
   * Due to three address code, inline cast expr
   *
   * @param v
   * @return
   */
  public static Value inlineCastExpr(Value v) {
    if (v instanceof Immediate) {
      return v;
    }
    if (v instanceof CastExpr) {
      return inlineCastExpr(((CastExpr) v).getOp());
    }
    return v;
  }

  public static JimpleBody getEmptyJimpleBody(SootMethod m) {
    JimpleBody b = Jimple.v().newBody(m);
    resolveEmptyJimpleBody(b, m);
    return b;
  }

  public static void resolveEmptyJimpleBody(JimpleBody b, SootMethod m) {
    Jimple j = Jimple.v();
    // if not static add this stmt
    if (!m.isStatic()) {
      RefType thisType = m.getDeclaringClass().getType();
      Local l = j.newLocal("this", thisType);
      IdentityStmt identityStmt = j.newIdentityStmt(l, j.newThisRef(thisType));
      b.getLocals().add(l);
      b.getUnits().add(identityStmt);
    }
    // parameters
    for (int i = 0; i < m.getParameterCount(); i++) {
      Type parameterType = m.getParameterType(i);
      Local paramLocal = j.newLocal("arg" + i, parameterType);
      b.getLocals().add(paramLocal);
      b.getUnits().add(j.newIdentityStmt(paramLocal, j.newParameterRef(parameterType, i)));
    }
    LocalGenerator lg = Scene.v().createLocalGenerator(b);
    b.getUnits().add(j.newThrowStmt(lg.generateLocal(soot.RefType.v("java.lang.Throwable"))));
    if (m.getReturnType() instanceof VoidType) {
      b.getUnits().add(j.newReturnVoidStmt());
    } else if (m.getReturnType() instanceof PrimType) {
      b.getUnits().add(j.newReturnStmt(DotnetTypeFactory.initType(m.getReturnType())));
    } else {
      b.getUnits().add(j.newReturnStmt(NullConstant.v()));
    }
  }

}
