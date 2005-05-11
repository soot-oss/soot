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


package soot.jimple.toolkits.typing;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

class ConstraintCollectorBV extends AbstractStmtSwitch
{
  private TypeResolverBV resolver;
  private ClassHierarchy hierarchy;
  private boolean uses;  // if true, include use contraints
  
  private JimpleBody stmtBody;

  public ConstraintCollectorBV(TypeResolverBV resolver, boolean uses)
  {
    this.resolver = resolver;
    this.uses = uses;

    hierarchy = resolver.hierarchy();
  }

  public void collect(Stmt stmt, JimpleBody stmtBody)
  {
    this.stmtBody = stmtBody;
    stmt.apply(this);
  }
  private void handleInvokeExpr(InvokeExpr ie)
  {
    if(!uses)
      return;

    if(ie instanceof InterfaceInvokeExpr)
      {
	InterfaceInvokeExpr invoke = (InterfaceInvokeExpr) ie;
	
	SootMethodRef method = invoke.getMethodRef();
	Value base = invoke.getBase();
	
	if(base instanceof Local)
	  {
	    Local local = (Local) base;
	    
	    TypeVariableBV localType = resolver.typeVariable(local);
	    
	    localType.addParent(resolver.typeVariable(method.declaringClass()));
	  }
	
	int count = invoke.getArgCount();
	
	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);

		TypeVariableBV localType = resolver.typeVariable(local);

		localType.addParent(resolver.typeVariable(method.parameterType(i)));
	      }
	  }
      }
    else if(ie instanceof SpecialInvokeExpr)
      {
	SpecialInvokeExpr invoke = (SpecialInvokeExpr) ie;

	SootMethodRef method = invoke.getMethodRef();
	Value base = invoke.getBase();

	if(base instanceof Local)
	  {
	    Local local = (Local) base;

	    TypeVariableBV localType = resolver.typeVariable(local);

	    localType.addParent(resolver.typeVariable(method.declaringClass()));
	  }

	int count = invoke.getArgCount();

	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);

		TypeVariableBV localType = resolver.typeVariable(local);

		localType.addParent(resolver.typeVariable(method.parameterType(i)));
	      }
	  }
      }
    else if(ie instanceof VirtualInvokeExpr)
      {
	VirtualInvokeExpr invoke = (VirtualInvokeExpr) ie;

	SootMethodRef method = invoke.getMethodRef();
	Value base = invoke.getBase();

	if(base instanceof Local)
	  {
	    Local local = (Local) base;

	    TypeVariableBV localType = resolver.typeVariable(local);

	    localType.addParent(resolver.typeVariable(method.declaringClass()));
	  }

	int count = invoke.getArgCount();

	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);

		TypeVariableBV localType = resolver.typeVariable(local);

		localType.addParent(resolver.typeVariable(method.parameterType(i)));
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

		TypeVariableBV localType = resolver.typeVariable(local);

		localType.addParent(resolver.typeVariable(method.parameterType(i)));
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
    handleInvokeExpr((InvokeExpr) stmt.getInvokeExpr());
  }

  public void caseAssignStmt(AssignStmt stmt)
  {
    Value l = stmt.getLeftOp();
    Value r = stmt.getRightOp();

    TypeVariableBV left = null;
    TypeVariableBV right = null;

    //******** LEFT ********

    if(l instanceof ArrayRef)
      {
	ArrayRef ref = (ArrayRef) l;
	Value base = ref.getBase();
	Value index = ref.getIndex();
	
	TypeVariableBV baseType = resolver.typeVariable((Local) base);
	baseType.makeElement();
	left = baseType.element();
	
	if(index instanceof Local)
	  {
	    if(uses)
	      {
		resolver.typeVariable((Local) index).addParent(resolver.typeVariable(IntType.v()));
	      }
	  }
      }
    else if(l instanceof Local)
      {
	left = resolver.typeVariable((Local) l);
      }
    else if(l instanceof InstanceFieldRef)
      {
	InstanceFieldRef ref = (InstanceFieldRef) l;
	
	if(uses)
	  {
	    TypeVariableBV baseType = resolver.typeVariable((Local) ref.getBase());
	    baseType.addParent(resolver.typeVariable(ref.getField().getDeclaringClass()));
	   
	    left = resolver.typeVariable(ref.getField().getType());
	  }
      }
    else if(l instanceof StaticFieldRef)
      {
	if(uses)
	  {
	    StaticFieldRef ref = (StaticFieldRef) l;
	    
	    left = resolver.typeVariable(ref.getField().getType());
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
	Value base = ref.getBase();
	Value index = ref.getIndex();
	
	TypeVariableBV baseType = resolver.typeVariable((Local) base);
	baseType.makeElement();
	right = baseType.element();
	
	if(index instanceof Local)
	  {
	    if(uses)
	      {
		resolver.typeVariable((Local) index).addParent(resolver.typeVariable(IntType.v()));
	      }
	  }
      }
    else if(r instanceof DoubleConstant)
      {
	right = resolver.typeVariable(DoubleType.v());
      }
    else if(r instanceof FloatConstant)
      {
	right = resolver.typeVariable(FloatType.v());
      }
    else if(r instanceof IntConstant)
      {
	right = resolver.typeVariable(IntType.v());
      }
    else if(r instanceof LongConstant)
      {
	right = resolver.typeVariable(LongType.v());
      }
    else if(r instanceof NullConstant)
      {
	right = resolver.typeVariable(NullType.v());
      }
    else if(r instanceof StringConstant)
      {
	right = resolver.typeVariable(RefType.v("java.lang.String"));
      }
    else if(r instanceof ClassConstant)
      {
	right = resolver.typeVariable(RefType.v("java.lang.Class"));
      }
    else if(r instanceof BinopExpr)
      {
	//******** BINOP EXPR ********
	
	BinopExpr be = (BinopExpr) r;

	Value lv = be.getOp1();
	Value rv = be.getOp2();
	
	TypeVariableBV lop;
	TypeVariableBV rop;

	//******** LEFT ********
	if(lv instanceof Local)
	  {
	    lop = resolver.typeVariable((Local) lv);
	  }
	else if(lv instanceof DoubleConstant)
	  {
	    lop = resolver.typeVariable(DoubleType.v());
	  }
	else if(lv instanceof FloatConstant)
	  {
	    lop = resolver.typeVariable(FloatType.v());
	  }
	else if(lv instanceof IntConstant)
	  {
	    lop = resolver.typeVariable(IntType.v());
	  }
	else if(lv instanceof LongConstant)
	  {
	    lop = resolver.typeVariable(LongType.v());
	  }
	else if(lv instanceof NullConstant)
	  {
	    lop = resolver.typeVariable(NullType.v());
	  }
	else if(lv instanceof StringConstant)
	  {
	    lop = resolver.typeVariable(RefType.v("java.lang.String"));
	  }
	else if(lv instanceof ClassConstant)
	  {
	    lop = resolver.typeVariable(RefType.v("java.lang.Class"));
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
	  }
	
	//******** RIGHT ********
	if(rv instanceof Local)
	  {
	    rop = resolver.typeVariable((Local) rv);
	  }
	else if(rv instanceof DoubleConstant)
	  {
	    rop = resolver.typeVariable(DoubleType.v());
	  }
	else if(rv instanceof FloatConstant)
	  {
	    rop = resolver.typeVariable(FloatType.v());
	  }
	else if(rv instanceof IntConstant)
	  {
	    rop = resolver.typeVariable(IntType.v());
	  }
	else if(rv instanceof LongConstant)
	  {
	    rop = resolver.typeVariable(LongType.v());
	  }
	else if(rv instanceof NullConstant)
	  {
	    rop = resolver.typeVariable(NullType.v());
	  }
	else if(rv instanceof StringConstant)
	  {
	    rop = resolver.typeVariable(RefType.v("java.lang.String"));
	  }
	else if(rv instanceof ClassConstant)
	  {
	    rop = resolver.typeVariable(RefType.v("java.lang.Class"));
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
	  }
	
	if((be instanceof AddExpr) ||
	   (be instanceof SubExpr) ||
	   (be instanceof MulExpr) ||
	   (be instanceof DivExpr) ||
	   (be instanceof RemExpr) ||
	   (be instanceof AndExpr) ||
	   (be instanceof OrExpr) ||
	   (be instanceof XorExpr))
	  {
	    if(uses)
	      {
		TypeVariableBV common = resolver.typeVariable();
		rop.addParent(common);
		lop.addParent(common);
	      }
	    
	    if(left != null)
	      {
		rop.addParent(left);
		lop.addParent(left);
	      }
	  }
	else if((be instanceof ShlExpr) ||
		(be instanceof ShrExpr) ||
		(be instanceof UshrExpr))
	  {
	    if(uses)
	      {
		rop.addParent(resolver.typeVariable(IntType.v()));
	      }
	    
	    right = lop;
	  }
	else if((be instanceof CmpExpr) ||
		(be instanceof CmpgExpr) ||
		(be instanceof CmplExpr) ||
		(be instanceof EqExpr) ||
		(be instanceof GeExpr) ||
		(be instanceof GtExpr) ||
		(be instanceof LeExpr) ||
		(be instanceof LtExpr) ||
		(be instanceof NeExpr))
	  {
	    if(uses)
	      {
		TypeVariableBV common = resolver.typeVariable();
		rop.addParent(common);
		lop.addParent(common);
	      }
	    
	    right = resolver.typeVariable(IntType.v());
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression type: " + be.getClass());
	  }
      }
    else if(r instanceof CastExpr)
      {
	CastExpr ce = (CastExpr) r;

	right = resolver.typeVariable(ce.getCastType());
      }
    else if(r instanceof InstanceOfExpr)
      {
	InstanceOfExpr ioe = (InstanceOfExpr) r;
	
	right = resolver.typeVariable(IntType.v());
      }
    else if(r instanceof InvokeExpr)
      {
	InvokeExpr ie = (InvokeExpr) r;

	handleInvokeExpr(ie);

	right = resolver.typeVariable(ie.getMethodRef().returnType());
      }
    else if(r instanceof NewArrayExpr)
      {
	NewArrayExpr nae = (NewArrayExpr) r;

	Type baseType = nae.getBaseType();

	if(baseType instanceof ArrayType)
	  {
	    right = resolver.typeVariable(ArrayType.v(((ArrayType) baseType).baseType, 
						      ((ArrayType) baseType).numDimensions + 1));
	  }
	else
	  {
	    right = resolver.typeVariable(ArrayType.v(baseType, 1));
	  }

	if(uses)
	  {
	    Value size = nae.getSize();
	    if(size instanceof Local)
	      {
		TypeVariableBV var = resolver.typeVariable((Local) size);
		var.addParent(resolver.typeVariable(IntType.v()));
	      }
	  }
      }
    else if(r instanceof NewExpr)
      {
	NewExpr na = (NewExpr) r;

	right = resolver.typeVariable(na.getBaseType());
      }
    else if(r instanceof NewMultiArrayExpr)
      {
	NewMultiArrayExpr nmae = (NewMultiArrayExpr) r;

	right = resolver.typeVariable(nmae.getBaseType());

	if(uses)
	  {
	    for(int i = 0; i < nmae.getSizeCount(); i++)
	      {
		Value size = nmae.getSize(i);
		if(size instanceof Local)
		  {
		    TypeVariableBV var = resolver.typeVariable((Local) size);
		    var.addParent(resolver.typeVariable(IntType.v()));
		  }
	      }
	  }
      }
    else if(r instanceof LengthExpr)
      {
	LengthExpr le = (LengthExpr) r;

	if(uses)
	  {
	    if(le.getOp() instanceof Local)
	      {
		resolver.typeVariable((Local) le.getOp()).makeElement();
	      }
	  }

	right = resolver.typeVariable(IntType.v());
      }
    else if(r instanceof NegExpr)
      {
	NegExpr ne = (NegExpr) r;

	if(ne.getOp() instanceof Local)
	  {
	    right = resolver.typeVariable((Local) ne.getOp());
	  }
	else if(ne.getOp() instanceof DoubleConstant)
	  {
	    right = resolver.typeVariable(DoubleType.v());
	  }
	else if(ne.getOp() instanceof FloatConstant)
	  {
	    right = resolver.typeVariable(FloatType.v());
	  }
	else if(ne.getOp() instanceof IntConstant)
	  {
	    right = resolver.typeVariable(IntType.v());
	  }
	else if(ne.getOp() instanceof LongConstant)
	  {
	    right = resolver.typeVariable(LongType.v());
	  }
	else
	  {
	    throw new RuntimeException("Unhandled neg expression operand type: " + ne.getOp().getClass());
	  }
      }
    else if(r instanceof Local)
      {
	right = resolver.typeVariable((Local) r);
      }
    else if(r instanceof InstanceFieldRef)
      {
	InstanceFieldRef ref = (InstanceFieldRef) r;

	if(uses)
	  {
	    TypeVariableBV baseType = resolver.typeVariable((Local) ref.getBase());
	    baseType.addParent(resolver.typeVariable(ref.getField().getDeclaringClass()));
	  }
	
	right = resolver.typeVariable(ref.getField().getType());
      }
    else if(r instanceof StaticFieldRef)
      {
	StaticFieldRef ref = (StaticFieldRef) r;

	right = resolver.typeVariable(ref.getField().getType());
      }
    else
      {
	throw new RuntimeException("Unhandled assignment right hand side type: " + r.getClass());
      }

    if(left != null && right != null)
      {
	right.addParent(left);
      }
  }

  public void caseIdentityStmt(IdentityStmt stmt)
  {
    Value l = stmt.getLeftOp();
    Value r = stmt.getRightOp();

    if(l instanceof Local)
      {
	TypeVariableBV left = resolver.typeVariable((Local) l);

	if(!(r instanceof CaughtExceptionRef))
	  {
	    TypeVariableBV right = resolver.typeVariable(r.getType());
	    right.addParent(left);
	  }
	else
	  {
	    List exceptionTypes = TrapManager.getExceptionTypesOf(stmt, stmtBody);
	    Iterator typeIt = exceptionTypes.iterator();

	    while(typeIt.hasNext())
	      {
		Type t = (Type) typeIt.next();

		resolver.typeVariable(t).addParent(left);
	      }

	    if(uses)
	      {
		left.addParent(resolver.typeVariable(RefType.v("java.lang.Throwable")));
	      }
	  }
      }
  }

  public void caseEnterMonitorStmt(EnterMonitorStmt stmt)
  {
    if(uses)
      {
	if(stmt.getOp() instanceof Local)
	  {
	    TypeVariableBV op = resolver.typeVariable((Local) stmt.getOp());
	    
	    op.addParent(resolver.typeVariable(RefType.v("java.lang.Object")));
	  }
      }
  }

  public void caseExitMonitorStmt(ExitMonitorStmt stmt)
  {
    if(uses)
      {
	if(stmt.getOp() instanceof Local)
	  {
	    TypeVariableBV op = resolver.typeVariable((Local) stmt.getOp());
	    
	    op.addParent(resolver.typeVariable(RefType.v("java.lang.Object")));
	  }
      }
  }

  public void caseGotoStmt(GotoStmt stmt)
  {
  }

  public void caseIfStmt(IfStmt stmt)
  {
    if(uses)
      {
	ConditionExpr cond = (ConditionExpr) stmt.getCondition();
	
	BinopExpr expr = (BinopExpr) cond;
	Value lv = expr.getOp1();
	Value rv = expr.getOp2();
	
	TypeVariableBV lop;
	TypeVariableBV rop;

	//******** LEFT ********
	if(lv instanceof Local)
	  {
	    lop = resolver.typeVariable((Local) lv);
	  }
	else if(lv instanceof DoubleConstant)
	  {
	    lop = resolver.typeVariable(DoubleType.v());
	  }
	else if(lv instanceof FloatConstant)
	  {
	    lop = resolver.typeVariable(FloatType.v());
	  }
	else if(lv instanceof IntConstant)
	  {
	    lop = resolver.typeVariable(IntType.v());
	  }
	else if(lv instanceof LongConstant)
	  {
	    lop = resolver.typeVariable(LongType.v());
	  }
	else if(lv instanceof NullConstant)
	  {
	    lop = resolver.typeVariable(NullType.v());
	  }
	else if(lv instanceof StringConstant)
	  {
	    lop = resolver.typeVariable(RefType.v("java.lang.String"));
	  }
	else if(lv instanceof ClassConstant)
	  {
	    lop = resolver.typeVariable(RefType.v("java.lang.Class"));
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
	  }
	
	//******** RIGHT ********
	if(rv instanceof Local)
	  {
	    rop = resolver.typeVariable((Local) rv);
	  }
	else if(rv instanceof DoubleConstant)
	  {
	    rop = resolver.typeVariable(DoubleType.v());
	  }
	else if(rv instanceof FloatConstant)
	  {
	    rop = resolver.typeVariable(FloatType.v());
	  }
	else if(rv instanceof IntConstant)
	  {
	    rop = resolver.typeVariable(IntType.v());
	  }
	else if(rv instanceof LongConstant)
	  {
	    rop = resolver.typeVariable(LongType.v());
	  }
	else if(rv instanceof NullConstant)
	  {
	    rop = resolver.typeVariable(NullType.v());
	  }
	else if(rv instanceof StringConstant)
	  {
	    rop = resolver.typeVariable(RefType.v("java.lang.String"));
	  }
	else if(rv instanceof ClassConstant)
	  {
	    rop = resolver.typeVariable(RefType.v("java.lang.Class"));
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
	  }

	TypeVariableBV common = resolver.typeVariable();
	rop.addParent(common);
	lop.addParent(common);
      }
  }

  public void caseLookupSwitchStmt(LookupSwitchStmt stmt)
  {
    if(uses)
      {
	Value key = stmt.getKey();

	if(key instanceof Local)
	  {
	    resolver.typeVariable((Local) key).addParent(resolver.typeVariable(IntType.v()));
	  }
      }
  }

  public void caseNopStmt(NopStmt stmt)
  {
  }

  public void caseReturnStmt(ReturnStmt stmt)
  {
    if(uses)
      {
	if(stmt.getOp() instanceof Local)
	  {
	    resolver.typeVariable((Local) stmt.getOp()).
	      addParent(resolver.typeVariable(stmtBody.getMethod().getReturnType()));
	  }
      }
  }

  public void caseReturnVoidStmt(ReturnVoidStmt stmt)
  {
  }

  public void caseTableSwitchStmt(TableSwitchStmt stmt)
  {
    if(uses)
      {
	Value key = stmt.getKey();
	
	if(key instanceof Local)
	  {
	    resolver.typeVariable((Local) key).addParent(resolver.typeVariable(IntType.v()));
	  }
      }
  }

  public void caseThrowStmt(ThrowStmt stmt)
  {
    if(uses)
      {
	if(stmt.getOp() instanceof Local)
	  {
	    TypeVariableBV op = resolver.typeVariable((Local) stmt.getOp());
	    
	    op.addParent(resolver.typeVariable(RefType.v("java.lang.Throwable")));
	  }
      }
  }

  public void defaultCase(Stmt stmt)
  {
    throw new RuntimeException("Unhandled statement type: " + stmt.getClass());
  }
}
