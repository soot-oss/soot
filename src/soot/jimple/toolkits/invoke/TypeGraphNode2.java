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


/* The type graph node identifies a node by name (string), but it has
 * a unique id for hashing, and quick check of equals
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

public class TypeGraphNode2 implements ReferenceVariable {

  /* Static structure for pooling the nodes 
   * Make this pool accessible from NewVTATypeGraph
   */
  protected static HashMap nameToNode = new HashMap(10000);
  protected static int counter = 0;

  private String name;
  private int id;
  private TypeSet2 reachingTypes;

  /* disallow direct instantiation */
  private TypeGraphNode2(String name, TypeSet2 types){
    this.name = name;
    this.id   = counter++;
    this.reachingTypes = types;
  }

  /** The only method to create a new node.
   *  If there is a node exists for the name, return the node
   *  otherwise, create a new node and returns it.
   */
  public static TypeGraphNode2 v(String name) {
    TypeGraphNode2 node = (TypeGraphNode2)nameToNode.get(name);
    if (node == null) {
      node = new TypeGraphNode2(name, 
			       new TypeSet2());
      nameToNode.put(name, node);
    }
    return node;
  }

  /** Check if the node for the name exists
   */
  public boolean exists(String name){
    return nameToNode.containsKey(name);
  }

  /* Return the node representing the name. 
   * returns null if the node does not exists.
   */
  public TypeGraphNode2 getNode(String name){
    return (TypeGraphNode2)nameToNode.get(name);
  }

  /* get reaching types. */
  public TypeSet2 getTypeSet2(){
    return this.reachingTypes;
  }

  /* return the name. */
  public String getName() {
    return this.name;
  }

  /** Returns hash code, it is a quick impl
   */
  public int hashCode() {
    return this.id;
  }

  /** Check equality of two nodes.
   *  Since the node is unique, just check equality of two object address.
   */
  public boolean equals(Object other){
    return this == other;
  }

  public String toString(){
    return "TypeGraphNode2@"+name;
  }

  /* methods for create temporary graph node */
  private static int tmpcount=0;

  public static TypeGraphNode2 makeTempNode() {
    return v("tmp"+tmpcount++);
  }
}
