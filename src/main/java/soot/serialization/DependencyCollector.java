package soot.serialization;

import java.util.Collection;
import java.util.Collections;

import soot.Local;
import soot.RefType;
import soot.Type;
import soot.jimple.AbstractJimpleValueSwitch;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.MethodHandle;
import soot.jimple.MethodType;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;

/**
 * @author Manuel Benz at 2019-09-04
 */
public class DependencyCollector extends AbstractJimpleValueSwitch {

  private Collection<RefType> result;

  @Override
  public Collection<RefType> getResult() {
    return result;
  }

  @Override
  public void caseArrayRef(ArrayRef v) {
    super.caseArrayRef(v);
  }

  @Override
  public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
    super.caseInterfaceInvokeExpr(v);
  }

  @Override
  public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
    super.caseSpecialInvokeExpr(v);
  }

  @Override
  public void caseStaticInvokeExpr(StaticInvokeExpr v) {
    result = Collections.singleton(v.getMethodRef().getDeclaringClass().getType());
  }

  @Override
  public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
    super.caseVirtualInvokeExpr(v);
  }

  @Override
  public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
    super.caseDynamicInvokeExpr(v);
  }

  @Override
  public void caseCastExpr(CastExpr v) {
    super.caseCastExpr(v);
  }

  @Override
  public void caseInstanceOfExpr(InstanceOfExpr v) {
    super.caseInstanceOfExpr(v);
  }

  @Override
  public void caseNewArrayExpr(NewArrayExpr v) {
    super.caseNewArrayExpr(v);
  }

  @Override
  public void caseNewMultiArrayExpr(NewMultiArrayExpr v) {
    super.caseNewMultiArrayExpr(v);
  }

  @Override
  public void caseNewExpr(NewExpr v) {
    super.caseNewExpr(v);
  }

  @Override
  public void caseInstanceFieldRef(InstanceFieldRef v) {
    super.caseInstanceFieldRef(v);
  }

  @Override
  public void caseCaughtExceptionRef(CaughtExceptionRef v) {
    super.caseCaughtExceptionRef(v);
  }

  @Override
  public void caseStaticFieldRef(StaticFieldRef v) {
    super.caseStaticFieldRef(v);
  }

  @Override
  public void caseClassConstant(ClassConstant v) {
    super.caseClassConstant(v);
  }

  @Override
  public void caseMethodHandle(MethodHandle v) {
    super.caseMethodHandle(v);
  }

  @Override
  public void caseMethodType(MethodType v) {
    super.caseMethodType(v);
  }

  @Override
  public void caseLocal(Local v) {
    Type type = v.getType();
    if (type instanceof RefType) {
      result = Collections.singleton((RefType) type);
    } else {
      defaultCase(v);
    }
  }

  @Override
  public void defaultCase(Object v) {
    result = Collections.EMPTY_SET;
  }
}
