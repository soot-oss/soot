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
import soot.options.*;

public class TypeConstants {
    public static TypeConstants v() { return G.v().soot_jimple_toolkits_pointer_representations_TypeConstants(); }
  
  public Type OBJECTCLASS; 
  public Type STRINGCLASS;
  public Type CLASSLOADERCLASS;
  public Type PROCESSCLASS;
  public Type THREADCLASS;
  public Type CLASSCLASS;
  public Type LEASTCLASS;
  public Type FIELDCLASS; 
  public Type METHODCLASS;
  public Type CONSTRUCTORCLASS;
  public Type FILESYSTEMCLASS;
  public Type PRIVILEGEDACTIONEXCEPTION;

    public TypeConstants( Singletons.Global g ) {
        int jdkver = 
            new CGOptions(PhaseOptions.v().getPhaseOptions("cg")).jdkver();

        OBJECTCLASS = 
        RefType.v("java.lang.Object");

        STRINGCLASS =
        RefType.v("java.lang.String");

        CLASSLOADERCLASS =
        AnySubType.v( RefType.v("java.lang.ClassLoader") );

        PROCESSCLASS =
        AnySubType.v( RefType.v("java.lang.Process") );

        THREADCLASS =
        AnySubType.v( RefType.v( "java.lang.Thread"));

        CLASSCLASS =
        RefType.v("java.lang.Class");

        LEASTCLASS =
        AnySubType.v( RefType.v( "java.lang.Object" ) );

        FIELDCLASS = 
        RefType.v("java.lang.reflect.Field");

        METHODCLASS =
        RefType.v("java.lang.reflect.Method");

        CONSTRUCTORCLASS =
        RefType.v("java.lang.reflect.Constructor");

        if(jdkver >= 2) {
            FILESYSTEMCLASS =
            AnySubType.v( RefType.v("java.io.FileSystem") );
        }

        if(jdkver >= 2) {
            PRIVILEGEDACTIONEXCEPTION =
            AnySubType.v( RefType.v("java.security.PrivilegedActionException") );
        }
    }
}
