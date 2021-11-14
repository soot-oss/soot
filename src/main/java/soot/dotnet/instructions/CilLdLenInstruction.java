package soot.dotnet.instructions;

import soot.ArrayType;
import soot.Body;
import soot.Immediate;
import soot.Value;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;

/**
 * Load length of array
 */
public class CilLdLenInstruction extends AbstractCilnstruction {
    public CilLdLenInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArray(), dotnetBody, cilBlock);
        Value arr = cilExpr.jimplifyExpr(jb);
        if (!(arr instanceof Immediate))
            throw new RuntimeException("LdLen: Given value is no Immediate!");
        return Jimple.v().newLengthExpr(arr);
    }
}
