/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Patrick Lam (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Patrick Lam.  All rights reserved.             *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on September 12, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca (*)
   Changed PrintStream to PrintWriter.

 - Modified on 31-Aug-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Minor print changes.

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Changed Hashtable to HashMap.

 - Modified on July 5, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Changed caseDefault to defaultCase, to avoid name conflicts (and conform
   to the standard).

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.io.*;

/**
    The JimpleRepresentation interface defines all the constructors for the components of the Jimple
    grammar for the Jimple body. <br><br>

    Immediate -> Local | Constant <br>
    RValue -> Local | Constant | ConcreteRef | Expr<br>
    Variable -> Local | ArrayRef | InstanceFieldRef | StaticFieldRef <br>
 */


public interface JimpleRepresentation extends BodyRepresentation
{
  /**
     Constructs an empty JimpleBody for the given method.
  */
     
  public Body newBody(SootMethod m);
  public Body buildBodyOfFrom(SootMethod m, Body b, int buildBodyOptions);

  /**
     Constructs an empty ArgBox for a grammar chunk.
  */
  public ValueBox newArgBox(Value op);

  /**
     Constructs a XorExpr(Arg, Arg) grammar chunk.
  */

  public XorExpr newXorExpr(Value op1, Value op2);
  
  /**
     Constructs a UshrExpr(Arg, Arg) grammar chunk.
  */

  public UshrExpr newUshrExpr(Value op1, Value op2);
  
  /**
     Constructs a SubExpr(Arg, Arg) grammar chunk.
  */
  
  public SubExpr newSubExpr(Value op1, Value op2);
  
  /**
     Constructs a ShrExpr(Arg, Arg) grammar chunk.
  */
  
  public ShrExpr newShrExpr(Value op1, Value op2);
  
  /**
     Constructs a ShlExpr(Arg, Arg) grammar chunk.
  */
  
  public ShlExpr newShlExpr(Value op1, Value op2);
  
  /**
     Constructs a RemExpr(Arg, Arg) grammar chunk.
  */
  
  public RemExpr newRemExpr(Value op1, Value op2);
  
  /**
     Constructs a OrExpr(Arg, Arg) grammar chunk.
  */
  
  public OrExpr newOrExpr(Value op1, Value op2);
  
  /**
     Constructs a NeExpr(Arg, Arg) grammar chunk.
  */
  
  public NeExpr newNeExpr(Value op1, Value op2);
  
  /**
     Constructs a MulExpr(Arg, Arg) grammar chunk.
  */
  
  public MulExpr newMulExpr(Value op1, Value op2);
  
  /**
     Constructs a LeExpr(Arg, Arg) grammar chunk.
  */
  
  public LeExpr newLeExpr(Value op1, Value op2);
  
  /**
     Constructs a GeExpr(Arg, Arg) grammar chunk.
  */
  
  public GeExpr newGeExpr(Value op1, Value op2);
  
  /**
     Constructs a EqExpr(Arg, Arg) grammar chunk.
  */
  
  public EqExpr newEqExpr(Value op1, Value op2);
  
  /**
     Constructs a DivExpr(Arg, Arg) grammar chunk.
  */
  
  public DivExpr newDivExpr(Value op1, Value op2);
  
  /**
     Constructs a CmplExpr(Arg, Arg) grammar chunk.
  */
  
  public CmplExpr newCmplExpr(Value op1, Value op2);
  
  /**
     Constructs a CmpgExpr(Arg, Arg) grammar chunk.
  */
  
  public CmpgExpr newCmpgExpr(Value op1, Value op2);
  
  /**
     Constructs a CmpExpr(Arg, Arg) grammar chunk.
  */
  
  public CmpExpr newCmpExpr(Value op1, Value op2);
  
  /**
     Constructs a GtExpr(Arg, Arg) grammar chunk.
  */
  
  public GtExpr newGtExpr(Value op1, Value op2);
  
  /**
     Constructs a LtExpr(Arg, Arg) grammar chunk.
  */
  
  public LtExpr newLtExpr(Value op1, Value op2);
  
  /**
     Constructs a AddExpr(Arg, Arg) grammar chunk.
  */
  
  public AddExpr newAddExpr(Value op1, Value op2);
  
  /**
     Constructs a AndExpr(Arg, Arg) grammar chunk.
  */
  
  public AndExpr newAndExpr(Value op1, Value op2);
  
  /**
     Constructs a NegExpr(Arg, Arg) grammar chunk.
  */
  
  public NegExpr newNegExpr(Value op);
  
  /**
     Constructs a LengthExpr(Immediate) grammar chunk.
  */
  
  public LengthExpr newLengthExpr(Value op);
  
  /**
     Constructs a CastExpr(Immediate, Type) grammar chunk.
  */
  
  public CastExpr newCastExpr(Value op1, Type t);
  
  /**
     Constructs a InstanceOfExpr(Immediate, Type)
     grammar chunk.
  */
  
  public InstanceOfExpr newInstanceOfExpr(Value op1, Type t);

  /**
     Constructs a NewArrayExpr(Type, Immediate) grammar chunk.
  */

  public NewArrayExpr newNewArrayExpr(Type type, Value size);

    /**
        Constructs a NewMultiArrayExpr(ArrayType, List of Immediate) grammar chunk.
     */

  public NewMultiArrayExpr newNewMultiArrayExpr(ArrayType type, List sizes);

    /**
        Constructs a NewStaticInvokeExpr(ArrayType, List of Immediate) grammar chunk.
     */

  public StaticInvokeExpr newStaticInvokeExpr(SootMethod method, List args);

    /**
        Constructs a NewSpecialInvokeExpr(Local base, SootMethod method, List of Immediate) grammar chunk.
     */

    public SpecialInvokeExpr newSpecialInvokeExpr
      (Local base, SootMethod method, List args);

    /**
        Constructs a NewVirtualInvokeExpr(Local base, SootMethod method, List of Immediate) grammar chunk.
     */

    public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethod method, List args);


    /**
        Constructs a NewInterfaceInvokeExpr(Local base, SootMethod method, List of Immediate) grammar chunk.
     */

    public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethod method, List args);


    /**
        Constructs a ThrowStmt(Immediate) grammar chunk.
     */

    public ThrowStmt newThrowStmt(Value op);

    /**
        Constructs a ExitMonitorStmt(Immediate) grammar chunk
     */

    public ExitMonitorStmt newExitMonitorStmt(Value op);


    /**
        Constructs a EnterMonitorStmt(Immediate) grammar chunk.
     */

    public EnterMonitorStmt newEnterMonitorStmt(Value op);

    /**
        Constructs a BreakpointStmt() grammar chunk.
     */

    public BreakpointStmt newBreakpointStmt();

    /**
        Constructs a GotoStmt(Stmt) grammar chunk.
     */

    public GotoStmt newGotoStmt(Unit target);

    /**
        Constructs a NopStmt() grammar chunk.
     */

    public NopStmt newNopStmt();

    /**
        Constructs a ReturnVoidStmt() grammar chunk.
     */

    public ReturnVoidStmt newReturnVoidStmt();

    /**
        Constructs a ReturnStmt(Immediate) grammar chunk.
     */

    public ReturnStmt newReturnStmt(Value op);

    /**
        Constructs a RetStmt(Local) grammar chunk.
     */

    public RetStmt newRetStmt(Value stmtAddress);

    /**
        Constructs a IfStmt(Condition, Stmt) grammar chunk.
     */

    public IfStmt newIfStmt(Value condition, Unit target);

    /**
        Constructs a IdentityStmt(Local, IdentityRef) grammar chunk.
     */

    public IdentityStmt newIdentityStmt(Value local, Value identityRef);

    /**
        Constructs a AssignStmt(Variable, RValue) grammar chunk.
     */

    public AssignStmt newAssignStmt(Value variable, Value rvalue);

    /**
        Constructs a InvokeStmt(InvokeExpr) grammar chunk.
     */

    public InvokeStmt newInvokeStmt(Value op);

    /**
        Constructs a TableSwitchStmt(Immediate, int, int, List of Unit, Stmt) grammar chunk.
     */

    public TableSwitchStmt newTableSwitchStmt(Value key, int lowIndex, int highIndex, List targets, Unit defaultTarget);

    /**
        Constructs a LookupSwitchStmt(Immediate, List of Immediate, List of Unit, Stmt) grammar chunk.
     */

    public LookupSwitchStmt newLookupSwitchStmt(Value key, List lookupValues, List targets, Unit defaultTarget);

    /**
        Constructs a Local with the given name and type.
    */

    public Local newLocal(String name, Type t);

    /**
        Constructs a new Trap for the given exception on the given Stmt range with the given Stmt handler.
    */

    public Trap newTrap(SootClass exception, Unit beginStmt, Unit endStmt, Unit handlerStmt);

    /**
        Constructs a StaticFieldRef(SootField) grammar chunk.
     */

    public StaticFieldRef newStaticFieldRef(SootField f);

    /**
        Constructs a ThisRef(SootClass) grammar chunk.
     */

    public ThisRef newThisRef(SootClass c);

    /**
        Constructs a ParameterRef(SootMethod, int) grammar chunk.
     */

    public ParameterRef newParameterRef(SootMethod m, int number);

    /**
        Constructs a NextNextStmtRef() grammar chunk.
     */

    public NextNextStmtRef newNextNextStmtRef();

    /**
        Constructs a InstanceFieldRef(Value, SootField) grammar chunk.
     */

    public InstanceFieldRef newInstanceFieldRef(Value base, SootField f);

    /**
        Constructs a CaughtExceptionRef() grammar chunk.
     */

    public CaughtExceptionRef newCaughtExceptionRef(JimpleBody b);

    /**
        Constructs a ArrayRef(Local, Immediate) grammar chunk.
     */

    public ArrayRef newArrayRef(Value base, Value index);

    public ValueBox newVariableBox(Value value);

    public ValueBox newLocalBox(Value value);

    public ValueBox newRValueBox(Value value);

    public ValueBox newImmediateBox(Value value);

    public ValueBox newIdentityRefBox(Value value);

    public ValueBox newConditionExprBox(Value value);

    public ValueBox newInvokeExprBox(Value value);

    public UnitBox newStmtBox(Unit unit);
}



