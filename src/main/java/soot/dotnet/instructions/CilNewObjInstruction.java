package soot.dotnet.instructions;

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
import soot.Local;
import soot.NullType;
import soot.Scene;
import soot.SootClass;
import soot.Value;
import soot.dotnet.members.DotnetMethod;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;

/**
 * Combi instruction with instantiating a new object and calling the constructor (no structs often) Call
 * resolveCallConstructorBody() afterwards in StLoc
 */
public class CilNewObjInstruction extends AbstractNewObjInstanceInstruction {
  private Local lhs;

  public CilNewObjInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  protected boolean hasBaseObj() {
    return false;
  }

  @Override
  public void jimplify(Body jb) {
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    this.lhs = createTempVar(jb, NullType.v());
    Value j = super.jimplifyExpr(jb);
    if (j != null) {
      throw new RuntimeException("Should never happen.");
    }
    return lhs;

  }

  @Override
  protected InvokeExpr createInvokeExpr(Body jb, SootClass clazz, DotnetMethod method, MethodParams methodParams) {
    Jimple j = Jimple.v();
    NewExpr newExpr = j.newNewExpr(clazz.getType());
    lhs.setType(clazz.getType());
    jb.getUnits().add(j.newAssignStmt(lhs, newExpr));
    methodRef = Scene.v().makeConstructorRef(clazz, methodParams.methodRef.getParameterTypes());
    return j.newSpecialInvokeExpr(lhs, methodRef, methodParams.argumentVariables);
  }

}
