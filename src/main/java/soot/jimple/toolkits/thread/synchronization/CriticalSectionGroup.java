package soot.jimple.toolkits.thread.synchronization;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import java.util.List;

import soot.Value;
import soot.jimple.toolkits.pointer.CodeBlockRWSet;
import soot.jimple.toolkits.pointer.RWSet;

class CriticalSectionGroup implements Iterable<CriticalSection> {
  int groupNum;

  // Information about the group members
  List<CriticalSection> criticalSections;

  // Group read/write set
  RWSet rwSet;

  // Information about the selected lock(s)
  public boolean isDynamicLock; // is lockObject actually dynamic? or is it a static ref?
  public boolean useDynamicLock; // use one dynamic lock per tn
  public Value lockObject;
  public boolean useLocksets;

  public CriticalSectionGroup(int groupNum) {
    this.groupNum = groupNum;
    this.criticalSections = new ArrayList<CriticalSection>();
    this.rwSet = new CodeBlockRWSet();

    this.isDynamicLock = false;
    this.useDynamicLock = false;
    this.lockObject = null;
    this.useLocksets = false;
  }

  public int num() {
    return groupNum;
  }

  public int size() {
    return criticalSections.size();
  }

  public void add(CriticalSection tn) {
    tn.setNumber = groupNum;
    tn.group = this;
    if (!criticalSections.contains(tn)) {
      criticalSections.add(tn);
    }
  }

  public boolean contains(CriticalSection tn) {
    return criticalSections.contains(tn);
  }

  public Iterator<CriticalSection> iterator() {
    return criticalSections.iterator();
  }

  public void mergeGroups(CriticalSectionGroup other) {
    if (other == this) {
      return;
    }

    Iterator<CriticalSection> tnIt = other.criticalSections.iterator();
    while (tnIt.hasNext()) {
      CriticalSection tn = tnIt.next();
      add(tn);
    }
    other.criticalSections.clear();
  }
}
