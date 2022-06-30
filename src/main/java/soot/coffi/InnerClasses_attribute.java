package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Archie L. Cobbs
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
 * Attribute that connects inner classes with their containing classes.
 * 
 * @see attribute_info
 * @author Archie L. Cobbs
 */
class InnerClasses_attribute extends attribute_info {
  /** Length of the inner classes table. */
  public int inner_classes_length;
  /** Actual table of local variables. */
  public inner_class_entry inner_classes[];

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < inner_classes_length; i++) {
      buffer.append(inner_classes[i]);
      buffer.append('\n');
    }
    return buffer.toString();
  }
}
