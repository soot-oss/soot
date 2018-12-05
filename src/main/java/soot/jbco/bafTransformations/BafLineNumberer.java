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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.PatchingChain;
import soot.Unit;
import soot.baf.IdentityInst;
import soot.baf.Inst;
import soot.jbco.IJbcoTransform;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;

public class BafLineNumberer extends BodyTransformer implements IJbcoTransform {

  public static String name = "bb.jbco_bln";

  public void outputSummary() {
  }

  public String[] getDependencies() {
    return new String[] { name };
  }

  public String getName() {
    return name;
  }

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    int idx = 0;
    PatchingChain<Unit> units = b.getUnits();
    Iterator<Unit> it = units.iterator();
    while (it.hasNext()) {
      Inst i = (Inst) it.next();
      List<Tag> tags = i.getTags();
      for (int k = 0; k < tags.size(); k++) {
        Tag t = (Tag) tags.get(k);
        if (t instanceof LineNumberTag) {
          tags.remove(k);
          break;
        }
      }
      if (i instanceof IdentityInst) {
        continue;
      }
      i.addTag(new LineNumberTag(idx++));
    }
  }
}