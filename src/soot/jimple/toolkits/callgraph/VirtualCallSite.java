/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.jimple.toolkits.callgraph;
import soot.*;
import soot.jimple.*;

import java.util.*;
import soot.util.*;
import soot.util.queue.*;

/** Holds relevant information about a particular virtual call site.
 * @author Ondrej Lhotak
 */
public class VirtualCallSite
{ 
    private InstanceInvokeExpr iie;
    private Stmt stmt;
    private SootMethod container;
    private NumberedString subSig;
    Kind kind;

    public VirtualCallSite( Stmt stmt, SootMethod container,
            InstanceInvokeExpr iie, NumberedString subSig, Kind kind ) {
        this.stmt = stmt;
        this.container = container;
        this.iie = iie;
        this.subSig = subSig;
        this.kind = kind;
    }
    public Stmt stmt() { return stmt; }
    public SootMethod container() { return container; }
    public InstanceInvokeExpr iie() { return iie; }
    public NumberedString subSig() { return subSig; }
    public Kind kind() { return kind; }
}


