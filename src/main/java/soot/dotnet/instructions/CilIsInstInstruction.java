package soot.dotnet.instructions;

import soot.Body;
import soot.Local;
import soot.Value;
import soot.dotnet.exceptions.NoStatementInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.dotnet.types.DotnetTypeFactory;
import soot.jimple.*;

/**
 * CIL isinst differs from instanceof: isinst returns object or null, while instanceof returns a boolean - rewrite
 */
public class CilIsInstInstruction extends AbstractCilnstruction {
    public CilIsInstInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        throw new NoStatementInstructionException(instruction);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        String type = instruction.getType().getFullname();
        CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getArgument(), dotnetBody, cilBlock);
        Value argument = cilExpr.jimplifyExpr(jb);
        return Jimple.v().newInstanceOfExpr(argument, DotnetTypeFactory.toSootType(type));
    }

    public void resolveRewritingIsInst(Body jb, Local variable, Value instanceOfExpr) {

        Local local = dotnetBody.variableManager.localGenerator.generateLocal(DotnetTypeFactory.toSootType(DotnetBasicTypes.SYSTEM_BOOLEAN));
        AssignStmt assignInstanceOfStmt = Jimple.v().newAssignStmt(local, instanceOfExpr);
        NopStmt nopStmt = Jimple.v().newNopStmt();
        AssignStmt assignIfTrueStmt = Jimple.v().newAssignStmt(variable, ((InstanceOfExpr) instanceOfExpr).getOp());
        AssignStmt assignIfFalseStmt = Jimple.v().newAssignStmt(variable, NullConstant.v());
        IfStmt ifStmt = Jimple.v().newIfStmt(Jimple.v().newEqExpr(local, IntConstant.v(1)), assignIfTrueStmt);
        GotoStmt gotoStmt = Jimple.v().newGotoStmt(nopStmt);

        jb.getUnits().add(assignInstanceOfStmt);
        jb.getUnits().add(ifStmt);
        jb.getUnits().add(assignIfFalseStmt);
        jb.getUnits().add(gotoStmt);
        jb.getUnits().add(assignIfTrueStmt);
        jb.getUnits().add(nopStmt);

        dotnetBody.variableManager.addLocalsToCast(variable.getName());
    }
}
