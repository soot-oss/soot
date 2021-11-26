package soot.jimple.toolkits.typing.fast;

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

import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.Type;
import soot.util.Chain;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/**
 * The default typing strategy
 */
public class DefaultTypingStrategy implements ITypingStrategy {

  public static final ITypingStrategy INSTANCE = new DefaultTypingStrategy();

  @Override
  public Typing createTyping(Chain<Local> locals) {
    return new Typing(locals);
  }

  @Override
  public Typing createTyping(Typing tg) {
    return new Typing(tg);
  }

  public static MultiMap<Local, Type> getFlatTyping(List<Typing> tgs) {
    MultiMap<Local, Type> map = new HashMultiMap<>();
    for (Typing tg : tgs) {
      map.putMap(tg.map);
    }
    return map;
  }

  public static Set<Local> getObjectLikeTypings(List<Typing> tgs) {
    Set<Type> objectLikeTypeSet = new HashSet<>();
    objectLikeTypeSet.add(Scene.v().getObjectType());
    objectLikeTypeSet.add(RefType.v("java.io.Serializable"));
    objectLikeTypeSet.add(RefType.v("java.lang.Cloneable"));

    Set<Local> objectLikeVars = new HashSet<>();
    MultiMap<Local, Type> ft = getFlatTyping(tgs);
    for (Local l : ft.keySet()) {
      if (objectLikeTypeSet.equals(ft.get(l))) {
        objectLikeVars.add(l);
      }
    }
    return objectLikeVars;
  }

  @Override
  public void minimize(List<Typing> tgs, IHierarchy h) {
    Set<Local> objectVars = getObjectLikeTypings(tgs);
    OUTER: for (ListIterator<Typing> i = tgs.listIterator(); i.hasNext();) {
      Typing tgi = i.next();

      // Throw out duplicate typings
      for (Typing tgj : tgs) {
        // if compare = 1, then tgi is the more general typing
        // We shouldn't pick that one as we would then end up
        // with lots of locals typed to Serializable etc.
        if (tgi != tgj && compare(tgi, tgj, h, objectVars) == 1) {
          i.remove();
          continue OUTER;
        }
      }
    }
  }

  public int compare(Typing a, Typing b, IHierarchy h, Collection<Local> localsToIgnore) {
    int r = 0;
    for (Local v : a.map.keySet()) {
      if (!localsToIgnore.contains(v)) {
        Type ta = a.get(v), tb = b.get(v);

        int cmp;
        if (TypeResolver.typesEqual(ta, tb)) {
          cmp = 0;
        } else if (h.ancestor(ta, tb)) {
          cmp = 1;
        } else if (h.ancestor(tb, ta)) {
          cmp = -1;
        } else {
          return -2;
        }

        if ((cmp == 1 && r == -1) || (cmp == -1 && r == 1)) {
          return 2;
        }
        if (r == 0) {
          r = cmp;
        }
      }
    }
    return r;
  }

  @Override
  public void finalizeTypes(Typing tp) {
    for (Local l : tp.getAllLocals()) {
      Type t = tp.get(l);
      if (!t.isAllowedInFinalCode()) {
        tp.set(l, t.getDefaultFinalType());
      }
    }
  }

}
