package soot.dotnet.instructions;

import soot.Body;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;

/**
 * @deprecated rewritten, cannot be used this way
 */
@Deprecated
public class CilLdVirtFtnInstruction extends AbstractCilnstruction {
    public CilLdVirtFtnInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException();
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        return new CilCallVirtInstruction(instruction, dotnetBody, cilBlock).jimplifyExpr(jb);
        // return StringConstant.v(i.getMethod().getFullName());
    }
}
