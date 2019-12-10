package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2019 Shawn Meier
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

import soot.Body;
import soot.BodyTransformer;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;

public class EnhancedUnitGraphTestUtility extends BodyTransformer {
  private EnhancedUnitGraph unitGraph = null;

  protected void internalTransform(Body body, String phase, java.util.Map<String, String> options) {
    String methodSig = body.getMethod().getSignature();
    if (methodSig.contains("soot.toolkits.graph.targets.TestException")
        && body.getMethod().getName().contains("main")) {
      unitGraph = new EnhancedUnitGraph(body);
    }
  }

  public EnhancedUnitGraph getUnitGraph() {
    return unitGraph;
  }
}
