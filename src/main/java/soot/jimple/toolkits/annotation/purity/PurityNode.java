package soot.jimple.toolkits.annotation.purity;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Antoine Mine
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
 * Interface shared by all kinds of nodes in a PurityGraph. Such nodes are immutables. They are hashable and two nodes are
 * equal only if they have the same kind and were constructed using the same arguments (structural equality).
 *
 */
public interface PurityNode {

  /** Is it an inside node ? */
  public boolean isInside();

  /** Is it a load node ? */
  public boolean isLoad();

  /** Is it a parameter or this node ? */
  public boolean isParam();
}
