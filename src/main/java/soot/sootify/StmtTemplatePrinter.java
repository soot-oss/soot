package soot.sootify;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import soot.PatchingChain;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;

class StmtTemplatePrinter implements StmtSwitch {
  private final TemplatePrinter p;

  private final ValueTemplatePrinter vtp; // text for expression

  private List<Unit> jumpTargets = new ArrayList<Unit>();

  public StmtTemplatePrinter(TemplatePrinter templatePrinter, PatchingChain<Unit> units) {
    this.p = templatePrinter;
    this.vtp = new ValueTemplatePrinter(p);

    for (Unit u : units) {
      for (UnitBox ub : u.getUnitBoxes()) {
        jumpTargets.add(ub.getUnit());
      }
    }

    final List<Unit> unitsList = new ArrayList<Unit>(units);
    Collections.sort(jumpTargets, new Comparator<Unit>() {
      public int compare(Unit o1, Unit o2) {
        return unitsList.indexOf(o1) - unitsList.indexOf(o2);
      }
    });

    for (int i = 0; i < jumpTargets.size(); i++) {
      p.println("NopStmt jumpTarget" + i + "= Jimple.v().newNopStmt();");
    }
  }

  private String nameOfJumpTarget(Unit u) {
    if (!isJumpTarget(u)) {
      throw new InternalError("not a jumpt target! " + u);
    }
    return "jumpTarget" + jumpTargets.indexOf(u);
  }

  private boolean isJumpTarget(Unit u) {
    return jumpTargets.contains(u);
  }

  private String printValueAssignment(Value value, String varName) {
    return vtp.printValueAssignment(value, varName);
  }

  private void printStmt(Unit u, String... ops) {
    String stmtClassName = u.getClass().getSimpleName();
    if (stmtClassName.charAt(0) == 'J') {
      stmtClassName = stmtClassName.substring(1);
    }
    if (isJumpTarget(u)) {
      String nameOfJumpTarget = nameOfJumpTarget(u);
      p.println("units.add(" + nameOfJumpTarget + ");");
    }
    p.print("units.add(");
    printFactoryMethodCall(stmtClassName, ops);
    p.printlnNoIndent(");");
  }

  private void printFactoryMethodCall(String stmtClassName, String... ops) {
    p.printNoIndent("Jimple.v().new");
    p.printNoIndent(stmtClassName);
    p.printNoIndent("(");
    int i = 1;
    for (String op : ops) {
      p.printNoIndent(op);
      if (i < ops.length) {
        p.printNoIndent(",");
      }
      i++;
    }
    p.printNoIndent(")");
  }

  public void caseThrowStmt(ThrowStmt stmt) {
    String varName = printValueAssignment(stmt.getOp(), "op");
    printStmt(stmt, varName);
  }

  public void caseTableSwitchStmt(TableSwitchStmt stmt) {
    p.openBlock();
    String varName = printValueAssignment(stmt.getKey(), "key");

    int lowIndex = stmt.getLowIndex();
    p.println("int lowIndex=" + lowIndex + ";");

    int highIndex = stmt.getHighIndex();
    p.println("int highIndex=" + highIndex + ";");

    p.println("List<Unit> targets = new LinkedList<Unit>();");
    for (Unit s : stmt.getTargets()) {
      String nameOfJumpTarget = nameOfJumpTarget(s);
      p.println("targets.add(" + nameOfJumpTarget + ")");
    }

    Unit defaultTarget = stmt.getDefaultTarget();
    p.println("Unit defaultTarget = " + nameOfJumpTarget(defaultTarget) + ";");

    printStmt(stmt, varName, "lowIndex", "highIndex", "targets", "defaultTarget");

    p.closeBlock();
  }

  public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
    printStmt(stmt);
  }

  public void caseReturnStmt(ReturnStmt stmt) {
    String varName = printValueAssignment(stmt.getOp(), "retVal");
    printStmt(stmt, varName);
  }

  public void caseRetStmt(RetStmt stmt) {
    String varName = printValueAssignment(stmt.getStmtAddress(), "stmtAddress");
    printStmt(stmt, varName);
  }

  public void caseNopStmt(NopStmt stmt) {
    printStmt(stmt);
  }

  public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
    p.openBlock();

    String keyVarName = printValueAssignment(stmt.getKey(), "key");

    p.println("List<IntConstant> lookupValues = new LinkedList<IntConstant>();");
    int i = 0;
    for (IntConstant c : (List<IntConstant>) stmt.getLookupValues()) {
      vtp.suggestVariableName("lookupValue" + i);
      c.apply(vtp);
      i++;

      p.println("lookupValues.add(lookupValue" + i + ");");
    }

    p.println("List<Unit> targets = new LinkedList<Unit>();");
    for (Unit u : stmt.getTargets()) {
      String nameOfJumpTarget = nameOfJumpTarget(u);
      p.println("targets.add(" + nameOfJumpTarget + ")");
    }

    Unit defaultTarget = stmt.getDefaultTarget();
    p.println("Unit defaultTarget=" + defaultTarget.toString() + ";");

    printStmt(stmt, keyVarName, "lookupValues", "targets", "defaultTarget");

    p.closeBlock();
  }

  public void caseInvokeStmt(InvokeStmt stmt) {
    String varName = printValueAssignment(stmt.getInvokeExpr(), "ie");
    printStmt(stmt, varName);
  }

  public void caseIfStmt(IfStmt stmt) {
    String varName = printValueAssignment(stmt.getCondition(), "condition");

    Unit target = stmt.getTarget();

    vtp.suggestVariableName("target");
    String targetName = vtp.getLastAssignedVarName();
    p.println("Unit " + targetName + "=" + nameOfJumpTarget(target) + ";");

    printStmt(stmt, varName, targetName);
  }

  public void caseIdentityStmt(IdentityStmt stmt) {
    String varName = printValueAssignment(stmt.getLeftOp(), "lhs");

    String varName2 = printValueAssignment(stmt.getRightOp(), "idRef");

    printStmt(stmt, varName, varName2);
  }

  public void caseGotoStmt(GotoStmt stmt) {
    Unit target = stmt.getTarget();

    vtp.suggestVariableName("target");
    String targetName = vtp.getLastAssignedVarName();
    p.println("Unit " + targetName + "=" + nameOfJumpTarget(target) + ";");

    printStmt(stmt, targetName);
  }

  public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
    String varName = printValueAssignment(stmt.getOp(), "monitor");

    printStmt(stmt, varName);
  }

  public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
    String varName = printValueAssignment(stmt.getOp(), "monitor");

    printStmt(stmt, varName);
  }

  public void caseBreakpointStmt(BreakpointStmt stmt) {
    printStmt(stmt);
  }

  public void caseAssignStmt(AssignStmt stmt) {
    String varName = printValueAssignment(stmt.getLeftOp(), "lhs");
    String varName2 = printValueAssignment(stmt.getRightOp(), "rhs");

    printStmt(stmt, varName, varName2);
  }

  public void defaultCase(Object obj) {
    throw new InternalError("should never be called");
  }

}
