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
import java.util.*;

public class Environment implements TypeConstants{

  private static ConstantObject clsloaders =
    new GeneralConstObject(CLASSLOADERCLASS, "classloader");
  
  private static ConstantObject processes  =
    new GeneralConstObject(PROCESSCLASS, "process");

  private static ConstantObject threads    =
    new GeneralConstObject(THREADCLASS, "thread");

  private static ConstantObject filesystem =
    new GeneralConstObject(FILESYSTEMCLASS, "filesystem");

  /* representing all possible java.lang.Class type objects,
   * mostly used by reflection.
   */
  private static ConstantObject classobject =
    new GeneralConstObject(CLASSCLASS, "unknownclass");

  /* representing all possible java.lang.String objects, used by
   * any getName() or similiar methods.
   */
  private static ConstantObject stringobject =
    new GeneralConstObject(STRINGCLASS, "unknownstring");

  /* to get finer resolution, it is worth to distinguish arrays and general
   * scalars.
   * WARNING: making array with java.lang.Object type may be a problem!
   */
  private static ConstantObject leastarray =
    new GeneralConstObject(LEASTCLASS, "leastarray");

  /* makes a general unknown object,
   * WARNING: unknown object must have the least type, it won't be
   *          useful when resolve virtual calls.
   *          Null type is a good candidate for this.
   */
  private static ConstantObject leastobject =
    new GeneralConstObject(LEASTCLASS, "leastobject");

  /* provides an abstract java.lang.reflect.Field object.
   */
  private static ConstantObject fieldobject =
    new GeneralConstObject(FIELDCLASS, "field");

  /* provides an abstract java.lang.reflect.Method object
   */
  private static ConstantObject methodobject =
    new GeneralConstObject(METHODCLASS, "method");
  
  /* provides an abstract java.lang.reflect.Constructor object
   */
  private static ConstantObject constructorobject =
    new GeneralConstObject(CONSTRUCTORCLASS, "constructor");

  /********************* INTERFACE to NATIVE METHODS *******************/
  public static ConstantObject getClassLoaderObject(){
    return clsloaders;
  }

  public static ConstantObject getProcessObject(){
    return processes;
  }

  public static ConstantObject getThreadObject(){
    return threads;
  }

  public static ConstantObject getClassObject(){
    return classobject;
  }

  public static ConstantObject getStringObject(){
    return stringobject;
  }

  public static ConstantObject getLeastArrayObject(){
    return leastarray;
  }
  
  public static ConstantObject getLeastObject(){
    return leastobject;
  }

  public static ConstantObject getFieldObject(){
    return fieldobject;
  }

  public static ConstantObject getMethodObject(){
    return methodobject;
  }

  public static ConstantObject getConstructorObject(){
    return constructorobject;
  }

  public static ConstantObject getFileSystemObject(){
    return filesystem;
  }
}
