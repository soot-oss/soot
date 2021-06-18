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

public abstract class AbstractStmtSwitch<T> implements StmtSwitch {

  T result;

  @Override
  public void caseBreakpointStmt(BreakpointStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseInvokeStmt(InvokeStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseAssignStmt(AssignStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseIdentityStmt(IdentityStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseGotoStmt(GotoStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseIfStmt(IfStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseNopStmt(NopStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseRetStmt(RetStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseReturnStmt(ReturnStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseTableSwitchStmt(TableSwitchStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void caseThrowStmt(ThrowStmt stmt) {
    defaultCase(stmt);
  }

  @Override
  public void defaultCase(Object obj) {
  }

  public void setResult(T result) {
    this.result = result;
  }

  public T getResult() {
    return result;
  }
}
