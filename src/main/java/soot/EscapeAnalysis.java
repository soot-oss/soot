package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
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

import soot.jimple.AnyNewExpr;

/**
 * A generic interface to an escape analysis.
 *
 * @author Ondrej Lhotak
 */

public interface EscapeAnalysis {
  /**
   * Returns true if objects allocated at n may continue to be live after the method in which they are allocated returns.
   */
  public boolean mayEscapeMethod(AnyNewExpr n);

  /**
   * Returns true if objects allocated at n in context c may continue to be live after the method in which they are allocated
   * returns.
   */
  public boolean mayEscapeMethod(Context c, AnyNewExpr n);

  /**
   * Returns true if objects allocated at n may be accessed in a thread other than the thread in which they were allocated.
   */
  public boolean mayEscapeThread(AnyNewExpr n);

  /**
   * Returns true if objects allocated at n in context c may be accessed in a thread other than the thread in which they were
   * allocated.
   */
  public boolean mayEscapeThread(Context c, AnyNewExpr n);
}
