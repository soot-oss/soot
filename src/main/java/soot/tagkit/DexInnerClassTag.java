package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Archie L. Cobbs
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
 * Contains additional information about inner classes for Android
 */
public class DexInnerClassTag extends InnerClassTag {

  private final String originalName;

  public DexInnerClassTag(String innerClass, String outerClass, String name, String originalName, int accessFlags) {
    super(innerClass, outerClass, name, accessFlags);
    this.originalName = originalName;
  }

  /**
   * Returns the name found in the original annotation in the dex file
   * 
   * @return the original name
   */
  public String getOriginalName() {
    return originalName;
  }

}
