package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Jennifer Lhotak
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
 * There can be many annotations in each Visibility attribute
 * 
 * @see attribute_info
 * @author Jennifer Lhotak
 */
public class annotation extends attribute_info {
  /** type_index - CONSTANT_Utf8_info structure constant pool entry. */
  public int type_index;
  /** num_element_value_pairs */
  public int num_element_value_pairs;
  /**
   * Each entry represents a single runtime visible annotation.
   */
  public element_value element_value_pairs[];
}
