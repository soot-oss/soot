package soot.toolkits.graph.pdg;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 - 2010 Hossein Sadat-Mohtasham
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

import soot.toolkits.graph.DominatorTree;
import soot.toolkits.graph.DominatorsFinder;

/**
 * Constructs a multi-headed dominator tree. This is mostly the same as the DominatorTree but the buildTree method is changed
 * to allow mutilple heads. This can be used for graphs that are multi-headed and cannot be augmented to become
 * single-headed.
 *
 * March 2014: Pulled this code into the original {@link DominatorTree}. This class now became a stub (Steven Arzt).
 *
 * @author Hossein Sadat-Mohtasham March 2009
 *
 **/
public class MHGDominatorTree<N> extends DominatorTree<N> {

  public MHGDominatorTree(DominatorsFinder<N> dominators) {
    super(dominators);
  }

}
