/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
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






package soot.grimp;

import soot.*;
import soot.jimple.*;
import soot.grimp.internal.*;
import soot.util.*;
import java.util.*;
import java.io.*;

/**
    The Grimp class contains all the constructors for the components of the Grimp
    grammar for the Grimp body. <br><br>

    Immediate -> Local | Constant <br>
    RValue -> Local | Constant | ConcreteRef | Expr<br>
    Variable -> Local | ArrayRef | InstanceFieldRef | StaticFieldRef <br>
 */


public class Grimp
{
    public Grimp( Singletons.Global g ) {}
    public static Grimp v() { return G.v().soot_grimp_Grimp(); }

    /**
        Constructs a XorExpr(Expr, Expr) grammar chunk.
     */

    public XorExpr newXorExpr(Value op1, Value op2)
    {
        return new GXorExpr(op1, op2);
    }


    /**
        Constructs a UshrExpr(Expr, Expr) grammar chunk.
     */

    public UshrExpr newUshrExpr(Value op1, Value op2)
    {
        return new GUshrExpr(op1, op2);
    }


    /**
        Constructs a SubExpr(Expr, Expr) grammar chunk.
     */

    public SubExpr newSubExpr(Value op1, Value op2)
    {
        return new GSubExpr(op1, op2);
    }


    /**
        Constructs a ShrExpr(Expr, Expr) grammar chunk.
     */

    public ShrExpr newShrExpr(Value op1, Value op2)
    {
        return new GShrExpr(op1, op2);
    }


    /**
        Constructs a ShlExpr(Expr, Expr) grammar chunk.
     */

    public ShlExpr newShlExpr(Value op1, Value op2)
    {
        return new GShlExpr(op1, op2);
    }


    /**
        Constructs a RemExpr(Expr, Expr) grammar chunk.
     */

    public RemExpr newRemExpr(Value op1, Value op2)
    {
        return new GRemExpr(op1, op2);
    }


    /**
        Constructs a OrExpr(Expr, Expr) grammar chunk.
     */

    public OrExpr newOrExpr(Value op1, Value op2)
    {
        return new GOrExpr(op1, op2);
    }


    /**
        Constructs a NeExpr(Expr, Expr) grammar chunk.
     */

    public NeExpr newNeExpr(Value op1, Value op2)
    {
        return new GNeExpr(op1, op2);
    }


    /**
        Constructs a MulExpr(Expr, Expr) grammar chunk.
     */

    public MulExpr newMulExpr(Value op1, Value op2)
    {
        return new GMulExpr(op1, op2);
    }


    /**
        Constructs a LeExpr(Expr, Expr) grammar chunk.
     */

    public LeExpr newLeExpr(Value op1, Value op2)
    {
        return new GLeExpr(op1, op2);
    }


    /**
        Constructs a GeExpr(Expr, Expr) grammar chunk.
     */

    public GeExpr newGeExpr(Value op1, Value op2)
    {
        return new GGeExpr(op1, op2);
    }


    /**
        Constructs a EqExpr(Expr, Expr) grammar chunk.
     */

    public EqExpr newEqExpr(Value op1, Value op2)
    {
        return new GEqExpr(op1, op2);
    }

    /**
        Constructs a DivExpr(Expr, Expr) grammar chunk.
     */

    public DivExpr newDivExpr(Value op1, Value op2)
    {
        return new GDivExpr(op1, op2);
    }


    /**
        Constructs a CmplExpr(Expr, Expr) grammar chunk.
     */

    public CmplExpr newCmplExpr(Value op1, Value op2)
    {
        return new GCmplExpr(op1, op2);
    }


    /**
        Constructs a CmpgExpr(Expr, Expr) grammar chunk.
     */

    public CmpgExpr newCmpgExpr(Value op1, Value op2)
    {
        return new GCmpgExpr(op1, op2);
    }


    /**
        Constructs a CmpExpr(Expr, Expr) grammar chunk.
     */

    public CmpExpr newCmpExpr(Value op1, Value op2)
    {
        return new GCmpExpr(op1, op2);
    }


    /**
        Constructs a GtExpr(Expr, Expr) grammar chunk.
     */

    public GtExpr newGtExpr(Value op1, Value op2)
    {
        return new GGtExpr(op1, op2);
    }


    /**
        Constructs a LtExpr(Expr, Expr) grammar chunk.
     */

    public LtExpr newLtExpr(Value op1, Value op2)
    {
        return new GLtExpr(op1, op2);
    }

    /**
        Constructs a AddExpr(Expr, Expr) grammar chunk.
     */

    public AddExpr newAddExpr(Value op1, Value op2)
    {
        return new GAddExpr(op1, op2);
    }


    /**
        Constructs a AndExpr(Expr, Expr) grammar chunk.
     */

    public AndExpr newAndExpr(Value op1, Value op2)
    {
        return new GAndExpr(op1, op2);
    }


    /**
        Constructs a NegExpr(Expr, Expr) grammar chunk.
     */

    public NegExpr newNegExpr(Value op)
    {
        return new GNegExpr(op);
    }


    /**
        Constructs a LengthExpr(Expr) grammar chunk.
     */

    public LengthExpr newLengthExpr(Value op)
    {
        return new GLengthExpr(op);
    }


    /**
        Constructs a CastExpr(Expr, Type) grammar chunk.
     */

    public CastExpr newCastExpr(Value op1, Type t)
    {
        return new GCastExpr(op1, t);
    }

    /**
        Constructs a InstanceOfExpr(Expr, Type)
        grammar chunk.
     */

    public InstanceOfExpr newInstanceOfExpr(Value op1, Type t)
    {
        return new GInstanceOfExpr(op1, t);
    }


    /**
        Constructs a NewExpr(RefType) grammar chunk.
     */

    NewExpr newNewExpr(RefType type)
    {
        return Jimple.v().newNewExpr(type);
    }


    /**
        Constructs a NewArrayExpr(Type, Expr) grammar chunk.
     */

    public NewArrayExpr newNewArrayExpr(Type type, Value size)
    {
        return new GNewArrayExpr(type, size);
    }

    /**
        Constructs a NewMultiArrayExpr(ArrayType, List of Expr) grammar chunk.
     */

    public NewMultiArrayExpr newNewMultiArrayExpr(ArrayType type, List sizes)
    {
        return new GNewMultiArrayExpr(type, sizes);
    }

    /**
        Constructs a NewInvokeExpr(Local base, List of Expr) grammar chunk.
     */

    public NewInvokeExpr newNewInvokeExpr(RefType base, SootMethodRef method, List args)
    {
        return new GNewInvokeExpr(base, method, args);
    }

    /**
        Constructs a StaticInvokeExpr(ArrayType, List of Expr) grammar chunk.
     */

    public StaticInvokeExpr newStaticInvokeExpr(SootMethodRef method, List args)
    {
        return new GStaticInvokeExpr(method, args);
    }


    /**
        Constructs a SpecialInvokeExpr(Local base, SootMethodRef method, List of Expr) grammar chunk.
     */

    public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethodRef method, List args)
    {
        return new GSpecialInvokeExpr(base, method, args);
    }


    /**
        Constructs a VirtualInvokeExpr(Local base, SootMethodRef method, List of Expr) grammar chunk.
     */

    public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethodRef method, List args)
    {
        return new GVirtualInvokeExpr(base, method, args);
    }


    /**
        Constructs a InterfaceInvokeExpr(Local base, SootMethodRef method, List of Expr) grammar chunk.
     */

    public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethodRef method, List args)
    {
        return new GInterfaceInvokeExpr(base, method, args);
    }


    /**
        Constructs a ThrowStmt(Expr) grammar chunk.
     */

    public ThrowStmt newThrowStmt(Value op)
    {
        return new GThrowStmt(op);
    }

    public ThrowStmt newThrowStmt(ThrowStmt s)
    {
        return new GThrowStmt(s.getOp());
    }

    /**
        Constructs a ExitMonitorStmt(Expr) grammar chunk
     */

    public ExitMonitorStmt newExitMonitorStmt(Value op)
    {
        return new GExitMonitorStmt(op);
    }

    public ExitMonitorStmt newExitMonitorStmt(ExitMonitorStmt s)
    {
        return new GExitMonitorStmt(s.getOp());
    }

    /**
        Constructs a EnterMonitorStmt(Expr) grammar chunk.
     */

    public EnterMonitorStmt newEnterMonitorStmt(Value op)
    {
        return new GEnterMonitorStmt(op);
    }

    public EnterMonitorStmt newEnterMonitorStmt(EnterMonitorStmt s)
    {
        return new GEnterMonitorStmt(s.getOp());
    }

    /**
        Constructs a BreakpointStmt() grammar chunk.
     */

    public BreakpointStmt newBreakpointStmt()
    {
        return Jimple.v().newBreakpointStmt();
    }
    
    public BreakpointStmt newBreakpointStmt(BreakpointStmt s)
    {
        return Jimple.v().newBreakpointStmt();
    }

    /**
        Constructs a GotoStmt(Stmt) grammar chunk.
     */

    public GotoStmt newGotoStmt(Unit target)
    {
        return Jimple.v().newGotoStmt(target);
    }

    public GotoStmt newGotoStmt(GotoStmt s)
    {
        return Jimple.v().newGotoStmt(s.getTarget());
    }

    /**
        Constructs a NopStmt() grammar chunk.
     */

    public NopStmt newNopStmt()
    {
        return Jimple.v().newNopStmt();
    }

    public NopStmt newNopStmt(NopStmt s)
    {
        return Jimple.v().newNopStmt();
    }

    /**
        Constructs a ReturnVoidStmt() grammar chunk.
     */

    public ReturnVoidStmt newReturnVoidStmt()
    {
        return Jimple.v().newReturnVoidStmt();
    }

    public ReturnVoidStmt newReturnVoidStmt(ReturnVoidStmt s)
    {
        return Jimple.v().newReturnVoidStmt();
    }

    /**
        Constructs a ReturnStmt(Expr) grammar chunk.
     */

    public ReturnStmt newReturnStmt(Value op)
    {
        return new GReturnStmt(op);
    }

    public ReturnStmt newReturnStmt(ReturnStmt s)
    {
        return new GReturnStmt(s.getOp());
    }

    /**
        Constructs a IfStmt(Condition, Stmt) grammar chunk.
     */

    public IfStmt newIfStmt(Value condition, Unit target)
    {
        return new GIfStmt(condition, target);
    }

    public IfStmt newIfStmt(IfStmt s)
    {
        return new GIfStmt(s.getCondition(), s.getTarget());
    }

    /**
        Constructs a IdentityStmt(Local, IdentityRef) grammar chunk.
     */

    public IdentityStmt newIdentityStmt(Value local, Value identityRef)
    {
        return new GIdentityStmt(local, identityRef);
    }

    public IdentityStmt newIdentityStmt(IdentityStmt s)
    {
        return new GIdentityStmt(s.getLeftOp(), s.getRightOp());
    }

    /**
        Constructs a AssignStmt(Variable, RValue) grammar chunk.
     */

    public AssignStmt newAssignStmt(Value variable, Value rvalue)
    {
        return new GAssignStmt(variable, rvalue);
    }

    public AssignStmt newAssignStmt(AssignStmt s)
    {
        return new GAssignStmt(s.getLeftOp(), s.getRightOp());
    }

    /**
        Constructs a InvokeStmt(InvokeExpr) grammar chunk.
     */

    public InvokeStmt newInvokeStmt(Value op)
    {
        return new GInvokeStmt(op);
    }

    public InvokeStmt newInvokeStmt(InvokeStmt s)
    {
        return new GInvokeStmt(s.getInvokeExpr());
    }

    /**
        Constructs a TableSwitchStmt(Expr, int, int, List of Unit, Stmt) grammar chunk.
     */

    public TableSwitchStmt newTableSwitchStmt(Value key, int lowIndex, int highIndex, List targets, Unit defaultTarget)
    {
        return new GTableSwitchStmt(key, lowIndex, highIndex, targets, defaultTarget);
    }

    public TableSwitchStmt newTableSwitchStmt(TableSwitchStmt s)
    {
        return new GTableSwitchStmt(s.getKey(), s.getLowIndex(), 
                                    s.getHighIndex(), s.getTargets(),
                                    s.getDefaultTarget());
    }

    /**
        Constructs a LookupSwitchStmt(Expr, List of Expr, List of Unit, Stmt) grammar chunk.
     */

    public LookupSwitchStmt newLookupSwitchStmt(Value key, List lookupValues, List targets, Unit defaultTarget)
    {
        return new GLookupSwitchStmt(key, lookupValues, targets, defaultTarget);
    }

    public LookupSwitchStmt newLookupSwitchStmt(LookupSwitchStmt s)
    {
        return new GLookupSwitchStmt(s.getKey(), s.getLookupValues(),
                                     s.getTargets(), s.getDefaultTarget());
    }

    /**
        Constructs a Local with the given name and type.
    */

    public Local newLocal(String name, Type t)
    {
        return Jimple.v().newLocal(name, t);
    }

    /**
        Constructs a new Trap for the given exception on the given Stmt range with the given Stmt handler.
    */

    public Trap newTrap(SootClass exception, Unit beginStmt, Unit endStmt, Unit handlerStmt)
    {
        return new GTrap(exception, beginStmt, endStmt, handlerStmt);
    }

    public Trap newTrap(Trap trap)
    {
        return new GTrap(trap.getException(), trap.getBeginUnit(),
                         trap.getEndUnit(), trap.getHandlerUnit());
    }

    /**
        Constructs a StaticFieldRef(SootFieldRef) grammar chunk.
     */

    public StaticFieldRef newStaticFieldRef(SootFieldRef f)
    {
        return Jimple.v().newStaticFieldRef(f);
    }


    /**
        Constructs a ThisRef(RefType) grammar chunk.
     */

    public ThisRef newThisRef(RefType t)
    {
        return Jimple.v().newThisRef(t);
    }


    /**
        Constructs a ParameterRef(SootMethod, int) grammar chunk.
     */

    public ParameterRef newParameterRef(Type paramType, int number)
    {
        return Jimple.v().newParameterRef(paramType, number);
    }

    /**
        Constructs a InstanceFieldRef(Value, SootFieldRef) grammar chunk.
     */

    public InstanceFieldRef newInstanceFieldRef(Value base, SootFieldRef f)
    {
        return new GInstanceFieldRef(base, f);
    }


    /**
        Constructs a CaughtExceptionRef() grammar chunk.
     */

    public CaughtExceptionRef newCaughtExceptionRef()
    {
        return Jimple.v().newCaughtExceptionRef();
    }


    /**
        Constructs a ArrayRef(Local, Expr) grammar chunk.
     */

    public ArrayRef newArrayRef(Value base, Value index)
    {
        return new GArrayRef(base, index);
    }

    public ValueBox newVariableBox(Value value)
    {
        return Jimple.v().newVariableBox(value);
    }

    public ValueBox newLocalBox(Value value)
    {
        return Jimple.v().newLocalBox(value);
    }

    public ValueBox newRValueBox(Value value)
    {
        return new GRValueBox(value);
    }

    public ValueBox newImmediateBox(Value value)
    {
        return Jimple.v().newImmediateBox(value);
    }

    public ValueBox newExprBox(Value value)
    {
        return new ExprBox(value);
    }

    public ValueBox newArgBox(Value value)
    {
        return new ExprBox(value);
    }

    public ValueBox newObjExprBox(Value value)
    {
        return new ObjExprBox(value);
    }

    public ValueBox newIdentityRefBox(Value value)
    {
        return Jimple.v().newIdentityRefBox(value);
    }

    public ValueBox newConditionExprBox(Value value)
    {
        return Jimple.v().newConditionExprBox(value);
    }

    public ValueBox newInvokeExprBox(Value value)
    {
        return Jimple.v().newInvokeExprBox(value);
    }

    public UnitBox newStmtBox(Unit unit)
    {
        return Jimple.v().newStmtBox((Stmt) unit);
    }

    /** Carries out the mapping from other Value's to Grimp Value's */
    public Value newExpr(Value value)
    {
        if (value instanceof Expr)
            {
                final ExprBox returnedExpr = new ExprBox(IntConstant.v(0));
                ((Expr)value).apply(new AbstractExprSwitch()
                {
                    public void caseAddExpr(AddExpr v)
                    {
                        returnedExpr.setValue
                            (newAddExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseAndExpr(AndExpr v)
                    {
                        returnedExpr.setValue
                            (newAndExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseCmpExpr(CmpExpr v)
                    {
                        returnedExpr.setValue
                            (newCmpExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseCmpgExpr(CmpgExpr v)
                    {
                        returnedExpr.setValue
                            (newCmpgExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseCmplExpr(CmplExpr v)
                    {
                        returnedExpr.setValue
                            (newCmplExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseDivExpr(DivExpr v)
                    {
                        returnedExpr.setValue
                            (newDivExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseEqExpr(EqExpr v)
                    {
                        returnedExpr.setValue
                            (newEqExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseNeExpr(NeExpr v)
                    {
                        returnedExpr.setValue
                            (newNeExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseGeExpr(GeExpr v)
                    {
                        returnedExpr.setValue
                            (newGeExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseGtExpr(GtExpr v)
                    {
                        returnedExpr.setValue
                            (newGtExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseLeExpr(LeExpr v)
                    {
                        returnedExpr.setValue
                            (newLeExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseLtExpr(LtExpr v)
                    {
                        returnedExpr.setValue
                            (newLtExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseMulExpr(MulExpr v)
                    {
                        returnedExpr.setValue
                            (newMulExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseOrExpr(OrExpr v)
                    {
                        returnedExpr.setValue
                            (newOrExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseRemExpr(RemExpr v)
                    {
                        returnedExpr.setValue
                            (newRemExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseShlExpr(ShlExpr v)
                    {
                        returnedExpr.setValue
                            (newShlExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseShrExpr(ShrExpr v)
                    {
                        returnedExpr.setValue
                            (newShrExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseUshrExpr(UshrExpr v)
                    {
                        returnedExpr.setValue
                            (newUshrExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseSubExpr(SubExpr v)
                    {
                        returnedExpr.setValue
                            (newSubExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseXorExpr(XorExpr v)
                    {
                        returnedExpr.setValue
                            (newXorExpr(newExpr(v.getOp1()),
                                        newExpr(v.getOp2())));
                    }

                    public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v)
                    {
                        ArrayList newArgList = new ArrayList();
                        for (int i = 0; i < v.getArgCount(); i++)
                            newArgList.add(newExpr(v.getArg(i)));
                        returnedExpr.setValue
                            (newInterfaceInvokeExpr((Local)(v.getBase()),
                                                    v.getMethodRef(),
                                                    newArgList));
                    }

                    public void caseSpecialInvokeExpr(SpecialInvokeExpr v)
                    {
                        ArrayList newArgList = new ArrayList();
                        for (int i = 0; i < v.getArgCount(); i++)
                            newArgList.add(newExpr(v.getArg(i)));
                        returnedExpr.setValue
                            (newSpecialInvokeExpr((Local)(v.getBase()),
                                                    v.getMethodRef(),
                                                    newArgList));
                    }

                    public void caseStaticInvokeExpr(StaticInvokeExpr v)
                    {
                        ArrayList newArgList = new ArrayList();
                        for (int i = 0; i < v.getArgCount(); i++)
                            newArgList.add(newExpr(v.getArg(i)));
                        returnedExpr.setValue
                            (newStaticInvokeExpr(v.getMethodRef(),
                                                 newArgList));
                    }

                    public void caseVirtualInvokeExpr(VirtualInvokeExpr v)
                    {
                        ArrayList newArgList = new ArrayList();
                        for (int i = 0; i < v.getArgCount(); i++)
                            newArgList.add(newExpr(v.getArg(i)));
                        returnedExpr.setValue
                            (newVirtualInvokeExpr((Local)(v.getBase()),
                                                  v.getMethodRef(),
                                                  newArgList));
                    }

                    public void caseCastExpr(CastExpr v)
                    {
                        returnedExpr.setValue(newCastExpr(newExpr(v.getOp()),
                                                          v.getType()));
                    }

                    public void caseInstanceOfExpr(InstanceOfExpr v)
                    {
                        returnedExpr.setValue(newInstanceOfExpr
                                              (newExpr(v.getOp()),
                                               v.getCheckType()));
                    }

                    public void caseNewArrayExpr(NewArrayExpr v)
                    {
                        returnedExpr.setValue(newNewArrayExpr(v.getBaseType(),
                                              v.getSize()));
                    }

                    public void caseNewMultiArrayExpr(NewMultiArrayExpr v)
                     {
                        returnedExpr.setValue(newNewMultiArrayExpr
                                              (v.getBaseType(),
                                              v.getSizes()));
                    }

                    public void caseNewExpr(NewExpr v)
                    {
                        returnedExpr.setValue(newNewExpr(v.getBaseType()));
                    }

                    public void caseLengthExpr(LengthExpr v)
                      {
                        returnedExpr.setValue(newLengthExpr
                                              (newExpr(v.getOp())));
                    }

                    public void caseNegExpr(NegExpr v)
                    {
                        returnedExpr.setValue(newNegExpr(newExpr(v.getOp())));
                    }

                    public void defaultCase(Object v)
                    {
                        returnedExpr.setValue((Expr)v);
                    }                        
                });
                return returnedExpr.getValue();
            }
        else 
            {
                if (value instanceof ArrayRef)
                    return newArrayRef(((ArrayRef)value).getBase(), 
                                       newExpr(((ArrayRef)value).getIndex()));
                if (value instanceof InstanceFieldRef)
                    return newInstanceFieldRef
                        (newExpr((((InstanceFieldRef)value).getBase())),
                         ((InstanceFieldRef)value).getFieldRef());
                /* have Ref/Value, which is fine -- not Jimple-specific. */
                return value;
            }
    }

    /** Returns an empty GrimpBody associated with method m. */
    public GrimpBody newBody(SootMethod m)
    {
        return new GrimpBody(m);
    }

    /** Returns a GrimpBody constructed from b. */
    public GrimpBody newBody(Body b, String phase)
    {
        return new GrimpBody(b);
    }

    public static Value cloneIfNecessary(Value val) 
    {
        if( val instanceof Local || val instanceof Constant )
            return val;
        else
            return (Value) val.clone();
    } 
}
