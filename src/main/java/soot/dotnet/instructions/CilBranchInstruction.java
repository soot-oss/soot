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
    cilBlock.getDeclaredBlockContainer().blockEntryPointsManager.gotoTargetsInBody.put(target,
        instruction.getTargetLabel());
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    throw new NoExpressionInstructionException(instruction);
  }
}
