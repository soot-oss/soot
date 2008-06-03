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
import soot.baf.ThrowInst;
import soot.jimple.ThrowStmt;

/**
 * <p>A source of information about the exceptions that
 * {@link Unit}s might throw.</p>
 *
 * <p>The <code>Unit</code>s corresponding to <code>athrow</code>
 * instructions may throw exceptions either explicitly&mdash;because
 * the exception is the <code>athrow</code>'s argument&mdash; or
 * implicitly&mdash;because some error arises in the course of
 * executing the instruction (only implicit exceptions are possible
 * for bytecode instructions other than <code>athrow</code>).  The
 * <code>mightThrowExplicitly()</code> and
 * <code>mightThrowImplicitly()</code> methods allow analyses to
 * exploit any extra precision that may be gained by distinguishing
 * between an <code>athrow</code>'s implicit and explicit exceptions.</p>
 */

public interface ThrowAnalysis {
    /**
     * Returns a set representing the {@link Throwable} types that 
     * the specified unit might throw.
     *
     * @param u {@link Unit} whose exceptions are to be returned.
     *
     * @return a representation of the <code>Throwable</code> types that
     * <code>u</code> might throw.
     */
    ThrowableSet mightThrow(Unit u);

    /**
     * Returns a set representing the {@link Throwable} types that
     * the specified throw instruction might throw explicitly, that is,
     * the possible types for its <code>Throwable</code> argument.
     *
     * @param t {@link ThrowInst} whose explicit exceptions are
     *          to be returned.
     *
     * @return a representation of the possible types of
     * <code>t</code>'s <code>Throwable</code> operand.
     */
    ThrowableSet mightThrowExplicitly(ThrowInst t);

    /**
     * Returns a set representing the {@link Throwable} types that
     * the specified throw statement might throw explicitly, that is,
     * the possible types for its <code>Throwable</code> argument.
     *
     * @param t {@link ThrowStmt} whose explicit exceptions are
     *          to be returned.
     *
     * @return a representation of the possible types of
     * <code>t</code>'s <code>Throwable</code> operand.
     */
    ThrowableSet mightThrowExplicitly(ThrowStmt t);

    /**
     * Returns a set representing the {@link Throwable} types that
     * the specified throw instruction might throw implicitly, that is,
     * the possible types of errors which might arise in the course
     * of executing the <code>throw</code> instruction, rather than
     * the type of the <code>throw</code>'s operand.
     *
     * @param t {@link ThrowStmt} whose implicit exceptions are
     *          to be returned.
     *
     * @return a representation of the types of exceptions that 
     * <code>t</code> might throw implicitly.
     */
    ThrowableSet mightThrowImplicitly(ThrowInst t);

    /**
     * Returns a set representing the {@link Throwable} types that
     * the specified throw statement might throw implicitly, that is,
     * the possible types of errors which might arise in the course
     * of executing the <code>throw</code> statement, rather than
     * the type of the <code>throw</code>'s operand.
     *
     * @param t {@link ThrowStmt} whose implicit exceptions are
     *          to be returned.
     *
     * @return a representation of the types of exceptions that 
     * <code>t</code> might throw implicitly.
     */
    ThrowableSet mightThrowImplicitly(ThrowStmt t);

}
