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

package soot.jimple.spark;
import soot.*;
import soot.jimple.spark.builder.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.solver.*;
import soot.jimple.spark.sets.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.options.SparkOptions;
import soot.tagkit.*;

/** Main entry point for Spark.
 * @author Ondrej Lhotak
 */
public abstract class AbstractSparkTransformer extends SceneTransformer
{ 
    protected static void reportTime( String desc, Date start, Date end ) {
        long time = end.getTime()-start.getTime();
        G.v().out.println( "[Spark] "+desc+" in "+time/1000+"."+(time/100)%10+" seconds." );
    }
    protected static void doGC() {
        // Do 5 times because the garbage collector doesn't seem to always collect
        // everything on the first try.
        System.gc();
        System.gc();
        System.gc();
        System.gc();
        System.gc();
    }

    protected void addTag( Host h, Node n, Map nodeToTag, Tag unknown ) {
        if( nodeToTag.containsKey( n ) ) h.addTag( (Tag) nodeToTag.get(n) );
        else h.addTag( unknown );
    }

    protected void findSetMass( AbstractPAG pag ) {
        int mass = 0;
        int varMass = 0;
        int adfs = 0;
        int scalars = 0;
        if( false ) {
            for( Iterator it = Scene.v().getReachableMethods().listener(); it.hasNext(); ) {
                SootMethod m = (SootMethod) it.next();
                G.v().out.println( m.getBytecodeSignature() );
            }
        }


        for( Iterator vIt = pag.getVarNodeNumberer().iterator(); vIt.hasNext(); ) {


            final VarNode v = (VarNode) vIt.next();
                scalars++;
            PointsToSetInternal set = v.getP2Set();
            if( set != null ) mass += set.size();
            if( set != null ) varMass += set.size();
            if( set != null && set.size() > 0 ) {
                //G.v().out.println( "V "+v.getVariable()+" "+set.size() );
            //    G.v().out.println( ""+v.getVariable()+" "+v.getMethod()+" "+set.size() );
            }
        }
        for( Iterator anIt = pag.allocSourcesIterator(); anIt.hasNext(); ) {
            final AllocNode an = (AllocNode) anIt.next();
            for( Iterator adfIt = an.getFields().iterator(); adfIt.hasNext(); ) {
                final AllocDotField adf = (AllocDotField) adfIt.next();
                PointsToSetInternal set = adf.getP2Set();
                if( set != null ) mass += set.size();
                if( set != null && set.size() > 0 ) {
                    adfs++;
            //        G.v().out.println( ""+adf.getBase().getNewExpr()+"."+adf.getField()+" "+set.size() );
                }
            }
        }
        G.v().out.println( "Set mass: " + mass );
        G.v().out.println( "Variable mass: " + varMass );
        G.v().out.println( "Scalars: "+scalars );
        G.v().out.println( "adfs: "+adfs );
        // Compute points-to set sizes of dereference sites BEFORE
        // trimming sets by declared type
        int[] deRefCounts = new int[30001];
        for( Iterator vIt = pag.getDereferences().iterator(); vIt.hasNext(); ) {
            final VarNode v = (VarNode) vIt.next();
            PointsToSetInternal set = v.getP2Set();
            int size = 0;
            if( set != null ) size = set.size();
            deRefCounts[size]++;
        }
        int total = 0;
        for( int i=0; i < deRefCounts.length; i++ ) total+= deRefCounts[i];
        G.v().out.println( "Dereference counts BEFORE trimming (total = "+total+"):" );
        for( int i=0; i < deRefCounts.length; i++ ) {
            if( deRefCounts[i] > 0 ) {
                G.v().out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
            }
        }
        // Compute points-to set sizes of dereference sites AFTER
        // trimming sets by declared type
        /*
        if( pag.getTypeManager().getFastHierarchy() == null ) {
            pag.getTypeManager().clearTypeMask();
            pag.getTypeManager().setFastHierarchy( Scene.v().getOrMakeFastHierarchy() );
            pag.getTypeManager().makeTypeMask( pag );
            deRefCounts = new int[30001];
            for( Iterator vIt = pag.getDereferences().iterator(); vIt.hasNext(); ) {
                final VarNode v = (VarNode) vIt.next();
                PointsToSetInternal set = 
                    pag.getSetFactory().newSet( v.getType(), pag );
                int size = 0;
                if( set != null ) {
                    v.getP2Set().setType( null );
                    v.getP2Set().getNewSet().setType( null );
                    v.getP2Set().getOldSet().setType( null );
                    set.addAll( v.getP2Set(), null );
                    size = set.size();
                }
                deRefCounts[size]++;
            }
            total = 0;
            for( int i=0; i < deRefCounts.length; i++ ) total+= deRefCounts[i];
            G.v().out.println( "Dereference counts AFTER trimming (total = "+total+"):" );
            for( int i=0; i < deRefCounts.length; i++ ) {
                if( deRefCounts[i] > 0 ) {
                    G.v().out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
                }
            }
        }
        */
        /*
        deRefCounts = new int[30001];
        for( Iterator siteIt = ig.getAllSites().iterator(); siteIt.hasNext(); ) {
            final Stmt site = (Stmt) siteIt.next();
            SootMethod method = ig.getDeclaringMethod( site );
            Value ie = site.getInvokeExpr();
            if( !(ie instanceof VirtualInvokeExpr) 
                    && !(ie instanceof InterfaceInvokeExpr) ) continue;
            InstanceInvokeExpr expr = (InstanceInvokeExpr) site.getInvokeExpr();
            Local receiver = (Local) expr.getBase();
            Collection types = pag.reachingObjects( method, site, receiver )
                .possibleTypes();
            Type receiverType = receiver.getType();
            if( receiverType instanceof ArrayType ) {
                receiverType = RefType.v( "java.lang.Object" );
            }
            Collection targets =
                Scene.v().getOrMakeFastHierarchy().resolveConcreteDispatchWithoutFailing(
                        types, expr.getMethod(), (RefType) receiverType );
            deRefCounts[targets.size()]++;
        }
        total = 0;
        for( int i=0; i < deRefCounts.length; i++ ) total+= deRefCounts[i];
        G.v().out.println( "Virtual invoke target counts (total = "+total+"):" );
        for( int i=0; i < deRefCounts.length; i++ ) {
            if( deRefCounts[i] > 0 ) {
                G.v().out.println( ""+i+" "+deRefCounts[i]+" "+(deRefCounts[i]*100.0/total)+"%" );
            }
        }
        */
    }
}


