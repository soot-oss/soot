package soot.dava.toolkits.base.AST.transformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Nomair A. Naeem
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Value;
import soot.ValueBox;
import soot.dava.internal.javaRep.DNewInvokeExpr;
import soot.dava.internal.javaRep.DVirtualInvokeExpr;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.grimp.internal.GAddExpr;

/*
 * Matches the output pattern
 *   (new StringBuffer()).append ............ .toString();
 *   Convert it to 
 *   append1 + append2 .....;
 */

public class NewStringBufferSimplification extends DepthFirstAdapter {
  public static boolean DEBUG = false;

  public NewStringBufferSimplification() {

  }

  public NewStringBufferSimplification(boolean verbose) {
    super(verbose);
  }

  public void inExprOrRefValueBox(ValueBox argBox) {
    if (DEBUG) {
      System.out.println("ValBox is: " + argBox.toString());
    }

    Value tempArgValue = argBox.getValue();
    if (DEBUG) {
      System.out.println("arg value is: " + tempArgValue);
    }

    if (!(tempArgValue instanceof DVirtualInvokeExpr)) {
      if (DEBUG) {
        System.out.println("Not a DVirtualInvokeExpr" + tempArgValue.getClass());
      }
      return;
    }

    // check this is a toString for StringBuffer
    if (DEBUG) {
      System.out.println("arg value is a virtual invokeExpr");
    }
    DVirtualInvokeExpr vInvokeExpr = ((DVirtualInvokeExpr) tempArgValue);

    // need this try catch since DavaStmtHandler expr will not have a "getMethod"
    try {
      if (!(vInvokeExpr.getMethod().toString().equals("<java.lang.StringBuffer: java.lang.String toString()>"))) {
        return;
      }
    } catch (Exception e) {
      return;
    }

    if (DEBUG) {
      System.out.println("Ends in toString()");
    }

    Value base = vInvokeExpr.getBase();
    List args = new ArrayList();
    while (base instanceof DVirtualInvokeExpr) {
      DVirtualInvokeExpr tempV = (DVirtualInvokeExpr) base;
      if (DEBUG) {
        System.out.println("base method is " + tempV.getMethod());
      }
      if (!tempV.getMethod().toString().startsWith("<java.lang.StringBuffer: java.lang.StringBuffer append")) {
        if (DEBUG) {
          System.out.println("Found a virtual invoke which is not a append" + tempV.getMethod());
        }
        return;
      }
      args.add(0, tempV.getArg(0));
      // System.out.println("Append: "+((DVirtualInvokeExpr)base).getArg(0) );
      // move to next base
      base = ((DVirtualInvokeExpr) base).getBase();
    }

    if (!(base instanceof DNewInvokeExpr)) {
      return;
    }

    if (DEBUG) {
      System.out.println("New expr is " + ((DNewInvokeExpr) base).getMethod());
    }

    if (!((DNewInvokeExpr) base).getMethod().toString().equals("<java.lang.StringBuffer: void <init>()>")) {
      return;
    }

    /*
     * The arg is a new invoke expr of StringBuffer and all the appends are present in the args list
     */
    if (DEBUG) {
      System.out.println("Found a new StringBuffer.append list in it");
    }

    // argBox contains the new StringBuffer
    Iterator it = args.iterator();
    Value newVal = null;
    while (it.hasNext()) {
      Value temp = (Value) it.next();
      if (newVal == null) {
        newVal = temp;
      } else {
        // create newVal + temp
        newVal = new GAddExpr(newVal, temp);
      }

    }
    if (DEBUG) {
      System.out.println("New expression for System.out.println is" + newVal);
    }

    argBox.setValue(newVal);
  }
}
