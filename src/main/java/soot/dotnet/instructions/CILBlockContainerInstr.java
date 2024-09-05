package soot.dotnet.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
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
