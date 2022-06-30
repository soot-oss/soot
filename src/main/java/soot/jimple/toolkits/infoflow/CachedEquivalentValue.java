package soot.jimple.toolkits.infoflow;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Eric Bodden
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

import java.util.WeakHashMap;

import soot.EquivalentValue;
import soot.Value;

/**
 * An {@link EquivalentValue} with cached hash code and equals-relation.
 * 
 * @author Eric Bodden
 */
public class CachedEquivalentValue extends EquivalentValue {

  protected int code = Integer.MAX_VALUE;

  protected WeakHashMap<Value, Boolean> isEquivalent = new WeakHashMap<Value, Boolean>();

  public CachedEquivalentValue(Value e) {
    super(e);
  }

  public int hashCode() {
    if (code == Integer.MAX_VALUE) {
      code = super.hashCode();
    }
    return code;
  }

  public boolean equals(Object o) {
    if (this.getClass() != o.getClass()) {
      return false;
    }
    EquivalentValue ev = (EquivalentValue) o;
    Value v = ev.getValue();
    Boolean b = isEquivalent.get(v);
    if (b == null) {
      b = super.equals(o);
      isEquivalent.put(v, b);
    }
    return b;
  }

}
