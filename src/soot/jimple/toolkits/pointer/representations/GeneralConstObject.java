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
 * A general constant object represents one of following environment objects:
 *    ClassLoader, Process, Thread, ...
 * Such environment constants are distinguished by name and managed by
 * Environment.
 *
 * @author Feng Qian
 */

package soot.jimple.toolkits.pointer.representations;

import soot.*;

public class GeneralConstObject extends ConstantObject {


  /* what's the soot class */
  private Type      type;
  private String    name;
  private int       id;

  public GeneralConstObject(Type t, String n){
    this.type = t;
    this.name = n;
    this.id   = G.v().GeneralConstObject_counter++;
  }
  
  public Type getType() {
    return type;
  }
  
  public String toString() {
    return name;
  }

  public int hashCode(){
    return this.id;
  }

  public boolean equals(Object other){
    return this == other;
  }
}
