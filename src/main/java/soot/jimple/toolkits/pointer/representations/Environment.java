package soot.jimple.toolkits.pointer.representations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Feng Qian
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

import soot.G;
import soot.Singletons;

public class Environment {
  public Environment(Singletons.Global g) {
  }

  public static Environment v() {
    return G.v().soot_jimple_toolkits_pointer_representations_Environment();
  }

  private final ConstantObject clsloaders = new GeneralConstObject(TypeConstants.v().CLASSLOADERCLASS, "classloader");

  private final ConstantObject processes = new GeneralConstObject(TypeConstants.v().PROCESSCLASS, "process");

  private final ConstantObject threads = new GeneralConstObject(TypeConstants.v().THREADCLASS, "thread");

  private final ConstantObject filesystem = new GeneralConstObject(TypeConstants.v().FILESYSTEMCLASS, "filesystem");

  /*
   * representing all possible java.lang.Class type objects, mostly used by reflection.
   */
  private final ConstantObject classobject = new GeneralConstObject(TypeConstants.v().CLASSCLASS, "unknownclass");

  /*
   * representing all possible java.lang.String objects, used by any getName() or similiar methods.
   */
  private final ConstantObject stringobject = new GeneralConstObject(TypeConstants.v().STRINGCLASS, "unknownstring");

  /*
   * provides an abstract java.lang.reflect.Field object.
   */
  private final ConstantObject fieldobject = new GeneralConstObject(TypeConstants.v().FIELDCLASS, "field");

  /*
   * provides an abstract java.lang.reflect.Method object
   */
  private final ConstantObject methodobject = new GeneralConstObject(TypeConstants.v().METHODCLASS, "method");

  /*
   * provides an abstract java.lang.reflect.Constructor object
   */
  private final ConstantObject constructorobject = new GeneralConstObject(TypeConstants.v().CONSTRUCTORCLASS, "constructor");

  /*
   * represents the PrivilegedActionException thrown by AccessController.doPrivileged
   */
  private final ConstantObject privilegedActionException
      = new GeneralConstObject(TypeConstants.v().PRIVILEGEDACTIONEXCEPTION, "constructor");

  private final ConstantObject accessControlContext
      = new GeneralConstObject(TypeConstants.v().ACCESSCONTROLCONTEXT, "exception");
  private final ConstantObject arrayFields = new GeneralConstObject(TypeConstants.v().ARRAYFIELDS, "field");
  private final ConstantObject arrayMethods = new GeneralConstObject(TypeConstants.v().ARRAYMETHODS, "method");
  private final ConstantObject arrayConstructors = new GeneralConstObject(TypeConstants.v().ARRAYCONSTRUCTORS, "ctor");
  private final ConstantObject arrayClasses = new GeneralConstObject(TypeConstants.v().ARRAYCLASSES, "classes");

  /********************* INTERFACE to NATIVE METHODS *******************/
  public ConstantObject getClassLoaderObject() {
    return clsloaders;
  }

  public ConstantObject getProcessObject() {
    return processes;
  }

  public ConstantObject getThreadObject() {
    return threads;
  }

  public ConstantObject getClassObject() {
    return classobject;
  }

  public ConstantObject getStringObject() {
    return stringobject;
  }

  public ConstantObject getFieldObject() {
    return fieldobject;
  }

  public ConstantObject getMethodObject() {
    return methodobject;
  }

  public ConstantObject getConstructorObject() {
    return constructorobject;
  }

  public ConstantObject getFileSystemObject() {
    return filesystem;
  }

  public ConstantObject getPrivilegedActionExceptionObject() {
    return privilegedActionException;
  }

  public ConstantObject getAccessControlContext() {
    return accessControlContext;
  }

  public ConstantObject getArrayConstructor() {
    return arrayConstructors;
  }

  public AbstractObject getArrayFields() {
    return arrayFields;
  }

  public AbstractObject getArrayMethods() {
    return arrayMethods;
  }

  public AbstractObject getArrayClasses() {
    return arrayClasses;
  }
}
