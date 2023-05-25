package soot.dotnet.members.method;

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
import soot.Immediate;
import soot.Local;
import soot.LocalGenerator;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.VoidType;
import soot.dotnet.instructions.CilBlockContainer;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.CastExpr;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NullConstant;

/**
 * Represents a .NET Method Body A method body starts with a BlockContainer, which contains Blocks, which have IL
 * Instructions .NET Method Body (with ILSpy AST) -> BlockContainer -> Block -> IL Instruction
 */
public class DotnetBody {

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
    variableManager.fillMethodParameter();
    variableManager.addInitLocalVariables(ilFunctionMsg.getVariablesList());

    // Resolve .NET Method Body -> BlockContainer -> Block -> IL Instruction
    CilBlockContainer blockContainer = new CilBlockContainer(ilFunctionMsg.getBody(), this);
    Body b = blockContainer.jimplify();
    this.jb.getUnits().addAll(b.getUnits());
    this.jb.getTraps().addAll(b.getTraps());
    blockEntryPointsManager.swapGotoEntriesInJBody(this.jb);
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
    // if not static add this stmt
    if (!m.isStatic()) {
      RefType thisType = m.getDeclaringClass().getType();
      Local l = Jimple.v().newLocal("this", thisType);
      IdentityStmt identityStmt = Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef(thisType));
      b.getLocals().add(l);
      b.getUnits().add(identityStmt);
    }
    // parameters
    for (int i = 0; i < m.getParameterCount(); i++) {
      Type parameterType = m.getParameterType(i);
      Local paramLocal = Jimple.v().newLocal("arg" + i, parameterType);
      b.getLocals().add(paramLocal);
      b.getUnits().add(Jimple.v().newIdentityStmt(paramLocal, Jimple.v().newParameterRef(parameterType, i)));
    }
    LocalGenerator lg = Scene.v().createLocalGenerator(b);
    b.getUnits().add(Jimple.v().newThrowStmt(lg.generateLocal(soot.RefType.v("java.lang.Throwable"))));
    if (m.getReturnType() instanceof VoidType) {
      b.getUnits().add(Jimple.v().newReturnVoidStmt());
    } else if (m.getReturnType() instanceof PrimType) {
      b.getUnits().add(Jimple.v().newReturnStmt(DotnetTypeFactory.initType(m.getReturnType())));
    } else {
      b.getUnits().add(Jimple.v().newReturnStmt(NullConstant.v()));
    }
  }

}
