package soot.shimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.shimple.ShimpleBody;
import soot.toolkits.scalar.LocalDefs;
import soot.util.Chain;

/**
 * This class implements the LocalDefs interface for Shimple. ShimpleLocalDefs can be used in conjunction with
 * SimpleLocalUses to provide Definition/Use and Use/Definition chains in SSA.
 *
 * <p>
 * This implementation can be considered a small demo for how SSA can be put to good use since it is much simpler than
 * soot.toolkits.scalar.SimpleLocalDefs. Shimple can often be treated as Jimple with the added benefits of SSA assumptions.
 *
 * <p>
 * In addition to the interface required by LocalDefs, ShimpleLocalDefs also provides a method for obtaining the definition
 * Unit given only the Local.
 *
 * @author Navindra Umanee
 * @see ShimpleLocalUses
 * @see soot.toolkits.scalar.SimpleLocalDefs
 * @see soot.toolkits.scalar.SimpleLocalUses
 **/
public class ShimpleLocalDefs implements LocalDefs {

  protected Map<Value, List<Unit>> localToDefs;

  /**
   * Build a LocalDefs interface from a ShimpleBody. Proper SSA form is required, otherwise correct behaviour is not
   * guaranteed.
   **/
  public ShimpleLocalDefs(ShimpleBody sb) {
    // Instead of rebuilding the ShimpleBody without the
    // programmer's knowledge, throw a RuntimeException
    if (!sb.isSSA()) {
      throw new RuntimeException("ShimpleBody is not in proper SSA form as required by ShimpleLocalDefs. "
          + "You may need to rebuild it or use SimpleLocalDefs instead.");
    }

    // build localToDefs map simply by iterating through all the
    // units in the body and saving the unique definition site for
    // each local -- no need for fancy analysis
    Chain<Unit> unitsChain = sb.getUnits();
    this.localToDefs = new HashMap<Value, List<Unit>>(unitsChain.size() * 2 + 1, 0.7f);
    for (Unit unit : unitsChain) {
      for (ValueBox vb : unit.getDefBoxes()) {
        Value value = vb.getValue();
        // only map locals
        if (value instanceof Local) {
          localToDefs.put(value, Collections.<Unit>singletonList(unit));
        }
      }
    }
  }

  /**
   * Unconditionally returns the definition site of a local (as a singleton list).
   **/
  @Override
  public List<Unit> getDefsOf(Local l) {
    List<Unit> defs = localToDefs.get(l);
    if (defs == null) {
      throw new RuntimeException("Local not found in Body.");
    }
    return defs;
  }

  /**
   * Returns the definition site for a Local at a certain point (Unit) in a method as a singleton list.
   *
   * @param l
   *          the Local in question.
   * @param s
   *          a unit that specifies the method context (location) to query for the definitions of the Local.
   * @return a singleton list containing the definition site.
   **/
  @Override
  public List<Unit> getDefsOfAt(Local l, Unit s) {
    // For consistency with SimpleLocalDefs, check that the local is indeed used
    // in the given Unit. This neatly sidesteps the problem of checking whether
    // the local is actually defined at the given point in the program.
    {
      boolean defined = false;
      for (ValueBox vb : s.getUseBoxes()) {
        if (vb.getValue().equals(l)) {
          defined = true;
          break;
        }
      }
      if (!defined) {
        throw new RuntimeException("Illegal LocalDefs query; local " + l + " is not being used at " + s);
      }
    }

    return getDefsOf(l);
  }
}
