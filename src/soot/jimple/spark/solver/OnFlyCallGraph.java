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
        for( Iterator it = ig.getAllSites().iterator(); it.hasNext(); ) {
            Stmt site = (Stmt) it.next();
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
        return receiverToSite.put( iie.getBase(), site );
    }

    public boolean addReachingType( VarNode receiver, Type type, Collection touchedNodes ) {
        boolean ret = false;
        if( receiverToType.put( receiver, type ) ) {
            for( Iterator it = receiverToSite.get( receiver ).iterator();
                    it.hasNext(); ) {
                Stmt site = (Stmt) it.next();
                InstanceInvokeExpr ie = (InstanceInvokeExpr) site.getInvokeExpr();
                RefType declaredTypeOfBase = (RefType) ie.getBase().getType();
                Collection targets = fh.resolveConcreteDispatch(
                        Collections.singletonList( type ), ie.getMethod(),
                        declaredTypeOfBase );
                for( Iterator targetIt = targets.iterator(); targetIt.hasNext(); ) {
                    SootMethod target = (SootMethod) it.next();
                    if( ig.addTarget( site, target ) ) {
                        parms.addCallTarget( site, target, touchedNodes );
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



