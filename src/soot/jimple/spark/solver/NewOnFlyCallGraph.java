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

package soot.jimple.spark.solver;
import soot.jimple.*;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.builder.*;
import soot.jimple.spark.callgraph.*;
import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.toolkits.invoke.InvokeGraph;

/** Performs a pseudo-topological sort on the VarNodes in a PAG.
 * @author Ondrej Lhotak
 */

public class NewOnFlyCallGraph extends OnFlyCallGraph {
    CallGraph cg;
    public CallGraph getCallGraph() { return cg; }
    public NewOnFlyCallGraph( PAG pag, FastHierarchy fh, InvokeGraph ig,
            Parms parms ) {
        super( pag, fh, ig, parms );
        cg = new CallGraph( new ImplicitMethodInvocation(), pag );
        cg.build();
    }
    public boolean addSite( Stmt site ) {
        return false;
    }

    public boolean addReachingType( VarNode receiver, Type type, Collection addedEdges ) {
        boolean ret = false;
        List edges = new ArrayList();

        Object r = receiver.getValue();
        if( !(r instanceof Local) ) return false;
        cg.addType( (Local)r, type, edges );
        VirtualCallSite vcs = null;
        for( Iterator oIt = edges.iterator(); oIt.hasNext(); ) {
            final Object o = (Object) oIt.next();
            if( o instanceof VirtualCallSite ) vcs = (VirtualCallSite) o;
            else {
                SootMethod target = (SootMethod) o;
                if( ig.addTarget( vcs.getStmt(), target ) ) {
                    parms.addCallTarget( vcs.getStmt(), target, addedEdges );
                    ret = true;
                }
            }
        }
        return ret;
    }

    /** Node uses this to notify PAG that n2 has been merged into n1. */
    public void mergedWith( Node n1, Node n2 ) {
    }

    public Set allReceivers() {
        throw new RuntimeException( "not implemented" );
    }
    
    /* End of public methods. */
    /* End of package methods. */

}



