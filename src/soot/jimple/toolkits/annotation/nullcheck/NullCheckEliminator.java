/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ganesh Sittampalam
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

package soot.jimple.toolkits.annotation.nullcheck;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Immediate;
import soot.Value;
import soot.jimple.BinopExpr;
import soot.jimple.EqExpr;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.NeExpr;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;

public class NullCheckEliminator extends BodyTransformer {

    public static class AnalysisFactory {
	public NullnessAnalysis newAnalysis(UnitGraph g) {
	    return new NullnessAnalysis(g);
	}
    }

    private AnalysisFactory analysisFactory;

    public NullCheckEliminator() {
	this(new AnalysisFactory());
    }

    public NullCheckEliminator(AnalysisFactory f) {
	this.analysisFactory=f;
    }

    public void internalTransform(Body body, String phaseName, Map options) {

	// really, the analysis should be able to use its own results to determine
	// that some branches are dead, but since it doesn't we just iterate.
	boolean changed;
	do {
	    changed=false;

	    NullnessAnalysis analysis=analysisFactory.newAnalysis(new ExceptionalUnitGraph(body));
	    
	    Chain units=body.getUnits();
	    Stmt s;
	    for(s=(Stmt) units.getFirst();s!=null;s=(Stmt) units.getSuccOf(s)) {
		if(!(s instanceof IfStmt)) continue;
		IfStmt is=(IfStmt) s;
		Value c=is.getCondition();
		if(!(c instanceof EqExpr || c instanceof NeExpr)) continue;
		BinopExpr e=(BinopExpr) c;
		Immediate i=null;
		if(e.getOp1() instanceof NullConstant) i=(Immediate) e.getOp2();
		if(e.getOp2() instanceof NullConstant) i=(Immediate) e.getOp1();
		if(i==null) continue;
		boolean alwaysNull = analysis.isAlwaysNullBefore(s, i);
		boolean alwaysNonNull = analysis.isAlwaysNonNullBefore(s, i);
		int elim=0; // -1 => condition is false, 1 => condition is true
		if(alwaysNonNull) elim=c instanceof EqExpr ? -1 : 1;
		if(alwaysNull) elim=c instanceof EqExpr ? 1 : -1;
		Stmt newstmt=null;
		if(elim==-1) newstmt=Jimple.v().newNopStmt();
		if(elim==1) newstmt=Jimple.v().newGotoStmt(is.getTarget());
		if(newstmt!=null) {
		    units.swapWith(s,newstmt);
		    s=newstmt;
		    changed=true;
		}
	    }
	} while(changed);
    }

}