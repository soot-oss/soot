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

import static soot.dotnet.members.method.DotnetBody.inlineCastExpr;

import soot.Body;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.proto.ProtoIlInstructions.IlInstructionMsg.IlComparisonKind;
import soot.jimple.BinopExpr;
import soot.jimple.Constant;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LeExpr;
import soot.jimple.LtExpr;
import soot.jimple.NeExpr;

/**
 * Compare opcode
 */
public class CilCompInstruction extends AbstractCilnstruction {
  public CilCompInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    throw new NoStatementInstructionException(instruction);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    Value left = CilInstructionFactory.fromInstructionMsg(instruction.getLeft(), dotnetBody, cilBlock).jimplifyExpr(jb);
    left = inlineCastExpr(left);
    left = simplifyComplexExpression(jb, left);
    Value right = CilInstructionFactory.fromInstructionMsg(instruction.getRight(), dotnetBody, cilBlock).jimplifyExpr(jb);
    right = inlineCastExpr(right);
    right = simplifyComplexExpression(jb, right);
    IlComparisonKind comparisonKind = instruction.getComparisonKind();
    if (right instanceof BinopExpr && left instanceof Constant) {
      if (comparisonKind == IlComparisonKind.Equality || comparisonKind == IlComparisonKind.Inequality) {
        Value tempRight = right;
        right = left;
        left = tempRight;
      }
    }

    if (left instanceof BinopExpr && right instanceof IntConstant) {
      boolean expectedValueTrue;
      IntConstant c = (IntConstant) right;
      if (c.value == 0) {
        expectedValueTrue = false;
      } else if (c.value == 1) {
        expectedValueTrue = true;
      } else {
        throw new RuntimeException("Missing case for c.value");
      }

      if (comparisonKind == IlComparisonKind.Inequality) {
        expectedValueTrue = !expectedValueTrue;
      }
      if (expectedValueTrue) {
        return left;
      } else {
        BinopExpr binop = (BinopExpr) left;
        if (left instanceof EqExpr) {
          return Jimple.v().newNeExpr(binop.getOp1(), binop.getOp2());
        }
        if (left instanceof NeExpr) {
          return Jimple.v().newEqExpr(binop.getOp1(), binop.getOp2());
        }
        if (left instanceof LtExpr) {
          return Jimple.v().newGeExpr(binop.getOp1(), binop.getOp2());
        }
        if (left instanceof LeExpr) {
          return Jimple.v().newGtExpr(binop.getOp1(), binop.getOp2());
        }
        if (left instanceof GeExpr) {
          return Jimple.v().newLtExpr(binop.getOp1(), binop.getOp2());
        }
        if (left instanceof GtExpr) {
          return Jimple.v().newLeExpr(binop.getOp1(), binop.getOp2());
        } else {
          return null;
        }
      }
    }

    switch (comparisonKind) {
      case Equality:
        return Jimple.v().newEqExpr(left, right);
      case Inequality:
        return Jimple.v().newNeExpr(left, right);
      case LessThan:
        return Jimple.v().newLtExpr(left, right);
      case LessThanOrEqual:
        return Jimple.v().newLeExpr(left, right);
      case GreaterThan:
        return Jimple.v().newGtExpr(left, right);
      case GreaterThanOrEqual:
        return Jimple.v().newGeExpr(left, right);
      default:
        return null;
    }
  }
}
