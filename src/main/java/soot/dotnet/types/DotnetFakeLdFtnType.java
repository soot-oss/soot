package soot.dotnet.types;

import java.util.ArrayList;
import java.util.List;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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
import soot.Local;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.SootResolver;
import soot.Type;
import soot.Value;
import soot.javaToJimple.IInitialResolver;
import soot.jimple.Jimple;

/**
 * Helper to generate the Fake.LdFtn Sootclass and the given method
 */
public class DotnetFakeLdFtnType {

  // Define static fake method
  private static final String FAKE_LDFTN_METHOD_NAME = "FakeLdFtn";

  /**
   * If LdFtn instruction, rewrite and resolve fake Soot class
   *
   * @param sootClass
   * @return
   */
  public static IInitialResolver.Dependencies resolve(SootClass sootClass) {
    IInitialResolver.Dependencies deps = new IInitialResolver.Dependencies();
    SootClass superClass = SootResolver.v().makeClassRef(DotnetBasicTypes.SYSTEM_OBJECT);
    deps.typesToHierarchy.add(superClass.getType());
    sootClass.setSuperclass(superClass);
    int classModifier = 0;
    classModifier |= Modifier.PUBLIC;
    classModifier |= Modifier.STATIC;
    sootClass.setModifiers(classModifier);

    // add fake method
    int modifier = 0;
    modifier |= Modifier.PUBLIC;
    modifier |= Modifier.STATIC;
    modifier |= Modifier.NATIVE;

    SootMethod m = Scene.v().makeSootMethod(FAKE_LDFTN_METHOD_NAME, new ArrayList<>(),
        DotnetTypeFactory.toSootType(DotnetBasicTypes.SYSTEM_INTPTR), modifier);
    sootClass.addMethod(m);

    return deps;
  }

  /**
   * Make fake method for CIL instruction LdFtn, which returns an IntPtr Workaround for difference in CIL and Java bytecode
   *
   * @return
   */
  public static Value makeMethod() {
    SootClass clazz = Scene.v().getSootClass(DotnetBasicTypes.FAKE_LDFTN);

    // arguments which are passed to this function
    List<Local> argsVariables = new ArrayList<>();
    // method-parameters (signature)
    List<Type> methodParams = new ArrayList<>();

    SootMethodRef methodRef = Scene.v().makeMethodRef(clazz, FAKE_LDFTN_METHOD_NAME, methodParams,
        DotnetTypeFactory.toSootType(DotnetBasicTypes.SYSTEM_INTPTR), true);
    return Jimple.v().newStaticInvokeExpr(methodRef, argsVariables);
  }
}
