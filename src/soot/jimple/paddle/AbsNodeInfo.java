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
import soot.util.*;
import soot.jimple.paddle.queue.*;
import java.util.*;

/** Keeps track of the type and method of each node.
 * @author Ondrej Lhotak
 */
public abstract class AbsNodeInfo implements DepItem
{ 
    protected Rvar_method_type locals;
    protected Rvar_type globals;
    protected Robj_method_type localallocs;
    protected Robj_type globalallocs;
    public AbsNodeInfo(
        Rvar_method_type locals,
        Rvar_type globals,
        Robj_method_type localallocs,
        Robj_type globalallocs
        )
    {
        this.locals = locals;
        this.globals = globals;
        this.localallocs = localallocs;
        this.globalallocs = globalallocs;
    }

    public abstract boolean update();
}

