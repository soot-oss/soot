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

import com.google.common.collect.Iterables;

import java.util.HashMap;
import java.util.Map;

import soot.Local;
import soot.Type;

class PartialConstantTyping implements ITyping {

  private Map<Local, Type> constantTypings = new HashMap<>();
  private ITyping inner;

  public PartialConstantTyping(ITyping typing) {
    this.inner = typing;
  }

  @Override
  public Type get(Local v) {
    Type t = constantTypings.get(v);
    if (t != null) {
      return t;
    }
    return inner.get(v);
  }

  @Override
  public void set(Local v, Type t) {
    inner.set(v, t);
  }

  @Override
  public Iterable<Local> getAllLocals() {
    return Iterables.concat(constantTypings.keySet(), inner.getAllLocals());
  }

  @Override
  public boolean isEmpty() {
    return inner.isEmpty();
  }

  @Override
  public void clear() {
    inner.clear();
  }

  @Override
  public Map<Local, Type> getMap() {
    //This is expensive, so we try to avoid it
    Map<Local, Type> m = new HashMap<>(constantTypings);
    m.putAll(inner.getMap());
    return m;
  }

  public void setConstantTyping(Local v, Type t) {
    constantTypings.put(v, t);
  }

  @Override
  public ITyping createCloneTyping() {
    PartialConstantTyping clone = new PartialConstantTyping(inner.createCloneTyping());
    //we share the constant typings.
    clone.constantTypings = constantTypings;
    return clone;
  }

  public Map<Local, Type> getConstantTypings() {
    return constantTypings;
  }

  public Map<Local, Type> getNonConstantTypings() {
    return inner.getMap();
  }

}