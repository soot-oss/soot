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
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.jimple.Jimple;

/**
 * ILSpy opcode Block BlockContainer -> Block
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
    if (entryUnit == null) {
      throw new RuntimeException("getEntryUnit() was called before jimplifying!");
    }
    return entryUnit;
  }
}
