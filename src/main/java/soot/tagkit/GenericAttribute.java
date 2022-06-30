package soot.tagkit;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import soot.UnitBox;

/**
 * Represents a general attribute which can be attached to implementations of Host. It can be directly used to add attributes
 * of class files, fields, and methods.
 *
 * @see CodeAttribute
 */
public class GenericAttribute implements Attribute {

  private final String mName;
  private byte[] mValue;

  public GenericAttribute(String name, byte[] value) {
    this.mName = name;
    this.mValue = value != null ? value : new byte[0];
  }

  @Override
  public String getName() {
    return mName;
  }

  @Override
  public byte[] getValue() {
    return mValue;
  }

  @Override
  public String toString() {
    return mName + ' ' + Arrays.toString(Base64.encode(mValue));
  }

  @Override
  public void setValue(byte[] value) {
    mValue = value;
  }

  public List<UnitBox> getUnitBoxes() {
    return Collections.emptyList();
  }
}
