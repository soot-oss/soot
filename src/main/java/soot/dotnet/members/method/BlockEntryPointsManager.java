package soot.dotnet.members.method;

import java.util.HashMap;
import java.util.List;
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
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.TableSwitchStmt;

/**
 * Part of DotnetBody Manager for entry points (leave, branch instructions) ILSpy AST Blocks are labeled with a string.
 * Strings are stored and replaced afterwards with the right JimpleStmt
 */
public class BlockEntryPointsManager {

  /**
   * Every method contains blocks of instructions targeted with a label. These are stored while visiting blocks and used
   * afterwards to swap the goto values.
   */
  private final HashMap<String, Unit> methodBlockEntryPoints = new HashMap<>();

  /**
   * first goto targets are nop stmts, mapped to the real entry point string_names. These are swapped afterwards with the
   * real goto values, after all method blocks are visited.
   */
  public final HashMap<Unit, String> gotoTargetsInBody = new HashMap<>();

  public void putBlockEntryPoint(String blockName, Unit entryUnit) {
    methodBlockEntryPoints.put(blockName, entryUnit);
  }

  public Unit getBlockEntryPoint(String blockName) {
    return methodBlockEntryPoints.get(blockName);
  }

  /**
   * After producing Jimple Body, swap all target branches (label to stmt)
   *
   * @param jb
   */
  public void swapGotoEntriesInJBody(Body jb) {
    // if there is a target branch with return leave instruction add RETURNVALUE Target with end instruction
    // e.g. if (comp.i4(ldloc capacity == ldloc num5)) leave IL_0000 (nop)
    methodBlockEntryPoints.put("RETURNLEAVE", jb.getUnits().getLast());

    // change goto / if goto targets to real targets
    for (Unit unit : jb.getUnits()) {
      if (unit instanceof GotoStmt) {
        String entryPointString = gotoTargetsInBody.get(((GotoStmt) unit).getTarget());
        if (entryPointString == null) {
          continue;
        }
        Unit unitToSwap = methodBlockEntryPoints.get(entryPointString);
        if (unitToSwap == null) {
          continue;
        }
        ((GotoStmt) unit).setTarget(unitToSwap);
      }
      if (unit instanceof IfStmt) {
        String entryPointString = gotoTargetsInBody.get(((IfStmt) unit).getTarget());
        if (entryPointString == null) {
          continue;
        }
        Unit unitToSwap = methodBlockEntryPoints.get(entryPointString);
        if (unitToSwap == null) {
          continue;
        }
        ((IfStmt) unit).setTarget(unitToSwap);
      }
      if (unit instanceof TableSwitchStmt) {
        TableSwitchStmt tableSwitchStmt = (TableSwitchStmt) unit;
        // swap all targets from nop to selected target
        List<Unit> targets = tableSwitchStmt.getTargets();
        for (int i = 0; i < targets.size(); i++) {
          Unit target = targets.get(i);
          String entryPointString = gotoTargetsInBody.get(target);
          if (entryPointString == null) {
            continue;
          }
          Unit unitToSwap = methodBlockEntryPoints.get(entryPointString);
          if (unitToSwap == null) {
            continue;
          }
          tableSwitchStmt.setTarget(i, unitToSwap);
        }

        // swap default target
        String entryPointStringDefault = gotoTargetsInBody.get(tableSwitchStmt.getDefaultTarget());
        if (entryPointStringDefault == null) {
          continue;
        }
        Unit unitToSwap = methodBlockEntryPoints.get(entryPointStringDefault);
        if (unitToSwap == null) {
          continue;
        }
        tableSwitchStmt.setDefaultTarget(unitToSwap);
      }
    }
  }

  /**
   * Swap two elements (out of the chain)
   *
   * @param in
   * @param out
   */
  public void swapGotoEntryUnit(Unit in, Unit out) {
    for (Map.Entry<String, Unit> set : methodBlockEntryPoints.entrySet()) {
      if (set.getValue() == out) {
        set.setValue(in);
      }
    }
  }
}
