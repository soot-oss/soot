package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Ben Sepanski, Marc Miltenberger
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

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import soot.G;
import soot.IntType;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.VoidType;

// Test case is based on https://github.com/soot-oss/soot/pull/1742
public class InsertOnEdgeTest {

  @Test
  public void testInsertOnEdgeDeadCode() throws Throwable {
    G.reset();
    Jimple j = Jimple.v();
    SootMethod sm = new SootMethod("Test", Arrays.asList(IntType.v(), RefType.v("java.lang.Exception")), VoidType.v());
    JimpleBody body = j.newBody(sm);
    SootClass tmp = new SootClass("Tmp");
    tmp.getOrAddMethod(sm);
    body.insertIdentityStmts();
    ReturnVoidStmt returnS = j.newReturnVoidStmt();
    IfStmt ifStmt = j.newIfStmt(j.newEqExpr(body.getParameterLocal(0), IntConstant.v(1)), returnS);
    body.getUnits().add(ifStmt);
    body.getUnits().add(j.newThrowStmt(body.getParameterLocal(1)));
    body.getUnits().add(returnS);
    NopStmt nopinsert = j.newNopStmt();

    body.getUnits().insertOnEdge(nopinsert, ifStmt, returnS);
    /*
    void Test(int, java.lang.Exception)
    {
        int parameter0;
        Tmp this;
        java.lang.Exception parameter1;
    
        this := @this: Tmp;
    
        parameter0 := @parameter0: int;
    
        parameter1 := @parameter1: java.lang.Exception;
    
        if parameter0 == 1 goto label1;
    
        throw parameter1;
    
        goto label2; <-- this is the dead code that would be inserted if Soot is not working correctly
    
     label1:
        nop; 
    
     label2:
        return;
    }
    
     */
    for (Unit u : body.getUnits()) {
      if (u instanceof GotoStmt) {
        if (!body.getUnits().getPredOf(u).fallsThrough()) {
          fail("Unnecessary goto due to insertOnEdge");
        }
      }
    }
  }
}
