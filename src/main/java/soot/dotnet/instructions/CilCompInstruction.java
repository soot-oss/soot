package soot.dotnet.instructions;

import soot.Body;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;

import static soot.dotnet.members.method.DotnetBody.inlineCastExpr;

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
        Value right = CilInstructionFactory.fromInstructionMsg(instruction.getRight(), dotnetBody, cilBlock).jimplifyExpr(jb);
        right = inlineCastExpr(right);
        switch (instruction.getComparisonKind()) {
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
