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
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.*;
import java.util.*;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.spark.internal.*;
import soot.options.SparkOptions;

/** Collapses VarNodes (green) forming strongly-connected components in
 * the pointer assignment graph.
 * @author Ondrej Lhotak
 */

public class SCCCollapser {
    /** Actually collapse the SCCs in the PAG. */
    public void collapse() {
        boolean verbose = pag.getOpts().verbose();
        if( verbose ) {
            G.v().out.println( "Total VarNodes: "+pag.getVarNodeNumberer().size()+". Collapsing SCCs..." );
        }

        new TopoSorter( pag, ignoreTypes ).sort();
        TreeSet s = new TreeSet();
        for( Iterator vIt = pag.getVarNodeNumberer().iterator(); vIt.hasNext(); ) {
            final VarNode v = (VarNode) vIt.next();
            s.add(v);
        }
        for( Iterator vIt = s.iterator(); vIt.hasNext(); ) {
            final VarNode v = (VarNode) vIt.next();
            dfsVisit( v, v );
        }

        if( verbose ) {
            G.v().out.println( ""+numCollapsed+" nodes were collapsed." );
        }
        visited = null;
    }
    public SCCCollapser( PAG pag, boolean ignoreTypes ) {
        this.pag = pag;
        this.ignoreTypes = ignoreTypes;
        this.typeManager = (TypeManager) pag.getTypeManager();
    }
    
    /* End of public methods. */
    /* End of package methods. */

    protected int numCollapsed = 0;
    protected PAG pag;
    protected HashSet visited = new HashSet();
    protected boolean ignoreTypes;
    protected TypeManager typeManager;

    final protected void dfsVisit( VarNode v, VarNode rootOfSCC ) {
        if( visited.contains( v ) ) return;
        visited.add( v );
        Node[] succs = pag.simpleInvLookup( v );
        for( int i = 0; i < succs.length; i++ ) {
            if( ignoreTypes
            || typeManager.castNeverFails( succs[i].getType(), v.getType() ) ) {
                dfsVisit( (VarNode) succs[i], rootOfSCC );
            }
        }
        if( v != rootOfSCC ) {
            if( !ignoreTypes ) {
                if( typeManager.castNeverFails(
                            v.getType(), rootOfSCC.getType() )
                 && typeManager.castNeverFails(
                            rootOfSCC.getType(), v.getType() ) ) {
                    rootOfSCC.mergeWith( v );
                    numCollapsed++;
                }
            } else /* ignoreTypes */ {
                if( typeManager.castNeverFails(
                            v.getType(), rootOfSCC.getType() ) ) {
                    rootOfSCC.mergeWith( v );
                } else if( typeManager.castNeverFails(
                            rootOfSCC.getType(), v.getType() ) ) {
                    v.mergeWith( rootOfSCC );
                } else {
                    rootOfSCC.getReplacement().setType( null );
                    PointsToSetInternal set = rootOfSCC.getP2Set();
                    if( set != null ) {
                        set.setType( null );
                    }
                    rootOfSCC.mergeWith( v );
                }
                numCollapsed++;
            }
        }
    }
}



