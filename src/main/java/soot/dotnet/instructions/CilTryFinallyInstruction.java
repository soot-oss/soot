package soot.dotnet.instructions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import soot.RefType;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoIlInstructions;

/**
 * Try-Finally block: finally block has to be cloned to every exit statement in the paths (rewrite), because in .NET
 * try-catch-finally-fault are blocks
 */
public class CilTryFinallyInstruction extends AbstractCilnstruction {
  public CilTryFinallyInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody,
      CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    // try block
    CilBlockContainer tryContainer
        = new CilBlockContainer(instruction.getTryBlock(), dotnetBody, CilBlockContainer.BlockContainerKind.TRY);
    Body tryContainerBlock = tryContainer.jimplify();

    // finally block
    CilBlockContainer finallyBlockContainer
        = new CilBlockContainer(instruction.getFinallyBlock(), dotnetBody, CilBlockContainer.BlockContainerKind.FINALLY);
    Body finallyBlockContainerBody = finallyBlockContainer.jimplify();

    // add finally block to all cases
    tryContainerBlock.getLocals().addAll(finallyBlockContainerBody.getLocals());
    // Restore endunits to original one, due to insert call
    Map<Trap, Unit> tmpTrapEnds = new HashMap<>();
    // store all endUnits of traps, due to insertion of finally blocks. Afterwards change back to ori. endUnit
    for (Trap trap : tryContainerBlock.getTraps()) {
      tmpTrapEnds.put(trap, trap.getEndUnit());
    }
    ArrayList<Unit> tmpUnits = new ArrayList<>();
    for (Unit unit : tryContainerBlock.getUnits()) {
      if (CilBlockContainer.isExitStmt(unit)) {
        tmpUnits.add(unit);
      }
    }
    for (Unit unit : tmpUnits) {
      // Set dummy method
      finallyBlockContainerBody.setMethod(new SootMethod("", new ArrayList<>(), RefType.v("")));
      Body cloneFinallyBlock = (Body) finallyBlockContainerBody.clone(true);
      tryContainerBlock.getUnits().insertBefore(cloneFinallyBlock.getUnits(), unit);
      // tryContainerBlock.getLocals().addAll(cloneFinallyBlock.getLocals());
      tryContainerBlock.getTraps().addAll(cloneFinallyBlock.getTraps());
    }
    for (Map.Entry<Trap, Unit> trapMap : tmpTrapEnds.entrySet()) {
      trapMap.getKey().setEndUnit(trapMap.getValue());
    }

    jb.getLocals().addAll(tryContainerBlock.getLocals());
    jb.getUnits().addAll(tryContainerBlock.getUnits());
    jb.getTraps().addAll(tryContainerBlock.getTraps());
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    throw new NoExpressionInstructionException(instruction);
  }
}
