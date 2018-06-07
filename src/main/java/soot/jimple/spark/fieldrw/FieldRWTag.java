package soot.jimple.spark.fieldrw;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.util.Set;

import soot.SootField;
import soot.tagkit.Tag;

/** Implements a tag that holds a list of fields read or written by a call. */
public abstract class FieldRWTag implements Tag {
  String fieldNames = new String();

  FieldRWTag(Set<SootField> fields) {
    StringBuffer sb = new StringBuffer();
    boolean first = true;
    for (SootField field : fields) {
      if (!first) {
        sb.append("%");
      }
      first = false;
      sb.append(field.getDeclaringClass().getName());
      sb.append(":");
      sb.append(field.getName());
    }
    fieldNames = sb.toString();
  }

  public abstract String getName();

  public byte[] getValue() {
    byte[] bytes = fieldNames.getBytes();
    byte[] ret = new byte[bytes.length + 2];
    ret[0] = (byte) (bytes.length / 256);
    ret[1] = (byte) (bytes.length % 256);
    System.arraycopy(bytes, 0, ret, 2, bytes.length);
    return ret;
  }

  public String toString() {
    return getName() + fieldNames;
  }
}
