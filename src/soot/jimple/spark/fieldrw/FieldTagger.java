package soot.jimple.spark.fieldrw;
import soot.tagkit.*;
import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.*;
import java.io.*;

public class FieldTagger extends BodyTransformer
{ 
    private static FieldTagger instance = new FieldTagger();
    private FieldTagger() {}

    public static FieldTagger v() { return instance; }

    private static HashSet processedMethods = new HashSet();
    private static HashMultiMap methodToWrite = new HashMultiMap();
    private static HashMultiMap methodToRead = new HashMultiMap();

    protected void ensureProcessed( SootMethod m ) {
        if( processedMethods.contains(m) ) return;
        processedMethods.add(m);
        if( !m.isConcrete() ) return;
        for( Iterator sIt = m.retrieveActiveBody().getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if( s instanceof AssignStmt ) {
                AssignStmt as = (AssignStmt) s;
                Value l = as.getLeftOp();
                if( l instanceof FieldRef ) {
                    methodToWrite.put( m, ((FieldRef) l).getField() );
                }
                Value r = as.getRightOp();
                if( r instanceof FieldRef ) {
                    methodToRead.put( m, ((FieldRef) r).getField() );
                }
            }
        }
    }
    protected void internalTransform(Body body, String phaseName, Map options)
    {
        int threshold = PackManager.getInt( options, "threshold" );

        ensureProcessed( body.getMethod() );

	if( !Scene.v().hasActiveInvokeGraph() ) {
	    InvokeGraphBuilder.v().transform( phaseName + ".igb" );
	}
        InvokeGraph ig = Scene.v().getActiveInvokeGraph();
statement: for( Iterator sIt = ig.getSitesOf( body.getMethod() ).iterator(); sIt.hasNext(); ) {     final Stmt s = (Stmt) sIt.next();
            HashSet transitiveTargets = new HashSet();
            for( Iterator targetIt = ig.getTargetsOf( s ).iterator(); targetIt.hasNext(); ) {
                final SootMethod target = (SootMethod) targetIt.next();
                transitiveTargets.add( target );
                transitiveTargets.addAll( ig.getTransitiveTargetsOf( target ) );
            }
            HashSet read = new HashSet();
            HashSet write = new HashSet();
            for( Iterator targetIt = transitiveTargets.iterator(); targetIt.hasNext(); ) {
                final SootMethod target = (SootMethod) targetIt.next();
                ensureProcessed( target );
                if( target.isNative() ) continue statement;
                read.addAll( methodToRead.get( target ) );
                write.addAll( methodToWrite.get( target ) );
                if( read.size() + write.size() > threshold ) {
                    continue statement;
                }
            }
            s.addTag( new FieldReadTag( read ) );
            s.addTag( new FieldWriteTag( write ) );
        }
    }
}


