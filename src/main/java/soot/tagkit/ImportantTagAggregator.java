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
import soot.baf.Inst;

/**
 * A tag aggregator that associates a tag with the <b>most important</b> instruction that is tagged with it. An instruction
 * is important if it contains a field or array reference, a method invocation, or an object allocation.
 */
public abstract class ImportantTagAggregator extends TagAggregator {

  /** Decide whether this tag should be aggregated by this aggregator. */
  @Override
  public void considerTag(Tag t, Unit u, LinkedList<Tag> tags, LinkedList<Unit> units) {
    Inst i = (Inst) u;
    if (i.containsInvokeExpr() || i.containsFieldRef() || i.containsArrayRef() || i.containsNewExpr()) {
      units.add(u);
      tags.add(t);
    }
  }
}
