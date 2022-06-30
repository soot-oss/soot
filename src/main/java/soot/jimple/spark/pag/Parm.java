package soot.jimple.spark.pag;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import soot.G;
import soot.PointsToAnalysis;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.toolkits.scalar.Pair;

/**
 * Represents a method parameter.
 * 
 * @author Ondrej Lhotak
 */
public class Parm implements SparkField {
  private final int index;
  private final SootMethod method;

  private Parm(SootMethod m, int i) {
    index = i;
    method = m;
    Scene.v().getFieldNumberer().add(this);
  }

  public static Parm v(SootMethod m, int index) {
    Pair<SootMethod, Integer> p = new Pair<SootMethod, Integer>(m, new Integer(index));
    Parm ret = (Parm) G.v().Parm_pairToElement.get(p);
    if (ret == null) {
      G.v().Parm_pairToElement.put(p, ret = new Parm(m, index));
    }
    return ret;
  }

  public static final void delete() {
    G.v().Parm_pairToElement = null;
  }

  public String toString() {
    return "Parm " + index + " to " + method;
  }

  public final int getNumber() {
    return number;
  }

  public final void setNumber(int number) {
    this.number = number;
  }

  public int getIndex() {
    return index;
  }

  public Type getType() {
    if (index == PointsToAnalysis.RETURN_NODE) {
      return method.getReturnType();
    }

    return method.getParameterType(index);
  }

  private int number = 0;
}
