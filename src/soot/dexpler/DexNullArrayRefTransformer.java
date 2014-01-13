// 
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for 
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>. 
//

package soot.dexpler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Immediate;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.LengthExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;

/**
 * If Dalvik bytecode contains statements using a base array which is always
 * null, Soot's fast type resolver will fail with the following exception:
 * "Exception in thread "main" java.lang.RuntimeException: Base of array reference is not an array!"
 * 
 * Those statements are replaced by a throw statement (this is what will happen in practice if
 * the code is executed).
 * 
 * @author alex
 *
 */
public class DexNullArrayRefTransformer extends BodyTransformer {
  
  public static DexNullArrayRefTransformer v() {
    return new DexNullArrayRefTransformer();
}

  protected void internalTransform(final Body body, String phaseName, @SuppressWarnings("rawtypes") Map options) {

    final ExceptionalUnitGraph g = new ExceptionalUnitGraph(body);
    
    List<Stmt> arrayRefs = new ArrayList<Stmt>();
    List<Stmt> lengthExprs = new ArrayList<Stmt>();
    for (Unit u: body.getUnits()) {
      Stmt s = (Stmt)u;
      if (s.containsArrayRef()) {
        arrayRefs.add(s);
      } else if (s instanceof AssignStmt) {
        AssignStmt ass = (AssignStmt)s;
        Value rightOp = ass.getRightOp();
        if (rightOp instanceof LengthExpr) {
          lengthExprs.add (s);
        }
      }
    }
    NullnessAnalysis na = new NullnessAnalysis (g);
    for (Stmt s: arrayRefs) {
      Debug.printDbg("statement contains arrayref: ", s);
      ArrayRef ar = s.getArrayRef();
      Value base = ar.getBase();
      boolean isAlwaysNullBefore = na.isAlwaysNullBefore(s, (Immediate) base);
      Debug.printDbg("is always null: ", isAlwaysNullBefore);
      if (isAlwaysNullBefore) {
        body.getUnits().swapWith(s, Jimple.v().newNopStmt());
      }       
    }
    for (Stmt s: lengthExprs) {
      Debug.printDbg("statement contains length expr: ", s);
      LengthExpr l = (LengthExpr)((AssignStmt)s).getRightOp();
      Value base = l.getOp();
      boolean isAlwaysNullBefore = na.isAlwaysNullBefore(s, (Immediate) base);
      Debug.printDbg("is always null: ", isAlwaysNullBefore);
      if (isAlwaysNullBefore) {
        body.getUnits().swapWith(s, Jimple.v().newNopStmt());
      }       
    }
    
  }    
}



