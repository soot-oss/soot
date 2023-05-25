package soot.jimple.toolkits.scalar;

import java.util.Collection;

import soot.Local;
import soot.Type;

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

public abstract class LocalCreation {
  protected final Collection<Local> localChain;
  protected final String prefix;

  public LocalCreation(Collection<Local> locals, String prefix) {
    this.localChain = locals;
    this.prefix = prefix;
  }

  /**
   * returns a new local with the prefix given to the constructor (or the default-prefix if none has been given) and the
   * given type.<br>
   * The returned local will automatically added to the locals-chain.<br>
   * The local will be of the form: <tt>prefix</tt><i>X</i> (where the last <i>X</i> is a number, so the local name is
   * unique).
   *
   * @param type
   *          the Type of the new local.
   * @return a new local with a unique name and the given type.
   */
  public abstract Local newLocal(Type type);

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
  public abstract Local newLocal(String prefix, Type type);

}
