package soot.jimple.toolkits.annotation;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jennifer Lhotak
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.jimple.Stmt;
import soot.tagkit.LinkTag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.MHGDominatorsFinder;

/**
 * A body transformer that records avail expression information in tags. - both pessimistic and optimistic options
 */
public class DominatorsTagger extends BodyTransformer {
  public DominatorsTagger(Singletons.Global g) {
  }

  public static DominatorsTagger v() {
    return G.v().soot_jimple_toolkits_annotation_DominatorsTagger();
  }

  protected void internalTransform(Body b, String phaseName, Map opts) {

    MHGDominatorsFinder analysis = new MHGDominatorsFinder(new ExceptionalUnitGraph(b));
    Iterator it = b.getUnits().iterator();
    while (it.hasNext()) {
      Stmt s = (Stmt) it.next();
      List dominators = analysis.getDominators(s);
      Iterator dIt = dominators.iterator();
      while (dIt.hasNext()) {
        Stmt ds = (Stmt) dIt.next();
        String info = ds + " dominates " + s;
        s.addTag(new LinkTag(info, ds, b.getMethod().getDeclaringClass().getName(), "Dominators"));
      }
    }
  }
}
