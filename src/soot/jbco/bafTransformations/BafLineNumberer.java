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

import soot.jbco.IJbcoTransform;
import java.util.*;
import soot.tagkit.*;
import soot.*;
import soot.baf.*;
import soot.jbco.util.*;
import soot.util.*;
import soot.jimple.Stmt;

public class BafLineNumberer extends BodyTransformer implements IJbcoTransform {
  public void outputSummary() {}
  public String[] getDependancies() { return new String[]{"bb.jbco_bln"};}
  public String getName() { return "bb.jbco_bln";}
  protected void internalTransform(Body b, String phaseName, Map options) {
    int idx = 0;
    PatchingChain units = b.getUnits();
    Iterator it = units.iterator();
    while (it.hasNext()) {
      Inst i  = (Inst)it.next();
      List tags = i.getTags();
      for (int k = 0; k < tags.size(); k++) {
        Tag t = (Tag)tags.get(k);
        if (t instanceof LineNumberTag) {
            tags.remove(k);
            break;
        }
      }
      if (i instanceof IdentityInst)
        continue;
      i.addTag(new LineNumberTag(idx++));    
    }
  }
}