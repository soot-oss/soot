package soot.jimple.toolkits.pointer.representations;

import soot.*;
import soot.jimple.*;

public class TypeConstants {
    public TypeConstants( Singletons.Global g ) {}
    public static TypeConstants v() { return G.v().TypeConstants(); }
  
  public Type OBJECTCLASS = 
    RefType.v("java.lang.Object");

  public Type STRINGCLASS =
    RefType.v("java.lang.String");

  public Type CLASSLOADERCLASS =
    AnySubType.v( RefType.v("java.lang.ClassLoader") );
  
  public Type PROCESSCLASS =
    AnySubType.v( RefType.v("java.lang.Process") );

  public Type THREADCLASS =
    AnySubType.v( RefType.v( "java.lang.Thread"));

  public Type CLASSCLASS =
    RefType.v("java.lang.Class");

  public Type LEASTCLASS =
    AnySubType.v( RefType.v( "java.lang.Object" ) );
  
  public Type FIELDCLASS = 
    RefType.v("java.lang.reflect.Field");

  public Type METHODCLASS =
    RefType.v("java.lang.reflect.Method");
  
  public Type CONSTRUCTORCLASS =
    RefType.v("java.lang.reflect.Constructor");

  public Type FILESYSTEMCLASS =
    AnySubType.v( RefType.v("java.io.FileSystem") );
}
