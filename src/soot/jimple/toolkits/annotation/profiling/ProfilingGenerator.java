package soot.jimple.toolkits.annotation.profiling;

import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.util.*;

import soot.tagkit.*;

import java.util.*;

public class ProfilingGenerator extends BodyTransformer
{
    private static ProfilingGenerator instance =
	new ProfilingGenerator();

    private ProfilingGenerator() {};

    public static ProfilingGenerator v()
    {
	return instance;
    }

    public static String mainSignature = "void main(java.lang.String[])";

    //    private String mainSignature = "long runBenchmark(java.lang.String[])";

    static boolean debug = soot.Main.isInDebugMode;

    //    static boolean debug = true;

    public String getDeclaredOptions()
    {
	return super.getDeclaredOptions()+" enable notmainentry";
    }

    public void internalTransform(Body body, String phaseName, Map options)
    {
        boolean enable = Options.getBoolean(options, "enable");

	if (!enable)
	    return;

	boolean notmainentry = Options.getBoolean(options, "notmainentry");
	if (notmainentry)
	    mainSignature = "long runBenchmark(java.lang.String[])";

	{
	    SootMethod m = body.getMethod();

	    SootClass counterClass = Scene.v().loadClassAndSupport("MultiCounter");
	    SootMethod reset = counterClass.getMethod("void reset()") ;
	    SootMethod report = counterClass.getMethod("void report()") ;
	    
	    boolean isMainMethod= m.getSubSignature().equals(mainSignature);
	    
	    Chain units = body.getUnits();

	    if (isMainMethod)
	    {
	        units.addFirst(Jimple.v().newInvokeStmt(
			       Jimple.v().newStaticInvokeExpr(reset)));		
	    }

	    Iterator stmtIt = body.getUnits().snapshotIterator();
	    while (stmtIt.hasNext())
	    {
	        Stmt stmt = (Stmt)stmtIt.next();

		if (stmt instanceof InvokeStmt)
		{
		    InvokeExpr iexpr = (InvokeExpr)
		      ((InvokeStmt)stmt).getInvokeExpr() ;
		
		    if (iexpr instanceof StaticInvokeExpr)
		    {
		        SootMethod tempm = ((StaticInvokeExpr)iexpr).getMethod() ;
			
			if (tempm.getSignature().equals(
				"<java.lang.System: void exit(int)>"))
			{
			    units.insertBefore (Jimple.v().newInvokeStmt( 
				    Jimple.v().newStaticInvokeExpr(report)), stmt) ;

			}
		    }
		}
		else
		if (isMainMethod
		    && (  stmt instanceof ReturnStmt 
			 || stmt instanceof ReturnVoidStmt))
		{
		    units.insertBefore(Jimple.v().newInvokeStmt(
			    Jimple.v().newStaticInvokeExpr(report)), stmt);				 
		}
	    }
	}
    }
}
