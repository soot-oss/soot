package soot.dotnet.instructions;

import soot.Body;
import soot.Value;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.proto.ProtoIlInstructions.IlBlockContainerMsg;
import soot.dotnet.proto.ProtoIlInstructions.IlInstructionMsg;

public class CILBlockContainerInstr extends CilBlockContainer {

  private Value expr;

  public CILBlockContainerInstr(IlBlockContainerMsg block, DotnetBody dotnetBody, BlockContainerKind kind) {
    super(block, dotnetBody, kind);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    IlInstructionMsg finalInst = blockContainer.getBlocks(0).getFinalInstruction();
    if (finalInst == null) {
      //The final instruction should contain the result of the expression...
      throw new RuntimeException("Final instruction expected for expression jimplification!");
    }
    jimplify(jb);
    return expr;
  }

  @Override
  public boolean isChildBlockContainer() {
    return false;
  }

  protected void jimplifyBlock(Body jb, ProtoIlInstructions.IlBlock block) {
    CilBlock cilBlock = new CilBlock(block, dotnetBody, this);
    cilBlock.jimplify(jb);
    IlInstructionMsg fiinstr = block.getFinalInstruction();
    CilInstruction cilInstruction = CilInstructionFactory.fromInstructionMsg(fiinstr, dotnetBody, cilBlock);
    expr = cilInstruction.jimplifyExpr(jb);

  }

}
