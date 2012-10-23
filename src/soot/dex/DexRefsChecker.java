/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.RefLikeType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.EqExpr;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LengthExpr;
import soot.jimple.NeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.toolkits.scalar.UnitValueBoxPair;

/**

 */
public class DexRefsChecker extends DexTransformer { 
	// Note: we need an instance variable for inner class access, treat this as
	// a local variable (including initialization before use)
  
	private boolean usedAsObject;
	private boolean doBreak = false;
	
    public static DexRefsChecker v() {
        return new DexRefsChecker();
    }

   Local l = null;
    
	@SuppressWarnings("unchecked")
	protected void internalTransform(final Body body, String phaseName, @SuppressWarnings("rawtypes") Map options) {
        final ExceptionalUnitGraph g = new ExceptionalUnitGraph(body);
        final SmartLocalDefs localDefs = new SmartLocalDefs(g, new SimpleLiveLocals(g));
        final SimpleLocalUses localUses = new SimpleLocalUses(g, localDefs);

        for (Unit u: getRefCandidates(body)) {
          Stmt s = (Stmt)u;
          boolean isDeclared = false;
          boolean isPhantom = false;
          boolean hasField = false;
          FieldRef fr = null;
          SootField sf = null;
          if (s.containsFieldRef()) {
            fr = s.getFieldRef();
            sf = fr.getField();
            if (sf != null) {             
              hasField = true;
              isDeclared = sf.isDeclared();
              isPhantom = sf.isPhantom();
            }
          } else {
            throw new RuntimeException("Unit '"+ u +"' does not contain array ref nor field ref.");
          }
          
          if (!hasField) {
            Debug.printDbg("field "+ fr +" '"+ fr +"' has not been found!");
            System.out.println("Warning: add missing field '"+ fr +"' to class!");
            SootClass sc = null;
            String frStr = fr.toString();
            if (frStr.contains(".<")) {
             sc = Scene.v().getSootClass(frStr.split(".<")[1].split(" ")[0].split(":")[0]);
            } else {
             sc = Scene.v().getSootClass(frStr.split(":")[0].replaceAll("^<", ""));
            }
            String fname = fr.toString().split(">")[0].split(" ")[2];
            int modifiers = soot.Modifier.PUBLIC;
            Type ftype = fr.getType();
            Debug.printDbg("missing field: to class '"+ sc +"' field name '"+ fname +"' field modifiers '"+ modifiers +"' field type '"+ ftype +"'");
            sc.addField(new SootField(fname, ftype, modifiers));
          } else {
            //System.out.println("field "+ sf.getName() +" '"+ sf +"' phantom: "+ isPhantom +" declared: "+ isDeclared);
          }
          
        } // for if statements
    }


  private boolean isObject(Type t) {
    return t instanceof RefLikeType;
  }
	
    /**
     * Collect all the if statements comparing two locals with 
     * an Eq or Ne expression
     *
     * @param body the body to analyze
     */
    private Set<Unit> getRefCandidates(Body body) {
        Set<Unit> candidates = new HashSet<Unit>();
        Iterator<Unit> i = body.getUnits().iterator();
        while (i.hasNext()) {
            Unit u = i.next();
            Stmt s = (Stmt)u;
            if (s.containsFieldRef()) {
              candidates.add(u);
            }
        }
        return candidates;
    }

}

