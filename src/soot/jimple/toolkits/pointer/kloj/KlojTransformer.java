package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.util.*;
import soot.jimple.toolkits.pointer.*;
import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.invoke.*;
import java.io.*;

public class KlojTransformer extends SceneTransformer
{ 
    private static KlojTransformer instance = 
	new KlojTransformer();
    private KlojTransformer() {}
    private InvokeGraph ig;

    public static KlojTransformer v() { return instance; }

    public String getDeclaredOptions() { return super.getDeclaredOptions() +
	" method dump-pag ras ignore-types dump-pag-benchmark hash-ras ebb parms-as-fields parms-not-as-fields in-parms-as-fields add-local-after-load collapse-objects types-for-sites merge-stringbuffer simulate-natives dont-trim-callgraph"; }

    public String getDefaultOptions() { return " method:worklist ras:colour ebb:true simulate-natives "; }

    protected void internalTransform( String phaseName, Map options)
    {
	Date startIg = new Date();
	InvokeGraphBuilder.v().transform( phaseName + ".igb" );
	ig = Scene.v().getActiveInvokeGraph();
        System.out.println( ig.computeStats() );
	Date startBuild = new Date();
	System.out.println( "Invoke Graph built in "+(startBuild.getTime() - startIg.getTime() )/1000+" seconds." );
	NodePPG b;
	int maxRasSize = Options.getInt( options, "max-ras-size" );
	if( !Options.getBoolean( options, "ignore-types" ) ) {
	    Ras.fh = Scene.v().getOrMakeFastHierarchy();
	}
	if( Options.getBoolean( options, "dump-pag" ) ) {
	    b = new NodePPG( ig );
	} else if( Options.getBoolean( options, "dump-pag-benchmark" ) ) {
	    b = new BenchmarkDumper( ig );
	} else {
	    Handler h;
	    if( Options.getString( options, "ras" ).equals( "colour" ) ) {
		h = new ColourHandler();
	    } else {
		h = new SimpleHandler();
	    }
	    Scheduler s;
	    String method = Options.getString( options, "method" );
	    if( method.equals( "iter" ) ) {
		s = new IterativeScheduler( h );
	    } else if( method.equals( "worklist" ) ) {
		s = new WorklistScheduler( h );
	    } else if( method.equals( "df" ) ) {
		s = new DepthFirstScheduler( h );
	    } else if( method.equals( "merge-iter" ) ) {
		s = new MergingIterativeScheduler();
	    } else {
		throw new RuntimeException( "Unknown method specified" );
	    }
	    b = new Base( ig, s, options );
	}
	b.parmsAsFields = false;
	b.returnsAsFields = false;
	if( Options.getBoolean( options, "parms-as-fields" ) ) {
	    b.parmsAsFields = true;
	    b.returnsAsFields = true;
	}
	if( Options.getBoolean( options, "in-parms-as-fields" ) ) {
	    b.parmsAsFields = true;
	}
	if( Options.getBoolean( options, "collapse-objects" ) ) {
	    b.collapseObjects = true;
	}
	if( Options.getBoolean( options, "types-for-sites" ) ) {
	    b.typesForSites = true;
	}
	if( Options.getBoolean( options, "merge-stringbuffer" ) ) {
	    b.mergeStringbuffer = true;
	}
	if( Options.getBoolean( options, "simulate-natives" ) ) {
	    b.simulateNatives = true;
	    NativeHelper.register( new KlojNativeHelper( b ) );
	}
	b.build();
	Date startCompute = new Date();
	System.out.println( "Pointer Graph built in "+(startCompute.getTime() - startBuild.getTime() )/1000+" seconds." );
	if( Options.getBoolean( options, "add-local-after-load" ) ) {
	    b.addLocalAfterLoad();
	}
	if( Options.getBoolean( options, "ebb" ) ) {
	    b.collapseEBBs( Ras.fh );
	}
	b.compute();
	Date doneCompute = new Date();
	System.out.println( "Solution found in "+(doneCompute.getTime() - startCompute.getTime() )/1000+" seconds." );
	b.dumpStats();
	if( b instanceof PointerAnalysis ) {
	    if( !Options.getBoolean( options, "dont-trim-callgraph" ) ) {
		System.out.println( ig.computeStats() );
		new InvokeGraphTrimmer( (PointerAnalysis) b, ig ).trimInvokeGraph();
		System.out.println( ig.computeStats() );
		Date doneTrim = new Date();
		System.out.println( "Graph trimmed in "+(doneTrim.getTime() - doneCompute.getTime() )/1000+" seconds." );
	    }
	    Scene.v().setActivePointerAnalysis( (PointerAnalysis) b );
	}
    }
}


