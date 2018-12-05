package soot.validation;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

public class ValidationException extends RuntimeException {
  private Object concerned;
  private String strMessage;
  private String strCompatibilityMessage;
  private boolean warning;

  /**
   * Creates a new ValidationException.
   *
   * @param concerned
   *          the object which is concerned and could be highlighted in an IDE; for example an unit, a SootMethod, a
   *          SootClass or a local.
   * @param strMessage
   *          the message to display in an IDE supporting the concerned feature
   * @param strCompatibilityMessage
   *          the compatibility message containing useful information without supporting the concerned object
   * @param isWarning
   *          whether the exception can be considered as a warning message
   */
  public ValidationException(Object concerned, String strMessage, String strCompatibilityMessage, boolean isWarning) {
    super(strMessage);
    this.strCompatibilityMessage = strCompatibilityMessage;
    this.strMessage = strMessage;
    this.concerned = concerned;
    this.warning = isWarning;
  }

  /**
   * Creates a new ValidationException, treated as an error.
   *
   * @param concerned
   *          the object which is concerned and could be highlighted in an IDE; for example an unit, a SootMethod, a
   *          SootClass or a local.
   * @param strMessage
   *          the message to display in an IDE supporting the concerned feature
   * @param strCompatibilityMessage
   *          the compatibility message containing useful information without supporting the concerned object
   */
  public ValidationException(Object concerned, String strMessage, String strCompatibilityMessage) {
    this(concerned, strMessage, strCompatibilityMessage, false);
  }

  /**
   * Creates a new ValidationException, treated as an error.
   *
   * @param concerned
   *          the object which is concerned and could be highlighted in an IDE; for example an unit, a SootMethod, a
   *          SootClass or a local.
   * @param strCompatibilityMessage
   *          the compatibility message containing useful information without supporting the concerned object
   */
  public ValidationException(Object concerned, String strCompatibilityMessage) {
    this(concerned, strCompatibilityMessage, strCompatibilityMessage, false);
  }

  public boolean isWarning() {
    return warning;
  }

  public String getRawMessage() {
    return strMessage;
  }

  public Object getConcerned() {
    return concerned;
  }

  @Override
  public String toString() {
    return strCompatibilityMessage;
  }

  private static final long serialVersionUID = 1L;

}
