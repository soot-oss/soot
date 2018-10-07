package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 Clark Verbrugge
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
 * A debugging attribute, this associates blocks of bytecode with specific source code line numbers.
 *
 * @see attribute_info
 * @author Clark Verbrugge
 */
public class LineNumberTable_attribute extends attribute_info {

  /** Length of the line_number_table array. */
  public int line_number_table_length;

  /**
   * Line number table.
   *
   * @see line_number_table_entry
   */
  public line_number_table_entry line_number_table[];

  public String toString() {
    String sv = "LineNumberTable : " + line_number_table_length + "\n";
    for (int i = 0; i < line_number_table_length; i++) {
      sv += "LineNumber(" + line_number_table[i].start_pc + ":" + line_number_table[i].start_inst + ","
          + line_number_table[i].line_number + ")";
      sv += "\n";
    }

    return sv;
  }
}
