package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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

/** This exception is thrown when an exception occurs while loading in instructions from a method source */
public class MethodSourceException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final SootMethod method;

  /**
   * Creates a new method source exception.
   * 
   * @param method
   *          Reading in the method source of that method triggered the error
   * @param innerException
   *          The exception responsible for the error
   */
  public MethodSourceException(SootMethod method, Exception innerException) {
    super(innerException);
    this.method = method;
  }

  /**
   * Creates a new method source exception.
   * 
   * @param method
   *          Reading in the method source of that method triggered the error
   * @param msg
   *          Information about the exception
   */
  public MethodSourceException(SootMethod method, String msg) {
    super(msg);
    this.method = method;
  }

  /**
   * Creates a new method source exception.
   * 
   * @param method
   *          Reading in the method source of that method triggered the error
   * @param msg
   *          Information about the exception
   * @param innerException
   *          The exception responsible for the error
   */
  public MethodSourceException(SootMethod method, String msg, Exception innerException) {
    super(msg, innerException);
    this.method = method;
  }

  /**
   * Reading in the method source of that method triggered the error
   * 
   * @return the soot method
   */
  public SootMethod getMethod() {
    return method;
  }
}
