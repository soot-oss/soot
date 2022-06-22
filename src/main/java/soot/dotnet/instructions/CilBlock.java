package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;

import java.util.ArrayList;

/**
 * ILSpy opcode Block
 * BlockContainer -> Block
 */
public class CilBlock implements CilInstruction {

    private final ProtoIlInstructions.IlBlock block;
    private final DotnetBody dotnetBody;

    public CilBlockContainer getDeclaredBlockContainer() {
        return blockContainer;
    }

    private final CilBlockContainer blockContainer;
    private Unit entryUnit = null;

    public CilBlock(ProtoIlInstructions.IlBlock block, DotnetBody dotnetBody, CilBlockContainer blockContainer) {
        this.block = block;
        this.dotnetBody = dotnetBody;
        this.blockContainer = blockContainer;
    }

    @Override
    public void jimplify(Body jb) {
        boolean setBlockGotoStmt = false;

        Body jbTmp = Jimple.v().newBody();
        jbTmp.setMethod(new SootMethod("", new ArrayList<>(), RefType.v(""))); // Set dummy method

        for (ProtoIlInstructions.IlInstructionMsg instruction : block.getListOfIlInstructionsList()) {
            CilInstruction cilInstruction = CilInstructionFactory.fromInstructionMsg(instruction, dotnetBody, this);
            cilInstruction.jimplify(jbTmp);

            // register block with target_name
            if (!setBlockGotoStmt && jbTmp.getUnits().size() != 0) {
                setBlockGotoStmt = true;
                entryUnit = jbTmp.getUnits().getFirst();
                dotnetBody.blockEntryPointsManager.putBlockEntryPoint(getBlockName(), entryUnit);
                blockContainer.blockEntryPointsManager.putBlockEntryPoint(getBlockName(), entryUnit);
            }
        }

        // register block target_name end
        if (jbTmp.getUnits().size() != 0) {
            dotnetBody.blockEntryPointsManager.putBlockEntryPoint("END_" + getBlockName(), jbTmp.getUnits().getLast());
            blockContainer.blockEntryPointsManager.putBlockEntryPoint("END_" + getBlockName(), entryUnit);
        }

        jb.getUnits().addAll(jbTmp.getUnits());
        jb.getLocals().addAll(jbTmp.getLocals());
        jb.getTraps().addAll(jbTmp.getTraps());
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        throw new NoExpressionInstructionException();
    }

    public String getBlockName() {
        return block.getBlockName();
    }

    public Unit getEntryUnit() {
        if (entryUnit == null)
            throw new RuntimeException("getEntryUnit() was called before jimplifying!");
        return entryUnit;
    }
}
