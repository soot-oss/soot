/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;
import java.util.*;
import java.io.*;
import soot.*;
import soot.util.*;
import soot.util.dot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.options.PurityOptions;
import soot.tagkit.*;

public class PurityInterproceduralAnalysis 
    extends AbstractInterproceduralAnalysis {

    /** The constructor does it all! */
    PurityInterproceduralAnalysis(CallGraph        cg,
				  SootMethodFilter filter,
				  Iterator         heads,
				  PurityOptions    opts)
    {
	super(cg,filter,heads);
	
	if (opts.dump_cg()) {
	    G.v().out.println("[AM] Dumping empty .dot call-graph");
	    drawAsOneDot("EmptyCallGraph");
	}

	Date start = new Date();
	G.v().out.println("[AM] Analysis began");
	doAnalysis();
	G.v().out.println("[AM] Analysis finished");
	Date finish = new Date();
	long runtime = finish.getTime() - start.getTime();
	G.v().out.println("[AM] run time: "+runtime/1000.+" s");

	if (opts.dump_cg()) {
	    G.v().out.println("[AM] Dumping annotated .dot call-graph");
	    drawAsOneDot("CallGraph");
	}

	if (opts.dump_summaries()) {
	    G.v().out.println("[AM] Dumping .dot summaries of analysed methods");
	    drawAsManyDot("Summary_");
	}

	if (opts.dump_intra()) {
	    G.v().out.println("[AM] Dumping .dot full intra-procedural method analyses");
	    // relaunch the interprocedural analysis once on each method
	    // to get a purity graph at each statement, not only summaries
	    Iterator it = getAnalysedMethods();
	    while (it.hasNext()) {
		SootMethod method = (SootMethod)it.next();
		Body body = method.retrieveActiveBody();
		ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body);
		//G.v().out.println("  |- "+method);
		PurityIntraproceduralAnalysis r = 
		    new PurityIntraproceduralAnalysis(graph, this);
		r.drawAsOneDot("Intra_",method.toString());
	    }
	}


	{
	    G.v().out.println("[AM] Annotate methods. ");
	    Iterator it = getAnalysedMethods();
	    while (it.hasNext()) {
		SootMethod m = (SootMethod)it.next();
		PurityGraphBox b = (PurityGraphBox)getSummaryFor(m);

		// purity
		boolean isPure;
		if (m.toString().indexOf("<init>")!=-1)
		    isPure = b.g.isPureConstructor() ;
		else 
		    isPure = b.g.isPure();
		m.addTag(new GenericAttribute("isPure",
	                 (new String(isPure?"yes":"no")).getBytes()));
		m.addTag(new StringTag("isPure: "+(isPure?"yes":"no")));
		G.v().out.println("  |- method "+m.toString()+" is "+(isPure?"PURE":"impure"));

		// param & this ro / safety
		if (!m.isStatic()) {
		    int status = b.g.thisStatus();
		    String s;
		    switch (status) {
		    case PurityGraph.PARAM_RW: s = "RW";break;
		    case PurityGraph.PARAM_RO: s = "RO";break;
		    case PurityGraph.PARAM_SAFE: s = "safe";break;
		    default: s = "unknown";
		    }
		    m.addTag(new GenericAttribute("thisStatus",s.getBytes()));
		    m.addTag(new StringTag("this: "+s));
		    G.v().out.println("  |   |- this is "+s);
		}
		
		Iterator itt = m.getParameterTypes().iterator();
		int i = 0;
		while (itt.hasNext()) {
		    if (itt.next() instanceof RefLikeType) {
			int status = b.g.paramStatus(i);
			String s;
			switch (status) {
			case PurityGraph.PARAM_RW: s = "RW";break;
			case PurityGraph.PARAM_RO: s = "RO";break;
			case PurityGraph.PARAM_SAFE: s = "safe";break;
			default: s = "unknown";
			}
			m.addTag(new GenericAttribute("param"+i+"Status",
						      s.getBytes()));
			m.addTag(new StringTag("param"+i+": "+s));
			G.v().out.println("  |   |- param "+i+" is "+s);
		    }
		    i++;
		}
	    }
	}

    }

    protected Object newInitialSummary()
    { return new PurityGraphBox(); }

    protected void merge(Object in1, Object in2, Object out)
    {
	PurityGraphBox i1  = (PurityGraphBox)in1;
	PurityGraphBox i2  = (PurityGraphBox)in2;
	PurityGraphBox o   = (PurityGraphBox)out;
	if (out!=i1) o.g = new PurityGraph(i1.g);
	o.g.union(i2.g);
    }

    protected void copy(Object source, Object dest)
    {
	PurityGraphBox src  = (PurityGraphBox)source;
	PurityGraphBox dst  = (PurityGraphBox)dest;
	dst.g = new PurityGraph(src.g);
    }
    
    protected void analyseMethod(SootMethod method,
				 Object     dst)
    {
	Body body = method.retrieveActiveBody();
	ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body);
	PurityIntraproceduralAnalysis r = 
	    new PurityIntraproceduralAnalysis(graph, this);
	r.copyResult(dst);
    }

    /**
     * @see PurityGraph.conservativeGraph
     */
    protected Object summaryOfUnanalysedMethod(SootMethod method)
    {
	PurityGraphBox b = new PurityGraphBox();
	b.g = PurityGraph.conservativeGraph(method);
	return b;
    }

    /**
     * @param stmt any statement containing an InvokeExpr
     * @see PurityGraph.methodCall
     */
    protected void applySummary(Object src,
				Stmt   stmt,
				Object summary,
				Object dst)
    {
	// extract call info
	InvokeExpr e = stmt.getInvokeExpr();
	Local ret = null;
	if (stmt instanceof AssignStmt) {
	    Local v = (Local)((AssignStmt)stmt).getLeftOp();
	    if (v.getType() instanceof RefLikeType) ret = v;
	}
	Local obj = null;
	if (!(e instanceof StaticInvokeExpr)) 
	    obj = (Local)((InstanceInvokeExpr)e).getBase();
	List args = e.getArgs();
	
	// call methoCall on the PurityGraph
	PurityGraphBox s = (PurityGraphBox)src;
	PurityGraphBox d = (PurityGraphBox)dst;
	PurityGraph g = new PurityGraph(s.g);
	g.methodCall(((PurityGraphBox)summary).g, obj, args, ret);
	d.g = g;
    }

    protected void fillDotGraph(String prefix, Object o, DotGraph out)
    {
	PurityGraphBox b = (PurityGraphBox)o;
	b.g.fillDotGraph(prefix, out);
    }

}
