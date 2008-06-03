/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
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
   Nomair A. Naeem 08-FEB-2005
   This analysis class has become obselete.
   Use soot.dava.toolkits.base.AST.analysis which provides a Visitor Design
   Pattern
*/

package soot.dava.toolkits.base.AST;

import soot.*;
import soot.jimple.*;
import soot.dava.internal.AST.*;

public abstract class ASTAnalysis
{
    public static final int

	ANALYSE_AST    = 0,
	ANALYSE_STMTS  = 1,
	ANALYSE_VALUES = 2;


    public abstract int getAnalysisDepth();

    public void analyseASTNode( ASTNode n)
    {
    }
    public void analyseDefinitionStmt( DefinitionStmt s)
    {
    }
    public void analyseReturnStmt( ReturnStmt s)
    {
    }
    public void analyseInvokeStmt( InvokeStmt s)
    {
    }
    public void analyseThrowStmt( ThrowStmt s)
    {
    }
    public void analyseStmt( Stmt s)
    {
    }
    public void analyseBinopExpr( BinopExpr v)
    {
    }
    public void analyseUnopExpr( UnopExpr v)
    {
    }
    public void analyseNewArrayExpr( NewArrayExpr v)
    {
    }
    public void analyseNewMultiArrayExpr( NewMultiArrayExpr v)
    {
    }
    public void analyseInstanceOfExpr( InstanceOfExpr v)
    {
    }
    public void analyseInstanceInvokeExpr( InstanceInvokeExpr v)
    {
    }
    public void analyseInvokeExpr( InvokeExpr v)
    {
    }
    public void analyseExpr( Expr v)
    {
    }
    public void analyseArrayRef( ArrayRef v)
    {
    }
    public void analyseInstanceFieldRef( InstanceFieldRef v)
    {
    }
    public void analyseRef( Ref v)
    {
    }
    public void analyseValue( Value v)
    {
    }
}
