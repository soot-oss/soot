package soot.dexpler;

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
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.toolkits.scalar.LocalDefs;

/**
 * Simplistic caching, flow-insensitive def/use analysis
 *
 * @author Steven Arzt
 *
 */
public class DexDefUseAnalysis implements LocalDefs {

  private final Body body;
  private Map<Local, Set<Unit>> localToUses = new HashMap<Local, Set<Unit>>();
  private Map<Local, Set<Unit>> localToDefs = new HashMap<Local, Set<Unit>>();
  private Map<Local, Set<Unit>> localToDefsWithAliases = new HashMap<Local, Set<Unit>>();

  protected BitSet[] localToDefsBits;
  protected BitSet[] localToUsesBits;
  protected Map<Local, Integer> localToNumber = new HashMap<>();
  protected List<Unit> unitList;

  public DexDefUseAnalysis(Body body) {
    this.body = body;

    initialize();
  }

  protected void initialize() {
    int lastLocalNumber = 0;
    for (Local l : body.getLocals()) {
      localToNumber.put(l, lastLocalNumber++);
    }

    localToDefsBits = new BitSet[body.getLocalCount()];
    localToUsesBits = new BitSet[body.getLocalCount()];

    unitList = new ArrayList<>(body.getUnits());
    for (int i = 0; i < unitList.size(); i++) {
      Unit u = unitList.get(i);

      // Record the definitions
      if (u instanceof DefinitionStmt) {
        Value val = ((DefinitionStmt) u).getLeftOp();
        if (val instanceof Local) {
          final int localIdx = localToNumber.get(val);
          BitSet bs = localToDefsBits[localIdx];
          if (bs == null) {
            bs = new BitSet();
            localToDefsBits[localIdx] = bs;
          }
          bs.set(i);
        }
      }

      // Record the uses
      for (ValueBox vb : u.getUseBoxes()) {
        Value val = vb.getValue();
        if (val instanceof Local) {
          final int localIdx = localToNumber.get(val);
          BitSet bs = localToUsesBits[localIdx];
          if (bs == null) {
            bs = new BitSet();
            localToUsesBits[localIdx] = bs;
          }
          bs.set(i);
        }
      }
    }
  }

  public Set<Unit> getUsesOf(Local l) {
    Set<Unit> uses = localToUses.get(l);
    if (uses == null) {
      uses = new HashSet<>();
      BitSet bs = localToUsesBits[localToNumber.get(l)];
      if (bs != null) {
        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
          uses.add(unitList.get(i));
        }
      }
      localToUses.put(l, uses);
    }
    return uses;
  }

  /**
   * Collect definitions of l in body including the definitions of aliases of l. This analysis exploits that the problem is
   * flow-insensitive anyway.
   *
   * In this context an alias is a local that propagates its value to l.
   *
   * @param l
   *          the local whose definitions are to collect
   */
  protected Set<Unit> collectDefinitionsWithAliases(Local l) {
    Set<Unit> defs = localToDefsWithAliases.get(l);
    if (defs == null) {
      Set<Local> seenLocals = new HashSet<Local>();
      defs = new HashSet<Unit>();

      List<Local> newLocals = new ArrayList<Local>();
      newLocals.add(l);

      while (!newLocals.isEmpty()) {
        Local curLocal = newLocals.remove(0);

        // Definition of l?
        BitSet bsDefs = localToDefsBits[localToNumber.get(curLocal)];
        if (bsDefs != null) {
          for (int i = bsDefs.nextSetBit(0); i >= 0; i = bsDefs.nextSetBit(i + 1)) {
            Unit u = unitList.get(i);
            defs.add(u);

            DefinitionStmt defStmt = (DefinitionStmt) u;
            if (defStmt.getRightOp() instanceof Local && seenLocals.add((Local) defStmt.getRightOp())) {
              newLocals.add((Local) defStmt.getRightOp());
            }
          }
        }

        // Use of l?
        BitSet bsUses = localToUsesBits[localToNumber.get(curLocal)];
        if (bsUses != null) {
          for (int i = bsUses.nextSetBit(0); i >= 0; i = bsUses.nextSetBit(i + 1)) {
            Unit use = unitList.get(i);
            if (use instanceof AssignStmt) {
              AssignStmt assignUse = (AssignStmt) use;
              if (assignUse.getRightOp() == curLocal && assignUse.getLeftOp() instanceof Local
                  && seenLocals.add((Local) assignUse.getLeftOp())) {
                newLocals.add((Local) assignUse.getLeftOp());
              }
            }
          }
        }
      }
      localToDefsWithAliases.put(l, defs);
    }

    return defs;
  }

  @Override
  public List<Unit> getDefsOfAt(Local l, Unit s) {
    return getDefsOf(l);
  }

  @Override
  public List<Unit> getDefsOf(Local l) {
    Set<Unit> defs = localToDefs.get(l);
    if (defs == null) {
      defs = new HashSet<>();
      BitSet bs = localToDefsBits[localToNumber.get(l)];
      if (bs != null) {
        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
          Unit u = unitList.get(i);
          if (u instanceof DefinitionStmt) {
            if (((DefinitionStmt) u).getLeftOp() == l) {
              defs.add(u);
            }
          }
        }
      }
      localToDefs.put(l, defs);
    }
    return new ArrayList<>(defs);
  }

}
