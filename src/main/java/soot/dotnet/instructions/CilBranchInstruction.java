package soot.dotnet.instructions;

import soot.Body;
import soot.Unit;
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.GotoStmt;
import soot.jimple.Jimple;

/**
 * jump to target branch opcode
 */
public class CilBranchInstruction extends AbstractCilnstruction {
    public CilBranchInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        Unit target = Jimple.v().newNopStmt();
        GotoStmt gotoStmt = Jimple.v().newGotoStmt(target);

        jb.getUnits().add(gotoStmt);
        // goto target will be changed later
        dotnetBody.blockEntryPointsManager.gotoTargetsInBody.put(target, instruction.getTargetLabel());
        cilBlock.getDeclaredBlockContainer().blockEntryPointsManager.gotoTargetsInBody.put(target, instruction.getTargetLabel());
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        throw new NoExpressionInstructionException(instruction);
    }
}
