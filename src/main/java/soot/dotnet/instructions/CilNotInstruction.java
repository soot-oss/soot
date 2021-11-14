package soot.dotnet.instructions;

import soot.Body;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;

/**
 * BitNot = Bytes XOR –1 (= b1111) (iconst_m1) (1-complement)
 * bit-not differs from neg (negation instruction for integers)
 */
public class CilNotInstruction extends AbstractCilnstruction {
    public CilNotInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArgument(), dotnetBody, cilBlock);
        Value argument = cilExpr.jimplifyExpr(jb);
        return Jimple.v().newXorExpr(argument, IntConstant.v(-1));
    }
}
