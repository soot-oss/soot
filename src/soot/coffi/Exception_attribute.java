/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997 Clark Verbrugge
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







package soot.coffi;

import java.io.*;

/** There should be exactly one Exceptions attribute in every method,
 * indicating the types of exceptions the method might throw.
 * @see attribute_info
 * @see method_info#attributes
 * @author Clark Verbrugge
 */
public class Exception_attribute extends attribute_info {
   /** Length of exception table array. */
   public int number_of_exceptions;
   /** Constant pool indices of CONSTANT_Class types representing exceptions
    * the associated method might throw.
    * @see CONSTANT_Class_info
    */
   public int exception_index_table[];
}
