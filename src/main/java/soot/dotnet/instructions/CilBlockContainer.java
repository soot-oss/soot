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

import java.util.ArrayList;

import soot.Body;
import soot.RefType;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.dotnet.members.method.BlockEntryPointsManager;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;

/**
 * ILSpy opcode BlockContainer
 */
public class CilBlockContainer implements CilInstruction {

  protected final ProtoIlInstructions.IlBlockContainerMsg blockContainer;
  protected final DotnetBody dotnetBody;
  public final BlockEntryPointsManager blockEntryPointsManager;
  protected final Stmt skipBlockContainerStmt;
  protected final BlockContainerKind blockContainerKind;

  public CilBlockContainer(ProtoIlInstructions.IlBlockContainerMsg blockContainer, DotnetBody dotnetBody) {
    this(blockContainer, dotnetBody, BlockContainerKind.NORMAL);
  }

  public CilBlockContainer(ProtoIlInstructions.IlBlockContainerMsg blockContainer, DotnetBody dotnetBody,
      BlockContainerKind blockContainerKind) {
    this.blockContainer = blockContainer;
    this.dotnetBody = dotnetBody;
    this.blockContainerKind = blockContainerKind;
    this.blockEntryPointsManager = new BlockEntryPointsManager();

    this.skipBlockContainerStmt = Jimple.v().newNopStmt();
  }

  @Override
  public void jimplify(Body jb) {
    // if method does not contain body
    if (blockContainer == null || blockContainer.getBlocksList().size() == 0
        || blockContainer.getBlocksList().get(0).getListOfIlInstructionsCount() == 0) {
      return;
    }

    for (ProtoIlInstructions.IlBlock block : blockContainer.getBlocksList()) {
      jimplifyBlock(jb, block);
    }

    if (isChildBlockContainer()) {
      jb.getUnits().add(skipBlockContainerStmt);
    }

    afterJimplification();

    // swap labels with nop stmt to the real target
    blockEntryPointsManager.swapGotoEntriesInJBody(jb);
  }

  protected void jimplifyBlock(Body jb, ProtoIlInstructions.IlBlock block) {
    CilBlock cilBlock = new CilBlock(block, dotnetBody, this);
    cilBlock.jimplify(jb);
  }

  protected void afterJimplification() {

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
    if (jb.getUnits().size() == 0) {
      return true;
    }
    return !isExitStmt(jb.getUnits().getLast());
  }

  /**
   * Check if given unit "exists a method"
   *
   * @param unit
   * @return
   */
  static boolean isExitStmt(Unit unit) {
    return unit instanceof ReturnStmt || unit instanceof ReturnVoidStmt || unit instanceof ThrowStmt;
  }

  /**
   * Define the type of a blockcontainer, if blockcontainer is try block, etc.
   */
  public enum BlockContainerKind {
    NORMAL, TRY, // try block
    CATCH_HANDLER, // catch handler block
    CATCH_FILTER, // filter block of a catch handler
    FAULT, // fault block
    FINALLY, // finally block
    CHILD, INSTR_BLOCK
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
   *
   * @return skip stmt
   */
  public Stmt getSkipBlockContainerStmt() {
    return skipBlockContainerStmt;
  }
}
