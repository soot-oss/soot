/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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
    public static FieldTagger v() { return G.v().soot_jimple_spark_fieldrw_FieldTagger(); }

    private HashSet processedMethods = new HashSet();
    private HashMultiMap methodToWrite = new HashMultiMap();
    private HashMultiMap methodToRead = new HashMultiMap();

    protected void ensureProcessed( SootMethod m ) {
        if( processedMethods.contains(m) ) return;
        processedMethods.add(m);
        if( !m.isConcrete() ) return;
        if( m.isPhantom() ) return;
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
        int threshold = PhaseOptions.getInt( options, "threshold" );

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
                if( target.isPhantom() ) continue statement;
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


