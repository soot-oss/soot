package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import soot.util.Numberable;

/**
 * A local variable, used within Body classes. Intermediate representations must use an implementation of Local for their
 * local variables.
 */
public interface Local extends Value, Numberable, Immediate {
  /** Returns the name of the current Local variable. */
  public String getName();

  /** Sets the name of the current variable. */
  public void setName(String name);

  /** Sets the type of the current variable. */
  public void setType(Type t);
}
