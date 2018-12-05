package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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
import java.util.Map;

import soot.G;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;

/** The aggregator for LineNumberTable attribute. */
public class InnerClassTagAggregator extends SceneTransformer {

  public InnerClassTagAggregator(Singletons.Global g) {
  }

  public static InnerClassTagAggregator v() {
    return G.v().soot_tagkit_InnerClassTagAggregator();
  }

  public String aggregatedName() {
    return "InnerClasses";
  }

  public void internalTransform(String phaseName, Map<String, String> options) {
    Iterator<SootClass> it = Scene.v().getApplicationClasses().iterator();
    while (it.hasNext()) {
      ArrayList<InnerClassTag> list = new ArrayList<InnerClassTag>();
      SootClass nextSc = it.next();
      for (Tag t : nextSc.getTags()) {
        if (t instanceof InnerClassTag) {
          list.add((InnerClassTag) t);
        }
      }
      if (!list.isEmpty()) {
        nextSc.addTag(new InnerClassAttribute(list));
      }
    }
  }
}
