package soot.jimple.toolkits.pointer;

import soot.jimple.toolkits.invoke.*;
import java.util.*;
import soot.util.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

public class InvokeGraphTrimmer
{
    PointerAnalysis pa;
    InvokeGraph ig;

    public InvokeGraphTrimmer( PointerAnalysis pa, InvokeGraph ig ) {
	this.pa = pa;
	this.ig = ig;
    }

    public void trimInvokeGraph()
    {
	System.out.println( "Trimming invoke graph" );

        List appAndLibClasses = new ArrayList();
        appAndLibClasses.addAll(Scene.v().getApplicationClasses());
        appAndLibClasses.addAll(Scene.v().getLibraryClasses());

	FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();

        Iterator classesIt = appAndLibClasses.iterator();
        while (classesIt.hasNext())
        {
            SootClass c = (SootClass)classesIt.next();

            Iterator methodsIt = c.getMethods().iterator();
            while (methodsIt.hasNext())
            {
                SootMethod m = (SootMethod)methodsIt.next();

                if(!m.isConcrete())
                    continue;

                Body b = null;

		b = m.retrieveActiveBody();

                Iterator unitsIt = b.getUnits().iterator();
                while (unitsIt.hasNext())
                {
                    Stmt s = (Stmt)unitsIt.next();
                    if (!s.containsInvokeExpr())
                        continue;

                    if (!ig.mcg.isReachable(m.toString())) {
                        if (ig.containsSite(s)) {
                            ig.removeAllTargets(s);
                            ig.removeSite(s);
                        }
                        continue;
                    }

                    InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();
                    
                    //List ieSites = ig.getTargetsOf(s);

                    if (ie instanceof VirtualInvokeExpr ||
                        ie instanceof InterfaceInvokeExpr)
                    {
                        Value base = ((InstanceInvokeExpr)ie).getBase();
                        Type receiverType = base.getType();

                        if(receiverType instanceof RefType)
                        {
                            // we have, now, a set of reaching types for receiver.
                            // remove extra targets (by clearing targets and re-adding
                            // the ones that VTA doesn't rule out.)
			    List oldTargets = ig.getTargetsOf(s);
                            ig.removeAllTargets(s);

			    List validReachingTypes = new LinkedList( 
				pa.reachingObjects( m, s, (Local) base )
				    .possibleTypes() );

                            Collection targets = fh.resolveConcreteDispatch(validReachingTypes, ie.getMethod(), (RefType) base.getType() );
			    /*
			    if( targets.isEmpty() ) {
				System.out.println( "Couldn't resolve dispatch "+s+" in method "+m );
				System.out.println( "reaching types: "+validReachingTypes );
			    }
			    */
                            Iterator targetsIt = targets.iterator();
                            
                            while (targetsIt.hasNext()) {
				SootMethod target = (SootMethod) targetsIt.next();
                                ig.addTarget(s, target);
				if( !oldTargets.contains( target ) ) {
				    System.out.println( "Computed possible target "+
					    target+" for site "+s+" in method "+m+
					    "that wasn't there in CHA" );
				    System.out.println( "Type of base is "+base.getType() );
				    System.out.println( "Reaching types of base are "+validReachingTypes );
				    System.out.println( "Old targets are "+oldTargets );
				    System.out.println( "New targets are "+targets );
				}
			    }
                        }
                    }
                }
            }
        }
	ig.mcg.refresh();
    }
}

