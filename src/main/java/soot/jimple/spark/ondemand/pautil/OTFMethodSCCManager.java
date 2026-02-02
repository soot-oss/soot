package soot.jimple.spark.ondemand.pautil;

import java.util.HashMap;
import java.util.Map;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Manu Sridharan
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

import java.util.Set;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.spark.ondemand.genericutil.DisjointSets;

public final class OTFMethodSCCManager {

  private DisjointSets disj;
  private Map<SootMethod, Integer> numbers = new HashMap<>();

  public OTFMethodSCCManager() {
    int num = 0;
    for (SootClass c : Scene.v().getClasses()) {
      for (SootMethod m : c.getMethods()) {
        numbers.put(m, num++);
      }
    }
    disj = new DisjointSets(num + 1);
  }

  public boolean inSameSCC(SootMethod m1, SootMethod m2) {
    return disj.find(numbers.get(m1)) == disj.find(numbers.get(m2));
  }

  public void makeSameSCC(Set<SootMethod> methods) {
    SootMethod prevMethod = null;
    for (SootMethod method : methods) {
      if (prevMethod != null) {
        int prevMethodRep = disj.find(numbers.get(prevMethod));
        int methodRep = disj.find(numbers.get(method));
        if (prevMethodRep != methodRep) {
          disj.union(prevMethodRep, methodRep);
        }
      }
      prevMethod = method;
    }
  }
}
