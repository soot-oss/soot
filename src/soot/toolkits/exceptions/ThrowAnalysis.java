/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 John Jorgensen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.toolkits.exceptions;

import soot.Unit;

/**
 * A source of information about the exceptions that
 * {@link Unit}s might throw.
 */

public interface ThrowAnalysis {
    /**
     * Returns a set representing the {@link Throwable} types that 
     * the specified unit might throw.
     *
     * @param u {@link Unit} whose exceptions are to be returned.
     *
     * @return a representations of the {@link Throwable} types that
     * <code>u</code> might throw.
     */
    ThrowableSet mightThrow(Unit u);
}
