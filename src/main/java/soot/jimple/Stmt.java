package soot.jimple;

import soot.Body;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import soot.Unit;
import soot.ValueBox;

/**
 * A common interface for all Jimple statements.
 */
public interface Stmt extends Unit {

  /**
   * Returns true if this Jimple statement performs a method invocation, which is equivalent to having an invoke expression.
   * 
   * @return true if there is an invoke expression
   */
  public boolean containsInvokeExpr();

  /**
   * Returns the invoke expression that represents the method call performed by this statement, or null
   * 
   * @return the invoke expression or null
   */
  public InvokeExpr getInvokeExprUnsafe();

  /**
   * Returns the invoke expression that represents the method call performed by this statement. Throws an exception if there
   * is no invoke expression
   * 
   * @return the invoke expression or null
   * @throws RuntimeException
   *           if there is no invoke expression
   * @see containsInvokeExpr
   * @see getInvokeExprUnsafe
   */
  public InvokeExpr getInvokeExpr() throws RuntimeException;

  /**
   * Returns the invoke expression box, that is an indirection for the invoke expression.
   * 
   * Throws an exception if there is no invoke expression.
   * 
   * @return the invoke expression box
   * @throws RuntimeException
   *           if there is no field reference
   * @see containsInvokeExpr
   */
  public ValueBox getInvokeExprBox() throws RuntimeException;

  /**
   * Returns true if and only if an array reference is used in the statement.
   * 
   * In case of Jimple, this might only happen on the left- or the right hand side of an assignment.
   * 
   * @return the invoke expression box
   */
  public boolean containsArrayRef();

  /**
   * Returns the array reference of this statement.
   * 
   * In case of Jimple, this might only happen on the left- or the right hand side of an assignment. Therefore, this array
   * reference is unique per statement.
   * 
   * This method throws an exception if there is no array reference.
   * 
   * @return the array reference
   * @throws RuntimeException
   *           if there is no array reference
   * @see containsArrayRef
   * @see getArrayRefUnsafe
   */
  public ArrayRef getArrayRef() throws RuntimeException;

  /**
   * Returns the array reference of this statement or null.
   * 
   * In case of Jimple, this might only happen on the left- or the right hand side of an assignment. Therefore, this array
   * reference is unique per statement.
   * 
   * @return the array reference or null
   */
  public ArrayRef getArrayRefUnsafe();

  /**
   * Returns the array reference box of this statement, which is an indirection on the array reference.
   * 
   * In case of Jimple, this might only happen on the left- or the right hand side of an assignment. Therefore, this array
   * reference is unique per statement.
   * 
   * Throws an exception if there is no array reference expression.
   * 
   * @throws RuntimeException
   *           if there is no array reference
   * @return the array reference box
   */
  public ValueBox getArrayRefBox() throws RuntimeException;

  /**
   * Returns true if this Jimple statement has a field reference.
   * 
   * Note that this might be a static or instance field.
   * 
   * In Jimple, there can only be one field reference per statement, and only part of an assignment. Depending on the
   * location of the field reference (left or right hand side), the field is read or written, respectively.
   * 
   * @return true if there is an field reference
   */
  public boolean containsFieldRef();

  /**
   * Returns the field reference of this statement. If there is no field reference, the method throws an exception.
   * 
   * Note that this might be a static or instance field.
   * 
   * In Jimple, there can only be one field reference per statement, and only part of an assignment. Depending on the
   * location of the field reference (left or right hand side), the field is read or written, respectively.
   * 
   * @throws RuntimeException
   *           if there is no field reference
   * @see containsFieldRef
   * @see getFieldRefUnsafe
   * @return the field reference
   */
  public FieldRef getFieldRef() throws RuntimeException;

  /**
   * Returns the field reference of this statement. If there is no field reference, the method returns null.
   * 
   * Note that this might be a static or instance field.
   * 
   * In Jimple, there can only be one field reference per statement, and only part of an assignment. Depending on the
   * location of the field reference (left or right hand side), the field is read or written, respectively.
   * 
   * @throws RuntimeException
   *           if there is no field reference
   * @return the field reference or null
   */
  public FieldRef getFieldRefUnsafe();

  /**
   * Returns the field reference box of this statement, which is an indirection on the field reference. If there is no field
   * reference, the method returns null.
   * 
   * Note that this might be a static or instance field.
   * 
   * In Jimple, there can only be one field reference per statement, and only part of an assignment. Depending on the
   * location of the field reference (left or right hand side), the field is read or written, respectively.
   * 
   * @throws RuntimeException
   *           if there is no field reference
   * @see ValueBox
   * @return the field reference box
   */
  public ValueBox getFieldRefBox() throws RuntimeException;

  /**
   * Returns the body the statement is part of, or null if there is no containing body
   * 
   * @return the containing body (or null)
   */
  public Body getContainingBody();

  /**
   * Sets the containing body
   * 
   * @param body
   *          the new body (or null if the statement was removed)
   */
  public void setContainingBody(Body body);

}
