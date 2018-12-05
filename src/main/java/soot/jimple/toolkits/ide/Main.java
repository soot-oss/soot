package soot.jimple.toolkits.ide;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2013 Eric Bodden and others
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

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;

import java.util.Map;

import soot.PackManager;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.toolkits.ide.exampleproblems.IFDSPossibleTypes;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

public class Main {

  /**
   * @param args
   */
  public static void main(String[] args) {

    PackManager.v().getPack("wjtp").add(new Transform("wjtp.ifds", new SceneTransformer() {
      protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {

        IFDSTabulationProblem<Unit, ?, SootMethod, InterproceduralCFG<Unit, SootMethod>> problem
            = new IFDSPossibleTypes(new JimpleBasedInterproceduralCFG());

        @SuppressWarnings({ "rawtypes", "unchecked" })
        JimpleIFDSSolver<?, InterproceduralCFG<Unit, SootMethod>> solver = new JimpleIFDSSolver(problem);
        solver.solve();
      }
    }));

    soot.Main.main(args);
  }

}
