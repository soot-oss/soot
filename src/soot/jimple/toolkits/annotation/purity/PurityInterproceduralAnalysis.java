/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Antoine Mine
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

/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;
import java.util.*;
import soot.*;
import soot.util.dot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import soot.toolkits.graph.*;
import soot.options.PurityOptions;
import soot.tagkit.*;

public class PurityInterproceduralAnalysis 
    extends AbstractInterproceduralAnalysis {

    // Note: these method lists are adapted to JDK-1.4.2.06 and may
    // not work for other versions

    // unanalysed methods assumed pure (& return a new obj)
    // class name prefix / method name
    static private final String[][] pureMethods =
    { 	{"java.lang.","valueOf"},
	{"java.","equals"},{"javax.","equals"},{"sun.","equals"},
	{"java.","compare"}, {"javax.","compare"},{"sun.","compare"},
	{"java.","getClass"},{"javax.","getClass"},{"sun.","getClass"},
	{"java.","hashCode"},{"javax.","hashCode"},{"sun.","hashCode"},
	{"java.","toString"},{"javax.","toString"},{"sun.","toString"},
	{"java.","valueOf"},{"javax.","valueOf"},{"sun.","valueOf"},
	{"java.","compareTo"},{"javax.","compareTo"},{"sun.","compareTo"},

	{"java.lang.System","identityHashCode"},

	// we assume that all standard class initialisers are pure!!!
	{"java.","<clinit>"}, {"javax.","<clinit>"}, {"sun.","<clinit>"},
	
	// if we define these as pure, the analysis will find them impure as
	// they call static native functions that could, in theory,
	// change the whole program state under our feets
	{ "java.lang.Math","abs" },
	{ "java.lang.Math","acos" },
	{ "java.lang.Math","asin" },
	{ "java.lang.Math","atan" },
	{ "java.lang.Math","atan2" },
	{ "java.lang.Math","ceil" },
	{ "java.lang.Math","cos" },
	{ "java.lang.Math","exp" },
	{ "java.lang.Math","floor" },
	{ "java.lang.Math","IEEEremainder" },
	{ "java.lang.Math","log" },
	{ "java.lang.Math","max" },
	{ "java.lang.Math","min" },
	{ "java.lang.Math","pow" },
	{ "java.lang.Math","rint" },
	{ "java.lang.Math","round" },
	{ "java.lang.Math","sin" },
	{ "java.lang.Math","sqrt" },
	{ "java.lang.Math","tan" },
	// TODO: put StrictMath as well ?

	{"java.lang.Throwable", "<init>"},

	// to break the cycle exception -> getCharsAt -> exception
	{"java.lang.StringIndexOutOfBoundsException","<init>"}
    };

    // unanalysed methods that modify the whole environment
    static private final String[][] impureMethods =
    { 
	{"java.io.","<init>"},
	{"java.io.","close"},
	{"java.io.","read"},
	{"java.io.","write"},
	{"java.io.","flush"}, 
	{"java.io.","flushBuffer"}, 
	{"java.io.","print"}, 
	{"java.io.","println"}, 

 	{"java.lang.Runtime","exit"},

	/*
	// for soot...
	{"java.io.","skip"},
	{"java.io.","ensureOpen"},
	{"java.io.","fill"},
	{"java.io.","readLine"},
	{"java.io.","available"},
	{"java.io.","mark"},
	{"java.io.","reset"},
	{"java.io.","toByteArray"},
	{"java.io.","size"},
	{"java.io.","writeTo"},
	{"java.io.","readBoolean"},
	{"java.io.","readChar"},
	{"java.io.","readDouble"},
	{"java.io.","readFloat"},
	{"java.io.","readByte"},
	{"java.io.","readShort"},
	{"java.io.","readInt"},
	{"java.io.","readLong"},
	{"java.io.","readUnsignedByte"},
	{"java.io.","readUnsignedShort"},
	{"java.io.","readUTF"},
	{"java.io.","readFully"},
	{"java.io.","writeBoolean"},
	{"java.io.","writeChar"},
	{"java.io.","writeChars"},
	{"java.io.","writeDouble"},
	{"java.io.","writeFloat"},
	{"java.io.","writeByte"},
	{"java.io.","writeBytes"},
	{"java.io.","writeShort"},
	{"java.io.","writeInt"},
	{"java.io.","writeLong"},
	{"java.io.","writeUTF"},
	{"java.io.","canRead"},
	{"java.io.","delete"},
	{"java.io.","exists"},
	{"java.io.","isDirectory"},
	{"java.io.","isFile"},
	{"java.io.","mkdir"},
	{"java.io.","mkdirs"},
	{"java.io.","getAbsoluteFile"},
	{"java.io.","getCanonicalFile"},
	{"java.io.","getParentFile"},
	{"java.io.","listFiles"},
	{"java.io.","getAbsolutePath"},
	{"java.io.","getCanonicalPath"},
	{"java.io.","getName"},
	{"java.io.","getParent"},
	{"java.io.","getPath"},
	{"java.io.","list"},
	{"java.io.","toURI"},
	{"java.io.","lastModified"},
	{"java.io.","length"},
	{"java.io.","implies"},
	{"java.io.","newPermissionCollection"},
	{"java.io.","getLineNumber"},
	{"java.io.","enableResolveObject"},
	{"java.io.","readClassDescriptor"},
	{"java.io.","readFields"},
	{"java.io.","readObject"},
	{"java.io.","readUnshared"},
	{"java.io.","defaultReadObject"},
	{"java.io.","defaultWriteObject"},
	{"java.io.","putFields"},
	{"java.io.","writeFields"},
	{"java.io.","writeObject"},
	{"java.io.","writeUnshared"},
	{"java.io.","unread"},
	{"java.io.","lineno"},
	{"java.io.","nextToken"},
	{"java.io.","commentChar"},
	{"java.io.","lowerCaseMode"},
	{"java.io.","ordinaryChar"},
	{"java.io.","quoteChar"},
	{"java.io.","resetSyntax"},
	{"java.io.","slashSlashComments"},
	{"java.io.","slashSltarComments"},
	{"java.io.","whitespaceChars"},
	{"java.io.","wordChars"},
	{"java.io.","markSupported"},
	{"java.","getCause"},
	{"java.","getMessage"},
	{"java.","getReason"},
	*/

    };
    
    // unanalysed methods that alter its arguments, but have no side effect
    static private final String[][] alterMethods =
    { 
	{"java.lang.System","arraycopy"},

	// these are really huge methods used internally by StringBuffer
	// printing => put here to speed-up the analysis
	{"java.lang.FloatingDecimal","dtoa"},
	{"java.lang.FloatingDecimal","developLongDigits"},
	{"java.lang.FloatingDecimal","big5pow"},
	{"java.lang.FloatingDecimal","getChars"},
	{"java.lang.FloatingDecimal","roundup"},
    };

    /** Filter out some method. */
    static private class Filter implements SootMethodFilter {
	public boolean want(SootMethod method) { 
	    // could be optimized with HashSet....
	    String c = method.getDeclaringClass().toString();
	    String m = method.getName();
	    String[][] o = PurityInterproceduralAnalysis.pureMethods;
	    for (String[] element : o)
			if (m.equals(element[1]) && c.startsWith(element[0])) return false;
	    o = PurityInterproceduralAnalysis.impureMethods;
	    for (String[] element : o)
			if (m.equals(element[1]) && c.startsWith(element[0])) return false;
	    o = PurityInterproceduralAnalysis.alterMethods;
	    for (String[] element : o)
			if (m.equals(element[1]) && c.startsWith(element[0])) return false;
	    return true;
	}
    }
    
    /** The constructor does it all! */
    PurityInterproceduralAnalysis(CallGraph        cg,
				  Iterator<SootMethod>         heads,
				  PurityOptions    opts)
    {
	super(cg,new Filter(),heads, opts.dump_cg());
	
	if (opts.dump_cg()) {
	    G.v().out.println("[AM] Dumping empty .dot call-graph");
	    drawAsOneDot("EmptyCallGraph");
	}

	Date start = new Date();
	G.v().out.println("[AM] Analysis began");
	doAnalysis(opts.verbose());
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
	    drawAsManyDot("Summary_",false);
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
		if (opts.verbose()) G.v().out.println("  |- "+method);
		PurityIntraproceduralAnalysis r = 
		    new PurityIntraproceduralAnalysis(graph, this);
		r.drawAsOneDot("Intra_",method.toString());
		PurityGraphBox b = new PurityGraphBox();
		r.copyResult(b);
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
		/* m.addTag(new GenericAttribute("isPure",
		   (new String(isPure?"yes":"no")).getBytes()));
		*/
		m.addTag(new StringTag("purity: "+(isPure?"pure":"impure")));
		if(isPure && opts.annotate())
			m.addTag(new GenericAttribute("Pure", new byte[0]));
		if (opts.print()) 
		    G.v().out.println("  |- method "+m.toString()+" is "+(isPure?"pure":"impure"));

		// param & this ro / safety
		if (!m.isStatic()) {
		    int status = b.g.thisStatus();
		    String s;
		    switch (status) {
		    case PurityGraph.PARAM_RW: s = "read/write";break;
		    case PurityGraph.PARAM_RO: s = "read-only";break;
		    case PurityGraph.PARAM_SAFE: s = "Safe";break;
		    default: s = "unknown";
		    }
		    /*  m.addTag(new GenericAttribute("thisStatus",s.getBytes()));
		     */
		    m.addTag(new StringTag("this: "+s));
		    if (opts.print()) G.v().out.println("  |   |- this is "+s);
		}
		
		Iterator itt = m.getParameterTypes().iterator();
		int i = 0;
		while (itt.hasNext()) {
		    if (itt.next() instanceof RefLikeType) {
			int status = b.g.paramStatus(i);
			String s;
			switch (status) {
			case PurityGraph.PARAM_RW: s = "read/write";break;
			case PurityGraph.PARAM_RO: s = "read-only";break;
			case PurityGraph.PARAM_SAFE: s = "safe";break;
			default: s = "unknown";
			}
			/*
			  m.addTag(new GenericAttribute("param"+i+"Status",
			  s.getBytes()));
			*/
			m.addTag(new StringTag("param"+i+": "+s));
			if (opts.print())
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
     * @see PurityGraph.freshGraph
     */
    protected Object summaryOfUnanalysedMethod(SootMethod method)
    {
	PurityGraphBox b = new PurityGraphBox();
	String c = method.getDeclaringClass().toString();
	String m = method.getName();

	// impure with side-effect, unless otherwise specified
	b.g = PurityGraph.conservativeGraph(method,true);

	String[][] o = PurityInterproceduralAnalysis.pureMethods;
	for (String[] element : o)
		if (m.equals(element[1]) && c.startsWith(element[0]))
		b.g = PurityGraph.freshGraph(method);

	o = PurityInterproceduralAnalysis.alterMethods;
	for (String[] element : o)
		if (m.equals(element[1]) && c.startsWith(element[0]))
		b.g = PurityGraph.conservativeGraph(method,false);

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
