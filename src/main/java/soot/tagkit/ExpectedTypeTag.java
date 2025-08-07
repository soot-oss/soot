package soot.tagkit;

import soot.RefType;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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
 * Contains some information about a type that is expected by soot.
 */
public class ExpectedTypeTag implements Tag {

  public static final String NAME = "EXPECTED_TYPE";
  private RefType type;

  public ExpectedTypeTag(RefType type) {
    this.type = type;
  }

  public RefType getExpectedType() {
    return type;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public byte[] getValue() throws AttributeValueException {
    return null;
  }

}
