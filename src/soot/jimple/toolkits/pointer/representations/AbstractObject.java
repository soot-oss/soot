/**
 * An abstract object is a static representation of a run-time object.
 * It is possible for an abstract object to represent all run-time objects
 * generated from the same source.
 *
 * Currently, there are following types of abstract objects:
 *    AbstractLocation, represents objects created by a new site.
 *    ObjectConstant, represents objects created by a VM, such as
 *                      objects representing classes, methods, ....
 *
 * @author Feng Qian
 */
package soot.jimple.toolkits.pointer.representations;

import soot.*;

public interface AbstractObject{
  public Type getType();
  public String toString();
  public String shortString();
}
