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
import soot.Scene;
import soot.SootClass;
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotNetBasicTypes;
import soot.jimple.DoubleConstant;
import soot.jimple.EqExpr;
import soot.jimple.Jimple;
import soot.jimple.NeExpr;
import soot.jimple.NopStmt;
import soot.jimple.ThrowStmt;

/**
 * Check for finite value otherwise infinity exception instruction
 */
public class CilCkFiniteInstruction extends AbstractCilnstruction {
  public CilCkFiniteInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody,
      CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArgument(), dotnetBody, cilBlock);
    Value argument = cilExpr.jimplifyExpr(jb);
    DoubleConstant posInfinity = DoubleConstant.v(Double.POSITIVE_INFINITY);
    DoubleConstant negInfinity = DoubleConstant.v(Double.NEGATIVE_INFINITY);

    // infinity conditions
    EqExpr eqPosInfExpr = Jimple.v().newEqExpr(argument, posInfinity);
    EqExpr eqNegInfExpr = Jimple.v().newEqExpr(argument, negInfinity);
    NeExpr eqNaNExpr = Jimple.v().newNeExpr(argument, argument);

    // if value is infinity, throw exception
    SootClass exceptionClass = Scene.v().getSootClass(DotNetBasicTypes.SYSTEM_ARITHMETICEXCEPTION);
    Local tmpLocalVar = dotnetBody.variableManager.localGenerator.generateLocal(exceptionClass.getType());
    ThrowStmt throwStmt = Jimple.v().newThrowStmt(tmpLocalVar);

    // if true throw exception
    jb.getUnits().add(Jimple.v().newIfStmt(eqPosInfExpr, throwStmt));
    jb.getUnits().add(Jimple.v().newIfStmt(eqNegInfExpr, throwStmt));
    jb.getUnits().add(Jimple.v().newIfStmt(eqNaNExpr, throwStmt));
    // if false goto
    NopStmt nopStmt = Jimple.v().newNopStmt();
    jb.getUnits().add(Jimple.v().newGotoStmt(nopStmt));
    jb.getUnits().add(throwStmt);
    jb.getUnits().add(nopStmt);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    throw new NoExpressionInstructionException(instruction);
  }
}
