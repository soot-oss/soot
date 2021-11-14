package soot.dotnet.instructions;

import soot.Body;
import soot.Type;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.Jimple;

/**
 * Cast class, unbox, box instruction
 */
public class CilCastClassUnBoxInstruction extends AbstractCilnstruction {
    public CilCastClassUnBoxInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        Type type = DotnetTypeFactory.toSootType(instruction.getType());
        CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArgument(), dotnetBody, cilBlock);
        Value argument = cilExpr.jimplifyExpr(jb);
        return Jimple.v().newCastExpr(argument, type);
    }
}
