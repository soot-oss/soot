package soot.jimple.toolkits.invoke;

import java.util.*;
import soot.util.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.jimple.spark.PointsToAnalysis;

public class InvokeGraphTrimmer
{
    PointsToAnalysis pa;
    InvokeGraph ig;

    public InvokeGraphTrimmer( PointsToAnalysis pa, InvokeGraph ig ) {
	this.pa = pa;
	this.ig = ig;
    }

    public void trimInvokeGraph()
    {
	G.v().out.println( "Trimming invoke graph" );

	FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();

        for( Iterator mIt = ig.getReachableMethods().iterator(); mIt.hasNext(); ) {

            final SootMethod m = (SootMethod) mIt.next();
            for( Iterator sIt = ig.getSitesOf( m ).iterator(); sIt.hasNext(); ) {
                final Stmt s = (Stmt) sIt.next();
                InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();
                
                if (! (ie instanceof VirtualInvokeExpr) && 
                    ! (ie instanceof InterfaceInvokeExpr) ) continue;

                Value base = ((InstanceInvokeExpr)ie).getBase();
                Type receiverType = base.getType();

                if(receiverType instanceof RefType)
                {
                    List validReachingTypes = new LinkedList( 
                        pa.reachingObjects( m, s, (Local) base )
                            .possibleTypes() );

                    Collection targets = fh.resolveConcreteDispatchWithoutFailing(validReachingTypes, ie.getMethod(), (RefType) base.getType() );

                    for( Iterator targetIt = new ArrayList(ig.getTargetsOf(s)).iterator(); targetIt.hasNext(); ) {

                        final SootMethod target = (SootMethod) targetIt.next();
                        if( !targets.contains( target ) ) {
                            ig.removeTarget( s, target );
                        }
                    }
                }
            }
        }
	ig.mcg.refresh();
	G.v().out.println( "Done trimming invoke graph" );
    }
}

