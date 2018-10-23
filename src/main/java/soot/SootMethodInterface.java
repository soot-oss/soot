package soot;

import java.util.List;

import soot.util.NumberedString;

/**
 * The common interface of {@link SootMethod} (resolved method) and {@link SootMethodRef} (unresolved method). Therefore it
 * allows to access the properties independently whether the method is a resolved one or not.
 */
public interface SootMethodInterface {

  /**
   *  @return The class which declares the current {@link SootMethod}/{@link SootMethodRef}  
   */
  public SootClass getDeclaringClass();

  /**
   * @return Name of the method
   */
  public String getName();

  public List<Type> getParameterTypes();

  public Type getParameterType(int i);

  public Type getReturnType();

  public boolean isStatic();

  /**
   * @return The Soot signature of this method. Used to refer to methods unambiguously.
   */
  public String getSignature();

}
