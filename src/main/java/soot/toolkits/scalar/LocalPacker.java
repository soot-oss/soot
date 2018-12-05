package soot.toolkits.scalar;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.IdentityUnit;
import soot.Local;
import soot.PhaseOptions;
import soot.Singletons;
import soot.Type;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.GroupIntPair;
import soot.options.Options;
import soot.util.DeterministicHashMap;

/**
 * A BodyTransformer that attemps to minimize the number of local variables used in Body by 'reusing' them when possible.
 * Implemented as a singleton. For example the code:
 *
 * for(int i; i < k; i++); for(int j; j < k; j++);
 *
 * would be transformed into: for(int i; i < k; i++); for(int i; i < k; i++);
 *
 * assuming to further conflicting uses of i and j.
 *
 * Note: LocalSplitter is corresponds to the inverse transformation.
 *
 * @see BodyTransformer
 * @see Body
 * @see LocalSplitter
 */
public class LocalPacker extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(LocalPacker.class);

  public LocalPacker(Singletons.Global g) {
  }

  public static LocalPacker v() {
    return G.v().soot_toolkits_scalar_LocalPacker();
  }

  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    boolean isUnsplit = PhaseOptions.getBoolean(options, "unsplit-original-locals");

    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Packing locals...");
    }

    Map<Local, Object> localToGroup = new DeterministicHashMap<Local, Object>(body.getLocalCount() * 2 + 1, 0.7f);
    // A group represents a bunch of locals which may potentially interfere
    // with each other
    // 2 separate groups can not possibly interfere with each other
    // (coloring say ints and doubles)

    Map<Object, Integer> groupToColorCount = new HashMap<Object, Integer>(body.getLocalCount() * 2 + 1, 0.7f);
    Map<Local, Integer> localToColor = new HashMap<Local, Integer>(body.getLocalCount() * 2 + 1, 0.7f);
    Map<Local, Local> localToNewLocal;

    // Assign each local to a group, and set that group's color count to 0.
    {
      for (Local l : body.getLocals()) {
        Type g = l.getType();

        localToGroup.put(l, g);

        if (!groupToColorCount.containsKey(g)) {
          groupToColorCount.put(g, 0);
        }
      }
    }

    // Assign colors to the parameter locals.
    {
      for (Unit s : body.getUnits()) {
        if (s instanceof IdentityUnit && ((IdentityUnit) s).getLeftOp() instanceof Local) {
          Local l = (Local) ((IdentityUnit) s).getLeftOp();

          Object group = localToGroup.get(l);
          int count = groupToColorCount.get(group).intValue();

          localToColor.put(l, new Integer(count));

          count++;

          groupToColorCount.put(group, new Integer(count));
        }
      }
    }

    // Call the graph colorer.
    if (isUnsplit) {
      FastColorer.unsplitAssignColorsToLocals(body, localToGroup, localToColor, groupToColorCount);
    } else {
      FastColorer.assignColorsToLocals(body, localToGroup, localToColor, groupToColorCount);
    }

    // Map each local to a new local.
    {
      List<Local> originalLocals = new ArrayList<Local>(body.getLocals());
      localToNewLocal = new HashMap<Local, Local>(body.getLocalCount() * 2 + 1, 0.7f);
      Map<GroupIntPair, Local> groupIntToLocal = new HashMap<GroupIntPair, Local>(body.getLocalCount() * 2 + 1, 0.7f);

      body.getLocals().clear();

      Set<String> usedLocalNames = new HashSet<>();
      for (Local original : originalLocals) {
        Object group = localToGroup.get(original);
        int color = localToColor.get(original).intValue();
        GroupIntPair pair = new GroupIntPair(group, color);

        Local newLocal;

        if (groupIntToLocal.containsKey(pair)) {
          newLocal = (Local) groupIntToLocal.get(pair);
        } else {
          newLocal = (Local) original.clone();
          newLocal.setType((Type) group);

          // Icky fix. But I guess it works. -PL
          // It is no substitute for really understanding the
          // problem, though. I'll leave that to someone
          // who really understands the local naming stuff.
          // Does such a person exist?

          // I'll just leave this comment as folklore for future
          // generations. The problem with it is that you can end up
          // with different locals that share the same name which can
          // lead to all sorts of funny results. (SA, 2017-03-02)

          // int signIndex = newLocal.getName().indexOf("#");
          // if (signIndex != -1)
          // newLocal.setName(newLocal.getName().substring(0, signIndex));

          // If we have a split local, let's find a better name for it
          int signIndex = newLocal.getName().indexOf("#");
          if (signIndex != -1) {
            String newName = newLocal.getName().substring(0, signIndex);
            if (usedLocalNames.add(newName)) {
              newLocal.setName(newName);
            } else {
              // just leave it alone for now
            }
          }

          groupIntToLocal.put(pair, newLocal);
          body.getLocals().add(newLocal);
        }

        localToNewLocal.put(original, newLocal);
      }
    }

    // Go through all valueBoxes of this method and perform changes
    {
      for (Unit s : body.getUnits()) {
        for (ValueBox box : s.getUseBoxes()) {
          if (box.getValue() instanceof Local) {
            Local l = (Local) box.getValue();
            box.setValue((Local) localToNewLocal.get(l));
          }
        }
        for (ValueBox box : s.getDefBoxes()) {
          if (box.getValue() instanceof Local) {
            Local l = (Local) box.getValue();
            box.setValue((Local) localToNewLocal.get(l));
          }
        }
      }
    }
  }
}
