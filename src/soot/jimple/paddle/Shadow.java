/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot.jimple.paddle;
import soot.*;
import soot.jimple.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;

/** A specification of a shadow of a pointcut that pushes/pops a cflow stack.
 * @author Ondrej Lhotak
 */
public interface Shadow
{ 
    /** Returns the method containing the shadow. */
    public SootMethod method();
    /** Returns the statement that would push onto the cflow stack. */
    public Stmt pushStmt();
    /** Returns the statement that would pop off the cflow stack. */
    public Stmt popStmt();
    /** Returns true if the push/pop occur every time the shadow is executed,
     * false if the shadow may be executed without the push/pop occuring. */
    public boolean unconditional();
}

