package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.util.LinkedList;

import soot.Unit;

/**
 * A tag aggregator that associates a tag with the <b>first</b> instruction that is tagged with it.
 */
public abstract class FirstTagAggregator extends TagAggregator {
  /** Decide whether this tag should be aggregated by this aggregator. */
  public abstract boolean wantTag(Tag t);

  /** Return name of the resulting aggregated tag. */
  public abstract String aggregatedName();

  /** Decide whether this tag should be aggregated by this aggregator. */
  @Override
  public void considerTag(Tag t, Unit u, LinkedList<Tag> tags, LinkedList<Unit> units) {
    if (units.size() > 0 && units.getLast() == u) {
      return;
    }
    units.add(u);
    tags.add(t);
  }
}
