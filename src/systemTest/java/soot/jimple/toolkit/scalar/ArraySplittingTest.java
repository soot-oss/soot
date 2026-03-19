package soot.jimple.toolkit.scalar;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.Test;

import soot.ArrayType;
import soot.CharConstant;
import soot.CharType;
import soot.G;
import soot.IntType;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.VoidType;
import soot.jimple.Constant;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.toolkits.typing.TypeAssigner;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;
import soot.toolkits.exceptions.PedanticThrowAnalysis;
import soot.toolkits.scalar.DifferentArrayTypeSplitter;

public class ArraySplittingTest extends AbstractTestingFramework {

  @Test
  public void testArrayTypeSplitter() throws Exception {
    G.reset();
    Scene.v().loadNecessaryClasses();
    DifferentArrayTypeSplitter ts = new DifferentArrayTypeSplitter(PedanticThrowAnalysis.v());
    Jimple j = Jimple.v();
    SootClass decl = new SootClass("TestClass");
    SootMethod m2 = new SootMethod("x", Arrays.asList(RefType.v("java.lang.Object")), VoidType.v(), Modifier.STATIC);
    decl.addMethod(m2);
    SootMethod m = new SootMethod("test", Arrays.asList(IntType.v()), VoidType.v(), Modifier.STATIC);
    decl.addMethod(m);
    JimpleBody jb = j.newBody(m);
    jb.insertIdentityStmts();
    Local l = j.newLocal("tmpArray", UnknownType.v());
    jb.getLocals().add(l);
    IfStmt ifS = j.newIfStmt(j.newEqExpr(jb.getParameterLocal(0), IntConstant.v(1)), (Unit) null);
    jb.getUnits().add(ifS);

    addArrayInit(jb, l, IntType.v(), IntConstant.v(1));
    GotoStmt gotoS = j.newGotoStmt((Unit) null);
    jb.getUnits().add(gotoS);
    addArrayInit(jb, l, CharType.v(), CharConstant.v('x'));
    ifS.setTarget(jb.getUnits().getSuccOf(gotoS));

    Unit invoke = j.newInvokeStmt(
        j.newStaticInvokeExpr(Scene.v().makeMethodRef(decl, "void x(java.lang.Object)", true), Arrays.asList(l)));
    jb.getUnits().add(invoke);
    gotoS.setTarget(invoke);
    jb.getUnits().add(j.newReturnVoidStmt());
    ts.transform(jb);
    TypeAssigner.v().transform(jb);
    Options.v().set_validate(true);
    jb.validate();
    boolean foundIntArray = false, foundCharArray = false;
    for (Local local : jb.getLocals()) {
      if (local.getType() instanceof ArrayType) {
        ArrayType at = (ArrayType) local.getType();
        if (at.getBaseType() instanceof IntType) {
          foundIntArray = true;
        } else if (at.getBaseType() instanceof CharType) {
          foundCharArray = true;
        }
      }
    }

    assertTrue(foundCharArray);
    assertTrue(foundIntArray);
  }

  private Local addArrayInit(JimpleBody jb, Local l, Type v, Constant c) {
    Jimple j = Jimple.v();
    jb.getUnits().add(j.newAssignStmt(l, j.newNewArrayExpr(v, IntConstant.v(1))));
    jb.getUnits().add(j.newAssignStmt(j.newArrayRef(l, IntConstant.v(0)), c));
    return l;
  }
}