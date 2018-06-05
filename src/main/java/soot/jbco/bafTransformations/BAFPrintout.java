package soot.jbco.bafTransformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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
import java.util.Stack;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Type;
import soot.Unit;
import soot.jbco.IJbcoTransform;

/**
 * @author Michael Batchelder
 * 
 *         Created on 15-Jun-2006
 */
public class BAFPrintout extends BodyTransformer implements IJbcoTransform {

  public static String name = "bb.printout";

  public void outputSummary() {
  }

  public String[] getDependencies() {
    return new String[0];
  }

  public String getName() {
    return name;
  }

  static boolean stack = false;

  public BAFPrintout() {
  }

  public BAFPrintout(String newname, boolean print_stack) {
    name = newname;
    stack = print_stack;
  }

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    // if (b.getMethod().getSignature().indexOf("run")<0) return;
    System.out.println("\n" + b.getMethod().getSignature());

    if (stack) {
      Map<Unit, Stack<Type>> stacks = null;
      Map<Local, Local> b2j = soot.jbco.Main.methods2Baf2JLocals.get(b.getMethod());

      try {
        if (b2j == null) {
          stacks = StackTypeHeightCalculator.calculateStackHeights(b);
        } else {
          stacks = StackTypeHeightCalculator.calculateStackHeights(b, b2j);
        }

        StackTypeHeightCalculator.printStack(b.getUnits(), stacks, true);
      } catch (Exception exc) {
        System.out.println("\n**************Exception calculating height " + exc + ", printing plain bytecode now\n\n");
        soot.jbco.util.Debugger.printUnits(b, "  FINAL");
      }
    } else {
      soot.jbco.util.Debugger.printUnits(b, "  FINAL");
    }
    System.out.println();
  }
}
