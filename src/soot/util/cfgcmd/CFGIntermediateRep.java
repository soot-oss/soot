/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 John Jorgensen
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

package soot.util.cfgcmd;

import soot.Body;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.baf.Baf;
import soot.grimp.Grimp;
import soot.shimple.Shimple;

/**
 * An enumeration type for representing the intermediate
 * representation to use, in tools that compare or display control
 * flow graphs.
 *
 * 
 */
public abstract class CFGIntermediateRep extends CFGOptionMatcher.CFGOption {

  private CFGIntermediateRep(String name) {
    super(name);
  }

  /**
   * Converts a {@link soot.jimple.JimpleBody JimpleBody} into the
   * corresponding {@link soot.Body Body} in this intermediate
   * representation.
   *
   * @param b The Jimple body to be represented.
   *
   * @return a {@link Body} in this intermediate representation which
   * represents the same method as the argument body.
   */
  public abstract Body getBody(JimpleBody b);


  public static final CFGIntermediateRep JIMPLE_IR = new CFGIntermediateRep("jimple") {
      public Body getBody(JimpleBody b) { 
	return (JimpleBody) b; 
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

  public static final CFGIntermediateRep VIA_SHIMPLE_JIMPLE_IR = 
    new CFGIntermediateRep("viaShimpleJimple") {
      public Body getBody(JimpleBody b) { 
	return Shimple.v().newJimpleBody(Shimple.v().newBody(b)); 
      }
    };

  private final static CFGOptionMatcher irOptions = 
    new CFGOptionMatcher(new CFGIntermediateRep[] {    
      JIMPLE_IR,
      BAF_IR,
      GRIMP_IR,
      SHIMPLE_IR,
      VIA_SHIMPLE_JIMPLE_IR,
    });

  /**
   * Returns the <tt>CFGIntermediateRep</tt> identified by the
   * passed name.
   *
   * @param name A {@link String} identifying the intermediate
   * representation.
   *
   * @return A {@link CFGIntermediateRep} object whose
   * {@link #getBody()} method will create the desired intermediate
   * representation.
   */
  public static CFGIntermediateRep getIR(String name) {
    return (CFGIntermediateRep) irOptions.match(name);
  }

  /**
   * Returns a string containing the names of all the
   * available {@link CFGIntermediateRep}s, separated by
   * '|' characters. 
   *
   * @param initialIndent The number of blank spaces to insert at the 
   *	                    beginning of the returned string. Ignored if 
   *                      negative.
   *
   * @param rightMargin   If positive, newlines will be inserted to try
   *                      to keep the length of each line in the
   *                      returned string less than or equal to
   *                      *<tt>rightMargin</tt>.
   *         
   * @param hangingIndent  If positive, this number of spaces will be
   *                       inserted immediately after each newline 
   *                       inserted to respect the <tt>rightMargin</tt>.
   */
  public static String help(int initialIndent, int rightMargin, 
			    int hangingIndent) {
    return irOptions.help(initialIndent, rightMargin, hangingIndent);
  }

}


