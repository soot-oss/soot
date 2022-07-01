package soot.dotnet.instructions;

import java.util.ArrayList;
import java.util.List;

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
import soot.jimple.Jimple;
import soot.jimple.NopStmt;
import soot.jimple.TableSwitchStmt;

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

    // default target
    Unit defaultInstruct = Jimple.v().newNopStmt(); // dummy
    switch (instruction.getDefaultInst().getOpCode()) {
      case BRANCH:
        cilBlock.getDeclaredBlockContainer().blockEntryPointsManager.gotoTargetsInBody.put(defaultInstruct,
            instruction.getDefaultInst().getTargetLabel());
        break;
      case LEAVE:
        if (cilBlock.getDeclaredBlockContainer().isChildBlockContainer()
            && !instruction.getDefaultInst().getTargetLabel().equals("IL_0000")) {
          defaultInstruct = cilBlock.getDeclaredBlockContainer().getSkipBlockContainerStmt(); // if child blockcontainer,
                                                                                              // jump to end of it
        } else {
          dotnetBody.blockEntryPointsManager.gotoTargetsInBody.put(defaultInstruct, "RETURNLEAVE");
        }
        break;
      default:
        throw new RuntimeException(
            "CilSwitchInstruction: Opcode " + instruction.getDefaultInst().getOpCode().name() + " not implemented!");
    }

    List<Unit> targets = new ArrayList<>();
    for (ProtoIlInstructions.IlSwitchSectionMsg section : instruction.getSwitchSectionsList()) {
      NopStmt nopStmt = Jimple.v().newNopStmt(); // dummy target
      switch (section.getTargetInstr().getOpCode()) {
        case BRANCH:
          targets.add(nopStmt);
          // dotnetBody.blockEntryPointsManager.gotoTargetsInBody.put(nopStmt, section.getTargetInstr().getTargetLabel());
          cilBlock.getDeclaredBlockContainer().blockEntryPointsManager.gotoTargetsInBody.put(nopStmt,
              section.getTargetInstr().getTargetLabel());
          break;
        case LEAVE:
          if (cilBlock.getDeclaredBlockContainer().isChildBlockContainer()
              && !section.getTargetInstr().getTargetLabel().equals("IL_0000")) {
            targets.add(cilBlock.getDeclaredBlockContainer().getSkipBlockContainerStmt());
          } else {
            targets.add(nopStmt);
            // dotnetBody.blockEntryPointsManager.gotoTargetsInBody.put(nopStmt, "END_" +
            // section.getTargetInstr().getTargetLabel());
            dotnetBody.blockEntryPointsManager.gotoTargetsInBody.put(nopStmt, "RETURNLEAVE");
          }
          break;
        default:
          throw new RuntimeException(
              "CilSwitchInstruction: Opcode " + section.getTargetInstr().getOpCode().name() + " not implemented!");
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
