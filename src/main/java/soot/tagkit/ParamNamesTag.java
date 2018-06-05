package soot.tagkit;

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

import java.util.Arrays;
import java.util.List;

/**
 * Represents a tag that just has a string to be printed with the code.
 */

public class ParamNamesTag implements Tag {
  String[] names;

  /**
   * Backwards compatibility
   * 
   * @param parameterNames
   */
  public ParamNamesTag(List<String> parameterNames) {
    this(parameterNames.toArray(new String[parameterNames.size()]));
  }

  public ParamNamesTag(String[] parameterNames) {
    names = parameterNames;
  }

  @Override
  public String toString() {
    return names.toString();
  }

  public List<String> getNames() {
    return Arrays.asList(names);
  }

  public String[] getNameArray() {
    return names;
  }

  /** Returns the tag name. */
  @Override
  public String getName() {
    return "ParamNamesTag";
  }

  public List<String> getInfo() {
    return getNames();
  }

  /** Returns the tag raw data. */
  @Override
  public byte[] getValue() {
    throw new RuntimeException("ParamNamesTag has no value for bytecode");
  }
}
