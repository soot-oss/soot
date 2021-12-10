package soot.sootify;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.HashSet;
import java.util.Set;

import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EqExpr;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleValueSwitch;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LtExpr;
import soot.jimple.MethodHandle;
import soot.jimple.MethodType;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.ThisRef;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;

public class ValueTemplatePrinter implements JimpleValueSwitch {

  private final TemplatePrinter p;
  private final TypeTemplatePrinter ttp;
  private String varName;

  private final Set<String> varnamesAlreadyUsed = new HashSet<String>();

  public ValueTemplatePrinter(TemplatePrinter p) {
    this.p = p;
    this.ttp = new TypeTemplatePrinter(p);

    this.varnamesAlreadyUsed.add("b");// body
    this.varnamesAlreadyUsed.add("m");// method
    this.varnamesAlreadyUsed.add("units");// unit chain
  }

  public String printValueAssignment(Value value, String varName) {
    suggestVariableName(varName);
    value.apply(this);
    return getLastAssignedVarName();
  }

  private void printConstant(Value v, String... ops) {
    String stmtClassName = v.getClass().getSimpleName();
    p.print("Value " + varName + " = ");
    p.printNoIndent(stmtClassName);
    p.printNoIndent(".v(");
    int i = 1;
    for (String op : ops) {
      p.printNoIndent(op);
      if (i < ops.length) {
        p.printNoIndent(",");
      }
      i++;
    }
    p.printNoIndent(")");
    p.printlnNoIndent(";");
  }

  private void printExpr(Value v, String... ops) {
    String stmtClassName = v.getClass().getSimpleName();
    if (stmtClassName.charAt(0) == 'J') {
      stmtClassName = stmtClassName.substring(1);
    }
    p.print("Value " + varName + " = ");
    printFactoryMethodCall(stmtClassName, ops);
    p.printlnNoIndent(";");
  }

  private void printFactoryMethodCall(String stmtClassName, String... ops) {
    p.printNoIndent("Jimple.v().new");
    p.printNoIndent(stmtClassName);
    p.printNoIndent("(");
    int i = 1;
    for (String op : ops) {
      p.printNoIndent(op);
      if (i < ops.length) {
        p.printNoIndent(",");
      }
      i++;
    }
    p.printNoIndent(")");
  }

  public void suggestVariableName(String name) {
    String actualName = name;
    int i = 0;
    do {
      actualName = name + i;
      i++;
    } while (varnamesAlreadyUsed.contains(actualName));
    this.varName = actualName;
    this.varnamesAlreadyUsed.add(actualName);
  }

  public String getLastAssignedVarName() {
    return varName;
  }

  @Override
  public void caseDoubleConstant(DoubleConstant v) {
    printConstant(v, Double.toString(v.value));
  }

  @Override
  public void caseFloatConstant(FloatConstant v) {
    printConstant(v, Float.toString(v.value));
  }

  @Override
  public void caseIntConstant(IntConstant v) {
    printConstant(v, Integer.toString(v.value));
  }

  @Override
  public void caseLongConstant(LongConstant v) {
    printConstant(v, Long.toString(v.value));
  }

  @Override
  public void caseNullConstant(NullConstant v) {
    printConstant(v);
  }

  @Override
  public void caseStringConstant(StringConstant v) {
    printConstant(v, "\"" + v.value + "\"");
  }

  @Override
  public void caseClassConstant(ClassConstant v) {
    printConstant(v, "\"" + v.value + "\"");
  }

  @Override
  public void caseAddExpr(AddExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseMethodHandle(MethodHandle handle) {
    throw new UnsupportedOperationException("we have not yet determined how to print Java 7 method handles");
  }

  @Override
  public void caseMethodType(MethodType type) {
    throw new UnsupportedOperationException("we have not yet determined how to print Java 8 method handles");
  }

  private void printBinaryExpr(BinopExpr v) {
    String className = v.getClass().getSimpleName();
    if (className.charAt(0) == 'J') {
      className = className.substring(1);
    }

    String oldName = varName;

    String v1 = printValueAssignment(v.getOp1(), "left");
    String v2 = printValueAssignment(v.getOp2(), "right");

    p.println("Value " + oldName + " = Jimple.v().new" + className + "(" + v1 + "," + v2 + ");");

    varName = oldName;
  }

  @Override
  public void caseAndExpr(AndExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseCmpExpr(CmpExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseCmpgExpr(CmpgExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseCmplExpr(CmplExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseDivExpr(DivExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseEqExpr(EqExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseNeExpr(NeExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseGeExpr(GeExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseGtExpr(GtExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseLeExpr(LeExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseLtExpr(LtExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseMulExpr(MulExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseOrExpr(OrExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseRemExpr(RemExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseShlExpr(ShlExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseShrExpr(ShrExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseUshrExpr(UshrExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseSubExpr(SubExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseXorExpr(XorExpr v) {
    printBinaryExpr(v);
  }

  @Override
  public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
    printInvokeExpr(v);
  }

  private void printInvokeExpr(InvokeExpr v) {
    p.openBlock();

    String oldName = varName;
    SootMethodRef method = v.getMethodRef();
    SootMethod m = method.resolve();

    if (!m.isStatic()) {
      Local base = (Local) ((InstanceInvokeExpr) v).getBase();
      p.println("Local base = localByName(b,\"" + base.getName() + "\");");
    }

    p.println("List<Type> parameterTypes = new LinkedList<Type>();");
    int i = 0;
    for (Type t : m.getParameterTypes()) {
      ttp.setVariableName("type" + i);
      t.apply(ttp);
      p.println("parameterTypes.add(type" + i + ");");
      i++;
    }

    ttp.setVariableName("returnType");
    m.getReturnType().apply(ttp);

    p.print("SootMethodRef methodRef = ");
    p.printNoIndent("Scene.v().makeMethodRef(");
    String className = m.getDeclaringClass().getName();
    p.printNoIndent("Scene.v().getSootClass(\"" + className + "\"),");
    p.printNoIndent("\"" + m.getName() + "\",");
    p.printNoIndent("parameterTypes,");
    p.printNoIndent("returnType,");
    p.printlnNoIndent(m.isStatic() + ");");

    printExpr(v, "base", "methodRef");

    varName = oldName;

    p.closeBlock();
  }

  @Override
  public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
    printInvokeExpr(v);
  }

  @Override
  public void caseStaticInvokeExpr(StaticInvokeExpr v) {
    printInvokeExpr(v);
  }

  @Override
  public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
    printInvokeExpr(v);
  }

  @Override
  public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
    printInvokeExpr(v);
  }

  // private void printBinaryExpr(BinopExpr v, Value lhs, String l, Value rhs, String r) {
  // String className = v.getClass().getSimpleName();
  // if(className.charAt(0)=='J') className = className.substring(1);
  //
  // String oldName = varName;
  //
  // String v1 = printValueAssignment(lhs, l);
  //
  // String v2 = printValueAssignment(rhs, r);
  //
  // p.println("Value "+oldName+" = Jimple.v().new"+className+"("+v1+","+v2+");");
  //
  // varName = oldName;
  // }

  @Override
  public void caseCastExpr(CastExpr v) {
    String oldName = varName;

    suggestVariableName("type");
    String lhsName = varName;
    ttp.setVariableName(varName);
    v.getType().apply(ttp);

    String rhsName = printValueAssignment(v.getOp(), "op");

    p.println("Value " + oldName + " = Jimple.v().newCastExpr(" + lhsName + "," + rhsName + ");");

    varName = oldName;
  }

  @Override
  public void caseInstanceOfExpr(InstanceOfExpr v) {
    String oldName = varName;

    suggestVariableName("type");
    String lhsName = varName;
    ttp.setVariableName(varName);
    v.getType().apply(ttp);

    String rhsName = printValueAssignment(v.getOp(), "op");

    p.println("Value " + oldName + " = Jimple.v().newInstanceOfExpr(" + lhsName + "," + rhsName + ");");

    varName = oldName;
  }

  @Override
  public void caseNewArrayExpr(NewArrayExpr v) {
    String oldName = varName;

    Value size = v.getSize();
    suggestVariableName("size");
    String sizeName = varName;
    size.apply(this);

    suggestVariableName("type");
    String lhsName = varName;
    ttp.setVariableName(varName);
    v.getType().apply(ttp);

    p.println("Value " + oldName + " = Jimple.v().newNewArrayExpr(" + lhsName + ", " + sizeName + ");");
    varName = oldName;
  }

  @Override
  public void caseNewMultiArrayExpr(NewMultiArrayExpr v) {
    p.openBlock();
    String oldName = varName;

    ttp.setVariableName("arrayType");
    v.getType().apply(ttp);

    p.println("List<IntConstant> sizes = new LinkedList<IntConstant>();");
    int i = 0;
    for (Value s : v.getSizes()) {
      this.suggestVariableName("size" + i);
      s.apply(this);
      i++;

      p.println("sizes.add(sizes" + i + ");");
    }

    p.println("Value " + oldName + " = Jimple.v().newNewMultiArrayExpr(arrayType, sizes);");
    varName = oldName;
    p.closeBlock();
  }

  @Override
  public void caseNewExpr(NewExpr v) {
    String oldName = varName;

    suggestVariableName("type");
    String typeName = varName;
    ttp.setVariableName(varName);
    v.getType().apply(ttp);

    p.println("Value " + oldName + " = Jimple.v().newNewExpr(" + typeName + ");");
    varName = oldName;
  }

  @Override
  public void caseLengthExpr(LengthExpr v) {
    String oldName = varName;

    Value op = v.getOp();
    suggestVariableName("op");
    String opName = varName;
    op.apply(this);

    p.println("Value " + oldName + " = Jimple.v().newLengthExpr(" + opName + ");");
    varName = oldName;
  }

  @Override
  public void caseNegExpr(NegExpr v) {
    String oldName = varName;

    Value op = v.getOp();
    suggestVariableName("op");
    String opName = varName;
    op.apply(this);

    p.println("Value " + oldName + " = Jimple.v().newNegExpr(" + opName + ");");
    varName = oldName;
  }

  @Override
  public void caseArrayRef(ArrayRef v) {
    String oldName = varName;

    Value base = v.getBase();
    suggestVariableName("base");
    String baseName = varName;
    base.apply(this);

    Value index = v.getIndex();
    suggestVariableName("index");
    String indexName = varName;
    index.apply(this);

    p.println("Value " + oldName + " = Jimple.v().newArrayRef(" + baseName + ", " + indexName + ");");
    varName = oldName;
  }

  @Override
  public void caseStaticFieldRef(StaticFieldRef v) {
    printFieldRef(v);
  }

  private void printFieldRef(FieldRef v) {
    String refTypeName = v.getClass().getSimpleName();

    p.openBlock();
    String oldName = varName;

    SootField f = v.getField();
    ttp.setVariableName("type");
    f.getType().apply(ttp);
    p.print("SootFieldRef fieldRef = ");
    p.printNoIndent("Scene.v().makeFieldRef(");
    String className = f.getDeclaringClass().getName();
    p.printNoIndent("Scene.v().getSootClass(\"" + className + "\"),");
    p.printNoIndent("\"" + f.getName() + "\",");
    p.printNoIndent("type,");
    p.printNoIndent(f.isStatic() + ");");

    p.println("Value " + oldName + " = Jimple.v().new" + refTypeName + "(fieldRef);");
    varName = oldName;
    p.closeBlock();
  }

  @Override
  public void caseInstanceFieldRef(InstanceFieldRef v) {
    printFieldRef(v);
  }

  @Override
  public void caseParameterRef(ParameterRef v) {
    String oldName = varName;

    Type paramType = v.getType();
    suggestVariableName("paramType");
    String paramTypeName = this.varName;
    ttp.setVariableName(paramTypeName);
    paramType.apply(ttp);

    suggestVariableName("number");
    p.println("int " + varName + "=" + v.getIndex() + ";");

    p.println("Value " + oldName + " = Jimple.v().newParameterRef(" + paramTypeName + ", " + varName + ");");
    varName = oldName;
  }

  @Override
  public void caseCaughtExceptionRef(CaughtExceptionRef v) {
    p.println("Value " + varName + " = Jimple.v().newCaughtExceptionRef();");
  }

  @Override
  public void caseThisRef(ThisRef v) {

    String oldName = varName;

    suggestVariableName("type");
    String typeName = this.varName;
    ttp.setVariableName(typeName);
    v.getType().apply(ttp);

    p.println("Value " + oldName + " = Jimple.v().newThisRef(" + typeName + ");");
    varName = oldName;

  }

  @Override
  public void caseLocal(Local l) {
    String oldName = varName;

    p.println("Local " + varName + " = localByName(b,\"" + l.getName() + "\");");

    varName = oldName;
  }

  @Override
  public void defaultCase(Object object) {
    throw new InternalError();
  }
}
