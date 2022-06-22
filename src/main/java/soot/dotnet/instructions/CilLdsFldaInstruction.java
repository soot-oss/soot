package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.AbstractDotnetMember;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.Jimple;

/**
 * Load a static field (only static)
 */
public class CilLdsFldaInstruction extends AbstractCilnstruction {
    public CilLdsFldaInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        // ldsflda is-static
        SootClass declaringClass = SootResolver.v().makeClassRef(instruction.getField().getDeclaringType().getFullname());

        // If System.String.Empty
        Value rewriteField = AbstractDotnetMember.checkRewriteCilSpecificMember(declaringClass, instruction.getField().getName());
        if (rewriteField != null)
            return rewriteField;

        SootFieldRef fieldRef = Scene.v().makeFieldRef(
                declaringClass,
                instruction.getField().getName(),
                DotnetTypeFactory.toSootType(instruction.getField().getType()),
                true);
        return Jimple.v().newStaticFieldRef(fieldRef);
    }
}
