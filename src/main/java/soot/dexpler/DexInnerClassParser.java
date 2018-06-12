package soot.dexpler;

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

import soot.tagkit.InnerClassTag;

/**
 * Utility class for handling inner/outer class references in Dalvik
 *
 * @author Steven Arzt
 *
 */
public class DexInnerClassParser {

  /**
   * Gets the name of the outer class (in Soot notation) from the given InnerClassTag
   *
   * @param icTag
   *          The InnerClassTag from which to read the name of the outer class
   * @return The nam,e of the outer class (in Soot notation) as specified in the tag. If the specification is invalid, null
   *         is returned.
   */
  public static String getOuterClassNameFromTag(InnerClassTag icTag) {
    String outerClass;

    if (icTag.getOuterClass() == null) { // anonymous and local classes
      String inner = icTag.getInnerClass().replaceAll("/", ".");
      if (inner.contains("$-")) {
        /*
         * This is a special case for generated lambda classes of jack and jill compiler. Generated lambda classes may
         * contain '$' which do not indicate an inner/outer class separator if the '$' occurs after a inner class with a name
         * starting with '-'. Thus we search for '$-' and anything after it including '-' is the inner classes name and
         * anything before it is the outer classes name.
         */
        outerClass = inner.substring(0, inner.indexOf("$-"));
      } else if (inner.contains("$")) {
        // remove everything after the last '$' including the last '$'
        outerClass = inner.substring(0, inner.lastIndexOf('$'));
      } else {
        // This tag points nowhere
        outerClass = null;
      }
    } else {
      outerClass = icTag.getOuterClass().replaceAll("/", ".");
    }

    return outerClass;
  }

}
