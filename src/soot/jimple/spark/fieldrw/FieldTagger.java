package soot.jimple.spark.fieldrw;
import soot.tagkit.*;
import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.*;
import java.io.*;

public class FieldTagger extends BodyTransformer
{ 
    public FieldTagger( Singletons.Global g ) {}
    public static FieldTagger v() { return G.v().FieldTagger(); }

    private HashSet processedMethods = new HashSet();
    private HashMultiMap methodToWrite = new HashMultiMap();
    private HashMultiMap methodToRead = new HashMultiMap();

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

        CallGraph cg = Scene.v().getCallGraph();
        TransitiveTargets tt = new TransitiveTargets( cg );
statement: for( Iterator sIt = body.getUnits().iterator(); sIt.hasNext(); ) {     final Stmt s = (Stmt) sIt.next();
            HashSet read = new HashSet();
            HashSet write = new HashSet();
            Iterator it = tt.iterator( s );
            while( it.hasNext() ) {
                SootMethod target = (SootMethod) it.next();
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


