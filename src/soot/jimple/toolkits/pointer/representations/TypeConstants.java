package soot.jimple.toolkits.pointer.representations;

import soot.*;
import soot.jimple.*;

public interface TypeConstants {
  
  public SootClass OBJECTCLASS = 
    Scene.v().getSootClass("java.lang.Object");

  public SootClass STRINGCLASS =
    Scene.v().getSootClass("java.lang.String");

  public SootClass CLASSLOADERCLASS =
    Scene.v().getSootClass("java.lang.ClassLoader");
  
  public SootClass PROCESSCLASS =
    Scene.v().getSootClass("java.lang.Process");

  public SootClass THREADCLASS =
    Scene.v().getSootClass("java.lang.Thread");

  public SootClass CLASSCLASS =
    Scene.v().getSootClass("java.lang.Class");

  /*** TODO ***/
  /* It must be the least class in hierarchy.
   */
  public SootClass LEASTCLASS =
    Scene.v().getSootClass("java.lang.Object");
  
  public SootClass FIELDCLASS = 
    Scene.v().getSootClass("java.lang.reflect.Field");

  public SootClass METHODCLASS =
    Scene.v().getSootClass("java.lang.reflect.Method");
  
  public SootClass CONSTRUCTORCLASS =
    Scene.v().getSootClass("java.lang.reflect.Constructor");

  public SootClass FILESYSTEMCLASS =
    Scene.v().getSootClass("java.io.FileSystem");
}
