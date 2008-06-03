/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Feng Qian
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/**
 * Environment simulates the VM environment such as objects representing
 * classes, methods, fields, and so on. There is only one:
 *
 *  java.lang.ClassLoader object,
 *  java.lang.Class       object,
 *  java.lang.Field       object,
 *  java.lang.Method      object,
 *  java.lang.Constructor object,
 *  java.lang.Process     object,
 *  java.lang.Thread      object,
 *  java.io.FileSystem    object
 *
 * String objects are special, since there are other string constants.
 * An unknown object has the least type, same as array.
 * 
 * This class defines all abstract object as constants.
 * Temporary variables can be obtained from NativeHelper. 
 *
 * @author Feng Qian
 */

package soot.jimple.toolkits.pointer.representations;

import soot.*;

public class Environment {
    public Environment( Singletons.Global g ) {}
    public static Environment v() { return G.v().soot_jimple_toolkits_pointer_representations_Environment(); }

  private final ConstantObject clsloaders =
    new GeneralConstObject(TypeConstants.v().CLASSLOADERCLASS, "classloader");
  
  private final ConstantObject processes  =
    new GeneralConstObject(TypeConstants.v().PROCESSCLASS, "process");

  private final ConstantObject threads    =
    new GeneralConstObject(TypeConstants.v().THREADCLASS, "thread");

  private final ConstantObject filesystem =
    new GeneralConstObject(TypeConstants.v().FILESYSTEMCLASS, "filesystem");

  /* representing all possible java.lang.Class type objects,
   * mostly used by reflection.
   */
  private final ConstantObject classobject =
    new GeneralConstObject(TypeConstants.v().CLASSCLASS, "unknownclass");

  /* representing all possible java.lang.String objects, used by
   * any getName() or similiar methods.
   */
  private final ConstantObject stringobject =
    new GeneralConstObject(TypeConstants.v().STRINGCLASS, "unknownstring");

  /* to get finer resolution, it is worth to distinguish arrays and general
   * scalars.
   * WARNING: making array with java.lang.Object type may be a problem!
   */
  private final ConstantObject leastarray =
    new GeneralConstObject(TypeConstants.v().LEASTCLASS, "leastarray");

  /* makes a general unknown object,
   * WARNING: unknown object must have the least type, it won't be
   *          useful when resolve virtual calls.
   *          Null type is a good candidate for this.
   */
  private final ConstantObject leastobject =
    new GeneralConstObject(TypeConstants.v().LEASTCLASS, "leastobject");

  /* provides an abstract java.lang.reflect.Field object.
   */
  private final ConstantObject fieldobject =
    new GeneralConstObject(TypeConstants.v().FIELDCLASS, "field");

  /* provides an abstract java.lang.reflect.Method object
   */
  private final ConstantObject methodobject =
    new GeneralConstObject(TypeConstants.v().METHODCLASS, "method");
  
  /* provides an abstract java.lang.reflect.Constructor object
   */
  private final ConstantObject constructorobject =
    new GeneralConstObject(TypeConstants.v().CONSTRUCTORCLASS, "constructor");

  /* represents the PrivilegedActionException thrown by
   * AccessController.doPrivileged
   */
  private final ConstantObject privilegedActionException =
    new GeneralConstObject(TypeConstants.v().PRIVILEGEDACTIONEXCEPTION, "constructor");

  /********************* INTERFACE to NATIVE METHODS *******************/
  public ConstantObject getClassLoaderObject(){
    return clsloaders;
  }

  public ConstantObject getProcessObject(){
    return processes;
  }

  public ConstantObject getThreadObject(){
    return threads;
  }

  public ConstantObject getClassObject(){
    return classobject;
  }

  public ConstantObject getStringObject(){
    return stringobject;
  }

  public ConstantObject getLeastArrayObject(){
    return leastarray;
  }
  
  public ConstantObject getLeastObject(){
    return leastobject;
  }

  public ConstantObject getFieldObject(){
    return fieldobject;
  }

  public ConstantObject getMethodObject(){
    return methodobject;
  }

  public ConstantObject getConstructorObject(){
    return constructorobject;
  }

  public ConstantObject getFileSystemObject(){
    return filesystem;
  }

  public ConstantObject getPrivilegedActionExceptionObject(){
    return privilegedActionException;
  }
}
