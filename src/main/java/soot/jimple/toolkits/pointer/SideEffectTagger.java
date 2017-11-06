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

package soot.jimple.toolkits.pointer;
import soot.*;
import java.util.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.*;

public class SideEffectTagger extends BodyTransformer
{ 
    public SideEffectTagger( Singletons.Global g ) {}
    public static SideEffectTagger v() { return G.v().soot_jimple_toolkits_pointer_SideEffectTagger(); }

    public int numRWs = 0;
    public int numWRs = 0;
    public int numRRs = 0;
    public int numWWs = 0;
    public int numNatives = 0;
    public Date startTime = null;
    boolean optionNaive = false;
    private CallGraph cg;
    
    protected class UniqueRWSets {
	protected ArrayList<RWSet> l = new ArrayList<RWSet>();
	RWSet getUnique( RWSet s ) {
	    if( s == null ) return s;
	    for (RWSet ret : l) {
	        if( ret.isEquivTo( s ) ) return ret;
	    }
	    l.add( s );
	    return s;
	}
	Iterator<RWSet> iterator() {
	    return l.iterator();
	}
	short indexOf( RWSet s ) {
	    short i = 0;
	    for (RWSet ret : l) {
	        if( ret.isEquivTo( s ) ) return i;
		i++;
	    }
	    return -1;
	}
    }

    protected void initializationStuff( String phaseName ) {
        G.v().Union_factory = new UnionFactory() {
	    //ReallyCheapRasUnion ru =  new ReallyCheapRasUnion();
	    //public Union newUnion() { return new RasUnion(); }
	    public Union newUnion() { return new MemoryEfficientRasUnion(); }
	};

	if( startTime == null ) {
	    startTime = new Date();
	}
        cg = Scene.v().getCallGraph();
    }
    protected Object keyFor( Stmt s ) {
	if( s.containsInvokeExpr() ) {
	    if( optionNaive ) throw new RuntimeException( "shouldn't get here" );
            Iterator it = cg.edgesOutOf( s );
	    if( !it.hasNext() ) {
		return Collections.EMPTY_LIST;
	    }
            ArrayList ret = new ArrayList();
            while( it.hasNext() ) {
                ret.add( it.next() );
            }
            return ret;
	} else {
	    return s;
	}
    }
    protected void internalTransform(Body body, String phaseName, Map options)
    {
	initializationStuff( phaseName );
	SideEffectAnalysis sea = Scene.v().getSideEffectAnalysis();
	optionNaive = PhaseOptions.getBoolean( options, "naive" );
	if( !optionNaive ) {
	    sea.findNTRWSets( body.getMethod() );
	}
	HashMap<Object, RWSet> stmtToReadSet = new HashMap<Object, RWSet>();
	HashMap<Object, RWSet> stmtToWriteSet = new HashMap<Object, RWSet>();
	UniqueRWSets sets = new UniqueRWSets();
	boolean justDoTotallyConservativeThing = 
	    body.getMethod().getName().equals( "<clinit>" );
	for( Iterator stmtIt = body.getUnits().iterator(); stmtIt.hasNext(); ) {
	    final Stmt stmt = (Stmt) stmtIt.next();
	    if( justDoTotallyConservativeThing 
	    || ( optionNaive && stmt.containsInvokeExpr() ) ) {
		stmtToReadSet.put( stmt, sets.getUnique( new FullRWSet() ) );
		stmtToWriteSet.put( stmt, sets.getUnique( new FullRWSet() ) );
		continue;
	    }
	    Object key = keyFor( stmt );
	    if( !stmtToReadSet.containsKey( key ) ) {
		stmtToReadSet.put( key,
		    sets.getUnique( sea.readSet( body.getMethod(), stmt ) ) );
		stmtToWriteSet.put( key,
		    sets.getUnique( sea.writeSet( body.getMethod(), stmt ) ) );
	    }
	}
	DependenceGraph graph = new DependenceGraph();
	for( Iterator<RWSet> outerIt = sets.iterator(); outerIt.hasNext(); ) {
	    final RWSet outer = outerIt.next();

	    for( Iterator<RWSet> innerIt = sets.iterator(); innerIt.hasNext(); ) {

	        final RWSet inner = innerIt.next();
		if( inner == outer ) break;
		if( outer.hasNonEmptyIntersection( inner ) ) {
                    //G.v().out.println( "inner set is: "+inner );
                    //G.v().out.println( "outer set is: "+outer );
		    graph.addEdge( sets.indexOf( outer ), sets.indexOf( inner ) );
		}
	    }
	}
        body.getMethod().addTag( graph );
	for( Iterator stmtIt = body.getUnits().iterator(); stmtIt.hasNext(); ) {
	    final Stmt stmt = (Stmt) stmtIt.next();
	    Object key;
	    if( optionNaive && stmt.containsInvokeExpr() ) {
		key = stmt;
	    } else {
		key = keyFor( stmt );
	    }
	    RWSet read = stmtToReadSet.get( key );
	    RWSet write = stmtToWriteSet.get( key );
	    if( read != null || write != null ) {
		DependenceTag tag = new DependenceTag();
		if( read != null && read.getCallsNative() ) {
		    tag.setCallsNative();
		    numNatives++;
		} else if( write != null && write.getCallsNative() ) {
		    tag.setCallsNative();
		    numNatives++;
		}
		tag.setRead( sets.indexOf( read ) );
		tag.setWrite( sets.indexOf( write ) );
                stmt.addTag( tag );

		// The loop below is just for calculating stats.
                /*
		if( !justDoTotallyConservativeThing ) {
		    for( Iterator innerIt = body.getUnits().iterator(); innerIt.hasNext(); ) {
		        final Stmt inner = (Stmt) innerIt.next();
			Object ikey;
			if( optionNaive && inner.containsInvokeExpr() ) {
			    ikey = inner;
			} else {
			    ikey = keyFor( inner );
			}
			RWSet innerRead = (RWSet) stmtToReadSet.get( ikey );
			RWSet innerWrite = (RWSet) stmtToWriteSet.get( ikey );
			if( graph.areAdjacent( sets.indexOf( read ),
				    sets.indexOf( innerWrite ) ) ) numRWs++;
			if( graph.areAdjacent( sets.indexOf( write ),
				    sets.indexOf( innerRead ) ) ) numWRs++;
			if( inner == stmt ) continue;
			if( graph.areAdjacent( sets.indexOf( write ),
				    sets.indexOf( innerWrite ) ) ) numWWs++;
			if( graph.areAdjacent( sets.indexOf( read ),
				    sets.indexOf( innerRead ) ) ) numRRs++;
		    }
		}
                */
	    }
	}
    }
}


