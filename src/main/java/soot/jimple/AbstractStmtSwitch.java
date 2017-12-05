/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */





package soot.jimple;

public abstract class AbstractStmtSwitch implements StmtSwitch
{
    Object result;

    public void caseBreakpointStmt(BreakpointStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseInvokeStmt(InvokeStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseAssignStmt(AssignStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseIdentityStmt(IdentityStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseEnterMonitorStmt(EnterMonitorStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseExitMonitorStmt(ExitMonitorStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseGotoStmt(GotoStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseIfStmt(IfStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseLookupSwitchStmt(LookupSwitchStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseNopStmt(NopStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseRetStmt(RetStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseReturnStmt(ReturnStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseReturnVoidStmt(ReturnVoidStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseTableSwitchStmt(TableSwitchStmt stmt)
    {
        defaultCase(stmt);
    }

    public void caseThrowStmt(ThrowStmt stmt)
    {
        defaultCase(stmt);
    }

    public void defaultCase(Object obj)
    {
    }

    public void setResult(Object result)
    {
        this.result = result;
    }

    public Object getResult()
    {
        return result;
    }
}

