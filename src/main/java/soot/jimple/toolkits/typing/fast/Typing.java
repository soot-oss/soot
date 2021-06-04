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
public class Typing {

  protected HashMap<Local, Type> map;

  public Typing(Collection<Local> vs) {
    this.map = new HashMap<Local, Type>(vs.size());
  }

  public Typing(Typing tg) {
    this.map = new HashMap<Local, Type>(tg.map);
  }

  public Map<Local, Type> getMap() {
    return map;
  }

  public Type get(Local v) {
    Type t = this.map.get(v);
    return (t == null) ? BottomType.v() : t;
  }

  public Type set(Local v, Type t) {
    return (t instanceof BottomType) ? null : this.map.put(v, t);
  }

  public Collection<Local> getAllLocals() {
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
}