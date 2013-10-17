/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2000 Etienne Gagnon.  All rights reserved.
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


package soot.jimple.toolkits.typing.integer;

import java.io.PrintWriter;
import java.io.StringWriter;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
import soot.NullType;
import soot.ShortType;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.BreakpointStmt;
import soot.jimple.CastExpr;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.EqExpr;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GotoStmt;
import soot.jimple.GtExpr;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.RemExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;
import soot.jimple.toolkits.typing.Util;

class ConstraintChecker extends AbstractStmtSwitch
{
  private final TypeResolver resolver;
  private final boolean fix;  // if true, fix constraint violations

  private JimpleBody stmtBody;

  public ConstraintChecker(TypeResolver resolver, boolean fix)
  {
    this.resolver = resolver;
    this.fix = fix;
  }

  public void check(Stmt stmt, JimpleBody stmtBody) throws TypeException
  {
    try
      {
	this.stmtBody = stmtBody;
	stmt.apply(this);
      }
    catch(RuntimeTypeException e)
      {
         StringWriter st = new StringWriter();
         PrintWriter pw = new PrintWriter(st);
	 e.printStackTrace(pw);
	 pw.close();
         throw new TypeException(st.toString());
      }
  }

  private static class RuntimeTypeException extends RuntimeException
  {
    RuntimeTypeException(String message)
    {
      super(message);
    }
  }

  static void error(String message)
  {
    throw new RuntimeTypeException(message);
  }

  private void handleInvokeExpr(InvokeExpr ie, Stmt invokestmt)
  {
    if(ie instanceof InterfaceInvokeExpr)
      {
	InterfaceInvokeExpr invoke = (InterfaceInvokeExpr) ie;
	SootMethodRef method = invoke.getMethodRef();
	int count = invoke.getArgCount();

	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);

		if(local.getType() instanceof IntegerType)
		  {
		    if(!ClassHierarchy.v().typeNode(local.getType()).
		       hasAncestor_1(ClassHierarchy.v().typeNode(method.parameterType(i))))
		      {
			if(fix)
			  {
			    invoke.setArg(i, insertCast(local, method.parameterType(i), invokestmt));
			  }
			else
			  {
			    error("Type Error(1)");
			  }
		      }
		  }
	      }
	  }
      }
    else if(ie instanceof SpecialInvokeExpr)
      {
	SpecialInvokeExpr invoke = (SpecialInvokeExpr) ie;
	SootMethodRef method = invoke.getMethodRef();
	int count = invoke.getArgCount();

	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);

		if(local.getType() instanceof IntegerType)
		  {
		    if(!ClassHierarchy.v().typeNode(local.getType()).
		       hasAncestor_1(ClassHierarchy.v().typeNode(method.parameterType(i))))
		      {
			if(fix)
			  {
			    invoke.setArg(i, insertCast(local, method.parameterType(i), invokestmt));
			  }
			else
			  {
			    error("Type Error(2)");
			  }
		      }
		  }
	      }
	  }
      }
    else if(ie instanceof VirtualInvokeExpr)
      {
	VirtualInvokeExpr invoke = (VirtualInvokeExpr) ie;
	SootMethodRef method = invoke.getMethodRef();
	int count = invoke.getArgCount();

	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);

		if(local.getType() instanceof IntegerType)
		  {
		    if(!ClassHierarchy.v().typeNode(local.getType()).
		       hasAncestor_1(ClassHierarchy.v().typeNode(method.parameterType(i))))
		      {
			if(fix)
			  {
			    invoke.setArg(i, insertCast(local, method.parameterType(i), invokestmt));
			  }
			else
			  {
			    error("Type Error(3)");
			  }
		      }
		  }
	      }
	  }
      }
    else if(ie instanceof StaticInvokeExpr)
      {
	StaticInvokeExpr invoke = (StaticInvokeExpr) ie;
	SootMethodRef method = invoke.getMethodRef();
	int count = invoke.getArgCount();

	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);

		if(local.getType() instanceof IntegerType)
		  {
		    if(!ClassHierarchy.v().typeNode(local.getType()).
		       hasAncestor_1(ClassHierarchy.v().typeNode(method.parameterType(i))))
		      {
			if(fix)
			  {
			    invoke.setArg(i, insertCast(local, method.parameterType(i), invokestmt));
			  }
			else
			  {
			    error("Type Error(4)");
			  }
		      }
		  }
	      }
	  }
      }
    else
      {
	throw new RuntimeException("Unhandled invoke expression type: " + ie.getClass());
      }
  }

  public void caseBreakpointStmt(BreakpointStmt stmt)
  {
    // Do nothing
  }

  public void caseInvokeStmt(InvokeStmt stmt)
  {
    handleInvokeExpr(stmt.getInvokeExpr(), stmt);
  }

  public void caseAssignStmt(AssignStmt stmt)
  {
    Value l = stmt.getLeftOp();
    Value r = stmt.getRightOp();

    TypeNode left = null;
    TypeNode right = null;

    //******** LEFT ********

    if(l instanceof ArrayRef)
      {
	ArrayRef ref = (ArrayRef) l;
	Type baset = ((Local) ref.getBase()).getType();
	if(baset instanceof ArrayType)
	{
	  ArrayType base = (ArrayType) baset;
	  Value index = ref.getIndex();

	  if((base.numDimensions == 1) &&
	     (base.baseType instanceof IntegerType))
	    {
	      left = ClassHierarchy.v().typeNode(base.baseType);
	    }

	  if(index instanceof Local)
	    {
	      if(!ClassHierarchy.v().typeNode(((Local) index).getType()).hasAncestor_1(ClassHierarchy.v().INT))
	        {
		  if(fix)
		    {
		      ref.setIndex(insertCast((Local) index, IntType.v(), stmt));
		    }
		  else
		    {
		      error("Type Error(5)");
		    }
	        }
	    }
        }
      }
    else if(l instanceof Local)
      {
	if(((Local) l).getType() instanceof IntegerType)
	  {
	    left = ClassHierarchy.v().typeNode(((Local) l).getType());
	  }
      }
    else if(l instanceof InstanceFieldRef)
      {
	InstanceFieldRef ref = (InstanceFieldRef) l;

	if(ref.getFieldRef().type() instanceof IntegerType)
	  {
	    left = ClassHierarchy.v().typeNode( ref.getFieldRef().type());
	  }
      }
    else if(l instanceof StaticFieldRef)
      {
	StaticFieldRef ref = (StaticFieldRef) l;

	if(ref.getFieldRef().type() instanceof IntegerType)
	  {
	    left = ClassHierarchy.v().typeNode(ref.getFieldRef().type());
	  }
      }
    else
      {
	throw new RuntimeException("Unhandled assignment left hand side type: " + l.getClass());
      }

    //******** RIGHT ********

    if(r instanceof ArrayRef)
      {
	ArrayRef ref = (ArrayRef) r;
	Type baset = ((Local) ref.getBase()).getType();
	if(!(baset instanceof NullType))
	{
	  ArrayType base = (ArrayType) baset;
	  Value index = ref.getIndex();

	  if((base.numDimensions == 1) &&
	     (base.baseType instanceof IntegerType))
	    {
	      right = ClassHierarchy.v().typeNode(base.baseType);
	    }

	  if(index instanceof Local)
	    {
	      if(!ClassHierarchy.v().typeNode(((Local) index).getType()).hasAncestor_1(ClassHierarchy.v().INT))
	        {
		  if(fix)
		    {
		      ref.setIndex(insertCast((Local) index, IntType.v(), stmt));
		    }
		  else
		    {
		      error("Type Error(6)");
		    }
	        }
	    }
	}
      }
    else if(r instanceof DoubleConstant)
      {
      }
    else if(r instanceof FloatConstant)
      {
      }
    else if(r instanceof IntConstant)
      {
	int value  = ((IntConstant) r).value;

	if(value < -32768)
	  {
	    right = ClassHierarchy.v().INT;
	  }
	else if(value < -128)
	  {
	    right = ClassHierarchy.v().SHORT;
	  }
	else if(value < 0)
	  {
	    right = ClassHierarchy.v().BYTE;
	  }
	else if(value < 2)
	  {
	    right = ClassHierarchy.v().R0_1;
	  }
	else if(value < 128)
	  {
	    right = ClassHierarchy.v().R0_127;
	  }
	else if(value < 32768)
	  {
	    right = ClassHierarchy.v().R0_32767;
	  }
	else if(value < 65536)
	  {
	    right = ClassHierarchy.v().CHAR;
	  }
	else
	  {
	    right = ClassHierarchy.v().INT;
	  }
      }
    else if(r instanceof LongConstant)
      {
      }
    else if(r instanceof NullConstant)
      {
      }
    else if(r instanceof StringConstant)
      {
      }
    else if(r instanceof ClassConstant)
      {
      }
    else if(r instanceof BinopExpr)
      {
	//******** BINOP EXPR ********

	BinopExpr be = (BinopExpr) r;

	Value lv = be.getOp1();
	Value rv = be.getOp2();

	TypeNode lop = null;
	TypeNode rop = null;

	//******** LEFT ********
	if(lv instanceof Local)
	  {
	    if(((Local) lv).getType() instanceof IntegerType)
	      {
		lop = ClassHierarchy.v().typeNode(((Local) lv).getType());
	      }
	  }
	else if(lv instanceof DoubleConstant)
	  {
	  }
	else if(lv instanceof FloatConstant)
	  {
	  }
	else if(lv instanceof IntConstant)
	  {
	    int value  = ((IntConstant) lv).value;

	    if(value < -32768)
	      {
		lop = ClassHierarchy.v().INT;
	      }
	    else if(value < -128)
	      {
		lop = ClassHierarchy.v().SHORT;
	      }
	    else if(value < 0)
	      {
		lop = ClassHierarchy.v().BYTE;
	      }
	    else if(value < 2)
	      {
		lop = ClassHierarchy.v().R0_1;
	      }
	    else if(value < 128)
	      {
		lop = ClassHierarchy.v().R0_127;
	      }
	    else if(value < 32768)
	      {
		lop = ClassHierarchy.v().R0_32767;
	      }
	    else if(value < 65536)
	      {
		lop = ClassHierarchy.v().CHAR;
	      }
	    else
	      {
		lop = ClassHierarchy.v().INT;
	      }
	  }
	else if(lv instanceof LongConstant)
	  {
	  }
	else if(lv instanceof NullConstant)
	  {
	  }
	else if(lv instanceof StringConstant)
	  {
	  }
	else if(lv instanceof ClassConstant)
	  {
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
	  }

	//******** RIGHT ********
	if(rv instanceof Local)
	  {
	    if(((Local) rv).getType() instanceof IntegerType)
	      {
		rop = ClassHierarchy.v().typeNode(((Local) rv).getType());
	      }
	  }
	else if(rv instanceof DoubleConstant)
	  {
	  }
	else if(rv instanceof FloatConstant)
	  {
	  }
	else if(rv instanceof IntConstant)
	  {
	    int value  = ((IntConstant) rv).value;

	    if(value < -32768)
	      {
		rop = ClassHierarchy.v().INT;
	      }
	    else if(value < -128)
	      {
		rop = ClassHierarchy.v().SHORT;
	      }
	    else if(value < 0)
	      {
		rop = ClassHierarchy.v().BYTE;
	      }
	    else if(value < 2)
	      {
		rop = ClassHierarchy.v().R0_1;
	      }
	    else if(value < 128)
	      {
		rop = ClassHierarchy.v().R0_127;
	      }
	    else if(value < 32768)
	      {
		rop = ClassHierarchy.v().R0_32767;
	      }
	    else if(value < 65536)
	      {
		rop = ClassHierarchy.v().CHAR;
	      }
	    else
	      {
		rop = ClassHierarchy.v().INT;
	      }
	  }
	else if(rv instanceof LongConstant)
	  {
	  }
	else if(rv instanceof NullConstant)
	  {
	  }
	else if(rv instanceof StringConstant)
	  {
	  }
	else if(rv instanceof ClassConstant)
	  {
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
	  }

	if((be instanceof AddExpr) ||
	   (be instanceof SubExpr) ||
	   (be instanceof MulExpr) ||
	   (be instanceof DivExpr) ||
	   (be instanceof RemExpr))
	  {
	    if(lop != null && rop != null)
	      {
		if(!lop.hasAncestor_1(ClassHierarchy.v().INT))
		  {
		    if(fix)
		      {
			be.setOp1(insertCast(be.getOp1(), getTypeForCast(lop), IntType.v(), stmt));
		      }
		    else
		      {
			error("Type Error(7)");
		      }
		  }

		if(!rop.hasAncestor_1(ClassHierarchy.v().INT))
		  {
		    if(fix)
		      {
			be.setOp2(insertCast(be.getOp2(), getTypeForCast(rop), IntType.v(), stmt));
		      }
		    else
		      {
			error("Type Error(8)");
		      }
		  }
	      }

	    right = ClassHierarchy.v().INT;
	  }
	else if((be instanceof AndExpr) ||
		(be instanceof OrExpr) ||
		(be instanceof XorExpr))
	  {
	    if(lop != null && rop != null)
	      {
		TypeNode lca = lop.lca_1(rop);

		if(lca == ClassHierarchy.v().TOP)
		  {
		    if(fix)
		      {
			if(!lop.hasAncestor_1(ClassHierarchy.v().INT))
			  {
			    be.setOp1(insertCast(be.getOp1(), getTypeForCast(lop), getTypeForCast(rop), stmt));
			    lca = rop;
			  }

			if(!rop.hasAncestor_1(ClassHierarchy.v().INT))
			  {
			    be.setOp2(insertCast(be.getOp2(), getTypeForCast(rop), getTypeForCast(lop), stmt));
			    lca = lop;
			  }
		      }
		    else
		      {
			error("Type Error(11)");
		      }
		  }

		right = lca;
	      }
	  }
	else if(be instanceof ShlExpr)
	  {
	    if(lop != null)
	      {
		if(!lop.hasAncestor_1(ClassHierarchy.v().INT))
		  {
		    if(fix)
		      {
			be.setOp1(insertCast(be.getOp1(), getTypeForCast(lop), IntType.v(), stmt));
		      }
		    else
		      {
			error("Type Error(9)");
		      }
		  }
	      }

	    if(!rop.hasAncestor_1(ClassHierarchy.v().INT))
	      {
		if(fix)
		  {
		    be.setOp2(insertCast(be.getOp2(), getTypeForCast(rop), IntType.v(), stmt));
		  }
		else
		  {
		    error("Type Error(10)");
		  }
	      }

	    right = (lop == null) ? null : ClassHierarchy.v().INT;
	  }
	else if((be instanceof ShrExpr) ||
		(be instanceof UshrExpr))
	  {
	    if(lop != null)
	      {
		if(!lop.hasAncestor_1(ClassHierarchy.v().INT))
		  {
		    if(fix)
		      {
			be.setOp1(insertCast(be.getOp1(), getTypeForCast(lop), ByteType.v(), stmt));
			lop = ClassHierarchy.v().BYTE;
		      }
		    else
		      {
			error("Type Error(9)");
		      }
		  }
	      }

	    if(!rop.hasAncestor_1(ClassHierarchy.v().INT))
	      {
		if(fix)
		  {
		    be.setOp2(insertCast(be.getOp2(), getTypeForCast(rop), IntType.v(), stmt));
		  }
		else
		  {
		    error("Type Error(10)");
		  }
	      }

	    right = lop;
	  }
	else if((be instanceof CmpExpr) ||
		(be instanceof CmpgExpr) ||
		(be instanceof CmplExpr))
	  {
	    right = ClassHierarchy.v().BYTE;
	  }
	else if((be instanceof EqExpr) ||
		(be instanceof GeExpr) ||
		(be instanceof GtExpr) ||
		(be instanceof LeExpr) ||
		(be instanceof LtExpr) ||
		(be instanceof NeExpr))
	  {
	    TypeNode lca = lop.lca_1(rop);

	    if(lca == ClassHierarchy.v().TOP)
	      {
		if(fix)
		  {
		    if(!lop.hasAncestor_1(ClassHierarchy.v().INT))
		      {
			be.setOp1(insertCast(be.getOp1(), getTypeForCast(lop), getTypeForCast(rop), stmt));
		      }

		    if(!rop.hasAncestor_1(ClassHierarchy.v().INT))
		      {
			be.setOp2(insertCast(be.getOp2(), getTypeForCast(rop), getTypeForCast(lop), stmt));
		      }
		  }
		else
		  {
		    error("Type Error(11)");
		  }
	      }

	    right = ClassHierarchy.v().BOOLEAN;
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression type: " + be.getClass());
	  }
      }
    else if(r instanceof CastExpr)
      {
	CastExpr ce = (CastExpr) r;

	if(ce.getCastType() instanceof IntegerType)
	  {
	    right = ClassHierarchy.v().typeNode(ce.getCastType());
	  }
      }
    else if(r instanceof InstanceOfExpr)
      {
	right = ClassHierarchy.v().BOOLEAN;
      }
    else if(r instanceof InvokeExpr)
      {
	InvokeExpr ie = (InvokeExpr) r;

	handleInvokeExpr(ie, stmt);

	if(ie.getMethodRef().returnType() instanceof IntegerType)
	  {
	    right = ClassHierarchy.v().typeNode(ie.getMethodRef().returnType());
	  }
      }
    else if(r instanceof NewArrayExpr)
      {
	NewArrayExpr nae = (NewArrayExpr) r;
	Value size = nae.getSize();

	if(size instanceof Local)
	  {
	    if(!ClassHierarchy.v().typeNode(((Local) size).getType()).
	       hasAncestor_1(ClassHierarchy.v().INT))
	      {
		if(fix)
		  {
		    nae.setSize(insertCast((Local) size, IntType.v(), stmt));
		  }
		else
		  {
		    error("Type Error(12)");
		  }
	      }
	  }
      }
    else if(r instanceof NewExpr)
      {
      }
    else if(r instanceof NewMultiArrayExpr)
      {
	NewMultiArrayExpr nmae = (NewMultiArrayExpr) r;

	for(int i = 0; i < nmae.getSizeCount(); i++)
	  {
	    Value size = nmae.getSize(i);

	    if(size instanceof Local)
	      {
		if(!ClassHierarchy.v().typeNode(((Local) size).getType()).
		   hasAncestor_1(ClassHierarchy.v().INT))
		  {
		    if(fix)
		      {
			nmae.setSize(i, insertCast((Local) size, IntType.v(), stmt));
		      }
		    else
		      {
			error("Type Error(13)");
		      }
		  }
	      }
	  }
      }
    else if(r instanceof LengthExpr)
      {
	right = ClassHierarchy.v().INT;
      }
    else if(r instanceof NegExpr)
      {
	NegExpr ne = (NegExpr) r;

	if(ne.getOp() instanceof Local)
	  {
	    Local local = (Local) ne.getOp();

	    if(local.getType() instanceof IntegerType)
	      {
		TypeNode ltype = ClassHierarchy.v().typeNode(local.getType());
		if(!ltype.hasAncestor_1(ClassHierarchy.v().INT))
		  {
		    if(fix)
		      {
			ne.setOp(insertCast(local, IntType.v(), stmt));
			ltype = ClassHierarchy.v().BYTE;
		      }
		    else
		      {
			error("Type Error(14)");
		      }
		  }

		right = (ltype == ClassHierarchy.v().CHAR) ? ClassHierarchy.v().INT : ltype;
	      }
	  }
	else if(ne.getOp() instanceof DoubleConstant)
	  {
	  }
	else if(ne.getOp() instanceof FloatConstant)
	  {
	  }
	else if(ne.getOp() instanceof IntConstant)
	  {
	    right = ClassHierarchy.v().INT;
	  }
	else if(ne.getOp() instanceof LongConstant)
	  {
	  }
	else
	  {
	    throw new RuntimeException("Unhandled neg expression operand type: " + ne.getOp().getClass());
	  }
      }
    else if(r instanceof Local)
      {
	Local local = (Local) r;

	if(local.getType() instanceof IntegerType)
	  {
	    right = ClassHierarchy.v().typeNode(local.getType());
	  }
      }
    else if(r instanceof InstanceFieldRef)
      {
	InstanceFieldRef ref = (InstanceFieldRef) r;

	if(ref.getFieldRef().type() instanceof IntegerType)
	  {
	    right = ClassHierarchy.v().typeNode(ref.getFieldRef().type());
	  }
      }
    else if(r instanceof StaticFieldRef)
      {
	StaticFieldRef ref = (StaticFieldRef) r;

	if(ref.getFieldRef().type() instanceof IntegerType)
	  {
	    right = ClassHierarchy.v().typeNode(ref.getFieldRef().type());
	  }
      }
    else
      {
	throw new RuntimeException("Unhandled assignment right hand side type: " + r.getClass());
      }

    if(left != null && right != null)
      {
	if(!right.hasAncestor_1(left))
	  {
	    if(fix)
	      {
		stmt.setRightOp(insertCast(stmt.getRightOp(), getTypeForCast(right), getTypeForCast(left), stmt));
	      }
	    else
	      {
		error("Type Error(15)");
	      }
	  }
      }
  }

  static Type getTypeForCast(TypeNode node)
      // This method is a local kludge, for avoiding NullPointerExceptions
      // when a R0_1, R0_127, or R0_32767 node is used in a type
      // cast. A more elegant solution would work with the TypeNode
      // type definition itself, but that would require a more thorough
      // knowledge of the typing system than the kludger posesses.
  {
    if (node.type() == null)
      {
	if (node == ClassHierarchy.v().R0_1)
	  {
	    return BooleanType.v();
	  }
	else if (node == ClassHierarchy.v().R0_127)
	  {
	    return ByteType.v();
	  }
	else if (node == ClassHierarchy.v().R0_32767)
	  {
	    return ShortType.v();
	  }
	// Perhaps we should throw an exception here, since I don't think
	// there should be any other cases where node.type() is null.
	// In case that supposition is incorrect, though, we'll just
	// go on to return the null, and let the callers worry about it.
      }
    return node.type();
  }

  public void caseIdentityStmt(IdentityStmt stmt)
  {
    Value l = stmt.getLeftOp();
    Value r = stmt.getRightOp();

    if(l instanceof Local)
      {
	if(((Local) l).getType() instanceof IntegerType)
	  {
	    TypeNode left = ClassHierarchy.v().typeNode((((Local) l).getType()));
	    TypeNode right = ClassHierarchy.v().typeNode(r.getType());

	    if(!right.hasAncestor_1(left))
	      {
		if(fix)
		  {
		    ((soot.jimple.internal.JIdentityStmt) stmt).setLeftOp(insertCastAfter((Local) l, getTypeForCast(left), getTypeForCast(right), stmt));
		  }
		else
		  {
		    error("Type Error(16)");
		  }
	      }
	  }
      }
  }

  public void caseEnterMonitorStmt(EnterMonitorStmt stmt)
  {
  }

  public void caseExitMonitorStmt(ExitMonitorStmt stmt)
  {
  }

  public void caseGotoStmt(GotoStmt stmt)
  {
  }

  public void caseIfStmt(IfStmt stmt)
  {
    ConditionExpr cond = (ConditionExpr) stmt.getCondition();

    BinopExpr expr = cond;
    Value lv = expr.getOp1();
    Value rv = expr.getOp2();

    TypeNode lop = null;
    TypeNode rop = null;

    //******** LEFT ********
    if(lv instanceof Local)
      {
	if(((Local) lv).getType() instanceof IntegerType)
	  {
	    lop = ClassHierarchy.v().typeNode(((Local) lv).getType());
	  }
      }
    else if(lv instanceof DoubleConstant)
      {
      }
    else if(lv instanceof FloatConstant)
      {
      }
    else if(lv instanceof IntConstant)
      {
	int value  = ((IntConstant) lv).value;

	if(value < -32768)
	  {
	    lop = ClassHierarchy.v().INT;
	  }
	else if(value < -128)
	  {
	    lop = ClassHierarchy.v().SHORT;
	  }
	else if(value < 0)
	  {
	    lop = ClassHierarchy.v().BYTE;
	  }
	else if(value < 2)
	  {
	    lop = ClassHierarchy.v().R0_1;
	  }
	else if(value < 128)
	  {
		lop = ClassHierarchy.v().R0_127;
	  }
	else if(value < 32768)
	  {
	    lop = ClassHierarchy.v().R0_32767;
	  }
	else if(value < 65536)
	  {
	    lop = ClassHierarchy.v().CHAR;
	  }
	else
	  {
	    lop = ClassHierarchy.v().INT;
	  }
      }
    else if(lv instanceof LongConstant)
      {
      }
    else if(lv instanceof NullConstant)
      {
      }
    else if(lv instanceof StringConstant)
      {
      }
    else if(lv instanceof ClassConstant)
      {
      }
    else
      {
	throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
      }

    //******** RIGHT ********
    if(rv instanceof Local)
      {
	if(((Local) rv).getType() instanceof IntegerType)
	  {
	    rop = ClassHierarchy.v().typeNode(((Local) rv).getType());
	  }
      }
    else if(rv instanceof DoubleConstant)
      {
      }
    else if(rv instanceof FloatConstant)
      {
      }
    else if(rv instanceof IntConstant)
      {
	int value  = ((IntConstant) rv).value;

	if(value < -32768)
	  {
	    rop = ClassHierarchy.v().INT;
	  }
	else if(value < -128)
	  {
	    rop = ClassHierarchy.v().SHORT;
	  }
	else if(value < 0)
	  {
	    rop = ClassHierarchy.v().BYTE;
	  }
	else if(value < 2)
	  {
	    rop = ClassHierarchy.v().R0_1;
	  }
	else if(value < 128)
	  {
	    rop = ClassHierarchy.v().R0_127;
	  }
	else if(value < 32768)
	  {
	    rop = ClassHierarchy.v().R0_32767;
	  }
	else if(value < 65536)
	  {
	    rop = ClassHierarchy.v().CHAR;
	  }
	else
	  {
	    rop = ClassHierarchy.v().INT;
	  }
      }
    else if(rv instanceof LongConstant)
      {
      }
    else if(rv instanceof NullConstant)
      {
      }
    else if(rv instanceof StringConstant)
      {
      }
    else if(rv instanceof ClassConstant)
      {
      }
    else
      {
	throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
      }

    if(lop != null && rop != null)
      {
	if(lop.lca_1(rop) == ClassHierarchy.v().TOP)
	  {
	    if(fix)
	      {
		if(!lop.hasAncestor_1(ClassHierarchy.v().INT))
		  {
		    expr.setOp1(insertCast(expr.getOp1(), getTypeForCast(lop), getTypeForCast(rop), stmt));
		  }

		if(!rop.hasAncestor_1(ClassHierarchy.v().INT))
		  {
		    expr.setOp2(insertCast(expr.getOp2(), getTypeForCast(rop), getTypeForCast(lop), stmt));
		  }
	      }
	    else
	      {
		error("Type Error(17)");
	      }
	  }
      }
  }

  public void caseLookupSwitchStmt(LookupSwitchStmt stmt)
  {
    Value key = stmt.getKey();

    if(key instanceof Local)
      {
	if(!ClassHierarchy.v().typeNode(((Local) key).getType()).
	   hasAncestor_1(ClassHierarchy.v().INT))
	  {
	    if(fix)
	      {
		stmt.setKey(insertCast((Local) key, IntType.v(), stmt));
	      }
	    else
	      {
		error("Type Error(18)");
	      }
	  }
      }
  }

  public void caseNopStmt(NopStmt stmt)
  {
  }

  public void caseReturnStmt(ReturnStmt stmt)
  {
    if(stmt.getOp() instanceof Local)
      {
	if(((Local) stmt.getOp()).getType() instanceof IntegerType)
	  {
	    if(!ClassHierarchy.v().typeNode(((Local) stmt.getOp()).getType()).
	       hasAncestor_1(ClassHierarchy.v().typeNode(stmtBody.getMethod().getReturnType())))
	      {
		if(fix)
		  {
		    stmt.setOp(insertCast((Local) stmt.getOp(), stmtBody.getMethod().getReturnType(), stmt));
		  }
		else
		  {
		    error("Type Error(19)");
		  }
	      }
	  }
      }
  }

  public void caseReturnVoidStmt(ReturnVoidStmt stmt)
  {
  }

  public void caseTableSwitchStmt(TableSwitchStmt stmt)
  {
    Value key = stmt.getKey();

    if(key instanceof Local)
      {
	if(!ClassHierarchy.v().typeNode(((Local) key).getType()).
	   hasAncestor_1(ClassHierarchy.v().INT))
	  {
	    if(fix)
	      {
		stmt.setKey(insertCast((Local) key, IntType.v(), stmt));
	      }
	    else
	      {
		error("Type Error(20)");
	      }
	  }
	resolver.typeVariable((Local) key).addParent(resolver.INT);
      }
  }

  public void caseThrowStmt(ThrowStmt stmt)
  {
  }

  public void defaultCase(Stmt stmt)
  {
    throw new RuntimeException("Unhandled statement type: " + stmt.getClass());
  }

  private Local insertCast(Local oldlocal, Type type, Stmt stmt)
  {
    Local newlocal = Jimple.v().newLocal("tmp", type);
    stmtBody.getLocals().add(newlocal);

    Unit u = Util.findFirstNonIdentityUnit(this.stmtBody, stmt);
    stmtBody.getUnits().insertBefore(
            Jimple.v().newAssignStmt(
                    newlocal, 
                    Jimple.v().newCastExpr(oldlocal, type)), u);
    return newlocal;
  }

  private Local insertCastAfter(Local leftlocal, Type lefttype, Type righttype, Stmt stmt)
  {
    Local newlocal = Jimple.v().newLocal("tmp", righttype);
    stmtBody.getLocals().add(newlocal);

    Unit u = Util.findLastIdentityUnit(this.stmtBody, stmt);
    stmtBody.getUnits().insertAfter(Jimple.v().newAssignStmt(leftlocal, Jimple.v().newCastExpr(newlocal, lefttype)), u);
    return newlocal;
  }

  private Local insertCast(Value oldvalue, Type oldtype, Type type, Stmt stmt)
  {
    Local newlocal1 = Jimple.v().newLocal("tmp", oldtype);
    Local newlocal2 = Jimple.v().newLocal("tmp", type);
    stmtBody.getLocals().add(newlocal1);
    stmtBody.getLocals().add(newlocal2);

    Unit u = Util.findFirstNonIdentityUnit(this.stmtBody, stmt);
    stmtBody.getUnits().insertBefore(Jimple.v().newAssignStmt(newlocal1, oldvalue), u);
    stmtBody.getUnits().insertBefore(Jimple.v().newAssignStmt(newlocal2, Jimple.v().newCastExpr(newlocal1, type)), u);
    return newlocal2;
  }
}
