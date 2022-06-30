package soot.jimple.toolkits.ide.icfg.dotexport;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import heros.InterproceduralCFG;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.SootMethod;
import soot.Unit;
import soot.util.dot.DotGraph;

public class ICFGDotVisualizer {
  private static final Logger logger = LoggerFactory.getLogger(ICFGDotVisualizer.class);
  private DotGraph dotIcfg = new DotGraph("");
  private ArrayList<Unit> visited = new ArrayList<Unit>();
  String fileName;
  Unit startPoint;
  InterproceduralCFG<Unit, SootMethod> icfg;

  /**
   * This class will save your ICFG in DOT format by traversing the ICFG Depth-first!
   * 
   * @param fileName:
   *          Name of the file to save ICFG in DOT extension
   * @param startPoint:
   *          This is of type Unit and is the starting point of the graph (eg. main method)
   * @param InterproceduralCFG<Unit,
   *          SootMethod>: Object of InterproceduralCFG which represents the entire ICFG
   */
  public ICFGDotVisualizer(String fileName, Unit startPoint, InterproceduralCFG<Unit, SootMethod> icfg) {

    this.fileName = fileName;
    this.startPoint = startPoint;
    this.icfg = icfg;
    if (this.fileName == null || this.fileName == "") {
      System.out.println("Please provide a vaid filename");
    }
    if (this.startPoint == null) {
      System.out.println("startPoint is null!");
    }
    if (this.icfg == null) {
      System.out.println("ICFG is null!");
    }

  }

  /**
   * For the given file name, export the ICFG into DOT format. All parameters initialized through the constructor
   */

  public void exportToDot() {
    if (this.startPoint != null && this.icfg != null && this.fileName != null) {
      graphTraverse(this.startPoint, this.icfg);
      dotIcfg.plot(this.fileName);
      logger.debug("" + fileName + DotGraph.DOT_EXTENSION);
    } else {
      System.out.println("Parameters not properly initialized!");
    }

  }

  private void graphTraverse(Unit startPoint, InterproceduralCFG<Unit, SootMethod> icfg) {
    List<Unit> currentSuccessors = icfg.getSuccsOf(startPoint);

    if (currentSuccessors.size() == 0) {
      System.out.println("Traversal complete");
      return;
    } else {
      for (Unit succ : currentSuccessors) {
        System.out.println("Succesor: " + succ.toString());
        if (!visited.contains(succ)) {
          dotIcfg.drawEdge(startPoint.toString(), succ.toString());
          visited.add(succ);
          graphTraverse(succ, icfg);

        } else {
          dotIcfg.drawEdge(startPoint.toString(), succ.toString());
        }
      }
    }
  }
}
