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

public class ASTWalker
{
    public ASTWalker( Singletons.Global g ) {}
    public static ASTWalker v() { return G.v().soot_dava_toolkits_base_AST_ASTWalker(); }
    
    public void walk_stmt( ASTAnalysis a, Stmt s)
    {
	if (a.getAnalysisDepth() < ASTAnalysis.ANALYSE_STMTS)
	    return;

	if (s instanceof DefinitionStmt) {
	    DefinitionStmt ds = (DefinitionStmt) s;

	    walk_value( a, ds.getRightOp());
	    walk_value( a, ds.getLeftOp());
	    a.analyseDefinitionStmt( ds);
	}
	else if (s instanceof ReturnStmt) {
	    ReturnStmt rs = (ReturnStmt) s;

	    walk_value( a, rs.getOp());
	    a.analyseReturnStmt( rs);
	}
	else if (s instanceof InvokeStmt) {
	    InvokeStmt is = (InvokeStmt) s;

	    walk_value( a, is.getInvokeExpr());
	    a.analyseInvokeStmt( is);
	}
	else if (s instanceof ThrowStmt) {
	    ThrowStmt ts = (ThrowStmt) s;

	    walk_value( a, ts.getOp());
	    a.analyseThrowStmt( ts);
	}
	else
	    a.analyseStmt( s);
    }

    public void walk_value( ASTAnalysis a, Value v)
    {
	if (a.getAnalysisDepth() < ASTAnalysis.ANALYSE_VALUES)
	    return;

	if (v instanceof Expr) {
	    Expr e = (Expr) v;

	    if (e instanceof BinopExpr) {
		BinopExpr be = (BinopExpr) e;
		
		walk_value( a, be.getOp1());
		walk_value( a, be.getOp2());
		a.analyseBinopExpr( be);
	    }
	    else if (e instanceof UnopExpr) {
		UnopExpr ue = (UnopExpr) e;
		
		walk_value( a, ue.getOp());
		a.analyseUnopExpr( ue);
	    }
	    else if (e instanceof CastExpr) {
		CastExpr ce = (CastExpr)e;

		walk_value(a, ce.getOp());
		a.analyseExpr(ce);
		}
	    else if (e instanceof NewArrayExpr) {
		NewArrayExpr nae = (NewArrayExpr) e;
		
		walk_value( a, nae.getSize());
		a.analyseNewArrayExpr( nae);
	    }
	    else if (e instanceof NewMultiArrayExpr) {
		NewMultiArrayExpr nmae = (NewMultiArrayExpr) e;
		
		for (int i=0; i<nmae.getSizeCount(); i++)
		    walk_value( a, nmae.getSize( i));
		a.analyseNewMultiArrayExpr( nmae);
	    }
	    else if (e instanceof InstanceOfExpr) {
		InstanceOfExpr ioe = (InstanceOfExpr) e;
		
		walk_value( a, ioe.getOp());
		a.analyseInstanceOfExpr( ioe);
	    }
	    else if (e instanceof InvokeExpr) {
		InvokeExpr ie = (InvokeExpr) e;
		
		for (int i=0; i<ie.getArgCount(); i++) 
		    walk_value( a, ie.getArg( i));
		
		if (ie instanceof InstanceInvokeExpr) {
		    InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
		    
		    walk_value( a, iie.getBase());
		    a.analyseInstanceInvokeExpr( iie);
		}
		else
		    a.analyseInvokeExpr( ie);
	    }
	    else
		a.analyseExpr( e);
	}
	else if (v instanceof Ref) {
	    Ref r = (Ref) v;

	    if (r instanceof ArrayRef) {
		ArrayRef ar = (ArrayRef) r;
		
		walk_value( a, ar.getBase());
		walk_value( a, ar.getIndex());
		a.analyseArrayRef( ar);
	    }
	    else if (r instanceof InstanceFieldRef) {
		InstanceFieldRef ifr = (InstanceFieldRef) r;
		
		walk_value( a, ifr.getBase());
		a.analyseInstanceFieldRef( ifr);
	    }
	    else
		a.analyseRef( r);
	}
	else 
	    a.analyseValue( v);
    }
}
