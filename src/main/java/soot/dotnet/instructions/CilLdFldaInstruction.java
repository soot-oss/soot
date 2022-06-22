package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.Jimple;

/**
 * Load a non-static field (only non-static)
 */
public class CilLdFldaInstruction extends AbstractCilnstruction {
    public CilLdFldaInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        // ldflda is only non-static
        SootClass declaringClass = SootResolver.v().makeClassRef(instruction.getField().getDeclaringType().getFullname());

        SootFieldRef fieldRef = Scene.v().makeFieldRef(
                declaringClass,
                instruction.getField().getName(),
                DotnetTypeFactory.toSootType(instruction.getField().getType()),
                false);
        CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getTarget(), dotnetBody, cilBlock);
        Value target = cilExpr.jimplifyExpr(jb);

        return Jimple.v().newInstanceFieldRef(target, fieldRef);
    }
}
