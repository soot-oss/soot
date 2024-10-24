package soot.jimple.toolkits.typing.fast;

import java.util.ArrayList;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2008 Ben Bellamy 
 * 
 * All rights reserved.
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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.Type;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/**
 * The default typing strategy
 */
public class DefaultTypingStrategy implements ITypingStrategy {
  private static final Logger logger = LoggerFactory.getLogger(DefaultTypingStrategy.class);

  public static final ITypingStrategy INSTANCE = new DefaultTypingStrategy();

  public static boolean MINIMIZING_ENABLED = true;
  public static int USE_PARALLEL_MINIMIZE_IF_ENTRIES_MORE_THAN = 1000;

  @Override
  public ITyping createEmptyTyping(Collection<Local> locals) {
    return new MapTyping(locals);
  }

  public static MultiMap<Local, Type> getFlatNonConstantTyping(List<ITyping> tgs) {
    MultiMap<Local, Type> map = new HashMultiMap<>();
    for (ITyping tg : tgs) {
      if (tg instanceof PartialConstantTyping) {
        PartialConstantTyping t = (PartialConstantTyping) tg;
        map.putMap(t.getNonConstantTypings());
      } else {
        map.putMap(tg.getMap());
      }
    }
    return map;
  }

  public static Set<Local> getUseTypingsForLocals(List<ITyping> tgs, IHierarchy h) {
    Set<Type> objectLikeTypeSet = new HashSet<>();
    objectLikeTypeSet.add(Scene.v().getObjectType());
    objectLikeTypeSet.add(RefType.v("java.io.Serializable"));
    objectLikeTypeSet.add(RefType.v("java.lang.Cloneable"));

    MultiMap<Local, Type> ft = getFlatNonConstantTyping(tgs);
    Set<Local> localsToCheck = new HashSet<>(ft.keySet());
    for (Local l : ft.keySet()) {
      Set<Type> typings = ft.get(l);
      if (typings.size() == 1) {
        //all typings agree on that type for that local
        localsToCheck.remove(l);
        continue;
      }
      if (objectLikeTypeSet.equals(typings)) {
        localsToCheck.remove(l);
      }
      Type[] types = new Type[typings.size()];
      typings.toArray(types);
      boolean allNotComparable = true;
      outer: for (int x = 0; x < types.length; x++) {
        Type ta = types[x];
        for (int y = x + 1; y < types.length; y++) {
          Type tb = types[y];
          if (TypeResolver.typesEqual(ta, tb)) {
            throw new AssertionError("Should not happen: " + ta + " == " + tb);
          } else if (h.ancestor(ta, tb) || h.ancestor(tb, ta)) {
            allNotComparable = false;
            break outer;
          }
        }
      }
      if (allNotComparable) {
        //we can't compare any of two types to each other, so why bother?
        localsToCheck.remove(l);
      }

    }
    return localsToCheck;
  }

  @Override
  public void minimize(List<ITyping> tgs, IHierarchy h) {
    if (!MINIMIZING_ENABLED) {
      return;
    }

    if (tgs.size() > USE_PARALLEL_MINIMIZE_IF_ENTRIES_MORE_THAN) {
      minimizeParallel(tgs, h);
      return;
    }

    minimizeSequential(tgs, h);
  }

  public void minimizeSequential(List<ITyping> tgs, IHierarchy h) {
    // int count = 0;
    // int tgsSize = tgs.size();
    Set<Local> test = getUseTypingsForLocals(tgs, h);
    if (test.isEmpty()) {
      return;
    }
    OUTER: for (ListIterator<ITyping> i = tgs.listIterator(); i.hasNext();) {
      ITyping tgi = i.next();
      // count++;
      // if (count % 500 == 0) {
      // logger.info("{} of {} = {}%", count, tgsSize, 100f * count / tgsSize);
      // }
      if (tgi == null) {
        // element is marked to be deleted, here we can finally remove it
        i.remove();
        continue;
      }

      // Throw out duplicate typings
      ListIterator<ITyping> j = tgs.listIterator(i.nextIndex());
      while (j.hasNext()) {
        ITyping tgj = j.next();
        if (tgj == null) {
          continue; // element is marked to be deleted
        }
        int comp = compare(tgi, tgj, h, test);
        if (comp == 1) {
          // if compare = 1, then tgi is the more general typing
          // We shouldn't pick that one as we would then end up
          // with lots of locals typed to Serializable etc.
          i.remove();
          continue OUTER;
        } else if (comp == -1) {
          // if compare == -1, then tgj is the more general typing
          // Set it to null as workaround for marking it as deleted.
          // We can not remove the element here as this would cause a
          // ConcurrentModificationException in the outer list iterator.
          j.set(null);
        }
      }
    }
  }

  public int compare(ITyping a, ITyping b, IHierarchy h, Collection<Local> localsCheck) {
    int r = 0;
    for (Local v : localsCheck) {
      Type ta = a.get(v), tb = b.get(v);

      int cmp;
      if (TypeResolver.typesEqual(ta, tb)) {
        cmp = 0;
      } else if (h.ancestor(ta, tb)) {
        cmp = 1;
        if (r == -1) {
          return 2;
        }
      } else if (h.ancestor(tb, ta)) {
        cmp = -1;
        if (r == 1) {
          return 2;
        }
      } else {
        return -2;
      }
      if (r == 0) {
        r = cmp;
      }
    }
    return r;
  }

  public void minimizeParallel(List<ITyping> tgs, IHierarchy h) {
    logger.debug("Performing parallel minimization");
    Set<Local> test = getUseTypingsForLocals(tgs, h);
    if (test.isEmpty()) {
      return;
    }

    // We don't know what type of list we get, we need a list that is thread safe for get/set
    // values. (get could return stale values, but this would not cause harm)
    ArrayList<ITyping> workList = new ArrayList<>(tgs);
    final AtomicInteger processed = new AtomicInteger();
    final int tgsSize = tgs.size();

    // We iterate over the list using the list item index.
    // This way we have something like a parallel list iterator.
    // The only disadvantage is that we can not delete items from the list.
    // As workaround we replace those entries with a null value (mark them for deletion).
    IntStream.range(0, tgsSize).parallel().forEach(i -> {
      int count = processed.incrementAndGet();
      if (count % 1000 == 0) {
        logger.debug("minimizing {} = {}%", count, (100f * count) / tgsSize);
      }
      ITyping tgi = workList.get(i);
      if (tgi == null) {
        return;
      }
      ListIterator<ITyping> j = workList.listIterator(i + 1);
      while (j.hasNext()) {
        ITyping tgj = j.next();
        if (tgj == null) {
          continue;
        }
        int comp = compare(tgi, tgj, h, test);
        if (comp == 1) {
          // if compare = 1, then tgi is the more general typing
          // We shouldn't pick that one as we would then end up
          // with lots of locals typed to Serializable etc.
          // Set it to null as workaround for marking it as deleted.
          workList.set(i, null);
          return;
        } else if (comp == -1) {
          // if compare == -1, then tgj is the more general typing
          // Set it to null as workaround for marking it as deleted.
          j.set(null);
        }
      }
    });
    // remove all null entries (entries marked for deletion)
    for (int i = tgsSize - 1; i >= 0; i--) {
      if (workList.get(i) == null) {
        tgs.remove(i);
      }
    }

    int diff = tgsSize - tgs.size();
    if (diff > 0) {
      logger.debug("Minimizing has removed {} of {} typing", diff, tgsSize);
    }
  }

  @Override
  public void finalizeTypes(ITyping tp) {
    for (Local l : tp.getAllLocals()) {
      Type t = tp.get(l);
      if (!t.isAllowedInFinalCode()) {
        tp.set(l, t.getDefaultFinalType());
      }
    }
  }

}
