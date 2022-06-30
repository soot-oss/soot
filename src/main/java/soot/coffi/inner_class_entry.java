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
 * An entry in the inner classes table.
 * 
 * @see InnerClasses_attribute
 * @author Archie L. Cobbs
 */
class inner_class_entry {
  /** Constant pool index for the inner class name */
  public int inner_class_index;
  /** Constant pool index for the outer class name */
  public int outer_class_index;
  /** Short name for the inner class if any, otherwise zero */
  public int name_index;
  /** Access flags for inner class */
  public int access_flags;
}
