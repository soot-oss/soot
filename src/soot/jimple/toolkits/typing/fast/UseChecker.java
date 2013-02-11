/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Ben Bellamy 
 * 
 * All rights reserved.
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
package soot.jimple.toolkits.typing.fast;

import java.util.*;

import soot.*;
import soot.jimple.*;

/**
 * This checks all uses against the rules in Jimple, except some uses are not
 * checked where the bytecode verifier guarantees use validity. 
 * @author Ben Bellamy
 */
public class UseChecker extends AbstractStmtSwitch
{
	private JimpleBody jb;
	
	private Typing tg;
	private IUseVisitor uv;
	
	public UseChecker(JimpleBody jb)
	{
		this.jb = jb;
	}
	
	public void check(Typing tg, IUseVisitor uv)
	{
		try {
			this.tg = tg;	
			this.uv = uv;
			if (this.tg == null) throw new Exception("null typing passed to useChecker");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		for ( Iterator<Unit> i = this.jb.getUnits().snapshotIterator();
			i.hasNext(); )
		{
			if ( uv.finish() )
				return;
			((Stmt)i.next()).apply(this);
		}
	}
	
	private void handleInvokeExpr(InvokeExpr ie, Stmt stmt)
	{
		SootMethodRef m = ie.getMethodRef();
		
		if ( ie instanceof InstanceInvokeExpr )
		{
			InstanceInvokeExpr iie = (InstanceInvokeExpr)ie;
			iie.setBase(this.uv.visit(
				iie.getBase(),m.declaringClass().getType(), stmt));
		}
		
		for ( int i = 0; i < ie.getArgCount(); i++ )
			ie.setArg(i, this.uv.visit(
				ie.getArg(i), m.parameterType(i), stmt));
	}
	
	private void handleBinopExpr(BinopExpr be, Stmt stmt, Type tlhs)
	{
		Value opl = be.getOp1(), opr = be.getOp2();
		Type tl = AugEvalFunction.eval_(this.tg, opl, stmt, this.jb),
			tr = AugEvalFunction.eval_(this.tg, opr, stmt, this.jb);
	
		if ( be instanceof AddExpr
			|| be instanceof SubExpr
			|| be instanceof MulExpr
			|| be instanceof DivExpr
			|| be instanceof RemExpr
			|| be instanceof GeExpr
			|| be instanceof GtExpr
			|| be instanceof LeExpr
			|| be instanceof LtExpr
			|| be instanceof ShlExpr
			|| be instanceof ShrExpr
			|| be instanceof UshrExpr )
		{
			if ( tlhs instanceof IntegerType )
			{
				be.setOp1(this.uv.visit(opl, IntType.v(), stmt));
				be.setOp2(this.uv.visit(opr, IntType.v(), stmt));
			}
		}
		else if ( be instanceof CmpExpr
			|| be instanceof CmpgExpr
			|| be instanceof CmplExpr )
		{
			// No checks in the original assigner
		}
		else if ( be instanceof AndExpr
			|| be instanceof OrExpr
			|| be instanceof XorExpr )
		{
			be.setOp1(this.uv.visit(opl, tlhs, stmt));
			be.setOp2(this.uv.visit(opr, tlhs, stmt));
		}
		else if ( be instanceof EqExpr
			|| be instanceof NeExpr )
		{
			if ( tl instanceof BooleanType && tr instanceof BooleanType )
			{ }
			else if ( tl instanceof Integer1Type || tr instanceof Integer1Type )
			{ }
			else if ( tl instanceof IntegerType )
			{
				be.setOp1(this.uv.visit(opl, IntType.v(), stmt));
				be.setOp2(this.uv.visit(opr, IntType.v(), stmt));
			}
		}
	}
	
	private void handleArrayRef(ArrayRef ar, Stmt stmt)
	{
		ar.setIndex(this.uv.visit(ar.getIndex(), IntType.v(), stmt));
	}
	
	private void handleInstanceFieldRef(InstanceFieldRef ifr, Stmt stmt)
	{
		ifr.setBase(this.uv.visit(ifr.getBase(),
			ifr.getField().getDeclaringClass().getType(), stmt));
	}

	public void caseBreakpointStmt(BreakpointStmt stmt) { }
	
	public void caseInvokeStmt(InvokeStmt stmt)
	{
		this.handleInvokeExpr(stmt.getInvokeExpr(), stmt);
	}
	
	public void caseAssignStmt(AssignStmt stmt)
	{
		Value lhs = stmt.getLeftOp();
		Type tlhs = null;
		
		
		
		if ( lhs instanceof Local )
			tlhs = this.tg.get((Local)lhs);
		else if ( lhs instanceof ArrayRef )
		{
			Local base = (Local)((ArrayRef)lhs).getBase();
			ArrayType at;
			//try to force Type integrity
			if (this.tg.get(base) instanceof ArrayType)
				at = (ArrayType)this.tg.get(base);
			else
				at = (ArrayType)this.tg.get(base).makeArrayType();
			tlhs = ((ArrayType)at).getElementType();
			this.handleArrayRef((ArrayRef)lhs, stmt);
		}
		else if ( lhs instanceof FieldRef )
		{
			tlhs = ((FieldRef)lhs).getField().getType();
			if ( lhs instanceof InstanceFieldRef )
				this.handleInstanceFieldRef((InstanceFieldRef)lhs, stmt);
		}
			
		Value rhs = stmt.getRightOp();
		
		if ( rhs instanceof Local )
			stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
		else if ( rhs instanceof ArrayRef )
		{
			this.handleArrayRef((ArrayRef)rhs, stmt);
			stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
		}
		else if ( rhs instanceof InstanceFieldRef )
		{
			this.handleInstanceFieldRef((InstanceFieldRef)rhs, stmt);
			stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
		}
		else if ( rhs instanceof BinopExpr )
			this.handleBinopExpr((BinopExpr)rhs, stmt, tlhs);
		else if ( rhs instanceof InvokeExpr )
		{
			this.handleInvokeExpr((InvokeExpr)rhs, stmt);
			stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
		}
		else if ( rhs instanceof CastExpr )
			stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
		else if ( rhs instanceof InstanceOfExpr )
		{
			InstanceOfExpr ioe = (InstanceOfExpr)rhs;
			ioe.setOp(this.uv.visit(
				ioe.getOp(), RefType.v("java.lang.Object"), stmt));
			stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
		}
		else if ( rhs instanceof NewArrayExpr )
		{
			NewArrayExpr nae = (NewArrayExpr)rhs;
			nae.setSize(this.uv.visit(nae.getSize(), IntType.v(), stmt));
			stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
		}
		else if ( rhs instanceof NewMultiArrayExpr )
		{
			NewMultiArrayExpr nmae = (NewMultiArrayExpr)rhs;
			for ( int i = 0; i < nmae.getSizeCount(); i++ )
				nmae.setSize(i, this.uv.visit(
					nmae.getSize(i), IntType.v(), stmt));
			stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
		}
		else if ( rhs instanceof LengthExpr )
		{
			stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
		}
		else if ( rhs instanceof NegExpr )
		{
			((NegExpr)rhs).setOp(this.uv.visit(
				((NegExpr)rhs).getOp(), tlhs, stmt));
		}
	}
	
	public void caseIdentityStmt(IdentityStmt stmt) { }
	
	public void caseEnterMonitorStmt(EnterMonitorStmt stmt)
	{
		stmt.setOp(this.uv.visit(
			stmt.getOp(), RefType.v("java.lang.Object"), stmt));
	}
	
	public void caseExitMonitorStmt(ExitMonitorStmt stmt)
	{
		stmt.setOp(this.uv.visit(
			stmt.getOp(), RefType.v("java.lang.Object"), stmt));
	}
	
	public void caseGotoStmt(GotoStmt stmt) { }
	
	public void caseIfStmt(IfStmt stmt)
	{
		this.handleBinopExpr((BinopExpr)stmt.getCondition(), stmt,
			BooleanType.v());
	}
	
	public void caseLookupSwitchStmt(LookupSwitchStmt stmt)
	{
		stmt.setKey(this.uv.visit(stmt.getKey(), IntType.v(), stmt));
	}
	
	public void caseNopStmt(NopStmt stmt) { }
	
	public void caseReturnStmt(ReturnStmt stmt)
	{
		stmt.setOp(this.uv.visit(
			stmt.getOp(), this.jb.getMethod().getReturnType(), stmt));
	}
	
	public void caseReturnVoidStmt(ReturnVoidStmt stmt) { }
	
	public void caseTableSwitchStmt(TableSwitchStmt stmt)
	{
		stmt.setKey(this.uv.visit(stmt.getKey(), IntType.v(), stmt));
	}
	
	public void caseThrowStmt(ThrowStmt stmt)
	{
		stmt.setOp(this.uv.visit(
			stmt.getOp(), RefType.v("java.lang.Throwable"), stmt));
	}
	
	public void defaultCase(Stmt stmt)
	{
		throw new RuntimeException(
			"Unhandled stgtement type: " + stmt.getClass());
	}
}