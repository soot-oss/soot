package soot.shimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Navindra Umanee <navindra@cs.mcgill.ca>
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

import soot.shimple.toolkits.graph.GlobalValueNumberer;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.DominanceFrontier;
import soot.toolkits.graph.DominatorTree;
import soot.toolkits.graph.DominatorsFinder;
import soot.toolkits.graph.ReversibleGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * @author Navindra Umanee
 **/
public interface ShimpleFactory {

  /**
   * Constructors should memoize their return value. Call clearCache() to force recomputations if body has changed and
   * setBody() hasn't been called again.
   **/
  public void clearCache();

  public UnitGraph getUnitGraph();

  public BlockGraph getBlockGraph();

  public DominatorsFinder<Block> getDominatorsFinder();

  public DominatorTree<Block> getDominatorTree();

  public DominanceFrontier<Block> getDominanceFrontier();

  public GlobalValueNumberer getGlobalValueNumberer();

  public ReversibleGraph<Block> getReverseBlockGraph();

  public DominatorsFinder<Block> getReverseDominatorsFinder();

  public DominatorTree<Block> getReverseDominatorTree();

  public DominanceFrontier<Block> getReverseDominanceFrontier();
}
