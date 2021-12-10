package soot.javaToJimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import java.util.Iterator;

import soot.LocalGenerator;
import soot.Scene;

public class PrivateFieldAccMethodSource implements soot.MethodSource {

  private final soot.Type fieldType;
  private final String fieldName;
  private final boolean isStatic;
  private final soot.SootClass classToInvoke;

  public PrivateFieldAccMethodSource(soot.Type fieldType, String fieldName, boolean isStatic, soot.SootClass classToInvoke) {
    this.fieldType = fieldType;
    this.fieldName = fieldName;
    this.isStatic = isStatic;
    this.classToInvoke = classToInvoke;
  }

  public soot.Body getBody(soot.SootMethod sootMethod, String phaseName) {

    soot.Body body = soot.jimple.Jimple.v().newBody(sootMethod);
    LocalGenerator lg = Scene.v().createLocalGenerator(body);

    soot.Local fieldBase = null;
    // create parameters
    Iterator paramIt = sootMethod.getParameterTypes().iterator();
    while (paramIt.hasNext()) {
      soot.Type sootType = (soot.Type) paramIt.next();
      soot.Local paramLocal = lg.generateLocal(sootType);

      soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(sootType, 0);
      soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(paramLocal, paramRef);
      body.getUnits().add(stmt);
      fieldBase = paramLocal;
    }

    // create field type local
    soot.Local fieldLocal = lg.generateLocal(fieldType);
    // assign local to fieldRef
    soot.SootFieldRef field = soot.Scene.v().makeFieldRef(classToInvoke, fieldName, fieldType, isStatic);

    soot.jimple.FieldRef fieldRef = null;
    if (isStatic) {
      fieldRef = soot.jimple.Jimple.v().newStaticFieldRef(field);
    } else {
      fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(fieldBase, field);
    }
    soot.jimple.AssignStmt assign = soot.jimple.Jimple.v().newAssignStmt(fieldLocal, fieldRef);
    body.getUnits().add(assign);

    // return local
    soot.jimple.Stmt retStmt = soot.jimple.Jimple.v().newReturnStmt(fieldLocal);
    body.getUnits().add(retStmt);

    return body;

  }
}
