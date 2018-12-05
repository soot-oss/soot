package soot.jimple.spark.fieldrw;

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
import java.util.Map;

import soot.Body;
import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.tagkit.Tag;
import soot.tagkit.TagAggregator;

public class FieldTagAggregator extends TagAggregator {
  public FieldTagAggregator(Singletons.Global g) {
  }

  public static FieldTagAggregator v() {
    return G.v().soot_jimple_spark_fieldrw_FieldTagAggregator();
  }

  protected void internalTransform(Body b, String phaseName, Map options) {
    FieldReadTagAggregator.v().transform(b, phaseName, options);
    FieldWriteTagAggregator.v().transform(b, phaseName, options);
  }

  /** Decide whether this tag should be aggregated by this aggregator. */
  public boolean wantTag(Tag t) {
    throw new RuntimeException();
  }

  public void considerTag(Tag t, Unit u, LinkedList<Tag> tags, LinkedList<Unit> units) {
    throw new RuntimeException();
  }

  public String aggregatedName() {
    throw new RuntimeException();
  }
}
