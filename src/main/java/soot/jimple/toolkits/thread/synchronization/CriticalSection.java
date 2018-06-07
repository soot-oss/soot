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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import soot.EquivalentValue;
import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.toolkits.pointer.CodeBlockRWSet;

class CriticalSection extends SynchronizedRegion {
  public static int nextIDNum = 1;

  // Information about the transactional region
  public int IDNum;
  public int nestLevel;
  public String name;

  public Value origLock;
  public CodeBlockRWSet read, write;
  public HashSet<Unit> invokes;
  public HashSet<Unit> units;
  public HashMap<Unit, CodeBlockRWSet> unitToRWSet;
  public HashMap<Unit, List> unitToUses; // For lockset analysis
  public boolean wholeMethod;

  // Information for analyzing conflicts with other transactions
  public SootMethod method;
  public int setNumber; // used for breaking the list of transactions into sets
  public CriticalSectionGroup group;
  public HashSet<CriticalSectionDataDependency> edges;
  public HashSet<Unit> waits;
  public HashSet<Unit> notifys;
  public HashSet<MethodOrMethodContext> transitiveTargets;

  // Locking Information
  public Value lockObject;
  public Value lockObjectArrayIndex;
  public List<EquivalentValue> lockset;

  CriticalSection(boolean wholeMethod, SootMethod method, int nestLevel) {
    super();
    this.IDNum = nextIDNum;
    nextIDNum++;
    this.nestLevel = nestLevel;
    this.read = new CodeBlockRWSet();
    this.write = new CodeBlockRWSet();
    this.invokes = new HashSet<Unit>();
    this.units = new HashSet<Unit>();
    this.unitToRWSet = new HashMap<Unit, CodeBlockRWSet>();
    this.unitToUses = new HashMap<Unit, List>();
    this.wholeMethod = wholeMethod;
    this.method = method;
    this.setNumber = 0; // 0 = no group, -1 = DELETE
    this.group = null;
    this.edges = new HashSet<CriticalSectionDataDependency>();
    this.waits = new HashSet<Unit>();
    this.notifys = new HashSet<Unit>();
    this.transitiveTargets = null;
    this.lockObject = null;
    this.lockObjectArrayIndex = null;
    this.lockset = null;
  }

  CriticalSection(CriticalSection tn) {
    super(tn);
    this.IDNum = tn.IDNum;
    this.nestLevel = tn.nestLevel;
    this.origLock = tn.origLock;
    this.read = new CodeBlockRWSet();
    this.read.union(tn.read);
    this.write = new CodeBlockRWSet();
    this.write.union(tn.write);
    this.invokes = (HashSet<Unit>) tn.invokes.clone();
    this.units = (HashSet<Unit>) tn.units.clone();
    this.unitToRWSet = (HashMap<Unit, CodeBlockRWSet>) tn.unitToRWSet.clone();
    this.unitToUses = (HashMap<Unit, List>) tn.unitToUses.clone();
    this.wholeMethod = tn.wholeMethod;
    this.method = tn.method;
    this.setNumber = tn.setNumber;
    this.group = tn.group;
    this.edges = (HashSet<CriticalSectionDataDependency>) tn.edges.clone();
    this.waits = (HashSet<Unit>) tn.waits.clone();
    this.notifys = (HashSet<Unit>) tn.notifys.clone();
    this.transitiveTargets
        = (HashSet<MethodOrMethodContext>) (tn.transitiveTargets == null ? null : tn.transitiveTargets.clone());
    this.lockObject = tn.lockObject;
    this.lockObjectArrayIndex = tn.lockObjectArrayIndex;
    this.lockset = tn.lockset;
  }

  protected Object clone() {
    return new CriticalSection(this);
  }

  public String toString() {
    return name;
  }
}
