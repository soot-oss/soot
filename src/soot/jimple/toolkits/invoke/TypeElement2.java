/* Soot - a J*va Optimization Framework
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


/**
 * A customized RefType as type element used by VTA2, 
 * Really it is a hack to RefType. 
 *
 * @author Feng Qian
 */

package soot.jimple.toolkits.invoke;

import java.util.*;
import soot.util.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

import soot.jimple.toolkits.pointer.representations.*;

public class TypeElement2 implements AbstractObject {

  /* pooling all seen classes */
  private String className;

  private TypeElement2(String cname){
    this.className = cname;
  }

  public static TypeElement2 v(String className) {
    TypeElement2 type = (TypeElement2)G.v().TypeElement2_nameToType.get(className);
    if (type == null) {
      type = new TypeElement2(className);
      G.v().TypeElement2_nameToType.put(className, type);
    }
    
    return type;
  }
  
  public static TypeElement2 v(RefType reftype){
    return v(reftype.getClassName());
  }
 
  public static TypeElement2 v(SootClass cls){
    return v(cls.getName());
  }

  /* overriding hashCode and equals */
  public int hashCode() {
    return G.v().TypeElement2_id;
  }

  public boolean equals(Object other){
    return this == other;
  }

  public RefType getRefType(){
    return RefType.v(className);
  }

  /******** Implementation of AbstractObject ******************/
  public Type getType() {
      return getRefType();
  }

  public String toString() {
    return className;
  }

  public String shortString() {
    return className;
  }
}

