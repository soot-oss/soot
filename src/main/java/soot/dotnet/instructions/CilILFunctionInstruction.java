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
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions.IlInstructionMsg;
import soot.dotnet.types.DotNetBasicTypes;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.AssignStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;

/**
 * CIL isinst differs from instanceof: isinst returns object or null, while instanceof returns a boolean - rewrite
 */
public class CilILFunctionInstruction extends AbstractCilnstruction {
  public CilILFunctionInstruction(IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    String type = instruction.getType().getFullname();
    CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArgument(), dotnetBody, cilBlock);
    Value argument = cilExpr.jimplifyExpr(jb);
    argument = simplifyComplexExpression(jb, argument);
    return Jimple.v().newInstanceOfExpr(argument, DotnetTypeFactory.toSootType(type));
  }

  public void resolveRewritingIsInst(Body jb, Local variable, Value instanceOfExpr) {

    Local local = dotnetBody.variableManager.localGenerator
        .generateLocal(DotnetTypeFactory.toSootType(DotNetBasicTypes.SYSTEM_BOOLEAN));
    AssignStmt assignInstanceOfStmt = Jimple.v().newAssignStmt(local, instanceOfExpr);
    NopStmt nopStmt = Jimple.v().newNopStmt();
    AssignStmt assignIfTrueStmt = Jimple.v().newAssignStmt(variable, ((InstanceOfExpr) instanceOfExpr).getOp());
    AssignStmt assignIfFalseStmt = Jimple.v().newAssignStmt(variable, NullConstant.v());
    IfStmt ifStmt = Jimple.v().newIfStmt(Jimple.v().newEqExpr(local, IntConstant.v(1)), assignIfTrueStmt);
    GotoStmt gotoStmt = Jimple.v().newGotoStmt(nopStmt);

    jb.getUnits().add(assignInstanceOfStmt);
    jb.getUnits().add(ifStmt);
    jb.getUnits().add(assignIfFalseStmt);
    jb.getUnits().add(gotoStmt);
    jb.getUnits().add(assignIfTrueStmt);
    jb.getUnits().add(nopStmt);

    dotnetBody.variableManager.addLocalsToCast(variable.getName());
  }
}
