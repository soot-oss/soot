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
import soot.Unit;
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.ConditionExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

/**
 *
 */
public class CilIfInstruction extends AbstractCilnstruction {
  public CilIfInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getCondition(), dotnetBody, cilBlock);
    Value condition = cilExpr.jimplifyExpr(jb);

    // if condition only accepts ConditionExpr and not JimpleLocals
    Value eqExpr;
    if (condition instanceof ConditionExpr) {
      eqExpr = condition;
    } else {
      // Store expression to local variable and check if true in second instruction
      Local tmpLocalCond = dotnetBody.variableManager.localGenerator.generateLocal(condition.getType());
      jb.getUnits().add(Jimple.v().newAssignStmt(tmpLocalCond, condition));
      eqExpr = Jimple.v().newNeExpr(tmpLocalCond, IntConstant.v(0));
    }

    // for such cases in ILSpy AST as: if (comp.i4(ldloc capacity == ldloc num5)) leave IL_0000 (nop)
    CilInstruction trueInstruction
        = CilInstructionFactory.fromInstructionMsg(instruction.getTrueInst(), dotnetBody, cilBlock);

    Unit trueInstruct = Jimple.v().newNopStmt(); // dummy stmt replace later
    IfStmt ifStmt = Jimple.v().newIfStmt(eqExpr, trueInstruct);

    jb.getUnits().add(ifStmt);
    String target
        = trueInstruction instanceof CilLeaveInstruction ? "RETURNLEAVE" : instruction.getTrueInst().getTargetLabel();
    // dotnetBody.blockEntryPointsManager.gotoTargetsInBody.put(trueInstruct, target);
    cilBlock.getDeclaredBlockContainer().blockEntryPointsManager.gotoTargetsInBody.put(trueInstruct, target);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    throw new NoExpressionInstructionException(instruction);
  }
}
