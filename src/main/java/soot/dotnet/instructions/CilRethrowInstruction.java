package soot.dotnet.instructions;

import soot.Body;
import soot.Local;
import soot.Value;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;
import soot.jimple.ThrowStmt;

/**
 * Throw the same thrown exception again (rethrow opcode)
 */
public class CilRethrowInstruction extends AbstractCilnstruction {
    public CilRethrowInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        Local variable = dotnetBody.variableManager.addOrGetVariable(instruction.getVariable(), jb);
        ThrowStmt throwStmt = Jimple.v().newThrowStmt(variable);
        jb.getUnits().add(throwStmt);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        throw new NoExpressionInstructionException(instruction);
    }
}
