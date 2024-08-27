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
import soot.ByteConstant;
import soot.Immediate;
import soot.Local;
import soot.ShortConstant;
import soot.UByteConstant;
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.ArrayByReferenceWrapperGenerator;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.proto.ProtoIlInstructions.IlInstructionMsg.IlOpCode;
import soot.dotnet.types.DotNetBasicTypes;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.UShortConstant;

/**
 * AssignStmt - Store ValueTypes to a local
 */
public class CilStObjInstruction extends AbstractCilnstruction {
  public CilStObjInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    Value target;
    if (instruction.getTarget().getOpCode() == IlOpCode.LDELEMA) {
      CilInstruction cilTargetExpr = new CilLdElemInstruction(instruction.getTarget(), dotnetBody, cilBlock);
      target = cilTargetExpr.jimplifyExpr(jb);
    } else {
      CilInstruction cilTargetExpr = CilInstructionFactory.fromInstructionMsg(instruction.getTarget(), dotnetBody, cilBlock);
      target = cilTargetExpr.jimplifyExpr(jb);
    }
    CilInstruction cilExpr
        = CilInstructionFactory.fromInstructionMsg(instruction.getValueInstruction(), dotnetBody, cilBlock);
    Value value = cilExpr.jimplifyExpr(jb);

    if (value instanceof IntConstant) {
      IntConstant ii = (IntConstant) value;
      switch (instruction.getTarget().getType().getFullname()) {
        case "System.Byte":
          value = UByteConstant.v((byte) ii.value);
          break;
        case "System.SByte":
          value = ByteConstant.v((byte) ii.value);
          break;
        case "System.Int16":
          value = ShortConstant.v((short) ii.value);
          break;
        case "System.UInt16":
          value = UShortConstant.v((short) ii.value);
          break;
      }
    }

    // create this cast, to validate successfully
    if (value instanceof Local && !target.getType().toString().equals(value.getType().toString())) {
      if (value.getType().toString().equals(DotNetBasicTypes.SYSTEM_OBJECT)) {
        value = Jimple.v().newCastExpr(value, target.getType());
      }
    }

    // if rvalue is not single value and lvalue is static-ref, rewrite with a local variable to meet three address
    // requirement
    if (value instanceof CastExpr && !(target instanceof Local)) {
      Local generatedLocal = dotnetBody.variableManager.localGenerator.generateLocal(target.getType());
      AssignStmt assignStmt = Jimple.v().newAssignStmt(generatedLocal, value);
      jb.getUnits().add(assignStmt);
      value = generatedLocal;
    }

    if (!(value instanceof Immediate) && !(target instanceof Local)) {
      value = simplifyComplexExpression(jb, value);
    }
    if (target instanceof CastExpr) {
      // weird but ok
      target = ((CastExpr) target).getOp();
    }
    if (target instanceof Local) {
      Local l = (Local) target;
      Local ref = dotnetBody.variableManager.getReferenceLocal(l);
      if (ref != null) {
        jb.getUnits().add(ArrayByReferenceWrapperGenerator.createSet(ref, value));
      }
    }

    AssignStmt astm = Jimple.v().newAssignStmt(target, value);
    jb.getUnits().add(astm);

  }

  @Override
  public Value jimplifyExpr(Body jb) {
    throw new NoExpressionInstructionException(instruction);
  }
}
