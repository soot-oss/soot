package soot.dava.toolkits.base.AST.transformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Nomair A. Naeem
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

import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;

public class ExtraLabelNamesRemover extends DepthFirstAdapter {

  /*
   * label_0: while(cond){ if(cond1) NO NEED for break label_0 break label_0 Use just break; }
   *
   * label_0: switch(cond){ case 0: Body break label_0; NO NEED for break label_0 case 1: Use just break Body break label_0;
   *
   * IDEA: In gerneral store the current label name Go through the tree rooted at this label name and find all breaks if any
   * break targets the current label name and not some previous one the label name can be removed from the break statement
   * since it is the most recent break....TEST IT ON CASES AND SEE IF THIS IS TRUE THE JAVA LANGUAGE SAYS IT SHOULD BE TRUE
   *
   */
  public ExtraLabelNamesRemover() {
  }

  public ExtraLabelNamesRemover(boolean verbose) {
    super(verbose);
  }

}
