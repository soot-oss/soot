/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

package soot.jbco.jimpleTransformations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Pattern;

import soot.*;
import soot.baf.Baf;
import soot.baf.IdentityInst;
import soot.jbco.util.Rand;
import soot.jbco.*;
import soot.jimple.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.scalar.*;
import soot.util.*;
/**
 * @author Michael Batchelder 
 * 
 * Created on 10-Jul-2006 
 */
public class AddSwitches extends BodyTransformer implements IJbcoTransform {

  int switchesadded = 0;
  public void outputSummary() {
    out.println("Switches added: "+switchesadded);
  }

  public static String dependancies[] = new String[] { "wjtp.jbco_fr", "jtp.jbco_adss", "bb.jbco_ful"};

  public String[] getDependancies() {
    return dependancies;
  }

  public static String name = "jtp.jbco_adss";

  public String getName() {
    return name;
  }

  public boolean checkTraps(Unit u, Body b) {
    Iterator it = b.getTraps().iterator();
    while (it.hasNext()) {
      Trap t = (Trap)it.next();
      if (t.getBeginUnit() == u ||
          t.getEndUnit() == u ||
          t.getHandlerUnit() == u)
        return true;
    }

    return false;
  }


  protected void internalTransform(Body b, String phaseName, Map options) 
  {
    if (b.getMethod().getSignature().indexOf("<clinit>") >= 0) return;
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;
    
    New2InitFlowAnalysis fa = new New2InitFlowAnalysis(new BriefUnitGraph(b));

    Vector zeroheight = new Vector();
    PatchingChain units = b.getUnits();

    Unit first = null;
    Iterator it = units.snapshotIterator();
    while (it.hasNext()) {
      Unit unit = (Unit)it.next();
      if (unit instanceof IdentityStmt)
        continue;
      first = unit;
      break;
    }
    
    it = units.snapshotIterator();
    while (it.hasNext()) {
      Unit unit = (Unit)it.next();
      if (unit instanceof IdentityStmt || checkTraps(unit,b)) 
        continue;
      // very conservative estimate about where new-<init> ranges are
      if (((FlowSet)fa.getFlowAfter(unit)).size() == 0
          && ((FlowSet)fa.getFlowBefore(unit)).size() == 0)
        zeroheight.add(unit);
    }

    if (zeroheight.size()<3) return;
    
    int idx = 0;
    Unit u = null;
    for (int i = 0; i < zeroheight.size(); i++)
    {
      idx = Rand.getInt(zeroheight.size()-1) + 1;
      u = (Unit)zeroheight.get(idx);
      if (u.fallsThrough())
        break;
      u = null;
    }
    // couldn't find a unit that fell through
    if (u == null || Rand.getInt(10) > weight) return;
    
    zeroheight.remove(idx);
    while(zeroheight.size() > (weight>3?weight:3)) {
      zeroheight.remove(Rand.getInt(zeroheight.size()));
    }
    
    Chain locals = b.getLocals();
    ArrayList targs = new ArrayList();
    targs.addAll(zeroheight);

    SootField ops[] = FieldRenamer.getRandomOpaques();

    Local b1 = Jimple.v().newLocal("addswitchesbool1", BooleanType.v());
    locals.add(b1);
    Local b2 = Jimple.v().newLocal("addswitchesbool2", BooleanType.v());
    locals.add(b2);

    if (ops[0].getType() instanceof PrimType) {
      units.insertBefore(Jimple.v().newAssignStmt(b1,Jimple.v().newStaticFieldRef(ops[0].makeRef())),u);
    } else {
      RefType rt = (RefType)ops[0].getType();
      SootMethod m = rt.getSootClass().getMethodByName("booleanValue");
      Local B = Jimple.v().newLocal("addswitchesBOOL1", rt);
      locals.add(B);
      units.insertBefore(Jimple.v().newAssignStmt(B,Jimple.v().newStaticFieldRef(ops[0].makeRef())),u);
      units.insertBefore(Jimple.v().newAssignStmt(b1,Jimple.v().newVirtualInvokeExpr(B,m.makeRef(),new ArrayList())),u);
    }
    if (ops[1].getType() instanceof PrimType) {
      units.insertBefore(Jimple.v().newAssignStmt(b2,Jimple.v().newStaticFieldRef(ops[1].makeRef())),u);
    } else {
      RefType rt = (RefType)ops[1].getType();
      SootMethod m = rt.getSootClass().getMethodByName("booleanValue");
      Local B = Jimple.v().newLocal("addswitchesBOOL2", rt);
      locals.add(B);
      units.insertBefore(Jimple.v().newAssignStmt(B,Jimple.v().newStaticFieldRef(ops[1].makeRef())),u);
      units.insertBefore(Jimple.v().newAssignStmt(b2,Jimple.v().newVirtualInvokeExpr(B,m.makeRef(),new ArrayList())),u);
    }

    IfStmt ifstmt = Jimple.v().newIfStmt(Jimple.v().newNeExpr(b1,b2),u);
    units.insertBefore(ifstmt,u);

    Local l = Jimple.v().newLocal("addswitchlocal",IntType.v());
    locals.add(l);
    units.insertBeforeNoRedirect(Jimple.v().newAssignStmt(l, IntConstant.v(0)), first);
    units.insertAfter(Jimple.v().newTableSwitchStmt(l,1,zeroheight.size(),targs,u),ifstmt);

    switchesadded += zeroheight.size() + 1; 
    
    Iterator tit = targs.iterator();
    while (tit.hasNext()) {
      Unit nxt = (Unit)tit.next();
      if (Rand.getInt(5) < 4) {
        units.insertBefore(Jimple.v().newAssignStmt(l,Jimple.v().newAddExpr(l,IntConstant.v(Rand.getInt(3)+1))), nxt);
      }
    }

    ifstmt.setTarget(u);
  }
}
