package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Florian Loitsch
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
import java.util.Iterator;
import java.util.Set;

import soot.Local;
import soot.Type;
import soot.jimple.Jimple;

/**
 * provides an easy interface to handle new var-names. New names are automatically added to the chain, and the provided
 * locals are guaranteed to have a unique name.
 */
public class LocalCreation {
  /** if no prefix is given, this one's used */
  public static final String DEFAULT_PREFIX = "soot";
  private String prefix;
  private int counter;
  private Set<String> locals;
  private Collection<Local> localChain;

  /**
   * all actions are done on the given locals-chain. as prefix the <code>DEFAULT-PREFIX</code> will be used.
   *
   * @param chain
   *          the locals-chain of a Jimple-body
   */
  public LocalCreation(Collection<Local> locals) {
    this(locals, DEFAULT_PREFIX);
  }

  /**
   * whenever <code>newLocal(type)</code> will be called, the given prefix is used.
   *
   * @param Chain
   *          the locals-chain of a Jimple-body
   * @param String
   *          prefix overrides the DEFAULT-PREFIX
   */
  public LocalCreation(Collection<Local> locals, String prefix) {
    this.locals = new HashSet<String>(locals.size());
    localChain = locals;
    Iterator<Local> it = locals.iterator();
    while (it.hasNext()) {
      Local l = (Local) it.next();
      this.locals.add(l.getName());
    }
    this.prefix = prefix;
    counter = 0; // try the first one with suffix 0.
  }

  /**
   * returns a new local with the prefix given to the constructor (or the default-prefix if none has been given) and the
   * given type.<br>
   * The returned local will automatically added to the locals-chain.<br>
   * The local will be of the form: <tt>prefix</tt><i>X</i> (where the last <i>X</i> is a number, so the localname is
   * unique).
   *
   * @param type
   *          the Type of the new local.
   * @return a new local with a unique name and the given type.
   */
  public Local newLocal(Type type) {
    return newLocal(prefix, type);
  }

  /**
   * returns a new local with the given prefix and the given type.<br>
   * the returned local will automatically added to the locals-chain. The local will be of the form: <tt>prefix</tt><i>X</i>
   * (where the last <i>X</i> is a number, so the localname is unique).
   *
   * @param prefix
   *          the prefix for the now local.
   * @param type
   *          the Type of the now local.
   * @return a local with the given prefix and the given type.
   */
  public Local newLocal(String prefix, Type type) {
    int suffix = 0;
    if (prefix == this.prefix || prefix.equals(this.prefix)) {
      suffix = counter;
    }

    while (locals.contains(prefix + suffix)) {
      suffix++;
    }

    if (prefix == this.prefix || prefix.equals(this.prefix)) {
      counter = suffix + 1;
    }
    String newName = prefix + suffix;
    Local newLocal = Jimple.v().newLocal(newName, type);
    localChain.add(newLocal);
    locals.add(newName);
    return newLocal;
  }
}
