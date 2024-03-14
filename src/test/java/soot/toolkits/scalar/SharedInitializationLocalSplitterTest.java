package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Timothy Hoffman
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

import java.util.Collections;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import soot.BooleanType;
import soot.G;
import soot.Local;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.UnknownType;
import soot.VoidType;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.options.Options;
import soot.util.Chain;

/**
 * Tests for the {@link SharedInitializationLocalSplitter}.
 * 
 * @author Timothy Hoffman
 */
public class SharedInitializationLocalSplitterTest {

  @Before
  public void initialize() {
    // load necessary classes
    G.reset();
    final Options opts = Options.v();
    opts.set_whole_program(true);
    final Scene sc = Scene.v();
    sc.loadNecessaryClasses();
  }

  @Ignore
  @Test
  public void testSingleInitUsedAsBoolAndInt() {
    // create test method and body
    final BooleanType tyBool = BooleanType.v();
    final SootMethod method = new SootMethod("testMethod", Collections.singletonList(tyBool), VoidType.v(), Modifier.PUBLIC);
    SootClass cl = new SootClass("TestClass", Modifier.PUBLIC);
    cl.addMethod(method);

    final Jimple jimp = Jimple.v();
    final JimpleBody body = jimp.newBody(method);
    method.setActiveBody(body);

    final Scene sc = Scene.v();
    final SootClass clPrtStrm = sc.loadClassAndSupport("java.io.PrintStream");
    final SootClass clSystem = sc.loadClassAndSupport("java.lang.System");

    // create locals
    final Chain<Local> locals = body.getLocals();
    final Local b = jimp.newLocal("b", UnknownType.v());
    locals.add(b);
    final Local s = jimp.newLocal("s", UnknownType.v());
    locals.add(s);
    final Local u = jimp.newLocal("u", UnknownType.v());
    locals.add(u);

    // create code
    final UnitPatchingChain units = body.getUnits();
    final Unit tgtRet = jimp.newReturnVoidStmt();
    final Unit tgtBranch = jimp.newNopStmt();
    // boolean b = @param0;
    units.add(jimp.newIdentityStmt(b, jimp.newParameterRef(tyBool, 0)));
    // java.io.PrintStream s = System.out;
    units.add(jimp.newAssignStmt(s, jimp.newStaticFieldRef(clSystem.getFieldByName("out").makeRef())));
    // int u = 0;
    units.add(jimp.newAssignStmt(u, IntConstant.v(0)));
    // if(b==0) goto <tgtBranch>
    units.add(jimp.newIfStmt(jimp.newEqExpr(b, IntConstant.v(0)), tgtBranch));
    // s.println<boolean>(u);
    units.add(jimp.newInvokeStmt(jimp.newVirtualInvokeExpr(s, clPrtStrm.getMethod("void println(boolean)").makeRef(), u)));
    // goto <tgtRet>
    units.add(jimp.newGotoStmt(tgtRet));
    // <tgtBranch>: nop
    units.add(tgtBranch);
    // s.println<int>(u);
    units.add(jimp.newInvokeStmt(jimp.newVirtualInvokeExpr(s, clPrtStrm.getMethod("void println(int)").makeRef(), u)));
    // <tgtRet>: return;
    units.add(tgtRet);

    // System.out.println("[testSingleInitUsedAsBoolAndInt] before = " + body);

    // execute transform
    SharedInitializationLocalSplitter.v().transform(body);
    UnusedLocalEliminator.v().transform(body);

    // System.out.println("[testSingleInitUsedAsBoolAndInt] after = " + body);
    
    // TODO: how to verify that SharedInitializationLocalSplitter did its job?
  }
}
