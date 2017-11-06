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

/** An entry in the inner classes table.
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
