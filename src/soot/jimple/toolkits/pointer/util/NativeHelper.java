/**
 * Native method helper 
 *
 * @author Feng Qian
 */


package soot.jimple.toolkits.pointer.util;

import soot.jimple.toolkits.pointer.representations.*;

public abstract class NativeHelper {

  private static NativeHelper _helper = null;

  /**
   * Somewhere should register an instance of this sub class.
   */
  public static final void register(NativeHelper impl){
    _helper = impl;
  }


  /**
   * Regular assignment such as "a = b".
   */
  public static void assign(ReferenceVariable lhs, ReferenceVariable rhs){
    _helper.assignImpl(lhs, rhs);
  }  

  /**
   * Assignment of an abstract object to the variable, such as
   * " a = new A()", which is considered to add a target in a's 
   * points-to set.
   *
   * This method is used to fomulate the effect of getting
   * an environmental constant object such as 'getClass'.
   */
  public static void assignObjectTo(ReferenceVariable lhs, AbstractObject obj){
    _helper.assignObjectToImpl(lhs, obj);
  }

  /**
   * Returns a reference variable representing the array element of
   * this variable. Now it does not look at the array index.
   */
  public static ReferenceVariable arrayElementOf(ReferenceVariable base){
    return _helper.arrayElementOfImpl(base);
  }

  /**
   * Returns a variable which has the effect of cloning.
   * A moderate approach would return the variable itself.
   * 
   * e.g., a = b.clone()  will be rendered to:
   *       Vr.isAssigned(Vb.cloneObject());
   *       Va = Vr;
   */
  public static ReferenceVariable cloneObject(ReferenceVariable source){
    return _helper.cloneObjectImpl(source);
  }

  /**
   * Returns a variable which carries an allocation site with
   * the least type (an artificial type, subtype of any other types,
   * which means such type info is useless for resolving invocation 
   * targets).
   *
   * It is used for simulating java.lang.Class.newInstance0();
   * To verify, @this variable mush have CLASSCLASS type.
   */
  public static ReferenceVariable newInstanceOf(ReferenceVariable cls){
    return _helper.newInstanceOfImpl(cls);
  }
  
  /** 
   * Returns a reference variable representing a static Java field.  
   * The implementation must ensure that there is only one such
   * representation for each static field. 
   *
   * @param field, must be a static field
   */
  public static ReferenceVariable staticField(String className, String fieldName ){
    return _helper.staticFieldImpl(className, fieldName);
  }

  /**
   * Returns a variable representing a non-existing Java field, used by
   * e.g., java.lang.Class: getSingers, setSigners
   *       java.lang.Class: getProtectionDomain0, setProtectionDomain0
   *
   * To simplify simulation, the temporary field variable is like a 
   * static field.
   *
   * The temporary fields are uniquely indexed by signatures.
   */
  public static ReferenceVariable tempField(String fieldsig){
    return _helper.tempFieldImpl(fieldsig);
  }

  /**
   * Make a temporary variable.
   * It is used for assignment where both sides are complex variables.
   * e.g., for java.lang.System arraycopy(src, ..., dst, ...)
   *    instead of make an expression : dst[] = src[],
   *    it introduces a temporary variable
   *                                    t = src[]
   *                                    dst[] = t
   *
   * The temporary variable has to be unique.
   */
  public static ReferenceVariable tempVariable(){
    return _helper.tempVariableImpl();
  }

  /**
   * Sub classes should implement both.
   */
  protected abstract 
    void assignImpl(ReferenceVariable lhs, ReferenceVariable rhs);
  protected abstract 
    void assignObjectToImpl(ReferenceVariable lhs, AbstractObject obj);
  protected abstract 
    ReferenceVariable arrayElementOfImpl(ReferenceVariable base);
  protected abstract 
    ReferenceVariable cloneObjectImpl(ReferenceVariable source);
  protected abstract 
    ReferenceVariable newInstanceOfImpl(ReferenceVariable cls);
  protected abstract 
    ReferenceVariable staticFieldImpl(String className, String fieldName );
  protected abstract 
    ReferenceVariable tempFieldImpl(String fieldsig);
  protected abstract
    ReferenceVariable tempVariableImpl();
}
