package soot.jimple.toolkits.ide.libsumm;

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

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.Transform;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public class Main {
  static int yes = 0, no = 0;

  /**
   * @param args
   */
  public static void main(String[] args) {
    PackManager.v().getPack("jtp").add(new Transform("jtp.fixedie", new BodyTransformer() {

      @Override
      protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        for (Unit u : b.getUnits()) {
          Stmt s = (Stmt) u;
          if (s.containsInvokeExpr()) {
            InvokeExpr ie = s.getInvokeExpr();
            if (FixedMethods.isFixed(ie)) {
              System.err.println("+++ " + ie);
              yes++;
            } else {
              System.err.println(" -  " + ie);
              no++;
            }
          }
        }
      }

    }));
    soot.Main.main(args);
    System.err.println("+++ " + yes);
    System.err.println(" -  " + no);
  }

}
