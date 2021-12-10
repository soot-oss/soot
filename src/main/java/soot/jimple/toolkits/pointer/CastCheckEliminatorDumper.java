package soot.jimple.toolkits.pointer;

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

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.toolkits.graph.BriefUnitGraph;

/** A body transformer that simply calls the CastCheckEliminator analysis. */
public class CastCheckEliminatorDumper extends BodyTransformer {

  public CastCheckEliminatorDumper(Singletons.Global g) {
  }

  public static CastCheckEliminatorDumper v() {
    return G.v().soot_jimple_toolkits_pointer_CastCheckEliminatorDumper();
  }

  public String getDefaultOptions() {
    return "";
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    CastCheckEliminator cce = new CastCheckEliminator(new BriefUnitGraph(b));
  }
}
