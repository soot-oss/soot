package soot.dava.toolkits.base.renamer;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import soot.Local;

public class heuristicSet {
  HashMap<Local, heuristicTuple> set;

  public heuristicSet() {
    set = new HashMap<Local, heuristicTuple>();
  }

  private heuristicTuple getTuple(Local var) {
    return set.get(var);
  }

  public void add(Local var, int bits) {
    heuristicTuple temp = new heuristicTuple(bits);
    set.put(var, temp);
  }

  public void addCastString(Local var, String castString) {
    heuristicTuple retrieved = getTuple(var);
    retrieved.addCastString(castString);
  }

  public List<String> getCastStrings(Local var) {
    heuristicTuple retrieved = getTuple(var);
    return retrieved.getCastStrings();
  }

  public void setFieldName(Local var, String fieldName) {
    heuristicTuple retrieved = getTuple(var);
    retrieved.setFieldName(fieldName);
  }

  public List<String> getFieldName(Local var) {
    heuristicTuple retrieved = getTuple(var);
    return retrieved.getFieldName();
  }

  public void setObjectClassName(Local var, String objectClassName) {
    heuristicTuple retrieved = getTuple(var);
    retrieved.setObjectClassName(objectClassName);
  }

  public List<String> getObjectClassName(Local var) {
    heuristicTuple retrieved = getTuple(var);
    return retrieved.getObjectClassName();
  }

  public void setMethodName(Local var, String methodName) {
    heuristicTuple retrieved = getTuple(var);
    retrieved.setMethodName(methodName);
  }

  public List<String> getMethodName(Local var) {
    heuristicTuple retrieved = getTuple(var);
    return retrieved.getMethodName();
  }

  public void setHeuristic(Local var, int bitIndex) {
    heuristicTuple retrieved = getTuple(var);
    retrieved.setHeuristic(bitIndex);
  }

  public boolean getHeuristic(Local var, int bitIndex) {
    heuristicTuple retrieved = getTuple(var);
    return retrieved.getHeuristic(bitIndex);
  }

  public boolean isAnyHeuristicSet(Local var) {
    heuristicTuple retrieved = getTuple(var);
    return retrieved.isAnyHeuristicSet();
  }

  public void print() {
    Iterator<Local> it = set.keySet().iterator();
    while (it.hasNext()) {
      Object local = it.next();
      heuristicTuple temp = set.get(local);
      String tuple = temp.getPrint();
      System.out.println(local + "  " + tuple + " DefinedType: " + ((Local) local).getType());
    }
  }

  public Iterator<Local> getLocalsIterator() {
    return set.keySet().iterator();
  }

  public boolean contains(Local var) {
    if (set.get(var) != null) {
      return true;
    } else {
      return false;
    }
  }
}