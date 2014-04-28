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
import java.io.File;
import soot.*;
import soot.util.dot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;

/**
 * Intra-procedural purity-graph analysis.
 *
 * You must pass an AbstractInterproceduralAnalysis object so that the
 * intraprocedural part can resolve the effect of method calls.
 * This manipulates PurityGraphBox.
 */
public class PurityIntraproceduralAnalysis extends ForwardFlowAnalysis
{

    AbstractInterproceduralAnalysis inter;

    protected Object newInitialFlow()
    { return new PurityGraphBox(); }

    protected Object entryInitialFlow()
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


    protected void flowThrough(Object inValue, Object unit, Object outValue)
    {
	PurityGraphBox i  = (PurityGraphBox)inValue;
	PurityGraphBox o  = (PurityGraphBox)outValue;
	Stmt         stmt = (Stmt)unit;

	o.g = new PurityGraph(i.g);
	
	// ********************
	// BIG PATTERN MATCHING
	// ********************

	// I throw much "match failure" Errors to ease debugging...
	// => we could optimize the pattern matching a little bit

	//G.v().out.println(" | |- exec "+stmt);


	///////////
	// Calls //
	///////////
	if (stmt.containsInvokeExpr()) {
	    inter.analyseCall(inValue, stmt, outValue);
	}

	/////////////
	// AssignStmt
	/////////////

	else if (stmt instanceof AssignStmt) {
	    Value leftOp = ((AssignStmt)stmt).getLeftOp();
	    Value rightOp = ((AssignStmt)stmt).getRightOp();

	    // v = ...
	    if (leftOp instanceof Local) {
		Local left = (Local)leftOp;

		// remove optional cast
		if (rightOp instanceof CastExpr) 
		    rightOp = ((CastExpr)rightOp).getOp();

		// ignore primitive types
		if (!(left.getType() instanceof RefLikeType)) {
		}

		// v = v
		else if (rightOp instanceof Local) {
		    Local right = (Local) rightOp;
		    o.g.assignLocalToLocal(right, left);
		}

		// v = v[i]
		else if (rightOp instanceof ArrayRef) {
		    Local right = (Local) ((ArrayRef)rightOp).getBase();
		    o.g.assignFieldToLocal(stmt, right, "[]", left);
		}

		// v = v.f
		else if (rightOp instanceof InstanceFieldRef) {
		    Local  right  = 
			(Local) ((InstanceFieldRef)rightOp).getBase();
		    String field = 
			((InstanceFieldRef)rightOp).getField().getName();
		    o.g.assignFieldToLocal(stmt, right, field, left);
		}

		// v = C.f
		else if (rightOp instanceof StaticFieldRef) {
		    o.g.localIsUnknown(left);
		}

		// v = cst
		else if (rightOp instanceof Constant) {
		    // do nothing...
		}


		// v = new / newarray / newmultiarray
		else if (rightOp instanceof AnyNewExpr)  {
		    o.g.assignNewToLocal(stmt, left);
		}

		// v = binary or unary operator
		else if (rightOp instanceof BinopExpr ||
			 rightOp instanceof UnopExpr ||
			 rightOp instanceof InstanceOfExpr) {
		    // do nothing...
		}

		else throw new Error("AssignStmt match failure (rightOp)"+stmt);
	    }

	    // v[i] = ...
	    else if (leftOp instanceof ArrayRef) {
		Local left = (Local) ((ArrayRef)leftOp).getBase();

		// v[i] = v
		if (rightOp instanceof Local) {
		    Local right = (Local)rightOp;
		    if (right.getType() instanceof RefLikeType)
			o.g.assignLocalToField(right, left, "[]");
		    else
			o.g.mutateField(left, "[]");
		}

		// v[i] = cst
		else if (rightOp instanceof Constant)
		    o.g.mutateField(left, "[]");
		
		else throw new Error("AssignStmt match failure (rightOp)"+stmt);
	    }

	    // v.f = ...
	    else if (leftOp instanceof InstanceFieldRef) {
		Local  left  = (Local) ((InstanceFieldRef)leftOp).getBase();
		String field = ((InstanceFieldRef)leftOp).getField().getName();

		// v.f = v
		if (rightOp instanceof Local) {
		    Local right = (Local)rightOp;
		    // ignore primitive types
		    if (right.getType() instanceof RefLikeType)
			o.g.assignLocalToField(right, left, field);
		    else
			o.g.mutateField(left, field);
		}

		// v.f = cst
		else if (rightOp instanceof Constant) 
		    o.g.mutateField(left, field);
		
		else throw new Error("AssignStmt match failure (rightOp) "+stmt);
	    }

	    // C.f = ...
	    else if (leftOp instanceof StaticFieldRef) {
		String field = ((StaticFieldRef)leftOp).getField().getName();
		
		// C.f = v
		if (rightOp instanceof Local) {
		    Local right = (Local)rightOp;
		    if (right.getType() instanceof RefLikeType)
			o.g.assignLocalToStaticField(right, field);
		    else
			o.g.mutateStaticField(field);
		}

		// C.f = cst
		else if (rightOp instanceof Constant)
		    o.g.mutateStaticField(field);

		else throw new Error("AssignStmt match failure (rightOp) "+stmt);
	    }

	    else throw new Error("AssignStmt match failure (leftOp) "+stmt);

	}


	///////////////
	// IdentityStmt
	///////////////

	else if (stmt instanceof IdentityStmt) {
	    Local left    = (Local)((IdentityStmt)stmt).getLeftOp();
	    Value rightOp = ((IdentityStmt)stmt).getRightOp();
	    
	    if (rightOp instanceof ThisRef) {
		o.g.assignThisToLocal(left);
	    }

	    else if (rightOp instanceof ParameterRef) {
		ParameterRef p = (ParameterRef)rightOp;
		// ignore primitive types
		if (p.getType() instanceof RefLikeType)
		    o.g.assignParamToLocal(p.getIndex(),left);
	    }

	    else if (rightOp instanceof CaughtExceptionRef) {
		// local =  exception
		o.g.localIsUnknown(left);
	    }

	    else throw new Error("IdentityStmt match failure (rightOp) "+stmt);

	}


	////////////
	// ThrowStmt
	////////////

	else if (stmt instanceof ThrowStmt) {
	    Value op = ((ThrowStmt)stmt).getOp();

	    if (op instanceof Local) {
		Local v = (Local)op;
		o.g.localEscapes(v);
	    }

	    else if (op instanceof Constant) {
		// do nothing...
	    }

	    else throw new Error("ThrowStmt match failure "+stmt);
	}


	/////////////
	// ReturnStmt
	/////////////

	else if (stmt instanceof ReturnVoidStmt) {
	    // do nothing...
	}

	else if (stmt instanceof ReturnStmt) {
	    Value v = ((ReturnStmt)stmt).getOp();

	    if (v instanceof Local) {
		// ignore primitive types
		if (v.getType() instanceof RefLikeType)
		    o.g.returnLocal((Local)v);
	    }

	    else if (v instanceof Constant) {
		// do nothing...
	    }

	    else throw new Error("ReturnStmt match failure "+stmt);

	}


	//////////
	// ignored
	//////////
	
	else if (stmt instanceof IfStmt ||
		 stmt instanceof GotoStmt ||
		 stmt instanceof LookupSwitchStmt ||
		 stmt instanceof TableSwitchStmt ||
		 stmt instanceof MonitorStmt ||
		 stmt instanceof BreakpointStmt ||
		 stmt instanceof NopStmt) {
	    // do nothing...
	}
		 

	else throw new Error("Stmt match faliure "+stmt);

	//o.g.updateStat();
    }


    /**
     * Draw the result of the intra-procedural analysis as one big dot file,
     * named className.methodName.dot, containing one purity graph for each
     * statement in the method.
     */
    public void drawAsOneDot(String prefix, String name)
    {
	DotGraph dot = new DotGraph(name);
	dot.setGraphLabel(name);
	dot.setGraphAttribute("compound","true");
	dot.setGraphAttribute("rankdir","LR");
	Map<Unit, Integer> node = new HashMap<Unit, Integer>();
	int id = 0;
	Iterator it = graph.iterator();
	while (it.hasNext()) {
	    Unit stmt = (Unit)it.next();
	    PurityGraphBox ref = (PurityGraphBox) getFlowAfter(stmt);
	    DotGraph       sub = dot.createSubGraph("cluster"+id);
	    DotGraphNode label = sub.drawNode("head"+id);
	    String lbl = stmt.toString();
	    if (lbl.startsWith("lookupswitch")) lbl = "lookupswitch...";
	    if (lbl.startsWith("tableswitch"))  lbl = "tableswitch...";
	    sub.setGraphLabel(" ");
	    label.setLabel(lbl);
	    label.setAttribute("fontsize","18");
	    label.setShape("box");
	    ref.g.fillDotGraph("X"+id,sub);
	    node.put(stmt,new Integer(id));
	    id++;
	}
	it = graph.iterator();
	while (it.hasNext()) {
	    Object src = it.next();
	    Iterator itt = graph.getSuccsOf(src).iterator();
	    while (itt.hasNext()) {
		Object dst = itt.next();
		DotGraphEdge edge =
		    dot.drawEdge("head"+node.get(src),"head"+node.get(dst));
		edge.setAttribute("ltail", "cluster"+node.get(src));
		edge.setAttribute("lhead", "cluster"+node.get(dst));
	    }
	}

	File f = new File (SourceLocator.v().getOutputDir(),
			   prefix+name+DotGraph.DOT_EXTENSION);
	dot.plot(f.getPath());
    }


    /**
     * Put into dst the purity graph obtained by merging all purity graphs at
     * the method return.
     * It is a valid summary that can be used in methodCall if you do
     * interprocedural analysis.
     * 
     */
    public void copyResult(Object dst)
    {
	PurityGraph r = new PurityGraph();
	Iterator it = graph.getTails().iterator();
	while (it.hasNext()) {
	    Stmt stmt = (Stmt)it.next();
	    PurityGraphBox ref = (PurityGraphBox) getFlowAfter(stmt);
	    r.union(ref.g);
	}
	r.removeLocals();
	//r.simplifyLoad();
	//r.simplifyInside();
	//r.updateStat();
	((PurityGraphBox)dst).g = r;
    }

    /**
     * Perform purity analysis on the Jimple unit graph g, as part of
     * a larger interprocedural analysis.
     * Once constructed, you may call copyResult and drawAsOneDot to query
     * the analysis result.
     */
    PurityIntraproceduralAnalysis(UnitGraph g,
				  AbstractInterproceduralAnalysis inter)
    {
	super(g);
	this.inter = inter;
	doAnalysis();
    }
}


