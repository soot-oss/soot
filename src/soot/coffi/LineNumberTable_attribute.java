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

/** A debugging attribute, this associates blocks of bytecode with
 * specific source code line numbers.
 * @see attribute_info
 * @author Clark Verbrugge
 */
public class LineNumberTable_attribute extends attribute_info {

   /** Length of the line_number_table array. */
   public int line_number_table_length;

   /** Line number table.
    * @see line_number_table_entry
    */
   public line_number_table_entry line_number_table[];

    public String toString()
    {
	String sv = "LineNumberTable : " + line_number_table_length + "\n";
	for (int i=0; i<line_number_table_length; i++)
	{
	    sv += "LineNumber("+line_number_table[i].start_pc
		+":"+line_number_table[i].start_inst+","
		+ line_number_table[i].line_number +")";
	    sv += "\n";
	}

	return sv;
    }
}
