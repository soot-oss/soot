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

package soot.jimple.toolkits.pointer.representations;

import soot.*;
import soot.jimple.*;

public class TypeConstants {
    public TypeConstants( Singletons.Global g ) {}
    public static TypeConstants v() { return G.v().soot_jimple_toolkits_pointer_representations_TypeConstants(); }
  
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
