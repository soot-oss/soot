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
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import java.util.*;
import soot.util.*;
import soot.util.queue.*;
import soot.options.SparkOptions;

/** The interface between the pointer analysis engine and the on-the-fly
 * call graph builder.
 * @author Ondrej Lhotak
 */

public class OnFlyCallGraph {
    CallGraphBuilder cgb;
    QueueReader reachables;
    QueueReader callEdges;
    public CallGraphBuilder getCallGraph() { return cgb; }
    public OnFlyCallGraph( PAG pag, FastHierarchy fh, Parms parms ) {
        this.pag = pag;
        this.fh = fh;
        this.parms = parms;
        cgb = new CallGraphBuilder( pag );
        reachables = cgb.reachables().listener();
        callEdges = cgb.getCallGraph().listener();
    }
    public void build() {
        cgb.build();
        processReachables();
        processCallEdges();
    }
    public boolean addSite( Stmt site ) {
        return false;
    }
    private void processReachables() {
        while(true) {
            SootMethod m = (SootMethod) reachables.next();
            if( m == null ) return;
            MethodPAG mpag = MethodPAG.v( pag, m );
            mpag.build();
            mpag.addToPAG(null);
        }
    }
    private void processCallEdges() {
        Stmt s = null;
        while(true) {
            Edge e = (Edge) callEdges.next();
            if( e == null ) break;
            MethodPAG.v( pag, e.tgt() ).addToPAG( null );
            parms.addCallTarget( e );
        }
    }

    public boolean wantReachingTypes( VarNode receiver ) {
        Object r = receiver.getVariable();
        if( !(r instanceof Local) ) return false;
        return cgb.wantTypes( (Local) r );
    }
    public void addReachingType( Type type ) {
        cgb.addType( type );
    }
    public void doneReachingTypes() {
        cgb.doneTypes();
        processReachables();
        processCallEdges();
    }

    /** Node uses this to notify PAG that n2 has been merged into n1. */
    public void mergedWith( Node n1, Node n2 ) {
    }

    public Set allReceivers() {
        throw new RuntimeException( "not implemented" );
    }

    public boolean wantStringConstants( VarNode v ) {
        Object r = v.getVariable();
        if( !(r instanceof Local) ) return false;
        return cgb.wantStringConstants( (Local) r );
    }

    public void newStringConstant( VarNode v, String name ) {
        cgb.newStringConstant( (Local) v.getVariable(), name );
    }
      
    /* End of public methods. */
    /* End of package methods. */

    private PAG pag;
    private FastHierarchy fh;
    private Parms parms;
}



