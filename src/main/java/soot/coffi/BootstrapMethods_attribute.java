package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Eric Bodden
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
 * There should be exactly one BootstrapMethods attribute in every class file.
 * 
 * @author Eric Bodden
 * @see http://www.xiebiao.com/docs/javase/7/api/java/lang/invoke/package-summary.html#bsmattr
 */
class BootstrapMethods_attribute extends attribute_info {

  // indices to method handles
  public short[] method_handles;

  // arguments to method handles, in same order as above, i.e., arg_indices[i] holds the arguments to method_handles[i]
  public short[][] arg_indices;

}
