package soot.dotnet.instructions;

import soot.*;
import soot.dotnet.members.method.BlockEntryPointsManager;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.*;

import java.util.ArrayList;

/**
 * ILSpy opcode BlockContainer
 */
public class CilBlockContainer implements CilInstruction {

    private final ProtoIlInstructions.IlBlockContainerMsg blockContainer;
    private final DotnetBody dotnetBody;
    public final BlockEntryPointsManager blockEntryPointsManager;
    private final Stmt skipBlockContainerStmt;
    private final BlockContainerKind blockContainerKind;

    public CilBlockContainer(ProtoIlInstructions.IlBlockContainerMsg blockContainer, DotnetBody dotnetBody) {
        this(blockContainer, dotnetBody, BlockContainerKind.NORMAL);
    }

    public CilBlockContainer(ProtoIlInstructions.IlBlockContainerMsg blockContainer, DotnetBody dotnetBody, BlockContainerKind blockContainerKind) {
        this.blockContainer = blockContainer;
        this.dotnetBody = dotnetBody;
        this.blockContainerKind = blockContainerKind;
        this.blockEntryPointsManager = new BlockEntryPointsManager();

        this.skipBlockContainerStmt = Jimple.v().newNopStmt();
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

        if (isChildBlockContainer())
            jb.getUnits().add(skipBlockContainerStmt);

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

    /**
     * Define the type of a blockcontainer, if blockcontainer is try block, etc.
     */
    public enum BlockContainerKind {
        NORMAL,
        TRY,            // try block
        CATCH_HANDLER,  // catch handler block
        CATCH_FILTER,   // filter block of a catch handler
        FAULT,          // fault block
        FINALLY,        // finally block
        CHILD
    }

    public DotnetBody getDeclaringDotnetBody() {
        return dotnetBody;
    }

    public boolean isChildBlockContainer() {
        return !getBlockContainerKind().equals(BlockContainerKind.NORMAL);
    }

    public BlockContainerKind getBlockContainerKind() {
        return blockContainerKind;
    }

    /**
     * Get the stmt with which this container is skipped (goto)
     * @return skip stmt
     */
    public Stmt getSkipBlockContainerStmt() {
        return skipBlockContainerStmt;
    }
}
