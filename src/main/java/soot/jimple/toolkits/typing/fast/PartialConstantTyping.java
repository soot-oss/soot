package soot.jimple.toolkits.typing.fast;

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