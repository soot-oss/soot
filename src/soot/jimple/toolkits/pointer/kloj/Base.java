package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.invoke.*;
import java.util.*;
import soot.util.*;
import soot.jimple.spark.PointsToSet;

class Base extends NodePPG implements PointerAnalysis
{
    Scheduler s;
    Handler h;
    Map options;

    protected RasMap rasmap;
    Base( InvokeGraph ig, Scheduler s, Map options ) {
	super( ig );
	this.s = s;
	this.options = options;
	s.setBase( this );
	Union.factory = new UnionFactory() {
	    //ReallyCheapRasUnion ru =  new ReallyCheapRasUnion();
	    public Union newUnion() { return new RasUnion(); }
	    //public Union newUnion() { return new MemoryEfficientRasUnion(); }
	};
	if( Options.getString( options, "ras" ).equals( "bit" ) ) {
	    System.out.println( "Using BitRas" );
	    rasmap = new RasMap();
	    Ras.factory = new RasFactory() {
		public Ras newRas( Type t ) {
		    return new BitRas( t );
		}
	    };
	} else if( Options.getString( options, "ras" ).equals( "hybrid" ) ) {
	    System.out.println( "Using HybridRas" );
	    rasmap = new RasMap();
	    Ras.factory = new RasFactory() {
		public Ras newRas( Type t ) {
		    return new HybridRas( t );
		}
	    };
	} else if( Options.getString( options, "ras" ).equals( "colour" ) ) {
	    System.out.println( "Using ColourRas" );
	    rasmap = new RasMap() {
		public void nextIter() {
		    Iterator it = values().iterator();
		    while( it.hasNext() ) {
			Ras r = (Ras) it.next();
			r.nextIter();
		    }
		}
	    };
	    Ras.factory = new RasFactory() {
		public Ras newRas( Type t ) {
		    return new ColourRas( t );
		}
	    };
	} else if( Options.getString( options, "ras" ).equals( "hash" ) ) {
	    System.out.println( "Using HashRas" );
	    rasmap = new RasMap();
	    Ras.factory = new RasFactory() {
		public Ras newRas( Type t ) {
		    return new HashRas( t );
		}
	    };
	} else {
	    throw new RuntimeException( "You must specify a RAS implementation" );
	}
    }

    public void compute() {
	System.out.println( "Total locals: "+VarNode.getAll().size() );
	s.compute();
	boolean isColourRas = Options.getString( options, "ras" ).equals( "colour" );
	for( Iterator it =  new LinkedList( rasmap.keySet() ).iterator();
	    it.hasNext(); ) {
	    Object key = it.next();

	    if( key instanceof FieldRefNode ) {
		rasmap.remove( key );
	    } else if( isColourRas ) {
		ColourRas r = (ColourRas) rasmap.get( key );
		if( !r.white.isEmpty() || !r.gray.isEmpty() ) {
		    throw new RuntimeException( "ColourRas not finished computing" );
		}
		rasmap.put( key, r.black );
	    }
	}
	Ras.factory = new RasFactory() {
	    public Ras newRas( Type t ) {
		return new HybridRas( t );
	    }
	};
	Scene.v().releaseActiveFastHierarchy();
    }
    public void dumpStats() {
	int counts[] = new int[30001];
	if( false ) for( Iterator it = rasmap.keySet().iterator(); it.hasNext(); ) {
	    Object key = it.next();
	    int i =  ( (Ras) rasmap.get( key ) ).size();
	    if( i >= counts.length ) i = counts.length-1;
	    counts[ i ]++;
	    {
		Object o = key;
		if( ebbMap != null ) {
		    o = ebbMap.get( o );
		    if( o == null ) o = key;
		}
		Ras r = (Ras) rasmap.get( o );
		if( r != null ) {
		    System.out.println( ""+o+" is reached by "+r.possibleTypes() );
		} else {
		    System.out.println( ""+o+" has no ras" );
		}
	    }
	    if( false && ( i > 2300 || ( i > 2100 && i < 2200 ) || ( i > 1630 && i < 1640 ) ) ) {
		System.out.print( ""+i+": " );
		if( key instanceof Node ) {
		    Node n = (Node) key;
		    System.out.println( "Type: "+n.getType() );
		}
		if( key instanceof VarNode ) {
		    System.out.println( "VarNode");
		    Object val = ((VarNode) key).getVal();
		    System.out.println( ""+val );
		    System.out.println( "Method: "+((VarNode) key).m );
		} else if( key instanceof FieldRefNode ) {
		    System.out.println( "FieldRefNode");
		    System.out.println( "Var is "+((FieldRefNode) key).getBase().getVal()+" Field is "+
			((FieldRefNode) key).getField() );
		    Object val = ((FieldRefNode) key).getBase().getVal();
		    System.out.println( ""+val );
		    if( val instanceof Pair ) {
			Pair p = (Pair) val;
			System.out.println( "Pair: f = "+p.o1+" s = "+p.o2 );
		    }
		    System.out.println( "Method: "+((FieldRefNode) key).m );
		} else {
		    System.out.println( "Other");
		    System.out.println( ""+key );
		}
	    }
	}
	long mass = 0;
	for( int i=0; i < counts.length; i++ ) {
	    if( counts[i] > 0 ) {
		System.out.println( ""+i+" "+counts[i] );
		mass += counts[i] * i;
	    }
	}
	System.out.println( "Set Mass: "+mass );
	System.out.println( "Total locals: "+VarNode.getAll().size() );
	System.out.println( "Total l or a.f: "+rasmap.keySet().size() );
    }
    public void addAllocSites( VarNode v, Ras a ) {
	if( rasmap.putAll( v, a ) ) {
	    s.wroteTo( v );
	}
    }
    public void addAllocSites( SiteDotField v, Ras a ) {
	if( rasmap.putAll( v, a ) ) {
	    s.wroteTo( v );
	}
    }
    public void addAllocSite( VarNode v, AllocNode a ) {
	if( rasmap.put( v, a ) ) {
	    s.wroteTo( v );
	}
    }
    public void addAllocSite( SiteDotField v, AllocNode a ) {
	if( rasmap.put( v, a ) ) {
	    s.wroteTo( v );
	}
    }
    public boolean hasAllocSites( SiteDotField v ) {
	return rasmap.containsKey( v );
    }
    public Ras getAllocSites( SiteDotField v ) {
	return rasmap.lookup( v );
    }
    public Ras getAllocSites( VarNode v ) {
	return rasmap.lookup( v );
    }
    public void nextIter() {
	rasmap.nextIter();
    }
    public PointsToSet reachingObjects( SootMethod method, Stmt stmt, Local l ) {
	VarNode v = VarNode.v( l );
	if( v == null ) {
	    throw new RuntimeException( "Couldn't find varnode for local "+l+" in stmt "+stmt+" in method "+method );
	}
	if( ebbMap != null ) {
	    VarNode vv = (VarNode) ebbMap.get( v );
	    if( vv != null ) v = vv;
	}
	return (PointsToSet) rasmap.lookup( v );
    }
}

