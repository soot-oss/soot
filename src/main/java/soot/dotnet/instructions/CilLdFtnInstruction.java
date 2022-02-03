package soot.dotnet.instructions;

import soot.Body;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetFakeLdFtnType;

/**
 * Create Fake stub for LdFtn (load function), cannot be represented in Jimple
 */
public class CilLdFtnInstruction extends AbstractCilnstruction {

    public CilLdFtnInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    /**
     * Call Expression
     * @param jb
     * @return
     */
    @Override
    public Value jimplifyExpr(Body jb) {
        return DotnetFakeLdFtnType.makeMethod();
    }

}
