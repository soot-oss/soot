/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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


package soot.jimple.toolkits.callgraph;

import soot.*;
import soot.util.*;
import java.util.*;


public class TopologicalOrderer
{
    CallGraph cg;
    List order = new ArrayList();
    NumberedSet visited = new NumberedSet( Scene.v().getMethodNumberer() );
    public TopologicalOrderer( CallGraph cg ) {
        this.cg = cg;
    }

    public void go() {
        Iterator methods = cg.sourceMethods();
        while( methods.hasNext() ) {
            SootMethod m = (SootMethod) methods.next();
            dfsVisit( m );
        }
    }

    private void dfsVisit( SootMethod m ) {
        if( visited.contains( m ) ) return;
        visited.add( m );
        Iterator targets = new Targets( cg.edgesOutOf(m) );
        while( targets.hasNext() ) {
            SootMethod target = (SootMethod) targets.next();
            dfsVisit( target );
        }
        order.add( m );
    }

    public List order() { return order; }
}
