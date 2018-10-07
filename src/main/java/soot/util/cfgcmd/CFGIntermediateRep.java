package soot.util.cfgcmd;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 John Jorgensen
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

import soot.Body;
import soot.baf.Baf;
import soot.grimp.Grimp;
import soot.jimple.JimpleBody;
import soot.shimple.Shimple;

/**
 * An enumeration type for representing the varieties of intermediate representation available, for use in tools that compare
 * or display control flow graphs.
 *
 *
 */
public abstract class CFGIntermediateRep extends CFGOptionMatcher.CFGOption {

  private CFGIntermediateRep(String name) {
    super(name);
  }

  /**
   * Converts a <code>JimpleBody</code> into the corresponding <code>Body</code> in this intermediate representation.
   *
   * @param b
   *          The Jimple body to be represented.
   *
   * @return a {@link Body} in this intermediate representation which represents the same method as <code>b</code>.
   */
  public abstract Body getBody(JimpleBody b);

  public static final CFGIntermediateRep JIMPLE_IR = new CFGIntermediateRep("jimple") {
    public Body getBody(JimpleBody b) {
      return b;
    }
  };

  public static final CFGIntermediateRep BAF_IR = new CFGIntermediateRep("baf") {
    public Body getBody(JimpleBody b) {
      return Baf.v().newBody(b);
    }
  };

  public static final CFGIntermediateRep GRIMP_IR = new CFGIntermediateRep("grimp") {
    public Body getBody(JimpleBody b) {
      return Grimp.v().newBody(b, "gb");
    }
  };

  public static final CFGIntermediateRep SHIMPLE_IR = new CFGIntermediateRep("shimple") {
    public Body getBody(JimpleBody b) {
      return Shimple.v().newBody(b);
    }
  };

  public static final CFGIntermediateRep VIA_SHIMPLE_JIMPLE_IR = new CFGIntermediateRep("viaShimpleJimple") {
    public Body getBody(JimpleBody b) {
      return Shimple.v().newJimpleBody(Shimple.v().newBody(b));
    }
  };

  private final static CFGOptionMatcher irOptions
      = new CFGOptionMatcher(new CFGIntermediateRep[] { JIMPLE_IR, BAF_IR, GRIMP_IR, SHIMPLE_IR, VIA_SHIMPLE_JIMPLE_IR, });

  /**
   * Returns the <code>CFGIntermediateRep</code> identified by the passed name.
   *
   * @param name
   *          A {@link String} identifying the intermediate representation.
   *
   * @return A <code>CFGIntermediateRep</code> object whose {@link #getBody(JimpleBody)} method will create the desired
   *         intermediate representation.
   */
  public static CFGIntermediateRep getIR(String name) {
    return (CFGIntermediateRep) irOptions.match(name);
  }

  /**
   * Returns a string containing the names of all the available <code>CFGIntermediateRep</code>s, separated by '|'
   * characters.
   *
   * @param initialIndent
   *          The number of blank spaces to insert at the beginning of the returned string. Ignored if negative.
   *
   * @param rightMargin
   *          If positive, newlines will be inserted to try to keep the length of each line in the returned string less than
   *          or equal to *<code>rightMargin</code>.
   *
   * @param hangingIndent
   *          If positive, this number of spaces will be inserted immediately after each newline inserted to respect the
   *          <code>rightMargin</code>.
   */
  public static String help(int initialIndent, int rightMargin, int hangingIndent) {
    return irOptions.help(initialIndent, rightMargin, hangingIndent);
  }

}
