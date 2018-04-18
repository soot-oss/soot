/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.tagkit;

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
