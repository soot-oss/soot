package soot.jimple.toolkits.pointer;
import soot.toolkits.graph.*;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.invoke.*;
import java.util.*;
import soot.util.*;
import java.io.*;

public class BenchmarkDumper extends NodePPG
{
    class SerialNumberer {
	int num = 0;
	HashMap map = new HashMap(4);
	Integer getNum( Object o ) {
	    Integer ret = (Integer) map.get( o );
	    if( ret == null ) {
		ret = new Integer( ++num );
		map.put( o, ret );
	    }
	    return ret;
	}

    }
    SerialNumberer allocSites = new SerialNumberer();
    SerialNumberer variables = new SerialNumberer();
    protected MultiMap assignments = new HashMultiMap();
    protected MultiMap news = new HashMultiMap();

    public void addAnyEdge( Object o, Object p ) {
	throw new RuntimeException( "Forgot to override something" );
    }
    public void addSimpleEdge( 
        Object src, Type srcType, 
        Object dest, Type destType )
    {
	assignments.put( variables.getNum( src ), variables.getNum( dest ) );
    }
    public void addLoadEdge( 
        Object base, Type baseType,
        Object field, Type fieldType, 
        Object dest, Type destType ) 
    {
	assignments.put( variables.getNum( field ), variables.getNum( dest ) );
    }
    public void addStoreEdge( 
        Object src, Type srcType,
        Object base, Type baseType,
        Object field, Type fieldType ) 
    {
	assignments.put( variables.getNum( src ), variables.getNum( field ) );
    }
    public void addNewEdge( 
        Expr newExpr, Type newExprType, 
        Object dest, Type destType ) 
    {
	news.put( allocSites.getNum( newExpr ), variables.getNum( dest ) );
    }
    public BenchmarkDumper( InvokeGraph ig ) {
	super( ig );
    }
    private void dumpEdgeSet( PrintStream out, MultiMap m ) {
	out.println( m.keySet().size() );
	for( Iterator it = m.keySet().iterator(); it.hasNext(); ) {
	    Integer n = (Integer) it.next();
	    out.print( n.intValue() );
	    for( Iterator it2 = m.get(n).iterator(); it2.hasNext(); ) {
		Integer n2 = (Integer) it2.next();
		out.print( " "+n2.intValue() );
	    }
	    out.println();
	}
    }
    public void compute() {
	PrintStream out = null;
	try {
	    out = new PrintStream( new FileOutputStream( "pag" ) );
	} catch( FileNotFoundException e ) {
	    return;
	}
	dumpEdgeSet( out, news );
	dumpEdgeSet( out, assignments );
    }
    public void dumpStats() {
    }
}

