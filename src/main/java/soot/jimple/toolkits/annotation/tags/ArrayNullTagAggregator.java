package soot.jimple.toolkits.annotation.tags;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Feng Qian
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

import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.baf.Inst;
import soot.tagkit.Tag;
import soot.tagkit.TagAggregator;

/** The aggregator for ArrayNullCheckAttribute. */

public class ArrayNullTagAggregator extends TagAggregator {
  public ArrayNullTagAggregator(Singletons.Global g) {
  }

  public static ArrayNullTagAggregator v() {
    return G.v().soot_jimple_toolkits_annotation_tags_ArrayNullTagAggregator();
  }

  public boolean wantTag(Tag t) {
    return (t instanceof OneByteCodeTag);
  }

  @Override
  public void considerTag(Tag t, Unit u, LinkedList<Tag> tags, LinkedList<Unit> units) {
    Inst i = (Inst) u;
    if (!(i.containsInvokeExpr() || i.containsFieldRef() || i.containsArrayRef())) {
      return;
    }

    OneByteCodeTag obct = (OneByteCodeTag) t;

    if (units.size() == 0 || units.getLast() != u) {
      units.add(u);
      tags.add(new ArrayNullCheckTag());
    }
    ArrayNullCheckTag anct = (ArrayNullCheckTag) tags.getLast();
    anct.accumulate(obct.getValue()[0]);
  }

  public String aggregatedName() {
    return "ArrayNullCheckAttribute";
  }
}
