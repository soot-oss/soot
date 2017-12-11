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

package soot.jbco.bafTransformations;

import java.util.*;

import soot.*;
import soot.baf.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.*;
import soot.jbco.jimpleTransformations.*;

/**
 * @author Michael Batchelder
 * 
 * Created on 31-May-2006
 */
public class UpdateConstantsToFields extends BodyTransformer  implements IJbcoTransform {

  public static String dependancies[] = new String[] {"wjtp.jbco_cc","bb.jbco_ecvf", "bb.jbco_ful", "bb.lp"};
  
  public String[] getDependancies() {
    return dependancies;
  }
  public static String name = "bb.jbco_ecvf";
  
  public String getName() {
    return name;
  }
  
  static int updated = 0;
  
  public void outputSummary() {
    out.println("Updated constant references: "+updated);
  }

  protected void internalTransform(Body b, String phaseName, Map<String,String> options) {
    if (b.getMethod().getName().indexOf("<clinit>")>=0) 
      return;
    
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;
    
    PatchingChain<Unit> units = b.getUnits();
    Iterator<Unit> iter = units.snapshotIterator();
    while (iter.hasNext()) {
      Unit u = (Unit)iter.next();
      if (u instanceof PushInst) {
        SootField f = CollectConstants.constantsToFields.get(((PushInst)u).getConstant());
        if (f!=null && Rand.getInt(10) <= weight) {
            Unit get = Baf.v().newStaticGetInst(f.makeRef());
            units.insertBefore(get, u);
            BodyBuilder.updateTraps(get,u,b.getTraps());
            units.remove(u);
            updated++;
        }
      }
    }
  }  
}