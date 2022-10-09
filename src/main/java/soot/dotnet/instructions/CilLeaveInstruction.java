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
import soot.Immediate;
import soot.Local;
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;

/**
 * return stmt - leave a given block
 */
public class CilLeaveInstruction extends AbstractCilnstruction {
  public CilLeaveInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    // void
    if (instruction.getValueInstruction().getOpCode().equals(ProtoIlInstructions.IlInstructionMsg.IlOpCode.NOP)) {
      jb.getUnits().add(Jimple.v().newReturnVoidStmt());
    } else {
      CilInstruction cilValueExpr
          = CilInstructionFactory.fromInstructionMsg(instruction.getValueInstruction(), dotnetBody, cilBlock);
      Value value = cilValueExpr.jimplifyExpr(jb);

      // if sth like return new Obj, rewrite value to tmp variable
      if (cilValueExpr instanceof AbstractNewObjInstanceInstruction) {
        // CilStLocInstruction stLocInstruction = new CilStLocInstruction(instruction, dotnetBody);
        // stLocInstruction.jimplify(jb);

        Local tmpVariable = dotnetBody.variableManager.localGenerator.generateLocal(value.getType());
        AssignStmt assignStmt = Jimple.v().newAssignStmt(tmpVariable, value);
        jb.getUnits().add(assignStmt);

        ((AbstractNewObjInstanceInstruction) cilValueExpr).resolveCallConstructorBody(jb, tmpVariable);

        ReturnStmt ret = Jimple.v().newReturnStmt(tmpVariable);
        jb.getUnits().add(ret);
        return;
      }
      // Jimple grammar does not allow returning static, instead assign to tmp variable
      // if System.Array.Empty of CilCallVirtInstruction (newExpr), rewrite
      if (!(value instanceof Immediate)) {
        // CilStLocInstruction stLocInstruction = new CilStLocInstruction(instruction, dotnetBody);
        // stLocInstruction.jimplify(jb);

        Local tmpVariable = dotnetBody.variableManager.localGenerator.generateLocal(value.getType());
        AssignStmt assignStmt = Jimple.v().newAssignStmt(tmpVariable, value);
        jb.getUnits().add(assignStmt);

        ReturnStmt ret = Jimple.v().newReturnStmt(tmpVariable);
        jb.getUnits().add(ret);
        return;
      }

      ReturnStmt ret = Jimple.v().newReturnStmt(value);
      jb.getUnits().add(ret);
    }
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    throw new NoExpressionInstructionException(instruction);
  }
}
