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
import soot.Value;
import soot.ValueBox;
import soot.jimple.GroupIntPair;
import soot.options.Options;
import soot.util.Chain;
import soot.util.DeterministicHashMap;

/**
 * A BodyTransformer that attempts to minimize the number of local variables used in Body by 'reusing' them when possible.
 * Implemented as a singleton. For example the code: {@code for(int i; i < k; i++); for(int j; j < k; j++);} would be
 * transformed into: {@code for(int i; i < k; i++); for(int i; i < k; i++);} assuming no further conflicting uses of
 * {@code i} and {@code j}.
 *
 * Note: LocalSplitter corresponds to the inverse transformation.
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

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Packing locals...");
    }

    final Chain<Local> bodyLocalsRef = body.getLocals();
    final int origLocalCount = bodyLocalsRef.size();
    if (origLocalCount < 1) {
      return;
    }

    // A group represents a bunch of locals which may potentially interfere with each other.
    // Separate groups can not possibly interfere with each other (coloring say ints and doubles).
    Map<Local, Type> localToGroup = new DeterministicHashMap<Local, Type>(origLocalCount * 2 + 1, 0.7f);
    Map<Type, Integer> groupToColorCount = new HashMap<Type, Integer>(origLocalCount * 2 + 1, 0.7f);
    Map<Local, Integer> localToColor = new HashMap<Local, Integer>(origLocalCount * 2 + 1, 0.7f);

    // Assign each local to a group, and set that group's color count to 0.
    for (Local l : bodyLocalsRef) {
      Type g = l.getType();
      localToGroup.put(l, g);
      groupToColorCount.putIfAbsent(g, 0);
    }

    // Assign colors to the parameter locals.
    for (Unit s : body.getUnits()) {
      if (s instanceof IdentityUnit) {
        Value leftOp = ((IdentityUnit) s).getLeftOp();
        if (leftOp instanceof Local) {
          Local l = (Local) leftOp;

          Type group = localToGroup.get(l);
          Integer count = groupToColorCount.get(group);
          localToColor.put(l, count);
          groupToColorCount.put(group, count + 1);
        }
      }
    }

    // Call the graph colorer.
    if (PhaseOptions.getBoolean(options, "unsplit-original-locals")) {
      FastColorer.unsplitAssignColorsToLocals(body, localToGroup, localToColor, groupToColorCount);
    } else {
      FastColorer.assignColorsToLocals(body, localToGroup, localToColor, groupToColorCount);
    }

    // Map each local to a new local.
    Map<Local, Local> localToNewLocal = new HashMap<Local, Local>(origLocalCount * 2 + 1, 0.7f);
    {
      Map<GroupIntPair, Local> groupIntToLocal = new HashMap<GroupIntPair, Local>(origLocalCount * 2 + 1, 0.7f);
      List<Local> originalLocals = new ArrayList<Local>(bodyLocalsRef);
      bodyLocalsRef.clear();

      final Set<String> usedLocalNames = new HashSet<>();
      for (Local original : originalLocals) {
        Type group = localToGroup.get(original);
        GroupIntPair pair = new GroupIntPair(group, localToColor.get(original));

        Local newLocal = groupIntToLocal.get(pair);
        if (newLocal == null) {
          newLocal = (Local) original.clone();
          newLocal.setType(group);

          // Added 'usedLocalNames' for distinct naming.
          // Icky fix. But I guess it works. -PL
          // It is no substitute for really understanding the
          // problem, though. I'll leave that to someone
          // who really understands the local naming stuff.
          // Does such a person exist?
          //
          // I'll just leave this comment as folklore for future
          // generations. The problem with it is that you can end up
          // with different locals that share the same name which can
          // lead to all sorts of funny results. (SA, 2017-03-02)
          //
          // If we have a split local, let's find a better name for it
          String name = newLocal.getName();
          if (name != null) {
            int signIndex = name.indexOf('#');
            if (signIndex >= 0) {
              String newName = name.substring(0, signIndex);
              if (usedLocalNames.add(newName)) {
                newLocal.setName(newName);
              } else {
                // just leave it alone for now
              }
            } else {
              usedLocalNames.add(name);

            }
          }

          groupIntToLocal.put(pair, newLocal);
          bodyLocalsRef.add(newLocal);
        }

        localToNewLocal.put(original, newLocal);
      }
    }

    // Go through all valueBoxes of this method and perform changes
    for (Unit s : body.getUnits()) {
      for (ValueBox box : s.getUseBoxes()) {
        Value val = box.getValue();
        if (val instanceof Local) {
          box.setValue(localToNewLocal.get((Local) val));
        }
      }
      for (ValueBox box : s.getDefBoxes()) {
        Value val = box.getValue();
        if (val instanceof Local) {
          box.setValue(localToNewLocal.get((Local) val));
        }
      }
    }
  }
}
