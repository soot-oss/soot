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
import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.toolkits.invoke.InvokeGraph;

/** Performs a pseudo-topological sort on the VarNodes in a PAG.
 * @author Ondrej Lhotak
 */

public class OnFlyCallGraph {
    public OnFlyCallGraph( PAG pag, FastHierarchy fh, InvokeGraph ig,
            Parms parms ) {
        this.pag = pag;
        this.fh = fh;
        this.ig = ig;
        this.parms = parms;
        for( Iterator siteIt = ig.getAllSites().iterator(); siteIt.hasNext(); ) {
            final Stmt site = (Stmt) siteIt.next();
            InvokeExpr ie = (InvokeExpr) site.getInvokeExpr();
            if( ie instanceof VirtualInvokeExpr 
            || ie instanceof InterfaceInvokeExpr ) {
                ig.removeAllTargets( site );
                addSite( site );
            }
        }
    }
    public boolean addSite( Stmt site ) {
        InstanceInvokeExpr iie = (InstanceInvokeExpr) site.getInvokeExpr();
        if( iie instanceof SpecialInvokeExpr ) 
            throw new RuntimeException( "Can't handle that" );
        parms.addCallTarget( site, null, null );
        return receiverToSite.put( pag.findVarNode( iie.getBase() ), site );
    }

    public boolean addReachingType( VarNode receiver, Type type, Collection addedEdges ) {
        boolean ret = false;
        if( receiverToType.put( receiver, type ) ) {
            for( Iterator siteIt = receiverToSite.get( receiver ).iterator(); siteIt.hasNext(); ) {
                final Stmt site = (Stmt) siteIt.next();
                InstanceInvokeExpr ie = (InstanceInvokeExpr) site.getInvokeExpr();
                Type baseType = ie.getBase().getType();
                RefType declaredTypeOfBase = null;
                if( baseType instanceof RefType ) {
                    declaredTypeOfBase = (RefType) baseType;
                } else if( baseType instanceof ArrayType ) {
                    declaredTypeOfBase = RefType.v("java.lang.Object");
                } else {
                    throw new RuntimeException( "Weird declared type: "+baseType );
                }
                Collection targets = null;
                try {
                    targets = fh.resolveConcreteDispatch(
                            Collections.singletonList( type ), ie.getMethod(),
                            declaredTypeOfBase );
                } catch( RuntimeException e ) {
                    // failed to resolve because pointer analysis is too
                    // conservative, and came up with a reaching type that
                    // doesn't actually reach.
                    targets = Collections.EMPTY_SET;
                }
                for( Iterator targetIt = targets.iterator(); targetIt.hasNext(); ) {
                    final SootMethod target = (SootMethod) targetIt.next();
                    if( ig.addTarget( site, target ) ) {
                        parms.addCallTarget( site, target, addedEdges );
                        ret = true;
                    }
                }
            }
        }
        return ret;
    }

    public Set allReceivers() {
        return receiverToSite.keySet();
    }
    
    /* End of public methods. */
    /* End of package methods. */

    protected PAG pag;
    protected MultiMap receiverToSite = new HashMultiMap();
    protected MultiMap receiverToType = new HashMultiMap();
    protected FastHierarchy fh;
    protected InvokeGraph ig;
    protected Parms parms;
}



