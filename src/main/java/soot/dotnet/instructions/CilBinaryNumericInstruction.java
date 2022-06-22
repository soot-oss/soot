package soot.dotnet.instructions;

import soot.Body;
import soot.Value;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;

import static soot.dotnet.members.method.DotnetBody.inlineCastExpr;

/**
 * ILSpy opcode BinaryNumericInstruction
 */
public class CilBinaryNumericInstruction extends AbstractCilnstruction {
    public CilBinaryNumericInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
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
        switch (instruction.getOperator()) {
            case Add:
                return Jimple.v().newAddExpr(left, right);
            case Sub:
                return Jimple.v().newSubExpr(left, right);
            case Mul:
                return Jimple.v().newMulExpr(left, right);
            case Div:
                return Jimple.v().newDivExpr(left, right);
            case Rem:
                return Jimple.v().newRemExpr(left, right);
            case BitAnd:
                return Jimple.v().newAndExpr(left, right);
            case BitOr:
                return Jimple.v().newOrExpr(left, right);
            case BitXor:
                return Jimple.v().newXorExpr(left, right);
            case ShiftLeft:
                return Jimple.v().newShlExpr(left, right);
            case ShiftRight:
                if (instruction.getSign().equals(ProtoIlInstructions.IlInstructionMsg.IlSign.Signed))
                    return Jimple.v().newShrExpr(left, right);
                return Jimple.v().newUshrExpr(left, right);
            default:
                return null;
        }
    }
}
