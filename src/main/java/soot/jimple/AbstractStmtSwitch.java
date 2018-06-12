package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

public abstract class AbstractStmtSwitch implements StmtSwitch {
  Object result;

  public void caseBreakpointStmt(BreakpointStmt stmt) {
    defaultCase(stmt);
  }

  public void caseInvokeStmt(InvokeStmt stmt) {
    defaultCase(stmt);
  }

  public void caseAssignStmt(AssignStmt stmt) {
    defaultCase(stmt);
  }

  public void caseIdentityStmt(IdentityStmt stmt) {
    defaultCase(stmt);
  }

  public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
    defaultCase(stmt);
  }

  public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
    defaultCase(stmt);
  }

  public void caseGotoStmt(GotoStmt stmt) {
    defaultCase(stmt);
  }

  public void caseIfStmt(IfStmt stmt) {
    defaultCase(stmt);
  }

  public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
    defaultCase(stmt);
  }

  public void caseNopStmt(NopStmt stmt) {
    defaultCase(stmt);
  }

  public void caseRetStmt(RetStmt stmt) {
    defaultCase(stmt);
  }

  public void caseReturnStmt(ReturnStmt stmt) {
    defaultCase(stmt);
  }

  public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
    defaultCase(stmt);
  }

  public void caseTableSwitchStmt(TableSwitchStmt stmt) {
    defaultCase(stmt);
  }

  public void caseThrowStmt(ThrowStmt stmt) {
    defaultCase(stmt);
  }

  public void defaultCase(Object obj) {
  }

  public void setResult(Object result) {
    this.result = result;
  }

  public Object getResult() {
    return result;
  }
}
