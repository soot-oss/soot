/**
 * An instance of ConstantObject represents a set of aliased environmental 
 * objects created by VM. It is used for:
 *          GeneralConstantObject
 *          ClassConstant
 *          MethodConstant
 *          FieldConstant
 *
 * @author Feng Qian
 */

package soot.jimple.toolkits.pointer.representations;

public abstract class ConstantObject implements AbstractObject {
  public String toString(){
    return "constantobject";
  }

  public String shortString(){
    return "shortstring";
  }
}
