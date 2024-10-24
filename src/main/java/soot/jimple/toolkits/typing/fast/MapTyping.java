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
import java.util.Map;

import soot.Local;
import soot.Type;

/**
 * @author Ben Bellamy
 */
public class MapTyping implements ITyping {

  protected HashMap<Local, Type> map;
  private Type bottomType = BottomType.v();

  public MapTyping(Collection<Local> vs) {
    this.map = new HashMap<Local, Type>(vs.size());
  }

  public MapTyping(ITyping tg) {
    if (tg instanceof MapTyping) {
      MapTyping mp = (MapTyping) tg;
      this.map = new HashMap<Local, Type>(mp.map);
    } else {
      cloneFrom(tg);
    }
  }

  @Override
  public Map<Local, Type> getMap() {
    return map;
  }

  @Override
  public Type get(Local v) {
    Type t = this.map.get(v);
    return (t == null) ? BottomType.v() : t;
  }

  @Override
  public void set(Local v, Type t) {
    if (t != bottomType) {
      this.map.put(v, t);
    }
  }

  @Override
  public Iterable<Local> getAllLocals() {
    return map.keySet();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    for (Map.Entry<Local, Type> e : this.map.entrySet()) {
      sb.append(e.getKey());
      sb.append(':');
      sb.append(e.getValue());
      sb.append(',');
    }
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public ITyping createCloneTyping() {
    return new MapTyping(this);
  }
}