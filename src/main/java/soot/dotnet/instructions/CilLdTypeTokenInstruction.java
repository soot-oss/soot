package soot.dotnet.instructions;

import soot.Body;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.soot.DotnetClassConstant;
import soot.jimple.StringConstant;

/**
 * ldtoken was split up by ILspy: LdTypeToken for types and LdMemberToken
 * Load a type handle token (e.g. reflection)
 */
public class CilLdTypeTokenInstruction extends AbstractCilnstruction {
    public CilLdTypeTokenInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        return DotnetClassConstant.v(instruction.getValueConstantString());
        // return StringConstant.v(i.getValueConstantString());
    }
}
