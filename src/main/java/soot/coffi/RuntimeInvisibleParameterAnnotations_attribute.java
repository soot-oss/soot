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
 * There should be at most one RuntimeVisibleParameterAnnotations attribute in method indicating the list of annotations for
 * each method parameter
 *
 * @see attribute_info
 * @see method_info#attributes
 * @author Jennifer Lhotak
 */
public class RuntimeInvisibleParameterAnnotations_attribute extends attribute_info {
  /** Length of annotations table array. */
  public int num_parameters;
  /**
   * Each entry represents a single runtime visible annotation.
   */
  public parameter_annotation parameter_annotations[];
}
