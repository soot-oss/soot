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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.shimple.ShimpleBody;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

/**
 * This class implements the LocalUses interface for Shimple. ShimpleLocalUses can be used in conjunction with
 * SimpleLocalDefs to provide Definition/Use and Use/Definition chains in SSA.
 *
 * <p>
 * In addition to the interface required by LocalUses, ShimpleLocalUses also provides a method for obtaining the list of uses
 * given only the Local. Furthermore, unlike SimpleLocalUses, a LocalDefs object is not required when constructing
 * ShimpleLocalUses.
 *
 * @author Navindra Umanee
 * @see ShimpleLocalDefs
 * @see soot.toolkits.scalar.SimpleLocalDefs
 * @see soot.toolkits.scalar.SimpleLocalUses
 **/
public class ShimpleLocalUses implements LocalUses {
  private static final Logger logger = LoggerFactory.getLogger(ShimpleLocalUses.class);

  protected Map<Local, List<UnitValueBoxPair>> localToUses = new HashMap<Local, List<UnitValueBoxPair>>();

  /**
   * Build a LocalUses interface from a ShimpleBody. Proper SSA form is required, otherwise correct behaviour is not
   * guaranteed.
   **/
  public ShimpleLocalUses(ShimpleBody sb) {
    // Instead of rebuilding the ShimpleBody without the
    // programmer's knowledge, throw a RuntimeException
    if (!sb.isSSA()) {
      throw new RuntimeException("ShimpleBody is not in proper SSA form as required by ShimpleLocalUses. "
          + "You may need to rebuild it or use SimpleLocalUses instead.");
    }

    // initialise the map
    Map<Local, List<UnitValueBoxPair>> localToUsesRef = this.localToUses;
    for (Local local : sb.getLocals()) {
      localToUsesRef.put(local, new ArrayList<UnitValueBoxPair>());
    }

    // Iterate through the units and save each Local use in the appropriate list. Due
    // to SSA form, each Local has a unique def, and therefore one appropriate list.
    for (Unit unit : sb.getUnits()) {
      for (ValueBox box : unit.getUseBoxes()) {
        Value value = box.getValue();
        if (value instanceof Local) {
          localToUsesRef.get((Local) value).add(new UnitValueBoxPair(unit, box));
        }
      }
    }
  }

  /**
   * Returns all the uses of the given Local as a list of UnitValueBoxPairs, each containing a Unit that uses the local and
   * the corresponding ValueBox containing the Local.
   *
   * <p>
   * This method is currently not required by the LocalUses interface.
   **/
  public List<UnitValueBoxPair> getUsesOf(Local local) {
    List<UnitValueBoxPair> uses = localToUses.get(local);
    return (uses != null) ? uses : Collections.<UnitValueBoxPair>emptyList();
  }

  /**
   * If a Local is defined in the Unit, returns all the uses of that Local as a list of UnitValueBoxPairs, each containing a
   * Unit that uses the local and the corresponding ValueBox containing the Local.
   **/
  @Override
  public List<UnitValueBoxPair> getUsesOf(Unit unit) {
    List<ValueBox> defBoxes = unit.getDefBoxes();
    switch (defBoxes.size()) {
      case 0:
        return Collections.<UnitValueBoxPair>emptyList();
      case 1:
        Value val = defBoxes.get(0).getValue();
        if (val instanceof Local) {
          return getUsesOf((Local) val);
        } else {
          return Collections.<UnitValueBoxPair>emptyList();
        }
      default:
        logger.warn("Unit has multiple definition boxes?");
        List<UnitValueBoxPair> usesList = new ArrayList<UnitValueBoxPair>();
        for (ValueBox next : defBoxes) {
          Value def = next.getValue();
          if (def instanceof Local) {
            usesList.addAll(getUsesOf((Local) def));
          }
        }
        return usesList;
    }
  }
}
