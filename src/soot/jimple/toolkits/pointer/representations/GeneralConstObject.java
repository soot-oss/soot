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

  private static int counter = 0;

  /* what's the soot class */
  private SootClass type;
  private String    name;
  private int       id;

  public GeneralConstObject(SootClass t, String n){
    this.type = t;
    this.name = n;
    this.id   = counter++;
  }
  
  public SootClass getType() {
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
