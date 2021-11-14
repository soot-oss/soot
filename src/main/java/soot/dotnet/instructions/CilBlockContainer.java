package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.members.method.BlockEntryPointsManager;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ThrowStmt;

import java.util.ArrayList;

/**
 * ILSpy opcode BlockContainer
 */
public class CilBlockContainer implements CilInstruction {

    private final ProtoIlInstructions.IlBlockContainerMsg blockContainer;

    public DotnetBody getDeclaringDotnetBody() {
        return dotnetBody;
    }

    private final DotnetBody dotnetBody;
    public final BlockEntryPointsManager blockEntryPointsManager;

    public CilBlockContainer(ProtoIlInstructions.IlBlockContainerMsg blockContainer, DotnetBody dotnetBody) {
        this.blockContainer = blockContainer;
        this.dotnetBody = dotnetBody;
        this.blockEntryPointsManager = new BlockEntryPointsManager();
    }

    @Override
    public void jimplify(Body jb) {
        // if method does not contain body
        if (blockContainer == null
                || blockContainer.getBlocksList().size() == 0
                || blockContainer.getBlocksList().get(0).getListOfIlInstructionsCount() == 0) {
            return;
        }

        for (ProtoIlInstructions.IlBlock block : blockContainer.getBlocksList()) {
            CilBlock cilBlock = new CilBlock(block, dotnetBody, this);
            cilBlock.jimplify(jb);
        }

        // swap labels with nop stmt to the real target
        blockEntryPointsManager.swapGotoEntriesInJBody(jb);
    }

    public Body jimplify() {
        Body jbTmp = Jimple.v().newBody();
        jbTmp.setMethod(new SootMethod("", new ArrayList<>(), RefType.v(""))); // Set dummy method
        jimplify(jbTmp);
        return jbTmp;
    }

    @Override
    public Value jimplifyExpr(Body jb) {
        throw new RuntimeException(this.getClass().getName() + " does not have expressions, but statements!");
    }

    public static boolean LastStmtIsNotReturn(Body jb) {
        if (jb.getUnits().size() == 0)
            return true;
        return !isExitStmt(jb.getUnits().getLast());
    }

    /**
     * Check if given unit "exists a method"
     * @param unit
     * @return
     */
    static boolean isExitStmt(Unit unit) {
        return unit instanceof ReturnStmt ||
                unit instanceof ReturnVoidStmt ||
                unit instanceof ThrowStmt;
    }
}
