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

package soot.jimple.spark.callgraph;
import soot.*;
import soot.jimple.*;
import soot.jimple.spark.*;
import java.util.*;
import soot.util.*;
import soot.jimple.toolkits.pointer.DumbPointerAnalysis;

/** Models the call graph.
 * @author Ondrej Lhotak
 */
public class CallGraph
{ 
    private NumberedSet reachableMethods = new NumberedSet( Scene.v().getMethodNumberer() );
    private HashMap invokeExprToVCS = new HashMap();
    private LargeNumberedMap localToVCS = new LargeNumberedMap( Scene.v().getLocalNumberer() );
    private LinkedList worklist = new LinkedList();
    private PointsToAnalysis pa;
    private ImplicitMethodInvocation imi;

    public CallGraph( ImplicitMethodInvocation imi, PointsToAnalysis pa ) {
        this.imi = imi;
        this.pa = pa;
    }
    public void build() {
        for( Iterator mIt = imi.getEntryPoints().iterator(); mIt.hasNext(); ) {
            final SootMethod m = (SootMethod) mIt.next();
            if( reachableMethods.add( m ) ) {
                worklist.add( m );
            }
        }
        processWorklist();
    }
    private void processWorklist() {
        while( !worklist.isEmpty() ) {
            SootMethod m = (SootMethod) worklist.removeFirst();
            processNewMethod( m );
            for( Iterator tgtIt = imi.getImplicitTargets( m ).iterator(); tgtIt.hasNext(); ) {
                final SootMethod tgt = (SootMethod) tgtIt.next();
                if( reachableMethods.add( tgt ) ) worklist.add( tgt );
            }
        }
    }
    private void processNewMethod( SootMethod m ) {
        if( m.isNative() ) {
            return;
        }
        if( !m.isConcrete() ) {
            System.out.println( "looking at abstract method "+m );
        }
        Body b = m.retrieveActiveBody();
        for( Iterator sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if( s.containsInvokeExpr() ) {
                InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();
                if( ie instanceof InstanceInvokeExpr ) {
                    VirtualCallSite vcs = new VirtualCallSite( s, m );
                    invokeExprToVCS.put( ie, vcs );
                    Local base = (Local) ((InstanceInvokeExpr)ie).getBase();
                    HashSet vcss = (HashSet) localToVCS.get( base );
                    if( vcss == null ) {
                        localToVCS.put( base, vcss = new HashSet() );
                    }
                    vcss.add( vcs );
                    NumberedSet targets = new NumberedSet( Scene.v().getMethodNumberer() );
                    addType( base, pa.reachingObjects( m, s, base ).possibleTypes().iterator() );
                    if( pa instanceof DumbPointerAnalysis ) {
                        vcs.noMoreTypes();
                    }
                }
            }
        }
    }
    public void addType( Local l, Type t, List edges ) {
        HashSet vcss = (HashSet) localToVCS.get( l );
        if( vcss != null ) {
            for( Iterator vcsIt = vcss.iterator(); vcsIt.hasNext(); ) {
                final VirtualCallSite vcs = (VirtualCallSite) vcsIt.next();
                NumberedSet targets = new NumberedSet( Scene.v().getMethodNumberer() );
                vcs.addType( t, targets );
                if( edges != null ) {
                    edges.add( vcs );
                }
                for( Iterator smIt = targets.iterator(); smIt.hasNext(); ) {
                    final SootMethod sm = (SootMethod) smIt.next();
                    if( reachableMethods.add( sm ) ) {
                        worklist.add( sm );
                    }
                    if( edges != null ) {
                        edges.add( sm );
                    }
                }
            }
        }
    }
    public void addType( Local l, Iterator types ) {
        HashSet vcss = (HashSet) localToVCS.get( l );
        if( vcss != null ) {
            for( Iterator vcsIt = vcss.iterator(); vcsIt.hasNext(); ) {
                final VirtualCallSite vcs = (VirtualCallSite) vcsIt.next();
                NumberedSet targets = new NumberedSet( Scene.v().getMethodNumberer() );
                while( types.hasNext() ) {
                    vcs.addType( (Type) types.next(), targets );
                }
                for( Iterator smIt = targets.iterator(); smIt.hasNext(); ) {
                    final SootMethod sm = (SootMethod) smIt.next();
                    if( reachableMethods.add( sm ) ) worklist.add( sm );
                }
            }
        }
    }
    public Iterator getReachableMethods() {
        return reachableMethods.iterator();
    }
}


