package soot.jimple.toolkits.typing.fast;

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
    final Type objectType = Scene.v().getObjectType();
    Set<Type> objectLikeTypeSet = new HashSet<>();
    objectLikeTypeSet.add(objectType);
    objectLikeTypeSet.add(RefType.v("java.io.Serializable"));
    objectLikeTypeSet.add(RefType.v("java.lang.Cloneable"));

    Set<Local> objectLikeVars = new HashSet<>();
    MultiMap<Local, Type> ft = getFlatTyping(tgs);
    for (Local l : ft.keySet()) {
      Set<Type> ts = ft.get(l);
      if (ts.equals(objectLikeTypeSet)) {
        objectLikeVars.add(l);
      }
    }
    return objectLikeVars;
  }

  public void minimize(List<Typing> tgs, IHierarchy h) {
    Set<Local> objectVars = getObjectLikeTypings(tgs);
    outer: for (ListIterator<Typing> i = tgs.listIterator(); i.hasNext();) {
      Typing tgi = i.next();

      // Throw out duplicate typings
      for (Typing tgj : tgs) {
        // if compare = 1, then tgi is the more general typing
        // We shouldn't pick that one as we would then end up
        // with lots of locals typed to Serializable etc.
        if (tgi != tgj && compare(tgi, tgj, h, objectVars) == 1) {
          i.remove();
          continue outer;
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
}
