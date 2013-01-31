/* Soot - a J*va Optimization Framework
 * Copyright (C) 2012 Richard Xiao
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
package soot.jimple.spark.geom.helper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Stmt;
import soot.jimple.spark.geom.dataRep.CallsiteContextVar;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.pag.LocalVarNode;

/**
 * Dump the pointers appear as the base pointers in store/load statements.
 * It is used for evaluating the points-to result outside soot.
 * 
 * @author xiao
 *
 */
public class PtInfoCollector 
{
	private GeomPointsTo geom;
	
	public PtInfoCollector()
	{
		geom = (GeomPointsTo)Scene.v().getPointsToAnalysis();
	}
	
	/**
	 * Output a list of pointers that appear at the pointer deference sites.
	 * @param geom
	 */
	public Set<IVarAbstraction> collectBasePointers( boolean isDump ) 
	{
		Value[] values = new Value[2];
		Set<IVarAbstraction> basePointers = new HashSet<IVarAbstraction>();
		
		for ( SootMethod sm : geom.getAllReachableMethods() ) {
			int sm_int = geom.getIDFromSootMethod(sm);
			if ( !geom.isReachableMethod(sm_int) )
				continue;
			if (!sm.isConcrete())
				continue;
			if (!sm.hasActiveBody()) {
				sm.retrieveActiveBody();
			}
			if ( !geom.isValidMethod(sm) )
				continue;
			
			// We first gather all the memory access expressions
			//access_expr.clear();
			for (Iterator stmts = sm.getActiveBody().getUnits().iterator(); stmts
					.hasNext();) {
				Stmt st = (Stmt) stmts.next();
				
				if ( st instanceof AssignStmt ) {
					AssignStmt a = (AssignStmt) st;
					values[0] = a.getLeftOp();
					values[1] = a.getRightOp();
					
					for ( Value v : values ) {
						// We only care those pointers p involving in the expression: p.f
						if (v instanceof InstanceFieldRef) {
							InstanceFieldRef ifr = (InstanceFieldRef) v;
							LocalVarNode vn = geom.findLocalVarNode((Local) ifr.getBase());
							if ( vn == null ) continue;
							IVarAbstraction pn = geom.findInternalNode(vn);
							if ( pn == null ) continue;
							basePointers.add(pn);
						}
						else if (v instanceof ArrayRef) {
							ArrayRef arf = (ArrayRef) v;
							LocalVarNode vn = geom.findLocalVarNode((Local) arf.getBase());
							if (vn == null ) continue;
							IVarAbstraction pn = geom.findInternalNode(vn);
							if ( pn == null ) continue;
							basePointers.add(pn);
						}
					}
				}
			}
		}
		
		if ( isDump ) {
			try {
				final PrintWriter file = new PrintWriter( geom.createOutputFile("geomBasePointers.txt") );
				
				for ( CallsiteContextVar cvar : ContextTranslator.pts_1cfa_map ) {
					IVarAbstraction pn = geom.findInternalNode( cvar.var );
					if ( !basePointers.contains(pn) ) continue;
					file.println( cvar.getNumber() );
				}
				
				file.close();
				
				
			} catch( IOException e ) {
	            throw new RuntimeException( "Couldn't dump solution."+e );
	        }
		}
		
		return basePointers;
	}
	
	
}
