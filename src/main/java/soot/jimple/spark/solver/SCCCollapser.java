package soot.jimple.spark.solver;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import java.util.HashSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.spark.internal.TypeManager;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.PointsToSetInternal;

/**
 * Collapses VarNodes (green) forming strongly-connected components in the pointer assignment graph.
 *
 * @author Ondrej Lhotak
 */

public class SCCCollapser {
  private static final Logger logger = LoggerFactory.getLogger(SCCCollapser.class);

  /** Actually collapse the SCCs in the PAG. */
  public void collapse() {
    boolean verbose = pag.getOpts().verbose();
    if (verbose) {
      logger.debug("" + "Total VarNodes: " + pag.getVarNodeNumberer().size() + ". Collapsing SCCs...");
    }

    new TopoSorter(pag, ignoreTypes).sort();
    TreeSet<VarNode> s = new TreeSet<VarNode>();
    for (final VarNode v : pag.getVarNodeNumberer()) {
      s.add(v);
    }
    for (VarNode v : s) {
      dfsVisit(v, v);
    }

    if (verbose) {
      logger.debug("" + "" + numCollapsed + " nodes were collapsed.");
    }
    visited = null;
  }

  public SCCCollapser(PAG pag, boolean ignoreTypes) {
    this.pag = pag;
    this.ignoreTypes = ignoreTypes;
    this.typeManager = pag.getTypeManager();
  }

  /* End of public methods. */
  /* End of package methods. */

  protected int numCollapsed = 0;
  protected PAG pag;
  protected HashSet<VarNode> visited = new HashSet<VarNode>();
  protected boolean ignoreTypes;
  protected TypeManager typeManager;

  final protected void dfsVisit(VarNode v, VarNode rootOfSCC) {
    if (visited.contains(v)) {
      return;
    }
    visited.add(v);
    Node[] succs = pag.simpleInvLookup(v);
    for (Node element : succs) {
      if (ignoreTypes || typeManager.castNeverFails(element.getType(), v.getType())) {
        dfsVisit((VarNode) element, rootOfSCC);
      }
    }
    if (v != rootOfSCC) {
      if (!ignoreTypes) {
        if (typeManager.castNeverFails(v.getType(), rootOfSCC.getType())
            && typeManager.castNeverFails(rootOfSCC.getType(), v.getType())) {
          rootOfSCC.mergeWith(v);
          numCollapsed++;
        }
      } else /* ignoreTypes */ {
        if (typeManager.castNeverFails(v.getType(), rootOfSCC.getType())) {
          rootOfSCC.mergeWith(v);
        } else if (typeManager.castNeverFails(rootOfSCC.getType(), v.getType())) {
          v.mergeWith(rootOfSCC);
        } else {
          rootOfSCC.getReplacement().setType(null);
          PointsToSetInternal set = rootOfSCC.getP2Set();
          if (set != null) {
            set.setType(null);
          }
          rootOfSCC.mergeWith(v);
        }
        numCollapsed++;
      }
    }
  }
}
