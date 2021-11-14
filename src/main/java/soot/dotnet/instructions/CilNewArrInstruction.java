package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.members.method.DotnetBodyVariableManager;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.Jimple;

import java.util.ArrayList;
import java.util.List;

/**
 * Instantiate new array
 */
public class CilNewArrInstruction extends AbstractCilnstruction {
    public CilNewArrInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        Type type = DotnetTypeFactory.toSootType(instruction.getType().getFullname());

        List<Value> sizesOfArr = new ArrayList<>();
        for (ProtoIlInstructions.IlInstructionMsg index : instruction.getIndicesList()) {
            CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(index, dotnetBody, cilBlock);
            Value value = cilExpr.jimplifyExpr(jb);
            Value val = value instanceof Immediate ? value : DotnetBodyVariableManager.inlineLocals(value, jb);
            sizesOfArr.add(val);
        }

        // if only one dim array
        if (sizesOfArr.size() == 1)
            return Jimple.v().newNewArrayExpr(type, sizesOfArr.get(0));

        ArrayType arrayType = ArrayType.v(type, sizesOfArr.size());
        return Jimple.v().newNewMultiArrayExpr(arrayType, sizesOfArr);
    }
}
