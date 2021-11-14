package soot.dotnet.instructions;

import soot.Body;
import soot.Unit;
import soot.Value;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;
import soot.jimple.NopStmt;
import soot.jimple.TableSwitchStmt;

import java.util.ArrayList;
import java.util.List;

public class CilSwitchInstruction extends AbstractCilnstruction {
    public CilSwitchInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
        super(instruction, dotnetBody, cilBlock);
    }

    @Override
    public void jimplify(Body jb) {
        CilInstruction cilExpr = CilInstructionFactory.fromInstructionMsg(instruction.getKeyInstr(), dotnetBody, cilBlock);
        Value key = cilExpr.jimplifyExpr(jb);
        int lowIndex = (int) instruction.getSwitchSections(0).getLabel();
        int highIndex = (int) instruction.getSwitchSections(instruction.getSwitchSectionsCount() - 1).getLabel();
        Unit defaultInstruct = Jimple.v().newNopStmt(); // dummy
        switch (instruction.getDefaultInst().getOpCode()) {
            case BRANCH:
                cilBlock.getDeclaredBlockContainer().blockEntryPointsManager.gotoTargetsInBody.put(defaultInstruct, instruction.getDefaultInst().getTargetLabel());
                break;
            case LEAVE:
                dotnetBody.blockEntryPointsManager.gotoTargetsInBody.put(defaultInstruct, "RETURNLEAVE"); // TODO return label of blockcontainer and block change
                break;
            default:
                throw new RuntimeException("CilSwitchInstruction: Opcode " + instruction.getDefaultInst().getOpCode().name() + " not implemented!");
        }

        List<Unit> targets = new ArrayList<>();
        for (ProtoIlInstructions.IlSwitchSectionMsg section : instruction.getSwitchSectionsList()) {
            NopStmt nopStmt = Jimple.v().newNopStmt(); // dummy target
            targets.add(nopStmt);
            switch (section.getTargetInstr().getOpCode()) {
                case BRANCH:
                    // dotnetBody.blockEntryPointsManager.gotoTargetsInBody.put(nopStmt, section.getTargetInstr().getTargetLabel());
                    cilBlock.getDeclaredBlockContainer().blockEntryPointsManager.gotoTargetsInBody.put(nopStmt, section.getTargetInstr().getTargetLabel());
                    break;
                case LEAVE:
                    // dotnetBody.blockEntryPointsManager.gotoTargetsInBody.put(nopStmt, "END_" + section.getTargetInstr().getTargetLabel());
                    dotnetBody.blockEntryPointsManager.gotoTargetsInBody.put(nopStmt, "RETURNLEAVE");
                    break;
                default:
                    throw new RuntimeException("CilSwitchInstruction: Opcode " + section.getTargetInstr().getOpCode().name() + " not implemented!");
            }
        }

        TableSwitchStmt tableSwitchStmt = Jimple.v().newTableSwitchStmt(key, lowIndex, highIndex, targets, defaultInstruct);
        jb.getUnits().add(tableSwitchStmt);
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        throw new NoExpressionInstructionException(instruction);
    }
}
