package soot.dotnet.instructions;

import soot.Body;
import soot.Value;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;

/**
 * @deprecated rewritten, cannot be used in this way
 */
@Deprecated
public class CilLdObjInstruction extends AbstractCilnstruction {


    public CilLdObjInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new RuntimeException("only expression!");
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        CilInstruction cilInstruction = CilInstructionFactory.fromInstructionMsg(instruction.getTarget(), dotnetBody, cilBlock);
        return cilInstruction.jimplifyExpr(jb);
    }
}
