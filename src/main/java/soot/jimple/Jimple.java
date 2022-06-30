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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import soot.ArrayType;
import soot.ErroneousType;
import soot.G;
import soot.Immediate;
import soot.Local;
import soot.RefType;
import soot.Singletons;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.StmtAddressType;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.ConditionExprBox;
import soot.jimple.internal.IdentityRefBox;
import soot.jimple.internal.ImmediateBox;
import soot.jimple.internal.InvokeExprBox;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JAndExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JBreakpointStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JCaughtExceptionRef;
import soot.jimple.internal.JCmpExpr;
import soot.jimple.internal.JCmpgExpr;
import soot.jimple.internal.JCmplExpr;
import soot.jimple.internal.JDivExpr;
import soot.jimple.internal.JDynamicInvokeExpr;
import soot.jimple.internal.JEnterMonitorStmt;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JExitMonitorStmt;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JInterfaceInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLengthExpr;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JMulExpr;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JNegExpr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import soot.jimple.internal.JNopStmt;
import soot.jimple.internal.JOrExpr;
import soot.jimple.internal.JRemExpr;
import soot.jimple.internal.JRetStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JShlExpr;
import soot.jimple.internal.JShrExpr;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JSubExpr;
import soot.jimple.internal.JTableSwitchStmt;
import soot.jimple.internal.JThrowStmt;
import soot.jimple.internal.JTrap;
import soot.jimple.internal.JUshrExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JXorExpr;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.internal.JimpleLocalBox;
import soot.jimple.internal.RValueBox;
import soot.jimple.internal.StmtBox;
import soot.jimple.internal.VariableBox;

/**
 * The Jimple class contains all the constructors for the components of the Jimple grammar for the Jimple body. <br>
 * <br>
 *
 * Immediate -> Local | Constant <br>
 * RValue -> Local | Constant | ConcreteRef | Expr<br>
 * Variable -> Local | ArrayRef | InstanceFieldRef | StaticFieldRef <br>
 */

public class Jimple {
  public Jimple(Singletons.Global g) {
  }

  public static Jimple v() {
    return G.v().soot_jimple_Jimple();
  }

  public final static String NEWARRAY = "newarray";
  public final static String NEWMULTIARRAY = "newmultiarray";
  public final static String NOP = "nop";
  public final static String RET = "ret";
  public final static String SPECIALINVOKE = "specialinvoke";
  public final static String DYNAMICINVOKE = "dynamicinvoke";
  public final static String STATICINVOKE = "staticinvoke";
  public final static String TABLESWITCH = "tableswitch";
  public final static String VIRTUALINVOKE = "virtualinvoke";
  public final static String NULL_TYPE = "null_type";
  public final static String UNKNOWN = "unknown";
  public final static String CMP = "cmp";
  public final static String CMPG = "cmpg";
  public final static String CMPL = "cmpl";
  public final static String ENTERMONITOR = "entermonitor";
  public final static String EXITMONITOR = "exitmonitor";
  public final static String INTERFACEINVOKE = "interfaceinvoke";
  public final static String LENGTHOF = "lengthof";
  public final static String LOOKUPSWITCH = "lookupswitch";
  public final static String NEG = "neg";
  public final static String IF = "if";
  public final static String ABSTRACT = "abstract";
  public final static String BOOLEAN = "boolean";
  public final static String BREAK = "break";
  public final static String BYTE = "byte";
  public final static String CASE = "case";
  public final static String CATCH = "catch";
  public final static String CHAR = "char";
  public final static String CLASS = "class";
  public final static String FINAL = "final";
  public final static String NATIVE = "native";
  public final static String PUBLIC = "public";
  public final static String PROTECTED = "protected";
  public final static String PRIVATE = "private";
  public final static String STATIC = "static";
  public final static String SYNCHRONIZED = "synchronized";
  public final static String TRANSIENT = "transient";
  public final static String VOLATILE = "volatile";
  public final static String STRICTFP = "strictfp";
  public final static String ENUM = "enum";
  public final static String ANNOTATION = "annotation";
  public final static String INTERFACE = "interface";
  public final static String VOID = "void";
  public final static String SHORT = "short";
  public final static String INT = "int";
  public final static String LONG = "long";
  public final static String FLOAT = "float";
  public final static String DOUBLE = "double";
  public final static String EXTENDS = "extends";
  public final static String IMPLEMENTS = "implements";
  public final static String BREAKPOINT = "breakpoint";
  public final static String DEFAULT = "default";
  public final static String GOTO = "goto";
  public final static String INSTANCEOF = "instanceof";
  public final static String NEW = "new";
  public final static String RETURN = "return";
  public final static String THROW = "throw";
  public final static String THROWS = "throws";
  public final static String NULL = "null";
  public final static String FROM = "from";
  public final static String TO = "to";
  public final static String WITH = "with";
  public final static String CLS = "cls";
  public final static String TRUE = "true";
  public final static String FALSE = "false";

  public static List<String> jimpleKeywordList() {
    List<String> l = new LinkedList<String>();
    Collections.addAll(l, NEWARRAY, NEWMULTIARRAY, NOP, RET, SPECIALINVOKE, STATICINVOKE, TABLESWITCH, VIRTUALINVOKE,
        NULL_TYPE, UNKNOWN, CMP, CMPG, CMPL, ENTERMONITOR, EXITMONITOR, INTERFACEINVOKE, LENGTHOF, LOOKUPSWITCH, NEG, IF,
        ABSTRACT, BOOLEAN, BREAK, BYTE, CASE, CATCH, CHAR, CLASS, FINAL, NATIVE, PUBLIC, PROTECTED, PRIVATE, STATIC,
        SYNCHRONIZED, TRANSIENT, VOLATILE, STRICTFP, ENUM, ANNOTATION, INTERFACE, VOID, SHORT, INT, LONG, FLOAT, DOUBLE,
        EXTENDS, IMPLEMENTS, BREAKPOINT, DEFAULT, GOTO, INSTANCEOF, NEW, RETURN, THROW, THROWS, NULL, FROM, TO, WITH, CLS,
        TRUE, FALSE);
    return l;
  }

  public static boolean isJavaKeywordType(Type t) {
    return !(t instanceof StmtAddressType || t instanceof UnknownType || t instanceof RefType
        || (t instanceof ArrayType && (!isJavaKeywordType(((ArrayType) t).baseType))) || t instanceof ErroneousType);
  }

  public static Value cloneIfNecessary(Value val) {
    if (val instanceof Immediate) {
      return val;
    } else {
      return (Value) val.clone();
    }
  }

  /**
   * Constructs a XorExpr(Immediate, Immediate) grammar chunk.
   */
  public XorExpr newXorExpr(Value op1, Value op2) {
    return new JXorExpr(op1, op2);
  }

  /**
   * Constructs a UshrExpr(Immediate, Immediate) grammar chunk.
   */
  public UshrExpr newUshrExpr(Value op1, Value op2) {
    return new JUshrExpr(op1, op2);
  }

  /**
   * Constructs a SubExpr(Immediate, Immediate) grammar chunk.
   */
  public SubExpr newSubExpr(Value op1, Value op2) {
    return new JSubExpr(op1, op2);
  }

  /**
   * Constructs a ShrExpr(Immediate, Immediate) grammar chunk.
   */
  public ShrExpr newShrExpr(Value op1, Value op2) {
    return new JShrExpr(op1, op2);
  }

  /**
   * Constructs a ShlExpr(Immediate, Immediate) grammar chunk.
   */
  public ShlExpr newShlExpr(Value op1, Value op2) {
    return new JShlExpr(op1, op2);
  }

  /**
   * Constructs a RemExpr(Immediate, Immediate) grammar chunk.
   */
  public RemExpr newRemExpr(Value op1, Value op2) {
    return new JRemExpr(op1, op2);
  }

  /**
   * Constructs a OrExpr(Immediate, Immediate) grammar chunk.
   */
  public OrExpr newOrExpr(Value op1, Value op2) {
    return new JOrExpr(op1, op2);
  }

  /**
   * Constructs a NeExpr(Immediate, Immediate) grammar chunk.
   */
  public NeExpr newNeExpr(Value op1, Value op2) {
    return new JNeExpr(op1, op2);
  }

  /**
   * Constructs a MulExpr(Immediate, Immediate) grammar chunk.
   */
  public MulExpr newMulExpr(Value op1, Value op2) {
    return new JMulExpr(op1, op2);
  }

  /**
   * Constructs a LeExpr(Immediate, Immediate) grammar chunk.
   */
  public LeExpr newLeExpr(Value op1, Value op2) {
    return new JLeExpr(op1, op2);
  }

  /**
   * Constructs a GeExpr(Immediate, Immediate) grammar chunk.
   */
  public GeExpr newGeExpr(Value op1, Value op2) {
    return new JGeExpr(op1, op2);
  }

  /**
   * Constructs a EqExpr(Immediate, Immediate) grammar chunk.
   */
  public EqExpr newEqExpr(Value op1, Value op2) {
    return new JEqExpr(op1, op2);
  }

  /**
   * Constructs a DivExpr(Immediate, Immediate) grammar chunk.
   */
  public DivExpr newDivExpr(Value op1, Value op2) {
    return new JDivExpr(op1, op2);
  }

  /**
   * Constructs a CmplExpr(Immediate, Immediate) grammar chunk.
   */
  public CmplExpr newCmplExpr(Value op1, Value op2) {
    return new JCmplExpr(op1, op2);
  }

  /**
   * Constructs a CmpgExpr(Immediate, Immediate) grammar chunk.
   */
  public CmpgExpr newCmpgExpr(Value op1, Value op2) {
    return new JCmpgExpr(op1, op2);
  }

  /**
   * Constructs a CmpExpr(Immediate, Immediate) grammar chunk.
   */
  public CmpExpr newCmpExpr(Value op1, Value op2) {
    return new JCmpExpr(op1, op2);
  }

  /**
   * Constructs a GtExpr(Immediate, Immediate) grammar chunk.
   */
  public GtExpr newGtExpr(Value op1, Value op2) {
    return new JGtExpr(op1, op2);
  }

  /**
   * Constructs a LtExpr(Immediate, Immediate) grammar chunk.
   */
  public LtExpr newLtExpr(Value op1, Value op2) {
    return new JLtExpr(op1, op2);
  }

  /**
   * Constructs a AddExpr(Immediate, Immediate) grammar chunk.
   */
  public AddExpr newAddExpr(Value op1, Value op2) {
    return new JAddExpr(op1, op2);
  }

  /**
   * Constructs a AndExpr(Immediate, Immediate) grammar chunk.
   */
  public AndExpr newAndExpr(Value op1, Value op2) {
    return new JAndExpr(op1, op2);
  }

  /**
   * Constructs a NegExpr(Immediate, Immediate) grammar chunk.
   */
  public NegExpr newNegExpr(Value op) {
    return new JNegExpr(op);
  }

  /**
   * Constructs a LengthExpr(Immediate) grammar chunk.
   */
  public LengthExpr newLengthExpr(Value op) {
    return new JLengthExpr(op);
  }

  /**
   * Constructs a CastExpr(Immediate, Type) grammar chunk.
   */
  public CastExpr newCastExpr(Value op1, Type t) {
    return new JCastExpr(op1, t);
  }

  /**
   * Constructs a InstanceOfExpr(Immediate, Type) grammar chunk.
   */
  public InstanceOfExpr newInstanceOfExpr(Value op1, Type t) {
    return new JInstanceOfExpr(op1, t);
  }

  /**
   * Constructs a NewExpr(RefType) grammar chunk.
   */
  public NewExpr newNewExpr(RefType type) {
    return new JNewExpr(type);
  }

  /**
   * Constructs a NewArrayExpr(Type, Immediate) grammar chunk.
   */
  public NewArrayExpr newNewArrayExpr(Type type, Value size) {
    return new JNewArrayExpr(type, size);
  }

  /**
   * Constructs a NewMultiArrayExpr(ArrayType, List of Immediate) grammar chunk.
   */
  public NewMultiArrayExpr newNewMultiArrayExpr(ArrayType type, List<? extends Value> sizes) {
    return new JNewMultiArrayExpr(type, sizes);
  }

  /**
   * Constructs a NewStaticInvokeExpr(ArrayType, List of Immediate) grammar chunk.
   */
  public StaticInvokeExpr newStaticInvokeExpr(SootMethodRef method, List<? extends Value> args) {
    return new JStaticInvokeExpr(method, args);
  }

  public StaticInvokeExpr newStaticInvokeExpr(SootMethodRef method, Value... args) {
    return newStaticInvokeExpr(method, Arrays.asList(args));
  }

  public StaticInvokeExpr newStaticInvokeExpr(SootMethodRef method, Value arg) {
    return newStaticInvokeExpr(method, Collections.singletonList(arg));
  }

  public StaticInvokeExpr newStaticInvokeExpr(SootMethodRef method) {
    return newStaticInvokeExpr(method, Collections.<Value>emptyList());
  }

  /**
   * Constructs a NewSpecialInvokeExpr(Local base, SootMethodRef method, List of Immediate) grammar chunk.
   */
  public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethodRef method, List<? extends Value> args) {
    return new JSpecialInvokeExpr(base, method, args);
  }

  /**
   * Constructs a NewSpecialInvokeExpr(Local base, SootMethodRef method, List of Immediate) grammar chunk.
   */
  public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethodRef method, Value... args) {
    return newSpecialInvokeExpr(base, method, Arrays.asList(args));
  }

  public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethodRef method, Value arg) {
    return newSpecialInvokeExpr(base, method, Collections.<Value>singletonList(arg));
  }

  public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethodRef method) {
    return newSpecialInvokeExpr(base, method, Collections.<Value>emptyList());
  }

  /**
   * Constructs a NewDynamicInvokeExpr(SootMethodRef bootstrapMethodRef, List bootstrapArgs, SootMethodRef methodRef, List
   * args) grammar chunk.
   */
  public DynamicInvokeExpr newDynamicInvokeExpr(SootMethodRef bootstrapMethodRef, List<? extends Value> bootstrapArgs,
      SootMethodRef methodRef, List<? extends Value> args) {
    return new JDynamicInvokeExpr(bootstrapMethodRef, bootstrapArgs, methodRef, args);
  }

  /**
   * Constructs a NewDynamicInvokeExpr(SootMethodRef bootstrapMethodRef, List bootstrapArgs, SootMethodRef methodRef, List
   * args) grammar chunk.
   */
  public DynamicInvokeExpr newDynamicInvokeExpr(SootMethodRef bootstrapMethodRef, List<? extends Value> bootstrapArgs,
      SootMethodRef methodRef, int tag, List<? extends Value> args) {
    return new JDynamicInvokeExpr(bootstrapMethodRef, bootstrapArgs, methodRef, tag, args);
  }

  /**
   * Constructs a NewVirtualInvokeExpr(Local base, SootMethodRef method, List of Immediate) grammar chunk.
   */
  public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethodRef method, List<? extends Value> args) {
    return new JVirtualInvokeExpr(base, method, args);
  }

  /**
   * Constructs a NewVirtualInvokeExpr(Local base, SootMethodRef method, List of Immediate) grammar chunk.
   */
  public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethodRef method, Value... args) {
    return newVirtualInvokeExpr(base, method, Arrays.asList(args));
  }

  public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethodRef method, Value arg) {
    return newVirtualInvokeExpr(base, method, Collections.<Value>singletonList(arg));
  }

  public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethodRef method) {
    return newVirtualInvokeExpr(base, method, Collections.<Value>emptyList());
  }

  /**
   * Constructs a NewInterfaceInvokeExpr(Local base, SootMethodRef method, List of Immediate) grammar chunk.
   */
  public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethodRef method, List<? extends Value> args) {
    return new JInterfaceInvokeExpr(base, method, args);
  }

  /**
   * Constructs a NewInterfaceInvokeExpr(Local base, SootMethodRef method, List of Immediate) grammar chunk.
   */
  public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethodRef method, Value... args) {
    return newInterfaceInvokeExpr(base, method, Arrays.asList(args));
  }

  public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethodRef method, Value arg) {
    return newInterfaceInvokeExpr(base, method, Collections.<Value>singletonList(arg));
  }

  public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethodRef method) {
    return newInterfaceInvokeExpr(base, method, Collections.<Value>emptyList());
  }

  /**
   * Constructs a ThrowStmt(Immediate) grammar chunk.
   */
  public ThrowStmt newThrowStmt(Value op) {
    return new JThrowStmt(op);
  }

  /**
   * Constructs a ExitMonitorStmt(Immediate) grammar chunk
   */
  public ExitMonitorStmt newExitMonitorStmt(Value op) {
    return new JExitMonitorStmt(op);
  }

  /**
   * Constructs a EnterMonitorStmt(Immediate) grammar chunk.
   */
  public EnterMonitorStmt newEnterMonitorStmt(Value op) {
    return new JEnterMonitorStmt(op);
  }

  /**
   * Constructs a BreakpointStmt() grammar chunk.
   */
  public BreakpointStmt newBreakpointStmt() {
    return new JBreakpointStmt();
  }

  /**
   * Constructs a GotoStmt(Stmt) grammar chunk.
   */
  public GotoStmt newGotoStmt(Unit target) {
    return new JGotoStmt(target);
  }

  public GotoStmt newGotoStmt(UnitBox stmtBox) {
    return new JGotoStmt(stmtBox);
  }

  /**
   * Constructs a NopStmt() grammar chunk.
   */
  public NopStmt newNopStmt() {
    return new JNopStmt();
  }

  /**
   * Constructs a ReturnVoidStmt() grammar chunk.
   */
  public ReturnVoidStmt newReturnVoidStmt() {
    return new JReturnVoidStmt();
  }

  /**
   * Constructs a ReturnStmt(Immediate) grammar chunk.
   */
  public ReturnStmt newReturnStmt(Value op) {
    return new JReturnStmt(op);
  }

  /**
   * Constructs a RetStmt(Local) grammar chunk.
   */
  public RetStmt newRetStmt(Value stmtAddress) {
    return new JRetStmt(stmtAddress);
  }

  /**
   * Constructs a IfStmt(Condition, Stmt) grammar chunk.
   */
  public IfStmt newIfStmt(Value condition, Unit target) {
    return new JIfStmt(condition, target);
  }

  /**
   * Constructs a IfStmt(Condition, UnitBox) grammar chunk.
   */
  public IfStmt newIfStmt(Value condition, UnitBox target) {
    return new JIfStmt(condition, target);
  }

  /**
   * Constructs a IdentityStmt(Local, IdentityRef) grammar chunk.
   */
  public IdentityStmt newIdentityStmt(Value local, Value identityRef) {
    return new JIdentityStmt(local, identityRef);
  }

  /**
   * Constructs a AssignStmt(Variable, RValue) grammar chunk.
   */
  public AssignStmt newAssignStmt(Value variable, Value rvalue) {
    return new JAssignStmt(variable, rvalue);
  }

  /**
   * Constructs a InvokeStmt(InvokeExpr) grammar chunk.
   */
  public InvokeStmt newInvokeStmt(Value op) {
    return new JInvokeStmt(op);
  }

  /**
   * Constructs a TableSwitchStmt(Immediate, int, int, List of Unit, Stmt) grammar chunk.
   */
  public TableSwitchStmt newTableSwitchStmt(Value key, int lowIndex, int highIndex, List<? extends Unit> targets,
      Unit defaultTarget) {
    return new JTableSwitchStmt(key, lowIndex, highIndex, targets, defaultTarget);
  }

  public TableSwitchStmt newTableSwitchStmt(Value key, int lowIndex, int highIndex, List<? extends UnitBox> targets,
      UnitBox defaultTarget) {
    return new JTableSwitchStmt(key, lowIndex, highIndex, targets, defaultTarget);
  }

  /**
   * Constructs a LookupSwitchStmt(Immediate, List of Immediate, List of Unit, Stmt) grammar chunk.
   */
  public LookupSwitchStmt newLookupSwitchStmt(Value key, List<IntConstant> lookupValues, List<? extends Unit> targets,
      Unit defaultTarget) {
    return new JLookupSwitchStmt(key, lookupValues, targets, defaultTarget);
  }

  public LookupSwitchStmt newLookupSwitchStmt(Value key, List<IntConstant> lookupValues, List<? extends UnitBox> targets,
      UnitBox defaultTarget) {
    return new JLookupSwitchStmt(key, lookupValues, targets, defaultTarget);
  }

  /**
   * Constructs a Local with the given name and type.
   */
  public Local newLocal(String name, Type t) {
    return new JimpleLocal(name, t);
  }

  /**
   * Constructs a new JTrap for the given exception on the given Stmt range with the given Stmt handler.
   */
  public Trap newTrap(SootClass exception, Unit beginStmt, Unit endStmt, Unit handlerStmt) {
    return new JTrap(exception, beginStmt, endStmt, handlerStmt);
  }

  public Trap newTrap(SootClass exception, UnitBox beginStmt, UnitBox endStmt, UnitBox handlerStmt) {
    return new JTrap(exception, beginStmt, endStmt, handlerStmt);
  }

  /**
   * Constructs a StaticFieldRef(SootFieldRef) grammar chunk.
   */
  public StaticFieldRef newStaticFieldRef(SootFieldRef f) {
    return new StaticFieldRef(f);
  }

  /**
   * Constructs a ThisRef(RefType) grammar chunk.
   */
  public ThisRef newThisRef(RefType t) {
    return new ThisRef(t);
  }

  /**
   * Constructs a ParameterRef(SootMethod, int) grammar chunk.
   */
  public ParameterRef newParameterRef(Type paramType, int number) {
    return new ParameterRef(paramType, number);
  }

  /**
   * Constructs a InstanceFieldRef(Local, SootFieldRef) grammar chunk.
   */
  public InstanceFieldRef newInstanceFieldRef(Value base, SootFieldRef f) {
    return new JInstanceFieldRef(base, f);
  }

  /**
   * Constructs a CaughtExceptionRef() grammar chunk.
   */
  public CaughtExceptionRef newCaughtExceptionRef() {
    return new JCaughtExceptionRef();
  }

  /**
   * Constructs a ArrayRef(Local, Immediate) grammar chunk.
   */
  public ArrayRef newArrayRef(Value base, Value index) {
    return new JArrayRef(base, index);
  }

  // Note: This is NOT used to create the variable box in JAssignStmt.
  public ValueBox newVariableBox(Value value) {
    return new VariableBox(value);
  }

  public ValueBox newLocalBox(Value value) {
    return new JimpleLocalBox(value);
  }

  // Note: This is NOT used to create the rvalue box in JAssignStmt.
  public ValueBox newRValueBox(Value value) {
    return new RValueBox(value);
  }

  public ValueBox newImmediateBox(Value value) {
    return new ImmediateBox(value);
  }

  public ValueBox newArgBox(Value value) {
    return new ImmediateBox(value);
  }

  public ValueBox newIdentityRefBox(Value value) {
    return new IdentityRefBox(value);
  }

  public ValueBox newConditionExprBox(Value value) {
    return new ConditionExprBox(value);
  }

  public ValueBox newInvokeExprBox(Value value) {
    return new InvokeExprBox(value);
  }

  public UnitBox newStmtBox(Unit unit) {
    return new StmtBox((Stmt) unit);
  }

  /** Returns an empty JimpleBody associated with method m. */
  public JimpleBody newBody(SootMethod m) {
    return new JimpleBody(m);
  }

  /** Returns an empty JimpleBody with no associated method. */
  public JimpleBody newBody() {
    return new JimpleBody();
  }

  /*
   * Uncomment these stubs to make it compile with old code using Soot that does not know about SootField/MethodRefs.
   */
  /*
   * public StaticFieldRef newStaticFieldRef(SootField f) { return newStaticFieldRef(f.makeRef()); }
   *
   * public InstanceFieldRef newInstanceFieldRef(Value base, SootField f) { return newInstanceFieldRef(base, f.makeRef()); }
   *
   * public StaticInvokeExpr newStaticInvokeExpr(SootMethod method, List args) { return newStaticInvokeExpr(method.makeRef(),
   * args); }
   *
   * public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethod method, List args) { return
   * newSpecialInvokeExpr(base, method.makeRef(), args); }
   *
   * public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethod method, List args) { return
   * newVirtualInvokeExpr(base, method.makeRef(), args); }
   *
   * public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethod method, List args) { return
   * newInterfaceInvokeExpr(base, method.makeRef(), args); }
   *
   * public StaticInvokeExpr newStaticInvokeExpr(SootMethod method) { return newStaticInvokeExpr(method.makeRef()); }
   *
   * public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethod method) { return newSpecialInvokeExpr(base,
   * method.makeRef()); }
   *
   * public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethod method) { return newVirtualInvokeExpr(base,
   * method.makeRef()); }
   *
   * public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethod method) { return newInterfaceInvokeExpr(base,
   * method.makeRef()); }
   *
   * public StaticInvokeExpr newStaticInvokeExpr(SootMethod method, Value arg) { return newStaticInvokeExpr(method.makeRef(),
   * arg); }
   *
   * public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethod method, Value arg) { return
   * newSpecialInvokeExpr(base, method.makeRef(), arg); }
   *
   * public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethod method, Value arg) { return
   * newVirtualInvokeExpr(base, method.makeRef(), arg); }
   *
   * public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethod method, Value arg) { return
   * newInterfaceInvokeExpr(base, method.makeRef(), arg); }
   *
   * public StaticInvokeExpr newStaticInvokeExpr(SootMethod method, Value arg1, Value arg2) { return
   * newStaticInvokeExpr(method.makeRef(), arg1, arg2); }
   *
   * public SpecialInvokeExpr newSpecialInvokeExpr(Local base, SootMethod method, Value arg1, Value arg2) { return
   * newSpecialInvokeExpr(base, method.makeRef(), arg1, arg2); }
   *
   * public VirtualInvokeExpr newVirtualInvokeExpr(Local base, SootMethod method, Value arg1, Value arg2) { return
   * newVirtualInvokeExpr(base, method.makeRef(), arg1, arg2); }
   *
   * public InterfaceInvokeExpr newInterfaceInvokeExpr(Local base, SootMethod method, Value arg1, Value arg2) { return
   * newInterfaceInvokeExpr(base, method.makeRef(), arg1, arg2); }
   */
}
