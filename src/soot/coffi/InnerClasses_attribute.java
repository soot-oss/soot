/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Archie L. Cobbs
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
import soot.*;

import java.io.*;

/** Attribute that connects inner classes with their containing classes.
 * @see attribute_info
 * @author Archie L. Cobbs
 */
class InnerClasses_attribute extends attribute_info {
   /** Length of the inner classes table. */
   public int inner_classes_length;
   /** Actual table of local variables. */
   public inner_class_entry inner_classes[];

   public String toString()
   {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < inner_classes_length; i++) {
            buffer.append(inner_classes[i]);
            buffer.append('\n');
	}
        return buffer.toString();
   }
}

