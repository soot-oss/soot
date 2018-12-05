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
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import soot.Local;
import soot.Type;

/**
 * @author Ben Bellamy
 */
public class Typing {
  private HashMap<Local, Type> map;

  public Typing(Collection<Local> vs) {
    map = new HashMap<Local, Type>(vs.size());
    final BottomType bottomType = BottomType.v();
    for (Local v : vs) {
      this.map.put(v, bottomType);
    }
  }

  public Typing(Typing tg) {
    this.map = new HashMap<Local, Type>(tg.map);
  }

  public Type get(Local v) {
    return this.map.get(v);
  }

  public Type set(Local v, Type t) {
    return this.map.put(v, t);
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append('{');
    for (Local v : this.map.keySet()) {
      sb.append(v);
      sb.append(':');
      sb.append(this.get(v));
      sb.append(',');
    }
    sb.append('}');
    return sb.toString();
  }

  public static void minimize(List<Typing> tgs, IHierarchy h) {
    outer: for (ListIterator<Typing> i = tgs.listIterator(); i.hasNext();) {
      Typing tgi = i.next();

      // Throw out duplicate typings
      for (Typing tgj : tgs) {
        // if compare = 1, then tgi is the more general typing
        // We shouldn't pick that one as we would then end up
        // with lots of locals typed to Serializable etc.
        if (tgi != tgj && compare(tgi, tgj, h) == 1) {
          i.remove();
          continue outer;
        }
      }
    }

  }

  public static int compare(Typing a, Typing b, IHierarchy h) {
    int r = 0;
    for (Local v : a.map.keySet()) {
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
    return r;
  }
}