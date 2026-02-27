package soot.jimple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import soot.ArrayType;
import soot.Body;
import soot.G;
import soot.Immediate;
import soot.Local;
import soot.Modifier;
import soot.PackManager;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.internal.JimpleLocal;
import soot.options.Options;
import soot.tagkit.AbstractHost;
import soot.tagkit.InnerClassTag;
import soot.tagkit.Tag;
import soot.util.Chain;

public class JimplePrinterTest {

  private static final String NORMAL_TESTCLASS_NAME = "test.annotation.return";

  @Test
  public void testPrinting() throws Throwable {
    G.reset();

    // we use names that need quotations
    SootClass clz = new SootClass(NORMAL_TESTCLASS_NAME);
    ArrayType at = ArrayType.v(clz.getType(), 2);
    SootMethod m = new SootMethod("throws", Arrays.asList(clz.getType(), at, RefType.v("java.lang.String")), at);
    clz.addMethod(m);
    SootMethod m2 = new SootMethod("throws", Arrays.asList(), VoidType.v(), Modifier.NATIVE);
    clz.addMethod(m2);

    SootField field = new SootField("return", clz.getType());
    clz.addField(field);
    Jimple j = Jimple.v();
    JimpleBody b = j.newBody(m);
    m.setActiveBody(b);
    m.addException(clz);
    b.insertIdentityStmts();
    b.getThisLocal().setName("throw");

    JimpleLocal lcl = j.newLocal("return", at);

    b.getLocals().add(lcl);

    List<Immediate> params = Arrays.asList(lcl, NullConstant.v(), NullConstant.v());
    b.getUnits().add(j.newAssignStmt(lcl, j.newInstanceFieldRef(lcl, field.makeRef())));
    b.getUnits().add(j.newAssignStmt(lcl, j.newVirtualInvokeExpr(b.getThisLocal(), m.makeRef(), params)));
    b.getUnits().add(j.newReturnVoidStmt());
    File tmpCodeDir = File.createTempFile("tmp", "jimple-code");
    tmpCodeDir.delete();
    tmpCodeDir.mkdirs();
    Options.v().set_output_dir(tmpCodeDir.getAbsolutePath());
    Options.v().set_output_format(Options.output_format_jimple);
    Options.v().set_no_writeout_body_releasing(true);
    Scene.v().addClass(clz);
    clz.setApplicationClass();
    PackManager.v().writeOutput();
    try {
      Scene.v().removeClass(clz);
      Options.v().set_process_dir(Arrays.asList(tmpCodeDir.getAbsolutePath()));
      Options.v().set_src_prec(Options.src_prec_jimple);
      Scene.v().loadNecessaryClasses();
      SootClass loadIn = Scene.v().getSootClass(NORMAL_TESTCLASS_NAME);
      assertTrue(loadIn != clz);
      compareClasses(clz, loadIn);
    } finally {
      FileUtils.deleteQuietly(tmpCodeDir);
    }

  }

  private void compareClasses(SootClass expected, SootClass check) {
    assertEquals(expected.getName(), check.getName());
    assertEquals(expected.getFieldCount(), check.getFieldCount());
    compareAnnotations(expected, check);
    for (SootField expF : expected.getFields()) {
      SootField newF = Scene.v().grabField(expF.getSignature());
      compareFields(expF, newF);
      newF = check.getField(expF.getSubSignature());
      compareFields(expF, newF);
      newF = check.getFieldByName(expF.getName());
      compareFields(expF, newF);
      compareAnnotations(expF, newF);
    }

    assertEquals(expected.getMethodCount(), check.getMethodCount());
    for (SootMethod expF : expected.getMethods()) {
      SootMethod newF = Scene.v().grabMethod(expF.getSignature());
      compareMethods(expF, newF);
      newF = check.getMethod(expF.getSubSignature());
      compareMethods(expF, newF);
      compareAnnotations(expF, newF);
    }

  }

  private void compareAnnotations(AbstractHost expected, AbstractHost check) {
    Collection<Tag> e = getAllAnnotationTags(expected);
    Collection<Tag> c = getAllAnnotationTags(check);
    assertEquals(e, c);
  }

  private Collection<Tag> getAllAnnotationTags(AbstractHost expected) {
    Collection<Tag> res = new TreeSet<>(new Comparator<Tag>() {

      @Override
      public int compare(Tag arg0, Tag arg1) {
        return arg0.toString().compareTo(arg1.toString());
      }

    });
    for (Tag i : expected.getTags()) {
      if (i instanceof InnerClassTag) {
        res.add(i);
      }
    }
    return res;
  }

  private void compareMethods(SootMethod expected, SootMethod check) {
    assertEquals(expected.getName(), check.getName());
    assertEquals(expected.getSubSignature(), check.getSubSignature());
    assertEquals(expected.getSignature(), check.getSignature());
    checkTypes(expected.getReturnType(), check.getReturnType());
    assertEquals(expected.getParameterCount(), check.getParameterCount());
    for (int i = 0; i < expected.getParameterCount(); i++) {
      checkTypes(expected.getParameterType(i), check.getParameterType(i));
    }

    assertEquals(expected.isConcrete(), check.isConcrete());
    if (expected.isConcrete()) {
      compareBodies(expected.retrieveActiveBody(), check.retrieveActiveBody());
    }
  }

  private void compareBodies(Body expected, Body check) {
    assertEquals(expected.getLocalCount(), check.getLocalCount());
    Iterator<Local> itCheck = sort(check.getLocals()).iterator();
    for (Local e : sort(expected.getLocals())) {
      Local c = itCheck.next();
      compareLocals(e, c);
    }
    assertEquals(expected.getUnits().size(), check.getUnits().size());
    Iterator<Unit> itUCheck = check.getUnits().iterator();
    for (Unit e : expected.getUnits()) {
      compareUnits(e, itUCheck.next());
    }
  }

  private List<Local> sort(Chain<Local> locals) {
    List<Local> res = new ArrayList<>(locals);
    res.sort(new Comparator<Local>() {

      @Override
      public int compare(Local arg0, Local arg1) {
        return arg0.toString().compareTo(arg1.toString());
      }
    });
    return res;
  }

  private void compareUnits(Unit expected, Unit check) {
    assertEquals(expected.toString(), check.toString());
  }

  private void compareLocals(Local expected, Local check) {
    assertEquals(expected.getName(), check.getName());
    checkTypes(expected.getType(), check.getType());

  }

  private void checkTypes(Type expected, Type check) {
    assertEquals(expected.toString(), check.toString());
    assertEquals(expected.getClass(), check.getClass());
  }

  private void compareFields(SootField expected, SootField check) {
    assertEquals(expected.getName(), check.getName());
    assertEquals(expected.getSubSignature(), check.getSubSignature());
    assertEquals(expected.getSignature(), check.getSignature());
    checkTypes(expected.getType(), check.getType());
  }
}
