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

import soot.util.*;

public interface StmtSwitch extends Switch
{
    public abstract void caseBreakpointStmt(BreakpointStmt stmt);
    public abstract void caseInvokeStmt(InvokeStmt stmt);
    public abstract void caseAssignStmt(AssignStmt stmt);
    public abstract void caseIdentityStmt(IdentityStmt stmt);
    public abstract void caseEnterMonitorStmt(EnterMonitorStmt stmt);
    public abstract void caseExitMonitorStmt(ExitMonitorStmt stmt);
    public abstract void caseGotoStmt(GotoStmt stmt);
    public abstract void caseIfStmt(IfStmt stmt);
    public abstract void caseLookupSwitchStmt(LookupSwitchStmt stmt);
    public abstract void caseNopStmt(NopStmt stmt);
    public abstract void caseRetStmt(RetStmt stmt);
    public abstract void caseReturnStmt(ReturnStmt stmt);
    public abstract void caseReturnVoidStmt(ReturnVoidStmt stmt);
    public abstract void caseTableSwitchStmt(TableSwitchStmt stmt);
    public abstract void caseThrowStmt(ThrowStmt stmt);
    public abstract void defaultCase(Object obj);
}
