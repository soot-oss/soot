/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.jimple.toolkits.scalar;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

/**
 * provides an easy interface to handle new var-names. New names are automaticly
 * added to the chain, and the provided locals are guaranteed to have a unique name.
 */
public class LocalCreation {
  /** if no prefix is given, this one's used */
  public static final String DEFAULT_PREFIX = "soot";
  private String prefix;
  private int counter;
  private Set locals;
  private Chain localChain;

  /**
   * all actions are done on the given locals-chain. as prefix the
   * <code>DEFAULT-PREFIX</code> will be used.
   *
   * @param chain the locals-chain of a Jimple-body
   */
  public LocalCreation(Chain locals) {
    this(locals, DEFAULT_PREFIX);
  }

  /**
   * whenever <code>newLocal(type)</code> will be called, the given prefix is
   * used.
   *
   * @param Chain the locals-chain of a Jimple-body
   * @param String prefix overrides the DEFAULT-PREFIX
   */
  public LocalCreation(Chain locals, String prefix) {
    this.locals = new HashSet(locals.size());
    localChain = locals;
    Iterator it = locals.iterator();
    while (it.hasNext()) {
      Local l = (Local)it.next();
      this.locals.add(l.getName());
    }
    this.prefix = prefix;
    counter = 0; //try the first one with suffix 0.
  }

  /**
   * returns a new local with the prefix given to the constructor (or the
   * default-prefix if none has been given) and the given type.<br>
   * The returned local will automaticly added to the locals-chain.<br>
   * The local will be of the form: <tt>prefix</tt><i>X</i> (where the last
   * <i>X</i> is a number, so the localname is unique).
   *
   * @param type the Type of the new local.
   * @return a new local with a unique name and the given type.
   */
  public Local newLocal(Type type) {
    return newLocal(prefix, type);
  }

  /**
   * returns a new local with the given prefix and the given type.<br>
   * the returned local will automaticly added to the locals-chain.
   * The local will be of the form: <tt>prefix</tt><i>X</i> (where the last
   * <i>X</i> is a number, so the localname is unique).
   *
   * @param prefix the prefix for the now local.
   * @param type the Type of the now local.
   * @return a local with the given prefix and the given type.
   */
  public Local newLocal(String prefix, Type type) {
    int suffix = 0;
    if (prefix == this.prefix ||
        prefix.equals(this.prefix)) {
      suffix = counter;
    }

    while (locals.contains(prefix + suffix)) suffix++;

    if (prefix == this.prefix ||
        prefix.equals(this.prefix)) {
      counter = suffix + 1;
    }
    String newName = prefix + suffix;
    Local newLocal = Jimple.v().newLocal(newName, type);
    localChain.addLast(newLocal);
    locals.add(newName);
    return newLocal;
  }
}    
