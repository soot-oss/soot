package soot.jimple.toolkits.pointer;
import soot.jimple.toolkits.pointer.kloj.*;
import soot.tagkit.*;
import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.*;
import java.io.*;

public class SideEffectTagger extends BodyTransformer
{ 
    public static int numRWs = 0;
    public static int numWRs = 0;
    public static int numRRs = 0;
    public static int numWWs = 0;
    public static int numNatives = 0;
    public static Date startTime = null;
    private static SideEffectTagger instance = new SideEffectTagger();
    private SideEffectTagger() {}
    boolean optionDontTag = false;
    boolean optionNaive = false;

    public static SideEffectTagger v() { return instance; }

    public String getDeclaredOptions() { return super.getDeclaredOptions() +
	" dont-tag max-size naive "; }

    public String getDefaultOptions() { return " max-size:1000000 "; }

    protected class UniqueRWSets {
	protected ArrayList l = new ArrayList();
	RWSet getUnique( RWSet s ) {
	    if( s == null ) return s;
	    for( Iterator retIt = l.iterator(); retIt.hasNext(); ) {
	        final RWSet ret = (RWSet) retIt.next();
		if( ret.isEquivTo( s ) ) return ret;
	    }
	    l.add( s );
	    return s;
	}
	Iterator iterator() {
	    return l.iterator();
	}
	short indexOf( RWSet s ) {
	    short i = 0;
	    for( Iterator retIt = l.iterator(); retIt.hasNext(); ) {
	        final RWSet ret = (RWSet) retIt.next();
		if( ret.isEquivTo( s ) ) return i;
		i++;
	    }
	    return -1;
	}
    }

    protected void initializationStuff( String phaseName ) {
	if( !Scene.v().hasActiveInvokeGraph() ) {
	    InvokeGraphBuilder.v().transform( phaseName + ".igb" );
	}
        Union.factory = new UnionFactory() {
	    //ReallyCheapRasUnion ru =  new ReallyCheapRasUnion();
	    //public Union newUnion() { return new RasUnion(); }
	    public Union newUnion() { return new MemoryEfficientRasUnion(); }
	};

	if( startTime == null ) {
	    startTime = new Date();
	}
    }
    protected Object keyFor( Stmt s ) {
	if( s.containsInvokeExpr() ) {
	    if( optionNaive ) throw new RuntimeException( "shouldn't get here" );
	    InvokeGraph ig = Scene.v().getActiveInvokeGraph();
	    if( !ig.containsSite( s ) ) {
		return Collections.EMPTY_LIST;
	    }
	    return Scene.v().getActiveInvokeGraph().getTargetsOf( s );
	} else {
	    return s;
	}
    }
    protected void internalTransform(Body body, String phaseName, Map options)
    {
	initializationStuff( phaseName );
	SideEffectAnalysis sea = Scene.v().getActiveSideEffectAnalysis();
	optionNaive = Options.getBoolean( options, "naive" );
	if( !optionNaive ) {
	    sea.findNTRWSets( body.getMethod() );
	}
	HashMap stmtToReadSet = new HashMap();
	HashMap stmtToWriteSet = new HashMap();
	UniqueRWSets sets = new UniqueRWSets();
	optionDontTag = Options.getBoolean( options, "dont-tag" );
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
	for( Iterator outerIt = sets.iterator(); outerIt.hasNext(); ) {
	    final RWSet outer = (RWSet) outerIt.next();

	    for( Iterator innerIt = sets.iterator(); innerIt.hasNext(); ) {

	        final RWSet inner = (RWSet) innerIt.next();
		if( inner == outer ) break;
		if( outer.hasNonEmptyIntersection( inner ) ) {
                    //System.out.println( "inner set is: "+inner );
                    //System.out.println( "outer set is: "+outer );
		    graph.addEdge( sets.indexOf( outer ), sets.indexOf( inner ) );
		}
	    }
	}
	if( !optionDontTag ) {
	    body.getMethod().addTag( graph );
	}
	for( Iterator stmtIt = body.getUnits().iterator(); stmtIt.hasNext(); ) {
	    final Stmt stmt = (Stmt) stmtIt.next();
	    Object key;
	    if( optionNaive && stmt.containsInvokeExpr() ) {
		key = stmt;
	    } else {
		key = keyFor( stmt );
	    }
	    RWSet read = (RWSet) stmtToReadSet.get( key );
	    RWSet write = (RWSet) stmtToWriteSet.get( key );
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
		if( !optionDontTag ) stmt.addTag( tag );

		// The loop below is just fro calculating stats.
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
	    }
	}
    }
}


