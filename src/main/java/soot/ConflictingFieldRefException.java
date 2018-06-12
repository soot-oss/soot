package soot;

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

/**
 * Exception that is thrown when code tries to resolve a reference for some field "fld: type1", but the target class already
 * declares a field "fld: type2". In other words, this exception denotes a mismatch in expected and declared type.
 *
 * @author Steven Arzt
 *
 */
public class ConflictingFieldRefException extends RuntimeException {

  private static final long serialVersionUID = -2351763146637880592L;

  private final SootField existingField;
  private final Type requestedType;

  public ConflictingFieldRefException(SootField existingField, Type requestedType) {
    this.existingField = existingField;
    this.requestedType = requestedType;
  }

  @Override
  public String toString() {
    return String.format("Existing field %s does not match expected field type %s", existingField.toString(),
        requestedType.toString());
  }

}
