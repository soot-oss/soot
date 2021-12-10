package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrice Pominville and Feng Qian
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
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.baf.BafBody;

/** Interface to aggregate tags of units. */
public abstract class TagAggregator extends BodyTransformer {

  /** Decide whether this tag should be aggregated by this aggregator. */
  public abstract boolean wantTag(Tag t);

  /** Aggregate the given tag assigned to the given unit */
  public abstract void considerTag(Tag t, Unit u, LinkedList<Tag> tags, LinkedList<Unit> units);

  /** Return name of the resulting aggregated tag. */
  public abstract String aggregatedName();

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    BafBody body = (BafBody) b;

    LinkedList<Tag> tags = new LinkedList<Tag>();
    LinkedList<Unit> units = new LinkedList<Unit>();

    /* aggregate all tags */
    for (Unit unit : body.getUnits()) {
      for (Tag tag : unit.getTags()) {
        if (wantTag(tag)) {
          considerTag(tag, unit, tags, units);
        }
      }
    }

    if (units.size() > 0) {
      b.addTag(new CodeAttribute(aggregatedName(), new LinkedList<Unit>(units), new LinkedList<Tag>(tags)));
    }
    fini();
  }

  /** Called after all tags for a method have been aggregated. */
  public void fini() {
  }
}
