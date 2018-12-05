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
// for storing statistical or informative data about a class file

import java.io.PrintStream;

class Info {

  public ClassFile cf;
  public long flength; // file length

  public int cp; // number of constant pool entries
  public int fields; // number of fields
  public int methods; // number of methods
  public int pfields; // private fields
  public int pmethods; // private methods

  public int attribsave; // savings through attribute elimination
  public int attribcpsave;// savings through cp compression after attribute elim.
  public int psave; // savings through renaming privates

  public Info(ClassFile newcf) {
    cf = newcf;
  }

  public void verboseReport(PrintStream ps) {
    int total;

    ps.println("<INFO> -- Debigulation Report on " + cf.fn + " --");
    ps.println("<INFO>   Length: " + flength);
    ps.println("<INFO>       CP: " + cp + " reduced to " + cf.constant_pool_count);
    ps.println("<INFO>   Fields: " + fields + " (" + pfields + " private)" + " reduced to " + cf.fields_count);
    ps.println("<INFO>  Methods: " + methods + " (" + pmethods + " private)" + " reduced to " + cf.methods_count);
    total = attribsave + attribcpsave + psave;
    if (total > 0) {
      ps.println("<INFO> -- Savings through debigulation --");
      if (attribsave > 0) {
        ps.println("<INFO>         Attributes: " + attribsave);
      }
      if (attribcpsave > 0) {
        ps.println("<INFO>     CP Compression: " + attribcpsave);
      }
      if (psave > 0) {
        ps.println("<INFO>   Private renaming: " + psave);
      }
      ps.println("<INFO>  Total savings: " + total);
      double d = ((total) * 100000.0) / (flength);
      int x = (int) d;
      d = (x) / 1000.0;
      ps.println("<INFO>          ratio: " + d + "%");
    }
  }
}
