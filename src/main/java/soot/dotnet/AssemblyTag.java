package soot.dotnet;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Saves a reference to the assembly file the entity originates.
 */
public class AssemblyTag implements Tag {

  public static final String ASSEMBLY = "Assembly";
  private String filename;

  public AssemblyTag(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }

  @Override
  public String getName() {
    return ASSEMBLY;
  }

  @Override
  public byte[] getValue() throws AttributeValueException {
    return null;
  }

}
