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
import soot.options.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.util.queue.*;

/** Models the call graph.
 * @author Ondrej Lhotak
 */
public final class CallGraphBuilder
{ 
    private PointsToAnalysis pa;
    private CGOptions options;
    private ReachableMethods reachables;
    private boolean appOnly = false;
    private OnFlyCallGraphBuilder ofcgb;
    private CallGraph cg;

    public CallGraph getCallGraph() { return cg; }
    public ReachableMethods reachables() { return reachables; }

    public static ContextManager makeContextManager( CallGraph cg ) {
        CGOptions options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        if( options.context() == options.context_insens ) {
            return new ContextInsensitiveContextManager( cg );
        } else if( options.context() == options.context_1cfa ) {
            return new OneCFAContextManager( cg );
        } else if( options.context() == options.context_objsens ) {
            return new ObjSensContextManager( cg );
        } else throw new RuntimeException( "Unhandled context-sensitivity level." );
    }

    /** This constructor builds a complete call graph using the given
     * PointsToAnalysis to resolve virtual calls. */
    public CallGraphBuilder( PointsToAnalysis pa ) {
        this.pa = pa;
        options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        if( options.all_reachable() ) {
            List entryPoints = new ArrayList();
            entryPoints.addAll( EntryPoints.v().all() );
            entryPoints.addAll( EntryPoints.v().methodsOfApplicationClasses() );
            Scene.v().setEntryPoints( entryPoints );
        }
        cg = new CallGraph();
        Scene.v().setCallGraph( cg );
        reachables = Scene.v().getReachableMethods();
        ContextManager cm = makeContextManager(cg);
        ofcgb = new OnFlyCallGraphBuilder( cm, reachables );
   }
    /** This constructor builds the incomplete hack call graph for the
     * Dava ThrowFinder.
     * It uses all application class methods as entry points, and it ignores
     * any calls by non-application class methods.
     * Don't use this constructor if you need a real call graph. */
    public CallGraphBuilder() {
        G.v().out.println( "Warning: using incomplete callgraph containing "+
                "only application classes." );
        pa = soot.jimple.toolkits.pointer.DumbPointerAnalysis.v();
        options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        cg = new CallGraph();
        Scene.v().setCallGraph(cg);
        List entryPoints = new ArrayList();
        entryPoints.addAll( EntryPoints.v().methodsOfApplicationClasses() );
        entryPoints.addAll( EntryPoints.v().implicit() );
        reachables = new ReachableMethods( cg, entryPoints );
        appOnly = true;
        ContextManager cm = new ContextInsensitiveContextManager( cg );
        ofcgb = new OnFlyCallGraphBuilder( cm, reachables, true );
    }
    public void build() {
        QueueReader worklist = reachables.listener();
        while(true) {
            ofcgb.processReachables();
            reachables.update();
            MethodOrMethodContext momc = (MethodOrMethodContext) worklist.next();
            if( momc == null ) break;
            List receivers = (List) ofcgb.methodToReceivers().get(momc.method());
            if( receivers != null) for( Iterator receiverIt = receivers.iterator(); receiverIt.hasNext(); ) {     
                final Local receiver = (Local) receiverIt.next();
                final PointsToSet p2set = pa.reachingObjects( receiver );
                for( Iterator typeIt = p2set.possibleTypes().iterator(); typeIt.hasNext(); ) {
                    final Type type = (Type) typeIt.next();
                    ofcgb.addType( receiver, momc.context(), type, null );
                }
            }
            List stringConstants = (List) ofcgb.methodToStringConstants().get(momc.method());
            if( stringConstants != null ) for( Iterator stringConstantIt = stringConstants.iterator(); stringConstantIt.hasNext(); ) {     
                final Local stringConstant = (Local) stringConstantIt.next();
                PointsToSet p2set = pa.reachingObjects( stringConstant );
                // p2set_psc is legally allowed to be null according to the
                // Javadoc in PointsToSet presumably the
                // (stringConstants != null) test above is supposed to
                // be redundant with this one, but not in my
                // experience
                final Set p2set_psc = p2set.possibleStringConstants();
                if (p2set_psc != null) {
                    for( Iterator constantIt = p2set_psc.iterator(); constantIt.hasNext(); ) {
                        final String constant = (String) constantIt.next();
                        ofcgb.addStringConstant( stringConstant, momc.context(), constant, null );
                    }
                }
            }
        }
    }
}

