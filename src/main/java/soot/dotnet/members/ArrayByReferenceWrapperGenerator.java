package soot.dotnet.members;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;

import soot.IntType;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.dotnet.types.DotNetBasicTypes;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

/**
 * Array elements in .NET can be loaded by reference. 
 * Since Jimple does not support this semantic, we generate Wrapper classes.
 * 
 * Note that in .NET, array covariance is allowed for reference types:
 * <code>
   string[] strings = new string[10];
   object[] objects = strings; 
   </code>
 * However, for value types, this is not the case.
 * So all the reference types may share one object[] wrapper, but value types need their own.
 * @author Marc Miltenberger
 */
public class ArrayByReferenceWrapperGenerator {
  private static final String SET_ELEMENT = "setElement";
  private static final String GET_ELEMENT = "getElement";
  public static final String WRAPPER_CLASS_NAME_START = "ArrayElementReferenceWrappers.Wrapper";

  public synchronized static SootClass getWrapperClass(Type elementType) {
    Scene scene = Scene.v();
    String name = WRAPPER_CLASS_NAME_START;
    String suffix = "";
    if (DotNetBasicTypes.isValueType(elementType)) {
      name += elementType.toString().replace(".", "_");
    } else {
      name += "Object";
      elementType = RefType.v(DotNetBasicTypes.SYSTEM_OBJECT);
    }
    name += suffix;
    RefType rt = RefType.v(name);
    if (rt.hasSootClass()) {
      return rt.getSootClass();
    }
    Type arrType = elementType.makeArrayType();

    SootClass sc = scene.makeSootClass(name, Modifier.FINAL | Modifier.STATIC);
    sc.setApplicationClass();
    SootField arrayField = scene.makeSootField("array", arrType);
    arrayField.setModifiers(Modifier.PUBLIC | Modifier.FINAL);
    sc.addField(arrayField);
    SootField elemField = scene.makeSootField("elem", IntType.v());
    elemField.setModifiers(Modifier.PUBLIC | Modifier.FINAL);
    sc.addField(elemField);

    SootMethod ctor = scene.makeSootMethod("<init>", Arrays.asList(arrType, IntType.v()), VoidType.v());
    Jimple j = Jimple.v();
    JimpleBody b = j.newBody(ctor);
    ctor.setActiveBody(b);
    sc.addMethod(ctor);
    b.insertIdentityStmts();
    b.getUnits().add(j.newAssignStmt(j.newInstanceFieldRef(b.getThisLocal(), arrayField.makeRef()), b.getParameterLocal(0)));
    b.getUnits().add(j.newAssignStmt(j.newInstanceFieldRef(b.getThisLocal(), elemField.makeRef()), b.getParameterLocal(1)));
    b.getUnits().add(j.newReturnVoidStmt());

    SootMethod get = scene.makeSootMethod(GET_ELEMENT, Collections.emptyList(), elementType);
    b = j.newBody(get);
    get.setActiveBody(b);
    sc.addMethod(get);
    b.insertIdentityStmts();
    Local arr = j.newLocal("arr", arrType);
    b.getLocals().add(arr);
    Local elemIdx = j.newLocal("elemIdx", IntType.v());
    b.getLocals().add(elemIdx);
    Local elem = j.newLocal("elem", elementType);
    b.getLocals().add(elem);

    b.getUnits().add(j.newAssignStmt(arr, j.newInstanceFieldRef(b.getThisLocal(), arrayField.makeRef())));
    b.getUnits().add(j.newAssignStmt(elemIdx, j.newInstanceFieldRef(b.getThisLocal(), elemField.makeRef())));
    b.getUnits().add(j.newAssignStmt(elem, j.newArrayRef(arr, elemIdx)));

    b.getUnits().add(j.newReturnStmt(elem));

    SootMethod set = scene.makeSootMethod(SET_ELEMENT, Collections.singletonList(elementType), VoidType.v());
    b = j.newBody(set);
    set.setActiveBody(b);
    sc.addMethod(set);
    b.insertIdentityStmts();
    arr = j.newLocal("arr", arrType);
    b.getLocals().add(arr);
    elem = j.newLocal("elem", elementType);
    b.getLocals().add(elem);
    b.getUnits().add(j.newAssignStmt(arr, j.newInstanceFieldRef(b.getThisLocal(), arrayField.makeRef())));
    b.getUnits().add(j.newAssignStmt(elem, j.newInstanceFieldRef(b.getThisLocal(), elemField.makeRef())));
    b.getUnits().add(j.newAssignStmt(j.newArrayRef(arr, elem), b.getParameterLocal(0)));
    b.getUnits().add(j.newReturnVoidStmt());
    return sc;
  }

  public static Value createGet(Value holder) {
    RefType rt = (RefType) holder.getType();
    return Jimple.v().newSpecialInvokeExpr((Local) holder, rt.getSootClass().getMethodByName(GET_ELEMENT).makeRef());
  }

  public static Unit createSet(Value holder, Value newValue) {
    RefType rt = (RefType) holder.getType();
    Jimple j = Jimple.v();
    return j.newInvokeStmt(
        j.newSpecialInvokeExpr((Local) holder, rt.getSootClass().getMethodByName(SET_ELEMENT).makeRef(), newValue));
  }
}
