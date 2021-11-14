package soot.dotnet.instructions;

import soot.Body;
import soot.Local;
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Jimple;

/**
 * AssignStmt - Store ValueTypes to a local
 */
public class CilStObjInstruction extends AbstractCilnstruction {
    public CilStObjInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        CilInstruction cilTargetExpr = CilInstructionFactory.fromInstructionMsg(instruction.getTarget(), dotnetBody, cilBlock);
        Value target = cilTargetExpr.jimplifyExpr(jb);
        CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getValueInstruction(), dotnetBody, cilBlock);
        Value value = cilExpr.jimplifyExpr(jb);

        // create this cast, to validate successfully
        if (value instanceof Local
                && !target.getType().toString().equals(value.getType().toString()))
            if (value.getType().toString().equals(DotnetBasicTypes.SYSTEM_OBJECT) &&
                    !target.getType().toString().equals(DotnetBasicTypes.SYSTEM_OBJECT))
                value = Jimple.v().newCastExpr(value, target.getType());

        // if rvalue is not single value and lvalue is static-ref, rewrite with a local variable to meet three address requirement
        if (value instanceof CastExpr && !(target instanceof Local)) {
            Local generatedLocal = dotnetBody.variableManager.localGenerator.generateLocal(target.getType());
            AssignStmt assignStmt = Jimple.v().newAssignStmt(generatedLocal, value);
            jb.getUnits().add(assignStmt);
            value = generatedLocal;
        }

        AssignStmt astm = Jimple.v().newAssignStmt(target, value);
        jb.getUnits().add(astm);

        // if new Obj also add call of constructor - relevant for structs (System.ValueType)
        if (cilExpr instanceof AbstractNewObjInstanceInstruction) {
            ((AbstractNewObjInstanceInstruction) cilExpr).resolveCallConstructorBody(jb, (Local)target);
        }
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        throw new NoExpressionInstructionException(instruction);
    }
}
